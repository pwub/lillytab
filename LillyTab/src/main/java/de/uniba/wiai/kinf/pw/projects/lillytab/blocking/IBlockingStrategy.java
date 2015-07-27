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
package de.uniba.wiai.kinf.pw.projects.lillytab.blocking;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.util.ICompleter;
import java.util.Set;


/**
 * 
 * Applying generating {@link ICompleter} without additional termination conditions may potentially generate infinite
 * models for some ontologies. The problem is usually solved by preventing the node generation step to generate more
 * nodes, when this step cannot generate additional information. (see the literature on blocking strategies for more
 * information).
 * <p />
 * This is achieved by searching for nodes that "block" the current node before a generation step is about to be
 * performed. Actual blocking conditions depend on the expressivness of the involved logic. Some logics even do not need
 * blocking.
 * <p /> {@link IBlockingStrategy} encapsulates the interface to a pluggable blocking strategy.
 * 
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public interface IBlockingStrategy<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
{
	/**
	 * 
	 * Retrieve the {@link NodeID}s of nodes blocked by the current node.
	 * <p />
	 * This method returns not a complete set, but only the set of nodes that are <emph>currently assumed</emph> to be
	 * blocked by the current node.
	 * <p />
	 * Blocking nodes must be searched inside the ABox. Since this is often a computationally intesive task, blocking
	 * status may be cached internally. Changes to the ABox that potentially invalidate the blocking condition must call 
	 * {@link #validateBlocks()} afterwards.
	 *
	 * @return The set of NodeIDs of nodes that are currently blocked by the current node.
	 *
	 */
	Set<NodeID> getBlockedNodeIDs(final IABoxNode<I, L, K, R> blocker);

	/**
	 * 
	 * Determine if the specified node is blocked by another node within the node's ABox.
	 * <p />
	 * Blocking nodes must be searched inside the ABox. Since this is often a computationally intesive task, blocking
	 * status may be cached internally. Changes to the ABox that potentially invalidate the blocking condition must call {@link #validateBlocks()
	 * } afterwards.
	 * 
	 *
	 * @param blockedNode The node to check.
	 * @return {@literal true} if the current node is blocked by some other node.
	 */
	boolean isBlocked(final IABoxNode<I, L, K, R> blockedNode);

	/**
	 * @return The blocker of the target node or {@literal null} if the current node is not blocked.
	 * 
	 * <p />
	 * Blocking nodes must be searched inside the ABox. Since this is often a computationally intesive task, blocking
	 * status may be cached internally. Changes to the ABox that potentially invalidate the blocking condition must call {@link #validateBlocks()} afterwards.
	 * 
	 **/
	IABoxNode<I, L, K, R> getBlocker(final IABoxNode<I, L, K, R> blockedNode);

	/**
	 * Check, if the blocking conditions for all nodes in the blocker list ({@link #getBlockedNodeIDs()}
	 * are still met. Return the list of nodes (from {@link #getBlockedNodeIDs()} for which the blocking conditions are
	 * no more satisfied.
	 *
	 * @param targetNode The target node to check
	 * @return A set of NodeIDs of invalidated blocked nodes.
	 *
	 */
	Set<NodeID> validateBlocks(final IABoxNode<I, L, K, R> targetNode);
}
