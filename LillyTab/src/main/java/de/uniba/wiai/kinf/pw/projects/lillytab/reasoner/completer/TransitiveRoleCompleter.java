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

import de.dhke.projects.cutil.collections.tree.IDecisionTree;
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

public class TransitiveRoleCompleter<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> 
	extends AbstractCompleter<I, L, K, R>
	implements ICompleter<I, L, K, R> {

	public TransitiveRoleCompleter(final INodeConsistencyChecker<I, L, K, R> cChecker)
	{
		super(cChecker);
	}


	public TransitiveRoleCompleter(final INodeConsistencyChecker<I, L, K, R> cChecker, boolean trace)
	{
		super(cChecker, trace);
	}


	@Override
	public ReasonerContinuationState completeNode(IDecisionTree.Node<Branch<I, L, K, R>> branchNode, IABoxNode<I, L, K, R> node)
		throws EReasonerException
	{
		final Branch<I, L, K, R> branch = branchNode.getData();
		final IABox<I, L, K, R> abox = node.getABox();
		final IRBox<I, L, K, R> rbox = abox.getRBox();

		/**
		 * Remember, if we performed any modifications. If so, we should recheck the branch queue afterwards.
		 */
		boolean wasModified = false;
		/**
		 * Walk through list of incoming roles, check for transitive roles
		 */
		for (R incoming : node.getRABox().getIncomingRoles()) {
			/**
			 * Check for predecessors that are connected to the current node via a transitive role.
			 */
			if (rbox.hasRoleProperty(incoming, RoleProperty.TRANSITIVE) && node.getRABox().hasPredecessor(incoming)) {
				/**
				 * If there is at least one predecessor connected to us via the current (transitive) role, walk the list
				 * of both predecessors and successors for the current role and add links as necessary.
				 */
				for (NodeID predID : node.getRABox().getPredecessors(incoming)) {
					for (NodeID succID : node.getRABox().getSuccessors(incoming)) {
						final IABoxNode<I, L, K, R> pred = abox.getNode(predID);
						/**
						 * If there is not yet a role link between successor and predecessor, add it.
						 */
						if (!pred.getRABox().hasSuccessor(incoming, succID)) {
							pred.getRABox().getAssertedSuccessors().put(incoming, succID);
							branch.touch(predID);
							branch.touch(succID);
							wasModified = true;
							if (isTracing()) {
								logFinest("Added transitive %s-link from %s to %s", incoming, predID, succID);
							}
						}
					}
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
