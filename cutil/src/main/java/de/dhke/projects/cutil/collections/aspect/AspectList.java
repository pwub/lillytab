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
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 *
 * @param <E> The element type
 * @param <L> The actual list type.
 * @author Peter Wullinger <java@dhke.de>
 */
public class AspectList<E, L extends List<E>>
	extends AspectCollectionNotifier<E, List<E>>
	implements List<E>, IDecorator<L>
{
	
	private final L _baseList;


	@Deprecated
	public AspectList(final L baseList)
	{
		super();
		_baseList = baseList;
	}

	public AspectList(final L baseList, final Object sender)
	{
		super(sender);
		_baseList = baseList;
	}


	@Deprecated
	public static <E, L extends List<E>> AspectList<E, L> decorate(final L list)
	{
		return new AspectList<>(list);
	}

	public static <E, L extends List<E>> AspectList<E, L> decorate(final L list, final Object sender)
	{
		return new AspectList<>(list, sender);
	}

	@Override
	public boolean addAll(int index,
		final Collection<? extends E> c)
	{
		for (E item : c) {
			add(index, item);
			++index;
		}
		return true;
	}


	@Override
	public E get(final int index)
	{
		return getDecoratee().get(index);
	}


	@Override
	public E set(final int index, final E element)
	{
		E oldItem = get(index);
		ListItemReplacedEvent<E, List<E>> ev = new ListItemReplacedEvent<E, List<E>>(this, this, index, oldItem, element);
		notifyBeforeElementReplaced(ev);
		oldItem = getDecoratee().set(index, element);
		notifyAfterElementReplaced(ev);
		return oldItem;
	}


	@Override
	public void add(final int index, final E element)
	{
		ListItemEvent<E, List<E>> ev = new ListItemEvent<>(this, null, element, index);
		notifyBeforeElementAdded(ev);
		getDecoratee().add(index, element);
		notifyAfterElementAdded(ev);
	}


	@Override
	public E remove(final int index)
	{
		E item = get(index);
		ListItemEvent<E, List<E>> ev = new ListItemEvent<E, List<E>>(this, item, index);
		notifyBeforeElementRemoved(ev);
		item = getDecoratee().remove(index);
		notifyAfterElementRemoved(ev);
		return item;
	}


	@Override
	public int indexOf(final Object o)
	{
		return getDecoratee().indexOf(o);
	}


	@Override
	public int lastIndexOf(final Object o)
	{
		return getDecoratee().indexOf(o);
	}


	@Override
	public ListIterator<E> listIterator()
	{
		return new Itr();
	}


	@Override
	public ListIterator<E> listIterator(final int index)
	{
		return new Itr(index);
	}


	@Override
	public List<E> subList(final int fromIndex, final int toIndex)
	{
		return new AspectList<>(getDecoratee().subList(fromIndex, toIndex), getSender());
	}


	@Override
	public int size()
	{
		return _baseList.size();
	}


	@Override
	public boolean isEmpty()
	{
		return _baseList.isEmpty();
	}


	@Override
	public boolean contains(final Object o)
	{
		return _baseList.contains(o);
	}


	@Override
	public Iterator<E> iterator()
	{
		return new Itr();
	}


	@Override
	public Object[] toArray()
	{
		return _baseList.toArray();
	}


	@Override
	public <T> T[] toArray(final T[] a)
	{
		return _baseList.toArray(a);
	}


	@Override
	public boolean add(final E e)
	{
		CollectionItemEvent<E, List<E>> ev = new CollectionItemEvent<E, List<E>>(this, e);
		notifyBeforeElementAdded(ev);
		boolean wasAdded = _baseList.add(e);
		if (wasAdded)
			notifyAfterElementAdded(ev);
		return wasAdded;
	}


	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(final Object o)
	{
		boolean wasRemoved = false;
		int index = indexOf(o);
		if (index != -1) {
			ListItemEvent<E, List<E>> ev = new ListItemEvent<E, List<E>>(this, (E) o, index);
			notifyBeforeElementRemoved(ev);
			try {
				E item = _baseList.remove(index);
				ev = new ListItemEvent<E, List<E>>(this, item, index);
				notifyAfterElementRemoved(ev);
				wasRemoved = true;
			} catch (IndexOutOfBoundsException ex) {
				wasRemoved = false;
			}
		}
		return wasRemoved;
	}


	@Override
	public boolean containsAll(final Collection<?> c)
	{
		return _baseList.containsAll(c);
	}


	@Override
	public boolean addAll(final Collection<? extends E> c)
	{
		for (E item: c) {
			CollectionItemEvent<E, List<E>> ev = new CollectionItemEvent<E, List<E>>(this, this, item);
			notifyBeforeElementAdded(ev);
		}

		boolean wasAdded = false;
		for (E item: c) {
			if (_baseList.add(item)) {
				CollectionItemEvent<E, List<E>> ev = new CollectionItemEvent<E, List<E>>(this, this, item);
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
		if (! isEmpty()) {
			CollectionEvent<E, List<E>> ev = new CollectionEvent<E, List<E>>(getSender(), this);
			notifyBeforeCollectionCleared(ev);
			_baseList.clear();
			notifyAfterCollectionCleared(ev);
		}
	}

	@Override
	public L getDecoratee()
	{
		return _baseList;
	}


	@Override
	public String toString()
	{
		return _baseList.toString();
	}


	@SuppressWarnings("unchecked")
	private boolean batchRemove(final Collection<?> c, final boolean retain)
	{
		if (retain) {
			for (E item : _baseList)
				if (c.contains(item))
					notifyBeforeElementRemoved(this, item);
		} else {
			for (Object item: c)
				notifyBeforeElementRemoved(this, (E)item);
		}

		boolean wasRemoved = false;
		Iterator<E> iter = _baseList.iterator();
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

	/// <editor-fold defaultstate="collapsed" desc="Iterator">
	public class Itr
		implements Iterator<E>, ListIterator<E>
	{
		private final ListIterator<E> _baseIterator;
		private IterationDirection _lastDirection = IterationDirection.NONE;


		public Itr()
		{
			_baseIterator = getDecoratee().listIterator();
		}


		public Itr(final int index)
		{
			_baseIterator = getDecoratee().listIterator(index);
		}


		@Override
		public boolean hasNext()
		{
			return _baseIterator.hasNext();
		}


		@Override
		public E next()
		{
			E item = _baseIterator.next();
			_lastDirection = IterationDirection.FORWARD;
			return item;
		}


		@Override
		public void remove()
		{
			E item = get(getCurrentIndex());
			ListItemEvent<E, List<E>> ev = new ListItemEvent<E, List<E>>(AspectList.this, item, getCurrentIndex());
			notifyBeforeElementRemoved(ev);
			_baseIterator.remove();
			_lastDirection = IterationDirection.NONE;
			notifyAfterElementRemoved(ev);
		}


		@Override
		public boolean hasPrevious()
		{
			return _baseIterator.hasPrevious();
		}


		@Override
		public E previous()
		{
			E item = _baseIterator.previous();
			_lastDirection = IterationDirection.FORWARD;
			return item;
		}


		@Override
		public int nextIndex()
		{
			return _baseIterator.nextIndex();
		}


		@Override
		public int previousIndex()
		{
			return _baseIterator.previousIndex();
		}


		@Override
		public void set(final E e)
		{
			E oldItem = get(getCurrentIndex());
			ListItemReplacedEvent<E, List<E>> ev = new ListItemReplacedEvent<E, List<E>>(AspectList.this, getCurrentIndex(), oldItem, e);
			notifyBeforeElementReplaced(ev);
			_baseIterator.set(e);
			notifyAfterElementReplaced(ev);
		}


		@Override
		public void add(final E e)
		{
			CollectionItemEvent<E, List<E>> ev = new CollectionItemEvent<E, List<E>>(AspectList.this, e);
			notifyBeforeElementAdded(ev);
			_baseIterator.add(e);
			ev = new ListItemEvent<E, List<E>>(AspectList.this, e, previousIndex());
			notifyAfterElementAdded(ev);
		}


		/**
		 * @return The position of the "current" item with regard
		 * to the last all to {@link #next()} or {@link #previous() }.
		 */
		private int getCurrentIndex()
		{
			switch (_lastDirection) {
				case BACKWARD:
					return nextIndex();
				case FORWARD:
					return previousIndex();
				default:
					throw new NoSuchElementException("Iterator position unknown");
			}
		}
	}
	/// </editor-fold>
	private enum IterationDirection
	{
		FORWARD,
		NONE,
		BACKWARD
	};
}
