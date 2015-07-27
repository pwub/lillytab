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
package de.dhke.projects.cutil.collections;

import de.dhke.projects.cutil.IDecorator;
import java.util.Iterator;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.iterators.TransformIterator;


/**
 * Iterator that supports transformed iteration over another iterator.
 * < /p>
 * This only exists, because {@link TransformIterator} cannot be used with wildcard collections.
 * 
 * @author Peter Wullinger <java@dhke.de>
 */
public class ExtractorIterator<I, O>
	implements Iterator<O>, IDecorator<Iterator<? extends I>>
{
	private final Iterator<? extends I> _baseIter;
	private final Transformer<I, O> _transformer;

	protected ExtractorIterator(final Iterator<? extends I> baseIter, final Transformer<I, O> transformer)
	{
		_baseIter = baseIter;
		_transformer = transformer;
	}

	public static <I, O> ExtractorIterator<I, O> decorate(final Iterator<? extends I> baseIter,
														  Transformer<I, O> transformer)
	{
		return new ExtractorIterator<>(baseIter, transformer);
	}

	@Override
	public O next()
	{
		return _transformer.transform(_baseIter.next());
	}

	@Override
	public boolean hasNext()
	{
		return _baseIter.hasNext();
	}

	@Override
	public void remove()
	{
		_baseIter.remove();
	}

	@Override
	public Iterator<? extends I> getDecoratee()
	{
		return _baseIter;
	}
}
