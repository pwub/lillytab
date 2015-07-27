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
package de.dhke.projects.cutil;

import java.util.Comparator;

/**
 *
 * @param <T>
 * @author Peter Wullinger <java@dhke.de>
 */
public class InverseComparator<T>
	implements Comparator<T>, IDecorator<Comparator<? super T>>
{
	private final Comparator<? super T> _baseComparator;

	public  InverseComparator(final Comparator<? super T> baseComparator)
	{
		_baseComparator = baseComparator;
	}

	public static <T> InverseComparator<T> decorate(final Comparator<? super T> baseComparator)
	{
		return new InverseComparator<>(baseComparator);
	}

	@Override
	public int compare(T o1, T o2)
	{
		return _baseComparator.compare(o2, o1);
	}

	@Override
	public Comparator<? super T> getDecoratee()
	{
		return _baseComparator;
	}

}
