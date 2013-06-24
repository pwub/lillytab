/**
 * (c) 2009-2013 Otto-Friedrich-University Bamberg
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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.util;

import de.dhke.projects.cutil.collections.tree.IDecisionTree;
import de.dhke.projects.lutil.LoggingClass;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.Branch;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.INodeConsistencyChecker;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ReasonerContinuationState;
import java.util.List;

/**
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public abstract class AbstractCompleter<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> 
	extends LoggingClass
	implements ICompleter<I, L, K, R> {

	private final INodeConsistencyChecker<I, L, K, R> _cChecker;
	private final boolean _trace;


	public AbstractCompleter(final INodeConsistencyChecker<I, L, K, R> cChecker, final boolean trace)
	{
		_cChecker = cChecker;
		_trace = trace;
	}


	public AbstractCompleter(final INodeConsistencyChecker<I, L, K, R> cChecker)
	{
		this(cChecker, false);
	}


	public INodeConsistencyChecker<I, L, K, R> getNodeConsistencyChecker()
	{
		return _cChecker;
	}


	protected boolean isTracing()
	{
		return _trace;
	}


	/**
	 *  Traverse the list of {@link BranchCreationInfo}s and update the branch tree as necessary. <p /> Also
	 * validate, if the modifications to the current branch require rechecking the node queue. 
	 *
	 * @param branchCreationInfos A list of recent {@link BranchCreationInfo}s.
	 * @param currentBranch The current (base) branch.
	 * @param node The current node.
	 * @param branchQueue The branch queue.
	 * @return {@literal true}, if processing can continue uninterruptedly on the current branch. {@literal false}, if
	 * the node queue needs to be rechecked before further processing.
	 *
	 */
	protected ReasonerContinuationState handleBranchCreation(
		final List<BranchCreationInfo<I, L, K, R>> branchCreationInfos,
		final IDecisionTree.Node<Branch<I, L, K, R>> currentBranchNode,
		final IABoxNode<I, L, K, R> node)
	{
		if (branchCreationInfos.size() == 1) {
			/*
			 * if only a single alternative was found, we can re-use the current Branch and just keep going
			 */
			return ReasonerContinuationState.RECHECK_NODE;
		} else {
			// Branch<I, L, K, R> currentBranch = currentBranchNode.getData();

			boolean haveBranched = false;
			for (BranchCreationInfo<I, L, K, R> bci : branchCreationInfos) {
				/**
				 * applying to the current branch (re-using the current branch) does not create a fork
				 *
				 */
				if (!currentBranchNode.getData().equals(bci.getTargetBranch())) {
					if (!haveBranched) {
						/*
						 * if we have not branched yet, the first branch that is not the existing branch creates a new
						 * decision point.
						 */
						final IDecisionTree.Node<Branch<I, L, K, R>> newNode = currentBranchNode.branch(
							bci.getTargetBranch());
						haveBranched = true;
					} else {
						/*
						 * any subsequent branches are just forked as siblings
						 */
						final IDecisionTree.Node<Branch<I, L, K, R>> newNode = currentBranchNode.fork(
							bci.getTargetBranch());
					}
				}
			}

			return ReasonerContinuationState.RECHECK_BRANCH;
		}
	}
}
