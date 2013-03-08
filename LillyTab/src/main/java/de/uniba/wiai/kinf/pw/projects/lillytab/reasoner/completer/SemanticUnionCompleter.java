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

import de.dhke.projects.cutil.collections.CollectionUtil;
import de.dhke.projects.cutil.collections.tree.IDecisionTree;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.Branch;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EReasonerError;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EReasonerException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.INodeConsistencyChecker;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ReasonerContinuationState;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLUnion;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.TermUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class SemanticUnionCompleter<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends AbstractCompleter<Name, Klass, Role>
	implements ICompleter<Name, Klass, Role> {

	/**
	 * The largest union size supported. Semantic branching creates pow(2, MAX_UNION_SIZE) branches for unions of that
	 * size.
	 *
	 * The default value already unreasonably large (65536 branches) and the value should never be hit by any
	 * "reasonable" ontology.
	 *
	 */
	public static final int MAX_UNION_SIZE = 16;


	public SemanticUnionCompleter(final INodeConsistencyChecker<Name, Klass, Role> cChecker, final boolean trace)
	{
		super(cChecker, trace);
	}


	public SemanticUnionCompleter(final INodeConsistencyChecker<Name, Klass, Role> cChecker)
	{
		this(cChecker, false);
	}


	@Override
	public ReasonerContinuationState completeNode(IABoxNode<Name, Klass, Role> node,
												  final IDecisionTree.Node<Branch<Name, Klass, Role>> branchNode)
		throws EReasonerException
	{
		final SortedSet<IDLTerm<Name, Klass, Role>> conceptTerms = node.getTerms().subSet(DLTermOrder.DL_UNION);
		final Iterator<IDLTerm<Name, Klass, Role>> iter = conceptTerms.iterator();

		final BranchActionList<Name, Klass, Role> branchActions = new BranchActionList<>();

		while (iter.hasNext()) {
			final IDLTerm<Name, Klass, Role> desc = iter.next();
			if (desc instanceof IDLUnion) {
				final IDLUnion<Name, Klass, Role> union = (IDLUnion<Name, Klass, Role>) desc;
				/* union condition: If not already one of the subterms present */
				final TermEntry<Name, Klass, Role> parentTerm = node.getABox().getTermEntryFactory().
					getEntry(node, desc);
				if (!CollectionUtil.containsOne(node.getTerms(), union)) {
					final int size = union.size();
					if (size > MAX_UNION_SIZE) {
						throw new EReasonerError(String.
							format(
							"Union size (%d) exceeds compiled-in maximum (%d).", size,
							MAX_UNION_SIZE));
					} else {
						/* create semantic branches */
						final int max = (1 << size);
						final List<IDLRestriction<Name, Klass, Role>> posDescs = new ArrayList<>();
						final List<IDLRestriction<Name, Klass, Role>> negDescs = new ArrayList<>();
						final IDLTermFactory<Name, Klass, Role> termFactory = node.getABox().getDLTermFactory();
						for (IDLRestriction<Name, Klass, Role> posDesc : union) {
							final IDLRestriction<Name, Klass, Role> simpPosDesc = TermUtil.simplify(posDesc, termFactory);
							posDescs.add(simpPosDesc);
							negDescs.add(TermUtil.simplify(termFactory.getDLNegation(simpPosDesc), termFactory));
						}

						assert size == negDescs.size();
						assert size == posDescs.size();
						/* bits == 0 is not a valid expansion. All others are. */
						for (int bits = 1; bits < max; ++bits) {
							final Set<IDLRestriction<Name, Klass, Role>> addDescs = new TreeSet<>();
							for (int i = 0; i < size; ++i) {
								if (((bits >> i) & 1) == 0) {
									addDescs.add(negDescs.get(i));
								} else {
									addDescs.add(posDescs.get(i));
								}
							}
							final IBranchAction<Name, Klass, Role> branchAction = new ConceptAddBranchAction<>(
								parentTerm,
								node,
								addDescs);
							branchActions.add(branchAction);
						}
					}
				}

				final List<BranchCreationInfo<Name, Klass, Role>> branchCreationInfos =
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
							logFiner("Created %d branches", branchCreationInfos.size());
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
