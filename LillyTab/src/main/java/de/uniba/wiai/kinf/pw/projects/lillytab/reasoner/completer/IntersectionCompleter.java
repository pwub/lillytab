/**
 * (c) 2009-2013 Otto-Friedrich-University Bamberg
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
 *
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer;

import de.dhke.projects.cutil.collections.iterator.ChainIterator;
import de.dhke.projects.cutil.collections.tree.IDecisionTree;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.Branch;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ConsistencyInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EReasonerException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.INodeConsistencyChecker;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ReasonerContinuationState;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.util.AbstractCompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.util.ICompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLIntersection;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLObjectIntersection;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
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
public class IntersectionCompleter<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends AbstractCompleter<I, L, K, R>
	implements ICompleter<I, L, K, R> {

	private static final Logger _logger = LoggerFactory.getLogger(IntersectionCompleter.class);


	public IntersectionCompleter(final INodeConsistencyChecker<I, L, K, R> cChecker, final boolean trace)
	{
		super(cChecker, trace);
	}


	public IntersectionCompleter(final INodeConsistencyChecker<I, L, K, R> cChecker)
	{
		this(cChecker, false);
	}


	/**
	 *
	 * Search the concept set of {@literal node} (on {@literal branch})
	 * for intersections ({@link IDLObjectIntersection}). Perform intersection
	 * completion by unfolding all parts of the intersection into the
	 * concept set of {@literal node}, e.g.
	 * <pre>
	 *   (and A B) =unfold=> (and A B), A, B
	 * </pre>
	 * <p />
	 * Any necessary node merges will be performed automatically.
	 * <p/>
	 *
	 * @param node       The node, whose concept set to search for Intersections.
	 * @param branchNode The current node inside the decision tree.
	 * <p/>
	 * @return {@link ReasonerContinuationState} determining how to continue with reasoning.
	 * <p/>
	 * @throws EReasonerException
	 */
	@Override
	public ReasonerContinuationState completeNode(final IDecisionTree.Node<Branch<I, L, K, R>> branchNode,
												  final IABoxNode<I, L, K, R> node)
	{
		final Branch<I, L, K, R> branch = branchNode.getData();
		final IABox<I, L, K, R> abox = branch.getABox();
		NodeMergeInfo<I, L, K, R> mergeInfo = new NodeMergeInfo<>(node, false);

		Iterator<IDLTerm<I, L, K, R>> iter = ChainIterator.decorate(
			node.getTerms().subSet(DLTermOrder.DL_OBJECT_INTERSECTION).iterator(),
			node.getTerms().subSet(DLTermOrder.DL_DATA_INTERSECTION).iterator());
		while (iter.hasNext()) {
			IDLTerm<I, L, K, R> term = iter.next();
			assert term instanceof IDLIntersection;

			IDLIntersection<I, L, K, R> intersection = (IDLIntersection<I, L, K, R>) term;

			/* if not all subterms are already part of the concept set */
			if ((!node.getTerms().containsAll(intersection.getTerms()))) {

				/* update dependency map and move governing term */
				boolean wasGovTerm = abox.getDependencyMap().getGoverningTerms().remove(abox.getTermEntryFactory().
					getEntry(node, intersection));

				for (IDLRestriction<I, L, K, R> subTerm : intersection.getTerms()) {
					if (!abox.getDependencyMap().containsKey(node, subTerm)) {
						abox.getDependencyMap().addParent(node, subTerm, node, intersection);
					}
					if (wasGovTerm)
						abox.getDependencyMap().addGoverningTerm(node, subTerm);
				}

				try {
					final NodeMergeInfo<I, L, K, R> unfoldResult = node.addTerms(intersection.getTerms());
					mergeInfo.append(unfoldResult);
				} catch (ENodeMergeException ex) {
					branch.getConsistencyInfo().addCulprits(node, intersection);
					branch.getConsistencyInfo().upgradeClashType(ConsistencyInfo.ClashType.FINAL);
					return ReasonerContinuationState.INCONSISTENT;
				}

				final ConsistencyInfo<I, L, K, R> cInfo = getNodeConsistencyChecker().isConsistent(
					mergeInfo.getCurrentNode());
				if (cInfo.isFinallyInconsistent()) {
					branch.upgradeConsistencyInfo(cInfo);
					return ReasonerContinuationState.INCONSISTENT;
				}

				if (!mergeInfo.getMergedNodes().isEmpty()) /**
				 * the current node was merged away, stop processing, recheck queues
				 */
				{
					return ReasonerContinuationState.RECHECK_NODE;
				} else if (mergeInfo.isModified(node)) {
					/* restart iteration for modified set, prevents ConcurrentModificationException */
					iter = ChainIterator.decorate(
						node.getTerms().subSet(DLTermOrder.DL_OBJECT_INTERSECTION).iterator(),
						node.getTerms().subSet(DLTermOrder.DL_DATA_INTERSECTION).iterator());
				}
			}
		}
		return ReasonerContinuationState.CONTINUE;
	}
}
