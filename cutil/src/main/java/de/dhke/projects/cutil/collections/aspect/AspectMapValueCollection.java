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


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class AspectMapValueCollection<K, V, M extends Map<K, V>>
	implements Collection<V>
{
	private AspectMap<K, V, M> _aspectMap;
	private Collection<V> _values;

	protected AspectMapValueCollection(final AspectMap<K, V, M> aspectMap)
	{
		_aspectMap = aspectMap;
		_values = aspectMap.getDecoratee().values();
	}

	@Override
	public int size()
	{
		return _values.size();
	}

	@Override
	public boolean isEmpty()
	{
		return _values.isEmpty();
	}

	@Override
	public boolean contains(final Object o)
	{
		return _values.contains(o);
	}

	@Override
	public Iterator<V> iterator()
	{
		return new Itr();
	}

	@Override
	public Object[] toArray()
	{
		return _values.toArray();
	}

	@Override
	public <T> T[] toArray(final T[] a)
	{
		return _values.toArray(a);
	}

	@Override
	public boolean add(final V e)
	{
		throw new UnsupportedOperationException("Cannot add to value collection");
	}

	@Override
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

	@Override
	public boolean containsAll(final Collection<?> c)
	{
		return _values.containsAll(c);
	}

	@Override
	public boolean addAll(final Collection<? extends V> c)
	{
		throw new UnsupportedOperationException("Cannot add to value collection");
	}

	@Override

	public boolean removeAll(final Collection<?> c)
	{
		return batchRemove(c, false);
	}

	@Override
	public boolean retainAll(final Collection<?> c)
	{
		return batchRemove(c, true);
	}

	@Override
	public void clear()
	{
		if (!isEmpty()) {
			CollectionEvent<Map.Entry<K, V>, Map<K, V>> ev = _aspectMap.notifyBeforeCollectionCleared(_aspectMap);
			_values.clear();
			_aspectMap.notifyAfterCollectionCleared(ev);
		}
	}

	@Override
	public String toString()
	{
		return _values.toString();
	}

		private boolean batchRemove(final Collection<?> c, final boolean retain)
	{
		for (Map.Entry<K, V> entry : _aspectMap.getDecoratee().entrySet()) {
			if (c.contains(entry.getValue()) != retain) {
				_aspectMap.notifyBeforeElementRemoved(_aspectMap, entry);
			}
		}
		boolean haveRemoved = false;
		Iterator<Map.Entry<K, V>> entryIter = _aspectMap.getDecoratee().entrySet().iterator();
		while (entryIter.hasNext()) {
			Map.Entry<K, V> entry = entryIter.next();
			if (c.contains(entry.getValue()) != retain) {
				entryIter.remove();
				_aspectMap.notifyAfterElementRemoved(_aspectMap, entry);
				haveRemoved = true;
			}
		}
		return haveRemoved;
	}
	private class Itr
		implements Iterator<V>
	{
		private final Iterator<Map.Entry<K, V>> _entryIterator;

		Itr()
		{
			_entryIterator = _aspectMap.entrySet().iterator();
		}

		@Override
		public boolean hasNext()
		{
			return _entryIterator.hasNext();
		}

		@Override
		public V next()
		{
			return _entryIterator.next().getValue();
		}

		@Override
		public void remove()
		{
			_entryIterator.remove();
		}
	}
}
