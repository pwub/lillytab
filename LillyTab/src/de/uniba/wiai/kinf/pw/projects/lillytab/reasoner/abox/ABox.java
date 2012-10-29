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

import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.tbox.TBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ITermSetListener;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ABoxNodeEvent;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermChangeEvent;
import de.uniba.wiai.kinf.pw.projects.lillytab.blocking.BlockingStateCache;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntryFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.immutable.ImmutableABox;
import de.dhke.projects.cutil.Pair;
import de.uniba.wiai.kinf.pw.projects.lillytab.blocking.IBlockingStateCache;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.INodeMergeListener;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.dhke.projects.cutil.collections.factories.ICollectionFactory;
import de.dhke.projects.cutil.collections.aspect.AspectSortedSet;
import de.dhke.projects.cutil.collections.aspect.ICollectionListener;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IDependencyMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.blocking.IBlockingStrategy;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.ITBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLNominalReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
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

/**
 * <p> In-memory copy-on-write implementation of the {@link IABox} interface. </p><p> </p>
 *
 * @param <Name> The type for nominals and values
 * @param <Klass> The type for DL classes
 * @param <Role> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ABox<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	implements IABox<Name, Klass, Role> {

	private static final long serialVersionUID = 3990522176232016954L;
	private static final boolean TO_STRING_ID_ONLY = false;
	/**
	 * <p> ABoxCommon contains the information common to a set of ABoxes, avoiding duplication. </p>
	 *
	 */
	private final ABoxCommon<Name, Klass, Role> _common;
	/**
	 * node id sequence generator
	 *
	 */
	protected final LinearSequenceNumberGenerator _nodeIDGenerator;
	/**
	 * The number of the last valid TBox generation used.
	 *
	 */
	private int _tboxGeneration;
	/**
	 * The {@link ITBox} of the current ABox
	 */
	private final TBox<Name, Klass, Role> _tbox;
	/**
	 * <p> The node set as given to any eventual callers. </p><p> {@link #_nodeSetListener} is attached to this
	 * {@link AspectSortedSet} by default and handles updates of the node list. </p>
	 *
	 */
	private final ABoxNodeSet<Name, Klass, Role> _nodes;
	/**
	 * The node map maps both node names and {@link NodeID}s to nodes.
	 *
	 * XXX - this is currently protected for access from {@link ABoxNodeSet}. To go away.
	 *
	 */
	protected final Map<Object, IABoxNode<Name, Klass, Role>> _nodeMap;
	/**
	 * The ABox-internal state blocking state cache used by {@link IBlockingStrategy}.
	 *
	 */
	private final IBlockingStateCache _blockingStateCache;
	/**
	 * The set of node {@link ITermSetListener}s.
	 *
	 */
	private List<ITermSetListener<Name, Klass, Role>> _termSetListeners = new ArrayList<ITermSetListener<Name, Klass, Role>>();
	/**
	 * The unique ID of this {@link ABox}.
	 *
	 */
	private final NodeID _id;
	/**
	 * The {@link IDependencyMap}
	 *
	 */
	private final DependencyMap<Name, Klass, Role> _dependencyMap;


	/// <editor-fold defaultstate="collapsed" desc="constructors">
	public ABox(
		final ABoxCommon<Name, Klass, Role> commons)
	{
		_common = commons;
		_dependencyMap = new DependencyMap<Name, Klass, Role>(_common.getTermEntryFactory());

		_id = new NodeID(_common.getAboxIDFactory().next());
		_nodeIDGenerator = new LinearSequenceNumberGenerator();
		_blockingStateCache = new BlockingStateCache();

		_nodes = new ABoxNodeSet<Name, Klass, Role>(this);

		_nodeMap = _common.getNodeMapFactory().getInstance();
		
		_tbox = new TBox<Name, Klass, Role>(_common.getTermFactory());
		_tboxGeneration = _tbox.getGeneration();
	}


	/**
	 * Create a new ABox, kloning from another ABox.
	 *
	 * @param commons
	 * @param idGenerator
	 * @param tbox
	 * @param blockingStateCache
	 */
	ABox(final ABox<Name, Klass, Role> klonee)
	{
		_common = klonee._common;
		_dependencyMap = klonee.getDependencyMap().clone();
		_id = new NodeID(_common.getAboxIDFactory().next());
		_nodeIDGenerator = klonee._nodeIDGenerator.clone();
		_blockingStateCache = klonee._blockingStateCache.clone();
		_nodes = new ABoxNodeSet<Name, Klass, Role>(this);
		_nodeMap = _common.getNodeMapFactory().getInstance();		
		_tbox = klonee._tbox.clone();
		_tboxGeneration = klonee._tboxGeneration;
		
		for (IABoxNode<Name, Klass, Role> node : klonee)
			add(node.clone(this));
	}
	/// </editor-fold>	


	ABoxCommon<Name, Klass, Role> getCommon()
	{
		return _common;
	}


	@Override
	public TBox<Name, Klass, Role> getTBox()
	{
		return _tbox;
	}


	@Override
	public IBlockingStateCache getBlockingStateCache()
	{
		return _blockingStateCache;
	}


	@Override
	public NodeID getID()
	{
		return _id;
	}

	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="Cloneable">
	/**
	 * Create a writable clone of the current ABox.
	 *
	 * @return An independent clone of the current {@link IABox}.
	 */
	@Override
	public ABox<Name, Klass, Role> clone()
	{
		ABox<Name, Klass, Role> klone = new ABox<Name, Klass, Role>(this);
		return klone;
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="node merging">

	private void moveSuccessors(final IABoxNode<Name, Klass, Role> source,
								final IABoxNode<Name, Klass, Role> target,
								NodeMergeInfo<Name, Klass, Role> mergeInfo) throws ENodeMergeException
	{
		/**
		 * Rewrite outgoing links
		 *
		 * We simply add any outgoing link from the source node to the target node. Reflexive links are handled
		 * explicitly.
		 *
		 * Note: With the link map now being a {@link MultiTreeSetHashMap}, the duplicate checks are actually no longer
		 * necessary. Keep them in place anyway, as they are not too costly and should avoid any problems for future
		 * changes.
		 *
		 */
		final NodeID sourceID = source.getNodeID();
		final Iterator<Map.Entry<Role, Collection<NodeID>>> succIter = source.getRABox().getAssertedSuccessors().
			entrySet().iterator();
		while (succIter.hasNext()) {
			/**
			 * Iterate over the source's successors and copy the links over to the target.
			 *
			 * Reflexive references (to source) need to be rewritten to point to target, instead.
			 */
			final Map.Entry<Role, Collection<NodeID>> successorEntry = succIter.next();
			final Role role = successorEntry.getKey();
			/* save the successor IDs in case the remove updates through */
			final Collection<NodeID> succs = new ArrayList<NodeID>(successorEntry.getValue());
			/* remove the link */
			succIter.remove();
			for (NodeID successorID : succs) {
				if (successorID.equals(sourceID)) {
					final NodeID targetID = target.getNodeID();
					/* handle reflexive references, avoid duplicate links */
					if (target.isDatatypeNode())
						throw new ENodeMergeException(target, source, "Cannot add successor to datatype node");
					else if (!target.getRABox().getAssertedSuccessors().containsValue(role, targetID)) {
						target.getRABox().getAssertedSuccessors().put(role, targetID);
						mergeInfo.setModified(target);
					}
				} else {
					if (target.isDatatypeNode())
						throw new ENodeMergeException(target, source, "Cannot add successor to datatype node");

					final IABoxNode<Name, Klass, Role> succ = getNode(successorID);
					/**
					 * Try to remove the backlink from the successor and mark the successors as modified, if a change
					 * was made.
					 *
					 */
					if (succ.getRABox().getAssertedPredecessors().remove(role, sourceID) != null)
						mergeInfo.setModified(succ);

					/*
					 * avoid duplicate links
					 */
					if (!target.getRABox().getAssertedSuccessors().containsValue(role, successorID)) {
						target.getRABox().getAssertedSuccessors().put(role, successorID);
						mergeInfo.setModified(target);
					}
				}
			}
		}
	}


	private void movePredecessors(IABoxNode<Name, Klass, Role> source, IABoxNode<Name, Klass, Role> target,
								  NodeMergeInfo<Name, Klass, Role> mergeInfo)
	{
		/**
		 * Rewrite incoming links.
		 *
		 */
		final NodeID sourceID = source.getNodeID();
		final NodeID targetID = target.getNodeID();

		final Iterator<Map.Entry<Role, Collection<NodeID>>> predIter = source.getRABox().getAssertedPredecessors().
			entrySet().iterator();
		while (predIter.hasNext()) {
			final Map.Entry<Role, Collection<NodeID>> predEntry = predIter.next();
			final Role role = predEntry.getKey();

			/* save predecessors ids in case the remove operation updates through */
			final Collection<NodeID> preds = new ArrayList<NodeID>(predEntry.getValue());
			/* remove the link */
			for (NodeID predID : preds)
				assert getNode(predID).getRABox().getAssertedSuccessors().containsValue(role, sourceID);

			predIter.remove();
			for (NodeID predID : preds) {
				if (!predID.equals(sourceID)) {
					/* remove source link */
					IABoxNode<Name, Klass, Role> pred = getNode(predID);
					assert pred != null;
					/* link is already gone via the remove above */
					if (pred.getRABox().getAssertedSuccessors().containsValue(role, sourceID)) {

						assert !pred.getRABox().getAssertedSuccessors().containsValue(role, sourceID);
					}
					mergeInfo.setModified(pred);
					mergeInfo.setModified(source);
					pred.getRABox().getAssertedSuccessors().put(role, targetID);
					mergeInfo.setModified(target);
				}
			}
		}
	}


	private void updateDependencyMap(IABoxNode<Name, Klass, Role> source,
									 IABoxNode<Name, Klass, Role> target)
	{
		/**
		 * Dependency map tracking
		 *
		 * We need to update all entries that match the source node to point to the target node.
		 *
		 */
		final Collection<Pair<TermEntry<Name, Klass, Role>, TermEntry<Name, Klass, Role>>> newEntries = new ArrayList<Pair<TermEntry<Name, Klass, Role>, TermEntry<Name, Klass, Role>>>();
		final Collection<Pair<TermEntry<Name, Klass, Role>, TermEntry<Name, Klass, Role>>> removeEntries = new ArrayList<Pair<TermEntry<Name, Klass, Role>, TermEntry<Name, Klass, Role>>>();
		final Iterator<Map.Entry<TermEntry<Name, Klass, Role>, Collection<TermEntry<Name, Klass, Role>>>> entryIter = getDependencyMap().
			entrySet().iterator();

		while (entryIter.hasNext()) {
			boolean replace = false;
			final Map.Entry<TermEntry<Name, Klass, Role>, Collection<TermEntry<Name, Klass, Role>>> mapEntry = entryIter.
				next();
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
	}


	@Override
	public boolean canMerge(IABoxNode<Name, Klass, Role> node1,
							IABoxNode<Name, Klass, Role> node2)
	{
		if (node1.isDatatypeNode() != node2.isDatatypeNode()) {
			return false;
		} else if (node1.isDatatypeNode()) {
			return node1.getRABox().getAssertedSuccessors().isEmpty() && node2.getRABox().getAssertedSuccessors().
				isEmpty();
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
	 * @throws ENodeMergeException
	 */
	@Override
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
			ABoxNode<Name, Klass, Role> target;
			ABoxNode<Name, Klass, Role> source;
			if (node1.compareTo(node2) <= 0) {
				target = (ABoxNode<Name, Klass, Role>) node1;
				source = (ABoxNode<Name, Klass, Role>) node2;
			} else {
				target = (ABoxNode<Name, Klass, Role>) node2;
				source = (ABoxNode<Name, Klass, Role>) node1;
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
			/**
			 * Add all concept terms from the source to the target node. The node map is updated automatically, as we
			 * listen to modifications on the concept set.
			 *
			 * We remember the source terms first, then clear the source list. This causes the node map to be updated,
			 * as well, avoiding name clashes when adding the terms to the target node.
			 *
			 */
			final ArrayList<IDLTerm<Name, Klass, Role>> sourceTerms = new ArrayList<IDLTerm<Name, Klass, Role>>(
				source.getTerms());
			source.getTerms().clear();
			if (target._terms.addAll(sourceTerms))
				mergeInfo.setModified(target);


			/* move successors to target */
			moveSuccessors(source, target, mergeInfo);
			/* move predessors to point to target */
			movePredecessors(source, target, mergeInfo);
			/* update dependency map */
			updateDependencyMap(source, target);

			/*
			 * PARANOIA: clear out any remains of source node
			 */
			assert source.getRABox().getAssertedSuccessors().isEmpty();
			assert source.getRABox().getAssertedPredecessors().isEmpty();

			/**
			 * detach source node from ABox
			 */
			final boolean wasSourceRemoved = remove(source);
			assert wasSourceRemoved;

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


	@Override
	public List<INodeMergeListener<Name, Klass, Role>> getNodeMergeListeners()
	{
		return _nodeMergeListeners;
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="Collection factories">

	@Override
	public ICollectionFactory<NodeID, Set<NodeID>> getNodeIDSetFactory()
	{
		return _common.getNodeIDSetFactory();
	}


	@Override
	public IDLTermFactory<Name, Klass, Role> getDLTermFactory()
	{
		return _common.getTermFactory();
	}


	@Override
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

	@Override
	public Map<Object, IABoxNode<Name, Klass, Role>> getNodeMap()
	{
		/* XXX - returns a modifiable node map */
		return _nodeMap;
	}


	@Override
	public IABoxNode<Name, Klass, Role> createNode(boolean isDatatypeNode)
		throws ENodeMergeException
	{
		final int newID = _nodeIDGenerator.next();
		final ABoxNode<Name, Klass, Role> newNode = new ABoxNode<Name, Klass, Role>(this, newID, isDatatypeNode);
		add(newNode);
		final NodeMergeInfo<Name, Klass, Role> mergeInfo = newNode.addUnfoldedDescriptions(getTBox().
			getGlobalDescriptions());
		assert mergeInfo.getCurrentNode() instanceof IABoxNode;
		return (IABoxNode<Name, Klass, Role>) mergeInfo.getCurrentNode();
	}


	@Override
	public IABoxNode<Name, Klass, Role> getOrAddNamedNode(final Name individual, boolean isDataTypeNode)
		throws ENodeMergeException
	{
		if (_nodeMap.containsKey(individual)) {
			return _nodeMap.get(individual);
		} else {
			IABoxNode<Name, Klass, Role> newNode = createNode(isDataTypeNode);
			/*
			 * adding the nomimal reference to the target node automatically updates the node map
			 */
			final IDLNominalReference<Name, Klass, Role> nominalRef = _common.getTermFactory().getDLNominalReference(
				individual);
			final NodeMergeInfo<Name, Klass, Role> mergeInfo = newNode.addUnfoldedDescription(nominalRef);
			final NodeMergeInfo<Name, Klass, Role> unfoldResult = newNode.addUnfoldedDescriptions(getTBox().
				getGlobalDescriptions());
			mergeInfo.append(unfoldResult);
			assert mergeInfo.getCurrentNode() instanceof IABoxNode;
			return (IABoxNode<Name, Klass, Role>) mergeInfo.getCurrentNode();
		}
	}


	@Override
	public IABoxNode<Name, Klass, Role> getNode(final NodeID id)
	{
		return _nodeMap.get(id);
	}


	@Override
	public IABoxNode<Name, Klass, Role> getNode(final Name name)
	{
		return _nodeMap.get(name);
	}


	@Override
	public List<ICollectionListener<IABoxNode<Name, Klass, Role>, Collection<IABoxNode<Name, Klass, Role>>>> getNodeSetListeners()
	{
		return _nodes.getListeners();
	}

	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="SortedSet<ABoxNode<Name, Klass, Role>>">
	@Override
	public Comparator<? super IABoxNode<Name, Klass, Role>> comparator()
	{
		return null;
	}


	@Override
	public SortedSet<IABoxNode<Name, Klass, Role>> subSet(final IABoxNode<Name, Klass, Role> fromElement,
														  final IABoxNode<Name, Klass, Role> toElement)
	{
		return _nodes.subSet(fromElement, toElement);
	}


	@Override
	public SortedSet<IABoxNode<Name, Klass, Role>> headSet(final IABoxNode<Name, Klass, Role> toElement)
	{
		return _nodes.headSet(toElement);
	}


	@Override
	public SortedSet<IABoxNode<Name, Klass, Role>> tailSet(final IABoxNode<Name, Klass, Role> fromElement)
	{
		return _nodes.tailSet(fromElement);
	}


	@Override
	public IABoxNode<Name, Klass, Role> first()
	{
		return _nodes.first();
	}


	@Override
	public IABoxNode<Name, Klass, Role> last()
	{
		return _nodes.last();
	}


	@Override
	public int size()
	{
		return _nodes.size();
	}


	@Override
	public boolean isEmpty()
	{
		return _nodes.isEmpty();
	}


	@Override
	public boolean contains(final Object o)
	{
		return _nodes.contains(o);
	}


	@Override
	public Iterator<IABoxNode<Name, Klass, Role>> iterator()
	{
		Iterator<IABoxNode<Name, Klass, Role>> iter = _nodes.iterator();

		return iter;
	}


	@Override
	public Object[] toArray()
	{
		return _nodes.toArray();
	}


	@Override
	public <T> T[] toArray(final T[] a)
	{
		return _nodes.toArray(a);
	}


	@Override
	public boolean add(final IABoxNode<Name, Klass, Role> e)
	{
		return _nodes.add(e);
	}


	@Override
	public boolean remove(final Object o)
	{
		return _nodes.remove(o);
	}


	@Override
	public boolean containsAll(final Collection<?> c)
	{
		return _nodes.containsAll(c);
	}


	@Override
	public boolean addAll(final Collection<? extends IABoxNode<Name, Klass, Role>> c)
	{
		return _nodes.addAll(c);
	}


	@Override
	public boolean retainAll(final Collection<?> c)
	{
		return _nodes.retainAll(c);
	}


	@Override
	public boolean removeAll(final Collection<?> c)
	{
		return _nodes.removeAll(c);
	}


	@Override
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
	@Override
	public Set<Klass> getClassesInSignature()
	{
		Set<Klass> classes = new TreeSet<Klass>();

		/*
		 * this is a bad case of: I don't know java generics enough to handle this
		 */
		for (IABoxNode<Name, Klass, Role> node : this) {
			for (IDLTerm<Name, Klass, Role> desc : node.getTerms()) {
				Set<IDLClassReference<Name, Klass, Role>> subTerms = TermUtil.collectSubTerms(desc,
																							  IDLClassReference.class);
				for (IDLClassReference<Name, Klass, Role> subTermObj : subTerms) {
					classes.add(subTermObj.getElement());
				}
			}
		}
		for (IDLTerm<Name, Klass, Role> term : getTBox()) {
			Set<IDLClassReference<Name, Klass, Role>> subTerms = TermUtil.collectSubTerms(term, IDLClassReference.class);
			for (IDLClassReference<Name, Klass, Role> subTermObj : subTerms) {
				classes.add(subTermObj.getElement());
			}
		}

		return classes;
	}


	@SuppressWarnings("unchecked")
	@Override
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
		roles.addAll(getTBox().getRBox().getRoles());

		return roles;
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="node set listener">

	@Override
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

		/* XXX - can possible be done more efficiently */
		Iterator<Object> iter = _nodeMap.keySet().iterator();
		while (iter.hasNext()) {
			/* remove only named references */
			final Object key = iter.next();
			if (!(key instanceof NodeID)) {
				if (node.equals(_nodeMap.get(key)))
					iter.remove();
			}
		}

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


	@Override
	public void unfoldAll()
		throws ENodeMergeException
	{
		/**
		 * We remember the TBox generation number and re-run the unfold operation only if the TBox
		 * was re-calculated in between.
		 */
		final TBox<Name, Klass, Role> tbox = getTBox();
		if (tbox.getGeneration() > _tboxGeneration) {
			final Set<IDLRestriction<Name, Klass, Role>> globalDescs = tbox.getGlobalDescriptions();

			Iterator<IABoxNode<Name, Klass, Role>> iter = iterator();
			while (iter.hasNext()) {
				IABoxNode<Name, Klass, Role> currentNode = iter.next();
				assert contains(currentNode);

				/*
				 * unfold existing terms
				 */
				NodeMergeInfo<Name, Klass, Role> unfoldResult = currentNode.unfoldAll();

				/*
				 * unfold global descriptions
				 */
				unfoldResult.append(currentNode.addUnfoldedDescriptions(globalDescs));

				if (!unfoldResult.getMergedNodes().isEmpty()) {
					/*
					 * a merge occured, refetch node
					 */
					currentNode = unfoldResult.getCurrentNode();
					assert contains(currentNode);
					iter = iterator();
				}
			}
			_tboxGeneration = tbox.getGeneration();
		}
	}


	@Override
	public boolean containsAllTermEntries(Collection<TermEntry<Name, Klass, Role>> entries)
	{
		for (TermEntry<Name, Klass, Role> entry : entries) {
			if (!containsTermEntry(entry))
				return false;
		}
		return true;
	}


	@Override
	public boolean containsTermEntry(TermEntry<Name, Klass, Role> entry)
	{
		final IABoxNode<Name, Klass, Role> node = getNode(entry.getNodeID());
		return ((node != null) && node.getTerms().contains(entry.getTerm()));
	}
	/// <editor-fold defaultstate="collapsed" desc="DependencyMap">


	@Override
	public DependencyMap<Name, Klass, Role> getDependencyMap()
	{
		return _dependencyMap;
	}


	protected boolean removeNoUnlink(final ABoxNode<Name, Klass, Role> node)
	{
		return _nodes.removeNoUnlink(node);
	}


	protected boolean addNoUnlink(final ABoxNode<Name, Klass, Role> node)
	{
		return _nodes.addNoUnlink(node);
	}
	/// </editor-fold>
}