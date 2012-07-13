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
package de.uniba.wiai.kinf.pw.projects.lillytab.abox;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import java.util.Collection;
import java.util.SortedSet;
import javax.management.relation.Role;
import org.apache.commons.collections15.MultiMap;

/**
 * <p>
 *  {@link IABoxNode}s are the representation of individuals in a description logic tableaux.
 * </p><p>
 *   Nodes are either associated with named individuals or not. If a node is associated
 *   with at least one individual, it is called a named node. The set of named individuals
 *	 is available via {@link #getNames() }.
 * </p><p>If a node is named, one of the names is
 *   the <emph>primary name</emph> of the node. The primary name is
 *   available via {@link #getPrimaryName()}. By convention the primary
 *   name is the first name in the sorted list of node names.
 * </p><p>
 *   Every ABox node has a set of (possibly complex) concepts (available via {@link #getTerms() }. The individual
 *   represented by the ABox node is an instance of these concepts.
 * </p><p>
 *   Additionally, ABox nodes may be linked by <emph>role instances</emph>. A role
 *   instance is associated with two ABox nodes and a role name.
 *   Role links are maintained in two interdependent maps,
 *   the successor map (available via {@link #getSuccessors() }.
 *   and the predecessor map (available via {@link #getPredecessors()}.
 * </p><p>
 *   <strong>Note:</strong> Successor and predecessor lists refer to {@link NodeID}s and
 *   not {@link IABoxNode}s. This simplifies internal handling, but also represents a leaky abstraction.
 * </p><p>
 *   A node also maintains a list of blocked nodes and conversely also a potential reference
 *   to a blocking node. These are used by DL reasoners.
 * </p>
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 * @param <Name> The type for named individuals.
 * @param <Klass> Type for class names
 * @param <Role> Type for role names
 */
public interface IABoxNode<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends Comparable<IABoxNode<Name, Klass, Role>> {

	/**
	 * @return The unmodifiable {@link NodeID} of the current node.
	 */
	NodeID getNodeID();

	/**
	 * @return The (potentially empty) set of names associated with this node.
	 */
	SortedSet<Name> getNames();

	/**
	 * @return  The primary name of this node.
	 */
	Name getPrimaryName();

	/**
	 * @return {@literal true} If the current node has at least one name.
	 */
	boolean isAnonymous();

	/**
	 * <p>
	 *  Every ABox individual (named or anonymous) is an instance of
	 *  one or more concepts.
	 * </p>
	 *
	 * @return Return the set of concept terms
	 */
	ITermSet<Name, Klass, Role> getTerms();
	
	/**
	 * <p>Get a collection of {@link TermEntry} representing the
	 * terms from the node's term set.
	 * @see #getTerms() 
	 * @return A collection of {@link TermEntries} representing the terms of the current node.
	 */
	Collection<TermEntry<Name, Klass, Role>> getTermEntries();

	/**
	 *
	 **/
	// SortedSet<IDLClassExpression<Name, Klass, Role>> getVisitedConceptTerms();
	/**
	 *
	 **/
	// SortedSet<IDLClassExpression<Name, Klass, Role>> getUnvisitedConceptTerms();
	/**
	 * @return The IDs of nodes that a connected to the current node via a role link.
	 */
	@Deprecated
	MultiMap<Role, NodeID> getSuccessors();

	/**
	 * @return All nodes that his nodes receives incoming connections via a role link.
	 */
	@Deprecated
	MultiMap<Role, NodeID> getPredecessors();
		
	/**
	 * The link map contains the role associations between nodes.
	 * 
	 * @see ILinkMap
	 * @return ŧhe link map.
	 */
	// ILinkMap<Name, Klass, Role> getLinkMap();

	/**
	 * <p>
	 * Add the specified description to the current node.
	 * </p><p>
	 * When the description list contains nominals, the current node
	 * may have to be joined with other nodes in the ABox. The method
	 * thus returns an appropriate {@link NodeMergeInfo} object describing
	 * any eventual merge operation.
	 * </p><p>
	 * After a merge operation, only the target node of the returned {@link NodeMergeInfo}
	 * is a valid node of the current {@link IABox}. All other nodes have been merged away.
	 * </p>
	 *
	 * @param desc A description to add to the current node.
	 * @return A {@link NodeMergeInfo} indicating the progress of the operation.
	 */
	NodeMergeInfo<Name, Klass, Role> addUnfoldedDescription(final IDLRestriction<Name, Klass, Role> desc)
		throws ENodeMergeException;

	/**
	 * <p>
	 * Add the specified descriptions to the current node.
	 * </p><p>
	 * When the description list contains nominals, the current node
	 * may have to be joined with other nodes in the ABox. The method
	 * thus returns an appropriate {@link NodeMergeInfo} object describing
	 * any eventual merge operation.
	 * </p><p>
	 * After a merge operation, only the target node of the returned {@link NodeMergeInfo}
	 * is a valid node of the current {@link IABox}. All other nodes have been merged away.
	 * </p>
	 *
	 * @param descs Sequence of descriptions to add to the current node.
	 * @return A {@link NodeMergeInfo} indicating the ID of the target node containing the unfoldings
	 * and information, if the target node was modified.
	 */
	NodeMergeInfo<Name, Klass, Role> addUnfoldedDescriptions(
		final Iterable<? extends IDLRestriction<Name, Klass, Role>> descs)
		throws ENodeMergeException;

	/**
	 * Create a copy of the current Node in the target ABox.
	 *
	 * @param newABox The ABox to create the cloned node in.
	 * @return A clone of the current node in {@literal newABox}.
	 */
	IABoxNode<Name, Klass, Role> clone(final IABox<Name, Klass, Role> newABox);

	/**
	 * @return The {@link IABox} this node is associated with.
	 */
	IABox<Name, Klass, Role> getABox();

	/**
	 * <p>
	 * Perform concept unfolding an all concept terms of the current node.
	 * </p><p>
	 * When the unfolding produces nominals references, node joins
	 * (see {@link IABox#mergeNodes(de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode, de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode) }
	 * may take place. {@literal unfoldAll()} thus returns a {@link NodeMergeInfo}
	 * indicating the ID of the target node containing the unfoldings
	 * and information, if the target node was modified.
	 * </p><p>
	 * Note, that unfolding may invalidate the current node.
	 * The caller is responsible for checking the returned {@link NodeMergeInfo},
	 * if the current node is still valid and react accordingly if further
	 * processing is required.
	 * </p>
	 *
	 * @return A {@link NodeMergeInfo} indicating the ID of the target node containing the unfoldings
	 * and information, if the target node was modified.
	 */
	NodeMergeInfo<Name, Klass, Role> unfoldAll()
		throws ENodeMergeException;

	/**
	 * <p>
	 * Calculate the hashcode of the current node by deep inspection of
	 * the node's contents.
	 * </p><p>
	 * Contrary to {@link #hashCode() }, the node's contents are taken into account
	 * and not only the node ID.
	 * </p>
	 * @return The hashcode of the current node, calculated through deep inspection of the node's contents.
	 */
	int deepHashCode();

	/**
	 * <p>
	 * Compare two {@link IABoxNode}es through deep inspection
	 * </p><p>
	 * Contrary to {@link #equals(java.lang.Object) }, this compares
	 * two nodes through deep inspection.
	 * </p>
	 *
	 * @param obj The target node to compare to
	 * @return {@literal true}, if both nodes are equal, {@literal false}, if not.
	 */
	boolean deepEquals(Object obj);

	/**
	 * Check, if the current node is a datatype node and thus
	 * cannot have successors.
	 *
	 * @return Is the current node a datatype node?
	 */
	boolean isDatatypeNode();

	String toString(String prefix);

	IABoxNode<Name, Klass, Role> getImmutable();
}
