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

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;

/**
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public abstract class AbstractBranchAction<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> 
	implements IBranchAction<I, L, K, R> {

	private final TermEntry<I, L, K, R> _parentEntry;
	private final NodeID _targetNodeID;


	public AbstractBranchAction(final TermEntry<I, L, K, R> parentEntry, IABoxNode<I, L, K, R> targetNode)
	{
		this(parentEntry, targetNode.getNodeID());
	}


	public AbstractBranchAction(final TermEntry<I, L, K, R> parentEntry, NodeID targetNodeID)
	{
		_parentEntry = parentEntry;
		_targetNodeID = targetNodeID;
	}


	public TermEntry<I, L, K, R> getParentEntry()
	{
		return _parentEntry;
	}


	/**
	 * @return the _targetNodeID
	 */
	public NodeID getTargetNodeID()
	{
		return _targetNodeID;
	}
}
