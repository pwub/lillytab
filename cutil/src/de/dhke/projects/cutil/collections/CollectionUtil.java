/**
 * (c) 2009-2012 Peter Wullinger
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
package de.dhke.projects.cutil.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.collections15.Transformer;


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class CollectionUtil
{
	private CollectionUtil()
	{
	}

	/**
	 * Return {@literal true}, if {@literal sup} contains at
	 * least one item from {@literal parts}.
	 *
	 * @param <E>
	 * @param sup
	 * @param parts
	 * @return {@literal true} if at least one item of {@literal parts} was found in {@literal sup}.
	 */
	public static <E> boolean containsOne(final Collection<E> sup, final Collection<? extends E> parts)
	{
		for (E part : parts) {
			if (sup.contains(part))
				return true;
		}

		return false;
	}

	/**
	 * Pick all elements from the iterable {@literal items} and
	 * convert their string representation into a {@literal separator}-separated
	 * single string.
	 *
	 * @param items The source item collection
	 * @param separator	The separator string between items
	 * @return A String containing the concatenated string representations of the elements in {@literal items}.
	 */
	public static String join(final Iterable<?> items, final String separator)
	{
		final StringBuilder sequence = new StringBuilder();
		final Iterator<?> iter = items.iterator();
		if (iter.hasNext()) {
			final String first = iter.next().toString();
			sequence.append(first);
			while (iter.hasNext()) {
				sequence.append(separator);
				final String next = iter.next().toString();
				sequence.append(next);
			}
		}
		return sequence.toString();
	}

	public static String deepToString(final Collection<?> collection)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("[");
		final Iterator<?> iter = collection.iterator();		
		if (iter.hasNext())
			sb.append(iter.next());
		while (iter.hasNext()) {
			sb.append(", ");
			sb.append(iter.next());
		}
		sb.append("]");
		return sb.toString();
	}

	public static String recursiveDeepToString(final Collection<?> collection)
	{		
		final StringBuilder sb = new StringBuilder();
		sb.append("[");
		final Iterator<?> iter = collection.iterator();		
		if (iter.hasNext()) {
			final Object item = iter.next();
			if (item instanceof Collection) {
				final Collection<?> subCollection = (Collection<?>)item;
				sb.append(recursiveDeepToString(subCollection));
			} else
				sb.append(item);
		}
		while (iter.hasNext()) {
			final Object item = iter.next();
			sb.append(", ");
			if (item instanceof Collection) {
				final Collection<?> subCollection = (Collection<?>)item;
				sb.append(recursiveDeepToString(subCollection));
			} else
				sb.append(item);
		}
		sb.append("]");
		return sb.toString();
	}

	public static <T> String transformedToString(final Collection<? extends T> collection, final Transformer<T, ?> stringer)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("[");
		final Iterator<? extends T> iter = collection.iterator();		
		if (iter.hasNext())
			sb.append(stringer.transform(iter.next()));
		while (iter.hasNext()) {
			sb.append(", ");
			sb.append(stringer.transform(iter.next()));
		}
		sb.append("]");
		return sb.toString();
	}
	
	public static <T, S extends Collection<T>> S flatten(final S targetCollection, final Collection<? extends Collection<? extends T>> setOfSets)
	{
		for (Collection<? extends T> innerSet: setOfSets)
			targetCollection.addAll(innerSet);
		return targetCollection;
	}
	
}
