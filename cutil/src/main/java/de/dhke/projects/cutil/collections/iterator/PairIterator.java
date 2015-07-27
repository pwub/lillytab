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
package de.dhke.projects.cutil.collections.iterator;

import de.dhke.projects.cutil.Pair;
import java.util.Iterator;

/**
 *
 * @param <First> 
 * @param <Second> 
 * @author Peter Wullinger <java@dhke.de>
 */
public class PairIterator<First, Second>
	implements Iterator<Pair<First, Second>>
{
	private final Iterator<? extends First> _firstIterator;
	private final Iterator<? extends Second> _secondIterator;

	protected PairIterator(final Iterable<? extends First> fi, final Iterable<? extends Second> si)
	{
		this(fi.iterator(), si.iterator());
	}

	protected PairIterator(final Iterator<? extends First> fi, final Iterator<? extends Second> si)
	{
		_firstIterator = fi;
		_secondIterator = si;
	}

	public static <First, Second> PairIterator<First, Second> wrap(final Iterable<First> fi, final Iterable<Second> si)
	{
		return new PairIterator<>(fi, si);
	}

	public static <First, Second> PairIterator<First, Second> wrap(final Iterator<First> fi, final Iterator<Second> si)
	{
		return new PairIterator<>(fi, si);
	}
	
	@Override
	public boolean hasNext()
	{
		return _firstIterator.hasNext() && _secondIterator.hasNext();
	}

	@Override
	public Pair<First, Second> next()
	{
		return Pair.wrap(_firstIterator.next(), _secondIterator.next());
	}

	@Override
	public void remove()
	{
		_firstIterator.remove();
		_secondIterator.remove();
	}
}
