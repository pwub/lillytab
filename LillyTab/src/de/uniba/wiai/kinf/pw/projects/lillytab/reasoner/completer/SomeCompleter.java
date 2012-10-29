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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer;

import de.uniba.wiai.kinf.pw.projects.lillytab.blocking.IBlockingStrategy;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleProperty;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.dhke.projects.cutil.collections.tree.IDecisionTree;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.Branch;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ConsistencyInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.INodeConsistencyChecker;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EReasonerException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ReasonerContinuationState;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleType;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLSomeRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import java.util.Iterator;
import java.util.Set;


/**
 *
 * @param <Name> The type for nominals and values
 * @param <Klass> The type for DL classes
 * @param <Role> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class SomeCompleter<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends AbstractGeneratingCompleter<Name, Klass, Role>
	implements ICompleter<Name, Klass, Role>
{
	public SomeCompleter(final INodeConsistencyChecker<Name, Klass, Role> cChecker,
						 final IBlockingStrategy<Name, Klass, Role> blockingStrategy,
						 final boolean trace)
	{
		super(cChecker, blockingStrategy, trace);
	}

	public SomeCompleter(final INodeConsistencyChecker<Name, Klass, Role> cChecker,
						 final IBlockingStrategy<Name, Klass, Role> blockingStrategy)
	{
		this(cChecker, blockingStrategy, false);
	}

	private ReasonerContinuationState completeFunctionalRole(
		final IDecisionTree.Node<Branch<Name, Klass, Role>> branchNode,
		final IABoxNode<Name, Klass, Role> node,
		final IDLSomeRestriction<Name, Klass, Role> someRestriction)
		throws EReasonerException
	{
		final Role role = someRestriction.getRole();
		final Branch<Name, Klass, Role> branch = branchNode.getData();
		final IABox<Name, Klass, Role> abox = branch.getABox();
		final IDLRestriction<Name, Klass, Role> someTerm = someRestriction.getTerm();
		IABoxNode<Name, Klass, Role> succ;
		boolean haveGenerated;

		if (node.isDatatypeNode()) {
			branch.getConsistencyInfo().addCulprits(node, someRestriction);
			branch.getConsistencyInfo().upgradeClashType(ConsistencyInfo.ClashType.FINAL);
			return ReasonerContinuationState.INCONSISTENT;
		}

		try {

			if (node.getRABox().hasSuccessor(role)) {
				Iterable<NodeID> successorIDs = node.getRABox().getSuccessors(role);
				succ = abox.getNode(successorIDs.iterator().next());
				haveGenerated = false;
			} else {
				final boolean isDataProperty = abox.getTBox().getRBox().hasRoleType(role, RoleType.DATA_PROPERTY);
				succ = abox.createNode(isDataProperty);
				node.getRABox().getAssertedSuccessors().put(role, succ.getNodeID());
				branch.touchNode(node);
				haveGenerated = true;
			}
			/*
			 * update dependency map
			 */
			if (!abox.getDependencyMap().containsKey(succ, someTerm))
				abox.getDependencyMap().addParent(succ, someTerm, node, someRestriction);
			/* final NodeMergeInfo<Name, Klass, Role> mergeInfo = */ succ.addUnfoldedDescription(someTerm);
		} catch (ENodeMergeException ex) {
			branch.getConsistencyInfo().addCulprits(node, someRestriction);
			branch.getConsistencyInfo().upgradeClashType(ConsistencyInfo.ClashType.FINAL);
			return ReasonerContinuationState.INCONSISTENT;
		}

		if (haveGenerated)
			return ReasonerContinuationState.RECHECK_NODE;
		else
			return ReasonerContinuationState.CONTINUE;
	}

	/**
	 *
	 * @param branch The current branch
	 * @param node The current node
	 * @param someRestriction The restriction this completion happends
	 * @return {@literal true}, if a successor was generated.
	 * @throws EReasonerException
	 */
	private ReasonerContinuationState completeNonFunctionalRole(
		final IDecisionTree.Node<Branch<Name, Klass, Role>> branchNode,
		final IABoxNode<Name, Klass, Role> node,
		final IDLSomeRestriction<Name, Klass, Role> someRestriction)
		throws EReasonerException
	{
		final Branch<Name, Klass, Role> branch = branchNode.getData();
		final IABox<Name, Klass, Role> abox = branch.getABox();
		final Role role = someRestriction.getRole();
		final IDLRestriction<Name, Klass, Role> subTerm = someRestriction.getTerm();
		boolean haveMatchingSuccessor = false;

		if (node.isDatatypeNode()) {
			branch.getConsistencyInfo().addCulprits(node, someRestriction);
			branch.getConsistencyInfo().upgradeClashType(ConsistencyInfo.ClashType.FINAL);
			return ReasonerContinuationState.INCONSISTENT;
		}

		/**
		 * Check, if we have a successor that contains the target concept of the current exists-rule.
		 *
		 */
		if (node.getRABox().hasSuccessor(role)) {
			final Iterable<IABoxNode<Name, Klass, Role>> successors = node.getRABox().getSuccessorNodes(role);
			for (IABoxNode<Name, Klass, Role> successor : successors) {
				if (successor.getTerms().contains(subTerm)) {
					haveMatchingSuccessor = true;
					break;
				}
			}
		}

		if (!haveMatchingSuccessor) {
			/*
			 * schedule the existing node for recheck
			 */
			branch.touchNode(node);

			/**
			 * No successor with matching term found, generate one.
			 *
			 * The new node is the last in the row and thus never a blocker for some other node (yet).
			 *
			 */
			final boolean isDataProperty = abox.getTBox().getRBox().hasRoleType(role, RoleType.DATA_PROPERTY);

			try {
				IABoxNode<Name, Klass, Role> newNode = abox.createNode(isDataProperty);
				/**
				 * update dependency map
				 *
				 */
				if (!abox.getDependencyMap().containsKey(newNode, subTerm)) {
					abox.getDependencyMap().addParent(newNode, subTerm, node, someRestriction);
					/**
					 * The governing term is not the initial "(∃ . term)", but the actual subterm "term" if this one is
					 * atomic.
					 *
					 */
					if (subTerm instanceof IAtom)
						abox.getDependencyMap().addGoverningTerm(newNode, subTerm);
				}

				final NodeMergeInfo<Name, Klass, Role> unfoldResult = newNode.addUnfoldedDescription(subTerm);
				newNode = unfoldResult.getCurrentNode();
				assert branch.getABox().contains(newNode);
				/*
				 * put link if it does not already exist The check is required, as the node obtain after the unfolding may
				 * not be new.
				 */
				if (!node.getRABox().hasSuccessor(role, newNode.getNodeID()))
					node.getRABox().getAssertedSuccessors().put(role, newNode.getNodeID());
				if (isTracing())
					logFinest("%s: Generated node: %s", branchNode, newNode);

				/*
				 * apply only ONE generating rule at a time
				 */
				return ReasonerContinuationState.RECHECK_NODE;
			} catch (ENodeMergeException ex) {
				branch.getConsistencyInfo().addCulprits(node, someRestriction);
				branch.getConsistencyInfo().upgradeClashType(ConsistencyInfo.ClashType.FINAL);
				return ReasonerContinuationState.INCONSISTENT;
			}
		}
		return ReasonerContinuationState.CONTINUE;
	}

	@Override
	public ReasonerContinuationState completeNode(IABoxNode<Name, Klass, Role> node,
												  IDecisionTree.Node<Branch<Name, Klass, Role>> branchNode)
		throws EReasonerException
	{
		if (!getBlockingStrategy().isBlocked(node)) {
			final Set<IDLTerm<Name, Klass, Role>> conceptTerms = node.getTerms().subSet(
				DLTermOrder.DL_SOME_RESTRICTION);

			final Iterator<IDLTerm<Name, Klass, Role>> iter = conceptTerms.iterator();
			while (iter.hasNext()) {
				final IDLTerm<Name, Klass, Role> term = iter.next();
				if (term instanceof IDLSomeRestriction) {
					final IDLSomeRestriction<Name, Klass, Role> someRestriction = (IDLSomeRestriction<Name, Klass, Role>) term;
					final Role role = someRestriction.getRole();

					ReasonerContinuationState cState;
					if (node.getABox().getTBox().getRBox().hasRoleProperty(role, RoleProperty.FUNCTIONAL)) {
						cState = completeFunctionalRole(branchNode, node, someRestriction);
					} else {
						cState = completeNonFunctionalRole(branchNode, node, someRestriction);
					}
					if (cState != ReasonerContinuationState.CONTINUE)
						return cState;
				}
			}
		} else {
			if (isTracing())
				logFinest("%s: Node %s blocked by %s", branchNode.getPath(), node, getBlockingStrategy().getBlocker(
					node));
		}
		return ReasonerContinuationState.CONTINUE;
	}
}
