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

import de.dhke.projects.cutil.IDecorator;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections15.keyvalue.DefaultMapEntry;


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class AspectMap<K, V, M extends Map<K, V>>
	extends AspectCollectionNotifier<Map.Entry<K, V>, Map<K, V>>
	implements Map<K, V>, IDecorator<M>
{
	private final M _baseMap;

	@Deprecated
	protected AspectMap(final M baseMap)
	{
		_baseMap = baseMap;
	}

	protected AspectMap(final M baseMap, final Object sender)
	{
		super(sender);
		_baseMap = baseMap;
	}

	@Deprecated
	public static <K, V, M extends Map<K, V>> AspectMap<K, V, M> decorate(final M baseMap)
	{
		return new AspectMap<K, V, M>(baseMap);
	}

	public static <K, V, M extends Map<K, V>> AspectMap<K, V, M> decorate(final M baseMap, final Object sender)
	{
		return new AspectMap<K, V, M>(baseMap, sender);
	}


	public M getDecoratee()
	{
		return _baseMap;
	}

	public int size()
	{
		return _baseMap.size();
	}

	public boolean isEmpty()
	{
		return _baseMap.isEmpty();
	}

	public boolean containsKey(final Object key)
	{
		return _baseMap.containsKey(key);
	}

	public boolean containsValue(final Object value)
	{
		return _baseMap.containsValue(value);
	}

	public V get(final Object key)
	{
		return _baseMap.get(key);
	}

	public V put(final K key, final V value)
	{
		if (containsKey(key)) {
			V oldValue = get(key);
			DefaultMapEntry<K, V> oldEntry = new DefaultMapEntry<K, V>(key, oldValue);
			DefaultMapEntry<K, V> newEntry = new DefaultMapEntry<K, V>(key, value);
			CollectionItemReplacedEvent<Map.Entry<K, V>, Map<K, V>> ev = new CollectionItemReplacedEvent<Map.Entry<K, V>, Map<K, V>>(
				this, this, oldEntry, newEntry);
			notifyBeforeElementReplaced(ev);
			oldValue = _baseMap.put(key, value);
			notifyAfterElementReplaced(ev);
			return oldValue;
		} else {
			DefaultMapEntry<K, V> newEntry = new DefaultMapEntry<K, V>(key, value);
			CollectionItemEvent<Map.Entry<K, V>, Map<K, V>> ev = new CollectionItemEvent<Entry<K, V>, Map<K, V>>(this,
																												 this,
																												 newEntry);
			notifyBeforeElementAdded(ev);
			V oldValue = _baseMap.put(key, value);
			notifyAfterElementAdded(ev);
			return oldValue;
		}
	}

	public V remove(final Object key)
	{
		V value = get(key);
		boolean haveKey = _baseMap.containsKey(key);
		if (haveKey) {
			@SuppressWarnings("unchecked")
			DefaultMapEntry<K, V> entry = new DefaultMapEntry<K, V>((K) key, value);
			CollectionItemEvent<Map.Entry<K, V>, Map<K, V>> ev = new CollectionItemEvent<Entry<K, V>, Map<K, V>>(this,
																												 this,
																												 entry);
			notifyBeforeElementRemoved(ev);
			value = _baseMap.remove(key);
			if (haveKey)
				notifyAfterElementRemoved(ev);
		}
		return value;
	}

	public void putAll(final Map<? extends K, ? extends V> m)
	{
		for (K key : m.keySet()) {
			V value = m.get(key);
			Map.Entry<K, V> newEntry = new DefaultMapEntry<K, V>(key, value);
			if (containsKey(key)) {
				V oldValue = get(key);
				Map.Entry<K, V> oldEntry = new DefaultMapEntry<K, V>(key, oldValue);
				notifyBeforeElementReplaced(this, oldEntry, newEntry);
			} else {
				notifyBeforeElementAdded(this, newEntry);
			}
		}

		for (K key : m.keySet()) {
			V value = m.get(key);
			Map.Entry<K, V> newEntry = new DefaultMapEntry<K, V>(key, value);
			if (containsKey(key)) {
				_baseMap.put(key, value);
				V oldValue = get(key);
				Map.Entry<K, V> oldEntry = new DefaultMapEntry<K, V>(key, oldValue);
				notifyAfterElementReplaced(this, oldEntry, newEntry);
			} else {
				_baseMap.put(key, value);
				notifyAfterElementAdded(this, newEntry);
			}
		}
	}

	public void clear()
	{
		if (! isEmpty()) {
			CollectionEvent<Map.Entry<K, V>, Map<K, V>> ev = new CollectionEvent<Entry<K, V>, Map<K, V>>(this, this);
			notifyBeforeCollectionCleared(ev);
			_baseMap.clear();
			notifyAfterCollectionCleared(ev);
		}
	}

	public Set<K> keySet()
	{
		return new AspectMapKeySet<K, V, M>(this);
	}

	public Collection<V> values()
	{
		return new AspectMapValueCollection<K, V, M>(this);
	}

	public Set<Entry<K, V>> entrySet()
	{
		return new AspectMapEntrySet<K, V, M>(this);
	}
}
