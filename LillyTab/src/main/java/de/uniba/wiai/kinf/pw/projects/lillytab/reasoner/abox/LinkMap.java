/**
 * (c) 2009-2013 Otto-Friedrich-University Bamberg
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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox;

import de.dhke.projects.cutil.Pair;
import de.dhke.projects.cutil.collections.aspect.AspectMultiMap;
import de.dhke.projects.cutil.collections.aspect.CollectionEvent;
import de.dhke.projects.cutil.collections.aspect.CollectionItemEvent;
import de.dhke.projects.cutil.collections.cow.CopyOnWriteMultiMap;
import de.dhke.projects.cutil.collections.factories.MultiTreeSetHashMapFactory;
import de.dhke.projects.cutil.collections.iterator.MultiMapEntryIterable;
import de.dhke.projects.cutil.collections.map.EmptyMultiMap;
import de.dhke.projects.cutil.collections.map.MultiTreeSetHashMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ILinkMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.collections15.MultiMap;

/**
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class LinkMap<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> 
	extends AspectMultiMap<R, NodeID, MultiMap<R, NodeID>>
	implements ILinkMap<I, L, K, R>, Cloneable {

	public LinkMap(
		final IABoxNode<I, L, K, R> node,
		boolean always_empty)
	{
		super(new EmptyMultiMap<R, NodeID>(), node);
	}


	public LinkMap(
		final IABoxNode<I, L, K, R> node)
	{
		super(CopyOnWriteMultiMap.decorate(new MultiTreeSetHashMap<R, NodeID>(),
										   new MultiTreeSetHashMapFactory<R, NodeID>()), node);
	}


	protected LinkMap(
		final IABoxNode<I, L, K, R> node,
		final CopyOnWriteMultiMap<R, NodeID> baseMap)
	{
		super(baseMap, node);
	}


	@SuppressWarnings("unchecked")
	@Override
	public IABoxNode<I, L, K, R> getNode()
	{
		return (IABoxNode<I, L, K, R>) getSender();
	}


	@Override
	public NodeID put(final R role, final IABoxNode<I, L, K, R> node)
	{
		return put(role, node.getNodeID());
	}


	@Override
	public boolean putAll(R key,
						  Collection<? extends IABoxNode<I, L, K, R>> values)
	{
		boolean added = false;
		for (IABoxNode<I, L, K, R> node : values) {
			if (put(key, node.getNodeID()) != null) {
				added = true;
			}
		}
		return added;
	}


	@Override
	protected void notifyAfterElementAdded(CollectionItemEvent<Entry<R, NodeID>, MultiMap<R, NodeID>> ev)
	{
		super.notifyAfterElementAdded(ev);
		final IABoxNode<I, L, K, R> source = getNode();
		final IABox<I, L, K, R> abox = source.getABox();
		assert abox != null;
		final Map.Entry<R, NodeID> entry = ev.getItem();
		final IABoxNode<I, L, K, R> target = abox.getNode(entry.getValue());
		assert target != null;
		if (isSuccessorMap(source, ev.getCollection())) {
			if (!target.getRABox().getAssertedPredecessors().containsValue(entry.getKey(), source.getNodeID())) {
				target.getRABox().getAssertedPredecessors().put(entry.getKey(), source.getNodeID());
			}
		} else if (isPredecessorMap(source, ev.getCollection())) {
			if (!target.getRABox().getAssertedSuccessors().containsValue(entry.getKey(), source.getNodeID())) {
				target.getRABox().getAssertedSuccessors().put(entry.getKey(), source.getNodeID());
			}
		} else {
			throw new IllegalArgumentException(String.format("Unsupported collection `%s'", ev.getCollection()));
		}

		super.notifyAfterElementAdded(ev);
	}


	@Override
	public void notifyAfterElementRemoved(final CollectionItemEvent<Entry<R, NodeID>, MultiMap<R, NodeID>> e)
	{
		final IABoxNode<I, L, K, R> source = getNode();
		final IABox<I, L, K, R> abox = source.getABox();
		if (abox != null) {
			final Map.Entry<R, NodeID> entry = e.getItem();
			final IABoxNode<I, L, K, R> target = abox.getNode(entry.getValue());

			if (target != null) {
				if (isSuccessorMap(source, e.getCollection())) {
					if (target.getRABox().getAssertedPredecessors().containsValue(entry.getKey(), source.
						getNodeID())) {
						target.getRABox().getAssertedPredecessors().remove(entry.getKey(), source.getNodeID());
					}
				} else if (isPredecessorMap(source, e.getCollection())) {
					if (target.getRABox().getAssertedSuccessors().
						containsValue(entry.getKey(), source.getNodeID())) {
						target.getRABox().getAssertedSuccessors().remove(entry.getKey(), source.getNodeID());
					}
				} else {
					throw new IllegalArgumentException(String.format("Unsupported collection `%s'", e.
						getCollection()));
				}
			}
		}

		super.notifyAfterElementRemoved(e);
	}


	@Override
	public void notifyBeforeCollectionCleared(CollectionEvent<Entry<R, NodeID>, MultiMap<R, NodeID>> e)
	{
		final IABoxNode<I, L, K, R> source = getNode();
		final IABox<I, L, K, R> abox = source.getABox();

		if (abox != null) {
			if (isPredecessorMap(source, e.getCollection())) {
				/* we cannot iterate through the map, because it gets modified by the remove() callbacks */
				final Collection<Pair<R, IABoxNode<I, L, K, R>>> predEntries = getAllEntries(abox, e.
					getCollection());

				final NodeID sourceID = source.getNodeID();
				for (Pair<R, IABoxNode<I, L, K, R>> predEntry : predEntries) {
					final NodeID removedID = predEntry.getSecond().getRABox().getAssertedSuccessors().
						remove(predEntry.getFirst(),
							   sourceID);
					assert removedID == sourceID;
				}
				assert source.getRABox().getAssertedPredecessors().isEmpty();
			} else if (isSuccessorMap(source, e.getCollection())) {
				/* we cannot iterate through the map, because it gets modified by the remove() callbacks */
				final Collection<Pair<R, IABoxNode<I, L, K, R>>> succEntries = getAllEntries(abox, e.
					getCollection());

				final NodeID sourceID = source.getNodeID();
				for (Pair<R, IABoxNode<I, L, K, R>> succEntry : succEntries) {
					final NodeID removedID = succEntry.getSecond().getRABox().getAssertedPredecessors().
						remove(succEntry.getFirst(),
							   sourceID);
					assert removedID == sourceID;
				}
				assert source.getRABox().getAssertedSuccessors().isEmpty();
			} else {
				throw new IllegalArgumentException(String.format("Unsupported collection `%s'", e.getCollection()));
			}
		}

		super.notifyBeforeCollectionCleared(e);
	}


	private boolean isSuccessorMap(final IABoxNode<I, L, K, R> node, final MultiMap<R, NodeID> map)
	{
		return map == node.getRABox().getAssertedSuccessors();
	}


	private boolean isPredecessorMap(final IABoxNode<I, L, K, R> node,
									 final MultiMap<R, NodeID> map)
	{
		return map == node.getRABox().getAssertedPredecessors();
	}


	private Collection<Pair<R, IABoxNode<I, L, K, R>>> getAllEntries(
		final IABox<I, L, K, R> abox,
		final MultiMap<R, NodeID> map)
	{
		final List<Pair<R, IABoxNode<I, L, K, R>>> list = new ArrayList<>(map.
			size());
		for (Map.Entry<R, NodeID> mapEntry : MultiMapEntryIterable.decorate(map.entrySet())) {
			list.add(Pair.wrap(mapEntry.getKey(), abox.getNode(mapEntry.getValue())));
		}
		return list;
	}


	public LinkMap<I, L, K, R> clone(final IABoxNode<I, L, K, R> newNode)
	{
		final LinkMap<I, L, K, R> newMap;
		if (getDecoratee() instanceof EmptyMultiMap) {
			newMap = new LinkMap<>(newNode, true);
		} else if (getDecoratee() instanceof CopyOnWriteMultiMap) {
			final CopyOnWriteMultiMap<R, NodeID> baseMap = (CopyOnWriteMultiMap<R, NodeID>) getDecoratee();
			newMap = new LinkMap<>(newNode, baseMap.clone());
		} else {
			throw new IllegalStateException(String.format("base map of LinkMap is of unsupported type: '%s'",
														  getDecoratee().getClass()));
		}

		return newMap;
	}
}
