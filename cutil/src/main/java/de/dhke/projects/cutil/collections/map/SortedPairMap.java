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

import de.dhke.projects.cutil.ComparablePair;
import de.dhke.projects.cutil.Pair;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class SortedPairMap<First extends Comparable<? super First>, Second extends Comparable<? super Second>, Tag>
	implements ISortedPairMap<First, Second, Tag>
{
	private final SortedMap<ComparablePair<First, Second>, Tag> _baseMap;

	protected SortedPairMap(final SortedMap<ComparablePair<First, Second>, Tag> baseMap)
	{
		_baseMap = baseMap;
	}

	public static <First extends Comparable<? super First>, Second extends Comparable<? super Second>, Tag>
		SortedPairMap<First, Second, Tag>
		decorate(final SortedMap<ComparablePair<First, Second>, Tag> baseMap)
	{
		return new SortedPairMap<>(baseMap);
	}

	public SortedMap<ComparablePair<First, Second>, Tag> getDecoratee()
	{
		return _baseMap;
	}

	@Override
	public boolean containsKey(final First first, final Second second)
	{
		return containsKey(Pair.wrap(first, second));
	}

	@Override
	public Tag get(final First first, final Second second)
	{
		return get(Pair.wrap(first, second));
	}

	@Override
	public Tag remove(final First first, final Second second)
	{
		return remove(Pair.wrap(first, second));
	}

	@Override
	public Tag put(final First first, final Second second, final Tag tag)
	{
		return put(ComparablePair.wrap(first, second), tag);
	}

	public int size()
	{
		return _baseMap.size();
	}

	public boolean isEmpty()
	{
		return _baseMap.isEmpty();
	}

	public boolean containsKey(Object key)
	{
		return _baseMap.containsKey(key);
	}

	public boolean containsValue(Object value)
	{
		return _baseMap.containsValue(value);
	}

	public Tag get(Object key)
	{
		return _baseMap.get(key);
	}

	public Tag put(ComparablePair<First, Second> key, Tag value)
	{
		return _baseMap.put(key, value);
	}

	public Tag remove(Object key)
	{
		return _baseMap.remove(key);
	}

	public void putAll(Map<? extends ComparablePair<First, Second>, ? extends Tag> m)
	{
		_baseMap.putAll(m);
	}

	public void clear()
	{
		_baseMap.clear();
	}

	public Set<ComparablePair<First, Second>> keySet()
	{
		return _baseMap.keySet();
	}

	public Collection<Tag> values()
	{
		return _baseMap.values();
	}

	public Set<Map.Entry<ComparablePair<First, Second>, Tag>> entrySet()
	{
		return _baseMap.entrySet();
	}

	public Comparator<? super ComparablePair<First, Second>> comparator()
	{
		return _baseMap.comparator();
	}

	public SortedMap<ComparablePair<First, Second>, Tag> subMap(ComparablePair<First, Second> fromKey, ComparablePair<First, Second> toKey)
	{
		return _baseMap.subMap(fromKey, toKey);
	}

	public SortedMap<ComparablePair<First, Second>, Tag> headMap(ComparablePair<First, Second> toKey)
	{
		return _baseMap.headMap(toKey);
	}

	public SortedMap<ComparablePair<First, Second>, Tag> tailMap(ComparablePair<First, Second> fromKey)
	{
		return _baseMap.tailMap(fromKey);
	}

	public ComparablePair<First, Second> firstKey()
	{
		return _baseMap.firstKey();
	}

	public ComparablePair<First, Second> lastKey()
	{
		return _baseMap.lastKey();
	}
}
