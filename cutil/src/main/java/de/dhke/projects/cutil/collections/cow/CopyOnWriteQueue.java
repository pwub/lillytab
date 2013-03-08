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
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class CopyOnWriteQueue<E>
	extends GenericCopyOnWriteQueue<E, Queue<E>>
{
	protected CopyOnWriteQueue(final Queue<E> baseQueue, final ICollectionFactory<E, Queue<E>> factory)
	{
		super(baseQueue, factory);
	}
	
	public static <E> CopyOnWriteQueue<E> decorate(final Queue<E> baseQueue, final ICollectionFactory<E, Queue<E>> factory)
	{
		return new CopyOnWriteQueue<>(baseQueue, new ICollectionFactory<E, Queue<E>>() {

			@Override
			public Queue<E> getInstance()
			{
				return new LinkedList<>();
			}

			@Override
			public Queue<E> getInstance(final Queue<E> baseCollection)
			{
				return new LinkedList<>(baseCollection);
			}
		});
	}
}
