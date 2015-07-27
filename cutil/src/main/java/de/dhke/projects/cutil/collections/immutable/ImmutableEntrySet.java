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
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.keyvalue.DefaultMapEntry;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class ImmutableEntrySet<K, V>
	implements Set<Map.Entry<K, V>>, IDecorator<Set<Map.Entry<K, V>>>
{
	private final Set<Map.Entry<K, V>> _backEntrySet;
	private final Transformer<Map.Entry<K, V>, Map.Entry<K, V>> _entryTransformer;
	private final Transformer<K, K> _keyTransformer;
	private final Transformer<V, V> _valueTransformer;

	protected ImmutableEntrySet(final Set<Map.Entry<K, V>> backEntrySet, final Transformer<K, K> keyTransformer, final Transformer<V, V> valueTransformer)
	{
		_backEntrySet = backEntrySet;
		_keyTransformer = keyTransformer;
		_valueTransformer = valueTransformer;
		_entryTransformer = new EntryTransformer();
	}

	public static <K, V> ImmutableEntrySet<K, V> decorate(final Set<Map.Entry<K, V>> backEntrySet)
	{
		return new ImmutableEntrySet<>(backEntrySet, null, null);
	}

	public static <K, V> ImmutableEntrySet<K, V> decorate(final Set<Map.Entry<K, V>> backEntrySet, final Transformer<V, V> valueTransformer)
	{
		return new ImmutableEntrySet<>(backEntrySet, null, valueTransformer);
	}

	public static <K, V> ImmutableEntrySet<K, V> decorate(final Set<Map.Entry<K, V>> backEntrySet, final Transformer<K, K> keyTransformer, final Transformer<V, V> valueTransformer)
	{
		return new ImmutableEntrySet<>(backEntrySet, keyTransformer, valueTransformer);
	}

	@Override
	public Set<Entry<K, V>> getDecoratee()
	{
		return _backEntrySet;
	}


	@Override
	public int size()
	{
		return _backEntrySet.size();
	}

	@Override
	public boolean isEmpty()
	{
		return _backEntrySet.isEmpty();
	}

	@Override
	public boolean contains(Object o)
	{
		return _backEntrySet.contains(o);
	}

	@Override
	public Iterator<Entry<K, V>> iterator()
	{
		return ImmutableIterator.decorate(_backEntrySet.iterator(), _entryTransformer);
	}

	@Override
	public Object[] toArray()
	{
		return _backEntrySet.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		return _backEntrySet.toArray(a);
	}

	@Override
	public boolean add(Entry<K, V> e)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean remove(Object o)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		return _backEntrySet.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Entry<K, V>> c)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableMultiMapEntrySet.");
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableMultiMapEntrySet.");
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableMultiMapEntrySet.");
	}

	@Override
	public void clear()
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableMultiMapEntrySet.");
	}

	private final class EntryTransformer
		implements Transformer<Map.Entry<K, V>, Map.Entry<K, V>>
	{
		@Override
		public Entry<K, V> transform(Entry<K, V> input)
		{
			K key;
			if (_keyTransformer != null)
				key = _keyTransformer.transform(input.getKey());
			else
				key = input.getKey();

			V value;
			if (_valueTransformer != null)
				value = _valueTransformer.transform(input.getValue());
			else
				value = input.getValue();

			return new DefaultMapEntry<>(key, value);
		}

	}

}
