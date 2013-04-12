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

import de.dhke.projects.cutil.collections.immutable.IImmutable;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.EIllegalTermTypeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import java.util.Collection;
import java.util.SortedSet;

/**
 *  {@link IABoxNode}s are the representation of individuals in a description logic tableaux.
 * <p />
 * Nodes are either associated with named individuals or not. If a node is associated with at least one individual, it
 * is called a named node. The set of named individuals is available via {@link #getNames() }.
 * <p />If a node is named, one of the names is the <emph>primary name</emph>
 * of the node. The primary name is available via {@link #getPrimaryName()}. By convention the primary name is the first
 * name in the sorted list of node names.
 * <p />
 * Every ABox node has a set of (possibly complex) concepts (available via {@link #getTerms()
 * }. The individual represented by the ABox node is an instance of these concepts.
 * <p />
 * Additionally, ABox nodes may be linked by <emph>role instances</emph>. A role instance is associated with two ABox
 * nodes and a role name. Role links are maintained in two interdependent maps, the successor map (available via {@link #getSuccessors()
 * }. and the predecessor map (available via {@link #getPredecessors()}.
 * <p />
 * <strong>Note:</strong> Successor and predecessor lists refer to {@link NodeID}s and not {@link IABoxNode}s. This
 * simplifies internal handling, but also represents a leaky abstraction.
 * <p />
 * A node also maintains a list of blocked nodes and conversely also a potential reference to a blocking node. These are
 * used by DL reasoners.
 * 
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 * @param <I> The type for nominals and values
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 */
public interface IABoxNode<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> 
	extends Comparable<IABoxNode<I, L, K, R>>, IImmutable<IABoxNode<I, L, K, R>> {

	/**
	 * @return The unmodifiable {@link NodeID} of the current node.
	 */
	NodeID getNodeID();


	/**
	 * @return The (potentially empty) set of names associated with this node.
	 */
	SortedSet<?> getNames();


	/**
	 * @return The primary name or value of this node.
	 */
	Object getPrimaryName();


	/**
	 * @return {@literal true} If the current node has at least one name.
	 */
	boolean isAnonymous();


	/**
	 * 
	 * Every ABox individual (named or anonymous) is an instance of one or more concepts.
	 * 
	 *
	 * @return Return the set of concept terms
	 */
	ITermSet<I, L, K, R> getTerms();


	/**
	 * Get a collection of {@link TermEntry} representing the terms from the node's term set.
	 *
	 * @see #getTerms()
	 * @return A collection of {@link TermEntries} representing the terms of the current node.
	 */
	Collection<TermEntry<I, L, K, R>> getTermEntries();


	/**
	 * 
	 * The RABox the role associations between nodes.
	 * 
	 *
	 * @see IRABox
	 * @return ŧhe RABox of the current node.
	 */
	IRABox<I, L, K, R> getRABox();

	/**
	 * Create a copy of the current Node in the target ABox.
	 *
	 * @param newABox The ABox to create the cloned node in.
	 * @return A clone of the current node in {@literal newABox}.
	 */
	IABoxNode<I, L, K, R> clone(final IABox<I, L, K, R> newABox);


	/**
	 * @return The {@link IABox} this node is associated with.
	 */
	IABox<I, L, K, R> getABox();

//	/**
//	 * 
//	 * Perform concept unfolding an all concept terms of the current node.
//	 * <p />
//	 * When the unfolding produces nominals references, node joins
//	 * (see {@link IABox#mergeNodes(de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode, de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode) }
//	 * may take place. {@literal unfoldAll()} thus returns a {@link NodeMergeInfo}
//	 * indicating the ID of the target node containing the unfoldings
//	 * and information, if the target node was modified.
//	 * <p />
//	 * Note, that unfolding may invalidate the current node.
//	 * The caller is responsible for checking the returned {@link NodeMergeInfo},
//	 * if the current node is still valid and react accordingly if further
//	 * processing is required.
//	 * 
//	 *
//	 * @return A {@link NodeMergeInfo} indicating the ID of the target node containing the unfoldings
//	 * and information, if the target node was modified.
//	 * @throws ENodeMergeException A node merge was required, but failed.
//	 */
//	NodeMergeInfo<I, L, K, R> unfoldAll() throws ENodeMergeException;

	/**
	 * 
	 * Calculate the hashcode of the current node by deep inspection of the node's contents.
	 * <p />
	 * Contrary to {@link #hashCode() }, the node's contents are taken into account and not only the node ID.
	 * 
	 *
	 * @return The hashcode of the current node, calculated through deep inspection of the node's contents.
	 */
	int deepHashCode();


	/**
	 * 
	 * Compare two {@link IABoxNode}es through deep inspection
	 * <p />
	 * Contrary to {@link #equals(java.lang.Object) }, this compares two nodes through deep inspection.
	 * 
	 *
	 * @param obj The target node to compare to
	 * @return {@literal true}, if both nodes are equal, {@literal false}, if not.
	 */
	boolean deepEquals(Object obj);


	/**
	 * Check, if the current node is a datatype node and thus cannot have successors.
	 *
	 * @return Is the current node a datatype node?
	 */
	boolean isDatatypeNode();


	String toString(String prefix);


	@Override
	IABoxNode<I, L, K, R> getImmutable();
	
	NodeMergeInfo<I, L, K, R> addTerm(final IDLRestriction<I, L, K, R> term)
		throws ENodeMergeException, EIllegalTermTypeException;
	NodeMergeInfo<I, L, K, R> addTerms(final Collection<? extends IDLRestriction<I, L, K, R>> terms)
		throws ENodeMergeException, EIllegalTermTypeException;	
}
