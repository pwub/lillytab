/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dhke.projects.cutil.collections.iterator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ChainIterator<E>
	implements Iterator<E>
{
	private final Queue<Iterator<? extends E>> _iterList = new LinkedList<Iterator<? extends E>>();
	
	protected ChainIterator(final Iterator<? extends E> ...iters)
	{
		_iterList.addAll(Arrays.asList(iters));
	}

	protected ChainIterator(final Collection<? extends Iterator<? extends E>> iters)
	{
		_iterList.addAll(iters);
	}
	
	
	public static <E> ChainIterator<E> decorate(final Iterator<? extends E> ...iters)
	{
		return new ChainIterator<E>(iters);
	}
	
	public static <E> ChainIterator<E> decorate(final Collection<? extends Iterator<? extends E>> iters)
	{
		return new ChainIterator<E>(iters);
	}


	@Override
	public boolean hasNext()
	{
		return (! _iterList.isEmpty()) && _iterList.element().hasNext();
	}


	@Override
	public E next()
	{
		final E item = _iterList.element().next();
		while ((! _iterList.isEmpty()) && (! _iterList.element().hasNext()))
			_iterList.remove();
		return item;
	}


	@Override
	public void remove()
	{
		_iterList.element().remove();
	}	
}
