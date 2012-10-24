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
package de.dhke.projects.cutil.collections.immutable;

import de.dhke.projects.cutil.IDecorator;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.iterators.UnmodifiableIterator;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class ImmutableMultiMap<K, V>
	implements MultiMap<K, V>, IDecorator<MultiMap<K, V>>
{
	private final MultiMap<K, V> _baseMap;
	private final Transformer<K, K> _keyTransformer;
	private final Transformer<V, V> _valueTransformer;
	private final Transformer<Collection<V>, Collection<V>> _immutableSetTransformer = new Transformer<Collection<V>, Collection<V>>() {

		public Collection<V> transform(Collection<V> input)
		{
			return GenericImmutableCollection.decorate(input, _valueTransformer);
		}
	};

	protected ImmutableMultiMap(final MultiMap<K, V> baseMap, final Transformer<K, K> keyTransformer, final Transformer<V, V> valueTransformer)
	{
		_baseMap = baseMap;
		_keyTransformer = keyTransformer;
		_valueTransformer = valueTransformer;
	}

	public static <K, V> ImmutableMultiMap<K, V> decorate(final MultiMap<K, V> baseMap)
	{
		return new ImmutableMultiMap<K, V>(baseMap, null, null);
	}

	public static <K, V> ImmutableMultiMap<K, V> decorate(final MultiMap<K, V> baseMap, final Transformer<V, V> valueTransformer)
	{
		return new ImmutableMultiMap<K, V>(baseMap, null, valueTransformer);
	}

	public static <K, V> ImmutableMultiMap<K, V> decorate(final MultiMap<K, V> baseMap, 
		final Transformer<K, K> keyTransformer,
		final Transformer<V, V> valueTransformer)
	{
		return new ImmutableMultiMap<K, V>(baseMap, keyTransformer, valueTransformer);
	}

	public MultiMap<K, V> getDecoratee()
	{
		return _baseMap;
	}

	public V remove(Object key, Object item)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableMultiMap.");
	}

	public int size(Object key)
	{
		return _baseMap.size(key);
	}

	public int size()
	{
		return _baseMap.size();
	}

	public Collection<V> get(Object key)
	{
		final Collection<V> values = _baseMap.get(key);
		if (values == null)
			return null;
		else
			return GenericImmutableCollection.decorate(values, _valueTransformer);
	}

	public boolean containsValue(Object value)
	{
		return _baseMap.containsValue(value);
	}

	public boolean containsValue(Object key, Object value)
	{
		return _baseMap.containsValue(key, value);
	}

	public V put(K key, V value)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableMultiMap.");
	}

	public Collection<V> remove(Object key)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableMultiMap.");
	}

	public Collection<V> values()
	{
		return Collections.unmodifiableCollection(_baseMap.values());
	}

	public boolean isEmpty()
	{
		return _baseMap.isEmpty();
	}

	public boolean containsKey(Object key)
	{
		return _baseMap.containsKey(key);
	}

	public void putAll(Map<? extends K, ? extends V> t)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableMultiMap.");
	}

	public void putAll(MultiMap<? extends K, ? extends V> t)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableMultiMap.");
	}

	public boolean putAll(K key,
						  Collection<? extends V> values)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableMultiMap.");
	}

	public Iterator<V> iterator(Object key)
	{
		return UnmodifiableIterator.decorate(_baseMap.iterator(key));
	}

	public void clear()
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableMultiMap.");
	}

	public Set<K> keySet()
	{
		return GenericImmutableSet.decorate(_baseMap.keySet(), _keyTransformer);
	}

	public Set<Entry<K, Collection<V>>> entrySet()
	{
		return ImmutableEntrySet.decorate(_baseMap.entrySet(), _keyTransformer, _immutableSetTransformer);
	}

	public Map<K, Collection<V>> map()
	{
		return ImmutableMap.decorate(_baseMap.map(), _immutableSetTransformer);
	}

}
