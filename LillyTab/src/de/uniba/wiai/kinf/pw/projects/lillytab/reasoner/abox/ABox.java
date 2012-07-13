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

import de.dhke.projects.cutil.Pair;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IBlockingStateCache;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.INodeMergeListener;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.dhke.projects.cutil.collections.factories.ICollectionFactory;
import de.dhke.projects.cutil.collections.factories.IMultiMapFactory;
import de.dhke.projects.cutil.collections.aspect.AbstractCollectionListener;
import de.dhke.projects.cutil.collections.aspect.AspectSortedSet;
import de.dhke.projects.cutil.collections.aspect.CollectionEvent;
import de.dhke.projects.cutil.collections.aspect.CollectionItemEvent;
import de.dhke.projects.cutil.collections.aspect.ICollectionListener;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ABoxNodeEvent;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.BlockingStateCache;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IBlockingStrategy;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ITermSetListener;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IUnfoldListener;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermChangeEvent;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntryFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLNominalReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRoleOperator;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.TermUtil;
import de.uniba.wiai.kinf.pw.projects.lillytab.util.LinearSequenceNumberGenerator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.Predicate;

/**
 * <p> In-memory copy-on-write implementation of the {@link IABox} interface. </p><p> </p>
 *
 * @param <Name> Type for named individuals
 * @param <Klass> Type for class names
 * @param <Role> Type for role names
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ABox<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	implements IABox<Name, Klass, Role> {

	private static final long serialVersionUID = 3990522176232016954L;
	private static final boolean TO_STRING_ID_ONLY = false;

	/// <editor-fold defaultstate="collapsed" desc="class NodeSetListener">
	/**
	 * Listen to modifications of the internal node set and update the node map accordingly.
	 */
	private class NodeSetListener
		extends AbstractCollectionListener<IABoxNode<Name, Klass, Role>, Collection<IABoxNode<Name, Klass, Role>>> {

		@Override
		public void afterCollectionCleared(
			CollectionEvent<IABoxNode<Name, Klass, Role>, Collection<IABoxNode<Name, Klass, Role>>> e)
		{
			_nodeMap.clear();
		}


		@Override
		public void beforeElementAdded(
			CollectionItemEvent<IABoxNode<Name, Klass, Role>, Collection<IABoxNode<Name, Klass, Role>>> e)
		{
			/**
			 * Check node map consistency before add.
			 */
			final IABoxNode<Name, Klass, Role> node = e.getItem();
			if (_nodeMap.containsKey(node.getNodeID()))
				throw new IllegalArgumentException(
					"Node with id " + node.getNodeID().toString() + " already in node map.");
			else {
				for (Name name : node.getNames()) {
					if (_nodeMap.containsKey(name))
						throw new IllegalArgumentException("Node with name " + name + " already in node map.");
				}
			}
		}


		@Override
		public void afterElementAdded(
			CollectionItemEvent<IABoxNode<Name, Klass, Role>, Collection<IABoxNode<Name, Klass, Role>>> e)
		{
			assert e.getItem() instanceof ABoxNode;
			final ABoxNode<Name, Klass, Role> node = (ABoxNode<Name, Klass, Role>) e.getItem();
			node.setABox(ABox.this);
			_nodeMap.put(node.getNodeID(), node);
			for (Name name : node.getNames())
				_nodeMap.put(name, node);
		}


		@Override
		public void afterElementRemoved(
			CollectionItemEvent<IABoxNode<Name, Klass, Role>, Collection<IABoxNode<Name, Klass, Role>>> e)
		{
			assert e.getItem() instanceof ABoxNode;
			final ABoxNode<Name, Klass, Role> node = (ABoxNode<Name, Klass, Role>) e.getItem();
			_nodeMap.remove(node.getNodeID());
			for (Name name : node.getNames()) {
				/*
				 * PARANOIA: remove mapping only if it pointed to the current node
				 */
				if (_nodeMap.get(name).equals(node))
					_nodeMap.remove(name);
			}
			node.setABox(null);
		}
	}
	/// </editor-fold>
	/// <editor-fold defaultstate="desc" desc="private variables" >
	/**
	 * <p> ABoxCommon contains the information common to a set of ABoxes, avoiding duplication. </p>
	 *
	 */
	private final ABoxCommon<Name, Klass, Role> _common;


	ABoxCommon<Name, Klass, Role> getCommon()
	{
		return _common;
	}

	/*
	 * node id sequence generator
	 */
	protected final LinearSequenceNumberGenerator _nodeIDGenerator;

	/*
	 * TBox
	 */
	private final TBox<Name, Klass, Role> _tbox;


	public TBox<Name, Klass, Role> getTBox()
	{
		return _tbox;
	}

	/*
	 * node set and node maps
	 */
	private final NodeSetListener _nodeSetListener = new NodeSetListener();
	/**
	 * The real node set, wrapped by the AspectSortedSet below. Modifications here don't propagate to the listeners.
	 *
	 */
	protected SortedSet<IABoxNode<Name, Klass, Role>> _realNodes;
	/**
	 * <p> The node set as given to any eventuall callers. </p><p>
	 * {@link #_nodeSetListener} is attached to this {@link AspectSortedSet} by default and handles updates of the node
	 * list. </p>
	 */
	private AspectSortedSet<IABoxNode<Name, Klass, Role>> _nodes;
	/**
	 * The node map maps both node names and {@link NodeID}s to nodes. This map is maintained by the {@link NodeSetListener}.
	 *
	 */
	private Map<Object, IABoxNode<Name, Klass, Role>> _nodeMap;
	/**
	 * The ABox-internal state blocking state cache used by {@link IBlockingStrategy}.
	 *
	 */
	protected IBlockingStateCache _blockingStateCache;


	public IBlockingStateCache getBlockingStateCache()
	{
		return _blockingStateCache;
	}
	/**
	 * The unique ID of this {@link ABox}.
	 *
	 */
	private final NodeID _id;


	public NodeID getID()
	{
		return _id;
	}

	/// </editor-fold>
	/// <editor-fold defaultstate="collapsed" desc="constructors">

	public ABox(
		final ABoxCommon<Name, Klass, Role> commons)
	{
		_common = commons;
		_dependencyMap = new DependencyMap<Name, Klass, Role>(_common.getTermEntryFactory());

		_id = new NodeID(_common.getAboxIDFactory().next());
		_nodeIDGenerator = new LinearSequenceNumberGenerator();
		_blockingStateCache = new BlockingStateCache();

		_realNodes = _common.getNodeSetFactory().getInstance();
		_nodes = AspectSortedSet.decorate(_realNodes, this);
		_nodes.getListeners().add(_nodeSetListener);
		_nodeMap = _common.getNodeMapFactory().getInstance();
		_tbox = new TBox<Name, Klass, Role>(_common.getTermFactory());
	}


	/**
	 * Create a new ABox, directly setting the specified
	 *
	 * @param commons
	 * @param idGenerator
	 * @param tbox
	 * @param blockingStateCache
	 */
	ABox(final ABoxCommon<Name, Klass, Role> commons,
		 final LinearSequenceNumberGenerator idGenerator,
		 final TBox<Name, Klass, Role> tbox,
		 final IBlockingStateCache blockingStateCache)
	{
		_common = commons;
		_dependencyMap = new DependencyMap<Name, Klass, Role>(_common.getTermEntryFactory());
		_id = new NodeID(commons.getAboxIDFactory().next());

		_nodeIDGenerator = idGenerator;
		_blockingStateCache = blockingStateCache;

		_realNodes = _common.getNodeSetFactory().getInstance();
		_nodes = AspectSortedSet.decorate(_realNodes, this);
		_nodes.getListeners().add(_nodeSetListener);
		_nodeMap = _common.getNodeMapFactory().getInstance();
		_tbox = tbox;
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="Cloneable">

	/**
	 * Copy the
	 */
	private void copyListeners(final ABox<Name, Klass, Role> source)
	{
		_nodeMergeListeners.addAll(source.getNodeMergeListeners());
		for (ICollectionListener<IABoxNode<Name, Klass, Role>, Collection<IABoxNode<Name, Klass, Role>>> nodeSetListener : source.getNodeSetListeners()) {
			/*
			 * ignore the others internal listener when copying
			 */
			if (nodeSetListener != source._nodeSetListener)
				getNodeSetListeners().add(nodeSetListener);
		}
		_unfoldListeners.addAll(source.getUnfoldListeners());
		_termSetListeners.addAll(source.getTermSetListeners());
	}


	/**
	 * Create a writable clone of
	 *
	 * @return
	 */
	@Override
	public ABox<Name, Klass, Role> clone()
	{
		LinearSequenceNumberGenerator cloneIDGenerator = _nodeIDGenerator.clone();
		// we never modify the TBox, currently.
		// XXX - this needs to change, if TBox modifications are need to be supported
		// reasoning!
		final TBox<Name, Klass, Role> cloneTBox = _tbox;

		/*
		 * clone state cache
		 */
		final IBlockingStateCache blockingStateCache = _blockingStateCache.clone();
		final IBlockingStateCache cloneBlockingStateCache = _blockingStateCache.clone();

		ABox<Name, Klass, Role> klone = new ABox<Name, Klass, Role>(_common, cloneIDGenerator, cloneTBox,
																	cloneBlockingStateCache);

		_blockingStateCache = blockingStateCache;
		for (IABoxNode<Name, Klass, Role> node : _realNodes) {
			klone.add(node.clone(klone));
		}
		klone.getDependencyMap().putAll(getDependencyMap());

		klone.copyListeners(this);

		return klone;
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="node merging">

	public boolean canMerge(IABoxNode<Name, Klass, Role> node1,
							IABoxNode<Name, Klass, Role> node2)
	{
		if (node1.isDatatypeNode() != node2.isDatatypeNode()) {
			return false;
		} else if (node1.isDatatypeNode()) {
			return node1.getSuccessors().isEmpty() && node2.getSuccessors().isEmpty();
		} else
			return true;
	}


	/**
	 * <p> Join the two nodes {@literal node1} and {@literal node2} into a single node. The result of the join is a
	 * single node containing all concepts (and thus names) of both nodes. </p><p> The returned node is the target node
	 * of the join, which is <em>always</em> the smaller (as defined by the natural order) of both nodes. </p><p> After
	 * the join operation, only the returned node is a valid node inside the current ABox, all other nodes have been
	 * removed and should not be accessed any more. </p><p> The smaller node is returned, as we want to make sure, that
	 * we never process any later node before we are done processing earlier nodes. If we join to the smaller node, we
	 * always move in to the direction of smaller nodes, thus we may revisit nodes, but nether jump over nodes. </p>
	 *
	 * @param node1 The first node to join.
	 * @param node2 The second node to join.
	 * @return Merge information about the node join
	 */
	public NodeMergeInfo<Name, Klass, Role> mergeNodes(final IABoxNode<Name, Klass, Role> node1,
													   final IABoxNode<Name, Klass, Role> node2)
		throws ENodeMergeException
	{
		/**
		 * We join a source node with a target node.
		 *
		 * - disconnect the source node from the Abox - copy all concepts from the source to the target node - move all
		 * outgoing links from the source node to the target node - move all incoming links from the source node to the
		 * target node - clear out any remaining items from the source node to cleanup dangling memory references.
		 *
		 *
		 */
		if (node1.equals(node2))
			/*
			 * same node, no join necessary
			 */
			return new NodeMergeInfo<Name, Klass, Role>(node1, false);
		else if (node1.isDatatypeNode() != node2.isDatatypeNode()) {
			throw new ENodeMergeException(node1, node2, "Cannot merge datatype and non-datatype nodes");
		} else {
			/**
			 * Determine target and source nodes. We always join to the smaller (as defined by the natural order) node.
			 *
			 */
			assert contains(node1);
			assert contains(node2);
			IABoxNode<Name, Klass, Role> target;
			IABoxNode<Name, Klass, Role> source;
			if (node1.compareTo(node2) <= 0) {
				target = node1;
				source = node2;
			} else {
				target = node2;
				source = node1;
			}

			/*
			 * notify BEFORE actual merge
			 */
			notifyNodeMergeListeners(source, target);

			/*
			 * create a new empty merge info tracker
			 */
			NodeMergeInfo<Name, Klass, Role> mergeInfo = new NodeMergeInfo<Name, Klass, Role>(source, false);

			/**
			 * XXX - it would be much cleaner to do this at the end, but if the source concept set contains a nominal,
			 * the ABox will complain about duplicate name assignments, if we add the concept terms to the target node.
			 *
			 * Thus we remove the source node from the ABox immediately. This means, we cannot use
			 * _abox.getNode(source.getID()) later.
			 *
			 */
			/*
			 * detach source node from ABox
			 */
			final boolean wasSourceRemoved = remove(source);
			assert wasSourceRemoved;

			/**
			 * Add all concept terms from the source to the target node The node map is updated automatically, as we
			 * listen to modifications on the concept set.
			 *
			 */
			if (target.getTerms().addAll(source.getTerms()))
				mergeInfo.setModified(target);

			/**
			 * Rewrite outgoing links
			 *
			 * We simply add any outgoing link from the source node to the target node. Reflexive links are handled
			 * explicitly.
			 *
			 * Note: With the link map now being a {@link MultiTreeSetHashMap}, the duplicate checks are actually no
			 * longer necessary. Keep them in place anyway, as they are not too costly and should avoid any problems for
			 * future changes.
			 *
			 */
			final MultiMap<Role, NodeID> targetSuccessors = target.getSuccessors();
			for (Map.Entry<Role, Collection<NodeID>> successorEntry : source.getSuccessors().entrySet()) {
				/**
				 * Iterate over the source's successors and copy the links over to the target.
				 *
				 * Reflexive references (to source) need to be rewritten to point to target, instead.
				 *
				 */
				final Role role = successorEntry.getKey();
				for (NodeID successorID : successorEntry.getValue()) {
					if (successorID.equals(source.getNodeID())) {
						/*
						 * handle reflexive references, avoid duplicate links
						 */
						if (!targetSuccessors.containsValue(role, target.getNodeID())) {
							if (target.isDatatypeNode())
								throw new ENodeMergeException(target, source, "Cannot add successor to datatype node");
							targetSuccessors.put(role, target.getNodeID());
							mergeInfo.setModified(target);
						}
					} else {
						final IABoxNode<Name, Klass, Role> succ = getNode(successorID);

						/**
						 * Try to remove the backlink from the successor and mark the successors as modified, if a
						 * change was made.
						 */
						if (succ.getPredecessors().remove(role, source.getNodeID()) != null)
							mergeInfo.setModified(getNode(successorID));

						/*
						 * avoid duplicate links
						 */
						if (!targetSuccessors.containsValue(role, successorID)) {
							if (target.isDatatypeNode())
								throw new ENodeMergeException(target, source, "Cannot add successor to datatype node");
							targetSuccessors.put(role, successorID);
							mergeInfo.setModified(target);
						}
					}
				}
			}

			/**
			 * Rewrite incoming links.
			 *
			 */
			for (Map.Entry<Role, Collection<NodeID>> predEntry : source.getPredecessors().entrySet()) {
				final Role role = predEntry.getKey();
				for (NodeID predID : predEntry.getValue()) {
					if (!predID.equals(source.getNodeID())) {
						IABoxNode<Name, Klass, Role> pred = getNode(predID);
						assert pred != null;
						assert pred.getSuccessors().containsValue(role, source.getNodeID());
						/**
						 * Remove link to source node, redirect to target node
						 *
						 */
						if (pred.getSuccessors().remove(role, source.getNodeID()) != null) {
							mergeInfo.setModified(pred);
							mergeInfo.setModified(source);
							pred.getSuccessors().put(role, target.getNodeID());
							mergeInfo.setModified(target);
						}
					}
				}
			}

			/**
			 * Dependency map tracking
			 *
			 * We need to update all entries that match the source node to point to the target node.
			 *
			 */
			final Collection<Pair<TermEntry<Name, Klass, Role>, TermEntry<Name, Klass, Role>>> newEntries = new ArrayList<Pair<TermEntry<Name, Klass, Role>, TermEntry<Name, Klass, Role>>>();
			final Collection<Pair<TermEntry<Name, Klass, Role>, TermEntry<Name, Klass, Role>>> removeEntries = new ArrayList<Pair<TermEntry<Name, Klass, Role>, TermEntry<Name, Klass, Role>>>();
			Iterator<Map.Entry<TermEntry<Name, Klass, Role>, Collection<TermEntry<Name, Klass, Role>>>> entryIter = getDependencyMap().
				entrySet().iterator();

			while (entryIter.hasNext()) {
				boolean replace = false;
				final Map.Entry<TermEntry<Name, Klass, Role>, Collection<TermEntry<Name, Klass, Role>>> mapEntry = entryIter.next();
				final TermEntry<Name, Klass, Role> childEntry = mapEntry.getKey();
				NodeID newNodeID = childEntry.getNodeID();
				if (newNodeID.equals(source.getNodeID())) {
					replace = true;
					newNodeID = target.getNodeID();
				}
				for (TermEntry<Name, Klass, Role> parentEntry : mapEntry.getValue()) {
					NodeID newParentNodeID = parentEntry.getNodeID();
					if (parentEntry.getNodeID().equals(source.getNodeID())) {
						replace = true;
						newParentNodeID = target.getNodeID();
					}
					if (replace) {
						removeEntries.add(Pair.wrap(mapEntry.getKey(), parentEntry));
						final TermEntry<Name, Klass, Role> newChildEntry = _common.getTermEntryFactory().getEntry(
							newNodeID,
							childEntry.getTerm());
						final TermEntry<Name, Klass, Role> newParentEntry = _common.getTermEntryFactory().getEntry(
							newParentNodeID,
							parentEntry.getTerm());

						newEntries.add(Pair.wrap(newChildEntry, newParentEntry));
					}
				}
			}
			for (Pair<TermEntry<Name, Klass, Role>, TermEntry<Name, Klass, Role>> removeEntry : removeEntries)
				getDependencyMap().remove(removeEntry.getFirst(), removeEntry.getSecond());
			for (Pair<TermEntry<Name, Klass, Role>, TermEntry<Name, Klass, Role>> newEntry : newEntries)
				getDependencyMap().put(newEntry.getFirst(), newEntry.getSecond());

			/*
			 * PARANOIA: clear out any remains of source node
			 */
			source.getTerms().clear();
			source.getSuccessors().clear();
			source.getPredecessors().clear();

			/*
			 * complex assertion to verify link map consistency
			 */
			assert new Predicate<Pair<ABox<Name, Klass, Role>, NodeID>>() {

				public boolean evaluate(Pair<ABox<Name, Klass, Role>, NodeID> object)
				{
					for (IABoxNode<Name, Klass, Role> node : object.getFirst()) {
						if (node.getSuccessors().containsValue(object.getSecond()))
							return false;
						if (node.getPredecessors().containsValue(object.getSecond()))
							return false;
					}
					return true;
				}
			}.evaluate(Pair.wrap(this, source.getNodeID()));

			/*
			 * return target node
			 */
			mergeInfo.recordMerge(target);
			return mergeInfo;
		}
	}
	private final List<INodeMergeListener<Name, Klass, Role>> _nodeMergeListeners = new ArrayList<INodeMergeListener<Name, Klass, Role>>();


	/**
	 * Notify all node merge listeners of an immanent node merge.
	 *
	 * @param source The source node of the node merge (the node to disappear)
	 * @param target The target node of the node merge
	 */
	public void notifyNodeMergeListeners(final IABoxNode<Name, Klass, Role> source,
										 final IABoxNode<Name, Klass, Role> target)
	{
		Collection<INodeMergeListener<Name, Klass, Role>> listeners = getNodeMergeListeners();
		if (!listeners.isEmpty()) {
			for (INodeMergeListener<Name, Klass, Role> listener : listeners) {
				assert listener != null;
				listener.beforeNodeMerge(source, target);
			}
		}
	}


	public List<INodeMergeListener<Name, Klass, Role>> getNodeMergeListeners()
	{
		return _nodeMergeListeners;
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="Collection factories">

	/**
	 * @return the _termSetFactory
	 */
	public ICollectionFactory<IDLTerm<Name, Klass, Role>, SortedSet<IDLTerm<Name, Klass, Role>>> getTermSetFactory()
	{
		return _common.getTermSetFactory();
	}


	public ICollectionFactory<NodeID, Set<NodeID>> getNodeIDSetFactory()
	{
		return _common.getNodeIDSetFactory();
	}


	public IMultiMapFactory<Role, NodeID, MultiMap<Role, NodeID>> getLinkMapFactory()
	{
		return _common.getLinkMapFactory();
	}


	public IDLTermFactory<Name, Klass, Role> getDLTermFactory()
	{
		return _common.getTermFactory();
	}


	public TermEntryFactory<Name, Klass, Role> getTermEntryFactory()
	{
		return _common.getTermEntryFactory();
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="toString()">

	@Override
	public String toString()
	{
		return toString("");
	}


	public String toString(final int indent)
	{
		char[] fill = new char[indent];
		Arrays.fill(fill, ' ');
		return new String(fill);
	}


	public String toString(final String prefix)
	{
		if (TO_STRING_ID_ONLY) {
			return getID().toString();
		} else {

			int capacity = 0;
			if (!isEmpty())
				capacity = size() * first().toString().length();
			StringBuilder sb = new StringBuilder(capacity);

			sb.append(prefix);
			sb.append("ABox ");
			sb.append(getID());
			sb.append(": \n");
			final String subPrefix = prefix + "\t";
			for (IABoxNode<Name, Klass, Role> node : this) {
				sb.append(subPrefix);
				sb.append(node.toString(prefix + "\t"));
			}

			sb.append(prefix);
			sb.append("TBox:\n");
			sb.append(subPrefix);
			sb.append(getTBox().toString(subPrefix));
			sb.append("\n");

			sb.append(prefix);
			sb.append("RBox:\n");
			sb.append(subPrefix);
			sb.append(getTBox().getRBox().toString(subPrefix));
			sb.append("\n");

			sb.append(subPrefix);
			sb.append("Dependency Map: ");
			sb.append(getDependencyMap().toString());
			sb.append("\n");

			return sb.toString();
		}
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="Node set and node map">

	public Map<Object, IABoxNode<Name, Klass, Role>> getNodeMap()
	{
		return _nodeMap;
	}


	public ABoxNode<Name, Klass, Role> createNode(boolean isDatatypeNode)
		throws ENodeMergeException
	{
		int newID = _nodeIDGenerator.next();
		ABoxNode<Name, Klass, Role> newNode = new ABoxNode<Name, Klass, Role>(this, newID, isDatatypeNode);
		add(newNode);
		final NodeMergeInfo<Name, Klass, Role> mergeInfo = newNode.addUnfoldedDescriptions(getTBox().
			getGlobalDescriptions());
		assert mergeInfo.getCurrentNode() instanceof ABoxNode;
		return (ABoxNode<Name, Klass, Role>) mergeInfo.getCurrentNode();
	}


	public IABoxNode<Name, Klass, Role> getOrAddNamedNode(final Name individual, boolean isDataTypeNode)
		throws ENodeMergeException
	{
		if (_nodeMap.containsKey(individual)) {
			return _nodeMap.get(individual);
		} else {
			ABoxNode<Name, Klass, Role> newNode = createNode(isDataTypeNode);
			/*
			 * adding the nomimal reference to the target node automatically updates the node map
			 */
			final IDLNominalReference<Name, Klass, Role> nominalRef = _common.getTermFactory().getDLNominalReference(
				individual);
			final NodeMergeInfo<Name, Klass, Role> mergeInfo = newNode.addUnfoldedDescription(nominalRef);
			final NodeMergeInfo<Name, Klass, Role> unfoldResult = newNode.addUnfoldedDescriptions(getTBox().
				getGlobalDescriptions());
			mergeInfo.append(unfoldResult);
			assert mergeInfo.getCurrentNode() instanceof ABoxNode;
			return (ABoxNode<Name, Klass, Role>) mergeInfo.getCurrentNode();
		}
	}


	public IABoxNode<Name, Klass, Role> getNode(final NodeID id)
	{
		return _nodeMap.get(id);
	}


	public IABoxNode<Name, Klass, Role> getNode(final Name name)
	{
		return _nodeMap.get(name);
	}


	public List<ICollectionListener<IABoxNode<Name, Klass, Role>, Collection<IABoxNode<Name, Klass, Role>>>> getNodeSetListeners()
	{
		return _nodes.getListeners();
	}

	/// </editor-fold>
	/// <editor-fold defaultstate="collapsed" desc="SortedSet<ABoxNode<Name, Klass, Role>>">

	public Comparator<? super IABoxNode<Name, Klass, Role>> comparator()
	{
		return null;
	}


	public SortedSet<IABoxNode<Name, Klass, Role>> subSet(final IABoxNode<Name, Klass, Role> fromElement,
														  final IABoxNode<Name, Klass, Role> toElement)
	{
		return _nodes.subSet(fromElement, toElement);
	}


	public SortedSet<IABoxNode<Name, Klass, Role>> headSet(final IABoxNode<Name, Klass, Role> toElement)
	{
		return _nodes.headSet(toElement);
	}


	public SortedSet<IABoxNode<Name, Klass, Role>> tailSet(final IABoxNode<Name, Klass, Role> fromElement)
	{
		return _nodes.tailSet(fromElement);
	}


	public IABoxNode<Name, Klass, Role> first()
	{
		return _nodes.first();
	}


	public IABoxNode<Name, Klass, Role> last()
	{
		return _nodes.last();
	}


	public int size()
	{
		return _nodes.size();
	}


	public boolean isEmpty()
	{
		return _nodes.isEmpty();
	}


	public boolean contains(final Object o)
	{
		return _nodes.contains(o);
	}


	public Iterator<IABoxNode<Name, Klass, Role>> iterator()
	{
		Iterator<IABoxNode<Name, Klass, Role>> iter = _nodes.iterator();

		return iter;
	}


	public Object[] toArray()
	{
		return _nodes.toArray();
	}


	public <T> T[] toArray(final T[] a)
	{
		return _nodes.toArray(a);
	}


	public boolean add(final IABoxNode<Name, Klass, Role> e)
	{
		return _nodes.add(e);
	}


	public boolean remove(final Object o)
	{
		return _nodes.remove(o);
	}


	public boolean containsAll(final Collection<?> c)
	{
		return _nodes.containsAll(c);
	}


	public boolean addAll(final Collection<? extends IABoxNode<Name, Klass, Role>> c)
	{
		return _nodes.addAll(c);
	}


	public boolean retainAll(final Collection<?> c)
	{
		return _nodes.retainAll(c);
	}


	public boolean removeAll(final Collection<?> c)
	{
		return _nodes.removeAll(c);
	}


	public void clear()
	{
		_nodes.clear();
	}

	/// </editor-fold>
	/// <editor-fold defaultstate="collapsed" desc="deep compare">

	public int deepHashCode()
	{
		int hashCode = 323;
		for (IABoxNode<Name, Klass, Role> node : this) {
			hashCode += 3 * node.deepHashCode();
		}
		hashCode += 5 * getBlockingStateCache().hashCode();
		return hashCode;
	}


	public boolean deepEquals(Object obj)
	{
		if (obj instanceof IABox) {
			@SuppressWarnings("unchecked")
			final IABox<Name, Klass, Role> other = (IABox<Name, Klass, Role>) obj;
			final Map<Object, IABoxNode<Name, Klass, Role>> otherNodeMap = other.getNodeMap();
			for (IABoxNode<Name, Klass, Role> thisNode : this) {
				/**
				 * We check if the target ABox contains matching nodes with the same node ID.
				 *
				 * This may be more restrictive than required.
				 *
				 */
				final IABoxNode<Name, Klass, Role> otherNode = other.getNodeMap().get(thisNode.getNodeID());
				if (!thisNode.deepEquals(otherNode))
					return false;
			}
			for (IABoxNode<Name, Klass, Role> otherNode : other) {
				if (!getNodeMap().containsKey(otherNode.getNodeID()))
					return false;
			}

			return getBlockingStateCache().equals(other.getBlockingStateCache());
		} else
			return false;
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="element collection">

	@SuppressWarnings("unchecked")
	public Set<Klass> getClassesInSignature()
	{
		Set<Klass> classes = new TreeSet<Klass>();

		/*
		 * this is a bad case of: I don't know java generics enough to handle this
		 */
		for (IABoxNode<Name, Klass, Role> node : this) {
			for (IDLTerm<Name, Klass, Role> desc : node.getTerms()) {
				Set subTerms = TermUtil.collectSubTerms(desc, IDLClassReference.class);
				for (Object subTermObj : subTerms) {
					classes.add(((IDLClassReference<Name, Klass, Role>) subTermObj).getElement());
				}
			}
		}
		for (IDLTerm<Name, Klass, Role> term : getTBox()) {
			Set subTerms = TermUtil.collectSubTerms(term, IDLClassReference.class);
			for (Object subTermObj : subTerms) {
				classes.add(((IDLClassReference<Name, Klass, Role>) subTermObj).getElement());
			}
		}

		return classes;
	}


	@SuppressWarnings("unchecked")
	public Set<Role> getRolesInSignature()
	{
		Set<Role> roles = new TreeSet<Role>();

		/*
		 * this is a bad case of: I don't know java generics enough to handle this
		 */
		for (IABoxNode<Name, Klass, Role> node : this) {
			for (IDLTerm<Name, Klass, Role> desc : node.getTerms()) {
				Set subTerms = TermUtil.collectSubTerms(desc, IDLRoleOperator.class);
				for (Object subTermObj : subTerms) {
					roles.addAll(((IDLRoleOperator<Name, Klass, Role>) subTermObj).getRoles());
				}
			}
		}
		for (IDLTerm<Name, Klass, Role> term : getTBox()) {
			Set subTerms = TermUtil.collectSubTerms(term, IDLRoleOperator.class);
			for (Object subTermObj : subTerms) {
				roles.addAll(((IDLRoleOperator<Name, Klass, Role>) subTermObj).getRoles());
			}
		}
		roles.addAll(getTBox().getRBox().keySet());
		roles.addAll(getTBox().getRBox().getRoleDomains().keySet());
		roles.addAll(getTBox().getRBox().getRoleRanges().keySet());

		return roles;
	}
	/// </editor-fold>
	/// <editor-fold defaultstate="collapsed" desc="unfold listener">
	private final List<IUnfoldListener<Name, Klass, Role>> _unfoldListeners = new ArrayList<IUnfoldListener<Name, Klass, Role>>();


	public List<IUnfoldListener<Name, Klass, Role>> getUnfoldListeners()
	{
		return _unfoldListeners;
	}


	public void notifyUnfoldListeners(
		final IABoxNode<Name, Klass, Role> node,
		final IDLRestriction<Name, Klass, Role> original,
		final Collection<IDLRestriction<Name, Klass, Role>> unfold)
	{
		if (!_unfoldListeners.isEmpty()) {
			for (IUnfoldListener<Name, Klass, Role> listener : _unfoldListeners)
				listener.beforeConceptUnfold(node, original, unfold);
		}
	}
	/// </editor-fold>
	/// <editor-fold defaultstate="collapsed" desc="node set listener">
	private List<ITermSetListener<Name, Klass, Role>> _termSetListeners = new ArrayList<ITermSetListener<Name, Klass, Role>>();


	public List<ITermSetListener<Name, Klass, Role>> getTermSetListeners()
	{
		return _termSetListeners;
	}


	protected TermChangeEvent<Name, Klass, Role> notifyTermAdded(final IABoxNode<Name, Klass, Role> node,
																 final IDLTerm<Name, Klass, Role> term)
	{
		final TermChangeEvent<Name, Klass, Role> ev = new TermChangeEvent<Name, Klass, Role>(this, node, term);
		if (!_termSetListeners.isEmpty()) {
			for (ITermSetListener<Name, Klass, Role> listener : _termSetListeners)
				listener.termAdded(ev);
		}
		return ev;
	}


	public TermChangeEvent<Name, Klass, Role> notifyTermRemoved(final IABoxNode<Name, Klass, Role> node,
																final IDLTerm<Name, Klass, Role> term)
	{
		final TermChangeEvent<Name, Klass, Role> ev = new TermChangeEvent<Name, Klass, Role>(this, node, term);
		if (!_termSetListeners.isEmpty()) {
			for (ITermSetListener<Name, Klass, Role> listener : _termSetListeners)
				listener.termRemoved(ev);
		}
		return ev;
	}


	public ABoxNodeEvent<Name, Klass, Role> notifyTermSetCleared(final IABoxNode<Name, Klass, Role> node)
	{
		final ABoxNodeEvent<Name, Klass, Role> ev = new ABoxNodeEvent<Name, Klass, Role>(this, node);
		if (!_termSetListeners.isEmpty()) {
			for (ITermSetListener<Name, Klass, Role> listener : _termSetListeners)
				listener.termSetCleared(ev);
		}
		return ev;
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="IImmutable">

	public IABox<Name, Klass, Role> getImmutable()
	{
		return ImmutableABox.decorate(this);
	}
	/// </editor-fold>
	/// <editor-fold defaultstate="collaped" desc="DependencyMap">
	private final DependencyMap<Name, Klass, Role> _dependencyMap;


	public DependencyMap<Name, Klass, Role> getDependencyMap()
	{
		return _dependencyMap;
	}
	/// </editor-fold>
}
