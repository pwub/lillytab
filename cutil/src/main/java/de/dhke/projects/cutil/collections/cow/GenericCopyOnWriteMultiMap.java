/**
 * (c) 2009-2013 Peter Wullinger
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
import de.dhke.projects.cutil.collections.factories.IMultiMapFactory;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections15.MultiMap;


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class GenericCopyOnWriteMultiMap<K, V, M extends MultiMap<K, V>>
	implements MultiMap<K, V>, IDecorator<M>
{
	private M _baseMap;
	private final IMultiMapFactory<K, V, M> _factory;
	/* stored key set, entry set, and value collection. Will be created on demand */
	private CopyOnWriteMultiMapKeySet<K, V, M> _keySet = null;
	private CopyOnWriteMultiMapValueCollection<K, V, M> _valueCollection = null;
	private CopyOnWriteMultiMapEntrySet<K, V, M> _entrySet = null;
	private boolean _isWasCopied = false;

	protected GenericCopyOnWriteMultiMap(final M baseMap, final IMultiMapFactory<K, V, M> factory)
	{
		_factory = factory;
		_baseMap = baseMap;
	}

	protected boolean isWasCopied()
	{
		return _isWasCopied;
	}

	protected void resetWasCopied()
	{
		_isWasCopied = false;
	}

	public boolean copy()
	{
		if (!_isWasCopied) {
			M newMap = getFactory().getInstance();
			newMap.putAll(_baseMap);
			_baseMap = newMap;
			_isWasCopied = true;
			return true;
		} else
			return false;
	}

	@Override
	public V remove(final Object key, final Object item)
	{
		copy();
		return _baseMap.remove(key, item);
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

	@Override
	public Collection<V> get(final Object key)
	{
		/* if (containsKey(key)) */
		return new CopyOnWriteMultiMapValueCollection<>(this, key);
		/* else
		 return null; */
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
	public V put(K key, V value)
	{
		copy();
		return _baseMap.put(key, value);
	}

	@Override
	public Collection<V> remove(final Object key)
	{
		copy();
		return _baseMap.remove(key);
	}

	@Override
	public Collection<V> values()
	{
		if (_valueCollection == null)
			_valueCollection = new CopyOnWriteMultiMapValueCollection<>(this);
		return _valueCollection;
	}

	@Override
	public boolean isEmpty()
	{
		return _baseMap.isEmpty();
	}

	@Override
	public boolean containsKey(final Object key)
	{
		return _baseMap.containsKey(key);
	}

	@Override
	public void putAll(final Map<? extends K, ? extends V> t)
	{
		copy();
		_baseMap.putAll(t);
	}

	@Override
	public void putAll(final MultiMap<? extends K, ? extends V> t)
	{
		copy();
		_baseMap.putAll(t);
	}

	@Override
	public boolean putAll(final K key,
						  final Collection<? extends V> values)
	{
		copy();
		return _baseMap.putAll(key, values);
	}

	@Override
	public Iterator<V> iterator(final Object key)
	{
		return get(key).iterator();
	}

	@Override
	public void clear()
	{
		_baseMap = getFactory().getInstance();
		_isWasCopied = true;
	}

	@Override
	public Set<K> keySet()
	{
		if (_keySet == null)
			_keySet = new CopyOnWriteMultiMapKeySet<>(this);
		return _keySet;
	}

	@Override
	public Map<K, Collection<V>> map()
	{
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public M getDecoratee()
	{
		return _baseMap;
	}

	@Override
	public Set<Entry<K, Collection<V>>> entrySet()
	{
		if (_entrySet == null)
			_entrySet = new CopyOnWriteMultiMapEntrySet<>(this);
		return _entrySet;
	}

	/**
	 * @return the _factory
	 */
	protected IMultiMapFactory<K, V, M> getFactory()
	{
		return _factory;
	}

	@Override
	public String toString()
	{
		return getDecoratee().toString();
	}
}
