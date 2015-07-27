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
package de.dhke.projects.cutil.stringer;

import java.util.Collection;
import java.util.Map;
import org.apache.commons.collections15.MultiMap;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
@SupportsType({Collection.class, Map.class, MultiMap.class})
public class CollectionStringer
	extends AbstractAnnotationStringer {

	public CollectionStringer()
	{
	}


	@Override
	public void append(StringBuilder sb, Object obj, final IToStringConverter backStringer)
	{
		if (obj instanceof Collection) {
			boolean first = true;
			sb.append("[");
			for (Object subObj : (Collection) obj) {
				if (first) {
					first = false;
				} else
					sb.append(", ");
				backStringer.append(sb, subObj, backStringer);
			}
			sb.append("]");
		} else if (obj instanceof Map) {
			final Map<?, ?> map = (Map<?, ?>) obj;
			boolean first = true;
			sb.append("{");
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				if (first) {
					first = false;
				} else
					sb.append(", ");
				backStringer.append(sb, entry.getKey(), backStringer);
				sb.append(": ");
				backStringer.append(sb, entry.getValue(), backStringer);
			}
			sb.append("}");
		} else if (obj instanceof MultiMap) {
			final MultiMap<?, ?> mMap = (MultiMap<?, ?>) obj;
			backStringer.append(sb, mMap.map(), backStringer);
		} else
			sb.append(obj);
	}
}
