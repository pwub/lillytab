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
package de.dhke.projects.cutil.collections.cow;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections15.keyvalue.DefaultMapEntry;

class CopyOnWriteMapEntrySet<K, V>
	implements Set<Entry<K, V>> {
	/// </editor-fold>
	private final CopyOnWriteMap<K, V> _cowMap;
	private Map<K, V> _lastBaseMap = null;
	private Set<Entry<K, V>> _cachedEntrySet = null;


	protected CopyOnWriteMapEntrySet(final CopyOnWriteMap<K, V> cowMap)
	{
		_cowMap = cowMap;
	}


	@Override
	public int size()
	{
		return getOriginalEntrySet().size();
	}


	@Override
	public boolean isEmpty()
	{
		return getOriginalEntrySet().isEmpty();
	}


	@Override
	public boolean contains(final Object o)
	{
		if (o instanceof Map.Entry) {
			Map.Entry<?, ?> entry = (Map.Entry) o;
			if (_cowMap.containsKey(entry.getKey()))
				return _cowMap.get(entry.getKey()).equals(entry.getValue());
			else
				return false;
		} else
			return false;
	}


	@Override
	public Iterator<Entry<K, V>> iterator()
	{
		return new Itr();
	}


	@Override
	public Object[] toArray()
	{
		return getOriginalEntrySet().toArray();
	}


	@Override
	public <T> T[] toArray(final T[] a)
	{
		return getOriginalEntrySet().toArray(a);
	}


	@Override
	public boolean add(final Entry<K, V> e)
	{
		_cowMap.put(e.getKey(), e.getValue());
		return true;
	}


	@Override
	public boolean remove(final Object o)
	{
		if (o instanceof Entry) {
			@SuppressWarnings(value = "unchecked")
			Entry<K, V> entry = (Entry<K, V>) o;
			if (_cowMap.containsKey(entry.getKey())) {
				_cowMap.remove(entry.getKey());
				return true;
			} else
				return false;
		} else
			return false;
	}


	@Override
	public boolean containsAll(final Collection<?> c)
	{
		return getOriginalEntrySet().containsAll(c);
	}


	@Override
	public boolean addAll(final Collection<? extends Entry<K, V>> c)
	{
		boolean added = false;
		for (Entry<K, V> entry : c) {
			if (add(entry))
				added = true;
		}
		return added;
	}


	@Override
	public boolean retainAll(final Collection<?> c)
	{
		_cowMap.copy();
		return getOriginalEntrySet().retainAll(c);
	}


	@Override
	public boolean removeAll(final Collection<?> c)
	{
		_cowMap.copy();
		return getOriginalEntrySet().removeAll(c);
	}


	@Override
	public void clear()
	{
		_cowMap.copy();
		getOriginalEntrySet().clear();
	}


	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		return _cowMap.equals(obj);
	}


	@Override
	public int hashCode()
	{
		int hCode = 0;
		for (Entry<K, V> entry : this)
			hCode += entry.hashCode();
		return hCode;
	}


	private Set<Entry<K, V>> getOriginalEntrySet()
	{
		/* cache entrty set to avoid re-fetch */
		if (_lastBaseMap != _cowMap.getDecoratee()) {
			_lastBaseMap = _cowMap.getDecoratee();
			_cachedEntrySet = _lastBaseMap.entrySet();
		}
		assert _cachedEntrySet != null;
		return _cachedEntrySet;
	}
	/// <editor-fold defaultstate="collapsed" desc="class Itr">

	private class Itr
		implements Iterator<Map.Entry<K, V>> {

		private final Iterator<K> _keyIterator;
		private final boolean _collectionAlreadyCopied;


		Itr()
		{
			super();
			_collectionAlreadyCopied = _cowMap.isWasCopied();
			_keyIterator = _cowMap.getDecoratee().keySet().iterator();
		}


		@Override
		public boolean hasNext()
		{
			return _keyIterator.hasNext();
		}


		@Override
		public Entry<K, V> next()
		{
			K nextKey = _keyIterator.next();
			V nextValue = _cowMap.get(nextKey);
			return new DefaultMapEntry<>(nextKey, nextValue);
		}


		@Override
		public void remove()
		{
			/**
			 * I cannot think of a proper way to fully support this method. If a COW list has not been copied yet, a
			 * remove would change the underlying collection. In this case, we would have to create a new iterator at
			 * exactly the same position for the new collection. This works for lists but not for collections.
			 *
			 */
			if (_collectionAlreadyCopied)
				_keyIterator.remove();
			else
				throw new UnsupportedOperationException("Cannot remove from untouched CopyOnWriteMap via iterator.");
		}
	}
}
