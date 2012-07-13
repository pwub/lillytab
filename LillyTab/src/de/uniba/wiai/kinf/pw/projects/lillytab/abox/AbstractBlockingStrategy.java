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

import java.util.Set;


/**
 *
 * @param <Name>
 * @param <Klass>
 * @param <Role>
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public abstract class AbstractBlockingStrategy<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>

	implements IBlockingStrategy<Name, Klass, Role>
{
	public Set<NodeID> getBlockedNodeIDs(IABoxNode<Name, Klass, Role> blocker)
	{
		final IBlockingStateCache stateCache = blocker.getABox().getBlockingStateCache();
		return stateCache.getBlockedNodes(blocker.getNodeID());
	}

	public boolean isBlocked(IABoxNode<Name, Klass, Role> blockedNode)
	{
		assert blockedNode.getABox() != null;
		final IBlockingStateCache stateCache = blockedNode.getABox().getBlockingStateCache();
		if (stateCache.hasBlocker(blockedNode.getNodeID())) {
			return true;
		} else {
			final IABoxNode<Name, Klass, Role> blocker = findBlocker(blockedNode);
			if (blocker != null)
				stateCache.setBlocker(blockedNode.getNodeID(), blocker.getNodeID());
			else
				stateCache.setBlocker(blockedNode.getNodeID(), null);
			return blocker != null;
		}
	}

	public IABoxNode<Name, Klass, Role> getBlocker(final IABoxNode<Name, Klass, Role> blockedNode)
	{
		final IBlockingStateCache stateCache = blockedNode.getABox().getBlockingStateCache();
		if (stateCache.hasBlocker(blockedNode.getNodeID())) {
			return stateCache.getBlocker(blockedNode);
		} else {
			final IABoxNode<Name, Klass, Role> blocker = findBlocker(blockedNode);
			stateCache.setBlocker(blockedNode.getNodeID(), blocker.getNodeID());
			return blocker;
		}
	}


	public abstract IABoxNode<Name, Klass, Role> findBlocker(final IABoxNode<Name, Klass, Role> targetNode);
}
