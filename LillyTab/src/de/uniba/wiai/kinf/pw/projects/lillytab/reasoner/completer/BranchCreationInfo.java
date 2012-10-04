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

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.Branch;

/**
 * <p>
 * Helper class to contain information about newly creation branches.
 * </p>
 *
 * @param <Name>
 * @param <Klass>
 * @param <Role>
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class BranchCreationInfo<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
{
	private final Branch<Name, Klass, Role> _targetBranch;
	private final IBranchAction<Name, Klass, Role> _action;
	private final NodeMergeInfo<Name, Klass, Role> _mergeInfo;

	public BranchCreationInfo(Branch<Name, Klass, Role> targetBranch, IBranchAction<Name, Klass, Role> action, NodeMergeInfo<Name, Klass, Role> mergeInfo)
	{
		_targetBranch = targetBranch;
		_action = action;
		_mergeInfo = mergeInfo;
	}

	/**
	 * @return the _targetBranch
	 */
	public Branch<Name, Klass, Role> getTargetBranch()
	{
		return _targetBranch;
	}

	/**
	 * @return the _action
	 */
	public IBranchAction<Name, Klass, Role> getAction()
	{
		return _action;
	}

	/**
	 * @return the _mergeInfo
	 */
	public NodeMergeInfo<Name, Klass, Role> getMergeInfo()
	{
		return _mergeInfo;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("New Branch:\n");
		sb.append(_targetBranch.toString());
		sb.append("via Action:\n");
		sb.append(_action.toString());
		sb.append("Result:\n");
		sb.append(_mergeInfo.toString());
		return sb.toString();
	}


}
