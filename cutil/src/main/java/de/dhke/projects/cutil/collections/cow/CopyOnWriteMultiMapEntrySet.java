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
package de.dhke.projects.cutil.collections.cow;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.keyvalue.DefaultMapEntry;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
class CopyOnWriteMultiMapEntrySet<K, V>
	implements Set<Map.Entry<K, Collection<V>>> {

	private CopyOnWriteMultiMap<K, V> _cowMap;
	/// </editor-fold>
	private MultiMap<K, V> _lastBaseMap = null;
	private Set<Entry<K, Collection<V>>> _cachedEntrySet = null;


	protected CopyOnWriteMultiMapEntrySet(final CopyOnWriteMultiMap<K, V> cowMap)
	{
		_cowMap = cowMap;
	}


	@Override
	public int size()
	{
		return getOriginalEntrySet().size();
	}


	@Override
	public boolean isEmpty()
	{
		return getOriginalEntrySet().isEmpty();
	}


	@Override
	public boolean contains(final Object o)
	{
		return getOriginalEntrySet().contains(o);
	}


	@Override
	public Iterator<Entry<K, Collection<V>>> iterator()
	{
		return new Itr();
	}


	@Override
	public Object[] toArray()
	{
		return getOriginalEntrySet().toArray();
	}


	@Override
	public <T> T[] toArray(final T[] a)
	{
		return getOriginalEntrySet().toArray(a);
	}


	@Override
	public  boolean add(final Entry<K, Collection<V>> e)
	{
		_cowMap.copy();
		return _cowMap.putAll(e.getKey(), e.getValue());
	}


	@Override
	public boolean remove(final Object o)
	{
		_cowMap.copy();
		return getOriginalEntrySet().remove(o);
	}


	@Override
	public boolean containsAll(final Collection<?> c)
	{
		return getOriginalEntrySet().containsAll(c);
	}


	@Override
	public boolean addAll(final Collection<? extends Entry<K, Collection<V>>> c)
	{
		_cowMap.copy();
		return getOriginalEntrySet().addAll(c);
	}


	@Override
	public boolean retainAll(final Collection<?> c)
	{
		_cowMap.copy();
		return getOriginalEntrySet().retainAll(c);
	}


	@Override
	public boolean removeAll(final Collection<?> c)
	{
		_cowMap.copy();
		return getOriginalEntrySet().removeAll(c);
	}


	@Override
	public void clear()
	{
		_cowMap.copy();
		getOriginalEntrySet().clear();
	}


		private Set<Entry<K, Collection<V>>> getOriginalEntrySet()
	{
		/* cache entrty set to avoid re-fetch */
		if (_lastBaseMap != _cowMap.getDecoratee()) {
			_lastBaseMap = _cowMap.getDecoratee();
			_cachedEntrySet = _lastBaseMap.entrySet();
		}
		assert _cachedEntrySet != null;
		return _cachedEntrySet;
	}

	/// <editor-fold defaultstate="collapsed" desc="class Itr">
	private class Itr
		implements Iterator<Map.Entry<K, Collection<V>>> {

		private final Iterator<K> _keyIterator;
		private final boolean _collectionAlreadyCopied;


		Itr()
		{
			_collectionAlreadyCopied = _cowMap.isWasCopied();
			_keyIterator = _cowMap.getDecoratee().keySet().iterator();
		}


		@Override
		public boolean hasNext()
		{
			return _keyIterator.hasNext();
		}


		@Override
		public Entry<K, Collection<V>> next()
		{
			K nextKey = _keyIterator.next();
			return new DefaultMapEntry<>(nextKey, _cowMap.get(nextKey));
		}


		@Override
		public void remove()
		{
			if (_collectionAlreadyCopied)
				_keyIterator.remove();
			else
				throw new UnsupportedOperationException("Cannot remove from untouched CopyOnWriteMultiMap via iterator.");
		}
	}
}
