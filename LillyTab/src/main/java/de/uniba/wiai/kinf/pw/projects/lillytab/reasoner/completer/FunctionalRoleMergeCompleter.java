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
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.util.AbstractCompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleProperty;
import java.util.Iterator;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class FunctionalRoleMergeCompleter<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> 
	extends AbstractCompleter<I, L, K, R> {

	public FunctionalRoleMergeCompleter(final INodeConsistencyChecker<I, L, K, R> cChecker, final boolean trace)
	{
		super(cChecker, trace);
	}


	public FunctionalRoleMergeCompleter(final INodeConsistencyChecker<I, L, K, R> cChecker)
	{
		this(cChecker, false);
	}


	@Override
	public ReasonerContinuationState completeNode(Node<Branch<I, L, K, R>> branchNode, IABoxNode<I, L, K, R> node)
		throws EReasonerException
	{
		try {
			final IABox<I, L, K, R> abox = node.getABox();
			if (abox == null) {
				logInfo("Expected ABox, got null: %s", branchNode.getData().getABox());
				logInfo("For Node %s", node);
				logInfo("Node fetch from ABOX yields %s", branchNode.getData().getABox().getNode(node.getNodeID()));
			}
			assert abox != null;
			assert abox.getTBox() != null;
			
			boolean changed = false;

			final IRBox<I, L, K, R> rbox = abox.getTBox().getRBox();
			final IRABox<I, L, K, R> raBox = node.getRABox();
			for (final R outRole : raBox.getOutgoingRoles()) {
				if (rbox.hasRoleProperty(outRole, RoleProperty.FUNCTIONAL)) {
					Iterator<IABoxNode<I, L, K, R>> succIter = raBox.getSuccessorNodes(outRole).iterator();
					assert succIter.hasNext();
					IABoxNode<I, L, K, R> firstNode = succIter.next();
					while (succIter.hasNext()) {
						final IABoxNode<I, L, K, R> nextNode = succIter.next();
						final NodeMergeInfo<I, L, K, R> mergeInfo = abox.mergeNodes(nextNode, firstNode);
						branchNode.getData().touchNode(mergeInfo.getCurrentNode());

						succIter = raBox.getSuccessorNodes(outRole).iterator();
						firstNode = succIter.next();

						changed = true;
					}
				}
			}

			for (final R inRole : raBox.getIncomingRoles()) {
				if (rbox.hasRoleProperty(inRole, RoleProperty.INVERSE_FUNCTIONAL)) {
					Iterator<IABoxNode<I, L, K, R>> predIter = raBox.getPredecessorNodes(inRole).iterator();
					assert predIter.hasNext();
					IABoxNode<I, L, K, R> firstNode = predIter.next();
					while (predIter.hasNext()) {
						final IABoxNode<I, L, K, R> nextNode = predIter.next();
						final NodeMergeInfo<I, L, K, R> mergeInfo = abox.mergeNodes(nextNode, firstNode);

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
