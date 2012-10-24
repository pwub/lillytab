/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
	
	private final T _item;	
	
	public FrozenSingletonSet(final T item)
	{
		_item = item;
	}
	
	public static <T> FrozenSingletonSet<T> decorate(final T item)
	{
		return new FrozenSingletonSet<T>(item);
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
}
