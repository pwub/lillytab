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
package de.dhke.projects.cutil.collections.aspect;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class AspectCollectionHistoryListener<E, C>
	implements ICollectionListener<E, C>
{
	public boolean vetoAdd = false;
	final public List<CollectionItemEvent<E, C>> beforeAddEvents;
	final public List<CollectionItemEvent<E, C>> afterAddEvents;
	public boolean vetoRemove = false;
	final public List<CollectionItemEvent<E, C>> beforeRemoveEvents;
	final public List<CollectionItemEvent<E, C>> afterRemoveEvents;
	public boolean vetoReplace = false;
	final public List<CollectionItemReplacedEvent<E, C>> beforeReplaceEvents;
	final public List<CollectionItemReplacedEvent<E, C>> afterReplaceEvents;
	public boolean vetoClear = false;
	final public List<CollectionEvent<E, C>> beforeClearEvents;
	final public List<CollectionEvent<E, C>> afterClearEvents;


	public AspectCollectionHistoryListener()
	{
		beforeAddEvents = new ArrayList<>();
		afterAddEvents = new ArrayList<>();
		beforeRemoveEvents = new ArrayList<>();
		afterRemoveEvents = new ArrayList<>();
		beforeReplaceEvents = new ArrayList<>();
		afterReplaceEvents = new ArrayList<>();
		beforeClearEvents = new ArrayList<>();
		afterClearEvents = new ArrayList<>();
	}


	@Override
	public void beforeElementAdded(CollectionItemEvent<E, C> e)
	{
		if (vetoAdd)
			throw new AssertionError("Add veto");
		else
			beforeAddEvents.add(e);
	}


	@Override
	public void beforeElementRemoved(CollectionItemEvent<E, C> e)
	{
		if (vetoRemove)
			throw new AssertionError("Remove veto");
		else
			beforeRemoveEvents.add(e);
	}


	@Override
	public void beforeElementReplaced(CollectionItemReplacedEvent<E, C> e)
	{
		if (vetoReplace)
			throw new AssertionError("Replace veto");
		else
			beforeReplaceEvents.add(e);
	}


	@Override
	public void beforeCollectionCleared(CollectionEvent<E, C> e)
	{
		if (vetoClear)
			throw new AssertionError("Clear veto");
		else
			beforeClearEvents.add(e);
	}


	@Override
	public void afterElementAdded(CollectionItemEvent<E, C> e)
	{
		afterAddEvents.add(e);
	}


	@Override
	public void afterElementRemoved(CollectionItemEvent<E, C> e)
	{
		afterRemoveEvents.add(e);
	}


	@Override
	public void afterElementReplaced(CollectionItemReplacedEvent<E, C> e)
	{
		afterReplaceEvents.add(e);
	}


	@Override
	public void afterCollectionCleared(CollectionEvent<E, C> e)
	{
		afterClearEvents.add(e);
	}
}
