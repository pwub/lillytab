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

import de.dhke.projects.cutil.collections.factories.ArrayListCollectionFactory;
import de.dhke.projects.cutil.collections.factories.ICollectionFactory;
import java.util.Collection;


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class CopyOnWriteCollection<E>
	extends GenericCopyOnWriteCollection<E, Collection<E>>
	implements Cloneable
{
	protected CopyOnWriteCollection(final Collection<E> baseCollection)
	{
		super(baseCollection, new ArrayListCollectionFactory<E>());
	}

	protected CopyOnWriteCollection(final Collection<E> baseCollection, final ICollectionFactory<E, Collection<E>> factory)
	{
		super(baseCollection, factory);
	}

	public static <E> CopyOnWriteCollection<E> decorate(final Collection<E> baseCollection, final ICollectionFactory<E, Collection<E>> factory)
	{
		return new CopyOnWriteCollection<E>(baseCollection, factory);
	}

	public static <E> CopyOnWriteCollection<E> decorate(final Collection<E> baseCollection)
	{
		return new CopyOnWriteCollection<E>(baseCollection);
	}

	@Override
	public CopyOnWriteCollection<E> clone()
	{
		final CopyOnWriteCollection<E> klone = decorate(getDecoratee(), getFactory());
		this.resetWasCopied();
		return klone;
	}
}
