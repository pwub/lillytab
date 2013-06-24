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
package de.dhke.projects.cutil.collections.queue;

import java.util.EmptyStackException;
import java.util.Queue;
import java.util.Stack;

/**
 *
 * @param <E>
 * @author Peter Wullinger <java@dhke.de>
 */
public class StackQueue<E>
	extends Stack<E>
	implements Queue<E>
{
	static final long serialVersionUID = -4126903274780274845L;

	public boolean offer(E e)
	{
		push(e);
		return true;
	}

	public E remove()
	{
		return pop();
	}

	public E poll()
	{
		if (isEmpty())
			return null;
		else
			return pop();
	}

	public E element()
	{
		if (isEmpty())
			throw new EmptyStackException();
		else
			return peek();
	}
}
