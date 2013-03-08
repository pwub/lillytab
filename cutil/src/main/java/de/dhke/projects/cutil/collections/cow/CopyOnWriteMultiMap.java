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
package de.dhke.projects.cutil.collections.cow;

import de.dhke.projects.cutil.collections.factories.IMultiMapFactory;
import de.dhke.projects.cutil.collections.factories.MultiHashMapFactory;
import org.apache.commons.collections15.MultiMap;

/**
 *
 * @param <K>
 * @param <V>
 * @author Peter Wullinger <java@dhke.de>
 */
public class CopyOnWriteMultiMap<K, V>
	extends GenericCopyOnWriteMultiMap<K, V, MultiMap<K, V>>
	implements Cloneable
{
	protected CopyOnWriteMultiMap(final MultiMap<K, V> baseMap, final IMultiMapFactory<K ,V, MultiMap<K, V>> factory)
	{
		super(baseMap, factory);
	}

	protected CopyOnWriteMultiMap(final MultiMap<K, V> baseMap)
	{
		super(baseMap, new MultiHashMapFactory<K, V>());
	}

	public static <K, V> CopyOnWriteMultiMap<K, V> decorate(final MultiMap<K ,V> baseMap, final IMultiMapFactory<K ,V, MultiMap<K, V>> factory)
	{
		return new CopyOnWriteMultiMap<>(baseMap, factory);
	}

	public static <K, V> CopyOnWriteMultiMap<K, V> decorate(final MultiMap<K ,V> baseMap)
	{
		return new CopyOnWriteMultiMap<>(baseMap);
	}

	@Override
	public CopyOnWriteMultiMap<K, V> clone()
	{
		CopyOnWriteMultiMap<K, V> klone = decorate(getDecoratee(), getFactory());
		resetWasCopied();
		return klone;
	}
}
