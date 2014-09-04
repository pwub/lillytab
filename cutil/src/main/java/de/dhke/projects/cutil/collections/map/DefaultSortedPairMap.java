/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
