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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.completer;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentABoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.Branch;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.INodeConsistencyChecker;

/**
 * <p>
 *	An action that is performed on a branch.
 * <p></p>
 *  The {@link IBranchAction} is used in combination with {@link BranchActionList} to
 *  support two-phase application of branch actions:
 *  <ul>
 *    <li>Branch actions are first check for the applicability and
 *	    then entered into the branch list.</li>
 *    <li>
 *      The actual branches are only created when 
 *		{@link BranchActionList#commit(de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.Branch) }
 *		is called.
 *    </li>    
 *  </ul>
 * 
 * </p>
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 * @return The merge info created by the branch action (if any).
 */
public interface IBranchAction<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
{
	/**
	 * <p>
	 * Check if the current branch action should really be committed
	 * to the target branch or if the action should be avoided,
	 * for example because it would cause an inconsistency straight away.
	 * </p><p>
	 * This method should return {@literal true} if and only if
	 * it is sure, that the action should be applied. Explicitly,
	 * a return value of {@literal false} does not mean, that
	 * committing the current action is always unproblematic.
	 * </p>
	 *
	 * @param branch The target branch
	 * @param cChecker A node consistency checker
	 * @return {@literal true} if the target action should be committed to the
	 * target branch.
	 */
	boolean isShouldCommit(Branch<Name, Klass, Role> branch, INodeConsistencyChecker<Name, Klass, Role> cChecker);

	/**
	 * Apply the changes indicated by this branch action to the specified branch.
	 * 
	 * @param branch The target branch
	 * @return The {@link NodeMergeInfo} created during the commit.
	 */
	NodeMergeInfo<Name, Klass, Role> commit(Branch<Name, Klass, Role> branch)
		throws EInconsistentABoxException;

}
