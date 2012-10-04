/**
 * (c) 2009-2012 Otto-Friedrich-University Bamberg
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

import java.lang.ref.SoftReference;
import java.util.WeakHashMap;


/**
 * 
 * <p>
 * A simple implementation of a weak item cache. Use when object creation is cheap,
 * but long term storage is expensive.
 * </p><p>
 * To use, this cache, generate a temporary new object with
 * the requires properties. Then call {@link #updateCache(java.lang.Object) }.
 * This will check the cache, if an equal (via {@link #hashCode()} and {@link #equals(java.lang.Object) }
 * object already exists in the cache. If so, the cached copy is returned.
 * </p><p>
 * Otherwise the provided temporary object is stored into the cache, effectively
 * making it into the cached copy.
 * </p>
 * 
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class SoftItemCache<T>
{
	private final WeakHashMap<T, SoftReference<T>> _itemCache = new WeakHashMap<T, SoftReference<T>>();
	
	/**
	* Check the cache, if an equal (via {@link #hashCode()} and {@link #equals(java.lang.Object) }
	* object already exists in the cache. If so, the cached copy is returned.
	* </p><p>
	* Otherwise the provided temporary object is stored into the cache, effectively
	* making it into the cached copy.
	* </p><p>
	* if {@literal item} is {@literal null}, behaviour is undefined. Otherwise, {@literal null} is never returned.
	* </p>
    * @param <TT> The type of the term object to check and return.
	* @param item The object to lookup (or store) in the cache.
    **/
	@SuppressWarnings("unchecked")
	public <TT extends T> TT updateCache(final TT item)
	{
		final SoftReference<T> ref = _itemCache.get(item);
		if ((ref != null) && (ref.get() != null)) {
			return (TT)ref.get();
		} else {
			_itemCache.put(item, new SoftReference<T>(item));
			return item;
		}
	}
}
