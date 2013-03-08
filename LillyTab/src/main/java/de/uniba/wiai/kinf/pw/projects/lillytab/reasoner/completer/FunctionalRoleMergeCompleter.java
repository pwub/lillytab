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

import de.dhke.projects.cutil.collections.tree.IDecisionTree.Node;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IRABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.Branch;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EReasonerException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.INodeConsistencyChecker;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ReasonerContinuationState;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleProperty;
import java.util.Iterator;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class FunctionalRoleMergeCompleter<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends AbstractCompleter<Name, Klass, Role> {

	public FunctionalRoleMergeCompleter(final INodeConsistencyChecker<Name, Klass, Role> cChecker, final boolean trace)
	{
		super(cChecker, trace);
	}


	public FunctionalRoleMergeCompleter(final INodeConsistencyChecker<Name, Klass, Role> cChecker)
	{
		this(cChecker, false);
	}


	@Override
	public ReasonerContinuationState completeNode(IABoxNode<Name, Klass, Role> node,
												  Node<Branch<Name, Klass, Role>> branchNode)
		throws EReasonerException
	{
		try {
			final IABox<Name, Klass, Role> abox = node.getABox();
			if (abox == null) {
				logInfo("Expected ABox, got null: %s", branchNode.getData().getABox());
				logInfo("For Node %s", node);
				logInfo("Node fetch from ABOX yields %s", branchNode.getData().getABox().getNode(node.getNodeID()));
			}
			assert abox != null;
			assert abox.getTBox() != null;
			
			boolean changed = false;

			final IRBox<Name, Klass, Role> rbox = abox.getTBox().getRBox();
			final IRABox<Name, Klass, Role> raBox = node.getRABox();
			for (final Role outRole : raBox.getOutgoingRoles()) {
				if (rbox.hasRoleProperty(outRole, RoleProperty.FUNCTIONAL)) {
					Iterator<IABoxNode<Name, Klass, Role>> succIter = raBox.getSuccessorNodes(outRole).iterator();
					assert succIter.hasNext();
					IABoxNode<Name, Klass, Role> firstNode = succIter.next();
					while (succIter.hasNext()) {
						final IABoxNode<Name, Klass, Role> nextNode = succIter.next();
						final NodeMergeInfo<Name, Klass, Role> mergeInfo = abox.mergeNodes(nextNode, firstNode);
						branchNode.getData().touchNode(mergeInfo.getCurrentNode());

						succIter = raBox.getSuccessorNodes(outRole).iterator();
						firstNode = succIter.next();

						changed = true;
					}
				}
			}

			for (final Role inRole : raBox.getIncomingRoles()) {
				if (rbox.hasRoleProperty(inRole, RoleProperty.INVERSE_FUNCTIONAL)) {
					Iterator<IABoxNode<Name, Klass, Role>> predIter = raBox.getPredecessorNodes(inRole).iterator();
					assert predIter.hasNext();
					IABoxNode<Name, Klass, Role> firstNode = predIter.next();
					while (predIter.hasNext()) {
						final IABoxNode<Name, Klass, Role> nextNode = predIter.next();
						final NodeMergeInfo<Name, Klass, Role> mergeInfo = abox.mergeNodes(nextNode, firstNode);

						branchNode.getData().touchNode(mergeInfo.getCurrentNode());

						predIter = raBox.getPredecessorNodes(inRole).iterator();
						firstNode = predIter.next();

						changed = true;
					}
				}
			}

			if (!changed) {
				return ReasonerContinuationState.CONTINUE;
			} else {
				return ReasonerContinuationState.RECHECK_NODE;
			}
		} catch (EInconsistencyException ex) {
			return ReasonerContinuationState.INCONSISTENT;
		}
	}
}
