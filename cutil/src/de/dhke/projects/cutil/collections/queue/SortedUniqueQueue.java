/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dhke.projects.cutil.collections.queue;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.TreeSet;


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class SortedUniqueQueue<T extends Comparable<? super T>>
	extends TreeSet<T>
	implements Queue<T>
{
	@Override
	public boolean offer(T e)
	{
		return add(e);
	}

	@Override
	public T remove()
	{
		if (isEmpty())
			throw new NoSuchElementException("Queue is empty");
		else {
			final T item = first();
			remove(item);
			return item;
		}
	}

	@Override
	public T poll()
	{
		if (isEmpty())
			return null;
		else {
			final T item = first();
			remove(item);
			return item;
		}
	}

	@Override
	public T element()
	{
		if (isEmpty())
			throw new NoSuchElementException("Queue is empty");
		else
			return first();
	}

	@Override
	public T peek()
	{
		if (isEmpty())
			return null;
		else
			return first();
	}
}
