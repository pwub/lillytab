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

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * A {@link HashMap}, that automatically add transitive links.
 * <p />
 * This means, that if (B, C) a key-value-pair within the map and
 * (A, B) is stored, the pair (A, C) is added automatically.
 * 
 * 
 * @param <T> 
 * @author Peter Wullinger <java@dhke.de>
 */
public class TransitiveHashMap<T>
	extends HashMap<T, T>
{
	private static final long serialVersionUID = 1L;
	
	public TransitiveHashMap()
	{	
	}
	
	public TransitiveHashMap(final Map<? extends T, ? extends T> map)
	{
		super(map);
	}

	@Override
	public T put(T key, T value)
	{
		final Map<T, T> updates = new HashMap<>();
		for (Map.Entry<T, T> entry: entrySet()) {
			if (entry.getValue().equals(key))
				updates.put(entry.getKey(), value);
		}
		super.putAll(updates);
		return super.put(key, value);
	}
	
}
