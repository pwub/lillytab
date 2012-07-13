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

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public abstract class AbstractBranchAction<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	implements IBranchAction<Name, Klass, Role>
{
	private final TermEntry<Name, Klass, Role> _parentEntry;
	private final NodeID _targetNodeID;

	public AbstractBranchAction(final TermEntry<Name, Klass, Role> parentEntry, IABoxNode<Name, Klass, Role> targetNode)
	{
		this(parentEntry, targetNode.getNodeID());
	}

	public AbstractBranchAction(final TermEntry<Name, Klass, Role> parentEntry, NodeID targetNodeID)
	{
		_parentEntry = parentEntry;
		_targetNodeID = targetNodeID;
	}

	public TermEntry<Name, Klass, Role> getParentEntry()
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
