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
package de.dhke.projects.cutil.collections.map;

import de.dhke.projects.cutil.collections.set.SortedListSet;
import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class MultiSortedListSetHashMap<K, V extends Comparable<? super V>>
	extends MultiHashMap<K ,V>
{
	private static final long serialVersionUID = -7072450359344907745L;
	public MultiSortedListSetHashMap()
	{
		super();
	}

	public MultiSortedListSetHashMap(Map<K, V> mapToCopy)
	{
		super(mapToCopy);
	}

	public MultiSortedListSetHashMap(MultiMap<K, V> mapToCopy)
	{
		super(mapToCopy);
	}

	public MultiSortedListSetHashMap(int initialCapacity)
	{
		super(initialCapacity);
	}

	public MultiSortedListSetHashMap(int initialCapacity, float loadFactory)
	{
		super(initialCapacity, loadFactory);
	}

	@Override
	protected Collection<V> createCollection(Collection<? extends V> coll)
	{
		if (coll != null)
			return new SortedListSet<V>(coll);
		else
			return new SortedListSet<V>();
	}
}
