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

import de.dhke.projects.cutil.Pair;
import de.dhke.projects.cutil.collections.tree.IDecisionTree;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.Branch;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ConsistencyInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EReasonerException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.INodeConsistencyChecker;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ReasonerContinuationState;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.EIllegalTermTypeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.util.AbstractCompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import java.util.Collection;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class RoleRestrictionCompleter<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> 
	extends AbstractCompleter<I, L, K, R> {

	public RoleRestrictionCompleter(final INodeConsistencyChecker<I, L, K, R> cChecker)
	{
		super(cChecker);
	}


	public RoleRestrictionCompleter(final INodeConsistencyChecker<I, L, K, R> cChecker, final boolean trace)
	{
		super(cChecker, trace);
	}


	/**
	 *
	 * @param node
	 * @param branch
	 * @param branchQueue
	 * @return {@literal true}, if further completion rules should be applied to this node, or {@literal false} if the
	 * node queue needs to be checked again before applying new rules.
	 * @throws EReasonerException
	 */
	@Override
	public ReasonerContinuationState completeNode(final IDecisionTree.Node<Branch<I, L, K, R>> branchNode, final IABoxNode<I, L, K, R> node)
		throws EReasonerException
	{
		final Branch<I, L, K, R> branch = branchNode.getData();
		for (Pair<R, NodeID> incoming : node.getRABox().getPredecessorPairs()) {
			final Collection<IDLRestriction<I, L, K, R>> range = branch.getABox().getTBox().getRBox().
				getRoleRanges(incoming.getFirst());
			if ((range != null) && (!node.getTerms().containsAll(range))) {
				try {
					final NodeMergeInfo<I, L, K, R> mergeInfo = node.addTerms(range);
					if (mergeInfo.isModified(node)) {
						/**
						 * the current node was merged away, stop processing, recheck queues
						 *
						 */
						return ReasonerContinuationState.RECHECK_NODE;
					}
				} catch (ENodeMergeException ex) {
					/* XXX - this be narrowed down to an individual term */
					branch.getConsistencyInfo().upgradeClashType(ConsistencyInfo.ClashType.FINAL);
					branch.getConsistencyInfo().addCulprits(node, range);
					return ReasonerContinuationState.INCONSISTENT;
				}
			}
		}
		for (Pair<R, NodeID> outgoing : node.getRABox().getSuccessorPairs()) {
			final Collection<IDLClassExpression<I, L, K, R>> domain = branch.getABox().getTBox().getRBox().
				getRoleDomains(outgoing.getFirst());
			try {
				if ((domain != null) && (!node.getTerms().containsAll(domain))) {
					final NodeMergeInfo<I, L, K, R> mergeInfo = node.addTerms(domain);
					if (mergeInfo.isModified(node)) {
						/**
						 * the current node was merged away, stop processing, recheck queues
						 *
						 */
						return ReasonerContinuationState.RECHECK_NODE;
					}
				}
			} catch (ENodeMergeException | EIllegalTermTypeException ex) {
				/* XXX - this be narrowed down to an individual term */
				branch.getConsistencyInfo().upgradeClashType(ConsistencyInfo.ClashType.FINAL);
				branch.getConsistencyInfo().addCulprits(node, domain);
				return ReasonerContinuationState.INCONSISTENT;
			}

		}
		return ReasonerContinuationState.CONTINUE;
	}
}
