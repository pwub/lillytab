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
package de.dhke.projects.cutil.collections.queue;

import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.TreeSet;


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class SortedUniqueQueue<T extends Comparable<? super T>>
	extends TreeSet<T>
	implements Queue<T>
{
	@Override
	public boolean offer(T e)
	{
		return add(e);
	}

	@Override
	public T remove()
	{
		if (isEmpty())
			throw new NoSuchElementException("Queue is empty");
		else {
			final T item = first();
			remove(item);
			return item;
		}
	}

	@Override
	public T poll()
	{
		if (isEmpty())
			return null;
		else {
			final T item = first();
			remove(item);
			return item;
		}
	}

	@Override
	public T element()
	{
		if (isEmpty())
			throw new NoSuchElementException("Queue is empty");
		else
			return first();
	}

	@Override
	public T peek()
	{
		if (isEmpty())
			return null;
		else
			return first();
	}
}
