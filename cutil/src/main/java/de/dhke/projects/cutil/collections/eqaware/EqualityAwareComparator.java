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
package de.dhke.projects.cutil.collections.eqaware;

import de.dhke.projects.cutil.IDecorator;
import java.util.Comparator;


final class EqualityAwareComparator<T>
	implements Comparator<T>, IDecorator<Comparator<? super T>>
{
	private final Comparator<? super T> _baseComparator;
  EqualityAwareComparator(final Comparator<? super T> baseComparator)
	{
		_baseComparator = baseComparator;
	}

	public static <T> EqualityAwareComparator<T> decorate(final Comparator<T> baseComparator)
	{
		return new EqualityAwareComparator<>(baseComparator);
	}

	@Override
	public int compare(T o1, T o2)
	{
		int compare = _baseComparator.compare(o1, o2);
		if ((compare == 0) && (! o1.equals(o2)))
			return System.identityHashCode(o2) - System.identityHashCode(o1);
		else
			return compare;
	}

	@Override
	public Comparator<? super T> getDecoratee()
	{
		return _baseComparator;
	}
}
