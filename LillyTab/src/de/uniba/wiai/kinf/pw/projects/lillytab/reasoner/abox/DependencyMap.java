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

import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.immutable.ImmutableDependencyMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IDependencyMap;
import de.dhke.projects.cutil.collections.CollectionUtil;
import de.dhke.projects.cutil.collections.map.MultiTreeSetHashMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntryFactory;
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
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
// public class DependencyMap<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
public class DependencyMap<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends MultiTreeSetHashMap<TermEntry<Name, Klass, Role>, TermEntry<Name, Klass, Role>>
	implements IDependencyMap<Name, Klass, Role>
{
	private static final long serialVersionUID = 1947663804178866730L;
	
	private final TermEntryFactory<Name, Klass, Role> _entryFactory;
	private final Set<TermEntry<Name, Klass, Role>> _governingTerms = new TreeSet<TermEntry<Name, Klass, Role>>();

	public DependencyMap(final TermEntryFactory<Name, Klass, Role> entryFactory)
	{
		super();
		_entryFactory = entryFactory;
	}

	@Override
	public Collection<TermEntry<Name, Klass, Role>> getParents(final TermEntry<Name, Klass, Role> entry)
	{
		final Collection<TermEntry<Name, Klass, Role>> parents = get(entry);
		if (parents != null)
			return parents;
		else
			return Collections.<TermEntry<Name, Klass, Role>>emptySet();

	}

	@Override
	public Collection<TermEntry<Name, Klass, Role>> getParents(final NodeID nodeID, final IDLTerm<Name, Klass, Role> term)
	{
		return getParents(_entryFactory.getEntry(nodeID, term));
	}

	@Override
	public Collection<TermEntry<Name, Klass, Role>> getParents(final IABoxNode<Name, Klass, Role> node, final IDLTerm<Name, Klass, Role> term)
	{
		return getParents(_entryFactory.getEntry(node, term));
	}
	
//	private void getParentsRecursive(final TermEntry<Name, Klass, Role> entry, final Collection<TermEntry<Name, Klass, Role>> targetSet)
//	{
//		targetSet.add(entry);
//		final Collection<TermEntry<Name, Klass, Role>> directParents = getParents(entry);
//		for (TermEntry<Name, Klass, Role> child: directParents) {
//			if (! targetSet.contains(child))
//				getParentsRecursive(child, targetSet);
//		}
//		
//		targetSet.addAll(directParents);
//		
//	}


	@Override
	public Collection<TermEntry<Name, Klass, Role>> getParents(TermEntry<Name, Klass, Role> entry, boolean recursive)
	{
		Collection<TermEntry<Name, Klass, Role>> directParents = getParents(entry);		
		if (!recursive)
			return directParents;
		else if (directParents != null) {
			final Set<TermEntry<Name, Klass, Role>> allParents = new TreeSet<TermEntry<Name, Klass, Role>>();
			for (TermEntry<Name, Klass, Role> parentEntry: directParents) {
				assert ! allParents.contains(parentEntry);
				allParents.addAll(getParents(parentEntry, true));
			}
			allParents.addAll(directParents);
			return allParents;
		} else
			return null;
	}


	@Override
	public Collection<TermEntry<Name, Klass, Role>> getParents(IABoxNode<Name, Klass, Role> node,
															   IDLTerm<Name, Klass, Role> term, boolean recursive)
	{
		return getParents(node.getNodeID(), term, recursive);
	}


	@Override
	public Collection<TermEntry<Name, Klass, Role>> getParents(NodeID nodeID,
															   IDLTerm<Name, Klass, Role> term, boolean recursive)
	{
		final TermEntry<Name, Klass, Role> entry = _entryFactory.getEntry(nodeID, term);
		return getParents(entry, recursive);
	}
	
	

	@Override
	public boolean containsKey(final IABoxNode<Name, Klass, Role> node, final IDLTerm<Name, Klass, Role> term)
	{
		return containsKey(_entryFactory.getEntry(node, term));
	}

	@Override
	public boolean containsValue(IABoxNode<Name, Klass, Role> node,
								 IDLTerm<Name, Klass, Role> term,
								 IABoxNode<Name, Klass, Role> parentNode,
								 IDLTerm<Name, Klass, Role> parentTerm)
	{
		return containsValue(node.getNodeID(), term, parentNode.getNodeID(), parentTerm);
	}

	@Override
	public boolean containsValue(NodeID nodeID,
								 IDLTerm<Name, Klass, Role> term, NodeID parentNodeID,
								 IDLTerm<Name, Klass, Role> parentTerm)
	{
		return containsValue(_entryFactory.getEntry(nodeID, term), _entryFactory.getEntry(parentNodeID, parentTerm));
	}



	@Override
	public void addParent(final IABoxNode<Name, Klass, Role> node, final IDLTerm<Name, Klass, Role> term, final IABoxNode<Name, Klass, Role> parentNode, final IDLTerm<Name, Klass, Role> parentTerm)
	{
		addParent(node.getNodeID(), term, parentNode.getNodeID(), parentTerm);
	}

	@Override
	public void addParent(final TermEntry<Name, Klass, Role> termEntry, final TermEntry<Name, Klass, Role> parentEntry)
	{
		put(termEntry, parentEntry);
	}
	
	@Override
	public void addParent(final TermEntry<Name, Klass, Role> termEntry, final NodeID parentNode, final IDLTerm<Name, Klass, Role> parentTerm)
	{
		put(termEntry, _entryFactory.getEntry(parentNode, parentTerm));
	}

	@Override
	public void addParent(final NodeID nodeID, final IDLTerm<Name, Klass, Role> term, final TermEntry<Name, Klass, Role> parentEntry)
	{
		put(_entryFactory.getEntry(nodeID, term), parentEntry);
	}

	@Override
	public void addParent(NodeID nodeID,
						  IDLTerm<Name, Klass, Role> term, NodeID parentNodeID,
						  IDLTerm<Name, Klass, Role> parentTerm)
	{
		addParent(_entryFactory.getEntry(nodeID, term), _entryFactory.getEntry(parentNodeID, parentTerm));
	}



	@Override
	public DependencyMap<Name, Klass, Role> clone()
	{
		final DependencyMap<Name, Klass, Role> klone = new DependencyMap<Name, Klass, Role>(_entryFactory);
		klone.putAll(this);
		klone.getGoverningTerms().addAll(getGoverningTerms());
		return klone;
	}

	@Override
	public IDependencyMap<Name, Klass, Role> getImmutable()
	{
		return ImmutableDependencyMap.decorate(this);
	}

	@Override
	public Collection<TermEntry<Name, Klass, Role>> getChildren(TermEntry<Name, Klass, Role> parent)		
	{
		final Set<TermEntry<Name, Klass, Role>> children = new HashSet<TermEntry<Name, Klass, Role>>();
		for (Map.Entry<TermEntry<Name, Klass, Role>, Collection<TermEntry<Name, Klass, Role>>> mapEntry: entrySet()) {
			if (mapEntry.getValue().contains(parent))
				children.add(mapEntry.getKey());
		}
		return children;
	}

	@Override
	public Collection<TermEntry<Name, Klass, Role>> getChildren(NodeID nodeID, IDLTerm<Name, Klass, Role> term)
	{
		return getChildren(_entryFactory.getEntry(nodeID, term));
	}

	@Override
	public Collection<TermEntry<Name, Klass, Role>> getChildren(IABoxNode<Name, Klass, Role> node, IDLTerm<Name, Klass, Role> term)
	{
		return getChildren(node.getNodeID(), term);
	}
	
	private void getChildrenRecursive(		
		final TermEntry<Name, Klass, Role> entry,
		final Collection<TermEntry<Name, Klass, Role>> targetSet)
	{		
		targetSet.add(entry);
		final Collection<TermEntry<Name, Klass, Role>> directChildren = getChildren(entry);
		for (TermEntry<Name, Klass, Role> child: directChildren) {
			if (! targetSet.contains(child))
				getChildrenRecursive(child, targetSet);
		}
		
		targetSet.addAll(directChildren);
	}
	
	@Override
	public Collection<TermEntry<Name, Klass, Role>> getChildren(TermEntry<Name, Klass, Role> entry, boolean recursive)
	{
		if (recursive) {
			final Set<TermEntry<Name, Klass, Role>> allChildren = new TreeSet<TermEntry<Name, Klass, Role>>();
			getChildrenRecursive(entry, allChildren);
			return allChildren;
		} else
			return getChildren(entry);
	}


	@Override
	public Collection<TermEntry<Name, Klass, Role>> getChildren(IABoxNode<Name, Klass, Role> node,
																IDLTerm<Name, Klass, Role> term, boolean recursive)
	{
		return getChildren(node.getNodeID(), term, recursive);
	}


	@Override
	public Collection<TermEntry<Name, Klass, Role>> getChildren(NodeID nodeID,
																IDLTerm<Name, Klass, Role> term, boolean recursive)
	{
		return getChildren(_entryFactory.getEntry(nodeID, term), recursive);
	}
	
	@Override
	public boolean hasChild(TermEntry<Name, Klass, Role> parent, TermEntry<Name, Klass, Role> child)
	{
		final Collection<TermEntry<Name, Klass, Role>> parents = getParents(child);
		return parents.contains(parent);
	}

	@Override
	public boolean hasChild(NodeID parentNodeID, IDLTerm<Name, Klass, Role> parentTerm, NodeID childNodeID, IDLTerm<Name, Klass, Role> childTerm)
	{
		return hasChild(_entryFactory.getEntry(parentNodeID, parentTerm), _entryFactory.getEntry(childNodeID, childTerm));
	}

	@Override
	public boolean hasChild(IABoxNode<Name, Klass, Role> parentNode, IDLTerm<Name, Klass, Role> parentTerm, IABoxNode<Name, Klass, Role> childNode, IDLTerm<Name, Klass, Role> childTerm)
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
		for (TermEntry<Name, Klass, Role> termEntry: _governingTerms) {
			if (isFirst)
				isFirst = false;
			else
				sb.append(", @");
			sb.append(termEntry);			
		}
		for (Map.Entry<TermEntry<Name, Klass, Role>, Collection<TermEntry<Name, Klass, Role>>> entry: this.entrySet()) {
			if (isFirst)
				isFirst = false;
			else
				sb.append(", ");
			sb.append(entry.getKey());
			sb.append(": ");
			sb.append(CollectionUtil.deepToString(entry.getValue()));
		}
		sb.append("}");
		return sb.toString();
	}

	@Override
	public TermEntryFactory<Name, Klass, Role> getTermEntryFactory()
	{
		return _entryFactory;
	}

	@Override
	public Collection<TermEntry<Name, Klass, Role>> getGoverningTerms()
	{
		return _governingTerms;
	}

	@Override
	public boolean addGoverningTerm(IABoxNode<Name, Klass, Role> node,
									IDLTerm<Name, Klass, Role> term)
	{
		return _governingTerms.add(_entryFactory.getEntry(node, term));
	}

	@Override
	public boolean addGoverningTerm(NodeID nodeID,
									IDLTerm<Name, Klass, Role> term)
	{
		return _governingTerms.add(_entryFactory.getEntry(nodeID, term));
	}

	@Override
	public boolean addGoverningTerm(TermEntry<Name, Klass, Role> termEntry)
	{
		return _governingTerms.add(termEntry);
	}

	@Override
	public Collection<TermEntry<Name, Klass, Role>> getNodeRoots(final NodeID nodeID)
	{
		final Collection<TermEntry<Name, Klass, Role>> nodeRoots = new ArrayList<TermEntry<Name, Klass, Role>>();
		for (TermEntry<Name, Klass, Role> entry: this.values()) {
			if ((entry.getNodeID().equals(nodeID) && ((! containsKey(entry)) || (get(entry) == null) || (get(entry).isEmpty()))))
				nodeRoots.add(entry);
		}
		return nodeRoots;
	}

	@Override
	public Collection<TermEntry<Name, Klass, Role>> getNodeRoots(final IABoxNode<Name, Klass, Role> node)
	{
		return getNodeRoots(node.getNodeID());
	}
}
