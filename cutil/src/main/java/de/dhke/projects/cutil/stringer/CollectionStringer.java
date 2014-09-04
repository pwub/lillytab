/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
