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

import org.apache.commons.collections15.MultiMap;


/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public interface ILinkMap<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
{
	IABoxNode<Name, Klass, Role> getNode();
	
	/**
	 * <p>
	 * Get the list of asserted role successors, i.e. those
	 * role links that are present in the actual representation of the graph.
	 * </p><p>
	 * The asserted can but need not include inferred connections,
	 * for example because of role inheritance or transitivity assertions.
	 * @return 
	 */
	MultiMap<Role, NodeID> getAssertedSuccessors();
	MultiMap<Role, NodeID> getAssertedPredecessors();
	
	Iterable<NodeID> getSuccessors(final Role role);
	Iterable<NodeID> getPredecessors(final Role role);
	
	Iterable<IABoxNode<Name, Klass, Role>> getSuccessorNodes(final Role role);
	Iterable<IABoxNode<Name, Klass, Role>> getPredecessorNodes(final Role role);
}
