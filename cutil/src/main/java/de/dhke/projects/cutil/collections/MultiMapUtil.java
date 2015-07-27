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
package de.dhke.projects.cutil.collections;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.SetUtils;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public final class MultiMapUtil {
	private MultiMapUtil()
	{
	}

	public static <K, V> boolean deepEquals(MultiMap<K, V> m1, MultiMap<? extends K, ? extends V> m2)
	{
		for (Map.Entry<K, Collection<V>> entry1: m1.entrySet()) {
			if (! m2.containsKey(entry1.getKey()))
				return false;
			else {
				final Collection<? extends V> values2 = m2.get(entry1.getKey());
				if ((! entry1.getValue().containsAll(values2)) || (! values2.containsAll(entry1.getValue())))
					return false;
			}
		}
		return m1.keySet().containsAll(m2.keySet());
	}

	public static <K, V> int deepHashCode(Map.Entry<K, Collection<V>> entry)
	{
		return entry.getKey().hashCode() + SetUtils.hashCodeForSet(entry.getValue());
	}


	public static <K, V> int deepHashCode(MultiMap<K, V> m)
	{
		int hashCode = 65537;
		for (Map.Entry<K, Collection<V>> entry: m.entrySet()) {
			hashCode += 7 * deepHashCode(entry);
		}
		return hashCode;
	}
	

	public static <V> Collection<V> getTransitive(final MultiMap<V, V> multiMap, final V key, final Collection<V> targetSet)
	{
		Collection<V> keyValues = multiMap.get(key);
		if (keyValues != null) {
			for (V keyValue: keyValues) {
				if (! targetSet.contains(keyValue)) {
					targetSet.add(keyValue);
					targetSet.addAll(getTransitive(multiMap, keyValue, targetSet));
				}
			}
		}
		return targetSet;
	}

	
	public static <V> Collection<V> getTransitive(final MultiMap<V, V> multiMap, final V key)
	{		
		final Set<V> values = new HashSet<>();
		getTransitive(multiMap, key, values);

		return values;
	}	
}
