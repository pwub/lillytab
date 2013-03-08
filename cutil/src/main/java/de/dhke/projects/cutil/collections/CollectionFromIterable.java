/**
 * (c) 2009-2012 Peter Wullinger
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
		return new CollectionFromIterable<>(iterable, cacheSize);
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
