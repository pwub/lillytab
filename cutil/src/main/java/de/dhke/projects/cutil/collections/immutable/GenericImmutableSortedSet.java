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

import java.util.Comparator;
import java.util.SortedSet;
import org.apache.commons.collections15.Transformer;

/**
 *
 * @param <T> 
 * @param <S> 
 * @author Peter Wullinger <java@dhke.de>
 */
public class GenericImmutableSortedSet<T, S extends SortedSet<T>>
	extends GenericImmutableSet<T, S>
	implements SortedSet<T>
{
	protected GenericImmutableSortedSet(final S baseSet, Transformer<T, T> valueTransformer)
	{
		super(baseSet, valueTransformer);
	}

	public static <T, S extends SortedSet<T>> SortedSet<T> decorate(final S baseSet)
	{
		return new GenericImmutableSortedSet<>(baseSet, null);
	}

	public static <T, S extends SortedSet<T>> SortedSet<T> decorate(final S baseSet, final Transformer<T, T> valueTransformer)
	{
		return new GenericImmutableSortedSet<>(baseSet, valueTransformer);
	}

	@Override
	public Comparator<? super T> comparator()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public SortedSet<T> subSet(T fromElement, T toElement)
	{
		return GenericImmutableSortedSet.decorate(getDecoratee().subSet(fromElement, toElement), getValueTransformer());
	}

	@Override
	public SortedSet<T> headSet(T toElement)
	{
		return GenericImmutableSortedSet.decorate(getDecoratee().headSet(toElement), getValueTransformer());
	}

	@Override
	public SortedSet<T> tailSet(T fromElement)
	{
		return GenericImmutableSortedSet.decorate(getDecoratee().tailSet(fromElement), getValueTransformer());
	}

	@Override
	public T first()
	{
		return getValueTransformer().transform(getDecoratee().first());
	}

	@Override
	public T last()
	{
		return getValueTransformer().transform(getDecoratee().last());
	}
}
