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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer;

import de.dhke.projects.cutil.collections.iterator.ChainIterator;
import de.dhke.projects.cutil.collections.tree.IDecisionTree;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IDatatypeABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.Branch;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ConsistencyInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EReasonerException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.INodeConsistencyChecker;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ReasonerContinuationState;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.EIllegalTermTypeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.util.AbstractCompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.util.ICompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleProperty;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleType;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLDataSomeRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLNodeTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLObjectSomeRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLSomeRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class SomeCompleter<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends AbstractCompleter<I, L, K, R>
	implements ICompleter<I, L, K, R> {

	private static final Logger _logger = LoggerFactory.getLogger(SomeCompleter.class);


	public SomeCompleter(final INodeConsistencyChecker<I, L, K, R> cChecker,
						 final boolean trace)
	{
		super(cChecker, trace);
	}


	public SomeCompleter(final INodeConsistencyChecker<I, L, K, R> cChecker)
	{
		this(cChecker, false);
	}


	@Override
	public ReasonerContinuationState completeNode(
		IDecisionTree.Node<Branch<I, L, K, R>> branchNode, IABoxNode<I, L, K, R> node)
		throws EReasonerException
	{
		@SuppressWarnings("unchecked")
		final Iterator<IDLTerm<I, L, K, R>> iter = ChainIterator.decorate(
			node.getTerms().iterator(IDLObjectSomeRestriction.class),
			node.getTerms().iterator(IDLDataSomeRestriction.class));
		while (iter.hasNext()) {
			final IDLTerm<I, L, K, R> term = iter.next();
			if (term instanceof IDLSomeRestriction) {
				final IDLSomeRestriction<I, L, K, R> someRestriction = (IDLSomeRestriction<I, L, K, R>) term;
				final R role = someRestriction.getRole();

				ReasonerContinuationState cState;
				if (node.getABox().getTBox().getRBox().hasRoleProperty(role, RoleProperty.FUNCTIONAL)) {
					cState = completeFunctionalRole(branchNode, node, someRestriction);
				} else {
					cState = completeNonFunctionalRole(branchNode, node, someRestriction);
				}
				if (cState != ReasonerContinuationState.CONTINUE) {
					return cState;
				}
			}
		}
		return ReasonerContinuationState.CONTINUE;
	}

	protected ReasonerContinuationState completeFunctionalRole(
		final IDecisionTree.Node<Branch<I, L, K, R>> branchNode, final IABoxNode<I, L, K, R> node, final IDLSomeRestriction<I, L, K, R> someRestriction)
		throws EReasonerException
	{
		final R role = someRestriction.getRole();
		final Branch<I, L, K, R> branch = branchNode.getData();
		final IABox<I, L, K, R> abox = branch.getABox();
		final IDLNodeTerm<I, L, K, R> subTerm = someRestriction.getTerm();
		IABoxNode<I, L, K, R> succ;
		boolean haveGenerated;

		if (node instanceof IDatatypeABoxNode) {
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
				// abox.touchNode(node);
				haveGenerated = true;
			}

			/**
			 * update dependency map and governing terms
			 * <p/>
			 * The governing term always moves to the successor.
			 */
			final TermEntry<I, L, K, R> someTermEntry = abox.getTermEntryFactory().getEntry(node, someRestriction);
			final TermEntry<I, L, K, R> subTermEntry = abox.getTermEntryFactory().getEntry(succ, subTerm);
			if (abox.getDependencyMap().getGoverningTerms().remove(someTermEntry)) {
				abox.getDependencyMap().addGoverningTerm(subTermEntry);
			}
			if (!abox.getDependencyMap().containsKey(succ, subTerm)) {
				abox.getDependencyMap().addParent(succ, subTerm, node, someRestriction);
			}
			succ.addTerm(subTerm);


			/* final NodeMergeInfo<I, L, K, R> mergeInfo = */ succ.addTerm(subTerm);
		} catch (ENodeMergeException | EIllegalTermTypeException ex) {
			branch.getConsistencyInfo().addCulprits(node, someRestriction);
			branch.getConsistencyInfo().upgradeClashType(ConsistencyInfo.ClashType.FINAL);
			return ReasonerContinuationState.INCONSISTENT;
		}

		if (haveGenerated) {
			return ReasonerContinuationState.RECHECK_NODE;
		} else {
			return ReasonerContinuationState.CONTINUE;
		}
	}


	/**
	 *
	 * @param branchNode      The current branch node
	 * @param node            The current node
	 * @param someRestriction The restriction this completion happens on
	 * <p/>
	 * @return {@literal true}, if a successor was generated.
	 * <p/>
	 * @throws EReasonerException
	 */
		protected ReasonerContinuationState completeNonFunctionalRole(final IDecisionTree.Node<Branch<I, L, K, R>> branchNode, final IABoxNode<I, L, K, R> node, final IDLSomeRestriction<I, L, K, R> someRestriction)
		throws EReasonerException
	{
		final Branch<I, L, K, R> branch = branchNode.getData();
		final IABox<I, L, K, R> abox = branch.getABox();
		final R role = someRestriction.getRole();
		final IDLNodeTerm<I, L, K, R> subTerm = someRestriction.getTerm();
		boolean haveMatchingSuccessor = false;

		if (node instanceof IDatatypeABoxNode) {
			branch.getConsistencyInfo().addCulprits(node, someRestriction);
			branch.getConsistencyInfo().upgradeClashType(ConsistencyInfo.ClashType.FINAL);
			return ReasonerContinuationState.INCONSISTENT;
		}

		/**
		 * Check, if we have a successor that contains the target concept of the current exists-rule.
		 *
		 */
		if (node.getRABox().hasSuccessor(role)) {
			final Iterable<IABoxNode<I, L, K, R>> successors = node.getRABox().getSuccessorNodes(role);
			for (IABoxNode<I, L, K, R> successor : successors) {
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
			// abox.touchNode(node);

			/**
			 * No successor with matching term found, generate one.
			 *
			 * The new node is the last in the row and thus never a blocker for some other node (yet).
			 */
			final boolean isDataProperty = abox.getRBox().hasRoleType(role, RoleType.DATA_PROPERTY);

			try {
				IABoxNode<I, L, K, R> newNode = abox.createNode(isDataProperty);
				/**
				 * update dependency map
				 */
				if (!abox.getDependencyMap().containsKey(newNode, subTerm)) {
					abox.getDependencyMap().addParent(newNode, subTerm, node, someRestriction);
					/**
					 * The governing term is not the initial some restriction "(∃ . term)",
					 * but the actual subterm "term".
					 * If the some restriction was a governing term, delete it.
					 *
					 */
					if (abox.getDependencyMap().getGoverningTerms().remove(abox.getTermEntryFactory().getEntry(node,
																											   someRestriction))) {
						abox.getDependencyMap().addGoverningTerm(node, abox.getDLTermFactory().getDLThing());

					}
					abox.getDependencyMap().addGoverningTerm(newNode, subTerm);
				}

				final NodeMergeInfo<I, L, K, R> unfoldResult = newNode.addTerm(subTerm);
				newNode = unfoldResult.getCurrentNode();
				assert branch.getABox().contains(newNode);
				/*
				 * put link if it does not already exist The check is required, as the node obtain after the unfolding may
				 * not be new.
				 */
				if (!node.getRABox().hasSuccessor(role, newNode.getNodeID())) {
					node.getRABox().getAssertedSuccessors().put(role, newNode.getNodeID());
				}
				if (isTracing()) {
					_logger.trace("%s: Generated node: %s", branchNode, newNode);
				}

				/*
				 * apply only ONE generating rule at a time
				 */
				return ReasonerContinuationState.RECHECK_NODE;
			} catch (ENodeMergeException | EIllegalTermTypeException ex) {
				branch.getConsistencyInfo().addCulprits(node, someRestriction);
				branch.getConsistencyInfo().upgradeClashType(ConsistencyInfo.ClashType.FINAL);
				return ReasonerContinuationState.INCONSISTENT;
			}
		}
		return ReasonerContinuationState.CONTINUE;
	}
}
