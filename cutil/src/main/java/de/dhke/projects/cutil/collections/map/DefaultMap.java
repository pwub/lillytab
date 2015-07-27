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

import de.dhke.projects.cutil.IDecorator;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class DefaultMap<K, V>
	extends AbstractMap<K, V>
	implements IDecorator<Map<K, V>>, Map<K, V> {
	private final IDefaultFactory<K, V> _valueFactory;
	private final Map<K, V> _backend;

	protected DefaultMap(final Map<K, V> backend, final IDefaultFactory<K, V> valueFactory)
	{
		_backend = backend;
		_valueFactory = valueFactory;
	}


	protected DefaultMap(final Map<K, V> backend, final V defaultValue)
	{
		_backend = backend;
		_valueFactory = new IDefaultFactory<K, V>() {
			@Override
			public V getInstance(K key)
			{
				return defaultValue;
			}
		};
	}


	protected DefaultMap(final Map<K, V> backend, final Class<? extends V> defaultClass)
	{
		_backend = backend;
		_valueFactory = new IDefaultFactory<K, V>() {
			@Override
			public V getInstance(K key)
			{
				try {
					return defaultClass.getConstructor().newInstance();
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException ex) {
					throw new IllegalArgumentException("Unable to create instance from " + defaultClass);
				}
			}
		};
	}


	public static <K, V> DefaultMap<K, V> decorate(final Map<K, V> backend, final IDefaultFactory<K, V> valueFactory)
	{
		return new DefaultMap<>(backend, valueFactory);
	}


	public static <K, V> DefaultMap<K, V> decorate(final Map<K, V> backend, final V defaultValue)
	{
		return new DefaultMap<>(backend, defaultValue);
	}


	public static <K, V> DefaultMap<K, V> decorate(final Map<K, V> backend, final Class<? extends V> defaultClass)
	{
		return new DefaultMap<>(backend, defaultClass);
	}


	@Override
	public Set<Entry<K, V>> entrySet()
	{
		return _backend.entrySet();
	}


	@Override
	public Map<K, V> getDecoratee()
	{
		return _backend;
	}


	@Override
	@SuppressWarnings("unchecked")
	public V get(Object key)
	{
		if (!containsKey(key)) {
			_backend.put((K) key, _valueFactory.getInstance((K) key));
		}
		return _backend.get(key);
	}

	@Override
	public V put(K key, V value)
	{
		return _backend.put(key, value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m)
	{
		_backend.putAll(m);
	}

	@Override
	public boolean containsKey(Object key)
	{
		return _backend.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value)
	{
		return _backend.containsValue(value);
	}

	public IDefaultFactory<K, V> getValueFactory()
	{
		return _valueFactory;
	}

	@Override
	public V remove(Object key)
	{
		return _backend.remove(key);
	}

	@Override
	public Set<K> keySet()
	{
		/* XXX - with default maps, the keyset could be addable */
		return _backend.keySet();
	}

	@Override
	public void clear()
	{
		_backend.clear();
	}

	@Override
	public int size()
	{
		return _backend.size();
	}

	@Override
	public Collection<V> values()
	{
		return _backend.values();
	}

	@Override
	public boolean isEmpty()
	{
		return _backend.isEmpty();
	}	

	public interface IDefaultFactory<K, V> {

		V getInstance(final K key);
	}
}
