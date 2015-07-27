/**
 * (c) 2009-2014 Peter Wullinger
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
package de.dhke.projects.cutil.collections.frozen;

import de.dhke.projects.cutil.Pair;
import de.dhke.projects.cutil.collections.iterator.PairIterable;
import de.dhke.projects.cutil.collections.set.SortedListSet;
import java.util.*;
import org.apache.commons.collections15.SetUtils;


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class FrozenSortedList<T extends Comparable<? super T>>
	extends AbstractSet<T>
	implements Comparable<FrozenSortedList<T>>
{
	final List<T> _elements;
	
	public FrozenSortedList(final Collection<? extends T> source)
	{
		final SortedListSet<T> elements = new SortedListSet<>(source);
		_elements = Collections.unmodifiableList(elements);
	}

	@Override
	public Iterator<T> iterator()
	{
		return _elements.iterator();
	}

	@Override
	public int size()
	{
		return _elements.size();
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean contains(Object o)
	{
		return Collections.binarySearch(_elements, (T)o) >= 0;
	}

	@Override
	public boolean isEmpty()
	{
		return _elements.isEmpty();
	}

	@Override
	public int hashCode()
	{
		return SetUtils.hashCodeForSet(_elements);
	}
	
	public List<T> getList()
	{
		return _elements;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Collection) {
			Collection<?> c = (Collection<?>)o;
			return SetUtils.isEqualSet(_elements, c);
		} else
			return false;
	}	


	@Override
	public int compareTo(
						 FrozenSortedList<T> o)
	{
		for (Pair<T, T> pair: PairIterable.wrap(this, o)) {
			int compare = pair.getFirst().compareTo(pair.getSecond());
			if (compare != 0)
				return compare;
		}
		return 0;
	}
}
