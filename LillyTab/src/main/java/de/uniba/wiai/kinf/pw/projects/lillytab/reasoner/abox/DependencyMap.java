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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox;

import de.dhke.projects.cutil.collections.CollectionUtil;
import de.dhke.projects.cutil.collections.factories.TreeSetFactory;
import de.dhke.projects.cutil.collections.map.GenericMultiHashMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IDependencyMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntryFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.immutable.ImmutableDependencyMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
// public class DependencyMap<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> 
public class DependencyMap<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends GenericMultiHashMap<TermEntry<I, L, K, R>, TermEntry<I, L, K, R>>
	implements IDependencyMap<I, L, K, R> {

	private static final long serialVersionUID = 1947663804178866730L;
	private final TermEntryFactory<I, L, K, R> _entryFactory;
	private final Set<TermEntry<I, L, K, R>> _governingTerms = new TreeSet<>();


	public DependencyMap(final TermEntryFactory<I, L, K, R> entryFactory)
	{
		super(new TreeSetFactory<TermEntry<I, L, K, R>>());
		_entryFactory = entryFactory;
	}


	@Override
	public Collection<TermEntry<I, L, K, R>> getParents(final TermEntry<I, L, K, R> entry)
	{
		final Collection<TermEntry<I, L, K, R>> parents = get(entry);
		if (parents != null) {
			return parents;
		} else {
			return Collections.<TermEntry<I, L, K, R>>emptySet();
		}

	}


	@Override
	public Collection<TermEntry<I, L, K, R>> getParents(final NodeID nodeID,
														final IDLTerm<I, L, K, R> term)
	{
		return getParents(_entryFactory.getEntry(nodeID, term));
	}


	@Override
	public Collection<TermEntry<I, L, K, R>> getParents(final IABoxNode<I, L, K, R> node,
														final IDLTerm<I, L, K, R> term)
	{
		return getParents(_entryFactory.getEntry(node, term));
	}

//	private void getParentsRecursive(final TermEntry<I, L, K, R> entry, final Collection<TermEntry<I, L, K, R>> targetSet)
//	{
//		targetSet.add(entry);
//		final Collection<TermEntry<I, L, K, R>> directParents = getParents(entry);
//		for (TermEntry<I, L, K, R> child: directParents) {
//			if (! targetSet.contains(child))
//				getParentsRecursive(child, targetSet);
//		}
//		
//		targetSet.addAll(directParents);
//		
//	}

	@Override
	public Collection<TermEntry<I, L, K, R>> getParents(TermEntry<I, L, K, R> entry, boolean recursive)
	{
		Collection<TermEntry<I, L, K, R>> directParents = getParents(entry);
		if (!recursive) {
			return directParents;
		} else if (directParents != null) {
			final Set<TermEntry<I, L, K, R>> allParents = new TreeSet<>();
			for (TermEntry<I, L, K, R> parentEntry : directParents) {
				assert !allParents.contains(parentEntry);
				allParents.addAll(getParents(parentEntry, true));
			}
			allParents.addAll(directParents);
			return allParents;
		} else {
			return null;
		}
	}


	@Override
	public Collection<TermEntry<I, L, K, R>> getParents(IABoxNode<I, L, K, R> node,
														IDLTerm<I, L, K, R> term, boolean recursive)
	{
		return getParents(node.getNodeID(), term, recursive);
	}


	@Override
	public Collection<TermEntry<I, L, K, R>> getParents(NodeID nodeID,
														IDLTerm<I, L, K, R> term, boolean recursive)
	{
		final TermEntry<I, L, K, R> entry = _entryFactory.getEntry(nodeID, term);
		return getParents(entry, recursive);
	}


	@Override
	public boolean containsKey(final IABoxNode<I, L, K, R> node, final IDLTerm<I, L, K, R> term)
	{
		return containsKey(_entryFactory.getEntry(node, term));
	}


	@Override
	public boolean containsValue(IABoxNode<I, L, K, R> node,
								 IDLTerm<I, L, K, R> term,
								 IABoxNode<I, L, K, R> parentNode,
								 IDLTerm<I, L, K, R> parentTerm)
	{
		return containsValue(node.getNodeID(), term, parentNode.getNodeID(), parentTerm);
	}


	@Override
	public boolean containsValue(NodeID nodeID,
								 IDLTerm<I, L, K, R> term, NodeID parentNodeID,
								 IDLTerm<I, L, K, R> parentTerm)
	{
		return containsValue(_entryFactory.getEntry(nodeID, term), _entryFactory.getEntry(parentNodeID, parentTerm));
	}


	@Override
	public void addParent(final IABoxNode<I, L, K, R> node, final IDLTerm<I, L, K, R> term,
						  final IABoxNode<I, L, K, R> parentNode, final IDLTerm<I, L, K, R> parentTerm)
	{
		addParent(node.getNodeID(), term, parentNode.getNodeID(), parentTerm);
	}


	@Override
	public void addParent(final TermEntry<I, L, K, R> termEntry, final TermEntry<I, L, K, R> parentEntry)
	{
		if (termEntry.equals(parentEntry))
			throw new IllegalArgumentException("Term cannot be its own parent");
		put(termEntry, parentEntry);
	}


	@Override
	public void addParent(final TermEntry<I, L, K, R> termEntry, final NodeID parentNode,
						  final IDLTerm<I, L, K, R> parentTerm)
	{
		addParent(termEntry, _entryFactory.getEntry(parentNode, parentTerm));
	}


	@Override
	public void addParent(final NodeID nodeID, final IDLTerm<I, L, K, R> term,
						  final TermEntry<I, L, K, R> parentEntry)
	{
		addParent(_entryFactory.getEntry(nodeID, term), parentEntry);
	}


	@Override
	public void addParent(NodeID nodeID,
						  IDLTerm<I, L, K, R> term, NodeID parentNodeID,
						  IDLTerm<I, L, K, R> parentTerm)
	{
		addParent(_entryFactory.getEntry(nodeID, term), _entryFactory.getEntry(parentNodeID, parentTerm));
	}


	@Override
	public DependencyMap<I, L, K, R> clone()
	{
		final DependencyMap<I, L, K, R> klone = new DependencyMap<>(_entryFactory);
		klone.putAll(this);
		klone._governingTerms.addAll(getGoverningTerms());
		return klone;
	}


	@Override
	public IDependencyMap<I, L, K, R> getImmutable()
	{
		return ImmutableDependencyMap.decorate(this);
	}


	@Override
	public Collection<TermEntry<I, L, K, R>> getChildren(TermEntry<I, L, K, R> parent)
	{
		final Set<TermEntry<I, L, K, R>> children = new HashSet<>();
		for (Map.Entry<TermEntry<I, L, K, R>, Collection<TermEntry<I, L, K, R>>> mapEntry : entrySet()) {
			if (mapEntry.getValue().contains(parent)) {
				children.add(mapEntry.getKey());
			}
		}
		return children;
	}


	@Override
	public Collection<TermEntry<I, L, K, R>> getChildren(NodeID nodeID, IDLTerm<I, L, K, R> term)
	{
		return getChildren(_entryFactory.getEntry(nodeID, term));
	}


	@Override
	public Collection<TermEntry<I, L, K, R>> getChildren(IABoxNode<I, L, K, R> node,
														 IDLTerm<I, L, K, R> term)
	{
		return getChildren(node.getNodeID(), term);
	}


	@Override
	public Collection<TermEntry<I, L, K, R>> getChildren(
		TermEntry<I, L, K, R> entry, boolean recursive)
	{
		if (recursive) {
			final Set<TermEntry<I, L, K, R>> allChildren = new TreeSet<>();
			getChildrenRecursive(entry, allChildren);
			return allChildren;
		} else {
			return getChildren(entry);
		}
	}


	@Override
	public Collection<TermEntry<I, L, K, R>> getChildren(IABoxNode<I, L, K, R> node, IDLTerm<I, L, K, R> term, boolean recursive)
	{
		return getChildren(node.getNodeID(), term, recursive);
	}


	@Override
	public Collection<TermEntry<I, L, K, R>> getChildren(NodeID nodeID, IDLTerm<I, L, K, R> term, boolean recursive)
	{
		return getChildren(_entryFactory.getEntry(nodeID, term), recursive);
	}


	@Override
	public boolean hasChild(TermEntry<I, L, K, R> parent, TermEntry<I, L, K, R> child)
	{
		final Collection<TermEntry<I, L, K, R>> parents = getParents(child);
		return parents.contains(parent);
	}


	@Override
	public boolean hasChild(NodeID parentNodeID, IDLTerm<I, L, K, R> parentTerm, NodeID childNodeID, IDLTerm<I, L, K, R> childTerm)
	{
		return hasChild(_entryFactory.getEntry(parentNodeID, parentTerm), _entryFactory.getEntry(childNodeID, childTerm));
	}


	@Override
	public boolean hasChild(IABoxNode<I, L, K, R> parentNode, IDLTerm<I, L, K, R> parentTerm, IABoxNode<I, L, K, R> childNode, IDLTerm<I, L, K, R> childTerm)
	{
		return hasChild(parentNode.getNodeID(), parentTerm, childNode.getNodeID(), childTerm);
	}


	@Override
	public String toString()
	{
		/* a wild guess at the required capacity */
		final StringBuilder sb = new StringBuilder(_governingTerms.size() * 4);
		sb.append("{");
		boolean isFirst = true;
		for (TermEntry<I, L, K, R> termEntry : _governingTerms) {
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(", @");
			}
			sb.append(termEntry);
		}
		for (Map.Entry<TermEntry<I, L, K, R>, Collection<TermEntry<I, L, K, R>>> entry : this.entrySet()) {
			sb.append("<");
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(", ");
			}
			sb.append(entry.getKey());
			sb.append(": ");
			sb.append(CollectionUtil.deepToString(entry.getValue()));
			sb.append(">");
		}
		sb.append("}");
		return sb.toString();
	}


	@Override
	public TermEntryFactory<I, L, K, R> getTermEntryFactory()
	{
		return _entryFactory;
	}


	@Override
	public Collection<TermEntry<I, L, K, R>> getGoverningTerms()
	{
		return _governingTerms;
	}


	@Override
	public boolean addGoverningTerm(IABoxNode<I, L, K, R> node, IDLTerm<I, L, K, R> term)
	{
		return _governingTerms.add(_entryFactory.getEntry(node, term));
	}


	@Override
	public boolean addGoverningTerm(NodeID nodeID, IDLTerm<I, L, K, R> term)
	{
		return _governingTerms.add(_entryFactory.getEntry(nodeID, term));
	}


	@Override
	public boolean addGoverningTerm(TermEntry<I, L, K, R> termEntry)
	{
		return _governingTerms.add(termEntry);
	}


	@Override
	public Collection<TermEntry<I, L, K, R>> getNodeRoots(final NodeID nodeID)
	{
		final Collection<TermEntry<I, L, K, R>> nodeRoots = new ArrayList<>();
		for (TermEntry<I, L, K, R> entry : this.values()) {
			if ((entry.getNodeID().equals(nodeID) && ((!containsKey(entry)) || (get(entry) == null) || (get(entry).
				isEmpty())))) {
				nodeRoots.add(entry);
			}
		}
		return nodeRoots;
	}


	@Override
	public Collection<TermEntry<I, L, K, R>> getNodeRoots(final IABoxNode<I, L, K, R> node)
	{
		return getNodeRoots(node.getNodeID());
	}


	@Override
	public boolean hasGoverningTerm(IABoxNode<I, L, K, R> node, IDLTerm<I, L, K, R> term)
	{
		return hasGoverningTerm(getTermEntryFactory().getEntry(node, term));
	}


	@Override
	public boolean hasGoverningTerm(
		TermEntry<I, L, K, R> termEntry)
	{
		return _governingTerms.contains(termEntry);
	}


	@Override
	public boolean hasGoverningTerm(
		NodeID nodeID, IDLTerm<I, L, K, R> term)
	{
		return hasGoverningTerm(getTermEntryFactory().getEntry(nodeID, term));
	}


	@Override
	public boolean hasGoverningTerm(NodeID nodeID)
	{
		for (TermEntry<I, L, K, R> govTerm : _governingTerms) {
			if (govTerm.getNodeID().equals(nodeID))
				return true;
		}
		return false;
	}


	@Override
	public boolean hasGoverningTerm(IABoxNode<I, L, K, R> node)
	{
		return hasGoverningTerm(node.getNodeID());
	}


		private void getChildrenRecursive(
		final TermEntry<I, L, K, R> entry, final Collection<TermEntry<I, L, K, R>> targetSet)
	{
		final Collection<TermEntry<I, L, K, R>> directChildren = getChildren(entry);
		for (TermEntry<I, L, K, R> child : directChildren) {
			if (!targetSet.contains(child)) {
				targetSet.add(child);
				getChildrenRecursive(child, targetSet);
			}
		}

		targetSet.addAll(directChildren);
	}
}
