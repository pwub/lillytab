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
package de.uniba.wiai.kinf.pw.projects.lillytab.abox;

import de.dhke.projects.cutil.collections.immutable.IImmutable;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import java.util.Collection;
import org.apache.commons.collections15.MultiMap;


/**
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public interface IDependencyMap<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends MultiMap<TermEntry<I, L, K, R>, TermEntry<I, L, K, R>>,
	IImmutable<IDependencyMap<I, L, K, R>>
{
	/**
	 * 
	 * Returns the list of governing terms of the current ABox.
	 * <p />
	 * When creating a branch, new terms are often added when branch is created initially. When every branching action
	 * adds these terms uniquely identify the branch.
	 * <p />
	 * For example in description logics without an at-most number restriction, branches are uniquely identified by
	 * their governing terms.
	 * 
	 *
	 * @return The list of the governing terms of the current {
	 * @see IABox}.
	 */
	Collection<TermEntry<I, L, K, R>> getGoverningTerms();

	boolean addGoverningTerm(final IABoxNode<I, L, K, R> node, final IDLTerm<I, L, K, R> term);

	boolean addGoverningTerm(final NodeID nodeID, final IDLTerm<I, L, K, R> term);

	boolean addGoverningTerm(final TermEntry<I, L, K, R> termEntry);

	boolean hasGoverningTerm(final IABoxNode<I, L, K, R> node, final IDLTerm<I, L, K, R> term);

	boolean hasGoverningTerm(final TermEntry<I, L, K, R> termEntry);

	boolean hasGoverningTerm(final NodeID nodeID, final IDLTerm<I, L, K, R> term);

	boolean hasGoverningTerm(final NodeID nodeID);

	boolean hasGoverningTerm(final IABoxNode<I, L, K, R> node);

	void addParent(final IABoxNode<I, L, K, R> node, final IDLTerm<I, L, K, R> term,
				   final IABoxNode<I, L, K, R> parentNode, final IDLTerm<I, L, K, R> parentTerm);

	void addParent(final NodeID nodeID, final IDLTerm<I, L, K, R> term, final NodeID parentNodeID,
				   final IDLTerm<I, L, K, R> parentTerm);

	void addParent(final TermEntry<I, L, K, R> termEntry, final TermEntry<I, L, K, R> parentEntry);

	void addParent(final NodeID nodeID, final IDLTerm<I, L, K, R> term,
				   final TermEntry<I, L, K, R> parentEntry);

	void addParent(final TermEntry<I, L, K, R> termEntry, final NodeID parentNode,
				   final IDLTerm<I, L, K, R> parentTerm);

	/**
	 * 
	 * Returns {@literal null}, if {@literal entry} does not have any parent entries.
	 * 
	 * Get the direct parents of the {@literal entry}.
	 *
	 * @param entry
	 * @return A collection of the direct parents of {@literal entry}.
	 */
	Collection<TermEntry<I, L, K, R>> getParents(final TermEntry<I, L, K, R> entry);

	/**
	 * Get the direct parents of the entry composed of {@literal nodeID} and {@literal term}.
	 *
	 * @param nodeID The node id of the child term entry to look for.
	 * @param term The term of the child term entry to look for.
	 * @return A collection of the direct parents of the entry composed of {@literal nodeID} and {@literal term}.
	 */
	Collection<TermEntry<I, L, K, R>> getParents(final NodeID nodeID, final IDLTerm<I, L, K, R> term);

	Collection<TermEntry<I, L, K, R>> getParents(final IABoxNode<I, L, K, R> node,
												 final IDLTerm<I, L, K, R> term);

	Collection<TermEntry<I, L, K, R>> getParents(final TermEntry<I, L, K, R> entry, boolean recursive);

	Collection<TermEntry<I, L, K, R>> getParents(final NodeID nodeID, final IDLTerm<I, L, K, R> term,
												 boolean recursive);

	Collection<TermEntry<I, L, K, R>> getParents(final IABoxNode<I, L, K, R> node,
												 final IDLTerm<I, L, K, R> term, boolean recursive);

	Collection<TermEntry<I, L, K, R>> getChildren(final TermEntry<I, L, K, R> entry);

	Collection<TermEntry<I, L, K, R>> getChildren(final NodeID nodeID, final IDLTerm<I, L, K, R> term);

	Collection<TermEntry<I, L, K, R>> getChildren(final IABoxNode<I, L, K, R> node,
												  final IDLTerm<I, L, K, R> term);

	Collection<TermEntry<I, L, K, R>> getChildren(final TermEntry<I, L, K, R> entry, boolean recursive);

	Collection<TermEntry<I, L, K, R>> getChildren(final NodeID nodeID, final IDLTerm<I, L, K, R> term,
												  boolean recursive);

	Collection<TermEntry<I, L, K, R>> getChildren(final IABoxNode<I, L, K, R> node,
												  final IDLTerm<I, L, K, R> term, boolean recursive);

	boolean hasChild(final TermEntry<I, L, K, R> parent, final TermEntry<I, L, K, R> child);

	boolean hasChild(final NodeID parentNodeID, final IDLTerm<I, L, K, R> parentTerm, final NodeID childNodeID,
					 final IDLTerm<I, L, K, R> childTerm);

	boolean hasChild(final IABoxNode<I, L, K, R> parentNode, final IDLTerm<I, L, K, R> parentTerm,
					 final IABoxNode<I, L, K, R> childNode, final IDLTerm<I, L, K, R> childTerm);

	boolean containsKey(final IABoxNode<I, L, K, R> node, final IDLTerm<I, L, K, R> term);

	boolean containsValue(final IABoxNode<I, L, K, R> node, final IDLTerm<I, L, K, R> term,
						  final IABoxNode<I, L, K, R> parentNode, final IDLTerm<I, L, K, R> parentTerm);

	boolean containsValue(final NodeID nodeID, final IDLTerm<I, L, K, R> term, final NodeID parentNodeID,
						  final IDLTerm<I, L, K, R> parentTerm);

	Collection<TermEntry<I, L, K, R>> getNodeRoots(final NodeID node);

	Collection<TermEntry<I, L, K, R>> getNodeRoots(final IABoxNode<I, L, K, R> node);

	IDependencyMap<I, L, K, R> clone();

	TermEntryFactory<I, L, K, R> getTermEntryFactory();
}
