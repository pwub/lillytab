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

import de.dhke.projects.cutil.collections.aspect.AspectMultiMap;
import de.dhke.projects.cutil.collections.aspect.CollectionEvent;
import de.dhke.projects.cutil.collections.aspect.CollectionItemEvent;
import de.dhke.projects.cutil.collections.aspect.CollectionItemReplacedEvent;
import de.dhke.projects.cutil.collections.factories.IMultiMapFactory;
import java.util.Map;
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;

/**
 * A wrapper class around two linked {@link MultiMap}.
 * <p/>
 * One multimap always contains the inverse entries of the other multimap.
 * <p/>
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class LinkedMultiMap<A, B> {
	private final MultiMap<A, B> _realForwardMap;
	private final MultiMap<B, A> _realReverseMap;
	private final MultiMap<A, B> _forwardMap;
	private final MultiMap<B, A> _reverseMap;


	public LinkedMultiMap(final IMultiMapFactory<A, B, ? extends MultiMap<A, B>> forwardFactory,
						  final IMultiMapFactory<B, A, ? extends MultiMap<B, A>> reverseFactory)
	{
		this(forwardFactory.getInstance(), reverseFactory.getInstance());
	}


	public LinkedMultiMap()
	{
		this(new MultiHashMap<A, B>(), new MultiHashMap<B, A>());
	}


	protected LinkedMultiMap(final MultiMap<A, B> realForwardMap, final MultiMap<B, A> realReverseMap)
	{
		_realForwardMap = realForwardMap;
		_realReverseMap = realReverseMap;
		_forwardMap = new InverseUpdatingMap<>(_realForwardMap, _realReverseMap);
		_reverseMap = new InverseUpdatingMap<>(_realReverseMap, _realForwardMap);
	}


	public MultiMap<A, B> getForwardMap()
	{
		return _forwardMap;
	}


	public MultiMap<B, A> getReverseMap()
	{
		return _reverseMap;
	}

	class InverseUpdatingMap<A, B>
		extends AspectMultiMap<A, B, MultiMap<A, B>> {

		final MultiMap<B, A> _inverseMap;


		InverseUpdatingMap(final MultiMap<A, B> baseMap, final MultiMap<B, A> inverseMap)
		{
			/* abuse the source element to store the inverse map */
			super(baseMap, baseMap);
			_inverseMap = inverseMap;
		}


		@Override
		protected void notifyAfterElementAdded(final CollectionItemEvent<Map.Entry<A, B>, MultiMap<A, B>> ev)
		{
			final Map.Entry<A, B> entry = ev.getItem();
			final A item = _inverseMap.put(entry.getValue(), entry.getKey());
			assert item == entry.getKey();
			super.notifyAfterElementAdded(ev);
		}


		@Override
		protected void notifyAfterElementRemoved(final CollectionItemEvent<Map.Entry<A, B>, MultiMap<A, B>> ev)
		{
			final Map.Entry<A, B> entry = ev.getItem();
			final A item = _inverseMap.remove(entry.getValue(), entry.getKey());
			assert item == entry.getKey();
			super.notifyAfterElementRemoved(ev); //To change body of generated methods, choose Tools | Templates.
		}


		@Override
		protected void notifyAfterElementReplaced(final CollectionItemReplacedEvent<Map.Entry<A, B>, MultiMap<A, B>> ev)
		{
			final Map.Entry<A, B> oldEntry = ev.getItem();
			final A oldItem = _inverseMap.remove(oldEntry.getValue(), oldEntry.getKey());
			assert oldItem == oldEntry.getKey();

			final Map.Entry<A, B> newEntry = ev.getNewItem();
			final A newItem = _inverseMap.put(newEntry.getValue(), newEntry.getKey());
			assert newItem == newEntry.getKey();
			super.notifyAfterElementReplaced(ev); //To change body of generated methods, choose Tools | Templates.
		}


		@Override
		protected void notifyAfterCollectionCleared(final CollectionEvent<Map.Entry<A, B>, MultiMap<A, B>> ev)
		{
			_inverseMap.clear();
			super.notifyAfterCollectionCleared(ev); //To change body of generated methods, choose Tools | Templates.
		}
	}
}
