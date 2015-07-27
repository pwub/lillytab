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
package de.dhke.projects.cutil.collections.immutable;

import de.dhke.projects.cutil.IDecorator;
import java.util.Iterator;
import org.apache.commons.collections15.Transformer;


public class ImmutableIterator<T>
	implements Iterator<T>, IDecorator<Iterator<T>>
{
	private final Iterator<T> _baseIter;
	private final Transformer<T, T> _valueTransformer;
	
		public ImmutableIterator(final Iterator<T> baseIter, final Transformer<T, T> valueTransformer)
	{
		_baseIter = baseIter;
		_valueTransformer = valueTransformer;
	}

	@Override
	public  Iterator<T> getDecoratee()
	{
		return _baseIter;
	}

	public static <T> ImmutableIterator<T> decorate(final Iterator<T> baseIter)
	{
		return new ImmutableIterator<>(baseIter, null);
	}


	public static <T> ImmutableIterator<T> decorate(final Iterator<T> baseIter, final Transformer<T, T> valueTransformer)
	{
		return new ImmutableIterator<>(baseIter, valueTransformer);
	}

	@Override
	public boolean hasNext()
	{
		return _baseIter.hasNext();
	}

	@Override
	public T next()
	{
		if (_valueTransformer == null)
			return _baseIter.next();
		else
			return _valueTransformer.transform(_baseIter.next());
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableIterator.");
	}
}
