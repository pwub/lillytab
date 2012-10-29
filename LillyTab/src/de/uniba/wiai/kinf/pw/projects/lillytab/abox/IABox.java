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
package de.uniba.wiai.kinf.pw.projects.lillytab.abox;

import de.uniba.wiai.kinf.pw.projects.lillytab.blocking.IBlockingStateCache;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.ITBox;
import de.dhke.projects.cutil.collections.factories.ICollectionFactory;
import de.dhke.projects.cutil.collections.aspect.ICollectionListener;
import de.dhke.projects.cutil.collections.immutable.IImmutable;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.tbox.TBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 * An ABox is a set of instance nodes associated with descriptions. An axiom also references a set of global axioms, the
 * {@link TBox} of the ABox. </p><p> ABoxes are the primary target of a tableaux reasoner. During operation, a tableaux
 * reasoner will modify the description sets of the nodes in the ABox and potentially add additional nodes to the ABox.
 * </p> </p><p> Nodes inside an {@link IABox} need to have a natural order. Note, that the order of nodes in an ABox is
 * not allowed to change, while a node is part of the ABox's node set. If it is required to change the order of a node,
 * it has to be removed and re-inserted into the ABox. This ensures, that any module that depends on the natural node
 * order can register itself as a node list listener (see {@link #getNodeSetListeners()}/) and be informed of any
 * required updates. </p>
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 * @param <Name> The type for nominals and values
 * @param <Klass> The type for DL classes
 * @param <Role> The type for properties (roles)
 */
public interface IABox<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends SortedSet<IABoxNode<Name, Klass, Role>>, IImmutable<IABox<Name, Klass, Role>>, Cloneable {

	/**
	 * <p> Retrieve the unique ID of the current ABox. </p>
	 *
	 * @return The ID of the current {@link IABox}.
	 * @see NodeID
	 */
	NodeID getID();


	/**
	 * <p> Create a new, anonymous nodeand add it to the current ABox. </p><p> Note, that this does NOT unfold global
	 * descriptions onto the newly created node. </p><p> The {
	 *
	 * @see NodeID} of the new node is garantueed to be unique for the current ABox even across modifications (i.e. node
	 * merges). </p>
	 * @seealso #mergeNodes(de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode,
	 * de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode)
	 * @return The newly created IABoxNode.
	 */
	IABoxNode<Name, Klass, Role> createNode(boolean isDatatypeNode)
		throws ENodeMergeException;


	/**
	 * <p> Retrieve or create a new named {@link IABoxNode} referencing the specified {@literal individual}. If the
	 * individual did not exist, it will be created and added to the current Abox. </p> The {
	 *
	 * @see NodeID} for newly created nodes is garantueed to be unique for the current ABox even across modifications
	 * (i.e. node merges). </p>
	 * @seealso
	 * @param individual The individual to wrap.
	 * @return A named individual wrapping {@literal individual}.
	 */
	IABoxNode<Name, Klass, Role> getOrAddNamedNode(Name individual, boolean isDatatypeNode)
		throws ENodeMergeException;



	/**
	 * <p> The node map maps node names and node IDs to actual nodes. </p><p> As the current ABox may be cloned,
	 * references to ABox nodes should never be to the real class but indirectly via node ID or node name. </p>
	 *
	 * @return The the map of all nodes, indexed by identifier.
	 */
	Map<Object, IABoxNode<Name, Klass, Role>> getNodeMap();


	/**
	 * Retrieve the node with the specified {@literal id}.
	 *
	 * @param id The {@link NodeID}} of the node to retrieve.
	 * @return The node with the specified id or {@literal null}, if the node could not be found.
	 */
	IABoxNode<Name, Klass, Role> getNode(NodeID id);


	/**
	 * Retrieve the node with the specified name.
	 *
	 * @param name The name of the node to retrieve.
	 * @return The node with the specified name or {@literal null}, if the node could not be found.
	 *
	 */
	IABoxNode<Name, Klass, Role> getNode(Name name);


	/**
	 * @return The {@link TBox} for the ABox.
	 */
	ITBox<Name, Klass, Role> getTBox();


	/**
	 * @return A deep clone of the current {@link IABox }.
	 */
	IABox<Name, Klass, Role> clone();


	/**
	 * @return The {@link IDLTermFactory} of the current ABox.
	 */
	IDLTermFactory<Name, Klass, Role> getDLTermFactory();


	/**
	 * @return The {@link TermEntryFactory} of the current ABox.
	 */
	TermEntryFactory<Name, Klass, Role> getTermEntryFactory();


	/**
	 * @return A factory used to create a set of {@link NodeID}s.
	 */
	ICollectionFactory<NodeID, Set<NodeID>> getNodeIDSetFactory();


	/**
	 * Determine, if {@literal node1} and {@literal node2} can be merged.
	 *
	 * @param node1 First node to merge.
	 * @param node2 Second node to merge.
	 * @return {@literal true}, if {@literal node1} and {@literal node2} can be merged.
	 */
	boolean canMerge(final IABoxNode<Name, Klass, Role> node1, final IABoxNode<Name, Klass, Role> node2);


	/**
	 * <p> Join the two nodes {@literal node1} and {@literal node2} into a single node. The result of the join is a
	 * single node containing all concepts (and thus names) of both nodes. </p><p> The returned node is the target node
	 * of the join, which is <em>always</em> the smaller (as defined by the natural order) of both nodes. </p><p> After
	 * the join operation, only the target node of the returned {@link NodeMergeInfo} is a valid node inside the current
	 * ABox, all other nodes have been removed and should not be accessed any more. </p><p> The smaller node is
	 * returned, as we want to make sure, that we never process any later node before we are done processing earlier
	 * nodes. If we join to the smaller node, we always move in to the direction of smaller nodes, thus we may revisit
	 * nodes, but nether jump over nodes. </p>
	 *
	 * @param node1 First node to merge.
	 * @param node2 Second node to merge.
	 * @return The merged node, normally either {@literal node1} or {@literal node2}.
	 * @throws ENodeMergeException The two nodes could not be merged.
	 */
	NodeMergeInfo<Name, Klass, Role> mergeNodes(final IABoxNode<Name, Klass, Role> node1,
												final IABoxNode<Name, Klass, Role> node2)
		throws ENodeMergeException;


	/**
	 * <p> Retrieve the list of {@link ICollectionListener}s of the internal node set. </p><p> The
	 * {@link ICollectionListener} is informed, whenever a modification is made to the node list of the current ABox.
	 * </p><p> In particular, nodes are removed and re-added, when their order in the node list changes. Classes that
	 * depend on the order of nodes can register themselves as collection listeners. </p><p> Note, that callers should
	 * take care when modifying the listener list, because the list also may also contain internal listeners. In
	 * particular, it is not safe to assume that the list is initially empty. </p>
	 *
	 * @return The list of collection listeners of this ABox's node list.
	 */
	List<ICollectionListener<IABoxNode<Name, Klass, Role>, Collection<IABoxNode<Name, Klass, Role>>>> getNodeSetListeners();


	/**
	 * <p> Retrieve the list of {@link INodeMergeListener}s attached to this {@link IABox}. </p><p> A
	 * {@link INodeMergeListener} is informed, when two nodes are merged into one. </p><p> Note, that callers should
	 * take care when modifying the listener list, because the list also may also contain internal listeners. In
	 * particular, it is not safe to assume that the list is initially empty. </p><p> Listeners are not cloned together
	 * with ABoxes. </p>
	 *
	 * @return The modifiable list of {@link INodeMergeListener}s.
	 *
	 */
	List<INodeMergeListener<Name, Klass, Role>> getNodeMergeListeners();


	/**
	 * <p> Return the list of {@link ITermSetListener}s attached to this {@link IABox}. </p><p> The
	 * {@link ITermSetListener} are informed, when the term set of a node is modified. </p><p> Listeners are not cloned
	 * together with ABoxes. </p>
	 *
	 * @return The modifiable list of {@link ITermSetListener}s.
	 */
	List<ITermSetListener<Name, Klass, Role>> getTermSetListeners();


	/**
	 * <p> Calculate the hashcode of the current node by deep inspection of the abox's contents. </p><p> Contrary to {@link #hashCode()
	 * }, the abox's contents are taken into account and not only the object identity. </p>
	 *
	 * @return The hashcode of the current node, calculated through deep inspection of the abox's contents.
	 */
	int deepHashCode();


	/**
	 * <p> Compare two {@link IABox}es through deep inspection </p><p> Contrary to {@link #equals(java.lang.Object) },
	 * this compares two nodes through deep inspection. </p>
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
	Set<Klass> getClassesInSignature();


	/**
	 * Get the list of all role references by any of the axioms in the current ABox, TBox or RBox.
	 *
	 * @return A list of roles from the ABox.
	 *
	 */
	Set<Role> getRolesInSignature();


	/**
	 * Return the blocking state cache of the current ABox. The blocking state cache is used by reasoners to store
	 * cached information about the blocking status of nodes inside the ABox.
	 *
	 * @return The blocking state cache associated with the current ABox.
	 */
	IBlockingStateCache getBlockingStateCache();


	String toString(String prefix);


	/**
	 * <p> Return an immutable version of the current ABox. </p>>
	 *
	 *
	 * @return An immutable version of the current ABox.
	 */
	@Override
	IABox<Name, Klass, Role> getImmutable();


//	/**
//	 * <p> Retrieve the list of unfold listeners. </p><p> {@link IUnfoldListener}s are informed, whenever a node's
//	 * concept set is changed in result to an unfold (see {@link #addUnfoldedDescription(de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression)
//	 * }
//	 * operation. </p><p> Note, that callers should take care when modifying the listener list, because the list also
//	 * may also contain internal listeners. In particular, it is not safe to assume that the list is initially empty.
//	 * </p>
//	 *
//	 * @return The modifiable list of {@link IUnfoldListener}s.
//	 */
//	List<IUnfoldListener<Name, Klass, Role>> getUnfoldListeners();


	/**
	 * Retrieve the dependency map for the current {@link IABox}.
	 *
	 * @return The dependency map for the current {@link IABox}
	 */
	IDependencyMap<Name, Klass, Role> getDependencyMap();


	/**
	 * Determine if the current {@link IABox} contains the supplied {@link TermEntry}.
	 *
	 */
	boolean containsTermEntry(final TermEntry<Name, Klass, Role> entry);


	/**
	 * Determine if the current {@link IABox} contain all of the supplied {@link TermEntry}s.
	 *
	 */
	boolean containsAllTermEntries(final Collection<TermEntry<Name, Klass, Role>> entries);
	
	/**
	 * Perform lazy unfolding for all terms (for all nodes)
	 * contained in the current ABox.
	 * 
	 * @throws ENodeMergeException 
	 **/
	public void unfoldAll() 
		throws ENodeMergeException;
	
}
