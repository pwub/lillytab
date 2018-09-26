/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.blocking;

import de.dhke.projects.cutil.collections.set.Flat3Set;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;


public class BlockableAncestorIterable<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	implements Iterable<IABoxNode<I, L, K, R>> {

	private final IABoxNode<I, L, K, R> _start;

	public BlockableAncestorIterable(final IABoxNode<I, L, K, R> start)
	{
		_start = start;
	}

	@Override
	public Iterator<IABoxNode<I, L, K, R>> iterator()
	{
		return new BlockableAncestorIterator<>(_start);
	}


	static class BlockableAncestorIterator<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
		implements Iterator<IABoxNode<I, L, K, R>> {

		private final LinkedList<IABoxNode<I, L, K, R>> _candidates = new LinkedList<>();
		private final Set<IABoxNode<I, L, K, R>> _visited = new Flat3Set<>();

		public BlockableAncestorIterator(final IABoxNode<I, L, K, R> start)
		{
			addBlockablePredecessors(start);
		}

		private void addBlockablePredecessors(IABoxNode<I, L, K, R> start)
		{
			if (_visited.add(start)) {
				start.getRABox().getPredecessorNodes().forEach((t) -> {
					if (!_visited.contains(t) && t.isAnonymous() && (t.compareTo(start) < 0)) {
						_candidates.add(t);
						_visited.add(t);
					}
				});
			}
		}

		@Override
		public boolean hasNext()
		{
			return !_candidates.isEmpty();
		}

		@Override
		public IABoxNode<I, L, K, R> next()
		{
			final IABoxNode<I, L, K, R> ancestor = _candidates.remove();
			addBlockablePredecessors(ancestor);
			return ancestor;
		}

	}
}
