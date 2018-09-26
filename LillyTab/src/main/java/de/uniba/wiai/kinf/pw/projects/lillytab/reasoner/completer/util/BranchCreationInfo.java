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

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.Branch;

/**
 * 
 * Helper class to contain information about newly creation branches.
 * 
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public class BranchCreationInfo<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>  {

	private final Branch<I, L, K, R> _targetBranch;
	private final IBranchAction<I, L, K, R> _action;
	private final NodeMergeInfo<I, L, K, R> _mergeInfo;


	public BranchCreationInfo(Branch<I, L, K, R> targetBranch, IBranchAction<I, L, K, R> action,
							  NodeMergeInfo<I, L, K, R> mergeInfo)
	{
		_targetBranch = targetBranch;
		_action = action;
		_mergeInfo = mergeInfo;
	}


	/**
	 * @return the _targetBranch
	 */
	public Branch<I, L, K, R> getTargetBranch()
	{
		return _targetBranch;
	}


	/**
	 * @return the _action
	 */
	public IBranchAction<I, L, K, R> getAction()
	{
		return _action;
	}


	/**
	 * @return the _mergeInfo
	 */
	public NodeMergeInfo<I, L, K, R> getMergeInfo()
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
