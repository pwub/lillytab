/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dhke.projects.cutil.collections;

import de.dhke.projects.cutil.IDecorator;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class CollectionFromIterable<T>
	extends AbstractCollection<T>
	implements IDecorator<Iterable<T>>, Collection<T>
{
	private final Iterable<T> _iterable;
	/**
	 * Cached size.
	 * 
	 * -2 indicates, size caching is disabled
	 * -1 indicates, size caching is enabled, but has not yet been calculated.
	 * other values are the actual (cached) size of the underlying Iterable.
	 * 
	 **/
	static final int CACHE_SIZE_ENABLE = -1;
	static final int CACHE_SIZE_DISABLE = -2;
	private int _cachedSize = CACHE_SIZE_ENABLE;
	
	protected CollectionFromIterable(final Iterable<T> iterable, final boolean cacheSize)
	{
		_iterable = iterable;
		if (cacheSize)
			_cachedSize = CACHE_SIZE_ENABLE;
		else
			_cachedSize = CACHE_SIZE_DISABLE;
	}
	
	public static <T> CollectionFromIterable<T> decorate(final Iterable<T> iterable, final boolean cacheSize)
	{		
		return new CollectionFromIterable<T>(iterable, cacheSize);
	}

	@Override
	public Iterator<T> iterator()
	{
		return _iterable.iterator();
	}

	@Override
	public int size()
	{
		if (_cachedSize >= 0)
			return _cachedSize;
		else {
			int size = 0;
			for (T item: _iterable)
				++size;
			
			if (_cachedSize == CACHE_SIZE_ENABLE)
				_cachedSize = size;
			return size;
		}
	}

	@Override
	public Iterable<T> getDecoratee()
	{
		return _iterable;
	}

}
