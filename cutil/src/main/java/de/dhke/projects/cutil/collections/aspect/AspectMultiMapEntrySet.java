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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
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
public class AspectMultiMapEntrySet<K, V, M extends MultiMap<K, V>>
	implements Set<Map.Entry<K, Collection<V>>>
{

	private final AspectMultiMap<K, V, M> _aspectMap;
	private final Set<Map.Entry<K, Collection<V>>> _entrySet;
	
	protected AspectMultiMapEntrySet(final AspectMultiMap<K, V, M> aspectMap)
	{
		_aspectMap = aspectMap;
		_entrySet = _aspectMap.getDecoratee().entrySet();
	}

	@Override
	public int size()
	{
		return _entrySet.size();
	}

	@Override
	public boolean isEmpty()
	{
		return _entrySet.isEmpty();
	}

	@Override
	public boolean contains(final Object o)
	{
		return _entrySet.contains(o);
	}

	@Override
	public Iterator<Entry<K, Collection<V>>> iterator()
	{
		return new Itr();
	}

	@Override
	public Object[] toArray()
	{
		/* XXX - this may be a loophole */
		return _entrySet.toArray();
	}

	@Override
	public <T> T[] toArray(final T[] a)
	{
		/* XXX - this may be a loophole */
		return _entrySet.toArray(a);
	}

	@Override
	public boolean add(final Entry<K, Collection<V>> e)
	{
		throw new UnsupportedOperationException("Cannot add to entry set");
	}

	@Override
	public boolean remove(final Object o)
	{
		if (o instanceof Map.Entry) {
			final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)o;
			if (_aspectMap.containsKey(entry.getKey())) {
				Collection<V> values = _aspectMap.get(entry.getKey());
				if (values.equals(entry.getValue())) {
					_aspectMap.remove(entry.getKey());
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean containsAll(final Collection<?> c)
	{
		return _entrySet.containsAll(c);
	}

	@Override
	public boolean addAll(final Collection<? extends Entry<K, Collection<V>>> c)
	{
		throw new UnsupportedOperationException("Cannot add to entry set.");
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
		_aspectMap.clear();
	}

		private boolean batchRemove(Collection<?> c, boolean retain)
	{
		for (Map.Entry<K, Collection<V>> entry: _entrySet) {
			if (c.contains(entry) != retain) {
				for (V value : entry.getValue()) {
					final Map.Entry<K, V> evEntry = new DefaultMapEntry<>(entry.getKey(), value);
					_aspectMap.notifyBeforeElementRemoved( _aspectMap, evEntry);
				}
			}
		}

		final Iterator<Map.Entry<K, Collection<V>>> iter = _entrySet.iterator();
		boolean wasRemoved = false;
		while (iter.hasNext()) {
			Map.Entry<K, Collection<V>> entry = iter.next();
			if (c.contains(entry) != retain) {
				iter.remove();
				wasRemoved = true;
				for (V value : entry.getValue()) {
					Map.Entry<K, V> evEntry = new DefaultMapEntry<>(entry.getKey(), value);
					_aspectMap.notifyAfterElementRemoved(_aspectMap, evEntry);
				}
			}
		}
		return wasRemoved;
	}
	private class Itr
		implements Iterator<Map.Entry<K, Collection<V>>>
	{
		Iterator<K> _keyIterator;
		private Map.Entry<K, Collection<V>> _current = null;

		protected Itr()
		{
			_keyIterator = _aspectMap.keySet().iterator();
		}

		@Override
		public boolean hasNext()
		{
			return _keyIterator.hasNext();
		}

		@Override
		public Entry<K, Collection<V>> next()
		{
			K key = _keyIterator.next();
			Collection<V> value = _aspectMap.get(key);
			_current = new DefaultMapEntry<>(key, value);
			return _current;
		}

		@Override
		public void remove()
		{
			_keyIterator.remove();
		}
	}
}
