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
package de.uniba.wiai.kinf.pw.projects.lillytab.abox;

import de.dhke.projects.cutil.Pair;
import de.dhke.projects.cutil.collections.immutable.IImmutable;
import java.util.Collection;
import java.util.Set;
import org.apache.commons.collections15.MultiMap;


/**
 * <p>
 * The (r)ole (a)ssertion (box) (RABox) contains the
 * role assertions (links) between {@link IABoxNode}s.
 * </p>
 * 
 * @param <Name> The type for nominals and values
 * @param <Klass> The type for DL classes
 * @param <Role> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public interface IRABox<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends IImmutable<IRABox<Name, Klass, Role>>
{
	IABoxNode<Name, Klass, Role> getNode();
	
	/**
	 * <p>
	 * Get the list of asserted role successors, i.e. those
	 * outgoing role links that are present in the actual representation of the graph.
	 * </p><p>
	 * The asserted can but need not include inferred connections,
	 * for example because of role inheritance or transitivity assertions.
	 * 
	 * @return The asserted predecessors of the current node.
	 */
	ILinkMap<Name, Klass, Role> getAssertedSuccessors();
	
	/**
	 * <p>
	 * Get the list of asserted role predecessors, i.e. those
	 * incoming role links that are present in the actual representation of the graph.
	 * </p><p>
	 * The asserted can but need not include inferred connections,
	 * for example because of role inheritance or transitivity assertions.
	 * 
	 * @return The asserted successors of the current node.
	 */
	ILinkMap<Name, Klass, Role> getAssertedPredecessors();
	
	/**
	 * Determine if the current node is connected to the {@literal successor}
	 * via an outgoing {@literal role} link.
	 * @param role The role
	 * @param successor The successor
	 * @return {@literal true} if the current node is connected to {@literal successor} via a {@literal role} link.
	 **/
	boolean hasSuccessor(final Role role, final NodeID successor);
	/**
	 * Determine if the current node to any successor via the specified {@literal role}.
	 * @param role The role
	 * @return {@literal true} has a {@literal role} successor.
	 **/
	boolean hasSuccessor(final Role role);
	/**
	 * Determine if the current node is connected to any {@literal successor} 
	 * via a {@literal role} link.
	 * @param role The role
	 * @param successor The successor
	 * @return {@literal true} if the current node is connected to {@literal successor} via a {@literal role} link.
	 **/
	boolean hasSuccessor(final Role role, final IABoxNode<Name, Klass, Role> successor);
	
	/**
	 * Determine if the specified {@literal predecessor} is connected to the current node
	 * via a {@literal role} link.
	 * @param role The role
	 * @param successor The successor
	 * @return {@literal true} if the current node is connected to {@literal successor} via a {@literal role} link.
	 **/
	boolean hasPredecessor(final Role role, final NodeID predecessor);
	boolean hasPredecessor(final Role role);
	boolean hasPredecessor(final Role role, final IABoxNode<Name, Klass, Role> predecessor);
	
	Collection<Role> getOutgoingRoles();
	Collection<Role> getIncomingRoles();
	
	Iterable<NodeID> getSuccessors(final Role role);
	Set<NodeID> getSuccessors();
	Iterable<NodeID> getPredecessors(final Role role);
	Set<NodeID> getPredecessors();

	Iterable<Pair<Role, NodeID>> getPredecessorPairs();
	Iterable<Pair<Role, NodeID>> getSuccessorPairs();
	
	Iterable<IABoxNode<Name, Klass, Role>> getSuccessorNodes(final Role role);
	Iterable<IABoxNode<Name, Klass, Role>> getSuccessorNodes();
	Iterable<IABoxNode<Name, Klass, Role>> getPredecessorNodes(final Role role);
	Iterable<IABoxNode<Name, Klass, Role>> getPredecessorNodes();
	
	IRABox<Name, Klass, Role> clone(final IABoxNode<Name, Klass, Role> newNode);
	
	boolean deepEquals(final Object obj);
	int deepHashCode();
}
