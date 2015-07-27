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
package de.dhke.projects.cutil.collections.aspect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.keyvalue.DefaultMapEntry;


/**
 *
 * @param <K> 
 * @param <V> 
 * @param <M> 
 * @author Peter Wullinger <java@dhke.de>
 */
public class AspectMultiMapKeySet<K, V, M extends MultiMap<K, V>>
	implements Set<K>
{
	private final AspectMultiMap<K, V, M> _aspectMap;
	private final Set<K> _keySet;

	public AspectMultiMapKeySet(final AspectMultiMap<K, V, M> aspectMap)
	{
		_aspectMap = aspectMap;
		_keySet = _aspectMap.getDecoratee().keySet();
	}

	@Override
	public int size()
	{
		return _keySet.size();
	}

	@Override
	public boolean isEmpty()
	{
		return _keySet.isEmpty();
	}

	@Override
	public boolean contains(final Object o)
	{
		return _keySet.contains(o);
	}

	@Override
	public Iterator<K> iterator()
	{
		return new Itr();
	}

	@Override
	public Object[] toArray()
	{
		return _keySet.toArray();
	}

	@Override
	public <T> T[] toArray(final T[] a)
	{
		return _keySet.toArray(a);
	}

	@Override
	public boolean add(final K e)
	{
		throw new UnsupportedOperationException("Cannot add to key set");
	}

	@Override
	public boolean remove(final Object o)
	{
		if (_aspectMap.containsKey(o)) {
			/* dispatch to map */
			_aspectMap.remove(o);
			return true;
		} else
			return false;
	}

	@Override
	public boolean containsAll(final Collection<?> c)
	{
		return _keySet.containsAll(c);
	}

	@Override
	public boolean addAll(final Collection<? extends K> c)
	{
		throw new UnsupportedOperationException("Cannot add to key set");
	}

	@Override
	public boolean retainAll(final Collection<?> c)
	{
		return batchRemove(c, true);
	}

	@Override
	public boolean removeAll(final Collection<?> c)
	{
		return batchRemove(c, false);
	}

	@Override
	public void clear()
	{
		if (!isEmpty()) {
			/* assumption: clearing the keyset also clears the map */
			_aspectMap.notifyBeforeCollectionCleared(_aspectMap);
			_keySet.clear();
			_aspectMap.notifyAfterCollectionCleared(_aspectMap);
		}
	}

		private boolean batchRemove(final Collection<?> c, final boolean retain)
	{
		for (K key : _keySet) {
			if (c.contains(key) != retain) {
				Collection<V> values = _aspectMap.get(key);
				for (V value : values) {
					Map.Entry<K, V> entry = new DefaultMapEntry<>(key, value);
					_aspectMap.notifyBeforeElementRemoved(_aspectMap, entry);
				}
			}
		}

		Iterator<K> iter = _keySet.iterator();
		boolean wasRemoved = false;
		while (iter.hasNext()) {
			K key = iter.next();
			if (c.contains(key) != retain) {
				/* use a copy, as the original collection may be modified */
				Collection<V> values = new ArrayList<>(_aspectMap.get(key));
				iter.remove();
				wasRemoved = true;
				for (V value : values) {
					Map.Entry<K, V> entry = new DefaultMapEntry<>(key, value);
					_aspectMap.notifyAfterElementRemoved(_aspectMap, entry);
				}
			}
		}
		return wasRemoved;
	}
	private class Itr
		implements Iterator<K>
	{
		private final Iterator<K> _keyIter;
		private K _current;

		Itr()
		{
			_keyIter = _keySet.iterator();
			_current = null;
		}

		@Override
		public boolean hasNext()
		{
			return _keyIter.hasNext();
		}

		@Override
		public K next()
		{
			_current = _keyIter.next();
			return _current;
		}

		@Override
		public void remove()
		{
			/* we operate on copy in the case the MultiMap returns the real value collection */
			Collection<V> values = new ArrayList<>(_aspectMap.getDecoratee().get(_current));
			for (V value : values) {
				Map.Entry<K, V> entry = new DefaultMapEntry<>(_current, value);
				_aspectMap.notifyBeforeElementRemoved(_aspectMap, entry);
			}
			_keyIter.remove();
			for (V value : values) {
				Map.Entry<K, V> entry = new DefaultMapEntry<>(_current, value);
				_aspectMap.notifyAfterElementRemoved(_aspectMap, entry);
			}
		}
	}
}
