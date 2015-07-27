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

/**
 *
 * @param <T> 
 * @author Peter Wullinger <java@dhke.de>
 */
public class SortedPair<T extends Comparable<? super T>>
	extends ComparablePair<T, T>
{
	private static final long serialVersionUID = 5809721596536143002L;
	protected SortedPair(final T first, final T second)
	{
		super(first, second);
		assert first.compareTo(second) > 0;
	}

	public static <T extends Comparable<? super T>> SortedPair<T> wrapSorted(final T first, final T second)
	{
		if (first.compareTo(second) > 0)
			return new SortedPair<>(first, second);
		else
			return new SortedPair<>(second, first);
	}
}
