/**
 * (c) 2009-2012 Peter Wullinger
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

import org.apache.commons.collections15.MultiMap;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;


/**
 * This class is almost literally the same as CopyOnWriteMapKeySet
 *
 **/
/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
class CopyOnWriteMultiMapKeySet<K, V, M extends MultiMap<K, V>>
	implements Set<K>
{
	private class Itr
		implements Iterator<K>
	{
		private final Iterator<K> _keyIterator;
		private final boolean _collectionAlreadyCopied;

		public Itr()
		{
			_collectionAlreadyCopied = _cowMap.isWasCopied();
			_keyIterator = _cowMap.getDecoratee().keySet().iterator();
		}

		public boolean hasNext()
		{
			return _keyIterator.hasNext();
		}

		public K next()
		{
			return _keyIterator.next();
		}

		public void remove()
		{
			/**
			 * I cannot think of a proper way to fully support this method.
			 * If a COW list has not been copied yet, a remove would chang thee underlying collection.
			 * In this case, we would have to create a new iterator at exactly the same
			 * position for the new collection. This works for lists but not for collections.
			 **/
			if (_collectionAlreadyCopied)
				_keyIterator.remove();
			else
				throw new UnsupportedOperationException("Cannot remove from untouched CopyOnWriteMap via iterator.");
		}
	}
	private GenericCopyOnWriteMultiMap<K, V, M> _cowMap;
	private MultiMap<K, V> _lastBaseMap = null;
	private Set<K> _cachedKeySet = null;

	protected CopyOnWriteMultiMapKeySet(final GenericCopyOnWriteMultiMap<K, V, M> cowMap)
	{
		_cowMap = cowMap;
	}

	private Set<K> getOriginalKeySet()
	{
		/* cache value set to avoid re-fetch */
		if (_lastBaseMap != _cowMap.getDecoratee()) {
			_lastBaseMap = _cowMap.getDecoratee();
			_cachedKeySet = _lastBaseMap.keySet();
		}
		assert _cachedKeySet != null;
		return _cachedKeySet;
	}

	public int size()
	{
		return _cowMap.size();
	}

	public boolean isEmpty()
	{
		return _cowMap.isEmpty();
	}

	public boolean contains(final Object o)
	{
		return _cowMap.containsKey(o);
	}

	public Iterator<K> iterator()
	{
		return new Itr();
	}

	public Object[] toArray()
	{
		return getOriginalKeySet().toArray();
	}

	public <T> T[] toArray(final T[] a)
	{
		return getOriginalKeySet().toArray(a);
	}

	public boolean add(K key)
	{
		throw new UnsupportedOperationException("Cannot add to key set");
	}

	public boolean remove(final Object o)
	{
		boolean willRemove = _cowMap.containsKey(o);
		_cowMap.remove(o);
		return willRemove;
	}

	public boolean containsAll(final Collection<?> c)
	{
		return getOriginalKeySet().containsAll(c);
	}

	public boolean addAll(final Collection<? extends K> c)
	{
		throw new UnsupportedOperationException("Cannot add to key set");
	}

	public boolean retainAll(final Collection<?> c)
	{
		_cowMap.copy();
		return getOriginalKeySet().retainAll(c);
	}

	public boolean removeAll(final Collection<?> c)
	{
		_cowMap.copy();
		return getOriginalKeySet().removeAll(c);
	}

	public void clear()
	{
		_cowMap.copy();
		_cowMap.getDecoratee().clear();
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		return getOriginalKeySet().equals(obj);
	}

	@Override
	public int hashCode()
	{
		int hCode = 0;
		for (K key : this)
			hCode += key.hashCode();
		return hCode;
	}
}
