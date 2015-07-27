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
package de.dhke.projects.cutil.collections.map;

import de.dhke.projects.cutil.collections.factories.ArrayListFactory;
import de.dhke.projects.cutil.collections.factories.ICollectionFactory;
import java.util.Collection;
import org.apache.commons.collections15.multimap.MultiHashMap;

/**
 *
 * A {@link MultiHashMap} that uses HashSets for key storage instead of array lists.
 * <p/>
 *
 * @author Peter Wullinger <java@dhke.de>
 * @param <K> Key type
 * @param <V> Value type
 */
public class GenericMultiHashMap<K, V>
	extends MultiHashMap<K, V> {

	static final long serialVersionUID = 8236744137300969863L;
	private final ICollectionFactory<V, ? extends Collection<V>> _collectionFactory;


	public GenericMultiHashMap(final ICollectionFactory<V, ? extends Collection<V>> collectionFactory)
	{
		super();
		_collectionFactory = collectionFactory;
	}


	public GenericMultiHashMap()
	{
		super();
		_collectionFactory = new ArrayListFactory<>();
	}


	@Override
	protected Collection<V> createCollection(Collection<? extends V> coll)
	{
		if (coll != null)
			return _collectionFactory.getInstance(coll);
		else
			return _collectionFactory.getInstance();
	}
}
