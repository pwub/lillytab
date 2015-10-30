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
package de.uniba.wiai.kinf.pw.projects.lillytab.abox;

import de.dhke.projects.cutil.Pair;
import de.dhke.projects.cutil.collections.immutable.IImmutable;
import java.util.Collection;


/**
 *
 * The (r)ole (a)ssertion (box) (RABox) contains the role assertions (links) between {@link IABoxNode}s.
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public interface IRABox<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends IImmutable<IRABox<I, L, K, R>> {

	IABoxNode<I, L, K, R> getNode();

	/**
	 *
	 * Get the list of asserted role successors, i.e. those outgoing role links that are present in the actual
	 * representation of the graph.
	 * <p />
	 * The asserted can but need not include inferred connections, for example because of role inheritance or
	 * transitivity assertions.
	 *
	 * @return The asserted predecessors of the current node.
	 */
	ILinkMap<I, L, K, R> getAssertedSuccessors();

	/**
	 *
	 * Get the list of asserted role predecessors, i.e. those incoming role links that are present in the actual
	 * representation of the graph.
	 * <p />
	 * The asserted can but need not include inferred connections, for example because of role inheritance or
	 * transitivity assertions.
	 *
	 * @return The asserted successors of the current node.
	 */
	ILinkMap<I, L, K, R> getAssertedPredecessors();

	/**
	 * Determine if the current node is connected to the {@literal successor} via an outgoing {@literal role} link.
	 *
	 * @param role The role
	 * @param successor The successor
	 * @return {@literal true} if the current node is connected to {@literal successor} via a {@literal role} link.
	 *
	 */
	boolean hasSuccessor(final R role, final NodeID successor);

	/**
	 * Determine if the current node to any successor via the specified {@literal role}.
	 *
	 * @param role The role
	 * @return {@literal true} has a {@literal role} successor.
	 *
	 */
	boolean hasSuccessor(final R role);

	/**
	 * Determine if the current node is connected to any {@literal successor} via a {@literal role} link.
	 *
	 * @param role The role
	 * @param successor The successor
	 * @return {@literal true} if the current node is connected to {@literal successor} via a {@literal role} link.
	 *
	 */
	boolean hasSuccessor(final R role, final IABoxNode<I, L, K, R> successor);

	/**
	 * Determine if the specified {@literal predecessor} is connected to the current node via a {@literal role} link.
	 *
	 * @param role The role
	 * @param predecessor The predecessor
	 * @return {@literal true} if the current node is connected to {@literal successor} via a {@literal role} link.
	 *
	 */
	boolean hasPredecessor(final R role, final NodeID predecessor);

	boolean hasPredecessor(final R role);

	boolean hasPredecessor(final R role, final IABoxNode<I, L, K, R> predecessor);

	Collection<R> getOutgoingRoles();

	Iterable<R> getOutgoingRoles(final IABoxNode<L, I, K, R> target);

	Iterable<R> getOutgoingRoles(final NodeID target);

	Collection<R> getIncomingRoles();

	Iterable<R> getIncomingRoles(final IABoxNode<L, I, K, R> source);

	Iterable<R> getIncomingRoles(final NodeID source);

	Collection<NodeID> getSuccessors(final R role);

	Collection<NodeID> getSuccessors();

	Collection<NodeID> getPredecessors(final R role);

	Collection<NodeID> getPredecessors();

	Iterable<Pair<R, NodeID>> getPredecessorPairs();

	Iterable<Pair<R, NodeID>> getSuccessorPairs();

	Collection<IABoxNode<I, L, K, R>> getSuccessorNodes(final R role);

	Collection<IABoxNode<I, L, K, R>> getSuccessorNodes();

	Collection<IABoxNode<I, L, K, R>> getPredecessorNodes(final R role);

	Collection<IABoxNode<I, L, K, R>> getPredecessorNodes();

	IRABox<I, L, K, R> clone(final IABoxNode<I, L, K, R> newNode);

	boolean deepEquals(final Object obj);

	int deepHashCode();
}
