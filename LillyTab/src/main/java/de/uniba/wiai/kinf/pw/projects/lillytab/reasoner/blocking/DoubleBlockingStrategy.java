/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.blocking;

import de.dhke.projects.cutil.collections.CollectionUtil;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.blocking.BlockInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.blocking.BlockableAncestorIterable;
import de.uniba.wiai.kinf.pw.projects.lillytab.blocking.IBlockingStateCache;
import de.uniba.wiai.kinf.pw.projects.lillytab.blocking.IBlockingStrategy;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class DoubleBlockingStrategy<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	implements IBlockingStrategy<I, L, K, R> {

	protected boolean isLabelBlocked(final IABoxNode<I, L, K, R> blocker, final IABoxNode<I, L, K, R> blocked)
	{
		return blocker.isAnonymous()
			&& blocked.isAnonymous()
			&& blocker.getTerms().size() == blocked.getTerms().size()
			&& blocker.getTerms().containsAll(blocked.getTerms());
	}

	/**
	 * Test if (x0, x1) and (y0, y1) are connected by the
	 * same set of roles.
	 *
	 * @param x0 a node
	 * @param x1 a (potential) successor of x0
	 * @param y0 a node, typically an ancestor of x0
	 * @param y1 a (potential) successor of y1.
	 * @return {@literal true}, if (x0, x1) and (y0, y1) are connected by the same set of roles.
	 */
	protected boolean isRoleLabelBlocked(
		final IABoxNode<I, L, K, R> x0,
		final IABoxNode<I, L, K, R> x1,
		final IABoxNode<I, L, K, R> y0,
		final IABoxNode<I, L, K, R> y1
	)
	{
		/* because of how the tablaux is constructed, we do not need to
		 * consider successors. We always have an incoming role link
		 * from real predecessors.
		 **/
		boolean x1isSuccessor = false;
		boolean y1isSuccessor = false;
		int x0RoleCount = 0;
		int y0RoleCount = 0;

		for (R r : x0.getRABox().getOutgoingRoles(x1)) {
			x1isSuccessor = true;
			++x0RoleCount;
			if (!y0.getRABox().hasSuccessor(r, y1)) {
				return false;
			}
		}
		for (R r : x0.getRABox().getIncomingRoles(x1)) {
			++x0RoleCount;
			if (!y0.getRABox().hasPredecessor(r, y1)) {
				return false;
			}
		}

		for (R r : y0.getRABox().getOutgoingRoles(y1)) {
			y1isSuccessor = true;
			++y0RoleCount;
		}
		for (R r : y0.getRABox().getIncomingRoles(y1)) {
			++y0RoleCount;
		}
		return y1isSuccessor && y1isSuccessor && (x0RoleCount == y0RoleCount);
	}

	private boolean isBlocker(
		final IABoxNode<I, L, K, R> x0,
		final IABoxNode<I, L, K, R> x1,
		final IABoxNode<I, L, K, R> y0,
		final IABoxNode<I, L, K, R> y1
	)
	{
		if (!isLabelBlocked(x0, y0)) {
			return false;
		}
		if (isLabelBlocked(y1, y1) && isRoleLabelBlocked(x0, x1, y0, y1)) {
			return true;
		}
		return false;
	}

	private boolean isBlocker(
		final IABoxNode<I, L, K, R> x1,
		final IABoxNode<I, L, K, R> y1
	)
	{
		if (!isLabelBlocked(x1, y1)) {
			return false;
		}
		for (IABoxNode<I, L, K, R> x0 : x1.getRABox().getPredecessorNodes()) {
			for (IABoxNode<I, L, K, R> y0 : y1.getRABox().getPredecessorNodes()) {
				if (isLabelBlocked(y0, y1) && isRoleLabelBlocked(x0, x1, y0, y1)) {
					return true;
				}
			}
		}
		return false;
	}

	public BlockInfo findBlocker(IABoxNode<I, L, K, R> x1)
	{
		for (IABoxNode<I, L, K, R> x0 : x1.getRABox().getPredecessorNodes()) {
			for (final IABoxNode<I, L, K, R> y0 : new BlockableAncestorIterable<>(x0)) {
				for (IABoxNode<I, L, K, R> y1 : y0.getRABox().getSuccessorNodes()) {
					if (isBlocker(x0, x1, y0, y1)) {
						return new DoubleBlockingBlockInfo(x0.getNodeID(), x1.getNodeID(), y0.getNodeID(), y1.
							getNodeID());
					}
				}
			}
		}
		return null;
	}

	@Override
	public boolean isBlocked(final IABoxNode<I, L, K, R> blockedNode)
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
