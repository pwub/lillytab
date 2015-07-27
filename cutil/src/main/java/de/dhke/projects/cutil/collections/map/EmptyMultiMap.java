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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections15.MultiMap;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 * @param <K> key type
 * @param <V> value type
 */
public class EmptyMultiMap<K, V>
	implements MultiMap<K, V>
{
	public  EmptyMultiMap()
	{

	}

	public static <K, V> EmptyMultiMap<K, V> instance()
	{
		return new EmptyMultiMap<>();
	}

	@Override
	public V remove(Object key, Object item)
	{
		throw new UnsupportedOperationException("Cannot modify empty map");
	}

	@Override
	public int size(Object key)
	{
		return 0;
	}

	@Override
	public int size()
	{
		return 0;
	}

	@Override
	public Collection<V> get(Object key)
	{
		return Collections.<V>emptySet();
	}

	@Override
	public boolean containsValue(Object value)
	{
		return false;
	}

	@Override
	public boolean containsValue(Object key, Object value)
	{
		return false;
	}

	@Override
	public V put(K key, V value)
	{
		throw new UnsupportedOperationException("Cannot modify empty map");
	}

	@Override
	public Collection<V> remove(Object key)
	{
		throw new UnsupportedOperationException("Cannot modify empty map");
	}

	@Override
	public Collection<V> values()
	{
		return Collections.<V>emptySet();
	}

	@Override
	public boolean isEmpty()
	{
		return true;
	}

	@Override
	public boolean containsKey(Object key)
	{
		return false;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> t)
	{
		throw new UnsupportedOperationException("Cannot modify empty map");
	}

	@Override
	public void putAll(MultiMap<? extends K, ? extends V> t)
	{
		throw new UnsupportedOperationException("Cannot modify empty map");
	}

	@Override
	public boolean putAll(K key,
						  Collection<? extends V> values)
	{
		throw new UnsupportedOperationException("Cannot modify empty map");
	}

	@Override
	public Iterator<V> iterator(Object key)
	{
		return Collections.<V>emptySet().iterator();
	}

	@Override
	public void clear()
	{	
		throw new UnsupportedOperationException("Cannot modify empty map");
	}

	@Override
	public Set<K> keySet()
	{
		return Collections.<K>emptySet();
	}

	@Override
	public Set<Entry<K, Collection<V>>> entrySet()
	{
		return Collections.<Entry<K, Collection<V>>>emptySet();
	}

	@Override
	public Map<K, Collection<V>> map()
	{
		return Collections.<K, Collection<V>>emptyMap();
	}
}
