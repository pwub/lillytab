/**
 * (c) 2009-2013 Peter Wullinger
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
package de.dhke.projects.cutil.collections.map;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class MultiEnumSetHashMap<K, E extends Enum<E>> 
	extends MultiHashMap<K, E>
{
	private final Class<E> _class;
	
	public MultiEnumSetHashMap(final Class<E> klass)
	{
		super();
		_class = klass;
	}

	public MultiEnumSetHashMap(final Class<E> klass, Map<K, E> mapToCopy)
	{
		super(mapToCopy);
		_class = klass;
	}

	public MultiEnumSetHashMap(final Class<E> klass, MultiMap<K, E> mapToCopy)
	{
		super(mapToCopy);
		_class = klass;
	}

	public MultiEnumSetHashMap(final Class<E> klass, int initialCapacity)
	{
		super(initialCapacity);
		_class = klass;
	}

	public MultiEnumSetHashMap(final Class<E> klass, int initialCapacity, float loadFactory)
	{
		super(initialCapacity, loadFactory);
		_class = klass;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Collection<E> createCollection(Collection<? extends E> coll)
	{
		if (coll != null)
			return EnumSet.copyOf((Collection<E>)coll);
		else
			return EnumSet.noneOf(_class);
	}	
}
