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
package de.dhke.projects.cutil.collections.cow;

import de.dhke.projects.cutil.IDecorator;
import de.dhke.projects.cutil.collections.factories.ICollectionFactory;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 * @param <E> The element type
 * @param <C> The actual collection type.
 */
public class CopyOnWriteCollection<E>
	implements Collection<E>, IDecorator<Collection<E>> {

	private final ICollectionFactory<E, ? extends Collection<E>> _factory;
	private Collection<E> _wrappedCollection;
	private boolean _wasCopied = false;


	protected CopyOnWriteCollection(final Collection<E> initialCollection,
									final ICollectionFactory<E, ? extends Collection<E>> factory)
	{
		_wrappedCollection = initialCollection;
		_factory = factory;
	}


	@Override
	public Collection<E> getDecoratee()
	{
		return _wrappedCollection;
	}


	public boolean copy()
	{
		if (!_wasCopied) {
			final Collection<E> newCollection = _factory.getInstance();
			newCollection.addAll(_wrappedCollection);
			_wrappedCollection = newCollection;
			_wasCopied = true;
			return true;
		} else
			return false;
	}


	@Override
	public int size()
	{
		return _wrappedCollection.size();
	}


	@Override
	public boolean isEmpty()
	{
		return _wrappedCollection.isEmpty();
	}


	@Override
	public boolean contains(Object o)
	{
		return _wrappedCollection.contains(o);
	}


	@Override
	public Iterator<E> iterator()
	{
		return new Itr();
	}


	@Override
	public Object[] toArray()
	{
		return _wrappedCollection.toArray();
	}


	@Override
	public <T> T[] toArray(final T[] a)
	{
		return _wrappedCollection.toArray(a);
	}


	@Override
	public boolean add(final E e)
	{
		copy();
		return _wrappedCollection.add(e);
	}


	@Override
	public  boolean remove(final Object o)
	{
		copy();
		return _wrappedCollection.remove(o);
	}


	@Override
	public boolean containsAll(final Collection<?> c)
	{
		return _wrappedCollection.containsAll(c);
	}


	@Override
	public boolean addAll(final Collection<? extends E> c)
	{
		copy();
		return _wrappedCollection.addAll(c);
	}


	@Override
	public boolean removeAll(final Collection<?> c)
	{
		copy();
		return _wrappedCollection.removeAll(c);
	}


	@Override
	public boolean retainAll(final Collection<?> c)
	{
		copy();
		return _wrappedCollection.retainAll(c);
	}


	@Override
	public void clear()
	{
		/* don't copy items first */
		_wrappedCollection = _factory.getInstance();
		_wasCopied = true;
	}


	@Override
	public String toString()
	{
		return _wrappedCollection.toString();
	}


	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		return _wrappedCollection.equals(obj);
	}


	@Override
	public int hashCode()
	{
		int hCode = 0;
		for (E item : this) {
			hCode += item.hashCode();
		}
		return hCode;
	}


		protected boolean isWasCopied()
	{
		return _wasCopied;
	}


		protected void resetWasCopied()
	{
		_wasCopied = false;
	}


	/**
	 * @return the _factory
	 */
	protected ICollectionFactory<E, ? extends Collection<E>> getFactory()
	{
		return _factory;
	}


	@Override
	protected CopyOnWriteCollection<E> clone()
		throws CloneNotSupportedException
	{
		final CopyOnWriteCollection<E> klone = new CopyOnWriteCollection<>(getDecoratee(), getFactory());
		resetWasCopied();
		return klone;
	}

	private class Itr
		implements Iterator<E> {

		private final Iterator<E> _baseIter;
		private boolean _collectionAlreadyCopied = true;


		Itr()
		{
			_baseIter = getDecoratee().iterator();
		}


		@Override
		public boolean hasNext()
		{
			return _baseIter.hasNext();
		}


		@Override
		public E next()
		{
			return _baseIter.next();
		}


		/**
		 *
		 * Remove the current item from the collection.
		 * <p />
		 * Removing from an uncopied (and thus untouched)
		 * {@link CopyOnWriteCollection} is not supported.
		 * <p/>
		 */
		@Override
		public void remove()
		{
			/**
			 * I cannot think of a proper way to fully support this method.
			 * If a COW list has not been copied yet, a remove would change the underlying collection.
			 * In this case, we would have to create a new iterator at exactly the same
			 * position for the new collection. This works for lists but not for collections.
			 *
			 */
			if (_collectionAlreadyCopied)
				_baseIter.remove();
			else
				throw new UnsupportedOperationException(
					"Cannot remove from untouched CopyOnWriteCollection.");
		}
	}
}
