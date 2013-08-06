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

import de.dhke.projects.cutil.collections.aspect.ICollectionListener;
import de.dhke.projects.cutil.collections.factories.ICollectionFactory;
import de.dhke.projects.cutil.collections.immutable.GenericImmutableSet;
import de.dhke.projects.cutil.collections.immutable.ImmutableMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IDatatypeABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IDependencyMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IIndividualABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.INodeMergeListener;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ITermSetListener;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntryFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.blocking.IBlockingStateCache;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IAssertedRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.ITBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import org.apache.commons.collections15.Transformer;


/**
 * An immutable proxy object of {@link IABox} that forbids changes to the underlying ABox. <p /> Note that immutable
 * does not it self create a clone or prevent changes to the underlying ABox. That is the responsibility of the user.
 * {@link ImmutableABox} just provides a proxy object trough changes are not possible. <p /> If an Immutable is first
 * created and the underlying object is changed afterwards, the behaviour of the immutable is undefined.
 *
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ImmutableABox<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	implements IABox<I, L, K, R>
{
	public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> ImmutableABox<I, L, K, R> decorate(
		final IABox<I, L, K, R> baseABox)
	{
		return new ImmutableABox<>(baseABox);
	}
	private final IABox<I, L, K, R> _baseABox;
	private final ITBox<I, L, K, R> _tbox;
	private final Map<Object, IABoxNode<I, L, K, R>> _nodeMap;
	private final Transformer<IABoxNode<I, L, K, R>, IABoxNode<I, L, K, R>> _nodeTransformer = new Transformer<IABoxNode<I, L, K, R>, IABoxNode<I, L, K, R>>()
	{
		@Override
		public IABoxNode<I, L, K, R> transform(IABoxNode<I, L, K, R> input)
		{
			if (input != null) {
				if (input instanceof IIndividualABoxNode)
					return ImmutableIndividualABoxNode.decorate((IIndividualABoxNode<I, L, K, R>) input,
																ImmutableABox.this);
				else if (input instanceof IDatatypeABoxNode)
					return ImmutableLiteralABoxNode.decorate((IDatatypeABoxNode<I, L, K, R>) input, ImmutableABox.this);
				else
					throw new UnsupportedOperationException("Unknown ABox Node type: " + input.getClass());
			} else {
				return null;
			}
		}
	};
	private final Set<IABoxNode<I, L, K, R>> _nodeSet;
	private int _hashCode = 0;

	protected ImmutableABox(final IABox<I, L, K, R> baseABox)
	{
		/* the initial ABox is not cloned, but left as is. Make sure, you do not modify it, afterwards (cloning is okay) */
		_baseABox = baseABox;
		_tbox = baseABox.getTBox().getImmutable();
		_nodeSet = GenericImmutableSet.decorate(baseABox, _nodeTransformer);
		_nodeMap = ImmutableMap.decorate(_baseABox.getNodeMap(), _nodeTransformer);
	}

	protected IABox<I, L, K, R> getBaseABox()
	{
		return _baseABox;
	}

	@Override
	public NodeID getID()
	{
		return _baseABox.getID();
	}

	@Override
	public IABoxNode<I, L, K, R> createNode(final boolean isDatatypeNode)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox");
	}

	@Override
	public IIndividualABoxNode<I, L, K, R> getOrAddIndividualNode(I individual)
		throws ENodeMergeException
	{
		final IIndividualABoxNode<I, L, K, R> node = _baseABox.getIndividualNode(individual);
		if (node == null) {
			throw new UnsupportedOperationException("Cannot modify ImmutableABox");
		}
		return (IIndividualABoxNode<I, L, K, R>) _nodeTransformer.transform(node);
	}

	@Override
	public IDatatypeABoxNode<I, L, K, R> getOrAddDatatypeNode(L literal)
		throws ENodeMergeException
	{
		final IABoxNode<I, L, K, R> node = _baseABox.getDatatypeNode(literal);
		if (node == null) {
			throw new UnsupportedOperationException("Cannot modify ImmutableABox");
		}
		return (IDatatypeABoxNode<I, L, K, R>) _nodeTransformer.transform(node);
	}

	@Override
	public IIndividualABoxNode<I, L, K, R> getIndividualNode(I individual)
	{
		return _baseABox.getIndividualNode(individual);
	}

	@Override
	public IDatatypeABoxNode<I, L, K, R> getDatatypeNode(L literal)
	{
		return _baseABox.getDatatypeNode(literal);
	}

	@Override
	public Map<Object, IABoxNode<I, L, K, R>> getNodeMap()
	{
		return Collections.unmodifiableMap(_nodeMap);
	}

	@Override
	public IABoxNode<I, L, K, R> getNode(final NodeID id)
	{
		return _nodeTransformer.transform(_baseABox.getNode(id));
	}

	@Override
	public ITBox<I, L, K, R> getTBox()
	{
		return _tbox;
	}

	@Override
	public IRBox<I, L, K, R> getRBox()
	{
		return _tbox.getRBox();
	}

	@Override
	public IAssertedRBox<I, L, K, R> getAssertedRBox()
	{
		return _tbox.getAssertedRBox();
	}

	@Override
	public IABox<I, L, K, R> clone()
	{
		/* clone of Immtutables are readable */
		return _baseABox.clone();
	}

	@Override
	public IDLTermFactory<I, L, K, R> getDLTermFactory()
	{
		return _baseABox.getDLTermFactory();
	}

	@Override
	public ICollectionFactory<NodeID, Set<NodeID>> getNodeIDSetFactory()
	{
		return _baseABox.getNodeIDSetFactory();
	}

	@Override
	public NodeMergeInfo<I, L, K, R> mergeNodes(final IABoxNode<I, L, K, R> node1, final IABoxNode<I, L, K, R> node2)
		throws ENodeMergeException
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox.");
	}

	@Override
	public List<ICollectionListener<IABoxNode<I, L, K, R>, Collection<IABoxNode<I, L, K, R>>>> getNodeSetListeners()
	{
		return Collections.unmodifiableList(_baseABox.getNodeSetListeners());
	}

	@Override
	public List<INodeMergeListener<I, L, K, R>> getNodeMergeListeners()
	{
		return Collections.unmodifiableList(_baseABox.getNodeMergeListeners());
	}

	@Override
	public int deepHashCode()
	{
		if (_hashCode == 0) {
			_hashCode = _baseABox.deepHashCode();
		}
		return _hashCode;
	}

	@Override
	public boolean deepEquals(final Object obj)
	{
		return _baseABox.deepEquals(obj);
	}

	@Override
	public Set<K> getClassesInSignature()
	{
		return _baseABox.getClassesInSignature();
	}

	@Override
	public Set<R> getRolesInSignature()
	{
		return _baseABox.getRolesInSignature();
	}

	@Override
	public IBlockingStateCache getBlockingStateCache()
	{
		return _baseABox.getBlockingStateCache();
	}

	public IABox<I, L, K, R> getImmutableABox()
	{
		return this;
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
	public Iterator<IABoxNode<I, L, K, R>> iterator()
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
	public boolean add(final IABoxNode<I, L, K, R> e)
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
	public boolean addAll(final Collection<? extends IABoxNode<I, L, K, R>> c)
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
	public IABox<I, L, K, R> getImmutable()
	{
		return this;
	}
	//

	@Override
	public List<ITermSetListener<I, L, K, R>> getTermSetListeners()
	{
		return Collections.unmodifiableList(_baseABox.getTermSetListeners());
	}

	@Override
	public IDependencyMap<I, L, K, R> getDependencyMap()
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
	public TermEntryFactory<I, L, K, R> getTermEntryFactory()
	{
		return _baseABox.getTermEntryFactory();
	}

	@Override
	public boolean canMerge(final IABoxNode<I, L, K, R> node1, final IABoxNode<I, L, K, R> node2)
	{
		return _baseABox.canMerge(node1, node2);
	}

	@Override
	public boolean containsAllTermEntries(Collection<TermEntry<I, L, K, R>> entries)
	{
		return _baseABox.containsAllTermEntries(entries);
	}

	@Override
	public boolean containsTermEntry(TermEntry<I, L, K, R> entry)
	{
		return _baseABox.containsTermEntry(entry);
	}

	@Override
	public IDatatypeABoxNode<I, L, K, R> createDatatypeNode()
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox.");
	}

	@Override
	public IIndividualABoxNode<I, L, K, R> createIndividualNode()
		throws ENodeMergeException
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox.");
	}

	@Override
	public boolean removeFromQueues(
		Collection<NodeID> nodeIDs)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox.");
	}

	@Override
	public boolean removeFromQueues(NodeID nodeID)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox.");
	}

	@Override
	public boolean removeNodeFromQueues(
		IABoxNode<I, L, K, R> node)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox.");
	}

	@Override
	public boolean touch(NodeID nodeID)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox.");
	}

	@Override
	public boolean touchAll(
		Collection<NodeID> individuals)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox.");
	}

	@Override
	public boolean touchLiteral(L literal)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox.");
	}

	@Override
	public boolean touchIndividual(I individual)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox.");
	}

	@Override
	public boolean touchNode(
		IABoxNode<I, L, K, R> node)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox.");
	}

	@Override
	public boolean touchNodes(
		Collection<? extends IABoxNode<I, L, K, R>> nodes)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox.");
	}

	@Override
	public boolean removeNodesFromQueues(
		Collection<? extends IABoxNode<I, L, K, R>> nodes)
	{
		throw new UnsupportedOperationException("Cannot modify ImmutableABox.");
	}

	@Override
	public IABoxNode<I, L, K, R> nextGeneratingNode()
	{
		return _baseABox.nextGeneratingNode();
	}

	@Override
	public IABoxNode<I, L, K, R> nextNonGeneratingNode()
	{
		return _baseABox.nextNonGeneratingNode();
	}

	@Override
	public boolean hasMoreGeneratingNodes()
	{
		return _baseABox.hasMoreGeneratingNodes();
	}

	@Override
	public boolean hasMoreNonGeneratingNodes()
	{
		return _baseABox.hasMoreNonGeneratingNodes();
	}

	@Override
	public SortedSet<NodeID> getNonGeneratingQueue()
	{
		return _baseABox.getNonGeneratingQueue();
	}

	@Override
	public SortedSet<NodeID> getGeneratingQueue()
	{
		return _baseABox.getGeneratingQueue();
	}
}
