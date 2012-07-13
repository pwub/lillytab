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

import java.util.Map;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class MapUtil {
	private MapUtil()
	{
	}

	public static <K, V> int deepHashCode(Map.Entry<K, V> entry)
	{
		return entry.getKey().hashCode() * entry.getValue().hashCode();
	}

	public static <K, V> int deepHashCode(Map<K, V> m)
	{
		int hashCode = 91;
		for (Map.Entry<K, V> entry: m.entrySet()) {
			hashCode += 13 * deepHashCode(entry);
		}
		return hashCode;
	}

	public static <K, V> boolean deepEquals(final Map<K, V> m1, final Map<? extends K, ? extends V> m2)
	{
		for (Map.Entry<K, V> entry1: m1.entrySet()) {
			if (! m2.containsKey(entry1.getKey()))
				return false;
			else {
				if (! m2.get(entry1.getKey()).equals(entry1.getValue()))
					return false;
			}
		}
		return true;
	}
}
