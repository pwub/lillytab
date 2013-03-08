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

import de.dhke.projects.cutil.collections.tree.IDecisionTree;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleProperty;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.Branch;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.INodeConsistencyChecker;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EReasonerException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ReasonerContinuationState;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IRBox;

public class TransitiveRoleCompleter<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends AbstractCompleter<Name, Klass, Role>
	implements ICompleter<Name, Klass, Role> {

	public TransitiveRoleCompleter(final INodeConsistencyChecker<Name, Klass, Role> cChecker)
	{
		super(cChecker);
	}


	public TransitiveRoleCompleter(final INodeConsistencyChecker<Name, Klass, Role> cChecker, boolean trace)
	{
		super(cChecker, trace);
	}


	public ReasonerContinuationState completeNode(IABoxNode<Name, Klass, Role> node,
												  IDecisionTree.Node<Branch<Name, Klass, Role>> branchNode)
		throws EReasonerException
	{
		final Branch<Name, Klass, Role> branch = branchNode.getData();
		final IABox<Name, Klass, Role> abox = node.getABox();
		final IRBox<Name, Klass, Role> rbox = abox.getRBox();

		/**
		 * Remember, if we performed any modifications. If so, we should recheck the branch queue afterwards.
		 */
		boolean wasModified = false;
		/**
		 * Walk through list of incoming roles, check for transitive roles
		 */
		for (Role incoming : node.getRABox().getIncomingRoles()) {
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
						final IABoxNode<Name, Klass, Role> pred = abox.getNode(predID);
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
