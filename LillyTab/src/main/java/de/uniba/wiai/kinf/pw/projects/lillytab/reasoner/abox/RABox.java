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
import de.dhke.projects.cutil.collections.CollectionUtil;
import de.dhke.projects.cutil.collections.ExtractorCollection;
import de.dhke.projects.cutil.collections.MultiMapUtil;
import de.dhke.projects.cutil.collections.iterator.MultiMapEntryIterator;
import de.dhke.projects.cutil.collections.set.SortedListSet;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ILinkMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IRABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.immutable.ImmutableRABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IRBox;
import java.util.*;
import org.apache.commons.collections15.IteratorUtils;
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.Transformer;

/**
 *
 * @param <Name> The type for nominals and values
 * @param <Klass> The type for DL classes
 * @param <Role> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class RABox<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	implements IRABox<Name, Klass, Role> {

	/// <editor-fold defaultstate="collapsed" desc="class SubRoleAwareNodeIDIterator">
	/**
	 *
	 */
	final class SubRoleAwareNodeIDIterator
		implements Iterator<NodeID> {

		private Iterator<NodeID> _iter;


		SubRoleAwareNodeIDIterator(final Role role, final MultiMap<Role, NodeID> map)
		{
			final Collection<Role> subRoles = _node.getABox().getTBox().getRBox().getSubRoles(role);
			final List<Iterator<? extends NodeID>> iterators = new ArrayList<>(subRoles.size());
			for (Role subRole : subRoles) {
				final Iterator<NodeID> mapValueIter = map.iterator(subRole);
				if (mapValueIter != null) {
					iterators.add(mapValueIter);
				}
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
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="class SubRoleAwarePairIterator">
	/**
	 * XXX - support inverse roles
	 *
	 */
	final class SubRoleAwarePairIterator
		implements Iterator<Pair<Role, NodeID>> {

		private Iterator<Map.Entry<Role, NodeID>> _assertedIter;
		private Iterator<Role> _roleIter;
		private Pair<Role, NodeID> _currentPair;


		SubRoleAwarePairIterator(final MultiMap<Role, NodeID> map)
		{
			_assertedIter = MultiMapEntryIterator.decorate(map);
			advance();
		}


		private void advance()
		{
			if (CollectionUtil.isNullOrEmpty(_roleIter)) {
				if (CollectionUtil.isNullOrEmpty(_assertedIter)) {
					_currentPair = null;
				} else {
					final Map.Entry<Role, NodeID> nextEntry = _assertedIter.next();
					final IRBox<Name, Klass, Role> rbox = _node.getABox().getRBox();
					_roleIter = rbox.getSuperRoles(nextEntry.getKey()).iterator();
					_currentPair = Pair.wrap(_roleIter.next(), nextEntry.getValue());
				}
			} else {
				final NodeID nodeID = _currentPair.getSecond();
				_currentPair = Pair.wrap(_roleIter.next(), nodeID);
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
			throw new UnsupportedOperationException();
		}
	}
	/// </editor-fold>
	/**
	 * The set of successor nodes, indexed by role.
	 *
	 */
	private LinkMap<Name, Klass, Role> _predecessors;
	/**
	 * The set of predecessor nodes, indexed by role.
	 *
	 */
	private LinkMap<Name, Klass, Role> _successors;
	/**
	 * The node
	 */
	private IABoxNode<Name, Klass, Role> _node;


	public RABox(final IABoxNode<Name, Klass, Role> node)
	{
		_node = node;

		if (node.isDatatypeNode()) /*
		 * the successor list of a datatype node is always empty
		 */ {
			_successors = new LinkMap<>(node, true);
		} else {
			_successors = new LinkMap<>(node);
		}
		_predecessors = new LinkMap<>(node);
	}


	protected RABox(final IABoxNode<Name, Klass, Role> node,
					final LinkMap<Name, Klass, Role> predMap,
					final LinkMap<Name, Klass, Role> succMap)
	{
		_node = node;
		_predecessors = predMap;
		_successors = succMap;
	}


	@Override
	public IABoxNode<Name, Klass, Role> getNode()
	{
		return _node;
	}


	@Override
	public Collection<Role> getOutgoingRoles()
	{
		SortedListSet<Role> outRoles = new SortedListSet<>();
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
		SortedListSet<Role> inRoles = new SortedListSet<>();
		for (Role inRole : _predecessors.keySet()) {
			inRoles.add(inRole);
			for (Role superRole : _node.getABox().getTBox().getRBox().getSuperRoles(inRole)) {
				inRoles.add(superRole);
			}
		}
		return inRoles;
	}


	@Override
	public ILinkMap<Name, Klass, Role> getAssertedSuccessors()
	{
		return _successors;
	}


	@Override
	public ILinkMap<Name, Klass, Role> getAssertedPredecessors()
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
			// short circuit path: role link is asserted
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
	public Collection<NodeID> getSuccessors(final Role role)
	{
		final Set<NodeID> succs = new SortedListSet<>();
		final Iterator<NodeID> iter = new SubRoleAwareNodeIDIterator(role, _successors);
		while (iter.hasNext()) {
			succs.add(iter.next());
		}

		return succs;
	}


	@Override
	public Collection<NodeID> getPredecessors(final Role role)
	{
		final Set<NodeID> preds = new SortedListSet<>();
		final Iterator<NodeID> iter = new SubRoleAwareNodeIDIterator(role, _predecessors);
		while (iter.hasNext()) {
			preds.add(iter.next());
		}

		return preds;
	}


	@Override
	public Collection<IABoxNode<Name, Klass, Role>> getSuccessorNodes(final Role role)
	{
		return ExtractorCollection.decorate(getSuccessors(role),
											new Transformer<NodeID, IABoxNode<Name, Klass, Role>>() {
			@Override
			public IABoxNode<Name, Klass, Role> transform(NodeID input)
			{
				return _node.getABox().getNode(input);
			}
		});
	}


	@Override
	public Collection<IABoxNode<Name, Klass, Role>> getPredecessorNodes(final Role role)
	{
		return ExtractorCollection.decorate(getPredecessors(role),
											new Transformer<NodeID, IABoxNode<Name, Klass, Role>>() {
			@Override
			public IABoxNode<Name, Klass, Role> transform(NodeID input)
			{
				return _node.getABox().getNode(input);
			}
		});
	}


	@Override
	public Collection<IABoxNode<Name, Klass, Role>> getPredecessorNodes()
	{
		return ExtractorCollection.decorate(getPredecessors(),
											new Transformer<NodeID, IABoxNode<Name, Klass, Role>>() {
			@Override
			public IABoxNode<Name, Klass, Role> transform(NodeID input)
			{
				return _node.getABox().getNode(input);
			}
		});
	}


	@Override
	public Collection<IABoxNode<Name, Klass, Role>> getSuccessorNodes()
	{
		return ExtractorCollection.decorate(getSuccessors(),
											new Transformer<NodeID, IABoxNode<Name, Klass, Role>>() {
			@Override
			public IABoxNode<Name, Klass, Role> transform(NodeID input)
			{
				return _node.getABox().getNode(input);
			}
		});
	}


	@Override
	public IRABox<Name, Klass, Role> getImmutable()
	{
		return ImmutableRABox.decorate(this);
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
		return new Iterable<Pair<Role, NodeID>>() {
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
		return new Iterable<Pair<Role, NodeID>>() {
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
		final Set<NodeID> preds = new SortedListSet<>();
		for (Pair<Role, NodeID> predPair : getPredecessorPairs()) {
			// XXX - maybe avoid creating the pair
			preds.add(predPair.getSecond());
		}
		return preds;
	}


	@Override
	public Set<NodeID> getSuccessors()
	{
		final Set<NodeID> succs = new SortedListSet<>();
		for (Pair<Role, NodeID> succPair : getSuccessorPairs()) {
			// XXX - maybe avoid creating the pair
			succs.add(succPair.getSecond());
		}
		return succs;
	}


	@Override
	public IRABox<Name, Klass, Role> clone(final IABoxNode<Name, Klass, Role> newNode)
	{
		assert newNode instanceof IABoxNode;

		final RABox<Name, Klass, Role> klone = new RABox<>(newNode,
														   _predecessors.clone(newNode), _successors.clone(newNode));
		return klone;
	}


	@Override
	public boolean deepEquals(Object obj)
	{
		if (obj instanceof IRABox) {
			@SuppressWarnings("unchecked")
			final IRABox<Name, Klass, Role> other = (IRABox<Name, Klass, Role>) obj;
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
