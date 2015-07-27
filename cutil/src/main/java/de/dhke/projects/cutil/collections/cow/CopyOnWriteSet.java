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
import java.util.Set;

/**
 *
 * @param <E>
 * @param <S>
 *            <p/>
 * @author Peter Wullinger <java@dhke.de>
 */
public class CopyOnWriteSet<E>
	extends CopyOnWriteCollection<E>
	implements Set<E> {

	protected CopyOnWriteSet(final Set<E> wrappedSet, final ICollectionFactory<E, ? extends Set<E>> factory)
	{
		super(wrappedSet, factory);
	}


	@Override
	public Set<E> getDecoratee()
	{
		return (Set<E>) super.getDecoratee();
	}


	@Override
	@SuppressWarnings("unchecked")
	protected ICollectionFactory<E, ? extends Set<E>> getFactory()
	{
		return (ICollectionFactory<E, ? extends Set<E>>) super.getFactory(); //To change body of generated methods, choose Tools | Templates.
	}


	@Override
	protected CopyOnWriteSet<E> clone()
		throws CloneNotSupportedException
	{
		final CopyOnWriteSet<E> klone = new CopyOnWriteSet<>(getDecoratee(), getFactory());
		resetWasCopied();
		return klone;
	}
}
