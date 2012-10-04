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
package de.uniba.wiai.kinf.pw.projects.lillytab.blocking;

import de.dhke.projects.cutil.collections.immutable.IImmutable;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import java.util.Set;

/**
 * <p>
 *  Interface for a blocking state cache. The blocking state cache
 *  is used to cache the results of a search for blocking nodes inside an
 *  {@link IABox}. 
 * </p><p>
 *  The actual search is performed by an instance of {@link IBlockingStrategy}. An implementation of 
 *  {@link IBlockingStrategy} may decide to use return cached state to avoid an expensive search.
 * </p>
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public interface IBlockingStateCache
	extends Cloneable, IImmutable<IBlockingStateCache>
{
	/**
	 * Determine if the specified node has a cached blocker.
	 *
	 * @param blockedNode The node to check
	 * @return {@literal true} if {@literal blockedNode} has a cached blocker or {@literal false} otherwise.
	 */
	boolean hasBlocker(final NodeID blockedNode);

	/**
	 * Find all nodes blocked by the specified node.
	 *
	 * @param blocker The blocking (not blocked!) node.
	 * @return A set of nodes blocked by {@literal blocker}. Maybe empty.
	 **/
	Set<NodeID> getBlockedNodes(final NodeID blocker);

	/**
	 * Retrieve the blocker for the specified node.
	 *
	 * @param blockedNode The blocked node.
	 * @return The {@link NodeID} of the blocker of {@literal blockedNode}, if such exists in the cache; {@literal null} otherwise.
	 */
	NodeID getBlocker(final NodeID blockedNode);
	/**
	 * Retrieve the blocker for the specified node.
	 *
	 *
	 * @param <Name>
	 * @param <Klass>
	 * @param <Role>
	 * @param blockedNode The blocked node.
	 * @return The blocking node of {@literal blockedNode}, if such exists in the cache; {@literal null} otherwise.
	 */
	<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
		IABoxNode<Name, Klass, Role> getBlocker(final IABoxNode<Name, Klass, Role> blockedNode);

	/**
	 * <p>
	 * Update the blocker of {@literal blockedNode} to be {@literal blocker}.
	 * </p><p>
	 * If {@literal blocker} is {@literal null}, the cached blocker entry for
	 * {@literal blockedNode} is removed.
	 * </p>
	 *
	 * @param blockedNode The node to set blocking status for
	 * @param blocker The new blocker for {@literal blockedNode} or {@literal null} to delete the cache entry.
	 * @return The value of {@literal blocker}.
	 */
	NodeID setBlocker(final NodeID blockedNode, final NodeID blocker);

	IBlockingStateCache clone();

	@Override
	int hashCode();

	@Override
	boolean equals(Object other);
}
