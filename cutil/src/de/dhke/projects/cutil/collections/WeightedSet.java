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
package de.dhke.projects.cutil.collections;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections15.map.HashedMap;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class WeightedSet<K>
	implements Map<K, Double>
{
	private final Map<K, Double> _backend = new HashedMap<K, Double>();

	public WeightedSet()
	{
	}

	public int size()
	{
		return _backend.size();
	}

	public boolean isEmpty()
	{
		return _backend.isEmpty();
	}

	public boolean containsKey(Object key)
	{
		return _backend.containsKey(key);
	}

	public boolean containsValue(Object value)
	{
		return _backend.containsValue(value);
	}

	public Double get(Object key)
	{
		Double value = _backend.get(key);
		if (value == null)
			return 0.0;
		else
			return value;
	}

	public Double put(K key, Double value)
	{
		if ((value == null) || (value.doubleValue() == 0.0)) {
			value = get(key);
			remove(key);
			return value;
		} else {
			final Double oldValue = _backend.put(key, value);
			if (oldValue == null)
				return 0.0;
			else
				return oldValue;
		}
	}

	public Double remove(Object key)
	{
		return _backend.remove(key);
	}

	public void putAll(Map<? extends K, ? extends Double> m)
	{
		for (Entry<? extends K, ? extends Double> entry: m.entrySet())
			put(entry.getKey(), entry.getValue());
	}

	public void clear()
	{
		_backend.clear();
	}

	public Set<K> keySet()
	{
		return Collections.unmodifiableSet(_backend.keySet());
	}

	public Collection<Double> values()
	{
		return Collections.unmodifiableCollection(_backend.values());
	}

	public Set<Entry<K, Double>> entrySet()
	{
		return Collections.unmodifiableSet(_backend.entrySet());
	}

	public double add(K key, double value)
	{
		final double oldValue = get(key);
		final Double sum = value + oldValue;
		return put(key, sum);
	}

	public double sub(K key, double value)
	{
		final double oldValue = get(key);
		final Double diff = oldValue - value;
		return put(key, diff);
	}

	public void addAll(Map<K, Double> m)
	{
		for (Entry<? extends K, ? extends Double> entry: m.entrySet())
			add(entry.getKey(), entry.getValue());
	}

	public void subAll(Map<K, Double> m)
	{
		for (Entry<? extends K, ? extends Double> entry: m.entrySet())
			sub(entry.getKey(), entry.getValue());
	}

	@Override
	public String toString()
	{
		return _backend.toString();
	}
}
