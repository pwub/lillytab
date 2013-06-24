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
package de.uniba.wiai.kinf.pw.projects.lillytab.blocking;

import de.dhke.projects.cutil.collections.MapUtil;
import de.dhke.projects.cutil.collections.cow.CopyOnWriteMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
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
	implements IBlockingStateCache {

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
	 * Update the blocker of the target node. Set blocker to {@literal null} to clear blocking status.
	 *
	 * @param targetNode The node to set the blocker for
	 * @param blocker The blocking node
	 * @return The value of {@literal blocker}
	 *
	 */
	@Override
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


	@Override
	public NodeID getBlocker(final NodeID targetNode)
	{
		return _blockMap.get(targetNode);
	}


	@Override
	public <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>  IABoxNode<I, L, K, R> getBlocker(
		final IABoxNode<I, L, K, R> targetNode)
	{
		final IABox<I, L, K, R> abox = targetNode.getABox();
		final NodeID blockerID = _blockMap.get(targetNode.getNodeID());
		return abox.getNode(blockerID);
	}


	@Override
	public boolean hasBlocker(NodeID blockedNode)
	{
		return _blockMap.containsKey(blockedNode);
	}


	@Override
	public Set<NodeID> getBlockedNodes(NodeID blocker)
	{
		final Set<NodeID> blockedNodes = new TreeSet<>();

		for (Map.Entry<NodeID, NodeID> entry : _blockMap.entrySet()) {
			if (entry.getValue().equals(blocker)) {
				blockedNodes.add(entry.getKey());
			}
		}
		return blockedNodes;
	}


	@Override
	public BlockingStateCache clone()
	{
		Map<NodeID, NodeID> baseBlockMap = _blockMap;
		/* find base map, avoid multiple COW layers */
		while (baseBlockMap instanceof CopyOnWriteMap) {
			baseBlockMap = ((CopyOnWriteMap<NodeID, NodeID>) _blockMap).getDecoratee();
		}

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
		if (this == obj) {
			return true;
		}
		if (obj instanceof BlockingStateCache) {
			BlockingStateCache other = (BlockingStateCache) obj;
			return MapUtil.deepEquals(_blockMap, other._blockMap);
		} else {
			return false;
		}
	}


	@Override
	public IBlockingStateCache getImmutable()
	{
		return new ImmutableBlockingStateCache(this);
	}
}
