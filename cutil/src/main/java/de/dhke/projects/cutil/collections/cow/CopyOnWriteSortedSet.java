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
package de.dhke.projects.cutil.collections.cow;

import de.dhke.projects.cutil.collections.factories.ICollectionFactory;
import de.dhke.projects.cutil.collections.factories.TreeSortedSetFactory;
import java.util.SortedSet;


/**
 *
 * @param <E> 
 * @author Peter Wullinger <java@dhke.de>
 */
public class CopyOnWriteSortedSet<E>
	extends GenericCopyOnWriteSortedSet<E, SortedSet<E>>
	implements Cloneable
{
	protected CopyOnWriteSortedSet(final SortedSet<E> baseSet, final ICollectionFactory<E, SortedSet<E>> factory)
	{
		super(baseSet, factory);
	}

	protected CopyOnWriteSortedSet(final SortedSet<E> baseSet)
	{
		super(baseSet, new TreeSortedSetFactory<E>());
	}

	public static <E> CopyOnWriteSortedSet<E> decorate(final SortedSet<E> baseSet, final ICollectionFactory<E, SortedSet<E>> factory)
	{
		return new CopyOnWriteSortedSet<>(baseSet, factory);
	}

	public static <E> CopyOnWriteSortedSet<E> decorate(final SortedSet<E> baseSet)
	{
		return new CopyOnWriteSortedSet<>(baseSet);
	}

	@Override
	public CopyOnWriteSortedSet<E> clone()
	{
		final CopyOnWriteSortedSet<E> klone = decorate(getDecoratee(), getFactory());
		resetWasCopied();
		return klone;
	}
}
