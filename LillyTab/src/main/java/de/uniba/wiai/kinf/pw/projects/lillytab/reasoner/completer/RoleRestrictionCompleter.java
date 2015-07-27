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

import de.dhke.projects.cutil.collections.tree.IDecisionTree;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.Branch;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ConsistencyInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EReasonerError;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EReasonerException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.INodeConsistencyChecker;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ReasonerContinuationState;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.EIllegalTermTypeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.util.AbstractCompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleType;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLDataAllRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLNodeTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLObjectAllRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataRange;
import java.util.Collection;


/**
 *
 * @param <I> The type for individuals/nominals
 * @param <L> The type for literals
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class RoleRestrictionCompleter<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends AbstractCompleter<I, L, K, R>
{
	public RoleRestrictionCompleter(final INodeConsistencyChecker<I, L, K, R> cChecker)
	{
		super(cChecker);
	}

	public RoleRestrictionCompleter(final INodeConsistencyChecker<I, L, K, R> cChecker, final boolean trace)
	{
		super(cChecker, trace);
	}

	/**
	 *
	 * @param node
	 * @param branch
	 * @param branchQueue
	 * @return {@literal true}, if further completion rules should be applied to this node, or {@literal false} if the
	 * node queue needs to be checked again before applying new rules.
	 * @throws EReasonerException
	 */
	@Override
	public ReasonerContinuationState completeNode(final IDecisionTree.Node<Branch<I, L, K, R>> branchNode,
												  final IABoxNode<I, L, K, R> node)
		throws EReasonerException
	{
		final Branch<I, L, K, R> branch = branchNode.getData();

		/**
		 * We convert role domain restrictions into local forAll terms.
		 * This also makes the transitive propagation work.
		 */
		for (R outRole : node.getRABox().getOutgoingRoles()) {
			final Collection<IDLNodeTerm<I, L, K, R>> ranges = branch.getABox().getTBox().getRBox().
				getRoleRanges(outRole);
			final IABox<I, L, K, R> abox = node.getABox();
			for (IDLNodeTerm<I, L, K, R> range : ranges) {
				try {
					if (abox.getRBox().hasRoleType(outRole, RoleType.DATA_PROPERTY)) {
						final IDLDataRange<I, L, K, R> dataRange = (IDLDataRange<I, L, K, R>) range;
						final IDLDataAllRestriction<I, L, K, R> allRes = branch.getABox().getDLTermFactory().
							getDLDataAllRestriction(outRole, dataRange);
						if (! node.getTerms().contains(range)) {
							abox.getDependencyMap().addParent(node, range, node, abox.getDLTermFactory().getDLTopDatatype());
						}
						node.addTerm(allRes);
					} else if (abox.getRBox().hasRoleType(outRole, RoleType.OBJECT_PROPERTY)) {
						final IDLClassExpression<I, L, K, R> classExp = (IDLClassExpression<I, L, K, R>) range;
						final IDLObjectAllRestriction<I, L, K, R> allRes = branch.getABox().getDLTermFactory().
							getDLObjectAllRestriction(outRole, classExp);
						if (! node.getTerms().contains(range)) {
							abox.getDependencyMap().addParent(node, range, node, abox.getDLTermFactory().getDLThing());
						}
						node.addTerm(allRes);
					}
				} catch (EIllegalTermTypeException ex) {
					throw new EReasonerError("Internal reasoner error!", ex);
				} catch (ENodeMergeException ex) {
					branch.getConsistencyInfo().upgradeClashType(ConsistencyInfo.ClashType.FINAL);
					branch.getConsistencyInfo().addCulprits(node, range);
					return ReasonerContinuationState.INCONSISTENT;
				}
			}
			final Collection<IDLClassExpression<I, L, K, R>> domains = branch.getABox().getTBox().getRBox().getRoleDomains(outRole);
			if ((domains != null) && (!node.getTerms().containsAll(domains))) {
				try {
					for (IDLClassExpression<I, L, K, R> domain: domains) {
						if (! node.getTerms().contains(domain)) {
							abox.getDependencyMap().addParent(node, domain, node, abox.getDLTermFactory().getDLThing());
						}
					}
					final NodeMergeInfo<I, L, K, R> mergeInfo = node.addTerms(domains);
					if (mergeInfo.isModified(node)) {
						/**
						 * the current node was merged away, stop processing, recheck queues
						 *
						 */
						return ReasonerContinuationState.RECHECK_NODE;
					}
				} catch (ENodeMergeException ex) {
					/* XXX - this be narrowed down to an individual term */
					branch.getConsistencyInfo().upgradeClashType(ConsistencyInfo.ClashType.FINAL);
					branch.getConsistencyInfo().addCulprits(node, domains);
					return ReasonerContinuationState.INCONSISTENT;
				}
			}
		}
		return ReasonerContinuationState.CONTINUE;
	}
}
