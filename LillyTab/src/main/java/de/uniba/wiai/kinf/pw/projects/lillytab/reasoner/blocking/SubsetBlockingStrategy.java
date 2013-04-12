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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.blocking;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.blocking.AbstractBlockingStrategy;
import de.uniba.wiai.kinf.pw.projects.lillytab.blocking.IBlockingStateCache;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * An implementation of subset blocking as it is sufficient for description logics without inverse roles.
 *
 * @param <I> The type for nominals and values
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class SubsetBlockingStrategy<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> 
	extends AbstractBlockingStrategy<I, L, K, R> {

	/**
	 * 
	 * Determine, if this node is a potential blocker node for {@literal target}.
	 * <p />
	 * We are a potential blocker, if the target's concept set is a subset of ours and if we are (strictly) in front of
	 * target (according to the natural order of nodes). Note that we may not block ourselves.
	 * 
	 *
	 * @param blocker
	 * @param target A potential blocker.
	 * @return {@literal true} if {@literal target} is a potential blocking node for {@literal this} node.
	 */
	protected boolean isPotentialBlocker(final IABoxNode<I, L, K, R> blocker,
										 final IABoxNode<I, L, K, R> target)
	{
		/**
		 * This node is potentially blocking {@literal target}, if target's set of concept terms is a superset of ours
		 * i.e. if target is less specific that the current node.#
		 *
		 * The size check is a very simple performance trick, that helps for some ontologies (
		 *
		 */
		return ((blocker.compareTo(target) < 0) && (blocker.getTerms().size() >= target.getTerms().size())
			&& blocker.getTerms().containsAll(target.getTerms()));
	}


	@Override
	public Set<NodeID> validateBlocks(IABoxNode<I, L, K, R> blocker)
	{
		final Set<NodeID> unblocked = new TreeSet<>();
		final Iterator<NodeID> iter = getBlockedNodeIDs(blocker).iterator();
		final IABox<I, L, K, R> abox = blocker.getABox();
		final IBlockingStateCache stateCache = abox.getBlockingStateCache();
		while (iter.hasNext()) {
			final NodeID blockedID = iter.next();
			IABoxNode<I, L, K, R> blockedNode = abox.getNode(blockedID);
			/* node is sometimes null, happens after merges */
			if ((blockedNode == null) || (!isPotentialBlocker(blocker, blockedNode))) {
				// logFinest("'%s' is no longer a blocker for '%s'", node, this);
				stateCache.setBlocker(blockedNode.getNodeID(), null);
				unblocked.add(blockedID);
			}
		}
		return unblocked;
	}


	@Override
	public IABoxNode<I, L, K, R> findBlocker(IABoxNode<I, L, K, R> targetNode)
	{
		final IABox<I, L, K, R> abox = targetNode.getABox();
		assert abox != null;
		final IBlockingStateCache stateCache = abox.getBlockingStateCache();
		/* search for potential blockers */
		for (IABoxNode<I, L, K, R> testNode : abox.headSet(targetNode)) {
			/* stop if we hit the current node */
			if (targetNode.compareTo(testNode) <= 0) {
				break;
			}
			final IABoxNode<I, L, K, R> blocker = testNode;
			if (isPotentialBlocker(blocker, targetNode)) {
				stateCache.setBlocker(targetNode.getNodeID(), blocker.getNodeID());
				/* found blocker, stop */
				// logFinest("node '%s' blocked by '%s'", this, blocker);
				return blocker;
			}
		}
		return null;
	}
}
