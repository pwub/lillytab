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
package de.dhke.projects.cutil.collections.iterator;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.keyvalue.DefaultMapEntry;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class MultiMapItemIterator<K, V>
	implements Iterator<Map.Entry<K, V>> {

	private final Iterator<Map.Entry<K, Collection<V>>> _baseIter;
	private K _currentKey;
	/* the current value collection. {@literal null} if we reached the end of the map */
	private Iterator<V> _currentValueCollection;
	private Map.Entry<K, V> _currentEntry;


	protected MultiMapItemIterator(final MultiMap<K, V> baseMap)
	{
		this(baseMap.entrySet());
	}


	protected MultiMapItemIterator(final Iterable<Map.Entry<K, Collection<V>>> baseIterable)
	{
		_baseIter = baseIterable.iterator();
		/* advance to first value collection */
		if (_baseIter.hasNext()) {
			final Map.Entry<K, Collection<V>> entry = _baseIter.next();
			_currentKey = entry.getKey();
			_currentValueCollection = entry.getValue().iterator();
		}
		advance();
	}


	private Map.Entry<K, V> advance()
	{
		Map.Entry<K, V> currentEntry = _currentEntry;
		if (_currentValueCollection != null) {
			assert _currentKey != null;
			if (_currentValueCollection.hasNext()) {
				final V currentValue = _currentValueCollection.next();
				_currentEntry = new DefaultMapEntry<K, V>(_currentKey, currentValue);
			} else {
				_currentEntry = null;
				while ((_baseIter.hasNext()) && (_currentValueCollection != null) && (_currentEntry == null)) {
					final Map.Entry<K, Collection<V>> nextMapEntry = _baseIter.next();
					_currentKey = nextMapEntry.getKey();
					if (nextMapEntry.getValue() != null) {
						_currentValueCollection = nextMapEntry.getValue().iterator();
						if (_currentValueCollection.hasNext()) {
							final V currentValue = _currentValueCollection.next();
							_currentEntry = new DefaultMapEntry<K, V>(_currentKey, currentValue);
						}
					}
				}
			}
		}
		return currentEntry;
	}


	public static <K, V> MultiMapItemIterator<K, V> decorate(final MultiMap<K, V> baseMap)
	{
		return new MultiMapItemIterator<K, V>(baseMap.entrySet());
	}


	public static <K, V> MultiMapItemIterator<K, V> decorate(final Iterable<Map.Entry<K, Collection<V>>> baseIterable)
	{
		return new MultiMapItemIterator<K, V>(baseIterable);
	}


	public boolean hasNext()
	{
		return _currentEntry != null;
	}


	public Entry<K, V> next()
	{
		final Entry<K, V> currentEntry = advance();
		if (currentEntry == null)
			throw new NoSuchElementException("No more multimap entries");
		else
			return currentEntry;
	}


	public void remove()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
