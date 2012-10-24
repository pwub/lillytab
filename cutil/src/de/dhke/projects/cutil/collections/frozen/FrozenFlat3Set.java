/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dhke.projects.cutil.collections.frozen;

import de.dhke.projects.cutil.collections.CollectionUtil;
import de.dhke.projects.cutil.collections.set.Flat3Set;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.text.AbstractDocument;
import org.apache.commons.collections15.SetUtils;

/**
 * A frozen set that uses {@link Flat3Set} 
 * as a backing set.
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class FrozenFlat3Set<T>
	extends Flat3Set<T> {

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
	private int _hashValue = 0;


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
