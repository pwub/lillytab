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
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IRABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.Branch;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EReasonerException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.INodeConsistencyChecker;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ReasonerContinuationState;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.util.AbstractCompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IRBox;

/**
 *
 * @param <I> The type for nominals and values
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class InverseRoleAssertionCompleter<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> 
	extends AbstractCompleter<I, L, K, R> {

	public InverseRoleAssertionCompleter(final INodeConsistencyChecker<I, L, K, R> cChecker, final boolean trace)
	{
		super(cChecker, trace);
	}


	public InverseRoleAssertionCompleter(final INodeConsistencyChecker<I, L, K, R> cChecker)
	{
		this(cChecker, false);
	}


	@Override
	public ReasonerContinuationState completeNode(Node<Branch<I, L, K, R>> branchNode, IABoxNode<I, L, K, R> node)
		throws EReasonerException
	{
		final IABox<I, L, K, R> abox = node.getABox();
		final IRABox<I, L, K, R> raBox = node.getRABox();
		final IRBox<I, L, K, R> rbox = abox.getTBox().getRBox();

		boolean hasChanged = false;

		for (R outRole : raBox.getAssertedSuccessors().keySet()) {
			for (R inverseRole : rbox.getInverseRoles(outRole)) {
				for (NodeID succID : raBox.getAssertedSuccessors().get(outRole)) {
					final IABoxNode<I, L, K, R> succ = abox.getNode(succID);
					assert succ != null;
					// add back link */
					if (!succ.getRABox().hasSuccessor(inverseRole, node)) {
						succ.getRABox().getAssertedSuccessors().put(inverseRole, node);
						branchNode.getData().touchNode(node);
						branchNode.getData().touch(succID);
						hasChanged = true;
					}
				}
			}
		}
		if (hasChanged) {
			return ReasonerContinuationState.RECHECK_NODE;
		} else {
			return ReasonerContinuationState.CONTINUE;
		}
	}
}