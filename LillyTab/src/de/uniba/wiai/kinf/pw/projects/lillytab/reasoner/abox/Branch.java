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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox;

import de.dhke.projects.cutil.collections.TransitiveHashMap;
import de.dhke.projects.cutil.collections.aspect.AbstractCollectionListener;
import de.dhke.projects.cutil.collections.aspect.CollectionEvent;
import de.dhke.projects.cutil.collections.aspect.CollectionItemEvent;
import de.dhke.projects.cutil.collections.aspect.CollectionItemReplacedEvent;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ABoxNodeEvent;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.INodeMergeListener;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermChangeEvent;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLNominalReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ITermSetListener;
import java.util.Map;

/**
 * <p> A branch represents a specific state in the reasoning process. It contains a (partially expanded) ABox as well as
 * various information needed to continue with the reasoning process. </p><p> Branches do support {@link Object#clone()}ing,
 * making it possible to open up a secondary decision path (i.e. a new branch). </p><p> Branches maintain two node
 * queues: The non-generating node queue available via {@link #getNonGeneratingQueue()} and the non-generating node
 * queue {@link #getGeneratingQueue()}. </p><p> This support is specified to {@link Reasoner}, as the implemented
 * algorithm first applies all non-generating rules (that do not generate a new node} and only if there are no more
 * applicable non-generating rules, start applying generating rules. </p><p> Both queues are sorted by the natural node
 * order. The next node on each queue is available via {@link #nextNonGeneratingNode() } and {@link #nextGeneratingNode()
 * }, respectively. Nodes may (re-) added to both queues at once via the
 * {@link #touch(java.lang.Comparable) }, {@link #touch(de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID) },
 * {@link #touchAll(java.util.Collection) },
 * {@link #touchNode(de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode)}, and {@link #touchNodes(java.util.Collection)
 * } methods. </p>
 *
 * @param <Name>
 * @param <Klass>
 * @param <Role>
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class Branch<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	implements Cloneable // Comparable<Branch<Name, Klass, Role>>
{

	private static final boolean TO_STRING_ID_ONLY = false;

	/// <editor-fold defaultstate="collapsed" desc="class NodeIDComparator">
	final class NodeIDComparator
		implements Comparator<NodeID> {

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
	 * <p> The node set listener is responsible for maintaining the proper association with the ABox' node set. </p>
	 */
	final class NodeSetListener
		extends AbstractCollectionListener<IABoxNode<Name, Klass, Role>, Collection<IABoxNode<Name, Klass, Role>>> {

		@Override
		public void afterCollectionCleared(
			CollectionEvent<IABoxNode<Name, Klass, Role>, Collection<IABoxNode<Name, Klass, Role>>> e)
		{
			_nonGeneratingQueue.clear();
			_generatingQueue.clear();
		}


		@Override
		public void beforeElementRemoved(
			CollectionItemEvent<IABoxNode<Name, Klass, Role>, Collection<IABoxNode<Name, Klass, Role>>> e)
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
			CollectionItemEvent<IABoxNode<Name, Klass, Role>, Collection<IABoxNode<Name, Klass, Role>>> e)
		{
			/*
			 * new nodes get added to both nodes queues immidiately
			 */
			_nonGeneratingQueue.add(e.getItem().getNodeID());
			_generatingQueue.add(e.getItem().getNodeID());
		}


		@Override
		public void beforeElementReplaced(
			CollectionItemReplacedEvent<IABoxNode<Name, Klass, Role>, Collection<IABoxNode<Name, Klass, Role>>> e)
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
		implements INodeMergeListener<Name, Klass, Role> {

		public void beforeNodeMerge(IABoxNode<Name, Klass, Role> source,
									IABoxNode<Name, Klass, Role> target)
		{
			assert _mergeMap != null;
			_mergeMap.put(source.getNodeID(), target.getNodeID());
		}
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="TermSetListener">
	final class TermSetListener
		implements ITermSetListener<Name, Klass, Role> {

		public void termAdded(TermChangeEvent<Name, Klass, Role> ev)
		{
			touchNode(ev.getNode());;
		}


		public void termRemoved(TermChangeEvent<Name, Klass, Role> ev)
		{
			touchNode(ev.getNode());;
		}


		public void termSetCleared(ABoxNodeEvent<Name, Klass, Role> ev)
		{
			touchNode(ev.getNode());;
		}
	}
	/// </editor-fold>
	/// <editor-fold defaultstate="collapsed" desc="private fields">
	private IABox<Name, Klass, Role> _abox;
	private final Comparator<NodeID> _nodeIDComparator = new NodeIDComparator();
	/*
	 * node queues
	 */
	private final SortedSet<NodeID> _nonGeneratingQueue = new TreeSet<NodeID>(_nodeIDComparator);
	private final SortedSet<NodeID> _generatingQueue = new TreeSet<NodeID>(_nodeIDComparator);
	private final NodeSetListener _nodeSetListener = new NodeSetListener();
	private final TermSetListener _termSetListener = new TermSetListener();
	private final NodeMergeListener _mergeListener = new NodeMergeListener();
	private Map<NodeID, NodeID> _mergeMap = null;
	/// </editor-fold>


	public Branch(final IABox<Name, Klass, Role> abox, final boolean enableMergeTracking)
	{
		if (enableMergeTracking)
			_mergeMap = new TransitiveHashMap<NodeID, NodeID>();

		setABox(abox);

		for (IABoxNode<Name, Klass, Role> node : _abox) {
			_nonGeneratingQueue.add(node.getNodeID());
			_generatingQueue.add(node.getNodeID());
		}
	}


	private Branch(final IABox<Name, Klass, Role> abox,
				   final Collection<NodeID> nonGeneratingQueue,
				   final Collection<NodeID> generatingQueue,
				   final Map<NodeID, NodeID> mergeMap)
	{
		if (mergeMap != null)
			_mergeMap = new TransitiveHashMap<NodeID, NodeID>(mergeMap);

		setABox(abox);
		_nonGeneratingQueue.addAll(nonGeneratingQueue);
		_generatingQueue.addAll(generatingQueue);
	}


	/**
	 * @return The branch's {@link ABox}.
	 */
	public IABox<Name, Klass, Role> getABox()
	{
		return _abox;
	}


	/**
	 * Update the ABox of the current branch and
	 *
	 * @param abox The new ABox
	 */
	private void setABox(final IABox<Name, Klass, Role> abox)
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
				if (_mergeMap != null)
					abox.getNodeMergeListeners().add(_mergeListener);
			}
			_abox = abox;
		}
	}


	public ReasonerResult<Name, Klass, Role> dispose()
	{
		final IABox<Name, Klass, Role> abox = getABox();

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
	 * <p> Create a new node in the specified branch and automatically add all global descriptions. </p><p> The new node
	 * is created anonymously at start, but the existence of nominals in the global descriptors may make it impossible
	 * to create a truly anonymous node. If the global descriptors contain a {@link IDLNominalReference}, a named node
	 * will be returned instead of an anonymous node. Additionally, the named node may not be new, but be an existing
	 * node from the {@link ABox}. </p>
	 *
	 * @return A (potentially new) node with a proper set of global descriptions.
	 */
	@Deprecated
	public IABoxNode<Name, Klass, Role> createNode(boolean isDatatypeNode)
		throws ENodeMergeException
	{
		IABoxNode<Name, Klass, Role> newNode = getABox().createNode(isDatatypeNode);
		assert getABox().contains(newNode);
		return newNode;
	}


	private IABoxNode<Name, Klass, Role> nextNode(final Collection<NodeID> queue)
	{
		IABoxNode<Name, Klass, Role> nextNode = null;
		while ((nextNode == null) && (!queue.isEmpty())) {
			final Iterator<NodeID> iter = queue.iterator();
			assert iter.hasNext();
			final NodeID nextNodeID = iter.next();
			iter.remove();
			nextNode = _abox.getNode(nextNodeID);
		}
		return nextNode;
	}


	public IABoxNode<Name, Klass, Role> nextNonGeneratingNode()
	{
		return nextNode(getNonGeneratingQueue());
	}


	public IABoxNode<Name, Klass, Role> nextGeneratingNode()
	{
		return nextNode(getGeneratingQueue());
	}


	/**
	 * @return the _nonGeneratingQueue
	 */
	protected SortedSet<NodeID> getNonGeneratingQueue()
	{
		return _nonGeneratingQueue;
	}


	/**
	 * @return the _generatingQueue
	 */
	protected SortedSet<NodeID> getGeneratingQueue()
	{
		return _generatingQueue;
	}

	/// <editor-fold defaultstate="collapsed" desc="Cloneable">

	private IABox<Name, Klass, Role> cloneABox()
	{
		if (_abox != null) {
			_abox.getNodeSetListeners().remove(_nodeSetListener);
			_abox.getTermSetListeners().remove(_termSetListener);
			if (_mergeMap != null)
				_abox.getNodeMergeListeners().remove(_mergeListener);;
			final IABox<Name, Klass, Role> aboxClone = _abox.clone();
			_abox.getNodeSetListeners().add(_nodeSetListener);
			_abox.getTermSetListeners().add(_termSetListener);
			_abox.getNodeMergeListeners().add(_mergeListener);
			return aboxClone;
		} else
			return null;
	}


	@Override
	public Branch<Name, Klass, Role> clone()
	{
		/*
		 * create clone
		 */
		final IABox<Name, Klass, Role> aboxClone = cloneABox();

		final Branch<Name, Klass, Role> klone = new Branch<Name, Klass, Role>(aboxClone,
																			  getNonGeneratingQueue(),
																			  getGeneratingQueue(),
																			  _mergeMap);

		return klone;
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="touch()" >

	public boolean touch(final Name name)
	{
		IABoxNode<Name, Klass, Role> node = _abox.getNode(name);
		return touchNode(node);
	}


	public boolean touch(final NodeID nodeID)
	{
		IABoxNode<Name, Klass, Role> node = _abox.getNode(nodeID);
		return touchNode(node);
	}


	public boolean touchAll(final Collection<NodeID> individuals)
	{
		boolean wasAdded = false;
		for (NodeID nodeID : individuals)
			wasAdded |= touch(nodeID);
		return wasAdded;
	}


	public boolean touchNode(final IABoxNode<Name, Klass, Role> node)
	{
		boolean wasAdded = false;
		assert node != null;
		assert _abox.contains(node);
		assert node.getABox() == _abox;
		/**
		 * XXX - TODO: This is currently inefficient for large aboxes
		 */
		if (!getNonGeneratingQueue().contains(node.getNodeID())) {
			getNonGeneratingQueue().add(node.getNodeID());
			wasAdded = true;
		}
		if (!getGeneratingQueue().contains(node.getNodeID())) {
			getGeneratingQueue().add(node.getNodeID());
			wasAdded = true;
		}
		return wasAdded;
	}


	public boolean touchNodes(final Collection<? extends IABoxNode<Name, Klass, Role>> nodes)
	{
		boolean wasAdded = false;
		for (IABoxNode<Name, Klass, Role> node : nodes) {
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
			if (getABox() != null)
				return getABox().toString();
			else
				return "(null ABox)";
		} else {
			final StringBuilder sb = new StringBuilder();
			sb.append(prefix);
			sb.append("Branch:\n");
			final String subPrefix = prefix + "\t";
			sb.append(subPrefix);
			sb.append(_abox.toString());
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
		final IABoxNode<Name, Klass, Role> node = _abox.getNode(nodeID);
		if (node != null) {
			wasRemoved = _nonGeneratingQueue.remove(nodeID);
			wasRemoved = _generatingQueue.remove(nodeID);
		}
		return wasRemoved;
	}


	public boolean removeNodeFromQueues(final IABoxNode<Name, Klass, Role> node)
	{
		return removeFromQueues(node.getNodeID());
	}


	public boolean removeFromQueues(final Collection<NodeID> nodeIDs)
	{
		boolean wasRemoved = false;
		wasRemoved |= getGeneratingQueue().removeAll(nodeIDs);
		wasRemoved |= getNonGeneratingQueue().removeAll(nodeIDs);
		return wasRemoved;
	}


	public boolean removeNodesFromQueues(final Collection<? extends IABoxNode<Name, Klass, Role>> nodes)
	{
		boolean wasRemoved = false;
		for (IABoxNode<Name, Klass, Role> node : nodes) {
			wasRemoved |= removeNodeFromQueues(node);
		}
		return wasRemoved;
	}

	/// <editor-fold defaultstate="collapsed" desc="Merge tracking">

	public Map<NodeID, NodeID> getMergeMap()
	{
		return _mergeMap;
	}
	/// </editor-fold>
}
