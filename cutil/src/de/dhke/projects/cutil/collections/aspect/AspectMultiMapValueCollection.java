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
package de.dhke.projects.cutil.collections.aspect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.keyvalue.DefaultMapEntry;


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class AspectMultiMapValueCollection<K, V, M extends MultiMap<K, V>>
	implements Collection<V>
{
	private class Itr
		implements Iterator<V>
	{
		private final Iterator<K> _keyIterator;
		private Iterator<V> _valueIterator;
		private V _current;

		public Itr()
		{
			if (_key == null) {
				_keyIterator = _aspectMap.keySet().iterator();
			} else {
				_keyIterator = null;
				assert _aspectMap.getDecoratee() != null;
				_valueIterator = _aspectMap.getDecoratee().iterator(_key);
			}
		}

		private boolean updateIterator()
		{
			if ((_valueIterator == null) || (!_valueIterator.hasNext())) {
				while ((_keyIterator != null) && _keyIterator.hasNext() && ((_valueIterator == null) || (!_valueIterator.
					hasNext()))) {
					_key = _keyIterator.next();
					_valueIterator = _aspectMap.getDecoratee().iterator(_key);
				}
				if ((_valueIterator != null) && (!_valueIterator.hasNext()))
					_valueIterator = null;
				return _valueIterator != null;
			} else {
				return true;
			}
		}

		public boolean hasNext()
		{
			return updateIterator();
		}

		public V next()
		{
			if (!updateIterator())
				throw new NoSuchElementException();
			else {
				_current = _valueIterator.next();
				return _current;
			}
		}

		public void remove()
		{
			Map.Entry<K, V> entry = new DefaultMapEntry<K, V>(_key, _current);
			CollectionItemEvent<Map.Entry<K, V>, MultiMap<K, V>> ev = _aspectMap.notifyBeforeElementRemoved(_aspectMap,
																											entry);
			_valueIterator.remove();
			_aspectMap.notifyAfterElementRemoved(ev);
		}
	}
	private K _key;
	private final AspectMultiMap<K, V, M> _aspectMap;
	private Collection<V> _cachedValueCollection = null;

	public Collection<V> getOriginalValueCollection()
	{
		return _cachedValueCollection;
	}

	protected AspectMultiMapValueCollection(final AspectMultiMap<K, V, M> aspectMap)
	{
		_aspectMap = aspectMap;
		_key = null;
		_cachedValueCollection = aspectMap.getDecoratee().values();
	}

	protected AspectMultiMapValueCollection(final AspectMultiMap<K, V, M> aspectMap, K key)
	{
		_aspectMap = aspectMap;
		_key = key;
		_cachedValueCollection = _aspectMap.getDecoratee().get(key);
	}

	public int size()
	{
		return getOriginalValueCollection().size();
	}

	public boolean isEmpty()
	{
		if (getOriginalValueCollection() == null)
			System.err.println("what?");
		return getOriginalValueCollection().isEmpty();
	}

	public boolean contains(Object o)
	{
		return getOriginalValueCollection().contains(o);
	}

	public Iterator<V> iterator()
	{
		return new Itr();
	}

	public Object[] toArray()
	{
		return getOriginalValueCollection().toArray();
	}

	public <T> T[] toArray(final T[] a)
	{
		return getOriginalValueCollection().toArray(a);
	}

	public boolean add(final V key)
	{
		/* XXX - this could be supported, if _key != null */
		throw new UnsupportedOperationException("Cannot add to value collection");
	}

	public boolean remove(final Object o)
	{
		/**
		 * This potentially changes the behaviour of the underlying
		 * collection, as the first key-value-pair with a matching
		 * value will be removed.
		 *
		 * Also, searching through the collection may also
		 * be slower as the remove()-implementation in the backend map.
		 *
		 * Still, this seems to be the only proper way to support to remove
		 * items from the value collection.
		 */
		Iterator<V> iter = iterator();
		while (iter.hasNext()) {
			V value = iter.next();
			if (value.equals(o)) {
				iter.remove();
				return true;
			}
		}
		return false;
	}

	public boolean containsAll(final Collection<?> c)
	{
		return getOriginalValueCollection().containsAll(c);
	}

	public boolean addAll(final Collection<? extends V> c)
	{
		/* XXX - this could be supported, if _key != null */
		throw new UnsupportedOperationException("Cannot add to value collection");
	}

	/*
	private static boolean containsAny(Collection<?> collection, Collection<?> testCollection)
	{
	for (Object o: testCollection)
	if (collection.contains(o))
	return true;
	return false;
	}
	 */
	private boolean batchRemove(final Collection<?> c, final boolean retain)
	{
		/**
		 * This requires more memory than necessary, but is more efficent in the
		 * typical case.
		 * 
		 * We collect the keys with matching values and
		 * trigger the before-triggers first.
		 *
		 * Then we re-iterator over the capturated
		 * triggered, copy their value sets and
		 * remove. 
		 *
		 * If k is the number of keys and m is the maximum
		 * size of a value set for any key, the algorithm
		 * is O(2 km�) time and O(km) space.
		 **/
		Collection<K> keys = new ArrayList<K>();
		for (Map.Entry<K, Collection<V>> entry : _aspectMap.getDecoratee().entrySet()) {
			boolean haveValue = false;
			for (V value : entry.getValue()) {
				if (c.contains(value) != retain) {
					haveValue = true;
					Map.Entry<K, V> evEntry = new DefaultMapEntry<K, V>(entry.getKey(), value);
					_aspectMap.notifyBeforeElementRemoved(_aspectMap, evEntry);
				}
			}
			if (haveValue)
				keys.add(entry.getKey());
		}
		boolean wasRemoved = false;
		for (K key : keys) {
			/* create a copy, as we will modify the collection */
			Collection<V> values = new ArrayList<V>(_aspectMap.getDecoratee().get(key));
			for (V value : values) {
				if (c.contains(value) != retain) {
					wasRemoved = true;
					_aspectMap.getDecoratee().remove(key, value);
					Map.Entry<K, V> evEntry = new DefaultMapEntry<K, V>(key, value);
					_aspectMap.notifyAfterElementRemoved(_aspectMap, evEntry);
				}
			}
		}
		return wasRemoved;
	}

	public boolean removeAll(final Collection<?> c)
	{
		return batchRemove(c, false);
	}

	public boolean retainAll(final Collection<?> c)
	{
		return batchRemove(c, true);
	}

	public void clear()
	{
		if (!isEmpty()) {
			if (_key == null) {
				/* no key, delete the complete collection */
				CollectionEvent<Map.Entry<K, V>, MultiMap<K, V>> ev = _aspectMap.notifyBeforeCollectionCleared(_aspectMap);
				getOriginalValueCollection().clear();
				_aspectMap.notifyAfterCollectionCleared(ev);
			} else {
				/* key, delete items one by one */
				Iterator<V> iter = iterator();
				while (iter.hasNext()) {
					iter.next();
					iter.remove();
				}
			}
		}
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
}
