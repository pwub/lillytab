/**
 * (c) 2009-2012 Otto-Friedrich-University Bamberg
 *
 * $Id$
 *
 * Use, modification and restribution of this file are covered by the terms of the Artistic License 2.0.
 *
 * You should have received a copy of the license terms in a file named "LICENSE" together with this software package.
 *
 * Disclaimer of Warranty: THE PACKAGE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS "AS IS' AND WITHOUT ANY
 * EXPRESS OR IMPLIED WARRANTIES. THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT ARE DISCLAIMED TO THE EXTENT PERMITTED BY YOUR LOCAL LAW. UNLESS REQUIRED BY LAW, NO COPYRIGHT
 * HOLDER OR CONTRIBUTOR WILL BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING IN ANY
 * WAY OUT OF THE USE OF THE PACKAGE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox;

import de.dhke.projects.cutil.Pair;
import de.dhke.projects.cutil.collections.MultiMapUtil;
import de.dhke.projects.cutil.collections.aspect.AspectMultiMap;
import de.dhke.projects.cutil.collections.cow.CopyOnWriteMultiMap;
import de.dhke.projects.cutil.collections.factories.IMultiMapFactory;
import de.dhke.projects.cutil.collections.factories.MultiTreeSetHashMapFactory;
import de.dhke.projects.cutil.collections.map.EmptyMultiMap;
import de.dhke.projects.cutil.collections.set.SortedListSet;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ILinkMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.immutable.ImmutableLinkMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IRBox;
import java.util.*;
import org.apache.commons.collections15.IteratorUtils;
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.Transformer;


/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class LinkMap<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	implements ILinkMap<Name, Klass, Role>
{
	/**
	 *
	 */
	final class SubRoleAwareNodeIDIterator
		implements Iterator<NodeID>
	{
		private Iterator<NodeID> _iter;

		public SubRoleAwareNodeIDIterator(final Role role, final MultiMap<Role, NodeID> map)
		{
			final Collection<Role> subRoles = _node.getABox().getTBox().getRBox().getSubRoles(role);
			final List<Iterator<? extends NodeID>> iterators = new ArrayList<Iterator<? extends NodeID>>(subRoles.size());
			for (Role subRole : subRoles) {
				final Iterator<NodeID> mapValueIter = map.iterator(subRole);
				if (mapValueIter != null)
					iterators.add(mapValueIter);
			}
			_iter = IteratorUtils.chainedIterator(iterators);
		}

		@Override
		public boolean hasNext()
		{
			return _iter.hasNext();
		}

		@Override
		public NodeID next()
		{
			return _iter.next();
		}

		@Override
		public void remove()
		{
			_iter.remove();
		}
	}


	/**
	 * XXX - support inverse roles
	 *
	 */
	final class SubRoleAwarePairIterator
		implements Iterator<Pair<Role, NodeID>>
	{
		private final MultiMap<Role, NodeID> _map;
		private Iterator<Role> _assertedRoleIter;
		private Role _assertedRole;
		private Iterator<Role> _roleIter;
		private Role _currentRole;
		private Iterator<NodeID> _nodeIDIter;
		private Pair<Role, NodeID> _currentPair;

		public SubRoleAwarePairIterator(final MultiMap<Role, NodeID> map)
		{
			_map = map;
			_assertedRoleIter = map.keySet().iterator();
			advance();
		}

		private void advance()
		{
			/* XXX - this can be done more elegantly */
			final IRBox<Name, Klass, Role> rbox = _node.getABox().getTBox().getRBox();
			_currentPair = null;
			while (_currentPair == null) {
				if ((_assertedRole == null) && _assertedRoleIter.hasNext()) {
					_assertedRole = _assertedRoleIter.next();
					_roleIter = rbox.getSuperRoles(_assertedRole).iterator();
					_nodeIDIter = null;
					_currentRole = null;
				}
				if (_assertedRole != null) {
					if ((_currentRole == null) && _roleIter.hasNext()) {
						_currentRole = _roleIter.next();
						if (_map.get(_currentRole) != null)
							_nodeIDIter = _map.get(_currentRole).iterator();
						else
							_nodeIDIter = null;
					}
					if ((_currentRole != null) && (_nodeIDIter != null) && _nodeIDIter.hasNext())
						_currentPair = Pair.wrap(_currentRole, _nodeIDIter.next());
					else
						return;
				} else
					return;
			}
		}

		@Override
		public boolean hasNext()
		{
			return _currentPair != null;
		}

		@Override
		public Pair<Role, NodeID> next()
		{
			if (_currentPair == null) {
				throw new NoSuchElementException("No more linked nodes");
			} else {
				Pair<Role, NodeID> thisPair = _currentPair;
				advance();
				return thisPair;
			}
		}

		@Override
		public void remove()
		{
			_nodeIDIter.remove();
		}
	}
	/**
	 * The set of successor nodes, indexed by role.
	 *
	 */
	private AspectMultiMap<Role, NodeID, MultiMap<Role, NodeID>> _predecessors;
	/**
	 * The set of predecessor nodes, indexed by role.
	 *
	 */
	private AspectMultiMap<Role, NodeID, MultiMap<Role, NodeID>> _successors;
	/**
	 * The node
	 */
	private ABoxNode<Name, Klass, Role> _node;

	public LinkMap(final ABoxNode<Name, Klass, Role> node)
	{
		final IMultiMapFactory<Role, NodeID, MultiMap<Role, NodeID>> linkMapFactory = new MultiTreeSetHashMapFactory<Role, NodeID>();

		_node = node;

		if (node.isDatatypeNode())
			/*
			 * the successor list of a datatype node is always empty
			 */
			_successors = AspectMultiMap.decorate((MultiMap<Role, NodeID>) new EmptyMultiMap<Role, NodeID>(), _node);
		else
			_successors = AspectMultiMap.decorate(linkMapFactory.getInstance(), _node);
		_predecessors = AspectMultiMap.decorate(linkMapFactory.getInstance(), _node);
		addCollectionListeners();
	}

	protected LinkMap(final ABoxNode<Name, Klass, Role> node,
					  final MultiMap<Role, NodeID> realSuccessors,
					  final MultiMap<Role, NodeID> realPredecessors)
	{
		_node = node;
		_successors = AspectMultiMap.decorate(realSuccessors, node);
		_predecessors = AspectMultiMap.decorate(realPredecessors, node);
		addCollectionListeners();
	}


	private void addCollectionListeners()
	{
		if (!_node.isDatatypeNode()) {
			/*
			 * the successor list of a datatype node cannot be modified
			 */
			_successors.getListeners().add(_node.getABox().getCommon().getLinkMapListener());
		}
		_predecessors.getListeners().add(_node.getABox().getCommon().getLinkMapListener());
	}
	
	
	@Override
	public ABoxNode<Name, Klass, Role> getNode()
	{
		return _node;
	}

	@Override
	public Collection<Role> getOutgoingRoles()
	{
		SortedListSet<Role> outRoles = new SortedListSet<Role>();
		for (Role outRole : _successors.keySet()) {
			outRoles.add(outRole);
			for (Role superRole : _node.getABox().getTBox().getRBox().getSuperRoles(outRole)) {
				outRoles.add(superRole);
			}
		}
		return outRoles;
	}

	@Override
	public Collection<Role> getIncomingRoles()
	{
		SortedListSet<Role> inRoles = new SortedListSet<Role>();
		for (Role inRole : _predecessors.keySet()) {
			inRoles.add(inRole);
			for (Role superRole : _node.getABox().getTBox().getRBox().getSuperRoles(inRole)) {
				inRoles.add(superRole);
			}
		}
		return inRoles;
	}

	@Override
	public MultiMap<Role, NodeID> getAssertedSuccessors()
	{
		return _successors;
	}

	@Override
	public MultiMap<Role, NodeID> getAssertedPredecessors()
	{
		return _predecessors;
	}

	@Override
	public boolean hasSuccessor(Role role, NodeID successor)
	{
		if (_successors.containsValue(role, successor)) {
			/*
			 * short circuit path: role link is asserted
			 */
			return true;
		} else {
			for (Role superRole : _node.getABox().getTBox().getRBox().getSubRoles(role)) {
				if (_successors.containsValue(superRole, successor)) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public boolean hasSuccessor(Role role,
								IABoxNode<Name, Klass, Role> successor)
	{
		return hasSuccessor(role, successor.getNodeID());
	}

	@Override
	public boolean hasPredecessor(Role role, NodeID predecessor)
	{
		if (_predecessors.containsValue(role, predecessor)) {
			/*
			 * short circuit path: role link is asserted
			 */
			return true;
		} else {
			for (Role superRole : _node.getABox().getTBox().getRBox().getSubRoles(role)) {
				if (_predecessors.containsValue(superRole, predecessor)) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public boolean hasPredecessor(Role role,
								  IABoxNode<Name, Klass, Role> predecessor)
	{
		return hasPredecessor(role, predecessor.getNodeID());
	}

	@Override
	public Iterable<NodeID> getSuccessors(final Role role)
	{
		return new Iterable<NodeID>()
		{
			@Override
			public Iterator<NodeID> iterator()
			{
				return new SubRoleAwareNodeIDIterator(role, _successors);
			}
		};
	}

	@Override
	public Iterable<NodeID> getPredecessors(final Role role)
	{
		return new Iterable<NodeID>()
		{
			@Override
			public Iterator<NodeID> iterator()
			{
				return new SubRoleAwareNodeIDIterator(role, _predecessors);
			}
		};
	}

	@Override
	public Iterable<IABoxNode<Name, Klass, Role>> getSuccessorNodes(final Role role)
	{
		return new Iterable<IABoxNode<Name, Klass, Role>>()
		{
			@Override
			public Iterator<IABoxNode<Name, Klass, Role>> iterator()
			{
				return IteratorUtils.transformedIterator(getSuccessors(role).iterator(),
														 new Transformer<NodeID, IABoxNode<Name, Klass, Role>>()
				{
					@Override
					public IABoxNode<Name, Klass, Role> transform(NodeID input)
					{
						return _node.getABox().getNode(input);
					}
				});
			}
		};
	}

	@Override
	public Iterable<IABoxNode<Name, Klass, Role>> getPredecessorNodes(final Role role)
	{
		return new Iterable<IABoxNode<Name, Klass, Role>>()
		{
			@Override
			public Iterator<IABoxNode<Name, Klass, Role>> iterator()
			{
				return IteratorUtils.transformedIterator(getPredecessors(role).iterator(),
														 new Transformer<NodeID, IABoxNode<Name, Klass, Role>>()
				{
					@Override
					public IABoxNode<Name, Klass, Role> transform(NodeID input)
					{
						return _node.getABox().getNode(input);
					}
				});
			}
		};
	}

	@Override
	public Iterable<IABoxNode<Name, Klass, Role>> getPredecessorNodes()
	{
		return new Iterable<IABoxNode<Name, Klass, Role>>()
		{
			@Override
			public Iterator<IABoxNode<Name, Klass, Role>> iterator()
			{
				return IteratorUtils.transformedIterator(getPredecessors().iterator(),
														 new Transformer<NodeID, IABoxNode<Name, Klass, Role>>()
				{
					@Override
					public IABoxNode<Name, Klass, Role> transform(NodeID input)
					{
						return _node.getABox().getNode(input);
					}
				});
			}
		};
	}

	@Override
	public Iterable<IABoxNode<Name, Klass, Role>> getSuccessorNodes()
	{
		return new Iterable<IABoxNode<Name, Klass, Role>>()
		{
			@Override
			public Iterator<IABoxNode<Name, Klass, Role>> iterator()
			{
				return IteratorUtils.transformedIterator(getSuccessors().iterator(),
														 new Transformer<NodeID, IABoxNode<Name, Klass, Role>>()
				{
					@Override
					public IABoxNode<Name, Klass, Role> transform(NodeID input)
					{
						return _node.getABox().getNode(input);
					}
				});
			}
		};
	}

	@Override
	public ILinkMap<Name, Klass, Role> getImmutable()
	{
		return ImmutableLinkMap.decorate(this);
	}

	@Override
	public boolean hasSuccessor(Role role)
	{
		if (_successors.containsKey(role)) {
			/*
			 * short circuit path: role link is asserted
			 */
			return true;
		} else {
			for (Role superRole : _node.getABox().getTBox().getRBox().getSuperRoles(role)) {
				if (_successors.containsKey(superRole)) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public boolean hasPredecessor(Role role)
	{
		if (_predecessors.containsKey(role)) {
			/*
			 * short circuit path: role link is asserted
			 */
			return true;
		} else {
			for (Role superRole : _node.getABox().getTBox().getRBox().getSuperRoles(role)) {
				if (_predecessors.containsKey(superRole)) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public Iterable<Pair<Role, NodeID>> getSuccessorPairs()
	{
		return new Iterable<Pair<Role, NodeID>>()
		{
			@Override
			public Iterator<Pair<Role, NodeID>> iterator()
			{
				return new SubRoleAwarePairIterator(_successors);
			}
		};
	}

	@Override
	public Iterable<Pair<Role, NodeID>> getPredecessorPairs()
	{
		return new Iterable<Pair<Role, NodeID>>()
		{
			@Override
			public Iterator<Pair<Role, NodeID>> iterator()
			{
				return new SubRoleAwarePairIterator(_predecessors);
			}
		};
	}

	@Override
	public Set<NodeID> getPredecessors()
	{
		final Set<NodeID> preds = new SortedListSet<NodeID>();
		for (Pair<Role, NodeID> predPair : getPredecessorPairs()) {
			// XXX - maybe avoid creating the pair
			preds.add(predPair.getSecond());
		}
		return preds;
	}

	@Override
	public Set<NodeID> getSuccessors()
	{
		final Set<NodeID> succs = new SortedListSet<NodeID>();
		for (Pair<Role, NodeID> succPair : getSuccessorPairs()) {
			// XXX - maybe avoid creating the pair
			succs.add(succPair.getSecond());
		}
		return succs;
	}

	@Override
	public ILinkMap<Name, Klass, Role> clone(final IABoxNode<Name, Klass, Role> newNode)
	{
		assert newNode instanceof ABoxNode;
		final IMultiMapFactory<Role, NodeID, MultiMap<Role, NodeID>> linkMapFactory = new MultiTreeSetHashMapFactory<Role, NodeID>();

		MultiMap<Role, NodeID> realSuccessors;
		/*
		 * move past the AspectMultiMap, first
		 */
		realSuccessors =
			_successors.getDecoratee();
		while (realSuccessors instanceof CopyOnWriteMultiMap) {
			realSuccessors = ((CopyOnWriteMultiMap<Role, NodeID>) realSuccessors).getDecoratee();
		}
		assert !(realSuccessors instanceof CopyOnWriteMultiMap);


		final MultiMap<Role, NodeID> successors;
		final MultiMap<Role, NodeID> cloneSuccessors;
		/**
		 * Datatype nodes do not have a successor list. Do not clone it.
		 *
		 */
		if (_node.isDatatypeNode()) {
			assert realSuccessors instanceof EmptyMultiMap;
			successors = realSuccessors;
			cloneSuccessors = realSuccessors;
		} else {
			successors = CopyOnWriteMultiMap.decorate(realSuccessors, linkMapFactory);
			cloneSuccessors = CopyOnWriteMultiMap.decorate(realSuccessors, linkMapFactory);
		}

		MultiMap<Role, NodeID> realPredecessors;
		realPredecessors = _predecessors.getDecoratee();
		while (realPredecessors instanceof CopyOnWriteMultiMap) {
			realPredecessors = ((CopyOnWriteMultiMap<Role, NodeID>) realPredecessors).getDecoratee();
		}
		assert !(realPredecessors instanceof CopyOnWriteMultiMap);

		final MultiMap<Role, NodeID> predecessors = CopyOnWriteMultiMap.decorate(realPredecessors, linkMapFactory);
		final MultiMap<Role, NodeID> clonePredecessors = CopyOnWriteMultiMap.decorate(realPredecessors, linkMapFactory);

		_successors = AspectMultiMap.decorate(successors, this);
		_predecessors = AspectMultiMap.decorate(predecessors, this);

		final LinkMap<Name, Klass, Role> klone = new LinkMap<Name, Klass, Role>((ABoxNode<Name, Klass, Role>) newNode,
																				cloneSuccessors, clonePredecessors);
		return klone;
	}

	@Override
	public boolean deepEquals(Object obj)
	{
		if (obj instanceof ILinkMap) {
			@SuppressWarnings("unchecked")
			final ILinkMap<Name, Klass, Role> other = (ILinkMap<Name, Klass, Role>) obj;
			if (!MultiMapUtil.deepEquals(_successors, other.getAssertedSuccessors())) {
				return false;
			}
			if (!MultiMapUtil.deepEquals(_predecessors, other.getAssertedPredecessors())) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int deepHashCode()
	{
		int hashcode = 0;
		for (Map.Entry<Role, Collection<NodeID>> succEntry : _successors.entrySet()) {
			hashcode += 5 * succEntry.getKey().hashCode();
			for (NodeID succID : succEntry.getValue()) {
				hashcode += 6 * succID.hashCode();
			}
		}
		for (Map.Entry<Role, Collection<NodeID>> predEntry : _successors.entrySet()) {
			hashcode += 7 * predEntry.getKey().hashCode();
			for (NodeID predID : predEntry.getValue()) {
				hashcode += 8 * predID.hashCode();
			}
		}
		return hashcode;
	}
}