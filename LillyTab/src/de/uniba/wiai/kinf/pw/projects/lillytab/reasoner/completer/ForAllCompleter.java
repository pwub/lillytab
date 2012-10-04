/**
 * (c) 2009-2012 Otto-Friedrich-University Bamberg
 *
 * $Id$
 *
 * Use, modification and restribution of this file are covered by the
 * terms of the Artistic License 2.0.
 *
 * You should have received a copy of the license terms in a file named
 * "LICENSE" together with this software package.
 *
 * Disclaimer of Warranty: THE PACKAGE IS PROVIDED BY THE COPYRIGHT
 * HOLDER AND CONTRIBUTORS "AS IS' AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES. THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
 * A PARTICULAR PURPOSE, OR NON-INFRINGEMENT ARE DISCLAIMED TO THE
 * EXTENT PERMITTED BY YOUR LOCAL LAW. UNLESS REQUIRED BY LAW, NO
 * COPYRIGHT HOLDER OR CONTRIBUTOR WILL BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING IN ANY WAY OUT
 * OF THE USE OF THE PACKAGE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 **/
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.Branch;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EInconsistentABoxNodeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EReasonerException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.INodeConsistencyChecker;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ReasonerContinuationState;
import de.dhke.projects.cutil.collections.tree.IDecisionTree;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLAllRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import java.util.Iterator;
import java.util.SortedSet;


/**
 *
 * @param <Name>
 * @param <Klass>
 * @param <Role>
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ForAllCompleter<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends AbstractCompleter<Name, Klass, Role>
	implements ICompleter<Name, Klass, Role>
{
	public ForAllCompleter(final INodeConsistencyChecker<Name, Klass, Role> cChecker, final boolean trace)
	{
		super(cChecker, trace);
	}

	/**
	 * Search the concept set of {@literal node} for forAll-descriptions ({@link IDLAllRestriction}, <pre>(ONLY r C)</pre>).
	 *
	 * @param node The node to complete
	 * @param branch The current branch {@literal node} is on
	 * @param branchQueue The branch queue
	 *
	 * @return {@literal true}, if further completion rules should be applied to this node,
	 *	or {@literal false} if the node queue needs to be checked again before applying new rules.
	 * @throws EReasonerException
	 */
	public ReasonerContinuationState completeNode(final IABoxNode<Name, Klass, Role> node,
												  final IDecisionTree.Node<Branch<Name, Klass, Role>> branchNode)
		throws EReasonerException, EInconsistencyException
	{
		try {
			final Branch<Name, Klass, Role> branch = branchNode.getData();
			SortedSet<IDLTerm<Name, Klass, Role>> conceptTerms = node.getTerms().subSet(DLTermOrder.DL_ALL_RESTRICTION);
			Iterator<IDLTerm<Name, Klass, Role>> termIter = conceptTerms.iterator();
			/**
			 * The proper order of rule application is not enforced,
			 * here, as the forAll-rules potentially make alterations all across
			 * the abox.
			 *
			 * This is not a problem, since we only need guarantee, that all possible
			 * non-generating rules are followed before any generating rule.
			 *
			 * We thus apply forAll-completion to a single node and also
			 * follow this node across any merge operations.
			 */
			while (termIter.hasNext()) {
				final IDLTerm<Name, Klass, Role> term = termIter.next();
				if (term instanceof IDLRestriction) {
					IDLRestriction<Name, Klass, Role> desc = (IDLRestriction<Name, Klass, Role>) term;
					if (desc instanceof IDLAllRestriction) {
						final IDLAllRestriction<Name, Klass, Role> allRestriction = (IDLAllRestriction<Name, Klass, Role>) desc;
						final Role role = allRestriction.getRole();
						final IDLRestriction<Name, Klass, Role> forAllTerm = allRestriction.getTerm();

						final IABox<Name, Klass, Role> abox = branch.getABox();
						Iterator<NodeID> successorIter = node.getLinkMap().getSuccessors(role).iterator();
						while ((successorIter != null) && successorIter.hasNext()) {
							final NodeID succID = successorIter.next();
							IABoxNode<Name, Klass, Role> succ = abox.getNode(succID);
							assert succ != null;
							/* update dependency map */
							if (!abox.getDependencyMap().containsKey(succ, forAllTerm))
								abox.getDependencyMap().addParent(succ, forAllTerm, node, term);

							NodeMergeInfo<Name, Klass, Role> modInfo = succ.addUnfoldedDescription(forAllTerm);
							
							if (isTracing())
								logFinest("ForAll-propagation of %s from node %s to %s", forAllTerm, node.getNodeID(), succID);
							
							/* track eventual merge */
							succ = modInfo.getCurrentNode();
							getNodeConsistencyChecker().checkConsistency(succ);

							if (modInfo.getMergedNodes().contains(node))
								/**
								 * The current node was merged with another node:
								 * Stop processing, recheck queues.
								 */
								return ReasonerContinuationState.RECHECK_NODE;
							else if (modInfo.isModified(node)) {
								/* if the local concept set was modified, we have to restart both iterators */
								conceptTerms = node.getTerms().subSet(DLTermOrder.DL_ALL_RESTRICTION);
								termIter = conceptTerms.iterator();
								successorIter = node.getLinkMap().getSuccessors(role).iterator();
							}
						}
					}
				}
			}
			return ReasonerContinuationState.CONTINUE;
		} catch (ENodeMergeException ex) {
			throw new EInconsistentABoxNodeException(ex.getSourceNode());
		}
	}
}
