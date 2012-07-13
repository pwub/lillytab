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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections15.keyvalue.DefaultMapEntry;


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class AspectMapKeySet<K, V, M extends Map<K, V>>
	implements Set<K>
{
	private class Itr
		implements Iterator<K>
	{
		private final Iterator<K> _keyIterator;
		private K _current = null;

		public Itr()
		{
			_keyIterator = _aspectMap.getDecoratee().keySet().iterator();
		}

		public boolean hasNext()
		{
			return _keyIterator.hasNext();
		}

		public K next()
		{
			_current = _keyIterator.next();
			return _current;
		}

		public void remove()
		{
			Map.Entry<K, V> entry = new DefaultMapEntry<K, V>(_current, _aspectMap.get(_current));
			CollectionItemEvent<Entry<K, V>, Map<K, V>> ev = _aspectMap.notifyBeforeElementRemoved(_aspectMap, entry);
			_keyIterator.remove();
			_aspectMap.notifyAfterElementRemoved(ev);
		}
	}
	private final AspectMap<K, V, M> _aspectMap;
	private final Set<K> _keySet;

	protected AspectMapKeySet(final AspectMap<K, V, M> aspectMap)
	{
		_aspectMap = aspectMap;
		_keySet = aspectMap.getDecoratee().keySet();
	}

	public int size()
	{
		return _keySet.size();
	}

	public boolean isEmpty()
	{
		return _keySet.isEmpty();
	}

	public boolean contains(Object o)
	{
		return _keySet.contains(o);
	}

	public Iterator<K> iterator()
	{
		return new Itr();
	}

	public Object[] toArray()
	{
		return _keySet.toArray();
	}

	public <T> T[] toArray(final T[] a)
	{
		return _keySet.toArray(a);
	}

	public boolean add(final K e)
	{
		throw new UnsupportedOperationException("Cannot add to keyset");
	}

	public boolean remove(final Object o)
	{
		boolean wasRemoved = _aspectMap.containsKey(o);
		_aspectMap.remove(o);
		return wasRemoved;
	}

	public boolean containsAll(final Collection<?> c)
	{
		return _keySet.containsAll(c);
	}

	public boolean addAll(final Collection<? extends K> c)
	{
		throw new UnsupportedOperationException("Cannot add to keyset");
	}

	private boolean batchRemove(final Collection<?> c, final boolean retain)
	{
		for (K key: _keySet) {
			if (c.contains(key) != retain) {
				Map.Entry<K, V> entry = new DefaultMapEntry<K, V>(key, _aspectMap.get(key));
				_aspectMap.notifyBeforeElementRemoved(_aspectMap, entry);
			}
		}

		boolean wasRemoved = false;
		Iterator<K> iter = _aspectMap.getDecoratee().keySet().iterator();
		while (iter.hasNext()) {
			K key = iter.next();
			if (c.contains(key) != retain) {
				Map.Entry<K, V> entry = new DefaultMapEntry<K, V>(key, _aspectMap.get(key));
				iter.remove();
				wasRemoved = true;
				_aspectMap.notifyAfterElementRemoved(_aspectMap, entry);
			}
		}
		return wasRemoved;
	}

	public boolean retainAll(final Collection<?> c)
	{
		return batchRemove(c, true);
	}

	public boolean removeAll(final Collection<?> c)
	{
		return batchRemove(c, false);
	}

	public void clear()	
	{
		if (!isEmpty()) {
			CollectionEvent<Map.Entry<K, V>, Map<K, V>> ev = _aspectMap.notifyBeforeCollectionCleared(_aspectMap);
			_keySet.clear();
			_aspectMap.notifyAfterCollectionCleared(ev);
		}
	}

	@Override
	public String toString()
	{
		return _keySet.toString();
	}
}
