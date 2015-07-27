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
 * @param <I> The type for nominals and values
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public abstract class AbstractBlockingStrategy<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> 
	implements IBlockingStrategy<I, L, K, R> {

	@Override
	public Set<NodeID> getBlockedNodeIDs(IABoxNode<I, L, K, R> blocker)
	{
		final IBlockingStateCache stateCache = blocker.getABox().getBlockingStateCache();
		return stateCache.getBlockedNodes(blocker.getNodeID());
	}


	@Override
	public boolean isBlocked(IABoxNode<I, L, K, R> blockedNode)
	{
		assert blockedNode.getABox() != null;
		final IBlockingStateCache stateCache = blockedNode.getABox().getBlockingStateCache();
		if (stateCache.hasBlocker(blockedNode.getNodeID())) {
			return true;
		} else {
			final IABoxNode<I, L, K, R> blocker = findBlocker(blockedNode);
			if (blocker != null) {
				stateCache.setBlocker(blockedNode.getNodeID(), blocker.getNodeID());
			} else {
				stateCache.setBlocker(blockedNode.getNodeID(), null);
			}
			return blocker != null;
		}
	}


	@Override
	public IABoxNode<I, L, K, R> getBlocker(final IABoxNode<I, L, K, R> blockedNode)
	{
		final IBlockingStateCache stateCache = blockedNode.getABox().getBlockingStateCache();
		if (stateCache.hasBlocker(blockedNode.getNodeID())) {
			return stateCache.getBlocker(blockedNode);
		} else {
			final IABoxNode<I, L, K, R> blocker = findBlocker(blockedNode);
			stateCache.setBlocker(blockedNode.getNodeID(), blocker.getNodeID());
			return blocker;
		}
	}

	public abstract IABoxNode<I, L, K, R> findBlocker(final IABoxNode<I, L, K, R> targetNode);

}
