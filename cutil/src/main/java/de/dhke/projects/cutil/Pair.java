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

import java.io.Serializable;

/**
 *
 * @param <First> 
 * @param <Second> 
 * @author Peter Wullinger <java@dhke.de>
 */
public class Pair<First, Second>
	implements Serializable
{
	private static final long serialVersionUID = 2824595808898153855L;
	private final First _first;
	private final Second _second;

	public Pair(First first, Second second)
	{
		_first = first;
		_second = second;
	}

	public First getFirst()
	{
		return _first;
	}

	public Second getSecond()
	{
		return _second;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj instanceof Pair) {
			final Pair<?, ?> other = (Pair<?, ?>)obj;
			return getFirst().equals(other.getFirst()) && getSecond().equals(other.getSecond());
		} else
			return false;
	}

	@Override
	public int hashCode()
	{
		return 154 + _first.hashCode() + 53 * _second.hashCode();
	}

	public static <First, Second> Pair<First, Second> wrap(First first, Second second)
	{
		return new Pair<>(first, second);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder("<");
		sb.append(_first);
		sb.append(", ");
		sb.append(_second);
		sb.append(">");
		return sb.toString();
	}


}
