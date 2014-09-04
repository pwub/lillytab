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
import de.dhke.projects.cutil.collections.aspect.AspectSortedSet;
import de.dhke.projects.cutil.collections.aspect.ICollectionListener;
import de.dhke.projects.cutil.collections.factories.ICollectionFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ABoxNodeEvent;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IDatatypeABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IDependencyMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IIndividualABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.INodeMergeListener;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ITermSetListener;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermChangeEvent;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntryFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.blocking.BlockingStateCache;
import de.uniba.wiai.kinf.pw.projects.lillytab.blocking.IBlockingStateCache;
import de.uniba.wiai.kinf.pw.projects.lillytab.blocking.IBlockingStrategy;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.immutable.ImmutableABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.tbox.TBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IAssertedRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.ITBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLIndividualReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLLiteralReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRoleOperator;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.TermUtil;
import de.uniba.wiai.kinf.pw.projects.lillytab.util.LinearSequenceNumberGenerator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * In-memory copy-on-write implementation of the {@link IABox} interface.
 * <p />
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ABox<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	implements IABox<I, L, K, R>
{
	/// </editor-fold>
	private static final long serialVersionUID = 3990522176232016954L;
	private static final boolean TO_STRING_ID_ONLY = false;
	/**
	 * node id sequence generator
	 *
	 */
	protected final LinearSequenceNumberGenerator _nodeIDGenerator;
	/**
	 * The node map maps both node names and {@link NodeID}s to nodes.
	 *
	 * XXX - this is currently protected for access from {@link ABoxNodeSet}. To go away.
	 *
	 */
	protected final Map<Object, IABoxNode<I, L, K, R>> _nodeMap;
	/**
	 * ABoxCommon contains the information common to a set of ABoxes, avoiding duplication.
	 *
	 */
	private final ABoxCommon<I, L, K, R> _common;
	/**
	 * The number of the last valid TBox generation used.
	 *
	 */
	private int _tboxGeneration;
	/**
	 * The {@link ITBox} of the current ABox
	 */
	private final TBox<I, L, K, R> _tbox;
	/**
	 * The node set as given to any eventual callers. <p /> {@link #_nodeSetListener} is attached to this
	 * {@link AspectSortedSet} by default and handles updates of the node list.
	 *
	 */
	private final ABoxNodeSet<I, L, K, R> _nodes;
	/**
	 * The ABox-internal state blocking state cache used by {@link IBlockingStrategy}.
	 *
	 */
	private final IBlockingStateCache _blockingStateCache;
	/**
	 * The set of node {@link ITermSetListener}s.
	 *
	 */
	private List<ITermSetListener<I, L, K, R>> _termSetListeners = new ArrayList<>();
	/**
	 * The unique ID of this {@link ABox}.
	 *
	 */
	private final NodeID _id;
	/**
	 * The {@link IDependencyMap}
	 *
	 */
	private final DependencyMap<I, L, K, R> _dependencyMap;
	private final List<INodeMergeListener<I, L, K, R>> _nodeMergeListeners = new ArrayList<>();
	/**
	 * Node queue management
	 */
	private final SortedSet<NodeID> _nonGeneratingQueue = new TreeSet<>();
	private final SortedSet<NodeID> _generatingQueue = new TreeSet<>();

	/// <editor-fold defaultstate="collapsed" desc="constructors">
	public ABox(
		final ABoxCommon<I, L, K, R> commons)
	{
		_common = commons;
		_dependencyMap = new DependencyMap<>(_common.getTermEntryFactory());

		_id = new NodeID(_common.getAboxIDFactory().next());
		_nodeIDGenerator = new LinearSequenceNumberGenerator();
		_blockingStateCache = new BlockingStateCache();

		_nodes = new ABoxNodeSet<>(this);

		_nodeMap = _common.getNodeMapFactory().getInstance();

		_tbox = new TBox<>(_common.getTermFactory());
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
	ABox(final ABox<I, L, K, R> klonee)
	{
		_common = klonee._common;
		_dependencyMap = klonee.getDependencyMap().clone();
		_id = new NodeID(_common.getAboxIDFactory().next());
		_nodeIDGenerator = klonee._nodeIDGenerator.clone();
		_blockingStateCache = klonee._blockingStateCache.clone();
		_nodes = new ABoxNodeSet<>(this);
		_nodeMap = _common.getNodeMapFactory().getInstance();
		_tbox = klonee._tbox.clone();
		_tboxGeneration = klonee._tboxGeneration;

		_nonGeneratingQueue.addAll(klonee._nonGeneratingQueue);
		_generatingQueue.addAll(klonee._generatingQueue);

		for (IABoxNode<I, L, K, R> node : klonee) {
			add(node.clone(this));
		}
	}
	/// </editor-fold>	

	@Override
	public TBox<I, L, K, R> getTBox()
	{
		return _tbox;
	}

	@Override
	public IAssertedRBox<I, L, K, R> getAssertedRBox()
	{
		return _tbox.getAssertedRBox();
	}

	@Override
	public IRBox<I, L, K, R> getRBox()
	{
		return _tbox.getRBox();
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
	@Override
	public ABox<I, L, K, R> clone()
	{
		ABox<I, L, K, R> klone = new ABox<>(this);
		return klone;
	}
	/// </editor-fold>


	@Override
	public boolean canMerge(IABoxNode<I, L, K, R> node1, IABoxNode<I, L, K, R> node2)
	{
		if ((node1 instanceof IDatatypeABoxNode) != (node2 instanceof IDatatypeABoxNode)) {
			return false;
		} else if ((node1 instanceof IDatatypeABoxNode)
			&& (!node1.isAnonymous()) && (!node2.isAnonymous())
			&& (!node1.getPrimaryName().equals(node2.getPrimaryName()))) {
			/* XXX - this assumes that different datatype literals are semantically different */
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Join the two nodes {@literal node1} and {@literal node2} into a single node. The result of the join is a single
	 * node containing all concepts (and thus names) of both nodes. <p /> The returned node is the target node of the
	 * join, which is <em>always</em> the smaller (as defined by the natural order) of both nodes. <p /> After the join
	 * operation, only the returned node is a valid node inside the current ABox, all other nodes have been removed and
	 * should not be accessed any more. <p /> The smaller node is returned, as we want to make sure, that we never
	 * process any later node before we are done processing earlier nodes. If we join to the smaller node, we always
	 * move in to the direction of smaller nodes, thus we may revisit nodes, but nether jump over nodes.
	 *
	 * @param node1 The first node to join.
	 * @param node2 The second node to join.
	 * @return Merge information about the node join
	 * @throws ENodeMergeException
	 */@Override
	public NodeMergeInfo<I, L, K, R> mergeNodes(final IABoxNode<I, L, K, R> node1, final IABoxNode<I, L, K, R> node2)
		throws ENodeMergeException
	{
		/**
		 * We join a source node with a target node.
		 *
		 * - disconnect the source node from the Abox - copy all concepts from the source to the target node - move all
		 * outgoing links from the source node to the target node - move all incoming links from the source node to the
		 * target node - clear out any remaining items from the source node to cleanup dangling memory references.
		 */
		if (node1.equals(node2)) {
			/* same node, no join necessary */
			return new NodeMergeInfo<>(node1, false);
		} else if ((node1 instanceof IDatatypeABoxNode) != (node2 instanceof IDatatypeABoxNode)) {
			throw new ENodeMergeException(node1, node2, "Cannot merge datatype and non-datatype nodes");
		} else {
			/**
			 * Determine target and source nodes. We always join to the smaller (as defined by the natural order) node.
			 *
			 */
			assert contains(node1);
			assert contains(node2);
			ABoxNode<?, I, L, K, R> target;
			ABoxNode<?, I, L, K, R> source;
			if (node1.compareTo(node2) <= 0) {
				target = (ABoxNode<?, I, L, K, R>) node1;
				source = (ABoxNode<?, I, L, K, R>) node2;
			} else {
				target = (ABoxNode<?, I, L, K, R>) node2;
				source = (ABoxNode<?, I, L, K, R>) node1;
			}

			if ((target instanceof IDatatypeABoxNode)
				&& (!source.isAnonymous())
				&& (!target.getPrimaryName().equals(source.getPrimaryName()))) {
				/* XXX - this assumes that different datatype literals are semantically different */
				throw new ENodeMergeException(target, source, "Cannot merge datatype nodes with different names");
			}

			/*
			 * notify BEFORE actual merge
			 */
			notifyNodeMergeListeners(source, target);

			/*
			 * create a new empty merge info tracker
			 */
			NodeMergeInfo<I, L, K, R> mergeInfo = new NodeMergeInfo<>(source, false);

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
			final ArrayList<IDLTerm<I, L, K, R>> sourceTerms = new ArrayList<>(
				source.getTerms());
			source.getTerms().clear();
			if (target._terms.addAll(sourceTerms)) {
				mergeInfo.setModified(target);
			}


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

	/**
	 * Notify all node merge listeners of an immanent node merge.
	 *
	 * @param source The source node of the node merge (the node to disappear)
	 * @param target The target node of the node merge
	 */
	public void notifyNodeMergeListeners(final IABoxNode<I, L, K, R> source, final IABoxNode<I, L, K, R> target)
	{
		Collection<INodeMergeListener<I, L, K, R>> listeners = getNodeMergeListeners();
		if (!listeners.isEmpty()) {
			for (INodeMergeListener<I, L, K, R> listener : listeners) {
				assert listener != null;
				listener.beforeNodeMerge(source, target);
			}
		}
	}

	@Override
	public List<INodeMergeListener<I, L, K, R>> getNodeMergeListeners()
	{
		return _nodeMergeListeners;
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="Collection factories">
	@Override
	public ICollectionFactory<NodeID, ? extends Set<NodeID>> getNodeIDSetFactory()
	{
		return _common.getNodeIDSetFactory();
	}
	
	@Override
	public IDLTermFactory<I, L, K, R> getDLTermFactory()
	{
		return _common.getTermFactory();
	}

	@Override
	public TermEntryFactory<I, L, K, R> getTermEntryFactory()
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

	@Override
	public String toString(final String prefix)
	{
		if (TO_STRING_ID_ONLY) {
			return getID().toString();
		} else {

			int capacity = 0;
			if (!isEmpty()) {
				capacity = size() * iterator().next().toString().length();
			}
			StringBuilder sb = new StringBuilder(capacity);

			sb.append(prefix);
			sb.append("ABox ");
			sb.append(getID());
			sb.append(": \n");
			final String subPrefix = prefix + "\t";
			for (IABoxNode<I, L, K, R> node : this) {
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
			sb.append("Node Queues: \n");
			sb.append(subPrefix);
			sb.append("\tGenerating Queue:     ");
			sb.append(getGeneratingQueue());
			sb.append("\n");
			sb.append(subPrefix);
			sb.append("\tNon-Generating Queue: ");
			sb.append(getNonGeneratingQueue());
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
	public Map<Object, IABoxNode<I, L, K, R>> getNodeMap()
	{
		/* XXX - returns a modifiable node map */
		return Collections.unmodifiableMap(_nodeMap);
	}
	@Override
	public IABoxNode<I, L, K, R> createNode(boolean isDatatypeNode) throws ENodeMergeException
	{
		if (isDatatypeNode) {
			return createDatatypeNode();
		} else {
			return createIndividualNode();
		}
	}

	@Override
	public IIndividualABoxNode<I, L, K, R> createIndividualNode() throws ENodeMergeException
	{
		final int newID = _nodeIDGenerator.next();
		final IndividualABoxNode<I, L, K, R> newNode = new IndividualABoxNode<>(this, newID);
		add(newNode);
		final NodeMergeInfo<I, L, K, R> mergeInfo = newNode.addClassTerm(getTBox().getGlobalDescriptions());
		return (IndividualABoxNode<I, L, K, R>) mergeInfo.getCurrentNode();
	}

	@Override
	public IDatatypeABoxNode<I, L, K, R> createDatatypeNode() throws ENodeMergeException
	{
		final int newID = _nodeIDGenerator.next();
		final LiteralABoxNode<I, L, K, R> newNode = new LiteralABoxNode<>(this, newID);		
		add(newNode);
		newNode.addDataTerm(getDLTermFactory().getDLTopDatatype());
		return newNode;
	}
	@Override
	public IDatatypeABoxNode<I, L, K, R> getOrAddDatatypeNode(L literal) throws ENodeMergeException
	{
		if (_nodeMap.containsKey(literal)) {
			return (IDatatypeABoxNode<I, L, K, R>) _nodeMap.get(literal);
		} else {
			IDatatypeABoxNode<I, L, K, R> newNode = (IDatatypeABoxNode<I, L, K, R>) createNode(true);
			/*
			 * adding the nomimal reference to the target node automatically updates the node map
			 */
			final IDLLiteralReference<I, L, K, R> li = _common.getTermFactory().getDLLiteralReference(literal);
			final NodeMergeInfo<I, L, K, R> mergeInfo = newNode.addDataTerm(li);
			_nodeMap.put(literal, newNode);
			return (IDatatypeABoxNode<I, L, K, R>) mergeInfo.getCurrentNode();
		}
	}

	@Override
	public IIndividualABoxNode<I, L, K, R> getOrAddIndividualNode(final I individual)
		throws ENodeMergeException
	{
		if (_nodeMap.containsKey(individual)) {
			return (IIndividualABoxNode<I, L, K, R>) _nodeMap.get(individual);
		} else {
			IIndividualABoxNode<I, L, K, R> newNode = createIndividualNode();
			/*
			 * adding the nomimal reference to the target node automatically updates the node map
			 */
			final IDLIndividualReference<I, L, K, R> indRef = _common.getTermFactory().getDLIndividualReference(
				individual);
			final NodeMergeInfo<I, L, K, R> mergeInfo = newNode.addClassTerm(indRef);
			return (IIndividualABoxNode<I, L, K, R>) mergeInfo.getCurrentNode();
		}
	}

	@Override
	public IABoxNode<I, L, K, R> getNode(final NodeID id)
	{
		return _nodeMap.get(id);
	}

	@Override
	public IDatatypeABoxNode<I, L, K, R> getDatatypeNode(L literal)
	{
		return (IDatatypeABoxNode<I, L, K, R>) _nodeMap.get(literal);
	}

	@Override
	public IIndividualABoxNode<I, L, K, R> getIndividualNode(I individual)
	{
		return (IIndividualABoxNode<I, L, K, R>) _nodeMap.get(individual);
	}

	@Override
	public List<ICollectionListener<IABoxNode<I, L, K, R>, Collection<IABoxNode<I, L, K, R>>>> getNodeSetListeners()
	{
		return _nodes.getListeners();
	}

	/// </editor-fold>
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
	public Iterator<IABoxNode<I, L, K, R>> iterator()
	{
		Iterator<IABoxNode<I, L, K, R>> iter = _nodes.iterator();

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
	public boolean add(final IABoxNode<I, L, K, R> e)
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
	public  boolean addAll(final Collection<? extends IABoxNode<I, L, K, R>> c)
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
	@Override
	public int deepHashCode()
	{
		int hashCode = 323;
		for (IABoxNode<I, L, K, R> node : this) {
			hashCode += 3 * node.deepHashCode();
		}
		hashCode += 5 * getBlockingStateCache().hashCode();
		return hashCode;
	}

	@Override
	public boolean deepEquals(Object obj)
	{
		if (obj instanceof IABox) {
			@SuppressWarnings("unchecked")
			final IABox<I, L, K, R> other = (IABox<I, L, K, R>) obj;
			final Map<Object, IABoxNode<I, L, K, R>> otherNodeMap = other.getNodeMap();
			for (IABoxNode<I, L, K, R> thisNode : this) {
				/**
				 * We check if the target ABox contains matching nodes with the same node ID.
				 *
				 * This may be more restrictive than required.
				 *
				 */
				final IABoxNode<I, L, K, R> otherNode = other.getNodeMap().get(thisNode.getNodeID());
				if (!thisNode.deepEquals(otherNode)) {
					return false;
				}
			}
			for (IABoxNode<I, L, K, R> otherNode : other) {
				if (!getNodeMap().containsKey(otherNode.getNodeID())) {
					return false;
				}
			}

			return getBlockingStateCache().equals(other.getBlockingStateCache());
		} else {
			return false;
		}
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="element collection">
	@SuppressWarnings("unchecked")
	@Override
	public Set<K> getClassesInSignature()
	{
		Set<K> classes = new TreeSet<>();

		/*
		 * this is a bad case of: I don't know java generics enough to handle this
		 */
		for (IABoxNode<I, L, K, R> node : this) {
			for (IDLTerm<I, L, K, R> desc : node.getTerms()) {
				Set<IDLClassReference<I, L, K, R>> subTerms = TermUtil.collectSubTerms(desc,
																					   IDLClassReference.class);
				for (IDLClassReference<I, L, K, R> subTermObj : subTerms) {
					classes.add(subTermObj.getElement());
				}
			}
		}
		for (IDLTerm<I, L, K, R> term : getTBox()) {
			Set<IDLClassReference<I, L, K, R>> subTerms = TermUtil.collectSubTerms(term, IDLClassReference.class);
			for (IDLClassReference<I, L, K, R> subTermObj : subTerms) {
				classes.add(subTermObj.getElement());
			}
		}

		return classes;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<R> getRolesInSignature()
	{
		Set<R> roles = new TreeSet<>();

		/*
		 * this is a bad case of: I don't know java generics enough to handle this
		 */
		for (IABoxNode<I, L, K, R> node : this) {
			for (IDLTerm<I, L, K, R> desc : node.getTerms()) {
				Set<IDLRoleOperator<I, L, K, R>> subTerms = TermUtil.collectSubTerms(desc, IDLRoleOperator.class);
				for (IDLRoleOperator<I, L, K, R> subTerm : subTerms) {
					roles.addAll(subTerm.getRoles());
				}
			}
		}
		for (IDLTerm<I, L, K, R> term : getTBox()) {
			Set<IDLRoleOperator<I, L, K, R>> subTerms = TermUtil.collectSubTerms(term, IDLRoleOperator.class);
			for (IDLRoleOperator<I, L, K, R> subTerm : subTerms) {
				roles.addAll(subTerm.getRoles());
			}
		}
		roles.addAll(getTBox().getRBox().getRoles());

		return roles;
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="node set listener">
	@Override
	public List<ITermSetListener<I, L, K, R>> getTermSetListeners()
	{
		return _termSetListeners;
	}

		public TermChangeEvent<I, L, K, R> notifyTermRemoved(final IABoxNode<I, L, K, R> node, final IDLTerm<I, L, K, R> term)
	{
		if (term instanceof IDLIndividualReference) {
			_nodeMap.remove(((IDLIndividualReference<I, L, K, R>) term).getIndividual());
		} else if (term instanceof IDLLiteralReference) {
			_nodeMap.remove(((IDLLiteralReference<I, L, K, R>) term).getLiteral());
		}

		touchNode(node);

		final TermChangeEvent<I, L, K, R> ev = new TermChangeEvent<>(this, node, term);
		if (!_termSetListeners.isEmpty()) {
			for (ITermSetListener<I, L, K, R> listener : _termSetListeners) {
				listener.termRemoved(ev);
			}
		}
		return ev;
	}
	public ABoxNodeEvent<I, L, K, R> notifyTermSetCleared(final IABoxNode<I, L, K, R> node)
	{
		final ABoxNodeEvent<I, L, K, R> ev = new ABoxNodeEvent<>(this, node);

		/* XXX - can possible be done more efficiently */
		Iterator<Object> iter = _nodeMap.keySet().iterator();
		while (iter.hasNext()) {
			/* remove only named references */
			final Object key = iter.next();
			if (!(key instanceof NodeID)) {
				if (node.equals(_nodeMap.get(key))) {
					iter.remove();
				}
			}
		}

		touchNode(node);

		if (!_termSetListeners.isEmpty()) {
			for (ITermSetListener<I, L, K, R> listener : _termSetListeners) {
				listener.termSetCleared(ev);
			}
		}
		return ev;
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="IImmutable">
	@Override
	public IABox<I, L, K, R> getImmutable()
	{
		return ImmutableABox.decorate(this);
	}
	/// </editor-fold>
	@Override
	public boolean containsAllTermEntries(Collection<TermEntry<I, L, K, R>> entries)
	{
		for (TermEntry<I, L, K, R> entry : entries) {
			if (!containsTermEntry(entry)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean containsTermEntry(TermEntry<I, L, K, R> entry)
	{
		final IABoxNode<I, L, K, R> node = getNode(entry.getNodeID());
		return ((node != null) && node.getTerms().contains(entry.getTerm()));
	}

	/// <editor-fold defaultstate="collapsed" desc="DependencyMap">
	@Override
	public DependencyMap<I, L, K, R> getDependencyMap()
	{
		return _dependencyMap;
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="Node Queues">
	@Override
	public boolean hasMoreNonGeneratingNodes()
	{
		return !getNonGeneratingQueue().isEmpty();
	}
	@Override
	public boolean hasMoreGeneratingNodes()
	{
		return !getGeneratingQueue().isEmpty();
	}

	@Override
	public boolean removeFromQueues(final NodeID nodeID)
	{
		checkTBoxGeneration();
		boolean wasRemoved = false;
		final IABoxNode<I, L, K, R> node = getNode(nodeID);
		if (node != null) {
			wasRemoved = _nonGeneratingQueue.remove(nodeID);
			wasRemoved |= _generatingQueue.remove(nodeID);
		}
		return wasRemoved;
	}

	@Override
	public boolean removeNodeFromQueues(final IABoxNode<I, L, K, R> node)
	{
		checkTBoxGeneration();
		boolean wasRemoved = false;
		if (node != null) {
			final NodeID nodeID = node.getNodeID();
			wasRemoved = _nonGeneratingQueue.remove(nodeID);
			wasRemoved |= _generatingQueue.remove(nodeID);
		}
		return wasRemoved;
	}
	@Override
	public boolean removeFromQueues(final Collection<NodeID> nodeIDs)
	{
		checkTBoxGeneration();
		boolean wasRemoved = false;
		wasRemoved |= getGeneratingQueue().removeAll(nodeIDs);
		wasRemoved |= getNonGeneratingQueue().removeAll(nodeIDs);
		return wasRemoved;
	}
	@Override
	public boolean removeNodesFromQueues(final Collection<? extends IABoxNode<I, L, K, R>> nodes)
	{
		checkTBoxGeneration();
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

	@Override
	public IABoxNode<I, L, K, R> nextNonGeneratingNode()
	{
		return nextNode(_nonGeneratingQueue);
	}

	@Override
	public IABoxNode<I, L, K, R> nextGeneratingNode()
	{
		return nextNode(_generatingQueue);
	}

	/**
	 * @return the _nonGeneratingQueue
	 */
	@Override
	public SortedSet<NodeID> getNonGeneratingQueue()
	{
		checkTBoxGeneration();
		return Collections.unmodifiableSortedSet(_nonGeneratingQueue);
	}

	/**
	 * @return the _generatingQueue
	 */
	@Override
	public SortedSet<NodeID> getGeneratingQueue()
	{
		checkTBoxGeneration();
		return Collections.unmodifiableSortedSet(_generatingQueue);
	}

	@Override
	public boolean touchLiteral(final L literal)
	{
		final IABoxNode<I, L, K, R> node = getDatatypeNode(literal);
		return touchNode(node);
	}

	@Override
	public boolean touchIndividual(final I individual)
	{
		final IABoxNode<I, L, K, R> node = getIndividualNode(individual);
		return touchNode(node);
	}

	@Override
	public boolean touch(final NodeID nodeID)
	{
		IABoxNode<I, L, K, R> node = getNode(nodeID);
		return touchNode(node);
	}

	@Override
	public boolean touchAll(final Collection<NodeID> individuals)
	{
		boolean wasAdded = false;
		for (NodeID nodeID : individuals) {
			wasAdded |= touch(nodeID);
		}
		return wasAdded;
	}

	@Override
	public boolean touchNode(final IABoxNode<I, L, K, R> node)
	{
		boolean wasAdded = false;
		assert node != null;
		assert contains(node);
		assert node.getABox() == this;
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

	@Override
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
	
		protected TermChangeEvent<I, L, K, R> notifyTermAdded(final IABoxNode<I, L, K, R> node, final IDLTerm<I, L, K, R> term)
	{
		if (term instanceof IDLIndividualReference) {
			_nodeMap.put(((IDLIndividualReference<I, L, K, R>) term).getIndividual(), node);
		} else if (term instanceof IDLLiteralReference) {
			_nodeMap.put(((IDLLiteralReference<I, L, K, R>) term).getLiteral(), node);
		}

		final TermChangeEvent<I, L, K, R> ev = new TermChangeEvent<>(this, node, term);

		touchNode(node);

		if (!_termSetListeners.isEmpty()) {
			for (ITermSetListener<I, L, K, R> listener : _termSetListeners) {
				listener.termAdded(ev);
			}
		}
		return ev;
	}

	ABoxCommon<I, L, K, R> getCommon()
	{
		return _common;
	}

	/// <editor-fold defaultstate="collapsed" desc="node merging">
		private void moveSuccessors(final IABoxNode<I, L, K, R> source, final IABoxNode<I, L, K, R> target, NodeMergeInfo<I, L, K, R> mergeInfo) throws ENodeMergeException
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
		final Iterator<Map.Entry<R, Collection<NodeID>>> succIter = source.getRABox().getAssertedSuccessors().
			entrySet().iterator();
		while (succIter.hasNext()) {
			/**
			 * Iterate over the source's successors and copy the links over to the target.
			 *
			 * Reflexive references (to source) need to be rewritten to point to target, instead.
			 */
			final Map.Entry<R, Collection<NodeID>> successorEntry = succIter.next();
			final R role = successorEntry.getKey();
			/* save the successor IDs in case the remove updates through */
			final Collection<NodeID> succs = new ArrayList<>(successorEntry.getValue());
			/* remove the link */
			succIter.remove();
			for (NodeID successorID : succs) {
				if (successorID.equals(sourceID)) {
					final NodeID targetID = target.getNodeID();
					/* handle reflexive references, avoid duplicate links */
					if (target instanceof IDatatypeABoxNode) {
						throw new ENodeMergeException(target, source, "Cannot add successor to datatype node");
					} else if (!target.getRABox().getAssertedSuccessors().containsValue(role, targetID)) {
						target.getRABox().getAssertedSuccessors().put(role, targetID);
						mergeInfo.setModified(target);
					}
				} else {
					if (target instanceof IDatatypeABoxNode) {
						throw new ENodeMergeException(target, source, "Cannot add successor to datatype node");
					}

					final IABoxNode<I, L, K, R> succ = getNode(successorID);
					/**
					 * Try to remove the backlink from the successor and mark the successors as modified, if a change
					 * was made.
					 *
					 */
					if (succ.getRABox().getAssertedPredecessors().remove(role, sourceID) != null) {
						mergeInfo.setModified(succ);
					}

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

		private void movePredecessors(IABoxNode<I, L, K, R> source, IABoxNode<I, L, K, R> target, NodeMergeInfo<I, L, K, R> mergeInfo)
	{
		/**
		 * Rewrite incoming links.
		 *
		 */
		final NodeID sourceID = source.getNodeID();
		final NodeID targetID = target.getNodeID();

		final Iterator<Map.Entry<R, Collection<NodeID>>> predIter = source.getRABox().getAssertedPredecessors().
			entrySet().iterator();
		while (predIter.hasNext()) {
			final Map.Entry<R, Collection<NodeID>> predEntry = predIter.next();
			final R role = predEntry.getKey();

			/* save predecessors ids in case the remove operation updates through */
			final Collection<NodeID> preds = new ArrayList<>(predEntry.getValue());
			/* remove the link */
			for (NodeID predID : preds) {
				assert getNode(predID).getRABox().getAssertedSuccessors().containsValue(role, sourceID);
			}

			predIter.remove();
			for (NodeID predID : preds) {
				if (!predID.equals(sourceID)) {
					/* remove source link */
					IABoxNode<I, L, K, R> pred = getNode(predID);
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

		private void updateDependencyMap(IABoxNode<I, L, K, R> source, IABoxNode<I, L, K, R> target)
	{
		/**
		 * Dependency map tracking
		 *
		 * We need to update all entries that match the source node to point to the target node.
		 *
		 */
		final Collection<Pair<TermEntry<I, L, K, R>, TermEntry<I, L, K, R>>> newEntries = new ArrayList<>();
		final Collection<Pair<TermEntry<I, L, K, R>, TermEntry<I, L, K, R>>> removeEntries = new ArrayList<>();
		final Collection<TermEntry<I, L, K, R>> newGovTerms = new ArrayList<>();

		final DependencyMap<I, L, K, R> dependencyMap = getDependencyMap();
		final TermEntryFactory<I, L, K, R> termEntryFactory = getTermEntryFactory();
		final Iterator<Map.Entry<TermEntry<I, L, K, R>, Collection<TermEntry<I, L, K, R>>>> entryIter = dependencyMap.entrySet().iterator();

		final NodeID sourceID = source.getNodeID();
		final NodeID targetID = target.getNodeID();

		while (entryIter.hasNext()) {
			boolean replace = false;
			final Map.Entry<TermEntry<I, L, K, R>, Collection<TermEntry<I, L, K, R>>> mapEntry = entryIter.
				next();
			final TermEntry<I, L, K, R> childEntry = mapEntry.getKey();
			NodeID newNodeID = childEntry.getNodeID();
			if (newNodeID.equals(sourceID)) {
				replace = true;
				newNodeID = targetID;
			}
			for (TermEntry<I, L, K, R> parentEntry : mapEntry.getValue()) {
				NodeID newParentNodeID = parentEntry.getNodeID();
				if (parentEntry.getNodeID().equals(sourceID)) {
					replace = true;
					newParentNodeID = targetID;
				}
				if (replace) {
					removeEntries.add(Pair.wrap(mapEntry.getKey(), parentEntry));
					final TermEntry<I, L, K, R> newChildEntry = termEntryFactory.getEntry(
						newNodeID,
						childEntry.getTerm());
					final TermEntry<I, L, K, R> newParentEntry = termEntryFactory.getEntry(
						newParentNodeID,
						parentEntry.getTerm());

					newEntries.add(Pair.wrap(newChildEntry, newParentEntry));
				}
			}
		}
		for (Pair<TermEntry<I, L, K, R>, TermEntry<I, L, K, R>> removeEntry : removeEntries) {
			dependencyMap.remove(removeEntry.getFirst(), removeEntry.getSecond());
		}
		removeEntries.clear();
		for (Pair<TermEntry<I, L, K, R>, TermEntry<I, L, K, R>> newEntry : newEntries) {
			dependencyMap.put(newEntry.getFirst(), newEntry.getSecond());
		}
		newEntries.clear();

		final Iterator<TermEntry<I, L, K, R>> govTermIter = dependencyMap.getGoverningTerms().iterator();
		while (govTermIter.hasNext()) {
			final TermEntry<I, L, K, R> govTerm = govTermIter.next();
			if (govTerm.getNodeID().equals(sourceID)) {
				govTermIter.remove();
				final TermEntry<I, L, K, R> newTerm = termEntryFactory.getEntry(targetID, govTerm.getTerm());
				newGovTerms.add(newTerm);
			}
		}

		/* update dependency map with new temrs */
		for (TermEntry<I, L, K, R> govTerm : newGovTerms) {
			dependencyMap.addGoverningTerm(govTerm);
		}
	}

		private IABoxNode<I, L, K, R> nextNode(final Collection<NodeID> queue)
	{
		checkTBoxGeneration();

		IABoxNode<I, L, K, R> nextNode = null;
		while ((nextNode == null) && (!queue.isEmpty())) {
			final Iterator<NodeID> iter = queue.iterator();
			assert iter.hasNext();
			final NodeID nextNodeID = iter.next();
			iter.remove();
			nextNode = getNode(nextNodeID);
		}
		return nextNode;
	}

	private void checkTBoxGeneration()
	{
		if (_tboxGeneration < getTBox().getGeneration()) {
			touchNodes(_nodes);
			_tboxGeneration = getTBox().getGeneration();
		}
	}
}