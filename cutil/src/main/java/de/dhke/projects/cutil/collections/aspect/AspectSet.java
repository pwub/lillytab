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

import java.util.Collection;
import java.util.Set;

/**
 * 
 * {@link AspectSet} is a specialized version of {@link AspectCollection} that
 * makes use of the special unique element property of a set to provide more
 * consistent behaviour with regard to item addition.
 * <p />
 * Wrapping a base collection into an {@link AspectCollection} will cause
 * a call to {@link ICollectionListener#beforeElementAdded(de.dhke.projects.cutil.collections.aspect.CollectionItemEvent) }
 * before every addition, even for already existing elements.
 * {@link AspectSet} modifies this behaviour, so that only elements
 * are added that are not yet already in the underlying set.
 * 
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class AspectSet<E, S extends Set<E>>
	extends AspectCollection<E, S>
	implements Set<E>
{
	public AspectSet(S baseSet)
	{
		super(baseSet);
	}

	public AspectSet(S baseSet, final Object sender)
	{
		super(baseSet, sender);
	}

	public static <E, S extends Set<E>> AspectSet<E, S> decorate(final S baseSet)
	{
		return new AspectSet<>(baseSet);
	}

	public static <E, S extends Set<E>> AspectSet<E, S> decorate(final S baseSet, final Object sender)
	{
		return new AspectSet<>(baseSet, sender);
	}

	@Override
	public boolean add(final E e)
	{
		return ((! contains(e)) && super.add(e));
	}

	@Override
	public boolean addAll(final Collection<? extends E> c)
	{
		for (E item: c) {
			if (! contains(item)) {
				notifyBeforeElementAdded(this, item);
			}
		}

		boolean wasAdded = false;
		for (E item: c) {
			if (! contains(item)) {
				wasAdded = getDecoratee().add(item);
				assert wasAdded;
				notifyAfterElementAdded(this, item);
				wasAdded = true;
			}
		}
		return wasAdded;
	}

}
