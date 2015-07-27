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
package de.dhke.projects.cutil.collections.frozen;

import java.util.Iterator;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public final class FrozenIterator<T>
	implements Iterator<T>
{
	private Iterator<? extends T> _baseIterator;
	
	protected FrozenIterator(final Iterator<? extends T> baseIterator)
	{
		_baseIterator = baseIterator;
	}
	
	public static <T> FrozenIterator<T> decorate(final Iterator<? extends T> baseIterator)
	{
		return new FrozenIterator<>(baseIterator);
	}

	@Override
	public boolean hasNext()
	{
		return _baseIterator.hasNext();
	}


	@Override
	public T next()
	{
		return _baseIterator.next();
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException("Cannot remove from FrozenIterator");
	}

}
