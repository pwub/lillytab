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

import de.dhke.projects.cutil.collections.CollectionUtil;
import de.dhke.projects.cutil.collections.iterator.ChainIterator;
import de.dhke.projects.cutil.collections.tree.IDecisionTree;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.Branch;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EReasonerError;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EReasonerException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.INodeConsistencyChecker;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ReasonerContinuationState;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.util.AbstractCompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.util.BranchActionList;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.util.BranchCreationInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.util.IBranchAction;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.util.ICompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.util.TermAddBranchAction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLNodeTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLUnion;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.TermUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
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
public class SemanticUnionCompleter<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends AbstractCompleter<I, L, K, R>
	implements ICompleter<I, L, K, R>
{
	/**
	 * The largest union size supported. Semantic branching creates pow(2, MAX_UNION_SIZE) branches for unions of that
	 * size.
	 *
	 * The default value already unreasonably large (65536 branches) and the value should never be hit by any
	 * "reasonable" ontology.
	 *
	 */
	public static final int MAX_UNION_SIZE = 16;
	private static final Logger _logger = LoggerFactory.getLogger(SemanticUnionCompleter.class);

	public SemanticUnionCompleter(final INodeConsistencyChecker<I, L, K, R> cChecker, final boolean trace)
	{
		super(cChecker, trace);
	}

	public SemanticUnionCompleter(final INodeConsistencyChecker<I, L, K, R> cChecker)
	{
		this(cChecker, false);
	}

	@Override
	public ReasonerContinuationState completeNode(final IDecisionTree.Node<Branch<I, L, K, R>> branchNode,
												  IABoxNode<I, L, K, R> node)
		throws EReasonerException
	{
		final Iterator<IDLTerm<I, L, K, R>> iter = ChainIterator.decorate(
			node.getTerms().subSet(DLTermOrder.DL_OBJECT_UNION).iterator(),
			node.getTerms().subSet(DLTermOrder.DL_DATA_UNION).iterator());

		final BranchActionList<I, L, K, R> branchActions = new BranchActionList<>();

		while (iter.hasNext()) {
			final IDLTerm<I, L, K, R> desc = iter.next();
			if (desc instanceof IDLUnion) {
				final IDLUnion<I, L, K, R> union = (IDLUnion<I, L, K, R>) desc;
				/* union condition: If not already one of the subterms present */
				final TermEntry<I, L, K, R> parentTerm = node.getABox().getTermEntryFactory().
					getEntry(node, desc);
				if (!CollectionUtil.containsOne(node.getTerms(), union.getTerms())) {
					final int size = union.getTerms().size();
					if (size > MAX_UNION_SIZE) {
						throw new EReasonerError(String.
							format(
							"Union size (%d) exceeds compiled-in maximum (%d).", size,
							MAX_UNION_SIZE));
					} else {
						/* create semantic branches */
						final int max = (1 << size);
						final List<IDLNodeTerm<I, L, K, R>> posDescs = new ArrayList<>();
						final List<IDLNodeTerm<I, L, K, R>> negDescs = new ArrayList<>();
						final IDLTermFactory<I, L, K, R> termFactory = node.getABox().getDLTermFactory();
						for (IDLNodeTerm<I, L, K, R> posDesc : union.getTerms()) {
							final IDLNodeTerm<I, L, K, R> simpPosDesc = TermUtil.simplify(posDesc, termFactory);
							posDescs.add(simpPosDesc);
							negDescs.add(TermUtil.negate(simpPosDesc, termFactory));
						}

						assert size == negDescs.size();
						assert size == posDescs.size();
						/* bits == 0 is not a valid expansion. All others are. */
						for (int bits = 1; bits < max; ++bits) {
							final Set<IDLNodeTerm<I, L, K, R>> addDescs = new TreeSet<>();
							for (int i = 0; i < size; ++i) {
								if (((bits >> i) & 1) == 0) {
									addDescs.add(negDescs.get(i));
								} else {
									addDescs.add(posDescs.get(i));
								}
							}
							final IBranchAction<I, L, K, R> branchAction = new TermAddBranchAction<>(
								parentTerm,
								node,
								addDescs);
							branchActions.add(branchAction);
						}
					}
				}

				final List<BranchCreationInfo<I, L, K, R>> branchCreationInfos =
					branchActions.commit(branchNode.getData(), getNodeConsistencyChecker());

				/**
				 * If we needed to branch, but all branches are inconsistent. Don't throw Exception, when we did not
				 * find a branch point.
				 *
				 */
				if (!branchActions.isEmpty()) {
					if (branchCreationInfos.isEmpty()) {
						/* this only happens for node merge clashes, not otherwise */
						/* XXX - do we need to change the branches consistency info? */
						return ReasonerContinuationState.INCONSISTENT;
					} else {
						if (isTracing()) {
							_logger.trace("Created %d branches", branchCreationInfos.size());
						}
						final ReasonerContinuationState contState = handleBranchCreation(branchCreationInfos, branchNode,
																						 node);
						if (contState != ReasonerContinuationState.CONTINUE) {
							return contState;
						}
					}
				}
			}
		}
		return ReasonerContinuationState.CONTINUE;
	}
}
