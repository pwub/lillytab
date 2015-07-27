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

import de.dhke.projects.cutil.IDecorator;
import de.dhke.projects.cutil.Pair;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**
 *
 * @param <First> 
 * @param <Second> 
 * @param <Tag> 
 * @author Peter Wullinger <java@dhke.de>
 */
public class PairMap<First, Second, Tag>
	implements IPairMap<First, Second, Tag>, IDecorator<Map<Pair<First, Second>, Tag>>
{
	private final Map<Pair<First, Second>, Tag> _baseMap;

	protected PairMap(final Map<Pair<First, Second>, Tag> baseMap)
	{
		_baseMap = baseMap;
	}

	public static <First, Second, Tag> IPairMap<First, Second, Tag>
		decorate(final Map<Pair<First, Second>, Tag> baseMap)
	{
		return new PairMap<>(baseMap);
	}

	@Override
	public Map<Pair<First, Second>, Tag> getDecoratee()
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
	public Tag removePair(final First first, final Second second)
	{
		return remove(Pair.wrap(first, second));
	}

	@Override
	public Tag put(final First first, final Second second, final Tag tag)
	{
		return put(Pair.wrap(first, second), tag);
	}

	@Override
	public int size()
	{
		return _baseMap.size();
	}

	@Override
	public boolean isEmpty()
	{
		return _baseMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key)
	{
		return _baseMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value)
	{
		return _baseMap.containsValue(value);
	}

	@Override
	public Tag get(Object key)
	{
		return _baseMap.get(key);
	}

	@Override
	public Tag put(Pair<First, Second> key, Tag value)
	{
		return _baseMap.put(key, value);
	}

	@Override
	public Tag remove(Object key)
	{
		return _baseMap.remove(key);
	}

	@Override
	public void putAll(Map<? extends Pair<First, Second>, ? extends Tag> m)
	{
		_baseMap.putAll(m);
	}

	@Override
	public void clear()
	{
		_baseMap.clear();
	}

	@Override
	public Set<Pair<First, Second>> keySet()
	{
		return _baseMap.keySet();
	}

	@Override
	public Collection<Tag> values()
	{
		return _baseMap.values();
	}

	@Override
	public Set<Entry<Pair<First, Second>, Tag>> entrySet()
	{
		return _baseMap.entrySet();
	}
}
