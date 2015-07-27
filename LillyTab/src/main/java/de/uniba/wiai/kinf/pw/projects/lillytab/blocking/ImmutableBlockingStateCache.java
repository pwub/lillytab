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

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import java.util.Set;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ImmutableBlockingStateCache
	implements IBlockingStateCache {

	private final BlockingStateCache _baseCache;


	ImmutableBlockingStateCache(final BlockingStateCache baseCache)
	{
		_baseCache = baseCache.clone();
	}


	@Override
	public boolean hasBlocker(NodeID blockedNode)
	{
		return _baseCache.hasBlocker(blockedNode);
	}


	@Override
	public Set<NodeID> getBlockedNodes(NodeID blocker)
	{
		return _baseCache.getBlockedNodes(blocker);
	}


	@Override
	public NodeID getBlocker(NodeID blockedNode)
	{
		return _baseCache.getBlocker(blockedNode);
	}


	@Override
	public <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>  IABoxNode<I, L, K, R> getBlocker(
		IABoxNode<I, L, K, R> blockedNode)
	{
		return _baseCache.getBlocker(blockedNode);
	}


	@Override
	public NodeID setBlocker(NodeID blockedNode, NodeID blocker)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableBlockingStateCache.");
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
}
