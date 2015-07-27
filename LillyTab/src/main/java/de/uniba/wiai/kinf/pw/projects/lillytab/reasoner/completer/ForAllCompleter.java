/**
 * (c) 2009-2014 Otto-Friedrich-University Bamberg
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

import de.dhke.projects.cutil.collections.iterator.ChainIterator;
import de.dhke.projects.cutil.collections.tree.IDecisionTree;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IDatatypeABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IIndividualABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.Branch;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ConsistencyInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EReasonerException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.INodeConsistencyChecker;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ReasonerContinuationState;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.util.AbstractCompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.util.ICompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleProperty;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLAllRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLDataAllRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLNodeTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLObjectAllRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataRange;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ForAllCompleter<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends AbstractCompleter<I, L, K, R>
	implements ICompleter<I, L, K, R>
{
	private static final Logger _logger = LoggerFactory.getLogger(ForAllCompleter.class);
	
	public ForAllCompleter(final INodeConsistencyChecker<I, L, K, R> cChecker, final boolean trace)
	{
		super(cChecker, trace);
	}

	/**
	 * Search the concept set of {@literal node} for forAll-descriptions ({@link IDLAllRestriction},
	 * <pre>(ONLY r C)</pre>).
	 *
	 * @param node The node to complete
	 * @param branch The current branch {@literal node} is on
	 * @param branchQueue The branch queue
	 *
	 * @return {@literal true}, if further completion rules should be applied to this node, or {@literal false} if the
	 * node queue needs to be checked again before applying new rules.
	 * @throws EReasonerException
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ReasonerContinuationState completeNode(final IDecisionTree.Node<Branch<I, L, K, R>> branchNode,
												  final IABoxNode<I, L, K, R> node)
		throws EReasonerException
	{
		@SuppressWarnings("unchecked")
		Iterator<IDLTerm<I, L, K, R>> termIter = ChainIterator.decorate(
			node.getTerms().iterator(IDLObjectAllRestriction.class),
			node.getTerms().iterator(IDLDataAllRestriction.class));
		/**
		 * The proper order of rule application is not enforced, here, as the forAll-rules potentially make alterations
		 * all across the abox.
		 *
		 * This is not a problem, since we only need guarantee, that all possible non-generating rules are followed
		 * before any generating rule.
		 *
		 * We thus apply forAll-completion to a single node and also follow this node across any merge operations.
		 */
		while (termIter.hasNext()) {
			final IDLTerm<I, L, K, R> term = termIter.next();
			/* remember governing term state */
			final Branch<I, L, K, R> branch = branchNode.getData();
			final IABox<I, L, K, R> abox = branch.getABox();
			final boolean wasGovTerm = abox.getDependencyMap().hasGoverningTerm(node, term);

			/**
			 * This causes _Thing_ to be inserted after a term was moved.
			 * This is only necessary, if the node has no other governing term.
			 * Since adding _Thing_ does not hurt, do it, here.
			 **/
			if (wasGovTerm)
				abox.getDependencyMap().addGoverningTerm(node, abox.getDLTermFactory().getDLThing());

			if (term instanceof IDLClassExpression) {
				IDLClassExpression<I, L, K, R> desc = (IDLClassExpression<I, L, K, R>) term;
				if (desc instanceof IDLAllRestriction) {
					final IDLAllRestriction<I, L, K, R> allRestriction = (IDLAllRestriction<I, L, K, R>) desc;
					final R role = allRestriction.getRole();
					final IDLNodeTerm<I, L, K, R> forAllTerm = allRestriction.getTerm();

					Iterator<NodeID> successorIter = node.getRABox().getSuccessors(role).iterator();
					while ((successorIter != null) && successorIter.hasNext()) {
						final NodeID succID = successorIter.next();
						IABoxNode<I, L, K, R> succ = abox.getNode(succID);
						assert succ != null;
						/* update dependency map */
						abox.getDependencyMap().addParent(succ, forAllTerm, node, desc);

						try {
							NodeMergeInfo<I, L, K, R> mergeInfo;
							if (succ instanceof IDatatypeABoxNode) {
								if (forAllTerm instanceof IDLDataRange) {
									mergeInfo = ((IDatatypeABoxNode<I, L, K, R>) succ).addDataTerm(
										(IDLDataRange<I, L, K, R>) forAllTerm);
								} else {
									final ConsistencyInfo<I, L, K, R> cInfo = new ConsistencyInfo<>(
										ConsistencyInfo.ClashType.FINAL);
									cInfo.addCulprits(node, desc);
									branch.upgradeConsistencyInfo(cInfo);
									return ReasonerContinuationState.INCONSISTENT;
								}
							} else {
								if (forAllTerm instanceof IDLClassExpression) {
									mergeInfo = ((IIndividualABoxNode<I, L, K, R>) succ).addClassTerm(
										(IDLClassExpression<I, L, K, R>) forAllTerm);
								} else {
									final ConsistencyInfo<I, L, K, R> cInfo = new ConsistencyInfo<>(
										ConsistencyInfo.ClashType.FINAL);
									cInfo.addCulprits(node, desc);
									branch.upgradeConsistencyInfo(cInfo);
									return ReasonerContinuationState.INCONSISTENT;
								}
							}

							/**
							 * Governing term handling:
							 * 
							 * ForAll terms only loose their governing term
							 * property if the associated role is functional and a successor
							 * already exists.
							 **/
							if (abox.getRBox().hasRoleProperty(role, RoleProperty.FUNCTIONAL) && wasGovTerm) {
								abox.getDependencyMap().addGoverningTerm(succ, forAllTerm);
								abox.getDependencyMap().getGoverningTerms().remove(abox.getTermEntryFactory().getEntry(node, term));
							}

							if (isTracing()) {
								_logger.trace("ForAll-propagation of %s from node %s to %s", forAllTerm, node.getNodeID(),
										  succID);
							}

							/* track eventual merge */
							succ = mergeInfo.getCurrentNode();
							final ConsistencyInfo<I, L, K, R> cInfo = getNodeConsistencyChecker().isConsistent(
								succ);
							if (cInfo.isFinallyInconsistent()) {
								branch.upgradeConsistencyInfo(cInfo);
								return ReasonerContinuationState.INCONSISTENT;
							}

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
								successorIter = node.getRABox().getSuccessors(role).iterator();
							}
						} catch (ENodeMergeException ex) {
							/* handle node merge failures. The problematic term is the forall term
							 * 
							 * TODO: - how expensive is it to track down the generating term of
							 * the successor and introduce a property clash set, here?
							 */
							branch.getConsistencyInfo().addCulprits(node, allRestriction);
							branch.getConsistencyInfo().upgradeClashType(ConsistencyInfo.ClashType.FINAL);
							return ReasonerContinuationState.INCONSISTENT;
						}
					}
				}
			}
		}

		return ReasonerContinuationState.CONTINUE;
	}
}
