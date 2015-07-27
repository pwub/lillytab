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
package de.dhke.projects.cutil.collections.modified;

import de.dhke.projects.cutil.IDecorator;
import de.dhke.projects.cutil.collections.aspect.AspectMultiMap;
import de.dhke.projects.cutil.collections.aspect.CollectionEvent;
import de.dhke.projects.cutil.collections.aspect.CollectionItemEvent;
import de.dhke.projects.cutil.collections.aspect.CollectionItemReplacedEvent;
import java.util.Map.Entry;
import org.apache.commons.collections15.MultiMap;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class WasModifiedMultiMap<K, V, M extends MultiMap<K, V>>
	extends AspectMultiMap<K, V, M>
	implements IDecorator<M>, IWasModifiedCollection
{
	private boolean _modified = false;

	@Deprecated
	protected WasModifiedMultiMap(final M baseMap)
	{
		super(baseMap);
	}


	protected WasModifiedMultiMap(final M baseMap, final Object sender)
	{
		super(baseMap, sender);
	}


	@Deprecated
	public static <K, V, M extends MultiMap<K, V>> WasModifiedMultiMap<K, V, M> decorate(final M baseMap)
	{
		return new WasModifiedMultiMap<>(baseMap);
	}


	public static <K, V, M extends MultiMap<K, V>> WasModifiedMultiMap<K, V, M> decorate(final M baseMap,
																						 final Object sender)
	{
		return new WasModifiedMultiMap<>(baseMap, sender);
	}
	
	@Override
	public boolean isWasModified()
	{
		return _modified;
	}
	
	@Override
	public void clearModified()
	{
		_modified = false;
	}
	
		protected void setModified()
	{
		_modified = true;		
	}


	@Override
	protected void notifyAfterCollectionCleared(CollectionEvent<Entry<K, V>, MultiMap<K, V>> ev)
	{
		setModified();
		super.notifyAfterCollectionCleared(ev);
	}


	@Override
	protected void notifyAfterElementAdded(CollectionItemEvent<Entry<K, V>, MultiMap<K, V>> ev)
	{
		setModified();
		super.notifyAfterElementAdded(ev);
	}


	@Override
	protected void notifyAfterElementRemoved(CollectionItemEvent<Entry<K, V>, MultiMap<K, V>> ev)
	{
		setModified();
		super.notifyAfterElementRemoved(ev);
	}


	@Override
	protected void notifyAfterElementReplaced(CollectionItemReplacedEvent<Entry<K, V>, MultiMap<K, V>> ev)
	{
		setModified();
		super.notifyAfterElementReplaced(ev);
	}
	
	
}
