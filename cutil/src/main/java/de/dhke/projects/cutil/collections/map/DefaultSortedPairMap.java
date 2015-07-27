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

import de.dhke.projects.cutil.ComparablePair;
import de.dhke.projects.cutil.Pair;
import java.lang.reflect.InvocationTargetException;
import java.util.SortedMap;


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class DefaultSortedPairMap<First extends Comparable<? super First>, Second extends Comparable<? super Second>, V>
	extends SortedPairMap<First, Second, V>
{
	private final DefaultPairMap.IDefaultFactory<First, Second, V> _valueFactory;

	protected DefaultSortedPairMap(final SortedMap<ComparablePair<First, Second>, V> backend,
								   final DefaultPairMap.IDefaultFactory<First, Second, V> valueFactory)
	{
		super(backend);
		_valueFactory = valueFactory;
	}

	protected DefaultSortedPairMap(final SortedMap<ComparablePair<First, Second>, V> backend, final V defaultValue)
	{
		super(backend);
		_valueFactory = new DefaultPairMap.IDefaultFactory<First, Second, V>()
		{
			@Override
			public V getInstance(final Pair<First, Second> key)
			{
				return defaultValue;
			}
		};
	}

	protected DefaultSortedPairMap(final SortedMap<ComparablePair<First, Second>, V> backend,
								   final Class<? extends V> defaultClass)
	{
		super(backend);
		_valueFactory = new DefaultPairMap.IDefaultFactory<First, Second, V>()
		{
			@Override
			public V getInstance(final Pair<First, Second> key)
			{
				try {
					return defaultClass.getConstructor().newInstance();
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException ex) {
					throw new IllegalArgumentException("Unable to create instance from " + defaultClass);
				}
			}
		};
	}

	public static <First extends Comparable<? super First>, Second extends Comparable<? super Second>, V> DefaultSortedPairMap<First, Second, V> decorate(
		final SortedMap<ComparablePair<First, Second>, V> backend,
		final DefaultPairMap.IDefaultFactory<First, Second, V> valueFactory)
	{
		return new DefaultSortedPairMap<>(backend, valueFactory);
	}

	public static <First extends Comparable<? super First>, Second extends Comparable<? super Second>, V> DefaultSortedPairMap<First, Second, V> decorate(
		final SortedMap<ComparablePair<First, Second>, V> backend, final V defaultValue)
	{
		return new DefaultSortedPairMap<>(backend, defaultValue);
	}

	public static <First extends Comparable<? super First>, Second extends Comparable<? super Second>, V> DefaultSortedPairMap<First, Second, V> decorate(
		final SortedMap<ComparablePair<First, Second>, V> backend, final Class<? extends V> defaultClass)
	{
		return new DefaultSortedPairMap<>(backend, defaultClass);
	}

	@Override
	public V get(Object key)
	{
		if (!containsKey(key)) {
			@SuppressWarnings("unchecked")
			final ComparablePair<First, Second> keyPair = (ComparablePair<First, Second>) key;
			put(keyPair, _valueFactory.getInstance(keyPair));
		}
		return get(key);
	}
	public interface IDefaultFactory<First, Second, V>
	{
		V getInstance(final Pair<First, Second> key);
	}
}
