/**
 * (c) 2009-2013 Otto-Friedrich-University Bamberg
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
package de.uniba.wiai.kinf.pw.projects.lillytab.abox;

import de.dhke.projects.cutil.collections.aspect.ICollectionListener;
import de.dhke.projects.cutil.collections.factories.ICollectionFactory;
import de.dhke.projects.cutil.collections.immutable.IImmutable;
import de.uniba.wiai.kinf.pw.projects.lillytab.blocking.IBlockingStateCache;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.tbox.TBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IAssertedRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.ITBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;


/**
 * An ABox is a set of instance nodes associated with descriptions. An axiom also references a set of global axioms, the
 * {@link TBox} of the ABox. <p />
 * ABoxes are the primary target of a tableaux reasoner. During operation, a tableaux reasoner will modify the
 * description sets of the nodes in the ABox and potentially add additional nodes to the ABox.
 * <p /> Nodes inside an {@link IABox} need to have a natural order. Note, that the order of nodes in an ABox is not
 * allowed to change, while a node is part of the ABox's node set. If it is required to change the order of a node, it
 * has to be removed and re-inserted into the ABox. This ensures, that any module that depends on the natural node order
 * can register itself as a node list listener (see {@link #getNodeSetListeners()}/) and be informed of any required
 * updates.
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 * 
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public interface IABox<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends Set<IABoxNode<I, L, K, R>>, IImmutable<IABox<I, L, K, R>>, Cloneable, Serializable
{
	/**
	 * Retrieve the unique ID of the current ABox.
	 *
	 * @return The ID of the current {@link IABox}.
	 * @see NodeID
	 */
	NodeID getID();

	/**
	 * Create a new, anonymous node and add it to the current ABox. For datatype nodes, the returned node is empty.
	 * Individual nodes ((@literal isDatatypeNode == false}) are created with global descriptions unfolded into the
	 * newly created node.
	 * <p />
	 * The {
	 *
	 * @see NodeID} of a newly node is garantueed to be unique for the current ABox and will never be re-used. However
	 * the returned node may not actually have that node id any more because of merging. In the case of merging, the
	 * newly created node will always be merged into an already existing node. In this case, the existing (the target
	 * node of the merge) node will be returned.
	 *
	 * @see #mergeNodes(de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode,
	 * de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode)
	 * @param isDatatypeNode {@literal true} if the new node should be a datatype node.
	 * @return The newly created IABoxNode.
	 * @throws ENodeMergeException Node merging was performed and an unrecoverable error occured while merging.
	 *
	 */
	IABoxNode<I, L, K, R> createNode(boolean isDatatypeNode)
		throws ENodeMergeException;

	/**
	 * Create a new, anonymous node literal node. The returned node is empty.
	 *
	 * @return The newly created IABoxNode.
	 *
	 * @throws ENodeMergeException Node merging was performed and an unrecoverable error occured while merging.
	 */
	IDatatypeABoxNode<I, L, K, R> createDatatypeNode()
		throws ENodeMergeException;

	/**
	 * Create a new, anonymous individual (non-datatype node and add it to the current ABox. Individual nodes
	 * ((@literal isDatatypeNode == false}) are created with global descriptions unfolded into the newly created node.
	 * <p />
	 * The {
	 *
	 * @see NodeID} of a newly node is garantueed to be unique for the current ABox and will never be re-used. However
	 * the returned node may not actually have that node id any more because of merging. In the case of merging, the
	 * newly created node will always be merged into an already existing node. In this case, the existing (the target
	 * node of the merge) node will be returned.
	 *
	 * @see #mergeNodes(de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode,
	 * de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode)
	 * @return The newly created IABoxNode.
	 * @throws ENodeMergeException Node merging was performed and an unrecoverable error occured while merging.
	 *
	 */
	IIndividualABoxNode<I, L, K, R> createIndividualNode()
		throws ENodeMergeException;

	/**
	 *
	 * Retrieve or create a new named {@link IABoxNode} referencing the specified {@literal individual}. If the
	 * individual did not exist, it will be created and added to the current ABox. The returned node is never a datatype
	 * node. Use {@link #getOrAddDatatypeNode(java.lang.Comparable) } if you need a datatype node.
	 * <p />
	 * Individual nodes are created with global descriptions unfolded into the newly created node.
	 * <p />
	 * The {
	 *
	 * @see NodeID} of a newly node is garantueed to be unique for the current ABox and will never be re-used. However
	 * the returned node may not actually have that node id any more because of merging. In the case of merging, the
	 * newly created node will always be merged into an already existing node. In this case, the existing (the target
	 * node of the merge) node will be returned.
	 *
	 * @see #createNode(boolean)
	 * @param individual The individual to wrap.
	 * @return A named individual wrapping {@literal individual}.
	 * @throws ENodeMergeException Node merging was performed and an unrecoverable error occured while merging.
	 */
	IIndividualABoxNode<I, L, K, R> getOrAddIndividualNode(final I individual)
		throws ENodeMergeException;

	/**
	 *
	 * Retrieve or create a new named {@link IABoxNode} referencing the specified {@literal literal}. If the individual
	 * did not exist, it will be created and added to the current ABox. The returned node is always a datatype node. Use {@link #getOrAddIndividualNode(java.lang.Comparable)
	 * } if you need a non-datatype node.
	 * <p />
	 * Individual nodes are created with global descriptions unfolded into the newly created node.
	 * <p />
	 * The {
	 *
	 * @see NodeID} of a newly node is garantueed to be unique for the current ABox and will never be re-used. However
	 * the returned node may not actually have that node id any more because of merging. In the case of merging, the
	 * newly created node will always be merged into an already existing node. In this case, the existing (the target
	 * node of the merge) node will be returned.
	 *
	 * @see #createNode(boolean)
	 * @param literal The literal value to wrap.
	 * @return A named individual wrapping {@literal individual}.
	 * @throws ENodeMergeException Node merging was performed and an unrecoverable error occured while merging.
	 */
	IDatatypeABoxNode<I, L, K, R> getOrAddDatatypeNode(final L literal)
		throws ENodeMergeException;

	/**
	 * The node map maps node names and node IDs to actual nodes. <p /> As the current ABox may be cloned, references to
	 * ABox nodes should never be to the real class but indirectly via node ID or node name.
	 *
	 * @return The the map of all nodes, indexed by identifier.
	 */
	Map<Object, IABoxNode<I, L, K, R>> getNodeMap();

	/**
	 * Retrieve the node with the specified {@literal id}.
	 *
	 * @param id The {@link NodeID}} of the node to retrieve.
	 * @return The node with the specified id or {@literal null}, if the node could not be found.
	 */
	IABoxNode<I, L, K, R> getNode(NodeID id);

	/**
	 * Retrieve the node with the specified {@literal literal}
	 *
	 * @param literal The literal to look for.
	 * @return The node for the specified literal or {@literal null} if no such node could be found.
	 *
	 */
	IDatatypeABoxNode<I, L, K, R> getDatatypeNode(final L literal);

	/**
	 * Retrieve the node with the specified {@literal individual}
	 *
	 * @param individual The node name to look for.
	 * @return The node for the specified individual.
	 * @throws NoSuchElementException A node with the specified individual could not be found.
	 *
	 */
	IIndividualABoxNode<I, L, K, R> getIndividualNode(final I individual);

	/**
	 * @return The {@link TBox} for the ABox.
	 */
	ITBox<I, L, K, R> getTBox();

	/**
	 * @return The {@link IRBox} for the ABox.
	 */
	IRBox<I, L, K, R> getRBox();

	/**
	 * @return The {@link IAssertedRBox} for the ABox.
	 */
	IAssertedRBox<I, L, K, R> getAssertedRBox();

	/**
	 * @return A deep clone of the current {@link IABox }.
	 */
	IABox<I, L, K, R> clone();

	/**
	 * @return The {@link IDLTermFactory} of the current ABox.
	 */
	IDLTermFactory<I, L, K, R> getDLTermFactory();

	/**
	 * @return The {@link TermEntryFactory} of the current ABox.
	 */
	TermEntryFactory<I, L, K, R> getTermEntryFactory();

	/**
	 * @return A factory used to create a set of {@link NodeID}s.
	 */
	ICollectionFactory<NodeID, ? extends Set<NodeID>> getNodeIDSetFactory();

	/**
	 * Determine, if {@literal node1} and {@literal node2} can be merged.
	 *
	 * @param node1 First node to merge.
	 * @param node2 Second node to merge.
	 * @return {@literal true}, if {@literal node1} and {@literal node2} can be merged.
	 */
	boolean canMerge(final IABoxNode<I, L, K, R> node1, final IABoxNode<I, L, K, R> node2);

	/**
	 * Join the two nodes {@literal node1} and {@literal node2} into a single node. The result of the join is a single
	 * node containing all concepts (and thus names) of both nodes. <p /> The returned node is the target node of the
	 * join, which is <em>always</em> the smaller (as defined by the natural order) of both nodes. <p /> After the join
	 * operation, only the target node of the returned {@link NodeMergeInfo} is a valid node inside the current ABox,
	 * all other nodes have been removed and should not be accessed any more. <p /> The smaller node is returned, as we
	 * want to make sure, that we never process any later node before we are done processing earlier nodes. If we join
	 * to the smaller node, we always move in to the direction of smaller nodes, thus we may revisit nodes, but nether
	 * jump over nodes.
	 *
	 * @param node1 First node to merge.
	 * @param node2 Second node to merge.
	 * @return The merged node, normally either {@literal node1} or {@literal node2}.
	 * @throws ENodeMergeException The two nodes could not be merged.
	 */
	NodeMergeInfo<I, L, K, R> mergeNodes(final IABoxNode<I, L, K, R> node1,
										 final IABoxNode<I, L, K, R> node2)
		throws ENodeMergeException;

	/**
	 * Retrieve the list of {@link ICollectionListener}s of the internal node set. <p /> The {@link ICollectionListener}
	 * is informed, whenever a modification is made to the node list of the current ABox.
	 * <p /> In particular, nodes are removed and re-added, when their order in the node list changes. Classes that
	 * depend on the order of nodes can register themselves as collection listeners. <p /> Note, that callers should
	 * take care when modifying the listener list, because the list also may also contain internal listeners. In
	 * particular, it is not safe to assume that the list is initially empty.
	 *
	 * @return The list of collection listeners of this ABox's node list.
	 */
	List<ICollectionListener<IABoxNode<I, L, K, R>, Collection<IABoxNode<I, L, K, R>>>> getNodeSetListeners();

	/**
	 * Retrieve the list of {@link INodeMergeListener}s attached to this {@link IABox}. <p /> A
	 * {@link INodeMergeListener} is informed, when two nodes are merged into one. <p /> Note, that callers should take
	 * care when modifying the listener list, because the list also may also contain internal listeners. In particular,
	 * it is not safe to assume that the list is initially empty. <p /> Listeners are not cloned together with ABoxes.
	 *
	 * @return The modifiable list of {@link INodeMergeListener}s.
	 *
	 */
	List<INodeMergeListener<I, L, K, R>> getNodeMergeListeners();

	/**
	 * Return the list of {@link ITermSetListener}s attached to this {@link IABox}. <p /> The {@link ITermSetListener}
	 * are informed, when the term set of a node is modified. <p /> Listeners are not cloned together with ABoxes.
	 *
	 * @return The modifiable list of {@link ITermSetListener}s.
	 */
	List<ITermSetListener<I, L, K, R>> getTermSetListeners();

	/**
	 * Calculate the hashcode of the current node by deep inspection of the abox's contents. <p /> Contrary to {@link #hashCode()
	 * }, the abox's contents are taken into account and not only the object identity.
	 *
	 * @return The hashcode of the current node, calculated through deep inspection of the abox's contents.
	 */
	int deepHashCode();

	/**
	 * Compare two {@link IABox}es through deep inspection <p /> Contrary to {@link #equals(java.lang.Object) }, this
	 * compares two nodes through deep inspection.
	 *
	 * @param obj The target abox to compare to
	 * @return {@literal true}, if both aboxes are equal, {@literal false}, if not.
	 */
	boolean deepEquals(Object obj);

	/**
	 * Get the list of all class references by any of the axioms in the current ABox, TBox or RBox.
	 *
	 * @return A list of classes from the ABox.
	 *
	 */
	Set<K> getClassesInSignature();

	/**
	 * Get the list of all role references by any of the axioms in the current ABox, TBox or RBox.
	 *
	 * @return A list of roles from the ABox.
	 *
	 */
	Set<R> getRolesInSignature();

	/**
	 * Return the blocking state cache of the current ABox. The blocking state cache is used by reasoners to store
	 * cached information about the blocking status of nodes inside the ABox.
	 *
	 * @return The blocking state cache associated with the current ABox.
	 */
	IBlockingStateCache getBlockingStateCache();

	String toString(String prefix);

	/**
	 * Return an immutable version of the current ABox. >
	 *
	 *
	 * @return An immutable version of the current ABox.
	 */
	@Override
	IABox<I, L, K, R> getImmutable();

//	/**
//	 *  Retrieve the list of unfold listeners. <p /> {@link IUnfoldListener}s are informed, whenever a node's
//	 * concept set is changed in result to an unfold (see {@link #addTerm(de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression)
//	 * }
//	 * operation. <p /> Note, that callers should take care when modifying the listener list, because the list also
//	 * may also contain internal listeners. In particular, it is not safe to assume that the list is initially empty.
//	 * 
//	 *
//	 * @return The modifiable list of {@link IUnfoldListener}s.
//	 */
//	List<IUnfoldListener<I, L, K, R>> getUnfoldListeners();
	/**
	 * Retrieve the dependency map for the current {@link IABox}.
	 *
	 * @return The dependency map for the current {@link IABox}
	 */
	IDependencyMap<I, L, K, R> getDependencyMap();

	/**
	 * Determine if the current {@link IABox} contains the supplied {@link TermEntry}.	*
	 *
	 * @param entry the term entry to search for
	 * @return {@literal true} if {@literal entry} is contained in the current {@link IABox}. 
	 *
	 */
	boolean containsTermEntry(final TermEntry<I, L, K, R> entry);

	/**
	 * Determine if the current {@link IABox} contain all of the supplied {@link TermEntry}s.
	 *
	 * @param entries the term entries to look for.
	 * @return {@literal true} if {@literal entry} all term entries in the current {@link IABox}. 
	 *
	 */
	boolean containsAllTermEntries(final Collection<TermEntry<I, L, K, R>> entries);
//	/**
//	 * Perform lazy unfolding for all terms (for all nodes)
//	 * contained in the current ABox.
//	 * 
//	 * @throws ENodeMergeException 
//	 **/
//	public void unfoldAll() 
//		throws ENodeMergeException;

	boolean hasMoreGeneratingNodes();

	/// <editor-fold defaultstate="collapsed" desc="Node Queues">
	boolean hasMoreNonGeneratingNodes();

	boolean removeFromQueues(
		final Collection<NodeID> nodeIDs);

	boolean removeFromQueues(final NodeID nodeID);

	boolean removeNodeFromQueues(
		final IABoxNode<I, L, K, R> node);

	boolean removeNodesFromQueues(
		final Collection<? extends IABoxNode<I, L, K, R>> nodes);

	boolean touch(final NodeID nodeID);

	boolean touchAll(
		final Collection<NodeID> individuals);

	boolean touchIndividual(final I individual);

	boolean touchLiteral(final L literal);

	boolean touchNode(
		final IABoxNode<I, L, K, R> node);

	boolean touchNodes(
		final Collection<? extends IABoxNode<I, L, K, R>> nodes);

	IABoxNode<I, L, K, R> nextGeneratingNode();

	IABoxNode<I, L, K, R> nextNonGeneratingNode();

	/**
	 * @return the _generatingQueue
	 */
	SortedSet<NodeID> getGeneratingQueue();

	/**
	 * @return the _nonGeneratingQueue
	 */
	SortedSet<NodeID> getNonGeneratingQueue();
	/// </editor-fold>
}
