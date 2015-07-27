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
import java.util.Set;
import org.apache.commons.collections15.MultiMap;


/**
 * This class is almost literally the same as CopyOnWriteMapKeySet
 *
 **/
/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
class CopyOnWriteMultiMapKeySet<K, V>
	implements Set<K>
{
	
	private CopyOnWriteMultiMap<K, V> _cowMap;
	private MultiMap<K, V> _lastBaseMap = null;
	private Set<K> _cachedKeySet = null;

	protected CopyOnWriteMultiMapKeySet(final CopyOnWriteMultiMap<K, V> cowMap)
	{
		_cowMap = cowMap;
	}

	@Override
	public int size()
	{
		return _cowMap.size();
	}

	@Override
	public boolean isEmpty()
	{
		return _cowMap.isEmpty();
	}

	@Override
	public boolean contains(final Object o)
	{
		return _cowMap.containsKey(o);
	}

	@Override
	public Iterator<K> iterator()
	{
		return new Itr();
	}

	@Override
	public Object[] toArray()
	{
		return getOriginalKeySet().toArray();
	}

	@Override
	public <T> T[] toArray(final T[] a)
	{
		return getOriginalKeySet().toArray(a);
	}

	@Override
	public  boolean add(K key)
	{
		throw new UnsupportedOperationException("Cannot add to key set");
	}

	@Override
	public boolean remove(final Object o)
	{
		boolean willRemove = _cowMap.containsKey(o);
		_cowMap.remove(o);
		return willRemove;
	}

	@Override
	public boolean containsAll(final Collection<?> c)
	{
		return getOriginalKeySet().containsAll(c);
	}

	@Override
	public boolean addAll(final Collection<? extends K> c)
	{
		throw new UnsupportedOperationException("Cannot add to key set");
	}

	@Override
	public boolean retainAll(final Collection<?> c)
	{
		_cowMap.copy();
		return getOriginalKeySet().retainAll(c);
	}

	@Override
	public boolean removeAll(final Collection<?> c)
	{
		_cowMap.copy();
		return getOriginalKeySet().removeAll(c);
	}

	@Override
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
	/// <editor-fold defaultstate="collapsed" desc="class Itr">
	private class Itr
		implements Iterator<K>
	{
		private Iterator<K> _keyIterator;
		/**
		 * the iteration position of this iterator.
		 * 
		 * if _iterPos == -2, we operate on an already copied COWmap and
		 * simple iterate.
		 * 
		 * if _iterPos >= -1, we operate 
		 */
		private int _iterPos;

		Itr()
		{
			if (_cowMap.isWasCopied()) {
				_iterPos = -2;
			} else
				_iterPos = -1;
			_keyIterator = _cowMap.getDecoratee().keySet().iterator();
		}

		@Override
		public boolean hasNext()
		{
			return _keyIterator.hasNext();
		}

		@Override
		public K next()
		{
			final K key = _keyIterator.next();
			if (_iterPos >= -1)
				++_iterPos;
			return key;
		}

		@Override
		public void remove()
		{
			/**
			 * I cannot think of a proper way to fully support this method.
			 * If a COW list has not been copied yet, a remove would change thee underlying collection.
			 * In this case, we would have to create a new iterator at exactly the same
			 * position for the new collection. This works for lists but not for collections.
			 **/
			if (_iterPos == -2)
				_keyIterator.remove();
			else {
				assert !_cowMap.isWasCopied();
				_cowMap.copy();
				assert _cowMap.isWasCopied();
				_keyIterator = _cowMap.getDecoratee().keySet().iterator();
				for (int i = -1; i < _iterPos; ++i)
					_keyIterator.next();
				_iterPos = -2;
				_keyIterator.remove();
			}
		}
	}
	/// </editor-fold>
}
