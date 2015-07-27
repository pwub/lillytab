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
 * @param <First> Type of the first item
 * @param <Second> Type of the second item
 * @author Peter Wullinger <java@dhke.de>
 */
public class PairIterable<First, Second>
	implements Iterable<Pair<First, Second>> {

	private final Iterable<First> _firstIterable;
	private final Iterable<Second> _secondIterable;


	protected PairIterable(final Iterable<First> fi, final Iterable<Second> si)
	{
		_firstIterable = fi;
		_secondIterable = si;
	}


	public static <First, Second> PairIterable<First, Second> wrap(final Iterable<First> fi, final Iterable<Second> si)
	{
		return new PairIterable<>(fi, si);
	}


	@Override
	public Iterator<Pair<First, Second>> iterator()
	{
		return PairIterator.wrap(_firstIterable, _secondIterable);
	}
}
