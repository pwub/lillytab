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
package de.dhke.projects.cutil.collections.map;

import de.dhke.projects.cutil.Pair;
import java.util.HashMap;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class PairHashMap<First, Second, Tag>
	extends PairMap<First, Second, Tag> {

	public PairHashMap()
	{
		super(new HashMap<Pair<First, Second>, Tag>());
	}


	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("{");
		boolean first = true;
		for (Pair<First, Second> keyPair : keySet()) {
			if (first)
				first = false;
			else
				sb.append(", ");
			sb.append(keyPair);
			sb.append(": ");
			sb.append(get(keyPair));
		}
		sb.append("}");
		return sb.toString();
	}
}
