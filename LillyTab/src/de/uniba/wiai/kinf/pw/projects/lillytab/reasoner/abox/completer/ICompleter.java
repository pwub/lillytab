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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.completer;

import de.dhke.projects.cutil.collections.tree.IDecisionTree;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentABoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ReasonerContinuationState;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.Branch;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.EReasonerException;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 * @param <Name>
 * @param <Klass>
 * @param <Role>
 */
public interface ICompleter<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> {

	/**
	 * <p>
	 * Apply completion rules to the specified {@literal node}.
	 * </p><p>
	 *  Should return {@literal true}, if further completion rules may be applied to the
	 *   current node or {@literal false}, if the completion needs to recheck
	 *   the branch queue and node queue (and possible continue completion of a different node and/or branch).
	 * </p>
	 * 
	 * @param node The node to complete
	 * @param branch The current branch {@literal node} is on
	 **/
	ReasonerContinuationState completeNode(
		final IABoxNode<Name, Klass, Role> node,
		final IDecisionTree.Node<Branch<Name, Klass, Role>> branchNode)
		throws EReasonerException, EInconsistentABoxException;
}
