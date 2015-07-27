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

import de.dhke.projects.cutil.IDecorator;
import de.dhke.projects.cutil.collections.factories.HashMapFactory;
import de.dhke.projects.cutil.collections.factories.IMapFactory;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * A copy on write map is used
 * <p/>
 *
 * @param <K> The map's key type
 * @param <V> The map's value type.
 * @param <M> The type of the wrapped map.
 * <p/>
 * @author Peter Wullinger <java@dhke.de>
 */
public class CopyOnWriteMap<K, V>
	implements IDecorator<Map<K, V>>, Map<K, V> {

	/**
	 * *************************************************************************************************************
	 *
	 * CopyOnWriteMap
	 *
	 **************************************************************************************************************
	 */
	private Map<K, V> _wrappedMap;
	private final IMapFactory<K, V, ? extends Map<K, V>> _factory;
	private boolean _wasCopied;
	private final CopyOnWriteMapEntrySet<K, V> _entrySet = new CopyOnWriteMapEntrySet<>(this);
	private final CopyOnWriteMapKeySet<K, V> _keySet = new CopyOnWriteMapKeySet<>(this);
	private final CopyOnWriteMapValueCollection<K, V> _valueCollection = new CopyOnWriteMapValueCollection<>(
		this);


	protected CopyOnWriteMap(final Map<K, V> wrappedMap, final IMapFactory<K, V, ? extends Map<K, V>> factory)
	{
		_wrappedMap = wrappedMap;
		_factory = factory;
	}


	public static <K, V> CopyOnWriteMap<K, V> decorate(final Map<K, V> wrappedMap,
													   final IMapFactory<K, V, ? extends Map<K, V>> factory)
	{
		return new CopyOnWriteMap<>(wrappedMap, factory);
	}


	public static <K, V> CopyOnWriteMap<K, V> decorate(final Map<K, V> wrappedMap)
	{
		return new CopyOnWriteMap<>(wrappedMap, new HashMapFactory<K, V>());
	}


	public boolean copy()
	{
		if (!_wasCopied) {
			final Map<K, V> newMap = getFactory().getInstance();
			newMap.putAll(_wrappedMap);
			_wrappedMap = newMap;
			_wasCopied = true;
			return true;
		} else
			return false;
	}


	@Override
	public Map<K, V> getDecoratee()
	{
		return _wrappedMap;
	}


	@Override
	public int size()
	{
		return _wrappedMap.size();
	}


	@Override
	public boolean isEmpty()
	{
		return _wrappedMap.isEmpty();
	}


	@Override
	public boolean containsKey(final Object key)
	{
		return _wrappedMap.containsKey(key);
	}


	@Override
	public boolean containsValue(final Object value)
	{
		return _wrappedMap.containsValue(value);
	}


	@Override
	public V get(final Object key)
	{
		return _wrappedMap.get(key);
	}


	@Override
	public V put(final K key, final V value)
	{
		copy();
		return _wrappedMap.put(key, value);
	}


	@Override
	public V remove(final Object key)
	{
		copy();
		return _wrappedMap.remove(key);
	}


	@Override
	public void putAll(final Map<? extends K, ? extends V> m)
	{
		copy();
		_wrappedMap.putAll(m);
	}


	@Override
	public void clear()
	{
		copy();
		_wrappedMap = getFactory().getInstance();
		_wasCopied = true;
	}


	@Override
	public Set<K> keySet()
	{
		return _keySet;
	}


	@Override
	public Set<Entry<K, V>> entrySet()
	{
		return _entrySet;
	}


	@Override
	public Collection<V> values()
	{
		return _valueCollection;
	}


	@Override
	public String toString()
	{
		return _wrappedMap.toString();
	}


	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		return _wrappedMap.equals(obj);
	}


	@Override
	public int hashCode()
	{
		int hCode = 0;
		for (Entry<K, V> entry : entrySet()) {
			hCode += entry.hashCode();
		}
		return hCode;
	}


		protected boolean isWasCopied()
	{
		return _wasCopied;
	}


		protected void resetWasCopied()
	{
		_wasCopied = false;
	}


	/**
	 * @return the _factory
	 */
	protected IMapFactory<K, V, ? extends Map<K, V>> getFactory()
	{
		return _factory;
	}
}
