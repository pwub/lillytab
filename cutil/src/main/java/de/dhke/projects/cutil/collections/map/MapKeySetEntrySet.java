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
package de.dhke.projects.cutil.collections.map;

import java.util.*;
import java.util.Map.Entry;
import org.apache.commons.collections15.keyvalue.DefaultMapEntry;

/**
 * 
 * An implementation of a {@link Map#entrySet()}
 * making use of the {@link Map#keySet() } as well
 * as {@link Map#put(java.lang.Object, java.lang.Object) }
 * and {@link Map#remove(java.lang.Object) } for its implementation.
 * <p />
 * This is reversed from {@link AbstractMap} in {@link java.util},
 * which requires and {@link  Map#entrySet()} entryset
 * implementation.
 * 
 * 
 * @author Peter Wullinger <java@dhke.de>
 */
public class MapKeySetEntrySet<K, V>
	implements Set<Map.Entry<K, V>>
{
	private final Map<K, V> _baseMap;
	
	protected MapKeySetEntrySet(final Map<K, V> baseMap)
	{
		_baseMap = baseMap;
	}


	@Override
	public int size()
	{
		return _baseMap.size();
	}


	@Override
	public boolean isEmpty()
	{
		return _baseMap.isEmpty();
	}


	@Override
	public boolean contains(Object o)
	{
		if (o instanceof Map.Entry) {
			if (o == null)
				return false;
			Map.Entry e = (Map.Entry)o;
			if (e.getKey() == null)
				return false;
			final V value = _baseMap.get(e.getKey());
			return (((e.getValue() == null) && (value == null))
			|| (e.getValue() == value)
			|| (e.getValue().equals(value)));
		} else {
			return false;
		}
	}


	@Override
	public Iterator<Entry<K, V>> iterator()
	{
		return new Itr();
	}


	@Override
	public Object[] toArray()
	{
		return _baseMap.entrySet().toArray();
	}


	@Override
	public <T> T[] toArray(T[] a)
	{
		return _baseMap.entrySet().toArray(a);
	}


	@Override
	public boolean add(Entry<K, V> e)
	{
		final V oldValue = _baseMap.put(e.getKey(), e.getValue());
		return (((e.getValue() != oldValue)
		|| ((e.getValue() == null) && (oldValue != null))
		|| (! e.getValue().equals(oldValue))));
	}

	@Override
	public boolean remove(Object o)
	{
		if (o instanceof Map.Entry) {
			final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)o;			
			boolean removed = _baseMap.containsKey(entry.getKey());
			_baseMap.remove(entry.getKey());
			assert !_baseMap.containsKey(entry.getKey());
			return removed;
		} else
			return false;
	}


	@Override
	public boolean containsAll(Collection<?> c)
	{
		for (Object o: c) {
			if (! contains(o))
				return false;
		}
		return true;
	}


	@Override
	public boolean addAll(Collection<? extends Entry<K, V>> c)
	{
		boolean added = false;
		for (Entry<K, V> entry: c)
			added |= add(entry);
		return added;
	}


	@Override
	public boolean retainAll(Collection<?> c)
	{
		boolean removed = false;
		Iterator<Entry<K, V>> iter = iterator();
		while (iter.hasNext()) {
			final Entry<K, V> entry = iter.next();
			if (! c.contains(entry)) {
				removed = true;
				iter.remove();
			}
		}
		return removed;
	}


	@Override
	public boolean removeAll(Collection<?> c)
	{
		boolean removed = false;
		Iterator<Entry<K, V>> iter = iterator();
		while (iter.hasNext()) {
			final Entry<K, V> entry = iter.next();
			if (c.contains(entry)) {
				removed = true;
				iter.remove();
			}
		}
		return removed;
		
	}


	@Override
	public void clear()
	{
		_baseMap.clear();;
	}	

	class Itr
		implements Iterator<Map.Entry<K, V>> {

		private final Iterator<K> _keyIter;
		
		Itr()
		{
				_keyIter = _baseMap.keySet().iterator();
		}


		@Override
		public boolean hasNext()
		{
			return _keyIter.hasNext();
		}


		@Override
		public Entry<K, V> next()
		{			
			final K key = _keyIter.next();
			final Map.Entry<K, V> entry = new DefaultMapEntry<>(key, _baseMap.get(key));
			return entry;
		}


		@Override
		public void remove()
		{
			_keyIter.remove();
		}
		
	}
}
