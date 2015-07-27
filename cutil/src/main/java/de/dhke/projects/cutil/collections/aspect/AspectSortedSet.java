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

import java.util.Comparator;
import java.util.SortedSet;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class AspectSortedSet<E>
	extends AspectSet<E, SortedSet<E>>
	implements SortedSet<E>
{
	protected AspectSortedSet(final SortedSet<E> baseSet)
	{
		super(baseSet);
	}

	protected AspectSortedSet(final SortedSet<E> baseSet, final Object sender)
	{
		super(baseSet, sender);
	}

	public static <E> AspectSortedSet<E> decorate(final SortedSet<E> baseSet)
	{
		return new AspectSortedSet<>(baseSet);
	}

	public static <E> AspectSortedSet<E> decorate(final SortedSet<E> baseSet, final Object sender)
	{
		return new AspectSortedSet<>(baseSet, sender);
	}


	@Override
	public Comparator<? super E> comparator()
	{
		return getDecoratee().comparator();
	}

	@Override
	public SortedSet<E> subSet(final E fromElement, final E toElement)
	{
		AspectSortedSet<E> subAspectSet = AspectSortedSet.decorate(getDecoratee().subSet(fromElement, toElement));
		subAspectSet.setListeners(getListeners());
		return subAspectSet;
	}

	@Override
	public SortedSet<E> headSet(final E toElement)
	{
		AspectSortedSet<E> subAspectSet = AspectSortedSet.decorate(getDecoratee().headSet(toElement));
		subAspectSet.setListeners(getListeners());
		return subAspectSet;
	}

	@Override
	public SortedSet<E> tailSet(final E fromElement)
	{
		AspectSortedSet<E> subAspectSet = AspectSortedSet.decorate(getDecoratee().tailSet(fromElement));
		subAspectSet.setListeners(getListeners());
		return subAspectSet;
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
}
