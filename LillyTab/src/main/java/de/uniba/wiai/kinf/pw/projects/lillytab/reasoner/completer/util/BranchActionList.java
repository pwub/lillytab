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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.util;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.Branch;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.INodeConsistencyChecker;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * 
 * Implements a list of branch actions that together represent a single branching action.
 * <p />
 * Committing a {@link BranchActionList} to an initial branch creates a sufficient number of successor branches from the
 * initial branch and applies any applicable action. The base branch is re-used as the first branch in the returned
 * successor list.
 * 
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public class BranchActionList<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> 
	extends ArrayList<IBranchAction<I, L, K, R>> {

	/**
	 * 
	 * Check the action list and create sufficient clones from the initial base branch.
	 * <p />
	 * No branches are created for action, that have been determined not to be applicable (e.g. lead to inconsistencies)
	 * (via {@link IBranchAction#isShouldCommit(de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.Branch, de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.INodeConsistencyChecker)
	 * }. in advance.
	 * <p />
	 * Commit the actions to the branches
	 * <p />
	 * It is safe to assume (if the branch list is not all empty), that the base branch is always the first branch in
	 * the returned list.
	 * 
	 *
	 *
	 * @param baseBranch The base branch to fork new branches from.
	 * @param cChecker The node consistency checker to use for local node checking.
	 * @return A list of forked branches.
	 */
	public List<BranchCreationInfo<I, L, K, R>> commit(final Branch<I, L, K, R> baseBranch,
															  final INodeConsistencyChecker<I, L, K, R> cChecker)
	{
		final int nBranches = size();
		final List<BranchCreationInfo<I, L, K, R>> creationInfos = new ArrayList<>();
		final List<Branch<I, L, K, R>> branches = new ArrayList<>();
		final BitSet commitList = new BitSet(nBranches);
		// happens automatically: commitList.clear();

		/* build commit list */
		int nCommittableActions = 0;
		for (int i = 0; i < nBranches; ++i) {
			final boolean isShouldCommit = get(i).isShouldCommit(baseBranch, cChecker);
			if (isShouldCommit) {
				/* mark the current action as applicable. */
				commitList.set(i);
				++nCommittableActions;
			}
		}

		if (nCommittableActions > 0) {
			/* create branch clones */
			branches.add(baseBranch);
			for (int i = 1; i < nCommittableActions; ++i) {
				branches.add(baseBranch.clone());
			}

			assert branches.size() == nCommittableActions;

			/* perform branch actions */
			/**
			 * go through the list of actions and apply those actions that are applicable.
			 *
			 * iBranch indexes into the pre-created branch-list and is only incremented for committed actions.
			 *
			 */
			int iBranch = 0;
			for (int iAction = 0; iAction < size(); ++iAction) {
				final IBranchAction<I, L, K, R> action = get(iAction);
				if (commitList.get(iAction)) {
					/* the current action is applicable iff the respective bit in the commitList is set */

					/* get the next branch and apply the action to it */
					final Branch<I, L, K, R> targetBranch = branches.get(iBranch);
					try {
						final NodeMergeInfo<I, L, K, R> mergeInfo = action.commit(targetBranch);
						creationInfos.add(new BranchCreationInfo<>(targetBranch, action, mergeInfo));
						++iBranch;
					} catch (ENodeMergeException ex) {
						/* ignore for now */
					}

				}
			}
		}
		return creationInfos;
	}
}
