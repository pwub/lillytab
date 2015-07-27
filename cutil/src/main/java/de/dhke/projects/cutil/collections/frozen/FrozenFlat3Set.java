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

import de.dhke.projects.cutil.collections.set.Flat3Set;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.collections15.SetUtils;

/**
 * A frozen set that uses {@link Flat3Set} 
 * as a backing set.
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class FrozenFlat3Set<T>
	extends Flat3Set<T> {
	private int _hashValue = 0;

	public FrozenFlat3Set(final Collection<? extends T> coll)
	{
		super(coll);
	}


	@Override
	public Iterator<T> iterator()
	{
		return FrozenIterator.decorate(super.iterator());
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
			_hashValue = SetUtils.hashCodeForSet(this);
		return _hashValue;
	}


	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		else if (obj instanceof Collection)
			return SetUtils.isEqualSet(this, (Collection<?>) obj);
		else
			return false;
	}

	@Override
	public String toString()
	{
		final StringBuffer sb = new StringBuffer(2 + size() * 3);
		sb.append("{");
		boolean first = true;
		for (T item: this) {
			if (first)
				first = false;
			else
				sb.append(", ");
			sb.append(item);
		}
		sb.append("}");
		return sb.toString();
	}
	
	
}
