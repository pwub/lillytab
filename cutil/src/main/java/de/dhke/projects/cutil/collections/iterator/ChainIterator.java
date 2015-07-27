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
	private final Queue<Iterator<? extends E>> _iterList = new LinkedList<>();
	
	@SafeVarargs
	protected ChainIterator(final Iterator<? extends E> ...iters)
	{
		_iterList.addAll(Arrays.asList(iters));
	}

	protected ChainIterator(final Collection<? extends Iterator<? extends E>> iters)
	{
		_iterList.addAll(iters);
	}
	
	
	@SafeVarargs
	public static <E> ChainIterator<E> decorate(final Iterator<? extends E> ...iters)
	{
		return new ChainIterator<>(iters);
	}
	
	public static <E> ChainIterator<E> decorate(final Collection<? extends Iterator<? extends E>> iters)
	{
		return new ChainIterator<>(iters);
	}


	@Override
	public boolean hasNext()
	{
		while (! _iterList.isEmpty() && (! _iterList.element().hasNext()))
			_iterList.remove();
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
