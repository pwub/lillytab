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
package de.dhke.projects.cutil.collections.factories;

import de.dhke.projects.cutil.collections.map.MultiTreeSetHashMap;
import org.apache.commons.collections15.MultiMap;

/**
 * @param <K> 
 * @param <V> 
 * @author Peter Wullinger <java@dhke.de>
 */
public class MultiTreeSetHashMapFactory<K, V extends Comparable<? super V>>
	implements IMultiMapFactory<K, V, MultiMap<K, V>>
{

	@Override
	public MultiMap<K, V> getInstance()
	{
		return new MultiTreeSetHashMap<>();
	}

	@Override
	public MultiMap<K, V> getInstance(MultiMap<K, V> baseMap)
	{
		return new MultiTreeSetHashMap<>(baseMap);
	}
}
