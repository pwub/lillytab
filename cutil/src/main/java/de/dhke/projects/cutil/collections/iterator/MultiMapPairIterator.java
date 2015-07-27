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
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.collections15.MultiMap;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class MultiMapPairIterator<K, V>
	implements Iterator<Pair<K, V>>
{
	private final Iterator<Map.Entry<K, V>> _entryIter;
	
	protected MultiMapPairIterator(final  Iterator<Map.Entry<K, V>> entryIter)
	{
		_entryIter = entryIter;
	}
	
	protected MultiMapPairIterator(final MultiMap<K, V> map)
	{
		this(MultiMapEntryIterator.decorate(map));
	}
	
	public static <K, V> MultiMapPairIterator<K, V> decorate(final Iterator<Map.Entry<K, V>> entryIter)
	{
		return new MultiMapPairIterator<>(entryIter);			
	}
	
	public static <K, V> MultiMapPairIterator<K, V> decorate(final MultiMap<K, V> map)
	{
		return new MultiMapPairIterator<>(map);			
	}


	@Override
	public boolean hasNext()
	{
		return _entryIter.hasNext();
	}


	@Override
	public Pair<K, V> next()
	{
		final Map.Entry<K, V> entry = _entryIter.next();
		return Pair.wrap(entry.getKey(), entry.getValue());
	}


	@Override
	public void remove()
	{
		_entryIter.remove();
	}
	
	
}
