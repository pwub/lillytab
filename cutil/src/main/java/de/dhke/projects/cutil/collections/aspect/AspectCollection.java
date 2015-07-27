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
package de.dhke.projects.cutil.collections.aspect;

import de.dhke.projects.cutil.IDecorator;
import java.util.Collection;
import java.util.Iterator;


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 * @param <E> The element type.
 * @param <C> The actual collection type.
 */
public class AspectCollection<E, C extends Collection<E>>
	extends AspectCollectionNotifier<E, Collection<E>>
	implements Collection<E>, IDecorator<C>
{
	private final C _baseCollection;

	public AspectCollection(final C baseCollection)
	{
		_baseCollection = baseCollection;
	}

	public AspectCollection(final C baseCollection, final Object sender)
	{
		super(sender);
		_baseCollection = baseCollection;
	}


	@Deprecated
	public static <E, C extends Collection<E>> AspectCollection<E, C> decorate(final C baseCollection)
	{
		return new AspectCollection<>(baseCollection);
	}

	public static <E, C extends Collection<E>> AspectCollection<E, C> decorate(final C baseCollection, final Object sender)
	{
		return new AspectCollection<>(baseCollection, sender);
	}


	@Override
	public int size()
	{
		return _baseCollection.size();
	}

	@Override
	public boolean isEmpty()
	{
		return _baseCollection.isEmpty();
	}

	@Override
	public boolean contains(final Object o)
	{
		return _baseCollection.contains(o);
	}

	@Override
	public Iterator<E> iterator()
	{
		return new Itr();
	}

	@Override
	public Object[] toArray()
	{
		return _baseCollection.toArray();
	}

	@Override
	public <T> T[] toArray(final T[] a)
	{
		return _baseCollection.toArray(a);
	}

	@Override
	public boolean add(final E e)
	{
		CollectionItemEvent<E, Collection<E>> ev = notifyBeforeElementAdded(this, e);
		boolean wasAdded = _baseCollection.add(e);
		if (wasAdded)
			notifyAfterElementAdded(ev);
		return wasAdded;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(final Object o)
	{
		if (contains(o)) {
			CollectionItemEvent<E, Collection<E>> ev = notifyBeforeElementRemoved(this, (E) o);
			boolean wasRemoved = _baseCollection.remove(o);
			if (wasRemoved)
				notifyAfterElementRemoved(ev);
			return wasRemoved;
		} else
			return false;
	}

	@Override
	public boolean containsAll(final Collection<?> c)
	{
		return _baseCollection.containsAll(c);
	}

	@Override
	public boolean addAll(final Collection<? extends E> c)
	{
		for (E item : c)
			notifyBeforeElementAdded(this, item);

		boolean wasAdded = false;
		for (E item : c) {
			if (_baseCollection.add(item)) {
				notifyAfterElementAdded(this, item);
				wasAdded = true;
			}
		}
		return wasAdded;
	}

	@Override
	public boolean removeAll(final Collection<?> c)
	{
		return batchRemove(c, false);
	}

	@Override
	public boolean retainAll(final Collection<?> c)
	{
		return batchRemove(c, true);
	}

	@Override
	public void clear()
	{
		if (!isEmpty()) {
			CollectionEvent<E, Collection<E>> ev = notifyBeforeCollectionCleared(this);
			_baseCollection.clear();
			notifyAfterCollectionCleared(ev);
		}
	}

	@Override
	public C getDecoratee()
	{
		return _baseCollection;
	}

	@Override
	public String toString()
	{
		return _baseCollection.toString();
	}

	@SuppressWarnings("unchecked")
	private boolean batchRemove(final Collection<?> c, boolean retain)
	{
		if (retain) {
			for (E item : _baseCollection)
				if (c.contains(item))
					notifyBeforeElementRemoved(this, item);
		} else {
			for (Object item : c)
				notifyBeforeElementRemoved(this, (E) item);
		}

		boolean wasRemoved = false;
		Iterator<E> iter = _baseCollection.iterator();
		while (iter.hasNext()) {
			E item = iter.next();
			if (c.contains(item) != retain) {
				wasRemoved = true;
				iter.remove();
				notifyAfterElementRemoved(this, item);
			}
		}
		return wasRemoved;
	}
	/**
	 *
	 * @author Peter Wullinger <java@dhke.de>
	 */
	public class Itr
		implements Iterator<E>
	{
		private final Iterator<E> _baseIterator;
		private E _current = null;

		public Itr()
		{
			_baseIterator = getDecoratee().iterator();
		}

		@Override
		public boolean hasNext()
		{
			return _baseIterator.hasNext();
		}

		@Override
		public E next()
		{
			_current = _baseIterator.next();
			return _current;
		}

		@Override
		public void remove()
		{
			CollectionItemEvent<E, Collection<E>> ev = notifyBeforeElementRemoved(AspectCollection.this, _current);
			_baseIterator.remove();
			notifyAfterElementRemoved(ev);
		}
	}
}
