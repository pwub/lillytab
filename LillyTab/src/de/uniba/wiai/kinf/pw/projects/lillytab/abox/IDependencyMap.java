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
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import java.util.Collection;
import org.apache.commons.collections15.MultiMap;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public interface IDependencyMap<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends MultiMap<TermEntry<Name, Klass, Role>, TermEntry<Name, Klass, Role>>, IImmutable<IDependencyMap<Name, Klass, Role>>
{
	/**
	 * <p>
	 * Returns the list of governing terms of the current ABox.
	 * </p><p>
	 * When creating a branch, new terms are often added when
	 * branch is created initially. When every branching action
	 * adds these terms uniquely identify the branch.
	 * </p><p>
	 * For example in description logics without an at-most number
	 * restriction, branches are uniquely identified by their 
	 * governing terms.
	 * </p>
	 * 
	 * @return The list of the governing terms of the current {@see IABox}.
	 */
	Collection<TermEntry<Name, Klass, Role>> getGoverningTerms();
	
	boolean addGoverningTerm(final IABoxNode<Name, Klass, Role> node, final IDLTerm<Name, Klass, Role> term);
	boolean addGoverningTerm(final NodeID nodeID, final IDLTerm<Name, Klass, Role> term);
	boolean addGoverningTerm(final TermEntry<Name, Klass, Role> termEntry);
	
	void addParent(final IABoxNode<Name, Klass, Role> node, final IDLTerm<Name, Klass, Role> term, final IABoxNode<Name, Klass, Role> parentNode, final IDLTerm<Name, Klass, Role> parentTerm);
	void addParent(final NodeID nodeID, final IDLTerm<Name, Klass, Role> term, final NodeID parentNodeID, final IDLTerm<Name, Klass, Role> parentTerm);
	void addParent(final TermEntry<Name, Klass, Role> termEntry, final TermEntry<Name, Klass, Role> parentEntry);
	void addParent(final NodeID nodeID, final IDLTerm<Name, Klass, Role> term, final TermEntry<Name, Klass, Role> parentEntry);
	void addParent(final TermEntry<Name, Klass, Role> termEntry, final NodeID parentNode, final IDLTerm<Name, Klass, Role> parentTerm);

	/**
	 * <p>
	 * Returns {@literal null}, if {@literal entry} does not have any parent entries.
	 * </p>
	 * Get the direct parents of the {@literal entry}.
	 * @param entry
	 * @return A collection of the direct parents of {@literal entry}.
	 */
	Collection<TermEntry<Name, Klass, Role>> getParents(final TermEntry<Name, Klass, Role> entry);
	/**
	 * Get the direct parents of the entry composed of {@literal nodeID} and {@literal term}.
	 * @param nodeID The node id of the child term entry to look for.
	 * @param term The term of the child term entry to look for.
	 * @return A collection of the direct parents of the entry composed of {@literal nodeID} and {@literal term}.
	 */
	Collection<TermEntry<Name, Klass, Role>> getParents(final NodeID nodeID, final IDLTerm<Name, Klass, Role> term);
	Collection<TermEntry<Name, Klass, Role>> getParents(final IABoxNode<Name, Klass, Role> node, final IDLTerm<Name, Klass, Role> term);
	Collection<TermEntry<Name, Klass, Role>> getParents(final TermEntry<Name, Klass, Role> entry, boolean recursive);
	Collection<TermEntry<Name, Klass, Role>> getParents(final NodeID nodeID, final IDLTerm<Name, Klass, Role> term, boolean recursive);
	Collection<TermEntry<Name, Klass, Role>> getParents(final IABoxNode<Name, Klass, Role> node, final IDLTerm<Name, Klass, Role> term, boolean recursive);

	Collection<TermEntry<Name, Klass, Role>> getChildren(final TermEntry<Name, Klass, Role> entry);
	Collection<TermEntry<Name, Klass, Role>> getChildren(final NodeID nodeID, final IDLTerm<Name, Klass, Role> term);
	Collection<TermEntry<Name, Klass, Role>> getChildren(final IABoxNode<Name, Klass, Role> node, final IDLTerm<Name, Klass, Role> term);
	Collection<TermEntry<Name, Klass, Role>> getChildren(final TermEntry<Name, Klass, Role> entry, boolean recursive);
	Collection<TermEntry<Name, Klass, Role>> getChildren(final NodeID nodeID, final IDLTerm<Name, Klass, Role> term, boolean recursive);
	Collection<TermEntry<Name, Klass, Role>> getChildren(final IABoxNode<Name, Klass, Role> node, final IDLTerm<Name, Klass, Role> term, boolean recursive);
	
	boolean hasChild(final TermEntry<Name, Klass, Role> parent, final TermEntry<Name, Klass, Role> child);
	boolean hasChild(final NodeID parentNodeID, final IDLTerm<Name, Klass, Role> parentTerm, final NodeID childNodeID, final IDLTerm<Name, Klass, Role> childTerm);
	boolean hasChild(final IABoxNode<Name, Klass, Role> parentNode, final IDLTerm<Name, Klass, Role> parentTerm, final IABoxNode<Name, Klass, Role> childNode, final IDLTerm<Name, Klass, Role> childTerm);

	boolean containsKey(final IABoxNode<Name, Klass, Role> node, final IDLTerm<Name, Klass, Role> term);

	boolean containsValue(final IABoxNode<Name, Klass, Role> node, final IDLTerm<Name, Klass, Role> term, final IABoxNode<Name, Klass, Role> parentNode, final IDLTerm<Name, Klass, Role> parentTerm);
	boolean containsValue(final NodeID nodeID, final IDLTerm<Name, Klass, Role> term, final NodeID parentNodeID, final IDLTerm<Name, Klass, Role> parentTerm);
	
	Collection<TermEntry<Name, Klass, Role>> getNodeRoots(final NodeID node);
	Collection<TermEntry<Name, Klass, Role>> getNodeRoots(final IABoxNode<Name, Klass, Role> node);

	IDependencyMap<Name, Klass, Role> clone();
	
	TermEntryFactory<Name, Klass, Role>	getTermEntryFactory();
}
