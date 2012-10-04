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
package de.dhke.projects.cutil.collections.immutable;

import java.util.Collection;
import org.apache.commons.collections15.Transformer;

/**
 *
 * @param <Collection<T>>
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class GenericImmutableCollection<T, C extends Collection<T>>
	extends GenericImmutableIterable<T, C>
	implements Collection<T>
{
	protected GenericImmutableCollection(final C backColl, final Transformer<T, T> valueTransformer)
	{
		super(backColl, valueTransformer);
	}

	public static <T, C extends Collection<T>> GenericImmutableCollection<T, C> decorate(final C backColl)
	{
		return new GenericImmutableCollection<T, C>(backColl, null);
	}

	public static <T, C extends Collection<T>> GenericImmutableCollection<T, C> decorate(final C backColl, final Transformer<T, T> valueTransformer)
	{
		return new GenericImmutableCollection<T, C>(backColl, valueTransformer);
	}


	public int size()
	{
		return getDecoratee().size();
	}

	public boolean isEmpty()
	{
		return getDecoratee().isEmpty();
	}

	public boolean contains(Object o)
	{
		return getDecoratee().contains(o);
	}

	public Object[] toArray()
	{
		return getDecoratee().toArray();
	}

	public <T> T[] toArray(T[] a)
	{
		return getDecoratee().toArray(a);
	}

	public boolean add(T e)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableCollectionSet.");
	}

	public boolean remove(Object o)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableCollectionSet.");
	}

	public boolean containsAll(Collection<?> c)
	{
		return getDecoratee().containsAll(c);
	}

	public boolean addAll(Collection<? extends T> c)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableCollectionSet.");
	}

	public boolean retainAll(Collection<?> c)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableCollectionSet.");
	}

	public boolean removeAll(Collection<?> c)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableCollectionSet.");
	}

	public void clear()
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableCollectionSet.");
	}
}
