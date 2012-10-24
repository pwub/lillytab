/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox;

import de.dhke.projects.cutil.Pair;
import de.dhke.projects.cutil.collections.aspect.AspectMultiMap;
import de.dhke.projects.cutil.collections.aspect.CollectionEvent;
import de.dhke.projects.cutil.collections.aspect.CollectionItemEvent;
import de.dhke.projects.cutil.collections.cow.CopyOnWriteMultiMap;
import de.dhke.projects.cutil.collections.factories.MultiTreeSetHashMapFactory;
import de.dhke.projects.cutil.collections.iterator.MultiMapItemIterable;
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
 * @param <Name> The type for nominals and values
 * @param <Klass> The type for DL classes
 * @param <Role> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class LinkMap<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends AspectMultiMap<Role, NodeID, MultiMap<Role, NodeID>>
	implements ILinkMap<Name, Klass, Role>, Cloneable
{
	public LinkMap(
		final IABoxNode<Name, Klass, Role> node,
		boolean always_empty)
	{
		super(new EmptyMultiMap<Role, NodeID>(), node);
	}

	public LinkMap(
		final IABoxNode<Name, Klass, Role> node)
	{
		super(CopyOnWriteMultiMap.decorate(new MultiTreeSetHashMap<Role, NodeID>(),
										   new MultiTreeSetHashMapFactory<Role, NodeID>()), node);
	}

	protected LinkMap(
		final IABoxNode<Name, Klass, Role> node,
		final CopyOnWriteMultiMap<Role, NodeID> baseMap)
	{
		super(baseMap, node);
	}

	@SuppressWarnings("unchecked")
	public IABoxNode<Name, Klass, Role> getNode()
	{
		return (IABoxNode<Name, Klass, Role>) getSender();
	}

	public NodeID put(final Role role, final IABoxNode<Name, Klass, Role> node)
	{
		return put(role, node.getNodeID());
	}

	public boolean putAll(Role key,
						  Collection<? extends IABoxNode<Name, Klass, Role>> values)
	{
		boolean added = false;
		for (IABoxNode<Name, Klass, Role> node : values) {
			if (put(key, node.getNodeID()) != null)
				added = true;
		}
		return added;
	}

	@Override
	protected void notifyAfterElementAdded(CollectionItemEvent<Entry<Role, NodeID>, MultiMap<Role, NodeID>> ev)
	{
		super.notifyAfterElementAdded(ev);
		final IABoxNode<Name, Klass, Role> source = getNode();
		final IABox<Name, Klass, Role> abox = source.getABox();
		assert abox != null;
		final Map.Entry<Role, NodeID> entry = ev.getItem();
		final IABoxNode<Name, Klass, Role> target = abox.getNode(entry.getValue());
		assert target != null;
		if (isSuccessorMap(source, ev.getCollection())) {
			if (!target.getRABox().getAssertedPredecessors().containsValue(entry.getKey(), source.getNodeID()))
				target.getRABox().getAssertedPredecessors().put(entry.getKey(), source.getNodeID());
		} else if (isPredecessorMap(source, ev.getCollection())) {
			if (!target.getRABox().getAssertedSuccessors().containsValue(entry.getKey(), source.getNodeID()))
				target.getRABox().getAssertedSuccessors().put(entry.getKey(), source.getNodeID());
		} else
			throw new IllegalArgumentException(String.format("Unsupported collection `%s'", ev.getCollection()));

		super.notifyAfterElementAdded(ev);
	}

	@Override
	public void notifyAfterElementRemoved(final CollectionItemEvent<Entry<Role, NodeID>, MultiMap<Role, NodeID>> e)
	{
		final IABoxNode<Name, Klass, Role> source = getNode();
		final IABox<Name, Klass, Role> abox = source.getABox();
		if (abox != null) {
			final Map.Entry<Role, NodeID> entry = e.getItem();
			final IABoxNode<Name, Klass, Role> target = abox.getNode(entry.getValue());

			if (target != null) {
				if (isSuccessorMap(source, e.getCollection())) {
					if (target.getRABox().getAssertedPredecessors().containsValue(entry.getKey(), source.
						getNodeID()))
						target.getRABox().getAssertedPredecessors().remove(entry.getKey(), source.getNodeID());
				} else if (isPredecessorMap(source, e.getCollection())) {
					if (target.getRABox().getAssertedSuccessors().
						containsValue(entry.getKey(), source.getNodeID()))
						target.getRABox().getAssertedSuccessors().remove(entry.getKey(), source.getNodeID());
				} else
					throw new IllegalArgumentException(String.format("Unsupported collection `%s'", e.
						getCollection()));
			}
		}

		super.notifyAfterElementRemoved(e);
	}

	@Override
	public void notifyBeforeCollectionCleared(CollectionEvent<Entry<Role, NodeID>, MultiMap<Role, NodeID>> e)
	{
		final IABoxNode<Name, Klass, Role> source = getNode();
		final IABox<Name, Klass, Role> abox = source.getABox();

		if (abox != null) {
			if (isPredecessorMap(source, e.getCollection())) {
				/* we cannot iterate through the map, because it gets modified by the remove() callbacks */
				final Collection<Pair<Role, IABoxNode<Name, Klass, Role>>> predEntries = getAllEntries(abox, e.
					getCollection());

				final NodeID sourceID = source.getNodeID();
				for (Pair<Role, IABoxNode<Name, Klass, Role>> predEntry : predEntries) {
					final NodeID removedID = predEntry.getSecond().getRABox().getAssertedSuccessors().
						remove(predEntry.getFirst(),
							   sourceID);
					assert removedID == sourceID;
				}
				assert source.getRABox().getAssertedPredecessors().isEmpty();
			} else if (isSuccessorMap(source, e.getCollection())) {
				/* we cannot iterate through the map, because it gets modified by the remove() callbacks */
				final Collection<Pair<Role, IABoxNode<Name, Klass, Role>>> succEntries = getAllEntries(abox, e.
					getCollection());

				final NodeID sourceID = source.getNodeID();
				for (Pair<Role, IABoxNode<Name, Klass, Role>> succEntry : succEntries) {
					final NodeID removedID = succEntry.getSecond().getRABox().getAssertedPredecessors().
						remove(succEntry.getFirst(),
							   sourceID);
					assert removedID == sourceID;
				}
				assert source.getRABox().getAssertedSuccessors().isEmpty();
			} else
				throw new IllegalArgumentException(String.format("Unsupported collection `%s'", e.getCollection()));
		}

		super.notifyBeforeCollectionCleared(e);
	}

	private boolean isSuccessorMap(final IABoxNode<Name, Klass, Role> node, final MultiMap<Role, NodeID> map)
	{
		return map == node.getRABox().getAssertedSuccessors();
	}

	private boolean isPredecessorMap(final IABoxNode<Name, Klass, Role> node,
									 final MultiMap<Role, NodeID> map)
	{
		return map == node.getRABox().getAssertedPredecessors();
	}

	private Collection<Pair<Role, IABoxNode<Name, Klass, Role>>> getAllEntries(
		final IABox<Name, Klass, Role> abox,
		final MultiMap<Role, NodeID> map)
	{
		final List<Pair<Role, IABoxNode<Name, Klass, Role>>> list = new ArrayList<Pair<Role, IABoxNode<Name, Klass, Role>>>(map.
			size());
		for (Map.Entry<Role, NodeID> mapEntry : MultiMapItemIterable.decorate(map.entrySet()))
			list.add(Pair.wrap(mapEntry.getKey(), abox.getNode(mapEntry.getValue())));
		return list;
	}

	public LinkMap<Name, Klass, Role> clone(final IABoxNode<Name, Klass, Role> newNode)
	{
		final LinkMap<Name, Klass, Role> newMap;
		if (getDecoratee() instanceof EmptyMultiMap)
			newMap = new LinkMap<Name, Klass, Role>(newNode, true);
		else if (getDecoratee() instanceof CopyOnWriteMultiMap) {
			final CopyOnWriteMultiMap<Role, NodeID> baseMap = (CopyOnWriteMultiMap<Role, NodeID>) getDecoratee();
			newMap = new LinkMap<Name, Klass, Role>(newNode, baseMap.clone());
		} else
			throw new IllegalStateException(String.format("base map of LinkMap is of unsupported type: '%s'",
														  getDecoratee().getClass()));

		return newMap;
	}
}
