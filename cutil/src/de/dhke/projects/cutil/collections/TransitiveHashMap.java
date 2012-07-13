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

import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class TransitiveHashMap<K, V>
	extends HashMap<K, V>
{
	private static final long serialVersionUID = 1L;
	
	public TransitiveHashMap()
	{	
	}
	
	public TransitiveHashMap(final Map<? extends K, ? extends V> map)
	{
		super(map);
	}

	@Override
	public V put(K key, V value)
	{
		final Map<K, V> updates = new HashMap<K, V>();
		for (Map.Entry<K, V> entry: entrySet()) {
			if (entry.getValue().equals(key))
				updates.put(entry.getKey(), value);
		}
		super.putAll(updates);
		return super.put(key, value);
	}
	
}
