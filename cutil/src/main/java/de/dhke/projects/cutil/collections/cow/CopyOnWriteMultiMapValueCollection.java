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
import org.apache.commons.collections15.MultiMap;

/**
 *
 * @param <K>
 * @param <V>
 * @param <M>
 * <p/>
 * @author Peter Wullinger <java@dhke.de>
 */
public class CopyOnWriteMultiMapValueCollection<K, V>
	implements Collection<V> {
	/// </editor-fold>
	private final CopyOnWriteMultiMap<K, V> _cowMap;
	private final Object _key;
	private MultiMap<K, V> _lastBaseMap = null;
	private Collection<V> _cachedValueCollection = null;


	protected CopyOnWriteMultiMapValueCollection(final CopyOnWriteMultiMap<K, V> cowMap)
	{
		_key = null;
		_cowMap = cowMap;
	}


	protected CopyOnWriteMultiMapValueCollection(final CopyOnWriteMultiMap<K, V> cowMap, final Object key)
	{
		_key = key;
		_cowMap = cowMap;
	}


	@Override
	public int size()
	{
		if (_key == null)
			return getOriginalValueCollection().size();
		else
			return _cowMap.size(_key);
	}


	@Override
	public boolean isEmpty()
	{
		if (_key == null)
			return getOriginalValueCollection().isEmpty();
		else
			return (!_cowMap.containsKey(_key)) || (_cowMap.getDecoratee().get(_key).isEmpty());
	}


	@Override
	public boolean contains(final Object o)
	{
		if (_key == null)
			return _cowMap.containsValue(o);
		else
			return _cowMap.containsValue(_key, o);
	}


	@Override
	public Iterator<V> iterator()
	{
		if (_key == null)
			return new Itr();
		else
			return new Itr(_key);
	}


	@Override
	public Object[] toArray()
	{
		return getOriginalValueCollection().toArray();
	}


	@Override
	public <T> T[] toArray(final T[] a)
	{
		return getOriginalValueCollection().toArray(a);
	}


	@Override
	public  boolean add(final V key)
	{
		throw new UnsupportedOperationException("Cannot add to value set");
	}


	@Override
	public boolean remove(final Object o)
	{
		_cowMap.copy();
		return getOriginalValueCollection().remove(o);
	}


	@Override
	public boolean containsAll(final Collection<?> c)
	{
		return getOriginalValueCollection().containsAll(c);
	}


	@Override
	public boolean addAll(final Collection<? extends V> c)
	{
		throw new UnsupportedOperationException("Cannot add to value set");
	}


	@Override
	public boolean retainAll(final Collection<?> c)
	{
		_cowMap.copy();
		return getOriginalValueCollection().retainAll(c);
	}


	@Override
	public boolean removeAll(final Collection<?> c)
	{
		_cowMap.copy();
		return getOriginalValueCollection().removeAll(c);
	}


	@Override
	public void clear()
	{
		getOriginalValueCollection().clear();
	}


	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		return getOriginalValueCollection().equals(obj);
	}


	@Override
	public int hashCode()
	{
		int hCode = 0;
		for (V value : this)
			hCode += value.hashCode();
		return hCode;
	}


	@Override
	public String toString()
	{
		return getOriginalValueCollection().toString();
	}


		private Collection<V> getOriginalValueCollection()
	{
		/* cache value set to avoid re-fetch */
		if (_lastBaseMap != _cowMap.getDecoratee()) {
			_lastBaseMap = _cowMap.getDecoratee();
			if (_key == null)
				_cachedValueCollection = _lastBaseMap.values();
			else
				_cachedValueCollection = _lastBaseMap.get(_key);
		}
		assert _cachedValueCollection != null;
		return _cachedValueCollection;
	}
	/// <editor-fold defaultstate="collapsed" desc="class Itr">

	private class Itr
		implements Iterator<V> {

		private final Iterator<V> _valueIterator;
		private final boolean _collectionAlreadyCopied;


		Itr()
		{
			_collectionAlreadyCopied = _cowMap.isWasCopied();
			_valueIterator = _cowMap.getDecoratee().values().iterator();
		}


		Itr(final Object key)
		{
			_collectionAlreadyCopied = _cowMap.isWasCopied();
			_valueIterator = _cowMap.getDecoratee().iterator(key);
		}


		@Override
		public boolean hasNext()
		{
			return _valueIterator.hasNext();
		}


		@Override
		public V next()
		{
			return _valueIterator.next();
		}


		@Override
		public void remove()
		{
			/**
			 * I cannot think of a proper way to fully support this method.
			 * If a COW list has not been copied yet, a remove would chang thee underlying collection.
			 * In this case, we would have to create a new iterator at exactly the same
			 * position for the new collection. This works for lists but not for collections.
			 *
			 */
			if (_collectionAlreadyCopied)
				_valueIterator.remove();
			else
				throw new UnsupportedOperationException("Cannot remove from untouched CopyOnWriteMap via iterator.");
		}
	}
}
