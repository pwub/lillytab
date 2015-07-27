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

import de.dhke.projects.cutil.collections.CollectionUtil;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;


/**
 * 
 * An implementation of a set type that
 * uses an array for sets of small size (default: up to three)
 * and defaults back to using a {@link HashSet} for
 * larger arrays.
 * 
 *
 * @param <T> The set element type.
 * @author Peter Wullinger <java@dhke.de>
 */
public class Flat3Set<T>
	implements Set<T>
{
	private static final int MAX_ARRAY_SIZE = 3;
	private Object[] _itemArray;
	private Set<T> _backSet;

	public  Flat3Set(final Collection<? extends T> coll)
	{
		if (coll.size() > MAX_ARRAY_SIZE) {
			_backSet = new HashSet<>(coll);
			_itemArray = null;
		} else {
			_backSet = null;
			_itemArray = new Object[coll.size()];
			coll.toArray(_itemArray);
		}
	}

	@SafeVarargs
	public Flat3Set(final T... items)
	{
		this(Arrays.asList(items));
	}

		public Flat3Set()
	{
		this(0);
	}

	public Flat3Set(final int initialCapacity)
	{
		if (initialCapacity > MAX_ARRAY_SIZE) {
			_backSet = new HashSet<>(initialCapacity);
			_itemArray = null;
		} else {
			_backSet = null;
			_itemArray = new Object[0];
		}
	}

	@Override
	public int size()
	{
		if (_itemArray != null)
			return _itemArray.length;
		else
			return _backSet.size();
	}

	@Override
	public boolean isEmpty()
	{
		return ((_itemArray != null) && (_itemArray.length == 0)) || (_backSet != null && _backSet.isEmpty());
	}

	@Override
	public boolean contains(Object o)
	{
		if (_itemArray != null) {
			for (Object item : _itemArray) {
				if (o.equals(item))
					return true;
			}
			return false;
		} else
			return _backSet.contains(o);
	}

	@Override
	public Iterator<T> iterator()
	{
		if (_itemArray != null)
			return new ArrayItr();
		else
			return _backSet.iterator();
	}

	@Override
	public Object[] toArray()
	{
		if (_itemArray != null)
			return Arrays.copyOf(_itemArray, _itemArray.length);
		else
			return _backSet.toArray();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a)
	{
		if (_itemArray != null) {
			if (a.length >= _itemArray.length) {
				for (int i = 0; i < _itemArray.length; ++i)
					a[i] = (T) _itemArray[i];
				return a;
			} else
				return (T[]) Arrays.copyOf(_itemArray, _itemArray.length);
		} else
			return _backSet.toArray(a);
	}

	@Override
	@SuppressWarnings("unchecked")
	public  boolean add(T e)
	{
		if (contains(e))
			return false;
		else if ((_itemArray != null) && (_itemArray.length < MAX_ARRAY_SIZE)) {
			_itemArray = Arrays.copyOf(_itemArray, _itemArray.length + 1);
			_itemArray[_itemArray.length - 1] = e;
			return true;
		} else {
			if (_backSet == null) {
				_backSet = new HashSet<>(_itemArray.length + 1);
				for (Object o : _itemArray)
					_backSet.add((T) o);
				_itemArray = null;
			}
			return _backSet.add(e);
		}
	}

	@Override
	public boolean remove(Object o)
	{
		if (_itemArray != null) {
			int pos = -1;
			for (int i = 0; i < _itemArray.length; ++i) {
				if (_itemArray[i].equals(o)) {
					pos = i;
					break;
				}
			}
			if (pos == -1)
				return false;
			else {
				_itemArray = arrayRemove(_itemArray, pos);
				return true;
			}
		} else
			return _backSet.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		for (Object o : c) {
			if (!contains(o))
				return false;
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends T> c)
	{
		boolean added = false;
		for (T item : c)
			added |= add(item);
		return added;
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		boolean removed = false;
		Iterator<T> thisIter = iterator();
		while (thisIter.hasNext()) {
			if (!c.contains(thisIter.next())) {
				removed = true;
				thisIter.remove();
			}
		}
		return removed;
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		boolean removed = false;
		for (Object item : c) {
			removed |= remove(item);
		}
		return removed;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void clear()
	{
		_itemArray = (T[]) new Object[0];
		_backSet.clear();
		_backSet = null;
	}

	@Override
	public String toString()
	{
		return CollectionUtil.deepToString(this);
	}

		private static <T> T[] arrayRemove(final T[] array, int removePos)
	{
		@SuppressWarnings("unchecked")
		final T[] newArray = (T[]) new Object[array.length - 1];
		int j = 0;
		for (int i = 0; i < array.length; ++i) {
			if (i != removePos) {
				newArray[j] = array[i];
				++j;
			}
		}
		return newArray;
	}	


	class ArrayItr
		implements Iterator<T>
	{
		int pos;

		ArrayItr()
		{
			pos = -1;
		}

		@Override
		public boolean hasNext()
		{
			return pos < _itemArray.length - 1;
		}

		@Override
		@SuppressWarnings("unchecked")
		public T next()
		{
			if (_itemArray == null)
				throw new ConcurrentModificationException();
			else {
				++pos;
				if (pos < _itemArray.length)
					return (T) _itemArray[pos];
				else
					throw new NoSuchElementException();
			}
		}

		@Override
		public void remove()
		{
			if (pos >= 0) {
				arrayRemove(_itemArray, pos);
			} else
				throw new IllegalStateException("Iterator before first element");
		}
	}
}
