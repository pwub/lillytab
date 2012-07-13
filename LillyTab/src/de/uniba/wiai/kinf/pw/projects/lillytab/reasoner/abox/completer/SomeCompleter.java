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
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.*;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.Branch;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.INodeConsistencyChecker;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.EIncompatibleNodeTypeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.EInconsistentABoxNodeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ReasonerContinuationState;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.EReasonerException;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLSomeRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;


/**
 *
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
		throws EReasonerException, EInconsistentABoxException
	{
		final Role role = someRestriction.getRole();
		final Branch<Name, Klass, Role> branch = branchNode.getData();
		final IABox<Name, Klass, Role> abox = branch.getABox();
		Collection<NodeID> successorIDs = node.getSuccessors().get(role);
		final IDLRestriction<Name, Klass, Role> someTerm = someRestriction.getTerm();
		IABoxNode<Name, Klass, Role> succ;
		boolean haveGenerated;

		if (node.isDatatypeNode())
			throw new EIncompatibleNodeTypeException(node, "Datatype nodes cannot have successors");

		if ((successorIDs == null) || (successorIDs.isEmpty())) {
			final boolean isDataProperty = abox.getTBox().getRBox().hasRoleProperty(role, RoleProperty.DATA_PROPERTY);
			succ = abox.createNode(isDataProperty);
			node.getSuccessors().put(role, succ.getNodeID());
			branch.touchNode(node);
			haveGenerated = true;
		} else {
			assert !successorIDs.isEmpty();
			succ = abox.getNode(successorIDs.iterator().next());
			haveGenerated = false;
		}
		/* update dependency map */
		if (!abox.getDependencyMap().containsKey(succ, someTerm))
			abox.getDependencyMap().addParent(succ, someTerm, node, someRestriction);
		NodeMergeInfo<Name, Klass, Role> mergeInfo = succ.addUnfoldedDescription(someTerm);

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
		throws EReasonerException, EInconsistentABoxException
	{
		final Branch<Name, Klass, Role> branch = branchNode.getData();
		final IABox<Name, Klass, Role> abox = branch.getABox();
		final Role role = someRestriction.getRole();
		final Collection<NodeID> successorIDs = node.getSuccessors().get(role);
		final IDLRestriction<Name, Klass, Role> someTerm = someRestriction.getTerm();
		boolean haveMatchingSuccessor = false;

		if (node.isDatatypeNode())
			throw new EIncompatibleNodeTypeException(node, "Datatype nodes cannot have successors");

		/**
		 * Check, if we have a successor that contains the
		 * target concept of the current exists-rule.
		 **/
		if (successorIDs != null) {
			for (NodeID successorID : successorIDs) {
				assert abox.getNode(successorID) instanceof ABoxNode;
				IABoxNode<Name, Klass, Role> successor = branch.getABox().getNode(successorID);
				if (successor.getTerms().contains(someTerm)) {
					haveMatchingSuccessor = true;
					break;
				}
			}
		}

		if (!haveMatchingSuccessor) {
			/* schedule the existing node for recheck */
			branch.touchNode(node);

			/**
			 * no successor with matching term found, generate one.
			 *
			 * The new node is the last in the row and thus never
			 * a blocker for some other node (yet).
			 **/
			final boolean isDataProperty = abox.getTBox().getRBox().hasRoleProperty(role, RoleProperty.DATA_PROPERTY);
			IABoxNode<Name, Klass, Role> newNode = abox.createNode(isDataProperty);
			/* update dependency map */
			if (!abox.getDependencyMap().containsKey(newNode, someTerm)) {
				abox.getDependencyMap().addGoverningTerm(newNode, someTerm);
				abox.getDependencyMap().addParent(newNode, someTerm, node, someRestriction);
			}

			final NodeMergeInfo<Name, Klass, Role> unfoldResult = newNode.addUnfoldedDescription(someTerm);
			newNode = unfoldResult.getCurrentNode();
			assert branch.getABox().contains(newNode);
			/*
			 * put link if it does not already exist
			 * The check is required, as the node obtain after the unfolding may not be new.
			 **/
			if (!node.getSuccessors().containsValue(role, newNode.getNodeID()))
				node.getSuccessors().put(role, newNode.getNodeID());
			if (isTracing())
				logFinest("%s: Generated node: %s", branchNode, newNode);

			/* apply only ONE generating rule at a time */
			return ReasonerContinuationState.RECHECK_NODE;
		}
		return ReasonerContinuationState.CONTINUE;
	}

	public ReasonerContinuationState completeNode(IABoxNode<Name, Klass, Role> node,
												  IDecisionTree.Node<Branch<Name, Klass, Role>> branchNode)
		throws EReasonerException, EInconsistentABoxException
	{
		try {

			if (!getBlockingStrategy().isBlocked(node)) {
				final Set<IDLTerm<Name, Klass, Role>> conceptTerms = node.getTerms().subSet(
					DLTermOrder.DL_SOME_RESTRICTION);

				final Iterator<IDLTerm<Name, Klass, Role>> iter = conceptTerms.iterator();
				while (iter.hasNext()) {
					final IDLTerm<Name, Klass, Role> term = iter.next();
					if (term instanceof IDLSomeRestriction) {
						final IDLSomeRestriction<Name, Klass, Role> someRestriction = (IDLSomeRestriction<Name, Klass, Role>) term;
						final Role role = someRestriction.getRole();

						ReasonerContinuationState contState;
						if (node.getABox().getTBox().getRBox().hasRoleProperty(role, RoleProperty.FUNCTIONAL)) {
							contState = completeFunctionalRole(branchNode, node, someRestriction);
						} else {
							contState = completeNonFunctionalRole(branchNode, node, someRestriction);
						}
						if (contState != ReasonerContinuationState.CONTINUE)
							return contState;
					}
				}
			} else {
				if (isTracing())
					logFinest("%s: Node %s blocked by %s", branchNode.getPath(), node, getBlockingStrategy().getBlocker(
						node));
			}
			return ReasonerContinuationState.CONTINUE;
		} catch (ENodeMergeException ex) {
			throw new EInconsistentABoxNodeException(ex.getSourceNode());
		}
	}
}
