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

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import java.util.Collection;
import java.util.Collections;


/**
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public class ImmutableBlockingStateCache
	implements IBlockingStateCache {

	private final IBlockingStateCache _baseCache;

	ImmutableBlockingStateCache(final IBlockingStateCache baseCache)
	{
		_baseCache = baseCache.clone();
	}

	@Override
	public IBlockingStateCache clone()
	{
		return _baseCache.clone();
	}

	@Override
	public IBlockingStateCache getImmutable()
	{
		return this;
	}

	@Override
	public boolean hasBlocker(NodeID blockedNode)
	{
		return _baseCache.hasBlocker(blockedNode);
	}

	@Override
	public Collection<NodeID> getAffects(NodeID influencer)
	{
		return Collections.unmodifiableCollection(_baseCache.getAffects(influencer));
	}

	@Override
	public BlockInfo getBlockInfo(NodeID blockedNode)
	{
		return _baseCache.getBlockInfo(blockedNode);
	}

	@Override
	public void setBlockInfo(NodeID blockedNode, BlockInfo blockInfo)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableBlockingStateCache");
	}

	@Override
	public void invalidate(NodeID influencer)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableBlockingStateCache");
	}
}
