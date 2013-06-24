/**
 * (c) 2009-2013 Peter Wullinger
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
 * @author Peter Wullinger <java@dhke.de>
 */
public class GenericCopyOnWriteSortedSet<E, S extends SortedSet<E>>
	extends GenericCopyOnWriteSet<E, S>
	implements SortedSet<E>
{
	protected GenericCopyOnWriteSortedSet(final S baseSet, final ICollectionFactory<E, S> factory)
	{
		super(baseSet, factory);
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
	public E last()
	{
		return getDecoratee().last();
	}

	@Override
	public String toString()
	{
		return getDecoratee().toString();
	}


}
