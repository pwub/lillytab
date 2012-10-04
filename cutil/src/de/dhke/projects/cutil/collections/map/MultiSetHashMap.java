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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;

/**
 * <p>
 *  A {@link MultiHashMap} that uses HashSets for key storage instead of array lists.
 * </p>
 *
 * @author Peter Wullinger <java@dhke.de>
 * @param <K> Key type
 * @param <V> Value type
 */
public class MultiSetHashMap<K, V>
	extends MultiHashMap<K, V>
{
	static final long serialVersionUID = 8236744137300969863L;

	public MultiSetHashMap()
	{
		super();
	}

	public MultiSetHashMap(Map<K, V> mapToCopy)
	{
		super(mapToCopy);
	}

	public MultiSetHashMap(MultiMap<K, V> mapToCopy)
	{
		super(mapToCopy);
	}

	public MultiSetHashMap(int intialCapacity)
	{
		super(intialCapacity);
	}

	public MultiSetHashMap(int initialCapacity, float loadFactory)
	{
		super(initialCapacity, loadFactory);
	}
	@Override
	protected Collection<V> createCollection(Collection<? extends V> coll)
	{
		if (coll != null)
			return new HashSet<V>(coll);
		else
			return new HashSet<V>();
	}
}
