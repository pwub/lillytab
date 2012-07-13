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

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IDependencyMap;
import de.dhke.projects.cutil.collections.aspect.ICollectionListener;
import de.dhke.projects.cutil.collections.factories.ICollectionFactory;
import de.dhke.projects.cutil.collections.factories.IMultiMapFactory;
import de.dhke.projects.cutil.collections.immutable.ImmutableMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IBlockingStateCache;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.INodeMergeListener;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ITBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ITermSetListener;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IUnfoldListener;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntryFactory;
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
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.Transformer;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ImmutableABox<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	implements IABox<Name, Klass, Role>
{
	private final IABox<Name, Klass, Role> _baseABox;
	private final Map<Object, IABoxNode<Name, Klass, Role>> _nodeMap;
	private final Transformer<IABoxNode<Name, Klass, Role>, IABoxNode<Name, Klass, Role>> _nodeTransformer
		= new Transformer<IABoxNode<Name, Klass, Role>, IABoxNode<Name, Klass, Role>>() {

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
		/* create a clone of the initial ABox */
		_baseABox = baseABox.clone();
		_nodeSet = Collections.unmodifiableSortedSet(_baseABox);
		_nodeMap = ImmutableMap.decorate(_baseABox.getNodeMap(), _nodeTransformer);
	}

	protected IABox<Name, Klass, Role> getBaseABox()
	{
		return _baseABox;
	}

	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
		ImmutableABox<Name, Klass, Role> decorate(final IABox<Name, Klass, Role> baseABox)
	{
		return new ImmutableABox<Name, Klass, Role>(baseABox);
	}

	public NodeID getID()
	{
		return _baseABox.getID();
	}

	public IABoxNode<Name, Klass, Role> createNode(boolean isDatatypeNode)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox");
	}

	public IABoxNode<Name, Klass, Role> getOrAddNamedNode(Name individual, boolean isDatatypeNode)
	{
		final IABoxNode<Name, Klass, Role> node = _baseABox.getNode(individual);
		if (node == null)
			throw new UnsupportedOperationException("Cannot modify ImmutableABox");
		return node;
	}

	public Map<Object, IABoxNode<Name, Klass, Role>> getNodeMap()
	{
		return _nodeMap;
	}

	public IABoxNode<Name, Klass, Role> getNode(NodeID id)
	{
		return _nodeTransformer.transform(_baseABox.getNode(id));
	}

	public IABoxNode<Name, Klass, Role> getNode(Name name)
	{
		return _nodeTransformer.transform(_baseABox.getNode(name));
	}

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

	public IDLTermFactory<Name, Klass, Role> getDLTermFactory()
	{
		return _baseABox.getDLTermFactory();
	}

	public ICollectionFactory<IDLTerm<Name, Klass, Role>, SortedSet<IDLTerm<Name, Klass, Role>>> getTermSetFactory()
	{
		return _baseABox.getTermSetFactory();
	}

	public IMultiMapFactory<Role, NodeID, MultiMap<Role, NodeID>> getLinkMapFactory()
	{
		return _baseABox.getLinkMapFactory();
	}

	public ICollectionFactory<NodeID, Set<NodeID>> getNodeIDSetFactory()
	{
		return _baseABox.getNodeIDSetFactory();
	}

	public NodeMergeInfo<Name, Klass, Role> mergeNodes(IABoxNode<Name, Klass, Role> node1,
													   IABoxNode<Name, Klass, Role> node2) throws ENodeMergeException
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox.");
	}

	public List<ICollectionListener<IABoxNode<Name, Klass, Role>, Collection<IABoxNode<Name, Klass, Role>>>> getNodeSetListeners()
	{
		return Collections.unmodifiableList(_baseABox.getNodeSetListeners());
	}

	public List<INodeMergeListener<Name, Klass, Role>> getNodeMergeListeners()
	{
		return Collections.unmodifiableList(_baseABox.getNodeMergeListeners());
	}

	private int _hashCode = 0;
	public int deepHashCode()
	{
		if (_hashCode == 0)
			_hashCode = _baseABox.deepHashCode();
		return _hashCode;
	}

	public boolean deepEquals(Object obj)
	{
		return _baseABox.deepEquals(obj);
	}

	public Set<Klass> getClassesInSignature()
	{
		return _baseABox.getClassesInSignature();
	}

	public Set<Role> getRolesInSignature()
	{
		return _baseABox.getRolesInSignature();
	}

	public IBlockingStateCache getBlockingStateCache()
	{
		return _baseABox.getBlockingStateCache();
	}

	public IABox<Name, Klass, Role> getImmutableABox()
	{
		return this;
	}

	public Comparator<? super IABoxNode<Name, Klass, Role>> comparator()
	{
		return _baseABox.comparator();
	}

	public SortedSet<IABoxNode<Name, Klass, Role>> subSet(IABoxNode<Name, Klass, Role> fromElement,
														  IABoxNode<Name, Klass, Role> toElement)
	{
		return _nodeSet.subSet(fromElement, toElement);
	}

	public SortedSet<IABoxNode<Name, Klass, Role>> headSet(IABoxNode<Name, Klass, Role> toElement)
	{
		return _nodeSet.headSet(toElement);
	}

	public SortedSet<IABoxNode<Name, Klass, Role>> tailSet(IABoxNode<Name, Klass, Role> fromElement)
	{
		return _nodeSet.tailSet(fromElement);
	}

	public IABoxNode<Name, Klass, Role> first()
	{
		return _nodeSet.first();
	}

	public IABoxNode<Name, Klass, Role> last()
	{
		return _nodeSet.last();
	}

	public int size()
	{
		return _nodeSet.size();
	}

	public boolean isEmpty()
	{
		return _nodeSet.isEmpty();
	}

	public boolean contains(Object o)
	{
		return _baseABox.contains(o);
	}

	public Iterator<IABoxNode<Name, Klass, Role>> iterator()
	{
		return _nodeSet.iterator();
	}

	public Object[] toArray()
	{
		return _baseABox.toArray();
	}

	public <T> T[] toArray(T[] a)
	{
		return _baseABox.toArray(a);
	}

	public boolean add(IABoxNode<Name, Klass, Role> e)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox.");
	}

	public boolean remove(Object o)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox.");
	}

	public boolean containsAll(Collection<?> c)
	{
		return _baseABox.containsAll(c);
	}

	public boolean addAll(Collection<? extends IABoxNode<Name, Klass, Role>> c)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox.");
	}

	public boolean retainAll(Collection<?> c)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox.");
	}

	public boolean removeAll(Collection<?> c)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox.");
	}

	public void clear()
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox.");
	}

	public IABox<Name, Klass, Role> getImmutable()
	{
		return this;
	}

	public List<IUnfoldListener<Name, Klass, Role>> getUnfoldListeners()
	{
		return Collections.unmodifiableList(_baseABox.getUnfoldListeners());
	}

	public List<ITermSetListener<Name, Klass, Role>> getTermSetListeners()
	{
		return Collections.unmodifiableList(_baseABox.getTermSetListeners());
	}

	public IDependencyMap<Name, Klass, Role> getDependencyMap()
	{
		return ImmutableDependencyMap.decorate(_baseABox.getDependencyMap());
	}


	@Override
	public String toString()
	{
		return _baseABox.toString();
	}

	public String toString(String prefix)
	{
		return _baseABox.toString(prefix);
	}

	public TermEntryFactory<Name, Klass, Role> getTermEntryFactory()
	{
		return _baseABox.getTermEntryFactory();
	}

	public boolean canMerge(IABoxNode<Name, Klass, Role> node1,
							IABoxNode<Name, Klass, Role> node2)
	{
		return _baseABox.canMerge(node1, node2);
	}
}
