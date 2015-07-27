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

import de.dhke.projects.cutil.collections.factories.ICollectionFactory;
import java.util.Comparator;
import java.util.SortedSet;

/**
 *
 * @param <E>
 * @param <S>
 *            <p/>
 * @author Peter Wullinger <java@dhke.de>
 */
public class CopyOnWriteSortedSet<E extends Comparable<? super E>>
	extends CopyOnWriteSet<E>
	implements SortedSet<E> {

	protected CopyOnWriteSortedSet(final SortedSet<E> baseSet,
								   final ICollectionFactory<E, ? extends SortedSet<E>> factory)
	{
		super(baseSet, factory);
	}


	public static <E extends Comparable<? super E>> CopyOnWriteSortedSet<E> decorate(final SortedSet<E> baseSet,
																					 final ICollectionFactory<E, ? extends SortedSet<E>> factory)
	{
		return new CopyOnWriteSortedSet<>(baseSet, factory);
	}


	@Override
	public Comparator<? super E> comparator()
	{
		return getDecoratee().comparator();
	}


	@Override
	public SortedSet<E> subSet(final E fromElement, final E toElement)
	{
		return new CopyOnWriteSortedSubSet<>(this, fromElement, toElement);
	}


	@Override
	public SortedSet<E> headSet(final E toElement)
	{
		return new CopyOnWriteSortedSubSet<>(this, null, toElement);
	}


	@Override
	public SortedSet<E> tailSet(final E fromElement)
	{
		return new CopyOnWriteSortedSubSet<>(this, fromElement, null);
	}


	@Override
	public E first()
	{
		return getDecoratee().first();
	}


	@Override
	public SortedSet<E> getDecoratee()
	{
		return (SortedSet<E>) super.getDecoratee();
	}


	@Override
	public E last()
	{
		return getDecoratee().last();
	}


	@Override
	public String toString()
	{
		return getDecoratee().toString();
	}


	@Override
	public CopyOnWriteSortedSet<E> clone()
	{
		final CopyOnWriteSortedSet<E> klone = new CopyOnWriteSortedSet<>(getDecoratee(), getFactory());
		resetWasCopied();
		return klone;
	}


	@Override
	@SuppressWarnings("unchecked")
	protected ICollectionFactory<E, ? extends SortedSet<E>> getFactory()
	{
		return (ICollectionFactory<E, ? extends SortedSet<E>>) super.getFactory();
	}
}
