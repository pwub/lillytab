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
 * @param <K> 
 * @param <V> 
 * @param <M> 
 * @author Peter Wullinger <java@dhke.de>
 */
public class AspectMapEntrySet<K, V, M extends Map<K, V>>
	implements Set<Map.Entry<K, V>>
{
	private class Itr
		implements Iterator<Map.Entry<K, V>>
	{
		private final Iterator<Map.Entry<K, V>> _entryIterator;
		private Map.Entry<K, V> _current = null;

		private Itr()
		{
			super();
			_entryIterator = _aspectMap.getDecoratee().entrySet().iterator();
		}

		public boolean hasNext()
		{
			return _entryIterator.hasNext();
		}

		public Entry<K, V> next()
		{
			_current = _entryIterator.next();
			return _current;
		}

		public void remove()
		{
			CollectionItemEvent<Map.Entry<K, V>, Map<K, V>> ev = new CollectionItemEvent<Map.Entry<K, V>, Map<K, V>>(
				this, _aspectMap, new DefaultMapEntry<K, V>(_current));
			_aspectMap.notifyBeforeElementRemoved(ev);
			_entryIterator.remove();
			_aspectMap.notifyAfterElementRemoved(ev);
		}
	}
	private final AspectMap<K, V, M> _aspectMap;
	private final Set<Map.Entry<K, V>> _entrySet;

	protected AspectMapEntrySet(final AspectMap<K, V, M> aspectMap)
	{
		_aspectMap = aspectMap;
		_entrySet = _aspectMap.getDecoratee().entrySet();
	}

	public int size()
	{
		return _entrySet.size();
	}

	public boolean isEmpty()
	{
		return _entrySet.isEmpty();
	}

	public boolean contains(final Object o)
	{
		return _entrySet.contains(o);
	}

	public Iterator<Entry<K, V>> iterator()
	{
		return new Itr();
	}

	public Object[] toArray()
	{
		return _entrySet.toArray();
	}

	public <T> T[] toArray(final T[] a)
	{
		return _entrySet.toArray(a);
	}

	public boolean add(final Entry<K, V> e)
	{
		throw new UnsupportedOperationException("Cannot add to entry set");
		/*
		 * disable to be consistent with standard entry sets.
		 *
		boolean haveEntry = _entrySet.contains(e);
		_aspectMap.put(e.getKey(), e.getValue());
		return !haveEntry;
		 */
	}

	@SuppressWarnings("unchecked")
	public boolean remove(final Object o)
	{
		boolean haveEntry = _entrySet.contains(o);
		assert o instanceof Map.Entry;
		_aspectMap.remove(((Map.Entry) o).getKey());
		return haveEntry;
	}

	public boolean containsAll(final Collection<?> c)
	{
		return _entrySet.containsAll(c);
	}

	public boolean addAll(final Collection<? extends Entry<K, V>> c)
	{
		throw new UnsupportedOperationException("Cannot add to entry set");
	}

	private boolean batchRemove(final Collection<?> c, final boolean retain)
	{
		for (Map.Entry<K, V> entry : this) {
			if (c.contains(entry) != retain) {
				CollectionItemEvent<Map.Entry<K, V>, Map<K, V>> ev = new CollectionItemEvent<Entry<K, V>, Map<K, V>>(
					this, _aspectMap, new DefaultMapEntry<K, V>(entry));
				_aspectMap.notifyBeforeElementRemoved(ev);
			}
		}
		boolean wasRemoved = false;
		Iterator<Map.Entry<K, V>> iter = _entrySet.iterator();
		while (iter.hasNext()) {
			Map.Entry<K, V> entry = iter.next();
			if (c.contains(entry) != retain) {
				CollectionItemEvent<Map.Entry<K, V>, Map<K, V>> ev = new CollectionItemEvent<Entry<K, V>, Map<K, V>>(
					this, _aspectMap, new DefaultMapEntry<K, V>(entry));
				iter.remove();
				_aspectMap.notifyAfterElementRemoved(ev);
				wasRemoved = true;
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
			CollectionEvent<Map.Entry<K, V>, Map<K, V>> ev = new CollectionEvent<Map.Entry<K, V>, Map<K, V>>(this,
																											 _aspectMap);
			_aspectMap.notifyBeforeCollectionCleared(ev);
			_entrySet.clear();
			_aspectMap.notifyAfterCollectionCleared(ev);
		}
	}

	@Override
	public String toString()
	{
		return _entrySet.toString();
	}
}
