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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.blocking;

import de.dhke.projects.cutil.collections.CollectionUtil;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.blocking.BlockInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.blocking.BlockableAncestorIterable;
import de.uniba.wiai.kinf.pw.projects.lillytab.blocking.IBlockingStateCache;
import de.uniba.wiai.kinf.pw.projects.lillytab.blocking.IBlockingStrategy;
import java.util.Collection;


/**
 * An implementation of subset blocking as it is sufficient for description logics without inverse roles.
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public class SubsetBlockingStrategy<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	implements IBlockingStrategy<I, L, K, R> {

	public BlockInfo findBlocker(IABoxNode<I, L, K, R> targetNode)
	{
		final IABox<I, L, K, R> abox = targetNode.getABox();
		assert abox != null;
		final IBlockingStateCache stateCache = abox.getBlockingStateCache();

		/* search for potential blockers */
		for (IABoxNode<I, L, K, R> candidate : new BlockableAncestorIterable<>(targetNode)) {
			if (isPotentialBlocker(candidate, targetNode)) {
				final BlockInfo blockInfo = new BlockInfo(candidate.getNodeID());
				stateCache.setBlockInfo(targetNode.getNodeID(), blockInfo);
				/* found blocker, stop */
				// logFinest("node '%s' blocked by '%s'", this, blocker);
				return blockInfo;
			}
		}
		return null;
	}

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
	protected boolean isPotentialBlocker(final IABoxNode<I, L, K, R> blocker, final IABoxNode<I, L, K, R> target)
	{
		/**
		 * This node is potentially blocking {@literal target},
		 * if
		 * - the potential blocker is an anonymous node
		 * - the target is synthetic
		 * - if the target's set of concept terms is a superset of the blocker's (i.e. if target is less specific that the current node.)
		 *
		 * The size check is a very simple performance trick, that helps for some ontologies
		 */
		return target.isSynthentic()
			&& blocker.isAnonymous()
			&& blocker.getTerms().size() >= target.getTerms().size()
			&& blocker.getTerms().containsAll(target.getTerms());
	}

	@Override
	public boolean isBlocked(IABoxNode<I, L, K, R> blockedNode)
	{
		final BlockInfo blockInfo = blockedNode.getABox().getBlockingStateCache().getBlockInfo(blockedNode.getNodeID());
		if (blockInfo != null) {
			return false;
		}
		return findBlocker(blockedNode) != null;
	}

	@Override
	public void validateBlocks(IABoxNode<I, L, K, R> influencer)
	{
		final IABox<I, L, K, R> abox = influencer.getABox();
		final Collection<NodeID> affected = influencer.getABox().getBlockingStateCache().getAffects(influencer.
			getNodeID());
		if (!CollectionUtil.isNullOrEmpty(affected)) {
			affected.forEach((node) -> {
				abox.getBlockingStateCache().setBlockInfo(node, null);
			});

		}
	}
}
