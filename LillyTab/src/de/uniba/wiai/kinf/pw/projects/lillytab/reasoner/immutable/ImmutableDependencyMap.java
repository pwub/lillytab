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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.immutable;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IDependencyMap;
import de.dhke.projects.cutil.collections.immutable.ImmutableMultiMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntryFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import java.util.Collection;
import java.util.Collections;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ImmutableDependencyMap<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends ImmutableMultiMap<TermEntry<Name, Klass, Role>, TermEntry<Name, Klass, Role>>
	implements IDependencyMap<Name, Klass, Role>
{
	protected ImmutableDependencyMap(final IDependencyMap<Name, Klass, Role> decoratee)
	{
		super(decoratee, null, null);
	}

	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> ImmutableDependencyMap<Name, Klass, Role> decorate(final IDependencyMap<Name, Klass, Role> baseMap)
	{
		return new ImmutableDependencyMap<Name, Klass, Role>(baseMap);
	}

	@Override
	public void addParent(IABoxNode<Name, Klass, Role> node, IDLTerm<Name, Klass, Role> term, IABoxNode<Name, Klass, Role> parentNode, IDLTerm<Name, Klass, Role> parentTerm)
	{
		throw new UnsupportedOperationException("Cannot modify an ImmutableDependencyMap");
	}

	@Override
	public void addParent(TermEntry<Name, Klass, Role> termEntry, TermEntry<Name, Klass, Role> parentEntry)
	{
		throw new UnsupportedOperationException("Cannot modify an ImmutableDependencyMap");
	}

	@Override
	public Collection<TermEntry<Name, Klass, Role>> getParents(TermEntry<Name, Klass, Role> entry)
	{
		return getDecoratee().getParents(entry);
	}

	@Override
	public IDependencyMap<Name, Klass, Role> getDecoratee()
	{
		return (IDependencyMap<Name, Klass, Role>)super.getDecoratee();
	}

	@Override
	public Collection<TermEntry<Name, Klass, Role>> getParents(NodeID nodeID, IDLTerm<Name, Klass, Role> term)
	{
		return getDecoratee().getParents(nodeID, term);
	}

	@Override
	public Collection<TermEntry<Name, Klass, Role>> getParents(IABoxNode<Name, Klass, Role> node, IDLTerm<Name, Klass, Role> term)
	{
		return getDecoratee().getParents(node, term);
	}


	@Override
	public Collection<TermEntry<Name, Klass, Role>> getParents(TermEntry<Name, Klass, Role> entry, boolean recursive)
	{
		return getDecoratee().getParents(entry, recursive);
	}


	@Override
	public Collection<TermEntry<Name, Klass, Role>> getParents(IABoxNode<Name, Klass, Role> node,
															   IDLTerm<Name, Klass, Role> term, boolean recursive)
	{
		return getDecoratee().getParents(node, term, recursive);
	}


	@Override
	public Collection<TermEntry<Name, Klass, Role>> getParents(NodeID nodeID,
															   IDLTerm<Name, Klass, Role> term, boolean recursive)
	{
		return getDecoratee().getParents(nodeID, term, recursive);
	}
	
	@Override
	public boolean containsKey(IABoxNode<Name, Klass, Role> node, IDLTerm<Name, Klass, Role> term)
	{
		return getDecoratee().containsKey(node, term);
	}

	public boolean containsKey(TermEntry<Name, Klass, Role> termEntry)
	{
		return getDecoratee().containsKey(termEntry);
	}

	@Override
	public IDependencyMap<Name, Klass, Role> getImmutable()
	{
		return this;
	}

	@Override
	public IDependencyMap<Name, Klass, Role> clone()
	{
		return getDecoratee().clone();
	}

	@Override
	public void addParent(NodeID nodeID,
						  IDLTerm<Name, Klass, Role> term, NodeID parentNodeID,
						  IDLTerm<Name, Klass, Role> parentTerm)
	{
		throw new UnsupportedOperationException("Cannot modify an ImmutableDependencyMap");
	}

	@Override
	public boolean containsValue(IABoxNode<Name, Klass, Role> node,
								 IDLTerm<Name, Klass, Role> term,
								 IABoxNode<Name, Klass, Role> parentNode,
								 IDLTerm<Name, Klass, Role> parentTerm)
	{
		return getDecoratee().containsValue(node, term, parentNode, parentTerm);
	}

	@Override
	public boolean containsValue(NodeID nodeID,
								 IDLTerm<Name, Klass, Role> term, NodeID parentNodeID,
								 IDLTerm<Name, Klass, Role> parentTerm)
	{
		return getDecoratee().containsValue(nodeID, term, parentNodeID, parentTerm);
	}

	@Override
	public Collection<TermEntry<Name, Klass, Role>> getChildren(TermEntry<Name, Klass, Role> entry)
	{
		return getDecoratee().getChildren(entry);
	}

	@Override
	public Collection<TermEntry<Name, Klass, Role>> getChildren(NodeID nodeID, IDLTerm<Name, Klass, Role> term)
	{
		return getDecoratee().getChildren(nodeID, term);
	}

	@Override
	public Collection<TermEntry<Name, Klass, Role>> getChildren(IABoxNode<Name, Klass, Role> node, IDLTerm<Name, Klass, Role> term)
	{
		return getDecoratee().getChildren(node, term);
	}


	@Override
	public Collection<TermEntry<Name, Klass, Role>> getChildren(TermEntry<Name, Klass, Role> entry, boolean recursive)
	{
		return getDecoratee().getChildren(entry, true);
	}


	@Override
	public Collection<TermEntry<Name, Klass, Role>> getChildren(IABoxNode<Name, Klass, Role> node,
																IDLTerm<Name, Klass, Role> term, boolean recursive)
	{
		return getDecoratee().getChildren(node, term, recursive);
	}


	@Override
	public Collection<TermEntry<Name, Klass, Role>> getChildren(NodeID nodeID,
																IDLTerm<Name, Klass, Role> term, boolean recursive)
	{
		return getDecoratee().getChildren(nodeID, term, recursive);
	}
	
	@Override
	public boolean hasChild(TermEntry<Name, Klass, Role> parent, TermEntry<Name, Klass, Role> child)
	{
		return getDecoratee().hasChild(parent, child);
	}

	@Override
	public boolean hasChild(NodeID parentNodeID, IDLTerm<Name, Klass, Role> parentTerm, NodeID childNodeID, IDLTerm<Name, Klass, Role> childTerm)
	{
		return getDecoratee().hasChild(parentNodeID, parentTerm, childNodeID, childTerm);
	}

	@Override
	public boolean hasChild(IABoxNode<Name, Klass, Role> parentNode, IDLTerm<Name, Klass, Role> parentTerm, IABoxNode<Name, Klass, Role> childNode, IDLTerm<Name, Klass, Role> childTerm)
	{
		return getDecoratee().hasChild(parentNode, parentTerm, childNode, childTerm);
	}

	@Override
	public void addParent(NodeID nodeID,
						  IDLTerm<Name, Klass, Role> term,
						  TermEntry<Name, Klass, Role> parentEntry)
	{
		throw new UnsupportedOperationException("Cannot modify an ImmutableDependencyMap");
	}

	@Override
	public void addParent(TermEntry<Name, Klass, Role> termEntry, NodeID parentNode,
						  IDLTerm<Name, Klass, Role> parentTerm)
	{
		throw new UnsupportedOperationException("Cannot modify an ImmutableDependencyMap");
	}

	@Override
	public TermEntryFactory<Name, Klass, Role> getTermEntryFactory()
	{
		return getDecoratee().getTermEntryFactory();
	}

	@Override
	public Collection<TermEntry<Name, Klass, Role>> getGoverningTerms()
	{		
		return Collections.unmodifiableCollection(getDecoratee().getGoverningTerms());
	}

	@Override
	public boolean addGoverningTerm(IABoxNode<Name, Klass, Role> node,
									IDLTerm<Name, Klass, Role> term)
	{
		throw new UnsupportedOperationException("Cannot modify an ImmutableDependencyMap");
	}

	@Override
	public boolean addGoverningTerm(NodeID nodeID,
									IDLTerm<Name, Klass, Role> term)
	{
		throw new UnsupportedOperationException("Cannot modify an ImmutableDependencyMap");
	}

	@Override
	public boolean addGoverningTerm(TermEntry<Name, Klass, Role> termEntry)
	{
		throw new UnsupportedOperationException("Cannot modify an ImmutableDependencyMap");
	}

	@Override
	public Collection<TermEntry<Name, Klass, Role>> getNodeRoots(NodeID node)
	{
		return getDecoratee().getNodeRoots(node);
	}

	@Override
	public Collection<TermEntry<Name, Klass, Role>> getNodeRoots(IABoxNode<Name, Klass, Role> node)
	{
		return getDecoratee().getNodeRoots(node);
	}
}
