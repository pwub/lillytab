/**
 * (c) 2009-2013 Peter Wullinger
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


/**
 *
 * @param <K>
 * @param <V>
 * @param <M>
 * @author Peter Wullinger <java@dhke.de>
 */
public class CopyOnWriteMapValueCollection<K, V, M extends Map<K, V>>
	implements Collection<V>
{
	/// <editor-fold defaultstate="collapsed" desc="class Itr">
	private class Itr
		implements Iterator<V>
	{
		private final Iterator<V> _valueIterator;
		private final boolean _collectionAlreadyCopied;

		public Itr()
		{
			_collectionAlreadyCopied = _cowMap.isWasCopied();
			_valueIterator = _cowMap.getDecoratee().values().iterator();
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
			 * If a COW list has not been copied yet, a remove would change the underlying collection.
			 * In this case, we would have to create a new iterator at exactly the same
			 * position for the new collection. This works for lists but not for collections.
			 **/
			if (_collectionAlreadyCopied)
				_valueIterator.remove();
			else
				throw new UnsupportedOperationException("Cannot remove from untouched CopyOnWriteMap via iterator.");
		}
	}
	/// </editor-fold>
	
	private final GenericCopyOnWriteMap<K, V, M> _cowMap;
	private Map<K, V> _lastBaseMap = null;
	private Collection<V> _cachedValueCollection = null;

	protected CopyOnWriteMapValueCollection(final GenericCopyOnWriteMap<K, V, M> cowMap)
	{
		_cowMap = cowMap;
	}

	private Collection<V> getOriginalValueCollection()
	{
		/* cache value set to avoid re-fetch */
		if (_lastBaseMap != _cowMap.getDecoratee()) {
			_lastBaseMap = _cowMap.getDecoratee();
			_cachedValueCollection = _lastBaseMap.values();
		}
		assert _cachedValueCollection != null;
		return _cachedValueCollection;
	}

	@Override
	public int size()
	{
		return getOriginalValueCollection().size();
	}

	@Override
	public boolean isEmpty()
	{
		return getOriginalValueCollection().isEmpty();
	}

	@Override
	public boolean contains(final Object o)
	{
		return _cowMap.containsValue(o);
	}

	@Override
	public Iterator<V> iterator()
	{
		return new Itr();
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
	public boolean add(final V key)
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
		_cowMap.copy();
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
}
