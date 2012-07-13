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
package de.uniba.wiai.kinf.pw.projects.lillytab.abox;

import de.dhke.projects.cutil.collections.CollectionUtil;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.commons.collections15.Transformer;


/**
 * <p>
 * Helper class contains information about one or multiple node merge
 * chains. The node merge chain starts at a single node (the initial node)
 * and ends at a final node (the current node).
 * </p>
 * <p>
 * If multiple nodes were modified, either a collection of {@link NodeMergeInfo}s
 * should be returned or --by convention-- the node merge info of the smallest modified
 * node according to the natural order of nodes.
 * </p>
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public final class NodeMergeInfo<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
{
	/**
	 * The set of nodes that were "merged" away during the operation. Nodes in this set are no longer
	 * part of an ABox and should be handled with care.
	 **/
	private final SortedSet<IABoxNode<Name, Klass, Role>> _mergedNodes = new TreeSet<IABoxNode<Name, Klass, Role>>();
	/**
	 * The set of nodes whose concept, successor set or predecessor set was modified during the merge operation.
	 * Nodes in the modified set are still valid, i.e. the intersection of the set of merged nodes and
	 * the set of modified nodes is empty.
	 **/
	private final SortedSet<IABoxNode<Name, Klass, Role>> _modifiedNodes = new TreeSet<IABoxNode<Name, Klass, Role>>();
	/**
	 * The initial node the merge operation was started from.
	 **/
	private final IABoxNode<Name, Klass, Role> _initialNode;
	/**
	 * The current target node of the merge chain.
	 */
	private IABoxNode<Name, Klass, Role> _currentNode;

	public NodeMergeInfo(final IABoxNode<Name, Klass, Role> initialNode, final boolean wasModified)
	{
		_currentNode = initialNode;
		if (wasModified)
			_modifiedNodes.add(_currentNode);
		_initialNode = initialNode;
	}

	/**
	 * The last node is the current node.
	 *
	 * @return The current (= only valid) node in the merge chain.
	 */
	public IABoxNode<Name, Klass, Role> getCurrentNode()
	{
		/* return last node */
		return _currentNode;
	}

	/**
	 * @return The set of nodes merged away during the merge operation.
	 */
	public SortedSet<IABoxNode<Name, Klass, Role>> getMergedNodes()
	{
		return _mergedNodes;
	}

	/**
	 * @return The set of nodes modified during the merge operation.
	 */
	public SortedSet<IABoxNode<Name, Klass, Role>> getModifiedNodes()
	{
		return _modifiedNodes;
	}

	/**
	 * Get the initial node of the node merge chain.
	 *
	 * @return The initial node of the node merge chain.
	 */
	public IABoxNode<Name, Klass, Role> getInitialNode()
	{
		return _initialNode;
	}

	/**
	 * @return If the {@literal node} was modified during this merge operation.
	 */
	public boolean isModified(final IABoxNode<Name, Klass, Role> node)
	{
		return _modifiedNodes.contains(node);
	}

	/**
	 * Mark the specified node as modified.
	 *
	 * @param node The node to mark as modified.
	 */
	public void setModified(final IABoxNode<Name, Klass, Role> node)
	{
		if (!_mergedNodes.contains(node))
			_modifiedNodes.add(node);
	}

	/**
	 * <p>
	 * Record, that the {@link #getCurrentNode() } has been
	 * merged unto {@literal targetNode}.
	 * </p>
	 *
	 * @param targetNode The new curent node, i.e. the target node of the merge.
	 * @param wasModified {@literal true}, if the term set of the target node was modified.
	 */
	public void recordMerge(final IABoxNode<Name, Klass, Role> targetNode, final boolean wasModified)
	{
		/* a merge only happens, if the target node is different from the current node */
		assert !_mergedNodes.contains(_currentNode);
		if (!targetNode.equals(_currentNode))
			_mergedNodes.add(_currentNode);
		_currentNode = targetNode;
		if (wasModified)
			setModified(targetNode);
	}

	/**
	 * <p>
	 * Record, that the {@link #getCurrentNode() } has been
	 * merged unto {@literal targetNode}. Call only, if the
	 * term set of {@literal targetNode} was not modified.
	 * </p>
	 *
	 * @param targetNode The new curent node, i.e. the target node of the merge.
	 **/
	public void recordMerge(final IABoxNode<Name, Klass, Role> targetNode)
	{
		recordMerge(targetNode, false);
	}

	/**
	 * Append the specified merge info to the current merge info.
	 *
	 * @param next The merge info to append to the current merge info.
	 */
	public void append(final NodeMergeInfo<Name, Klass, Role> next)
	{
		/**
		 * paranoia check:
		 *  next must either
		 *  (1) have matching current nodes
		 *  (2) have our current node as initial node or
		 *  (3) merge our current node on the merge list
		 *
		 * In case (1), we may actually join two merge paths, but we always retain
		 * the initial node of the left hand side (this) merge info.
		 */
		if ((_currentNode != next._currentNode)
			&& (next._initialNode != _currentNode) && (!next._mergedNodes.contains(_currentNode))) {
			throw new IllegalArgumentException("Cannot join merge infos, current node does not appear in successor");
		} else {
			if (_currentNode != next._currentNode)
				_mergedNodes.add(_currentNode);
			_mergedNodes.addAll(next._mergedNodes);
			_modifiedNodes.addAll(next._modifiedNodes);
			/* merged nodes are never marked modified (they are actually zombies) */
			_modifiedNodes.removeAll(_mergedNodes);
			_currentNode = next._currentNode;
		}
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("NodeMergeInfo: ");
		sb.append("from ");
		sb.append(_initialNode.getNodeID());
		sb.append(" to ");
		sb.append(_currentNode.getNodeID());
		sb.append(" via ");
		sb.append(CollectionUtil.transformedToString(_mergedNodes, NodeToNodeIDTransformer.getInstance()));
		sb.append(", merged nodes: ");
		sb.append(CollectionUtil.transformedToString(_modifiedNodes, NodeToNodeIDTransformer.getInstance()));
		return sb.toString();
	}
}
