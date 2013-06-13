/**
 * (c) 2009-2012 Otto-Friedrich-University Bamberg
 *
 * $Id$
 *
 * Use, modification and restribution of this file are covered by the terms of the Artistic License 2.0.
 *
 * You should have received a copy of the license terms in a file named "LICENSE" together with this software package.
 *
 * Disclaimer of Warranty: THE PACKAGE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS "AS IS' AND WITHOUT ANY
 * EXPRESS OR IMPLIED WARRANTIES. THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT ARE DISCLAIMED TO THE EXTENT PERMITTED BY YOUR LOCAL LAW. UNLESS REQUIRED BY LAW, NO COPYRIGHT
 * HOLDER OR CONTRIBUTOR WILL BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING IN ANY
 * WAY OUT OF THE USE OF THE PACKAGE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer;

import de.dhke.projects.cutil.collections.iterator.ChainIterator;
import de.dhke.projects.cutil.collections.tree.IDecisionTree;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.Branch;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ConsistencyInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EReasonerException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.INodeConsistencyChecker;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ReasonerContinuationState;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.util.AbstractCompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.util.ICompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleProperty;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLAllRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLDataAllRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLObjectAllRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import java.util.Iterator;


public class TransitiveCompleter<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends AbstractCompleter<I, L, K, R>
	implements ICompleter<I, L, K, R>
{
	public TransitiveCompleter(final INodeConsistencyChecker<I, L, K, R> cChecker)
	{
		super(cChecker);
	}

	public TransitiveCompleter(final INodeConsistencyChecker<I, L, K, R> cChecker, boolean trace)
	{
		super(cChecker, trace);
	}

	@Override
	@SuppressWarnings("unchecked")
	public ReasonerContinuationState completeNode(IDecisionTree.Node<Branch<I, L, K, R>> branchNode,
												  IABoxNode<I, L, K, R> node)
		throws EReasonerException
	{
		final Branch<I, L, K, R> branch = branchNode.getData();
		final IABox<I, L, K, R> abox = node.getABox();
		final IRBox<I, L, K, R> rbox = abox.getRBox();

		/**
		 * Remember, if we performed any modifications. If so, we should recheck the branch queue afterwards.
		 */
		boolean wasModified = false;
		@SuppressWarnings("unchecked")
		Iterator<IDLTerm<I, L, K, R>> termIter = ChainIterator.decorate(
			node.getTerms().iterator(IDLObjectAllRestriction.class),
			node.getTerms().iterator(IDLDataAllRestriction.class));

		while (termIter.hasNext()) {
			final IDLTerm<I, L, K, R> term = termIter.next();
			/**
			 * Walk through list of incoming roles, check for transitive roles
			 **/
			IDLAllRestriction<I, L, K, R> forAllTerm = (IDLAllRestriction<I, L, K, R>) term;
			if (rbox.hasRoleProperty(forAllTerm.getRole(), RoleProperty.TRANSITIVE)) {
				Iterator<IABoxNode<I, L, K, R>> successorIter = node.getRABox().getSuccessorNodes(forAllTerm.getRole()).
					iterator();
				try {
					while ((successorIter != null) && successorIter.hasNext()) {
						final IABoxNode<I, L, K, R> succNode = successorIter.next();
						if (!succNode.getTerms().contains(forAllTerm)) {
							/* update dependency map */
							abox.getDependencyMap().addParent(node, forAllTerm, succNode, forAllTerm);
							final NodeMergeInfo<I, L, K, R> mergeInfo = succNode.addTerm(forAllTerm);
							if (mergeInfo.getMergedNodes().contains(node)) {
								/**
								 * The current node was merged with another node: Stop processing, recheck queues.
								 */
								return ReasonerContinuationState.RECHECK_NODE;
							} else if (mergeInfo.isModified(node)) {
								/* if the local concept set was modified, we have to restart both iterators */
								termIter = ChainIterator.decorate(
									node.getTerms().iterator(IDLObjectAllRestriction.class),
									node.getTerms().iterator(IDLDataAllRestriction.class));
								successorIter = node.getRABox().getSuccessorNodes(forAllTerm.getRole()).
									iterator();
							}
						}
					}
				} catch (ENodeMergeException ex) {
					/* handle node merge failures. The problematic term is the forall term
					 * 
					 * TODO: - how expensive is it to track down the generating term of
					 * the successor and introduce a property clash set, here?
					 */
					branch.getConsistencyInfo().addCulprits(node, forAllTerm);
					branch.getConsistencyInfo().upgradeClashType(ConsistencyInfo.ClashType.FINAL);
					return ReasonerContinuationState.INCONSISTENT;
				}
			}
			if (wasModified) {
				return ReasonerContinuationState.RECHECK_NODE;
			} else {
				return ReasonerContinuationState.CONTINUE;
			}
		}

		return ReasonerContinuationState.CONTINUE;
	}
}
