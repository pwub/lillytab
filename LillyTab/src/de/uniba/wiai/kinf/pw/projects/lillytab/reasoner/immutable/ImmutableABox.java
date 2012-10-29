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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.immutable;

import de.dhke.projects.cutil.collections.aspect.ICollectionListener;
import de.dhke.projects.cutil.collections.factories.ICollectionFactory;
import de.dhke.projects.cutil.collections.immutable.GenericImmutableSortedSet;
import de.dhke.projects.cutil.collections.immutable.ImmutableMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IDependencyMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.INodeMergeListener;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ITermSetListener;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IUnfoldListener;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntryFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.blocking.IBlockingStateCache;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.ITBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import org.apache.commons.collections15.Transformer;

/**
 * <p> An immutable proxy object of {@link IABox} that forbids changes to the underlying ABox. </p><p> Note that
 * immutable does not it self create a clone or prevent changes to the underlying ABox. That is the responsibility of
 * the user. {@link ImmutableABox} just provides a proxy object trough changes are not possible. </p><p> If an Immutable
 * is first created and the underlying object is changed afterwards, the behaviour of the immutable is undefined. </p>
 *
 * @param <Name> The type for nominals and values
 * @param <Klass> The type for DL classes
 * @param <Role> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ImmutableABox<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	implements IABox<Name, Klass, Role> {

	private final IABox<Name, Klass, Role> _baseABox;
	private final Map<Object, IABoxNode<Name, Klass, Role>> _nodeMap;
	private final Transformer<IABoxNode<Name, Klass, Role>, IABoxNode<Name, Klass, Role>> _nodeTransformer = new Transformer<IABoxNode<Name, Klass, Role>, IABoxNode<Name, Klass, Role>>() {
		@Override
		public IABoxNode<Name, Klass, Role> transform(IABoxNode<Name, Klass, Role> input)
		{
			if (input != null)
				return ImmutableABoxNode.decorate(input, ImmutableABox.this);
			else
				return null;
		}
	};
	private final SortedSet<IABoxNode<Name, Klass, Role>> _nodeSet;


	protected ImmutableABox(final IABox<Name, Klass, Role> baseABox)
	{
		/* the initial ABox is not cloned, but left as is. Make sure, you do not modify it, afterwards (cloning is okay) */
		_baseABox = baseABox;
		_nodeSet = GenericImmutableSortedSet.decorate(baseABox, _nodeTransformer);
		_nodeMap = ImmutableMap.decorate(_baseABox.getNodeMap(), _nodeTransformer);
	}


	protected IABox<Name, Klass, Role> getBaseABox()
	{
		return _baseABox;
	}


	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> ImmutableABox<Name, Klass, Role> decorate(
		final IABox<Name, Klass, Role> baseABox)
	{
		return new ImmutableABox<Name, Klass, Role>(baseABox);
	}


	@Override
	public NodeID getID()
	{
		return _baseABox.getID();
	}


	@Override
	public IABoxNode<Name, Klass, Role> createNode(final boolean isDatatypeNode)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox");
	}


	@Override
	public IABoxNode<Name, Klass, Role> getOrAddNamedNode(final Name individual, final boolean isDatatypeNode)
	{
		final IABoxNode<Name, Klass, Role> node = _baseABox.getNode(individual);
		if (node == null)
			throw new UnsupportedOperationException("Cannot modify ImmutableABox");
		return _nodeTransformer.transform(node);
	}


	@Override
	public Map<Object, IABoxNode<Name, Klass, Role>> getNodeMap()
	{
		return _nodeMap;
	}


	@Override
	public IABoxNode<Name, Klass, Role> getNode(final NodeID id)
	{
		return _nodeTransformer.transform(_baseABox.getNode(id));
	}


	@Override
	public IABoxNode<Name, Klass, Role> getNode(final Name name)
	{
		return _nodeTransformer.transform(_baseABox.getNode(name));
	}


	@Override
	public ITBox<Name, Klass, Role> getTBox()
	{
		return _baseABox.getTBox();
	}


	@Override
	public IABox<Name, Klass, Role> clone()
	{
		/* clone of Immtutables are readable */
		return _baseABox.clone();
	}


	@Override
	public IDLTermFactory<Name, Klass, Role> getDLTermFactory()
	{
		return _baseABox.getDLTermFactory();
	}


	@Override
	public ICollectionFactory<NodeID, Set<NodeID>> getNodeIDSetFactory()
	{
		return _baseABox.getNodeIDSetFactory();
	}


	@Override
	public NodeMergeInfo<Name, Klass, Role> mergeNodes(final IABoxNode<Name, Klass, Role> node1,
													   final IABoxNode<Name, Klass, Role> node2) throws ENodeMergeException
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox.");
	}


	@Override
	public List<ICollectionListener<IABoxNode<Name, Klass, Role>, Collection<IABoxNode<Name, Klass, Role>>>> getNodeSetListeners()
	{
		return Collections.unmodifiableList(_baseABox.getNodeSetListeners());
	}


	@Override
	public List<INodeMergeListener<Name, Klass, Role>> getNodeMergeListeners()
	{
		return Collections.unmodifiableList(_baseABox.getNodeMergeListeners());
	}
	private int _hashCode = 0;


	@Override
	public int deepHashCode()
	{
		if (_hashCode == 0)
			_hashCode = _baseABox.deepHashCode();
		return _hashCode;
	}


	@Override
	public boolean deepEquals(final Object obj)
	{
		return _baseABox.deepEquals(obj);
	}


	@Override
	public Set<Klass> getClassesInSignature()
	{
		return _baseABox.getClassesInSignature();
	}


	@Override
	public Set<Role> getRolesInSignature()
	{
		return _baseABox.getRolesInSignature();
	}


	@Override
	public IBlockingStateCache getBlockingStateCache()
	{
		return _baseABox.getBlockingStateCache();
	}


	public IABox<Name, Klass, Role> getImmutableABox()
	{
		return this;
	}


	@Override
	public Comparator<? super IABoxNode<Name, Klass, Role>> comparator()
	{
		return _baseABox.comparator();
	}


	@Override
	public SortedSet<IABoxNode<Name, Klass, Role>> subSet(final IABoxNode<Name, Klass, Role> fromElement,
														  final IABoxNode<Name, Klass, Role> toElement)
	{
		return _nodeSet.subSet(fromElement, toElement);
	}


	@Override
	public SortedSet<IABoxNode<Name, Klass, Role>> headSet(final IABoxNode<Name, Klass, Role> toElement)
	{
		return _nodeSet.headSet(toElement);
	}


	@Override
	public SortedSet<IABoxNode<Name, Klass, Role>> tailSet(final IABoxNode<Name, Klass, Role> fromElement)
	{
		return _nodeSet.tailSet(fromElement);
	}


	@Override
	public IABoxNode<Name, Klass, Role> first()
	{
		return _nodeSet.first();
	}


	@Override
	public IABoxNode<Name, Klass, Role> last()
	{
		return _nodeSet.last();
	}


	@Override
	public int size()
	{
		return _nodeSet.size();
	}


	@Override
	public boolean isEmpty()
	{
		return _nodeSet.isEmpty();
	}


	@Override
	public boolean contains(final Object o)
	{
		return _baseABox.contains(o);
	}


	@Override
	public Iterator<IABoxNode<Name, Klass, Role>> iterator()
	{
		return _nodeSet.iterator();
	}


	@Override
	public Object[] toArray()
	{

		return _nodeSet.toArray();
	}


	@Override
	public <T> T[] toArray(final T[] a)
	{
		return _nodeSet.toArray(a);
	}


	@Override
	public boolean add(final IABoxNode<Name, Klass, Role> e)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox.");
	}


	@Override
	public boolean remove(final Object o)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox.");
	}


	@Override
	public boolean containsAll(final Collection<?> c)
	{
		return _baseABox.containsAll(c);
	}


	@Override
	public boolean addAll(final Collection<? extends IABoxNode<Name, Klass, Role>> c)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox.");
	}


	@Override
	public boolean retainAll(final Collection<?> c)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox.");
	}


	@Override
	public boolean removeAll(final Collection<?> c)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox.");
	}


	@Override
	public void clear()
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox.");
	}


	@Override
	public IABox<Name, Klass, Role> getImmutable()
	{
		return this;
	}
//
//	@Override
//	public List<IUnfoldListener<Name, Klass, Role>> getUnfoldListeners()
//	{
//		return Collections.unmodifiableList(_baseABox.getUnfoldListeners());
//	}


	@Override
	public List<ITermSetListener<Name, Klass, Role>> getTermSetListeners()
	{
		return Collections.unmodifiableList(_baseABox.getTermSetListeners());
	}


	@Override
	public IDependencyMap<Name, Klass, Role> getDependencyMap()
	{
		return ImmutableDependencyMap.decorate(_baseABox.getDependencyMap());
	}


	@Override
	public String toString()
	{
		return _baseABox.toString();
	}


	@Override
	public String toString(final String prefix)
	{
		return _baseABox.toString(prefix);
	}


	@Override
	public TermEntryFactory<Name, Klass, Role> getTermEntryFactory()
	{
		return _baseABox.getTermEntryFactory();
	}


	@Override
	public boolean canMerge(final IABoxNode<Name, Klass, Role> node1,
							final IABoxNode<Name, Klass, Role> node2)
	{
		return _baseABox.canMerge(node1, node2);
	}


	@Override
	public boolean containsAllTermEntries(Collection<TermEntry<Name, Klass, Role>> entries)
	{
		return _baseABox.containsAllTermEntries(entries);
	}


	@Override
	public boolean containsTermEntry(TermEntry<Name, Klass, Role> entry)
	{
		return _baseABox.containsTermEntry(entry);
	}


	@Override
	public void unfoldAll() throws ENodeMergeException
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox.");
	}
}
