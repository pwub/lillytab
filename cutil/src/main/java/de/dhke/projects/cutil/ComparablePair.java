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
 * @param <First> Type of the first item
 * @param <Second> Type of the second item
 * @author Peter Wullinger <java@dhke.de>
 */
public class ComparablePair<First extends Comparable<? super First>, Second extends Comparable<? super Second>>
	extends Pair<First, Second>
	implements Comparable<Pair<First, Second>>
{
	private static final long serialVersionUID = -560624382504444006L;
	public ComparablePair(final First first, final Second second)
	{
		super(first, second);
	}

	@Override
	public int compareTo(Pair<First, Second> o)
	{
		int compare = getFirst().compareTo(o.getFirst());
		if (compare == 0)
			compare = getSecond().compareTo(o.getSecond());
		return compare;
	}

	public static <First extends Comparable<? super First>, Second extends Comparable<? super Second>> ComparablePair<First, Second> wrap(First first, Second second)
	{
		return new ComparablePair<>(first, second);
	}
}
