/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.blocking;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.blocking.AbstractBlockingStrategy;
import de.uniba.wiai.kinf.pw.projects.lillytab.blocking.IBlockingStateCache;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class DoubleBlockingStrategy<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends AbstractBlockingStrategy<I, L, K, R> {

	@Override
	public IABoxNode<I, L, K, R> findBlocker(IABoxNode<I, L, K, R> targetNode)
	{
		final IABox<I, L, K, R> abox = targetNode.getABox();
		assert abox != null;
		final IBlockingStateCache stateCache = abox.getBlockingStateCache();

		final Set<IABoxNode<I, L, K, R>> visited = new HashSet<>();
		visited.add(targetNode);

		/* search for potential blockers */
		final Queue<IABoxNode<I, L, K, R>> candidates = new LinkedList<>();
		for (IABoxNode<I, L, K, R> pred : targetNode.getRABox().getPredecessorNodes()) {
			if (pred.compareTo(targetNode) < 0) {
				candidates.add(pred);
			}
		}

		throw new UnsupportedOperationException("Double blocking is not yet supported");
//		while (!candidates.isEmpty()) {
//			final IABoxNode<I, L, K, R> candidate = candidates.remove();
//			visited.add(candidate);
//			for (IABoxNode<I, L, K, R> pred : candidate.getRABox().getPredecessorNodes()) {
//				if ((pred.compareTo(targetNode) < 0) && !visited.contains(pred)) {
//					candidates.add(pred);
//				}
//			}
//			if (isPotentialBlocker(candidate, targetNode)) {
//				for (IABoxNode<I, L, K, R>I)
//				final Set<IABoxNode<I, L, K, R>> predCands = new TreeSet<>(candidate.getRABox().getPredecessorNodes());
//
//				/* prune candidates by role */
//				for (R role : targetNode.getRABox().getIncomingRoles()) {
//					predCands.retainAll(candidate.getRABox().getPredecessorNodes(role));
//				}
//
//				for (IABoxNode<I, L, K, R> predCand: predCands) {
//
//				}
//			}
//		}
//
//		return null;
	}

	@Override
	public Set<NodeID> validateBlocks(IABoxNode<I, L, K, R> targetNode)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
		 * - it is an anonymous node
		 * - if the target's set of concept terms is a equal to that of the blocker's
		 *
		 **/
		return blocker.isAnonymous() && blocker.getTerms().size() == target.getTerms().size() && blocker.getTerms().
			containsAll(target.getTerms());
	}
}
