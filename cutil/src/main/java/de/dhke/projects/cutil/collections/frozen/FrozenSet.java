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
package de.dhke.projects.cutil.collections.frozen;

import de.dhke.projects.cutil.collections.CollectionUtil;
import de.dhke.projects.cutil.collections.map.CuckooHashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.collections15.SetUtils;
import org.apache.commons.collections15.set.MapBackedSet;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class FrozenSet<T>
	implements Set<T>
{
	private final Set<T> _baseSet;


	private int _hashValue = 0;

	public FrozenSet(final Collection<? extends T> source)
	{
		final int capacity = source.size();
		_baseSet = MapBackedSet.decorate(new CuckooHashMap<T, Object>(capacity));
		_baseSet.addAll(source);
	}

	@Override
	public int size()
	{
		return _baseSet.size();
	}


	@Override
	public boolean isEmpty()
	{
		return _baseSet.isEmpty();
	}


	@Override
	public boolean contains(Object o)
	{
		return _baseSet.contains(o);
	}


	@Override
	public Iterator<T> iterator()
	{
		return FrozenIterator.decorate(_baseSet.iterator());
	}


	@Override
	public Object[] toArray()
	{
		return _baseSet.toArray();
	}


	@Override
	public <T> T[] toArray(T[] a)
	{
		return _baseSet.toArray(a);
	}


	@Override
	public boolean add(T e)
	{
		throw new UnsupportedOperationException("Unable to modify FrozenSet");
	}


	@Override
	public boolean remove(Object o)
	{
		throw new UnsupportedOperationException("Unable to modify FrozenSet");
	}


	@Override
	public boolean containsAll(Collection<?> c)
	{
		return _baseSet.containsAll(c);
	}


	@Override
	public boolean addAll(Collection<? extends T> c)
	{
		throw new UnsupportedOperationException("Unable to modify FrozenSet");
	}


	@Override
	public boolean retainAll(Collection<?> c)
	{
		throw new UnsupportedOperationException("Unable to modify FrozenSet");
	}


	@Override
	public boolean removeAll(Collection<?> c)
	{
		throw new UnsupportedOperationException("Unable to modify FrozenSet");
	}


	@Override
	public void clear()
	{
		throw new UnsupportedOperationException("Unable to modify FrozenSet");
	}
	@Override
	public int hashCode()
	{
		if (_hashValue == 0)
			_hashValue = SetUtils.hashCodeForSet(_baseSet);
		return _hashValue;
	}


	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		else if (obj instanceof Collection)
			return SetUtils.isEqualSet(_baseSet, (Collection<?>)obj);
		else
			return false;
	}


	@Override
	public String toString()
	{
		/* MapBackedSet fails to properly implement toString(). Do it here */
		return CollectionUtil.deepToString(_baseSet);
	}
}
