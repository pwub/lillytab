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

import de.dhke.projects.cutil.Pair;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.collections15.MultiMap;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class MultiMapPairIterable<K, V>
	implements Iterable<Pair<K, V>>
{
	private final Iterable<Map.Entry<K, V>> _baseIterable;

	protected MultiMapPairIterable(final MultiMap<K, V> baseMap)
	{
		_baseIterable = MultiMapEntryIterable.decorate(baseMap);
	}


	protected MultiMapPairIterable(final Iterable<Map.Entry<K, Collection<V>>> baseIterable)
	{
		_baseIterable = MultiMapEntryIterable.decorate(baseIterable);
	}

	public static <K, V> MultiMapPairIterable<K, V> decorate(final MultiMap<K, V> baseMap)
	{
		return new MultiMapPairIterable<>(baseMap.entrySet());
	}


	public static <K, V> MultiMapPairIterable<K, V> decorate(final Iterable<Map.Entry<K, Collection<V>>> baseIterable)
	{
		return new MultiMapPairIterable<>(baseIterable);
	}

	@Override
	public Iterator<Pair<K, V>> iterator()
	{
		return MultiMapPairIterator.decorate(_baseIterable.iterator());
	}
}
