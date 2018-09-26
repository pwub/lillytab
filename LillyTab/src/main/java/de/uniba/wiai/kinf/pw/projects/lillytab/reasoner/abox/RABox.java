/**
 * (c) 2009-2014 Otto-Friedrich-University Bamberg
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

import de.dhke.projects.cutil.ComparablePair;
import de.dhke.projects.cutil.Pair;
import de.dhke.projects.cutil.collections.CollectionUtil;
import de.dhke.projects.cutil.collections.ExtractorCollection;
import de.dhke.projects.cutil.collections.MultiMapUtil;
import de.dhke.projects.cutil.collections.iterator.MultiMapPairIterable;
import de.dhke.projects.cutil.collections.set.Flat3Set;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IDatatypeABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ILinkMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IRABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.immutable.ImmutableRABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IRBox;
import java.util.Collection;
import java.util.Map;
import java.util.Set;


/**
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public class RABox<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	implements IRABox<I, L, K, R> {
	/// </editor-fold>
	/**
	 * The set of successor nodes, indexed by role.
	 *
	 */
	private LinkMap<I, L, K, R> _predecessors;
	/**
	 * The set of predecessor nodes, indexed by role.
	 *
	 */
	private LinkMap<I, L, K, R> _successors;
	/**
	 * The node
	 */
	private IABoxNode<I, L, K, R> _node;

	public RABox(final IABoxNode<I, L, K, R> node)
	{
		_node = node;

		if (node instanceof IDatatypeABoxNode) /*
		 * the successor list of a datatype node is always empty
		 */ {
			_successors = new LinkMap<>(node, true);
		} else {
			_successors = new LinkMap<>(node);
		}
		_predecessors = new LinkMap<>(node);
	}

	protected RABox(final IABoxNode<I, L, K, R> node,
					final LinkMap<I, L, K, R> predMap,
					final LinkMap<I, L, K, R> succMap)
	{
		_node = node;
		_predecessors = predMap;
		_successors = succMap;
	}

	@Override
	public IABoxNode<I, L, K, R> getNode()
	{
		return _node;
	}

	@Override
	public ILinkMap<I, L, K, R> getAssertedSuccessors()
	{
		return _successors;
	}

	@Override
	public ILinkMap<I, L, K, R> getAssertedPredecessors()
	{
		return _predecessors;
	}

	@Override
	public boolean hasSuccessor(R role, NodeID successor)
	{
		final IRBox<I, L, K, R> rbox = _node.getABox().getTBox().getRBox();

		if (_successors.containsValue(role, successor)) {
			return true;
		} else {
			if (rbox.getSubRoles(role).stream().anyMatch((R superRole) -> {
				return _successors.containsValue(superRole, successor);
			})) {
				return true;
			} else {
				return rbox.getInverseRoles(role).stream().anyMatch((R invRole) -> {
					return _predecessors.containsValue(invRole, successor);
				});
			}
		}
	}

	@Override
	public boolean hasSuccessor(R role)
	{
		final IRBox<I, L, K, R> rbox = _node.getABox().getTBox().getRBox();

		if (_successors.containsKey(role)) {
			return true;
		} else {
			if (rbox.getSubRoles(role).stream().anyMatch((R superRole) -> {
				return _successors.containsKey(superRole);
			})) {
				return true;
			} else {
				return rbox.getInverseRoles(role).stream().anyMatch((R invRole) -> {
					return _predecessors.containsKey(invRole);
				});
			}
		}
	}

	@Override
	public boolean hasSuccessor(R role, IABoxNode<I, L, K, R> successor)
	{
		return hasSuccessor(role, successor.getNodeID());
	}

	@Override
	public boolean hasPredecessor(R role, NodeID predecessor)
	{
		final IRBox<I, L, K, R> rbox = _node.getABox().getTBox().getRBox();

		if (_predecessors.containsValue(role, predecessor)) {
			return true;
		} else {
			if (rbox.getSubRoles(role).stream().anyMatch((R superRole) -> {
				return _predecessors.containsValue(superRole, predecessor);
			})) {
				return true;
			} else {
				return rbox.getInverseRoles(role).stream().anyMatch((R invRole) -> {
					return _successors.containsValue(invRole, predecessor);
				});
			}
		}
	}

	@Override
	public boolean hasPredecessor(R role)
	{
		final IRBox<I, L, K, R> rbox = _node.getABox().getTBox().getRBox();

		if (_predecessors.containsKey(role)) {
			return true;
		} else {
			if (rbox.getSubRoles(role).stream().anyMatch((R superRole) -> {
				return _predecessors.containsKey(superRole);
			})) {
				return true;
			} else {
				return rbox.getInverseRoles(role).stream().anyMatch((R invRole) -> {
					return _successors.containsKey(invRole);
				});
			}
		}
	}

	@Override
	public boolean hasPredecessor(R role, IABoxNode<I, L, K, R> predecessor)
	{
		return hasPredecessor(role, predecessor.getNodeID());
	}

	@Override
	public Collection<R> getOutgoingRoles()
	{
		final IRBox<I, L, K, R> rbox = _node.getABox().getTBox().getRBox();

		final Set<R> outRoles = new Flat3Set<>();
		for (R outRole : _successors.keySet()) {
			outRoles.add(outRole);
			outRoles.addAll(rbox.getEquivalentRoles(outRole));
			outRoles.addAll(rbox.getSuperRoles(outRole));
		}
		for (R inRole : _predecessors.keySet()) {
			outRoles.addAll(rbox.getInverseRoles(inRole));
		}
		return outRoles;
	}

	@Override
	public Iterable<R> getOutgoingRoles(IABoxNode<I, L, K, R> target)
	{
		return getOutgoingRoles(target.getNodeID());
	}

	@Override
	public Iterable<R> getOutgoingRoles(NodeID target)
	{
		final IRBox<I, L, K, R> rbox = _node.getABox().getTBox().getRBox();

		final Set<R> outRoles = new Flat3Set<>();
		for (Pair<R, NodeID> succPair : MultiMapPairIterable.decorate(_successors)) {
			if (succPair.getSecond().equals(target)) {
				outRoles.add(succPair.getFirst());
				outRoles.addAll(rbox.getEquivalentRoles(succPair.getFirst()));
				outRoles.addAll(rbox.getSuperRoles(succPair.getFirst()));
			}
		}
		for (Pair<R, NodeID> predPair : MultiMapPairIterable.decorate(_predecessors)) {
			if (predPair.getSecond().equals(target)) {
				outRoles.addAll(rbox.getInverseRoles(predPair.getFirst()));
			}
		}
		return outRoles;
	}

	@Override
	public Collection<R> getIncomingRoles()
	{
		final IRBox<I, L, K, R> rbox = _node.getABox().getTBox().getRBox();

		final Set<R> inRoles = new Flat3Set<>();
		for (R inRole : _predecessors.keySet()) {
			inRoles.add(inRole);
			inRoles.addAll(rbox.getEquivalentRoles(inRole));
			inRoles.addAll(rbox.getSuperRoles(inRole));
		}
		for (R inRole : _successors.keySet()) {
			inRoles.addAll(rbox.getInverseRoles(inRole));
		}
		return inRoles;
	}

	@Override
	public Iterable<R> getIncomingRoles(IABoxNode<I, L, K, R> source)
	{
		return getIncomingRoles(source.getNodeID());
	}

	@Override
	public Iterable<R> getIncomingRoles(NodeID source)
	{
		final IRBox<I, L, K, R> rbox = _node.getABox().getTBox().getRBox();

		final Set<R> outRoles = new Flat3Set<>();
		for (Pair<R, NodeID> predPair : MultiMapPairIterable.decorate(_predecessors)) {
			if (predPair.getSecond().equals(source)) {
				outRoles.add(predPair.getFirst());
				outRoles.addAll(rbox.getEquivalentRoles(predPair.getFirst()));
				outRoles.addAll(rbox.getSuperRoles(predPair.getFirst()));
			}
		}
		for (Pair<R, NodeID> succPair : MultiMapPairIterable.decorate(_successors)) {
			if (succPair.getSecond().equals(source)) {
				outRoles.addAll(rbox.getInverseRoles(succPair.getFirst()));
			}
		}
		return outRoles;
	}

	@Override
	public Collection<NodeID> getSuccessors(R role)
	{
		final Set<NodeID> successors = new Flat3Set<>();

		getSuccessorPairs().stream().filter((Pair<R, NodeID> pair) -> {
			return pair.getFirst().equals(role);
		}).forEach((Pair<R, NodeID> pair) -> {
			successors.add(pair.getSecond());
		});
		return successors;
	}

	@Override
	public Collection<NodeID> getSuccessors()
	{
		final Set<NodeID> successors = new Flat3Set<>(
			ExtractorCollection.decorate(getSuccessorPairs(), Pair::getSecond)
		);
		return successors;
	}

	@Override
	public Collection<NodeID> getPredecessors(R role)
	{
		final Set<NodeID> predecessors = new Flat3Set<>();

		getPredecessorPairs().stream().filter((Pair<R, NodeID> pair) -> {
			return pair.getFirst().equals(role);
		}).forEach((Pair<R, NodeID> pair) -> {
			predecessors.add(pair.getSecond());
		});
		return predecessors;
	}

	@Override
	public Collection<NodeID> getPredecessors()
	{
		final IRBox<I, L, K, R> rbox = _node.getABox().getTBox().getRBox();
		final Set<NodeID> predecessors = new Flat3Set<>();
		for (Pair<R, NodeID> succPair : MultiMapPairIterable.decorate(_predecessors)) {
			predecessors.add(succPair.getSecond());
		}
		for (Pair<R, NodeID> predPair : MultiMapPairIterable.decorate(_successors)) {
			/* for all roles in the predecessor link map that have an inverse, we also have a successor */
			if (!CollectionUtil.isNullOrEmpty(rbox.getInverseRoles(predPair.getFirst()))) {
				predecessors.add(predPair.getSecond());
			}
		}

		return predecessors;
	}

	@Override
	public Collection<ComparablePair<R, NodeID>> getPredecessorPairs()
	{
		final Collection<ComparablePair<R, NodeID>> predecessors = new Flat3Set<>();

		final IRBox<I, L, K, R> rbox = _node.getABox().getTBox().getRBox();
		for (Pair<R, NodeID> predPair : MultiMapPairIterable.decorate(_predecessors)) {
			predecessors.add(new ComparablePair<>(predPair.getFirst(), predPair.getSecond()));
			rbox.getSuperRoles(predPair.getFirst()).stream().forEach((R superRole) -> {
				predecessors.add(new ComparablePair<>(superRole, predPair.getSecond()));
			});
		}
		for (Pair<R, NodeID> succPair : MultiMapPairIterable.decorate(_successors)) {
			/* for all roles in the succcessor link map that have an inverse, we also have a predecessor */
			for (R invRole : rbox.getInverseRoles(succPair.getFirst())) {
				predecessors.add(new ComparablePair<>(invRole, succPair.getSecond()));
			}
		}

		return predecessors;
	}

	@Override
	public Collection<ComparablePair<R, NodeID>> getSuccessorPairs()
	{
		final Collection<ComparablePair<R, NodeID>> successors = new Flat3Set<>();

		final IRBox<I, L, K, R> rbox = _node.getABox().getTBox().getRBox();
		for (Pair<R, NodeID> succPair : MultiMapPairIterable.decorate(_successors)) {
			successors.add(new ComparablePair<>(succPair.getFirst(), succPair.getSecond()));
			rbox.getSuperRoles(succPair.getFirst()).stream().forEach((R superRole) -> {
				successors.add(new ComparablePair<>(superRole, succPair.getSecond()));
			});
		}
		for (Pair<R, NodeID> predPair : MultiMapPairIterable.decorate(_predecessors)) {
			/* for all roles in the predecessor link map that have an inverse, we also have a successor */
			for (R invRole : rbox.getInverseRoles(predPair.getFirst())) {
				successors.add(new ComparablePair<>(invRole, predPair.getSecond()));
			}
		}

		return successors;
	}

	@Override
	public Collection<IABoxNode<I, L, K, R>> getSuccessorNodes(R role)
	{
		return ExtractorCollection.decorate(getSuccessors(role), _node.getABox()::getNode);
	}

	@Override
	public Collection<IABoxNode<I, L, K, R>> getSuccessorNodes()
	{
		return ExtractorCollection.decorate(getSuccessors(), _node.getABox()::getNode);
	}

	@Override
	public Collection<IABoxNode<I, L, K, R>> getPredecessorNodes(R role)
	{
		return ExtractorCollection.decorate(getPredecessors(role), _node.getABox()::getNode);
	}

	@Override
	public Collection<IABoxNode<I, L, K, R>> getPredecessorNodes()
	{
		return ExtractorCollection.decorate(getPredecessors(), _node.getABox()::getNode);
	}

	@Override
	public IRABox<I, L, K, R> getImmutable()
	{
		return ImmutableRABox.decorate(_node.getImmutable(), this);
	}

	@Override
	public int deepHashCode()
	{
		int hashcode = 0;
		for (Map.Entry<R, Collection<NodeID>> succEntry : _successors.entrySet()) {
			hashcode += 5 * succEntry.getKey().hashCode();
			for (NodeID succID : succEntry.getValue()) {
				hashcode += 6 * succID.hashCode();
			}
		}
		for (Map.Entry<R, Collection<NodeID>> predEntry : _successors.entrySet()) {
			hashcode += 7 * predEntry.getKey().hashCode();
			for (NodeID predID : predEntry.getValue()) {
				hashcode += 8 * predID.hashCode();
			}
		}
		return hashcode;
	}

	@Override
	public boolean deepEquals(Object obj
	)
	{
		if (obj instanceof IRABox) {
			@SuppressWarnings("unchecked")
			final IRABox<I, L, K, R> other = (IRABox<I, L, K, R>) obj;
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
	public IRABox<I, L, K, R> clone(final IABoxNode<I, L, K, R> newNode
	)
	{
		assert newNode instanceof IABoxNode;

		final RABox<I, L, K, R> klone = new RABox<>(newNode,
			_predecessors.clone(newNode), _successors.clone(newNode));
		return klone;
	}

}
