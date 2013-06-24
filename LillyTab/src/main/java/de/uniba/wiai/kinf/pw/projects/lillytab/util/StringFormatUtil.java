/**
 * (c) 2009-2013 Otto-Friedrich-University Bamberg
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
package de.uniba.wiai.kinf.pw.projects.lillytab.util;

import java.util.Collection;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class StringFormatUtil {

	public static String toString(final Iterable<?> coll, final IToStringFormatter formatter)
	{
		final StringBuilder sb = new StringBuilder();
		append(sb, coll, formatter);
		return sb.toString();
	}


	public static void append(final StringBuilder sb, final Iterable<?> coll, final IToStringFormatter formatter)
	{
		sb.append("[");
		boolean first = true;
		for (Object obj : coll) {
			if (!first) {
				sb.append(", ");
			} else {
				first = false;
			}
			if (obj instanceof Collection) {
				append(sb, (Collection<?>) obj, formatter);
			} else {
				formatter.append(sb, obj);
			}
		}
		sb.append("]");
	}


	private StringFormatUtil()
	{
	}
}
