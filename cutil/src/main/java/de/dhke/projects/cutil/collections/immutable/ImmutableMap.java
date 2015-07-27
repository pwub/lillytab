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
package de.dhke.projects.cutil.collections.immutable;

import de.dhke.projects.cutil.IDecorator;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections15.Transformer;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class ImmutableMap <K, V>
	implements Map<K, V>, IDecorator<Map<K, V>>
{
	private final Map<K, V> _backMap;
	private final Transformer<K, K> _keyTransformer;
	private final Transformer<V, V> _valueTransformer;

	private ImmutableMap(final Map<K, V> backMap, final Transformer<K, K> keyTransformer, final Transformer<V, V> valueTransformer)
	{
		_backMap = backMap;
		_keyTransformer = keyTransformer;
		_valueTransformer = valueTransformer;
	}

	public static <K, V> ImmutableMap<K, V> decorate(final Map<K, V> backMap)
	{
		return new ImmutableMap<>(backMap, null, null);
	}

	public static <K, V> ImmutableMap<K, V> decorate(final Map<K, V> backMap, final Transformer<V, V> valueTransformer)
	{
		return new ImmutableMap<>(backMap, null, valueTransformer);
	}


	public static <K, V> ImmutableMap<K, V> decorate(final Map<K, V> backMap,
		final Transformer<K, K> keyTransformer,
		final Transformer<V, V> valueTransformer)
	{
		return new ImmutableMap<>(backMap, keyTransformer, valueTransformer);
	}

	@Override
	public Map<K, V> getDecoratee()
	{
		return _backMap;
	}


	@Override
	public int size()
	{
		return _backMap.size();
	}

	@Override
	public boolean isEmpty()
	{
		return _backMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key)
	{
		return _backMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value)
	{
		return _backMap.containsValue(value);
	}

	@Override
	public V get(Object key)
	{
		return _backMap.get(key);
	}

	@Override
	public V put(K key, V value)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableCollectionMap.");
	}

	@Override
	public V remove(Object key)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableCollectionMap.");
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableCollectionMap.");
	}

	@Override
	public void clear()
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableCollectionMap.");
	}

	@Override
	public Set<K> keySet()
	{
		return GenericImmutableSet.decorate(_backMap.keySet(), _keyTransformer);
	}

	@Override
	public Collection<V> values()
	{
		return GenericImmutableCollection.decorate(_backMap.values(), _valueTransformer);
	}

	@Override
	public Set<Entry<K, V>> entrySet()
	{
		return ImmutableEntrySet.decorate(_backMap.entrySet(), _keyTransformer, _valueTransformer);
	}

}
