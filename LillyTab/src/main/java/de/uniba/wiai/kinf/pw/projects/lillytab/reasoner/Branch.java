/**
 * (c) 2009-2012 Otto-Friedrich-University Bamberg
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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner;

import de.dhke.projects.cutil.collections.aspect.AbstractCollectionListener;
import de.dhke.projects.cutil.collections.aspect.CollectionEvent;
import de.dhke.projects.cutil.collections.aspect.CollectionItemEvent;
import de.dhke.projects.cutil.collections.aspect.CollectionItemReplacedEvent;
import de.dhke.projects.cutil.collections.map.TransitiveHashMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ABoxNodeEvent;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.INodeMergeListener;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ITermSetListener;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermChangeEvent;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLIndividualReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A branch represents a specific state in the reasoning process. It contains a (partially expanded) ABox as well as
 * various information needed to continue with the reasoning process. <p /> Branches do support
 * {@link Object#clone()}ing, making it possible to open up a secondary decision path (i.e. a new branch). <p />
 * Branches maintain two node queues: The non-generating node queue available via {@link #getNonGeneratingQueue()} and
 * the non-generating node queue {@link #getGeneratingQueue()}. <p /> This support is specified to {@link Reasoner}, as
 * the implemented algorithm first applies all non-generating rules (that do not generate a new node} and only if there
 * are no more applicable non-generating rules, start applying generating rules.
 * <p /> Both queues are sorted by the natural node order. The next node on each queue is available via {@link #nextNonGeneratingNode()
 * } and {@link #nextGeneratingNode()
 * }, respectively. Nodes may (re-) added to both queues at once via the null {@link #touchLiteral(java.lang.Comparable) }, {@link #touch(de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID) },
 * {@link #touchAll(java.util.Collection) },
 * {@link #touchNode(de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode)}, and {@link #touchNodes(java.util.Collection)
 * } methods.
 *
 * @param <I> The type for nominals and values
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class Branch<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	implements Cloneable // Comparable<Branch<I, L, K, R>>
{

	private static final boolean TO_STRING_ID_ONLY = false;
	/// </editor-fold>
	private IABox<I, L, K, R> _abox;
	private final Comparator<NodeID> _nodeIDComparator = new NodeIDComparator();
	/*
	 * node queues
	 */
	private final SortedSet<NodeID> _nonGeneratingQueue = new TreeSet<>(_nodeIDComparator);
	private final SortedSet<NodeID> _generatingQueue = new TreeSet<>(_nodeIDComparator);
	private final NodeSetListener _nodeSetListener = new NodeSetListener();
	private final TermSetListener _termSetListener = new TermSetListener();
	private final NodeMergeListener _mergeListener = new NodeMergeListener();
	private Map<NodeID, NodeID> _mergeMap = null;
	private ConsistencyInfo<I, L, K, R> _consistencyInfo;


	public Branch(final IABox<I, L, K, R> abox, final boolean enableMergeTracking)
	{
		if (enableMergeTracking) {
			_mergeMap = new TransitiveHashMap<>();
		}

		setABox(abox);

		for (IABoxNode<I, L, K, R> node : _abox) {
			_nonGeneratingQueue.add(node.getNodeID());
			_generatingQueue.add(node.getNodeID());
		}
		_consistencyInfo = new ConsistencyInfo<>();
	}


	private Branch(final IABox<I, L, K, R> abox,
				   final Collection<NodeID> nonGeneratingQueue,
				   final Collection<NodeID> generatingQueue,
				   final Map<NodeID, NodeID> mergeMap)
	{
		if (mergeMap != null) {
			_mergeMap = new TransitiveHashMap<>(mergeMap);
		}

		setABox(abox);
		_nonGeneratingQueue.addAll(nonGeneratingQueue);
		_generatingQueue.addAll(generatingQueue);
		_consistencyInfo = new ConsistencyInfo<>();
	}


	public ConsistencyInfo<I, L, K, R> getConsistencyInfo()
	{
		return _consistencyInfo;
	}


	public ConsistencyInfo<I, L, K, R> upgradeConsistencyInfo(final ConsistencyInfo<I, L, K, R> cInfo)
	{
		_consistencyInfo = _consistencyInfo.updateFrom(cInfo);
		return _consistencyInfo;
	}


	/**
	 * @return The branch's {@link ABox}.
	 */
	public IABox<I, L, K, R> getABox()
	{
		return _abox;
	}


	/**
	 * Update the ABox of the current branch and update the branch-specified listeners of the ABoxes, if appropriate.
	 *
	 * @param abox The new ABox
	 */
	private void setABox(final IABox<I, L, K, R> abox)
	{
		if (abox != _abox) {
			if (_abox != null) {
				assert _nodeSetListener != null;
				_abox.getNodeSetListeners().remove(_nodeSetListener);
				assert _termSetListener != null;
				_abox.getTermSetListeners().remove(_termSetListener);
				if (_mergeMap != null) {
					assert _mergeListener != null;
					_abox.getNodeMergeListeners().remove(_mergeListener);
				}
				assert !_abox.getNodeMergeListeners().contains(_mergeListener);
			}
			if (abox != null) {
				abox.getNodeSetListeners().add(_nodeSetListener);
				abox.getTermSetListeners().add(_termSetListener);
				/*
				 * add node merge listener only if merge tracking is enabled
				 */
				if (_mergeMap != null) {
					abox.getNodeMergeListeners().add(_mergeListener);
				}
			}
			_abox = abox;
		}
	}


	public ReasonerResult<I, L, K, R> dispose()
	{
		final IABox<I, L, K, R> abox = getABox();

		final Map<NodeID, NodeID> mergeMap = _mergeMap;
		if (_mergeMap != null) {
			boolean removed = _abox.getNodeMergeListeners().remove(_mergeListener);
			assert removed;
			_mergeMap = null;

		}
		assert !_abox.getNodeMergeListeners().contains(_mergeListener);
		setABox(null);

		return ReasonerResult.create(abox, mergeMap);
	}


	/**
	 * Create a new node in the specified branch and automatically add all global descriptions. <p /> The new node is
	 * created anonymously at start, but the existence of nominals in the global descriptors may make it impossible to
	 * create a truly anonymous node. If the global descriptors contain a {@link IDLIndividualReference}, a named node
	 * will be returned instead of an anonymous node. Additionally, the named node may not be new, but be an existing
	 * node from the {@link ABox}.
	 *
	 * @param isDatatypeNode
	 * @return A (potentially new) node with a proper set of global descriptions.
	 *
	 * @throws ENodeMergeException
	 * @deprecated
	 */
	@Deprecated
	public IABoxNode<I, L, K, R> createNode(boolean isDatatypeNode)
		throws ENodeMergeException
	{
		IABoxNode<I, L, K, R> newNode = getABox().createNode(isDatatypeNode);
		assert getABox().contains(newNode);
		return newNode;
	}


	private IABoxNode<I, L, K, R> nextNode(final Collection<NodeID> queue)
	{
		IABoxNode<I, L, K, R> nextNode = null;
		while ((nextNode == null) && (!queue.isEmpty())) {
			final Iterator<NodeID> iter = queue.iterator();
			assert iter.hasNext();
			final NodeID nextNodeID = iter.next();
			iter.remove();
			nextNode = _abox.getNode(nextNodeID);
		}
		return nextNode;
	}


	public IABoxNode<I, L, K, R> nextNonGeneratingNode()
	{
		return nextNode(_nonGeneratingQueue);
	}


	public IABoxNode<I, L, K, R> nextGeneratingNode()
	{
		return nextNode(_generatingQueue);
	}


	/**
	 * @return the _nonGeneratingQueue
	 */
	protected SortedSet<NodeID> getNonGeneratingQueue()
	{
		return Collections.unmodifiableSortedSet(_nonGeneratingQueue);
	}


	/**
	 * @return the _generatingQueue
	 */
	protected SortedSet<NodeID> getGeneratingQueue()
	{
		return Collections.unmodifiableSortedSet(_generatingQueue);
	}

	/// <editor-fold defaultstate="collapsed" desc="Cloneable">

	private IABox<I, L, K, R> cloneABox()
	{
		if (_abox != null) {
			_abox.getNodeSetListeners().remove(_nodeSetListener);
			_abox.getTermSetListeners().remove(_termSetListener);
			if (_mergeMap != null) {
				_abox.getNodeMergeListeners().remove(_mergeListener);
			};
			final IABox<I, L, K, R> aboxClone = _abox.clone();
			_abox.getNodeSetListeners().add(_nodeSetListener);
			_abox.getTermSetListeners().add(_termSetListener);
			_abox.getNodeMergeListeners().add(_mergeListener);
			return aboxClone;
		} else {
			return null;
		}
	}


	@Override
	public Branch<I, L, K, R> clone()
	{
		/*
		 * create clone
		 */
		final IABox<I, L, K, R> aboxClone = cloneABox();

		final Branch<I, L, K, R> klone = new Branch<>(aboxClone,
													  getNonGeneratingQueue(),
													  getGeneratingQueue(),
													  _mergeMap);

		return klone;
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="touchLiteral()" >

	public boolean touchLiteral(final L literal)
	{
		final IABoxNode<I, L, K, R> node = _abox.getDatatypeNode(literal);
		return touchNode(node);
	}


	public boolean touchIndividual(final I individual)
	{
		final IABoxNode<I, L, K, R> node = _abox.getIndividualNode(individual);
		return touchNode(node);
	}


	public boolean touch(final NodeID nodeID)
	{
		IABoxNode<I, L, K, R> node = _abox.getNode(nodeID);
		return touchNode(node);
	}


	public boolean touchAll(final Collection<NodeID> individuals)
	{
		boolean wasAdded = false;
		for (NodeID nodeID : individuals) {
			wasAdded |= touch(nodeID);
		}
		return wasAdded;
	}


	public boolean touchNode(final IABoxNode<I, L, K, R> node)
	{
		boolean wasAdded = false;
		assert node != null;
		assert _abox.contains(node);
		assert node.getABox() == _abox;
		/**
		 * XXX - TODO: This is currently inefficient for large aboxes
		 */
		if (!_nonGeneratingQueue.contains(node.getNodeID())) {
			_nonGeneratingQueue.add(node.getNodeID());
			wasAdded = true;
		}
		if (!_generatingQueue.contains(node.getNodeID())) {
			_generatingQueue.add(node.getNodeID());
			wasAdded = true;
		}
		return wasAdded;
	}


	public boolean touchNodes(final Collection<? extends IABoxNode<I, L, K, R>> nodes)
	{
		boolean wasAdded = false;
		for (IABoxNode<I, L, K, R> node : nodes) {
			assert node != null;
			wasAdded |= touchNode(node);
		}
		return wasAdded;
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="toString()">

	@Override
	public String toString()
	{
		return toString("");
	}


	public String toString(int indent)
	{
		char[] fill = new char[indent];
		Arrays.fill(fill, ' ');
		return new String(fill);
	}


	public String toString(String prefix)
	{
		if (TO_STRING_ID_ONLY) {
			if (getABox() != null) {
				return getABox().toString();
			} else {
				return "(null ABox)";
			}
		} else {
			final StringBuilder sb = new StringBuilder();
			sb.append(prefix);
			sb.append("Branch:\n");
			final String subPrefix = prefix + "\t";
			sb.append(subPrefix);
			if (_abox != null) {
				sb.append(_abox.toString());
			}
			sb.append("\n");
			sb.append(subPrefix);
			sb.append("Generating Node Queue: ");
			sb.append(getGeneratingQueue().toString());
			sb.append("\n");
			sb.append(subPrefix);
			sb.append("Non-Generating Node Queue: ");
			sb.append(getNonGeneratingQueue().toString());
			sb.append("\n");
			sb.append(subPrefix);
			return sb.toString();
		}
	}
	/// </editor-fold>


	public boolean hasMoreNonGeneratingNodes()
	{
		return !_nonGeneratingQueue.isEmpty();
	}


	public boolean hasMoreGeneratingNodes()
	{
		return !_generatingQueue.isEmpty();
	}


	public boolean removeFromQueues(final NodeID nodeID)
	{
		boolean wasRemoved = false;
		final IABoxNode<I, L, K, R> node = _abox.getNode(nodeID);
		if (node != null) {
			wasRemoved = _nonGeneratingQueue.remove(nodeID);
			wasRemoved |= _generatingQueue.remove(nodeID);
		}
		return wasRemoved;
	}


	public boolean removeNodeFromQueues(final IABoxNode<I, L, K, R> node)
	{
		boolean wasRemoved = false;
		if (node != null) {
			final NodeID nodeID = node.getNodeID();
			wasRemoved = _nonGeneratingQueue.remove(nodeID);
			wasRemoved |= _generatingQueue.remove(nodeID);
		}
		return wasRemoved;
	}


	public boolean removeFromQueues(final Collection<NodeID> nodeIDs)
	{
		boolean wasRemoved = false;
		wasRemoved |= getGeneratingQueue().removeAll(nodeIDs);
		wasRemoved |= getNonGeneratingQueue().removeAll(nodeIDs);
		return wasRemoved;
	}


	public boolean removeNodesFromQueues(final Collection<? extends IABoxNode<I, L, K, R>> nodes)
	{
		boolean wasRemoved = false;
		for (IABoxNode<I, L, K, R> node : nodes) {
			wasRemoved |= removeNodeFromQueues(node);
		}
		return wasRemoved;
	}


	public boolean clearQueues()
	{
		boolean cleared = false;
		if (!_nonGeneratingQueue.isEmpty()) {
			_nonGeneratingQueue.clear();
			cleared = true;
		}
		if (!_generatingQueue.isEmpty()) {
			_nonGeneratingQueue.clear();
			cleared = true;
		}
		return cleared;
	}

	/// <editor-fold defaultstate="collapsed" desc="Merge tracking">

	public Map<NodeID, NodeID> getMergeMap()
	{
		return Collections.unmodifiableMap(_mergeMap);
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="class NodeIDComparator">
	final class NodeIDComparator
		implements Comparator<NodeID> {

		@Override
		public int compare(NodeID o1, NodeID o2)
		{
			assert _abox.getNode(o1) != null;
			assert _abox.getNode(o2) != null;
			return _abox.getNode(o1).compareTo(_abox.getNode(o2));
		}
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="class NodeSetListener">
	/**
	 * The node set listener is responsible for maintaining the proper association with the ABox' node set.
	 */
	final class NodeSetListener
		extends AbstractCollectionListener<IABoxNode<I, L, K, R>, Collection<IABoxNode<I, L, K, R>>> {

		@Override
		public void afterCollectionCleared(
			CollectionEvent<IABoxNode<I, L, K, R>, Collection<IABoxNode<I, L, K, R>>> e)
		{
			_nonGeneratingQueue.clear();
			_generatingQueue.clear();
		}


		@Override
		public void beforeElementRemoved(
			CollectionItemEvent<IABoxNode<I, L, K, R>, Collection<IABoxNode<I, L, K, R>>> e)
		{
			/**
			 * It is important to do this before the actual removal, as the queues cannot contain nodes that are not
			 * also contained inside the associated ABox as well.
			 *
			 * This condition is also checked via assertions.
			 */
			_nonGeneratingQueue.remove(e.getItem().getNodeID());
			_generatingQueue.remove(e.getItem().getNodeID());
		}


		@Override
		public void afterElementAdded(
			CollectionItemEvent<IABoxNode<I, L, K, R>, Collection<IABoxNode<I, L, K, R>>> e)
		{
			/*
			 * new nodes get added to both nodes queues immidiately
			 */
			_nonGeneratingQueue.add(e.getItem().getNodeID());
			_generatingQueue.add(e.getItem().getNodeID());
		}


		@Override
		public void beforeElementReplaced(
			CollectionItemReplacedEvent<IABoxNode<I, L, K, R>, Collection<IABoxNode<I, L, K, R>>> e)
		{
			_nonGeneratingQueue.remove(e.getItem().getNodeID());
			_generatingQueue.remove(e.getItem().getNodeID());
			_nonGeneratingQueue.add(e.getNewItem().getNodeID());
			_generatingQueue.add(e.getNewItem().getNodeID());
		}
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collaped" desc="class NodeMergeListener">
	final class NodeMergeListener
		implements INodeMergeListener<I, L, K, R> {

		@Override
		public void beforeNodeMerge(IABoxNode<I, L, K, R> source,
									IABoxNode<I, L, K, R> target)
		{
			assert _mergeMap != null;
			_mergeMap.put(source.getNodeID(), target.getNodeID());
		}
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="class TermSetListener">
	final class TermSetListener
		implements ITermSetListener<I, L, K, R> {

		@Override
		public void termAdded(TermChangeEvent<I, L, K, R> ev)
		{
			touchNode(ev.getNode());
		}


		@Override
		public void termRemoved(TermChangeEvent<I, L, K, R> ev)
		{
			touchNode(ev.getNode());
		}


		@Override
		public void termSetCleared(ABoxNodeEvent<I, L, K, R> ev)
		{
			touchNode(ev.getNode());
		}
	}
}
