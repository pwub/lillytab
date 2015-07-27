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
package de.dhke.projects.cutil.collections.eqaware;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class EqualityAwareTreeMap<K, V>
	extends TreeMap<K, V>
{
	private static final long serialVersionUID = -7920240908937043980L;
	public EqualityAwareTreeMap()
	{
		super(new DefaultEqualityAwareComparator<K>());
	}

	public EqualityAwareTreeMap(Map<? extends K, ? extends V> map)
	{
		super(new DefaultEqualityAwareComparator<K>());
		putAll(map);
	}

	public EqualityAwareTreeMap(SortedMap<? extends K, ? extends V> sortedMap)
	{
		super(new DefaultEqualityAwareComparator<K>());
		putAll(sortedMap);
	}

	public EqualityAwareTreeMap(Comparator<K> keyComparer)
	{
		super(new EqualityAwareComparator<>(keyComparer));
	}
}
