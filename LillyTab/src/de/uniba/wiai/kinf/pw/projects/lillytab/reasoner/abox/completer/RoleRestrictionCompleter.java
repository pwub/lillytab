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
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.Branch;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.EInconsistentABoxNodeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.INodeConsistencyChecker;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ReasonerContinuationState;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.EReasonerException;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import java.util.Collection;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class RoleRestrictionCompleter<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends AbstractCompleter<Name, Klass, Role>
{
	public RoleRestrictionCompleter(final INodeConsistencyChecker<Name, Klass, Role> cChecker)
	{
		super(cChecker);
	}

	public RoleRestrictionCompleter(final INodeConsistencyChecker<Name, Klass, Role> cChecker, final boolean trace)
	{
		super(cChecker, trace);
	}

	/**
	 *
	 * @param node
	 * @param branch
	 * @param branchQueue
	 * @return {@literal true}, if further completion rules should be applied to this node,
	 *	or {@literal false} if the node queue needs to be checked again before applying new rules.
	 * @throws EReasonerException
	 */
	public ReasonerContinuationState completeNode(final IABoxNode<Name, Klass, Role> node,
								final IDecisionTree.Node<Branch<Name, Klass, Role>> branchNode) throws EReasonerException, EInconsistentABoxException
	{
		try {
		final Branch<Name, Klass, Role> branch = branchNode.getData();
		for (Role incomingRole: node.getPredecessors().keySet()) {
			final Collection<IDLRestriction<Name, Klass, Role>> range
				= branch.getABox().getTBox().getRBox().getRoleRanges().get(incomingRole);
			if ((range != null) && (! node.getTerms().containsAll(range))) {
				final NodeMergeInfo<Name, Klass, Role> mergeInfo = node.addUnfoldedDescriptions(range);
				if (mergeInfo.isModified(node)) {
					/**
					 * the current node was merged away, stop processing,
					 * recheck queues
					 **/
					return ReasonerContinuationState.RECHECK_NODE;
				}
			}
		}
		for (Role outgoingRole: node.getSuccessors().keySet()) {
			final Collection<IDLRestriction<Name, Klass, Role>> domain
				= branch.getABox().getTBox().getRBox().getRoleDomains().get(outgoingRole);
			if ((domain != null) && (! node.getTerms().containsAll(domain))) {
				final NodeMergeInfo<Name, Klass, Role> mergeInfo = node.addUnfoldedDescriptions(domain);
				if (mergeInfo.isModified(node)) {
					/**
					 * the current node was merged away, stop processing,
					 * recheck queues
					 **/
					return ReasonerContinuationState.RECHECK_NODE;
				}
			}
		}
		return ReasonerContinuationState.CONTINUE;
		} catch (ENodeMergeException ex) {
			throw new EInconsistentABoxNodeException(ex.getSourceNode());
		}
	}
}
