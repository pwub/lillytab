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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.immutable;

import de.dhke.projects.cutil.IDecorator;
import de.dhke.projects.cutil.Pair;
import de.dhke.projects.cutil.collections.immutable.GenericImmutableCollection;
import de.dhke.projects.cutil.collections.immutable.ImmutableTransformer;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ILinkMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IRABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import java.util.Collection;

/**
 * * A proxy object to an {@link IRABox} that forbids changes to the underlying map. <p /> If an immutable is first
 * created and the underlying map is modified, afterwards, behaviour of the immutable is undefined.
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ImmutableRABox<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	implements IRABox<I, L, K, R>, IDecorator<IRABox<I, L, K, R>> {
	private final IABoxNode<I, L, K, R> _immutableNode;
	private final IRABox<I, L, K, R> _baseLinkMap;
	private final ImmutableTransformer<IABoxNode<I, L, K, R>> _immutableNodeTransformer = new ImmutableTransformer<>();


	public ImmutableRABox(
		final IRABox<I, L, K, R> baseLinkMap)
	{
		_immutableNode = baseLinkMap.getNode().getImmutable();
		_baseLinkMap = baseLinkMap;
	}


	protected ImmutableRABox(
		final IABoxNode<I, L, K, R> immutableNode,
		final IRABox<I, L, K, R> baseLinkMap)
	{
		_immutableNode = immutableNode;
		_baseLinkMap = baseLinkMap;
	}


		public static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> ImmutableRABox<I, L, K, R> decorate(final IRABox<I, L, K, R> baseLinkMap)
	{
		return new ImmutableRABox<>(baseLinkMap);
	}


	@Override
	public IABoxNode<I, L, K, R> getNode()
	{
		return _immutableNode;
	}


	@Override
	public ILinkMap<I, L, K, R> getAssertedSuccessors()
	{
		return ImmutableLinkMap.decorate(_baseLinkMap.getAssertedSuccessors());
	}


	@Override
	public ILinkMap<I, L, K, R> getAssertedPredecessors()
	{
		return ImmutableLinkMap.decorate(_baseLinkMap.getAssertedSuccessors());
	}


	@Override
	public boolean hasSuccessor(final R role, final NodeID successor)
	{
		return _baseLinkMap.hasSuccessor(role, successor);
	}


	@Override
	public boolean hasSuccessor(final R role)
	{
		return _baseLinkMap.hasSuccessor(role);
	}


	@Override
	public boolean hasSuccessor(final R role, final IABoxNode<I, L, K, R> successor)
	{
		return _baseLinkMap.hasSuccessor(role, successor);
	}


	@Override
	public boolean hasPredecessor(final R role, final NodeID predecessor)
	{
		return _baseLinkMap.hasPredecessor(role, predecessor);
	}


	@Override
	public boolean hasPredecessor(final R role)
	{
		return _baseLinkMap.hasPredecessor(role);
	}


	@Override
	public boolean hasPredecessor(final R role, final IABoxNode<I, L, K, R> predecessor)
	{
		return _baseLinkMap.hasPredecessor(role, predecessor);
	}


	@Override
	public Collection<R> getOutgoingRoles()
	{
		return _baseLinkMap.getOutgoingRoles();
	}


	@Override
	public Collection<R> getIncomingRoles()
	{
		return _baseLinkMap.getIncomingRoles();
	}


	@Override
	public Collection<NodeID> getSuccessors(final R role)
	{
		return _baseLinkMap.getSuccessors();
	}


	@Override
	public Collection<NodeID> getSuccessors()
	{
		return _baseLinkMap.getSuccessors();
	}


	@Override
	public Collection<NodeID> getPredecessors(final R role)
	{
		return _baseLinkMap.getPredecessors(role);
	}


	@Override
	public Collection<NodeID> getPredecessors()
	{
		return _baseLinkMap.getPredecessors();
	}


	@Override
	public Iterable<Pair<R, NodeID>> getPredecessorPairs()
	{
		return _baseLinkMap.getPredecessorPairs();
	}


	@Override
	public Iterable<Pair<R, NodeID>> getSuccessorPairs()
	{
		return _baseLinkMap.getSuccessorPairs();
	}


	@Override
	public Collection<IABoxNode<I, L, K, R>> getSuccessorNodes(final R role)
	{
		return GenericImmutableCollection.decorate(_baseLinkMap.getSuccessorNodes(role), _immutableNodeTransformer);
	}


	@Override
	public Collection<IABoxNode<I, L, K, R>> getSuccessorNodes()
	{
		return GenericImmutableCollection.decorate(_baseLinkMap.getSuccessorNodes(), _immutableNodeTransformer);
	}


	@Override
	public Collection<IABoxNode<I, L, K, R>> getPredecessorNodes(final R role)
	{
		return GenericImmutableCollection.decorate(_baseLinkMap.getPredecessorNodes(role), _immutableNodeTransformer);
	}


	@Override
	public Collection<IABoxNode<I, L, K, R>> getPredecessorNodes()
	{
		return GenericImmutableCollection.decorate(_baseLinkMap.getPredecessorNodes(), _immutableNodeTransformer);
	}


	@Override
	public IRABox<I, L, K, R> clone(final IABoxNode<I, L, K, R> newNode)
	{
		return _baseLinkMap.clone(newNode);
	}


	@Override
	public boolean deepEquals(final Object obj)
	{
		return _baseLinkMap.deepEquals(obj);
	}


	@Override
	public int deepHashCode()
	{
		return _baseLinkMap.deepHashCode();
	}


	@Override
	public IRABox<I, L, K, R> getImmutable()
	{
		return this;
	}


	@Override
	public IRABox<I, L, K, R> getDecoratee()
	{
		return _baseLinkMap;
	}
}
