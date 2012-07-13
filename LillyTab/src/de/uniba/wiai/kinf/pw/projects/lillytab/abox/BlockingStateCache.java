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

import de.dhke.projects.cutil.collections.MapUtil;
import de.dhke.projects.cutil.collections.cow.CopyOnWriteMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


/**
 * Map-based implementation of {@link IBlockingStateCache}.
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class BlockingStateCache
	implements IBlockingStateCache
{
	private final Map<NodeID, NodeID> _blockMap;

	public BlockingStateCache()
	{
		this(new HashMap<NodeID, NodeID>());
	}

	public BlockingStateCache(final Map<NodeID, NodeID> blockMap)
	{
		_blockMap = blockMap;
	}

	/**
	 * Update the blocker of the target node.
	 * Set blocker to {@literal null} to clear blocking status.
	 *
	 * @param targetNode The node to set the blocker for
	 * @param blocker The blocking node
	 * @return The value of {@literal blocker}
	 **/
	public NodeID setBlocker(final NodeID targetNode, final NodeID blocker)
	{
		if (blocker == null) {
			_blockMap.remove(targetNode);
			return null;
		} else {
			_blockMap.put(targetNode, blocker);
			return blocker;
		}
	}

	public boolean haveCachedBlocker(final NodeID targetNode)
	{
		return _blockMap.containsKey(targetNode);
	}

	public NodeID getBlocker(final NodeID targetNode)
	{
		return _blockMap.get(targetNode);
	}

	public <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
		   IABoxNode<Name, Klass, Role> getBlocker(
		final IABoxNode<Name, Klass, Role> targetNode)
	{
		final IABox<Name, Klass, Role> abox = targetNode.getABox();
		final NodeID blockerID = _blockMap.get(targetNode.getNodeID());
		return abox.getNode(blockerID);
	}

	public boolean hasBlocker(NodeID blockedNode)
	{
		return _blockMap.containsKey(blockedNode);
	}

	public Set<NodeID> getBlockedNodes(NodeID blocker)
	{
		final Set<NodeID> blockedNodes = new TreeSet<NodeID>();

		for (Map.Entry<NodeID, NodeID> entry : _blockMap.entrySet()) {
			if (entry.getValue().equals(blocker))
				blockedNodes.add(entry.getKey());
		}
		return blockedNodes;
	}

	@Override
	public BlockingStateCache clone()
	{
		Map<NodeID, NodeID> baseBlockMap = _blockMap;
		/* find base map, avoid multiple COW layers */
		while (baseBlockMap instanceof CopyOnWriteMap)
			baseBlockMap = ((CopyOnWriteMap<NodeID, NodeID>)_blockMap).getDecoratee();

		final Map<NodeID, NodeID> klonedBlockMap = CopyOnWriteMap.decorate(baseBlockMap);
		final BlockingStateCache klone = new BlockingStateCache(klonedBlockMap);
		return klone;
	}

	@Override
	public int hashCode()
	{
		return MapUtil.deepHashCode(_blockMap);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj instanceof BlockingStateCache) {
			BlockingStateCache other = (BlockingStateCache)obj;
			return MapUtil.deepEquals(_blockMap, other._blockMap);
		} else
			return false;
	}

	public IBlockingStateCache getImmutable()
	{
		return new ImmutableBlockingStateCache(this);
	}
}
