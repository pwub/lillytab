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

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class CopyOnWriteSortedSubSet<E extends Comparable<? super E>>
	implements SortedSet<E> {

	private CopyOnWriteSortedSet<E> _cowSet;
	private SortedSet<E> _originalSet = null;
	private SortedSet<E> _originalSubSet = null;
	private E _from = null;
	private E _to = null;


	protected CopyOnWriteSortedSubSet(final CopyOnWriteSortedSet<E> cowSet, final E from, final E to)
	{
		_cowSet = cowSet;
		_from = from;
		_to = to;
	}


	@Override
	public Comparator<? super E> comparator()
	{
		return _cowSet.getDecoratee().comparator();
	}


	@Override
	public SortedSet<E> subSet(final E fromElement, final E toElement)
	{
		return new CopyOnWriteSortedSubSet<>(_cowSet, fromElement, toElement);
	}


	@Override
	public SortedSet<E> headSet(final E toElement)
	{
		return new CopyOnWriteSortedSubSet<>(_cowSet, null, toElement);
	}


	@Override
	public SortedSet<E> tailSet(final E fromElement)
	{
		return new CopyOnWriteSortedSubSet<>(_cowSet, fromElement, null);
	}


	@Override
	public E first()
	{
		return getOriginalSubSet().first();
	}


	@Override
	public E last()
	{
		return getOriginalSubSet().last();
	}


	@Override
	public int size()
	{
		return getOriginalSubSet().size();
	}


	@Override
	public boolean isEmpty()
	{
		return getOriginalSubSet().isEmpty();
	}


	@Override
	public boolean contains(final Object o)
	{
		return getOriginalSubSet().contains(o);
	}


	@Override
	public Iterator<E> iterator()
	{
		return new Itr();
	}


	@Override
	public Object[] toArray()
	{
		return getOriginalSubSet().toArray();
	}


	@Override
	public <T> T[] toArray(final T[] a)
	{
		return getOriginalSubSet().toArray(a);
	}


	@Override
	public  boolean add(final E e)
	{
		_cowSet.copy();
		return getOriginalSubSet().add(e);
	}


	@Override
	public boolean remove(final Object o)
	{
		_cowSet.copy();
		return getOriginalSubSet().remove(o);
	}


	@Override
	public boolean containsAll(final Collection<?> c)
	{
		return getOriginalSubSet().containsAll(c);
	}


	@Override
	public boolean addAll(final Collection<? extends E> c)
	{
		_cowSet.copy();
		return getOriginalSubSet().addAll(c);
	}


	@Override
	public boolean retainAll(final Collection<?> c)
	{
		_cowSet.copy();
		return getOriginalSubSet().retainAll(c);
	}


	@Override
	public boolean removeAll(final Collection<?> c)
	{
		_cowSet.copy();
		return getOriginalSubSet().removeAll(c);
	}


	@Override
	public void clear()
	{
		_cowSet.copy();
		getOriginalSubSet().clear();
	}


		private SortedSet<E> getOriginalSubSet()
	{
		if ((_cowSet.getDecoratee() != _originalSet) || (_originalSubSet == null)) {
			_originalSet = _cowSet.getDecoratee();
			assert ((_from != null) || (_to != null));
			if (_from == null)
				_originalSubSet = _cowSet.getDecoratee().headSet(_to);
			else if (_to == null) {
				_originalSubSet = _cowSet.getDecoratee().tailSet(_from);
			} else
				_originalSubSet = _cowSet.getDecoratee().subSet(_from, _to);
		}
		return _originalSubSet;
	}

	/// <editor-fold defaultstate="collapsed" desc="class Itr">
	private class Itr
		implements Iterator<E> {

		private final Iterator<E> _baseIter;
		private boolean _collectionAlreadyCopied = true;


		Itr()
		{
			_baseIter = getOriginalSubSet().iterator();
			_collectionAlreadyCopied = _cowSet.isWasCopied();
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
		 * {@link GenericCopyOnWriteCollection} is not supported.
		 * <p/>
		 */
		@Override
		public void remove()
		{
			/**
			 * I cannot think of a proper way to fully support this method.
			 * If a COW list has not been copied yet, a remove would chang thee underlying collection.
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
	/// </editor-fold>
}
