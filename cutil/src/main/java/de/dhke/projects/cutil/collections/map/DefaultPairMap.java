/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dhke.projects.cutil.collections.map;

import de.dhke.projects.cutil.Pair;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class DefaultPairMap<First, Second, V> 
	extends PairMap<First, Second, V>
{
	
	private final IDefaultFactory<First, Second, V> _valueFactory;

	protected DefaultPairMap(final Map<Pair<First, Second>, V> backend, final IDefaultFactory<First, Second, V> valueFactory)
	{
		super(backend);
		_valueFactory = valueFactory;
	}


	protected DefaultPairMap(final Map<Pair<First, Second>, V> backend, final V defaultValue)
	{
		super(backend);
		_valueFactory = new IDefaultFactory<First, Second, V>() {
			@Override
			public V getInstance(final Pair<First, Second> key)
			{
				return defaultValue;
			}
		};
	}


	protected DefaultPairMap(final Map<Pair<First, Second>, V> backend, final Class<? extends V> defaultClass)
	{
		super(backend);
		_valueFactory = new IDefaultFactory<First, Second, V>() {
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


	public static <First, Second, V> DefaultPairMap<First, Second, V> decorate(final Map<Pair<First, Second>, V> backend, final IDefaultFactory<First, Second, V> valueFactory)
	{
		return new DefaultPairMap<>(backend, valueFactory);
	}


	public static <First, Second, V> DefaultPairMap<First, Second, V> decorate(final Map<Pair<First, Second>, V> backend, final V defaultValue)
	{
		return new DefaultPairMap<>(backend, defaultValue);
	}


	public static <First, Second, V> DefaultPairMap<First, Second, V> decorate(final Map<Pair<First, Second>, V> backend, final Class<? extends V> defaultClass)
	{
		return new DefaultPairMap<>(backend, defaultClass);
	}
	
	@Override
	public V get(Object key)
	{
		if (! containsKey(key)) {
			@SuppressWarnings("unchecked")
			final Pair<First, Second> keyPair = (Pair<First, Second>)key;
			put(keyPair, _valueFactory.getInstance(keyPair));
		}
		return get(key); 
	}
	public interface IDefaultFactory<First, Second, V> {

		V getInstance(final Pair<First, Second> key);
	}
	
	/**
	 * no need to override get(First, Second) because this calls get(Object).
	 **/
}
