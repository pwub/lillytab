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

import de.dhke.projects.cutil.IDecorator;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.keyvalue.DefaultMapEntry;


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class AspectMultiMap<K, V, M extends MultiMap<K, V>>

	extends AspectCollectionNotifier<Map.Entry<K, V>, MultiMap<K, V>>
	implements MultiMap<K, V>, IDecorator<M>
{
	private final M _baseMap;

	@Deprecated
	protected AspectMultiMap(final M baseMap)
	{
		_baseMap = baseMap;
	}

	protected AspectMultiMap(final M baseMap, final Object sender)
	{
		super(sender);
		_baseMap = baseMap;
	}

	@Deprecated
	public static <K, V, M extends MultiMap<K, V>>  AspectMultiMap<K, V, M> decorate(final M baseMap)
	{
		return new AspectMultiMap<>(baseMap);
	}

	public static <K, V, M extends MultiMap<K, V>>  AspectMultiMap<K, V, M> decorate(final M baseMap, final Object sender)
	{
		return new AspectMultiMap<>(baseMap, sender);
	}


	@Override
	public V remove(final Object key, final Object item)
	{
		@SuppressWarnings("unchecked")
		Map.Entry<K, V> entry = new DefaultMapEntry<>((K) key, (V) item);
		if (_baseMap.containsValue(key, item)) {
			notifyBeforeElementRemoved(this, entry);
			V value = _baseMap.remove(key, item);
			notifyAfterElementRemoved(this, entry);
			return value;
		} else
			return null;
	}

	@Override
	public int size(final Object key)
	{
		return _baseMap.size(key);
	}

	@Override
	public int size()
	{
		return _baseMap.size();
	}


	@SuppressWarnings("unchecked")
	@Override
	public Collection<V> get(final Object key)
	{
		if ((key == null) || (!containsKey(key)))
			return null;
		else
			return new AspectMultiMapValueCollection<>(this, (K) key);
	}

	@Override
	public boolean containsValue(final Object value)
	{
		return _baseMap.containsValue(value);
	}

	@Override
	public boolean containsValue(final Object key, final Object value)
	{
		return _baseMap.containsValue(key, value);
	}

	@Override
	public V put(final K key, final V value)
	{
		Map.Entry<K, V> entry = new DefaultMapEntry<>(key, value);
		CollectionItemEvent<Map.Entry<K, V>, MultiMap<K, V>> ev = notifyBeforeElementAdded(this, entry);
		final V oldValue = _baseMap.put(key, value);
		/* Notify after add only if the element was actually added */
		if (oldValue != null)
			notifyAfterElementAdded(ev);
		return oldValue;
	}

	@Override
	public Collection<V> remove(final Object key)
	{
		/* use the collection from the base map, as our own ValueCollection may or may not work here */
		Collection<V> values = _baseMap.get(key);
		if (values != null) {
			/* create copy, we modify original collection later */
			for (V value : values) {
				@SuppressWarnings("unchecked")
				Map.Entry<K, V> entry = new DefaultMapEntry<>((K) key, value);
				notifyBeforeElementRemoved(this, entry);
			}
			/* remove mapping */
			_baseMap.remove(key);
			for (V value : values) {
				@SuppressWarnings("unchecked")
				Map.Entry<K, V> entry = new DefaultMapEntry<>((K) key, value);
				notifyAfterElementRemoved(this, entry);
			}
		}
		return values;
	}

	@Override
	public Collection<V> values()
	{
		return new AspectMultiMapValueCollection<>(this);
	}

	@Override
	public boolean isEmpty()
	{
		return getDecoratee().isEmpty();
	}

	@Override
	public boolean containsKey(final Object key)
	{
		return getDecoratee().containsKey(key);
	}

	@Override
	public void putAll(final Map<? extends K, ? extends V> t)
	{
		/* since we want to provide all-or-nothing for all *all-methods, this is slightly complicated */
		for (Map.Entry<? extends K, ? extends V> entry : t.entrySet())
			prepareAdd(entry.getKey(), entry.getValue());

		for (Map.Entry<? extends K, ? extends V> entry : t.entrySet()) {
			if (!containsValue(entry.getKey(), entry.getValue())) {
				_baseMap.put(entry.getKey(), entry.getValue());
				Map.Entry<K, V> evEntry = new DefaultMapEntry<>(entry.getKey(), entry.getValue());
				notifyAfterElementAdded(this, evEntry);
			}
		}
	}

	@Override
	public void putAll(final MultiMap<? extends K, ? extends V> t)
	{
		for (Iterator<?> it = t.entrySet().iterator(); it.hasNext();) {
			@SuppressWarnings("unchecked")
			final Map.Entry<? extends K, Collection<? extends V>> entry = (Map.Entry<? extends K, Collection<? extends V>>) it.
				next();
			/**
			 * We have to add the collection entries individually in order to obtain
			 **/
			for (V value : entry.getValue())
				prepareAdd(entry.getKey(), value);
		}

		for (Iterator<?> it = t.entrySet().iterator(); it.hasNext();) {
			@SuppressWarnings("unchecked")
			Map.Entry<? extends K, Collection<? extends V>> entry = (Map.Entry<? extends K, Collection<? extends V>>) it.
				next();
			for (V value : entry.getValue()) {
				if (!containsValue(entry.getKey(), value)) {
					_baseMap.put(entry.getKey(), value);
					final Map.Entry<K, V> evEntry = new DefaultMapEntry<>(entry.getKey(), value);
					notifyAfterElementAdded(this, evEntry);
				}
			}
		}

	}

	@Override
	public boolean putAll(final K key, final Collection<? extends V> values)
	{
		for (V value : values)
			prepareAdd(key, value);

		boolean wasAdded = false;
		for (V value : values) {
			if (!containsValue(key, value)) {
				_baseMap.put(key, value);
				wasAdded = true;
				final Map.Entry<K, V> evEntry = new DefaultMapEntry<>(key, value);
				notifyAfterElementAdded(this, evEntry);
			}
		}
		return wasAdded;
	}

	@Override
	public Iterator<V> iterator(final Object key)
	{
		final Collection<V> valueCollection = get(key);
		if (valueCollection != null)
			return get(key).iterator();
		else
			return null;
	}

	@Override
	public void clear()
	{
		if (!isEmpty()) {
			notifyBeforeCollectionCleared(this);
			_baseMap.clear();
			notifyAfterCollectionCleared(this);
		}
	}

	@Override
	public Set<K> keySet()
	{
		return new AspectMultiMapKeySet<>(this);
	}

	@Override
	public Set<Entry<K, Collection<V>>> entrySet()
	{
		return new AspectMultiMapEntrySet<>(this);
	}

	@Override
	public Map<K, Collection<V>> map()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public M getDecoratee()
	{
		return _baseMap;
	}

	@Override
	public String toString()
	{
		return _baseMap.toString();
	}

		private void prepareAdd(final K key, final V value)
	{
		Map.Entry<K, V> evEntry = new DefaultMapEntry<>(key, value);
		notifyBeforeElementAdded(this, evEntry);
	}
}
