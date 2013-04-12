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
import de.dhke.projects.cutil.collections.tree.IDecisionTree.Node;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.Branch;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EReasonerException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.INodeConsistencyChecker;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ReasonerContinuationState;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.util.AbstractCompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.util.ICompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleProperty;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class SymmetricRoleCompleter<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> 
	extends AbstractCompleter<I, L, K, R>
	implements ICompleter<I, L, K, R> {

	public SymmetricRoleCompleter(final INodeConsistencyChecker<I, L, K, R> cChecker, boolean trace)
	{
		super(cChecker, trace);
	}


	public SymmetricRoleCompleter(final INodeConsistencyChecker<I, L, K, R> cChecker)
	{
		super(cChecker, false);
	}


	@Override
	public ReasonerContinuationState completeNode(Node<Branch<I, L, K, R>> branchNode, IABoxNode<I, L, K, R> node)
		throws EReasonerException
	{
		final Branch<I, L, K, R> branch = branchNode.getData();
		final IABox<I, L, K, R> abox = node.getABox();
		final IRBox<I, L, K, R> rbox = abox.getRBox();

		/**
		 * Remember, if we performed any modifications. If so, we should recheck the node queue afterwards.
		 */
		boolean wasModified = false;
		/**
		 * Walk through list of outgoing roles, check for symmetric roles
		 */
		for (Pair<R, NodeID> outgoing : node.getRABox().getSuccessorPairs()) {
			/**
			 * check for successors of the role that do not link back to us.
			 */
			final R role = outgoing.getFirst();
			if (rbox.hasRoleProperty(role, RoleProperty.SYMMETRIC)) {
				final IABoxNode<I, L, K, R> otherNode = abox.getNode(outgoing.getSecond());
				if (!otherNode.getRABox().hasSuccessor(role, node)) {
					otherNode.getRABox().getAssertedSuccessors().put(role, node.getNodeID());
					wasModified = true;
					branch.touchNode(node);
					branch.touchNode(otherNode);
					/**
					 * It is safe to continue after modification, because we never modify the successors of the current
					 * node.
					 *
					 * If the current node is the successor, the role is already reflexive.
					 *
					 */
				}
			}
		}
		if (wasModified) {
			return ReasonerContinuationState.RECHECK_NODE;
		} else {
			return ReasonerContinuationState.CONTINUE;
		}
	}
}
