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


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class GenericImmutableIterable<T, I extends Iterable<T>>
	implements Iterable<T>, IDecorator<I>
{
	private final I _backIterable;
	private final Transformer<T, T> _valueTransformer;

	protected GenericImmutableIterable(final I backIterable, final Transformer<T, T> valueTransformer)
	{
		assert backIterable != null;
		_backIterable = backIterable;
		_valueTransformer = valueTransformer;
	}

		public Transformer<T, T> getValueTransformer()
	{
		return _valueTransformer;
	}

	@Override
	public I getDecoratee()
	{
		return _backIterable;
	}

	public static <T, C extends Iterable<T>> GenericImmutableIterable<T, C> decorate(final C backColl)
	{
		return new GenericImmutableIterable<>(backColl, null);
	}

	public static <T, C extends Iterable<T>> GenericImmutableIterable<T, C> decorate(final C backColl,
																					 final Transformer<T, T> valueTransformer)
	{
		return new GenericImmutableIterable<>(backColl, valueTransformer);
	}

	@Override
	public Iterator<T> iterator()
	{
		return ImmutableIterator.decorate(_backIterable.iterator(), _valueTransformer);
	}
}
