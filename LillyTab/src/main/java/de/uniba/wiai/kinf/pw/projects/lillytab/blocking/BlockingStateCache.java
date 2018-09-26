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
package de.uniba.wiai.kinf.pw.projects.lillytab.blocking;

import de.dhke.projects.cutil.collections.MapUtil;
import de.dhke.projects.cutil.collections.MultiMapUtil;
import de.dhke.projects.cutil.collections.factories.TreeSetFactory;
import de.dhke.projects.cutil.collections.map.GenericMultiHashMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections15.MultiMap;


/**
 * Map-based implementation of {@link IBlockingStateCache}.
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public class BlockingStateCache
	implements IBlockingStateCache {

	private final Map<NodeID, BlockInfo> _blockMap;
	private final MultiMap<NodeID, NodeID> _isInvoledIn;

	public BlockingStateCache()
	{
		this(new HashMap<>(), new GenericMultiHashMap<>(new TreeSetFactory<>()));
	}

	public BlockingStateCache(
		final Map<NodeID, BlockInfo> blockMap,
		final MultiMap<NodeID, NodeID> isInvolvedIn
	)
	{
		_blockMap = blockMap;
		_isInvoledIn = isInvolvedIn;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj instanceof BlockingStateCache) {
			BlockingStateCache other = (BlockingStateCache) obj;
			return MapUtil.deepEquals(_blockMap, other._blockMap) && MultiMapUtil.deepEquals(_isInvoledIn,
				other._isInvoledIn);
		} else {
			return false;
		}
	}

	@Override
	public IBlockingStateCache getImmutable()
	{
		return new ImmutableBlockingStateCache(this);
	}

	@Override
	public int hashCode()
	{
		return MapUtil.deepHashCode(_blockMap) + 23 * MultiMapUtil.deepHashCode(_isInvoledIn);
	}

	@Override
	public boolean hasBlocker(NodeID blockedNode)
	{
		return _blockMap.containsKey(blockedNode);
	}

	@Override
	public BlockInfo getBlockInfo(NodeID blockedNode)
	{
		return _blockMap.get(blockedNode);
	}

	@Override
	public void setBlockInfo(NodeID blockedNode, BlockInfo blockInfo)
	{
		final BlockInfo oldBlockInfo = _blockMap.get(blockedNode);
		if (oldBlockInfo != null) {
			oldBlockInfo.getInvolvedNodes().forEach((influencer) -> {
				_isInvoledIn.remove(influencer, blockedNode);
			});
		}
		if (blockInfo != null) {
			_blockMap.put(blockedNode, blockInfo);
			blockInfo.getInvolvedNodes().forEach((influencer) -> {
				_isInvoledIn.put(influencer, blockedNode);
			});
		} else {
			_blockMap.remove(blockedNode);
		}

	}

	@Override
	public IBlockingStateCache clone()
	{
		final Map<NodeID, BlockInfo> blockMap = new HashMap<>(_blockMap);
		final MultiMap<NodeID, NodeID> isInvolvedIn = new GenericMultiHashMap<>(new TreeSetFactory<>());
		isInvolvedIn.putAll(_isInvoledIn);

		return new BlockingStateCache(blockMap, isInvolvedIn);
	}

	@Override
	public Collection<NodeID> getAffects(NodeID influencer)
	{
		return _isInvoledIn.get(influencer);
	}

	@Override
	public void invalidate(NodeID influencer)
	{
		
		setBlockInfo(influencer, null);
	}



}
