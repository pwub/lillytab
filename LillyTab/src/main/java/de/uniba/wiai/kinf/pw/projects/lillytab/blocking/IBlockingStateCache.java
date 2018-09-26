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

import de.dhke.projects.cutil.collections.immutable.IImmutable;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import java.util.Collection;


/**
 *
 * Interface for a blocking state cache. The blocking state cache is used to cache the results of a search for blocking
 * nodes inside an {@link IABox}.
 * <p />
 * The actual search is performed by an instance of {@link IBlockingStrategy}. An implementation of
 * {@link IBlockingStrategy} may decide to use return cached state to avoid an expensive search.
 *
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public interface IBlockingStateCache
	extends Cloneable, IImmutable<IBlockingStateCache> {

	/**
	 * Determine if the specified node has a cached blocker.
	 *
	 * @param blockedNode The node to check
	 * @return {@literal true} if {@literal blockedNode} has a cached blocker or {@literal false} otherwise.
	 */
	boolean hasBlocker(final NodeID blockedNode);

	/**
	 * Return an iterable of NodeIDs that the parameter node is
	 * involved with during blocking.
	 * @param influencer The node involved in a block
	 * @return An {@code Iterable} of nodes that this node is involved in blocking.
	 */
	Collection<NodeID> getAffects(final NodeID influencer);

	/**
	 * Retrieve the blocker for the specified node.
	 *
	 * @param blockedNode The blocked node.
	 * @return If the cached block exists, retrieve the {@literal BlockInfo} of the block.
	 * If no cached block exists, {@literal null} is returned. In this case, it is unclear
	 * if the node is blocked.
	 */
	BlockInfo getBlockInfo(final NodeID blockedNode);

	void setBlockInfo(final NodeID blockedNode, final BlockInfo blockInfo);

	/**
	 * Invalidate all cached blocks affected by the supplied node.
	 * @param influencer The modified node.
	 */
	void invalidate(final NodeID influencer);

	IBlockingStateCache clone();

	@Override
	int hashCode();

	@Override
	boolean equals(Object other);
}
