/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dhke.projects.cutil.collections.set;

import de.dhke.projects.cutil.Pair;
import de.dhke.projects.cutil.collections.iterator.PairIterable;
import java.util.Comparator;
import java.util.SortedSet;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class SortedSetComparator<T extends Comparable<? super T>, S extends SortedSet<T>>
	implements Comparator<S>
{
	@Override
	public int compare(S o1, S o2)
	{
		int compare = 0;
		for (Pair<T, T> pair: PairIterable.wrap(o1, o2)) {
			compare = pair.getFirst().compareTo(pair.getSecond());
			if (compare != 0)
				return compare; 
		}
		assert compare == 0;
		return compare;
	}

}
