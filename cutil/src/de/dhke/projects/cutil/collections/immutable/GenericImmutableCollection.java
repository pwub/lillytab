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

import de.dhke.projects.cutil.IDecorator;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.collections15.Transformer;

/**
 *
 * @param <Collection<T>>
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class GenericImmutableCollection<T, C extends Collection<T>>
	implements Collection<T>, IDecorator<C>
{
	private final C _backCollection;
	private final Transformer<T, T> _valueTransformer;

	public Transformer<T, T> getValueTransformer()
	{
		return _valueTransformer;
	}

	public C getDecoratee()
	{
		return _backCollection;
	}

	protected GenericImmutableCollection(final C backColl, final Transformer<T, T> valueTransformer)
	{
		assert backColl != null;
		_backCollection = backColl;
		_valueTransformer = valueTransformer;
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
		return _backCollection.size();
	}

	public boolean isEmpty()
	{
		return _backCollection.isEmpty();
	}

	public boolean contains(Object o)
	{
		return _backCollection.contains(o);
	}

	public Iterator<T> iterator()
	{
		return ImmutableIterator.decorate(_backCollection.iterator(), _valueTransformer);
	}

	public Object[] toArray()
	{
		return _backCollection.toArray();
	}

	public <T> T[] toArray(T[] a)
	{
		return _backCollection.toArray(a);
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
		return _backCollection.containsAll(c);
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
