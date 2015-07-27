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
package de.dhke.projects.cutil.collections.iterator;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.collections15.MultiMap;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class MultiMapEntryIterable<K, V>
	implements Iterable<Map.Entry<K, V>>
{
	private final Iterable<Map.Entry<K, Collection<V>>> _baseIterable;

	protected MultiMapEntryIterable(final MultiMap<K, V> baseMap)
	{
		_baseIterable = baseMap.entrySet();
	}


	protected MultiMapEntryIterable(final Iterable<Map.Entry<K, Collection<V>>> baseIterable)
	{
		_baseIterable = baseIterable;
	}

	public static <K, V> MultiMapEntryIterable<K, V> decorate(final MultiMap<K, V> baseMap)
	{
		return new MultiMapEntryIterable<>(baseMap.entrySet());
	}


	public static <K, V> MultiMapEntryIterable<K, V> decorate(final Iterable<Map.Entry<K, Collection<V>>> baseIterable)
	{
		return new MultiMapEntryIterable<>(baseIterable);
	}

	@Override
	public Iterator<Entry<K, V>> iterator()
	{
		return MultiMapEntryIterator.decorate(_baseIterable);
	}
}
