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
package de.dhke.projects.cutil.collections.set;

import java.util.*;

/**
 *
 * @param <T> 
 * @author Peter Wullinger <java@dhke.de>
 */
public class SortedListSet<T extends Comparable<? super T>>
	extends AbstractList<T>
	implements Set<T> {

	private final Comparator<T> _comparator;	
	private final List<T> _backingList;	
	
	public SortedListSet(final Collection<? extends T> collection)
	{
		_comparator = null;
		_backingList = new ArrayList<>(collection);
		Collections.sort(_backingList);
		removeDuplicates();
	}

	public SortedListSet(final Collection<? extends T> collection, final Comparator<T> comparator)
	{
		_comparator = comparator;
		_backingList = new ArrayList<>(collection);
		Collections.sort(_backingList, _comparator);
		removeDuplicates();
	}
	
	
	public SortedListSet(int initialCapacity)
	{
		_comparator = null;
		_backingList = new ArrayList<>(initialCapacity);
	}
	
	public SortedListSet()
	{
		_comparator = null;
		_backingList = new ArrayList<>();
	}


	@Override
	public T get(int index)
	{
		return _backingList.get(index);
	}


	@Override
	public int size()
	{
		return _backingList.size();
	}


	@Override
	public boolean add(final T e)
	{
		int position = getPosition(e);
		
		if (position < 0) {
			/* determine real insertion position from binarySearch() result */
			position = -(position + 1);
			_backingList.add(position, e);
			return true;
		} else
			return false;
	}


	@Override
	public boolean addAll(Collection<? extends T> c)
	{
		boolean added = false;
		for (T item: c)
			added |= add(item);
		return added;
	}


	@Override
	public boolean remove(Object o)
	{
		return _backingList.remove(o);
	}


	@Override
	public boolean removeAll(Collection<?> c)
	{
		return _backingList.removeAll(c);
	}

	@Override
	public T remove(int index)
	{
		return _backingList.remove(index);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean contains(Object o)
	{		
		return getPosition((T)o) >= 0;
	}

		private void removeDuplicates()
	{
		final Iterator<T> iter = _backingList.iterator();
		T lastObj = null;		
		/* remove duplicates */
		while (iter.hasNext()) {
			final T obj = iter.next();
			if ((lastObj == obj) || ((obj != null) && obj.equals(lastObj)))
				iter.remove();
			lastObj = obj;
		}
	}

		private int getPosition(final T e)
	{
		if (_comparator == null)
			return Collections.binarySearch(_backingList, e);
		else
			return Collections.binarySearch(_backingList, e, _comparator);		
	}
}
