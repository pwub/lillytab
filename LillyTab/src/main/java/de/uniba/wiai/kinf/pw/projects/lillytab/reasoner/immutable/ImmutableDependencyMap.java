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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.immutable;

import de.dhke.projects.cutil.collections.immutable.ImmutableMultiMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IDependencyMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntryFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import java.util.Collection;
import java.util.Collections;


/**
 * * 
 * A proxy object to an {@link IDependencyMap} that forbids changes to the underlying map.
 * <p />
 * If an immutable is first created and the underlying map is modified, afterwards, behaviour of the immutable is
 * undefined.
 * 
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ImmutableDependencyMap<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends ImmutableMultiMap<TermEntry<I, L, K, R>, TermEntry<I, L, K, R>>
	implements IDependencyMap<I, L, K, R>
{
	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> ImmutableDependencyMap<I, L, K, R> decorate(
		final IDependencyMap<I, L, K, R> baseMap)
	{
		return new ImmutableDependencyMap<>(baseMap);
	}

	ImmutableDependencyMap(
		final IDependencyMap<I, L, K, R> decoratee)
	{
		super(decoratee, null, null);
	}

	@Override
	public void addParent(IABoxNode<I, L, K, R> node, IDLTerm<I, L, K, R> term,
						  IABoxNode<I, L, K, R> parentNode, IDLTerm<I, L, K, R> parentTerm)
	{
		throw new UnsupportedOperationException("Cannot modify an ImmutableDependencyMap");
	}

	@Override
	public void addParent(TermEntry<I, L, K, R> termEntry, TermEntry<I, L, K, R> parentEntry)
	{
		throw new UnsupportedOperationException("Cannot modify an ImmutableDependencyMap");
	}

	@Override
	public Collection<TermEntry<I, L, K, R>> getParents(TermEntry<I, L, K, R> entry)
	{
		return getDecoratee().getParents(entry);
	}

	@Override
	public IDependencyMap<I, L, K, R> getDecoratee()
	{
		return (IDependencyMap<I, L, K, R>) super.getDecoratee();
	}

	@Override
	public Collection<TermEntry<I, L, K, R>> getParents(NodeID nodeID, IDLTerm<I, L, K, R> term)
	{
		return getDecoratee().getParents(nodeID, term);
	}

	@Override
	public Collection<TermEntry<I, L, K, R>> getParents(IABoxNode<I, L, K, R> node,
														IDLTerm<I, L, K, R> term)
	{
		return getDecoratee().getParents(node, term);
	}

	@Override
	public Collection<TermEntry<I, L, K, R>> getParents(TermEntry<I, L, K, R> entry, boolean recursive)
	{
		return getDecoratee().getParents(entry, recursive);
	}

	@Override
	public Collection<TermEntry<I, L, K, R>> getParents(IABoxNode<I, L, K, R> node,
														IDLTerm<I, L, K, R> term, boolean recursive)
	{
		return getDecoratee().getParents(node, term, recursive);
	}

	@Override
	public Collection<TermEntry<I, L, K, R>> getParents(NodeID nodeID,
														IDLTerm<I, L, K, R> term, boolean recursive)
	{
		return getDecoratee().getParents(nodeID, term, recursive);
	}

	@Override
	public boolean containsKey(IABoxNode<I, L, K, R> node, IDLTerm<I, L, K, R> term)
	{
		return getDecoratee().containsKey(node, term);
	}

	public boolean containsKey(TermEntry<I, L, K, R> termEntry)
	{
		return getDecoratee().containsKey(termEntry);
	}

	@Override
	public IDependencyMap<I, L, K, R> getImmutable()
	{
		return this;
	}

	@Override
	public IDependencyMap<I, L, K, R> clone()
	{
		return getDecoratee().clone();
	}

	@Override
	public void addParent(NodeID nodeID,
						  IDLTerm<I, L, K, R> term, NodeID parentNodeID,
						  IDLTerm<I, L, K, R> parentTerm)
	{
		throw new UnsupportedOperationException("Cannot modify an ImmutableDependencyMap");
	}

	@Override
	public boolean containsValue(IABoxNode<I, L, K, R> node,
								 IDLTerm<I, L, K, R> term,
								 IABoxNode<I, L, K, R> parentNode,
								 IDLTerm<I, L, K, R> parentTerm)
	{
		return getDecoratee().containsValue(node, term, parentNode, parentTerm);
	}

	@Override
	public boolean containsValue(NodeID nodeID,
								 IDLTerm<I, L, K, R> term, NodeID parentNodeID,
								 IDLTerm<I, L, K, R> parentTerm)
	{
		return getDecoratee().containsValue(nodeID, term, parentNodeID, parentTerm);
	}

	@Override
	public Collection<TermEntry<I, L, K, R>> getChildren(TermEntry<I, L, K, R> entry)
	{
		return getDecoratee().getChildren(entry);
	}

	@Override
	public Collection<TermEntry<I, L, K, R>> getChildren(NodeID nodeID, IDLTerm<I, L, K, R> term)
	{
		return getDecoratee().getChildren(nodeID, term);
	}

	@Override
	public Collection<TermEntry<I, L, K, R>> getChildren(IABoxNode<I, L, K, R> node,
														 IDLTerm<I, L, K, R> term)
	{
		return getDecoratee().getChildren(node, term);
	}

	@Override
	public Collection<TermEntry<I, L, K, R>> getChildren(TermEntry<I, L, K, R> entry, boolean recursive)
	{
		return getDecoratee().getChildren(entry, true);
	}

	@Override
	public Collection<TermEntry<I, L, K, R>> getChildren(IABoxNode<I, L, K, R> node,
														 IDLTerm<I, L, K, R> term, boolean recursive)
	{
		return getDecoratee().getChildren(node, term, recursive);
	}

	@Override
	public Collection<TermEntry<I, L, K, R>> getChildren(NodeID nodeID,
														 IDLTerm<I, L, K, R> term, boolean recursive)
	{
		return getDecoratee().getChildren(nodeID, term, recursive);
	}

	@Override
	public boolean hasChild(TermEntry<I, L, K, R> parent, TermEntry<I, L, K, R> child)
	{
		return getDecoratee().hasChild(parent, child);
	}

	@Override
	public boolean hasChild(NodeID parentNodeID, IDLTerm<I, L, K, R> parentTerm, NodeID childNodeID,
							IDLTerm<I, L, K, R> childTerm)
	{
		return getDecoratee().hasChild(parentNodeID, parentTerm, childNodeID, childTerm);
	}

	@Override
	public boolean hasChild(IABoxNode<I, L, K, R> parentNode, IDLTerm<I, L, K, R> parentTerm,
							IABoxNode<I, L, K, R> childNode, IDLTerm<I, L, K, R> childTerm)
	{
		return getDecoratee().hasChild(parentNode, parentTerm, childNode, childTerm);
	}

	@Override
	public void addParent(NodeID nodeID,
						  IDLTerm<I, L, K, R> term,
						  TermEntry<I, L, K, R> parentEntry)
	{
		throw new UnsupportedOperationException("Cannot modify an ImmutableDependencyMap");
	}

	@Override
	public void addParent(TermEntry<I, L, K, R> termEntry, NodeID parentNode,
						  IDLTerm<I, L, K, R> parentTerm)
	{
		throw new UnsupportedOperationException("Cannot modify an ImmutableDependencyMap");
	}

	@Override
	public TermEntryFactory<I, L, K, R> getTermEntryFactory()
	{
		return getDecoratee().getTermEntryFactory();
	}

	@Override
	public Collection<TermEntry<I, L, K, R>> getGoverningTerms()
	{
		return Collections.unmodifiableCollection(getDecoratee().getGoverningTerms());
	}

	@Override
	public boolean addGoverningTerm(IABoxNode<I, L, K, R> node,
									IDLTerm<I, L, K, R> term)
	{
		throw new UnsupportedOperationException("Cannot modify an ImmutableDependencyMap");
	}

	@Override
	public boolean addGoverningTerm(NodeID nodeID,
									IDLTerm<I, L, K, R> term)
	{
		throw new UnsupportedOperationException("Cannot modify an ImmutableDependencyMap");
	}

	@Override
	public boolean addGoverningTerm(TermEntry<I, L, K, R> termEntry)
	{
		throw new UnsupportedOperationException("Cannot modify an ImmutableDependencyMap");
	}

	@Override
	public Collection<TermEntry<I, L, K, R>> getNodeRoots(NodeID node)
	{
		return getDecoratee().getNodeRoots(node);
	}

	@Override
	public Collection<TermEntry<I, L, K, R>> getNodeRoots(IABoxNode<I, L, K, R> node)
	{
		return getDecoratee().getNodeRoots(node);
	}

	@Override
	public boolean hasGoverningTerm(
		IABoxNode<I, L, K, R> node,
		IDLTerm<I, L, K, R> term)
	{
		return getDecoratee().hasGoverningTerm(node, term);
	}

	@Override
	public boolean hasGoverningTerm(
		TermEntry<I, L, K, R> termEntry)
	{
		return getDecoratee().hasGoverningTerm(termEntry);
	}

	@Override
	public boolean hasGoverningTerm(NodeID nodeID,
									IDLTerm<I, L, K, R> term)
	{
		return getDecoratee().hasGoverningTerm(nodeID, term);
	}

	@Override
	public String toString()
	{
		return getDecoratee().toString();
	}
}
