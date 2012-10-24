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

import de.dhke.projects.cutil.IDecorator;
import de.dhke.projects.cutil.Pair;
import de.dhke.projects.cutil.collections.immutable.GenericImmutableIterable;
import de.dhke.projects.cutil.collections.immutable.ImmutableMultiMap;
import de.dhke.projects.cutil.collections.immutable.ImmutableTransformer;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ILinkMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IRABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import java.util.Collection;
import java.util.Set;


/**
 * * <p> A proxy object to an {@link IRABox} that forbids changes to the underlying map. </p><p> If an immutable is
 * first created and the underlying map is modified, afterwards, behaviour of the immutable is undefined. </p>
 *
 * @param <Name> The type for nominals and values
 * @param <Klass> The type for DL classes
 * @param <Role> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ImmutableRABox<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	implements IRABox<Name, Klass, Role>, IDecorator<IRABox<Name, Klass, Role>>
{
	private final IABoxNode<Name, Klass, Role> _immutableNode;
	private final IRABox<Name, Klass, Role> _baseLinkMap;
	private final ImmutableTransformer<IABoxNode<Name, Klass, Role>> _immutableNodeTransformer = new ImmutableTransformer<IABoxNode<Name, Klass, Role>>();

	public ImmutableRABox(
		final IRABox<Name, Klass, Role> baseLinkMap)
	{
		_immutableNode = baseLinkMap.getNode().getImmutable();
		_baseLinkMap = baseLinkMap;
	}

	protected ImmutableRABox(
		final IABoxNode<Name, Klass, Role> immutableNode,
		final IRABox<Name, Klass, Role> baseLinkMap)
	{
		_immutableNode = immutableNode;
		_baseLinkMap = baseLinkMap;
	}

	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> ImmutableRABox<Name, Klass, Role> decorate(
		final IRABox<Name, Klass, Role> baseLinkMap)
	{
		return new ImmutableRABox<Name, Klass, Role>(baseLinkMap);
	}

	@Override
	public IABoxNode<Name, Klass, Role> getNode()
	{
		return _immutableNode;
	}

	@Override
	public ILinkMap<Name, Klass, Role> getAssertedSuccessors()
	{
		return ImmutableLinkMap.decorate(_baseLinkMap.getAssertedSuccessors());
	}

	@Override
	public ILinkMap<Name, Klass, Role> getAssertedPredecessors()
	{
		return ImmutableLinkMap.decorate(_baseLinkMap.getAssertedSuccessors());
	}

	@Override
	public boolean hasSuccessor(final Role role, final NodeID successor)
	{
		return _baseLinkMap.hasSuccessor(role, successor);
	}

	@Override
	public boolean hasSuccessor(final Role role)
	{
		return _baseLinkMap.hasSuccessor(role);
	}

	@Override
	public boolean hasSuccessor(final Role role,
								final IABoxNode<Name, Klass, Role> successor)
	{
		return _baseLinkMap.hasSuccessor(role, successor);
	}

	@Override
	public boolean hasPredecessor(final Role role, final NodeID predecessor)
	{
		return _baseLinkMap.hasPredecessor(role, predecessor);
	}

	@Override
	public boolean hasPredecessor(final Role role)
	{
		return _baseLinkMap.hasPredecessor(role);
	}

	@Override
	public boolean hasPredecessor(final Role role,
								  final IABoxNode<Name, Klass, Role> predecessor)
	{
		return _baseLinkMap.hasPredecessor(role, predecessor);
	}

	@Override
	public Collection<Role> getOutgoingRoles()
	{
		return _baseLinkMap.getOutgoingRoles();
	}

	@Override
	public Collection<Role> getIncomingRoles()
	{
		return _baseLinkMap.getIncomingRoles();
	}

	@Override
	public Iterable<NodeID> getSuccessors(final Role role)
	{
		return _baseLinkMap.getSuccessors();
	}

	@Override
	public Set<NodeID> getSuccessors()
	{
		return _baseLinkMap.getSuccessors();
	}

	@Override
	public Iterable<NodeID> getPredecessors(final Role role)
	{
		return _baseLinkMap.getPredecessors(role);
	}

	@Override
	public Set<NodeID> getPredecessors()
	{
		return _baseLinkMap.getPredecessors();
	}

	@Override
	public Iterable<Pair<Role, NodeID>> getPredecessorPairs()
	{
		return _baseLinkMap.getPredecessorPairs();
	}

	@Override
	public Iterable<Pair<Role, NodeID>> getSuccessorPairs()
	{
		return _baseLinkMap.getSuccessorPairs();
	}

	@Override
	public Iterable<IABoxNode<Name, Klass, Role>> getSuccessorNodes(final Role role)
	{
		return GenericImmutableIterable.decorate(_baseLinkMap.getSuccessorNodes(role), _immutableNodeTransformer);
	}

	@Override
	public Iterable<IABoxNode<Name, Klass, Role>> getSuccessorNodes()
	{
		return GenericImmutableIterable.decorate(_baseLinkMap.getSuccessorNodes(), _immutableNodeTransformer);
	}

	@Override
	public Iterable<IABoxNode<Name, Klass, Role>> getPredecessorNodes(final Role role)
	{
		return GenericImmutableIterable.decorate(_baseLinkMap.getPredecessorNodes(role), _immutableNodeTransformer);
	}

	@Override
	public Iterable<IABoxNode<Name, Klass, Role>> getPredecessorNodes()
	{
		return GenericImmutableIterable.decorate(_baseLinkMap.getPredecessorNodes(), _immutableNodeTransformer);
	}

	@Override
	public IRABox<Name, Klass, Role> clone(final IABoxNode<Name, Klass, Role> newNode)
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
	public IRABox<Name, Klass, Role> getImmutable()
	{
		return this;
	}

	@Override
	public IRABox<Name, Klass, Role> getDecoratee()
	{
		return _baseLinkMap;
	}
}
