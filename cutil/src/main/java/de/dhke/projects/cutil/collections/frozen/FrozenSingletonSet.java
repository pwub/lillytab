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

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A non-modifiable set wrapping a single item.
 * @param <T> The element type.
 * @author Peter Wullinger <java@dhke.de>
 */
public class FrozenSingletonSet<T> 
	extends AbstractSet<T>
{
	
	private final T _item;	
	
	public FrozenSingletonSet(final T item)
	{
		_item = item;
	}
	
	public static <T> FrozenSingletonSet<T> decorate(final T item)
	{
		return new FrozenSingletonSet<>(item);
	}
	
	@Override
	public Iterator<T> iterator()
	{
		return new Itr();
	}

	@Override
	public boolean contains(Object o)
	{
		return _item.equals(o);
	}

	@Override
	public int size()
	{
		return 1;
	}

	@Override
	public int hashCode()
	{
		return _item.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		else if (o instanceof FrozenSingletonSet) {
			final FrozenSingletonSet<?> other = (FrozenSingletonSet<?>)o;
			return _item.equals(other._item);
		} else
			return false;
	}
	/// <editor-fold defaultstate="collapsed" desc="class Itr">
	class Itr 
		implements Iterator<T>
	{
		private boolean _iswasNextCalled = false;
		
		@Override
		public boolean hasNext()
		{
			return !_iswasNextCalled;
				
		}

		@Override
		public T next()
		{
			if (_iswasNextCalled)
				return _item;
			else
				throw new NoSuchElementException();
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException("Cannot modify a FixedSingletonSet");
		}		
	}
	/// </editor-fold>
}
