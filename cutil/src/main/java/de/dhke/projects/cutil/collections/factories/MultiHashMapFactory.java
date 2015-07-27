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
package de.dhke.projects.cutil.collections.factories;

import de.dhke.projects.cutil.collections.map.GenericMultiHashMap;
import java.util.Collection;
import org.apache.commons.collections15.MultiMap;

/**
 *
 * @param <K>
 * @param <V>
 *            <p/>
 * @author Peter Wullinger <java@dhke.de>
 */
public class MultiHashMapFactory<K, V>
	implements IMultiMapFactory<K, V, MultiMap<K, V>> {

	private final ICollectionFactory<V, ? extends Collection<V>> _collectionfactory;


	public MultiHashMapFactory(final ICollectionFactory<V, ? extends Collection<V>> collectionFactory)
	{
		_collectionfactory = collectionFactory;
	}


	public MultiHashMapFactory()
	{
		_collectionfactory = new ArrayListFactory<>();
	}


	@Override
	public MultiMap<K, V> getInstance()
	{
		return new GenericMultiHashMap<>(_collectionfactory);
	}


	@Override
	public MultiMap<K, V> getInstance(final MultiMap<K, V> baseMap)
	{
		return new GenericMultiHashMap<>(_collectionfactory);
	}
}
