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

import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.Branch;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EInconsistentABoxNodeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EReasonerException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.INodeConsistencyChecker;
import de.dhke.projects.cutil.collections.CollectionUtil;
import de.dhke.projects.cutil.collections.tree.IDecisionTree;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentABoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ConsistencyInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ReasonerContinuationState;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ReasonerContinuationState;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLUnion;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;


/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class UnionCompleter<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends AbstractCompleter<Name, Klass, Role>
	implements ICompleter<Name, Klass, Role>
{
	public UnionCompleter(final INodeConsistencyChecker<Name, Klass, Role> cChecker, final boolean trace)
	{
		super(cChecker, trace);
	}

	public UnionCompleter(final INodeConsistencyChecker<Name, Klass, Role> cChecker)
	{
		this(cChecker, false);
	}

	public ReasonerContinuationState completeNode(IABoxNode<Name, Klass, Role> node,
																	final IDecisionTree.Node<Branch<Name, Klass, Role>> branchNode) throws EReasonerException
	{
		final SortedSet<IDLTerm<Name, Klass, Role>> conceptTerms = node.getTerms().subSet(DLTermOrder.DL_UNION);
		final Iterator<IDLTerm<Name, Klass, Role>> iter = conceptTerms.iterator();

		final BranchActionList<Name, Klass, Role> branchActions = new BranchActionList<Name, Klass, Role>();

		while (iter.hasNext()) {
			final IDLTerm<Name, Klass, Role> desc = iter.next();
			if (desc instanceof IDLUnion) {
				final IDLUnion<Name, Klass, Role> union = (IDLUnion<Name, Klass, Role>) desc;
				/* union condition: If not already one of the subterms present */
				final TermEntry<Name, Klass, Role> parentTerm = node.getABox().getTermEntryFactory().
					getEntry(node, desc);
				if (!CollectionUtil.containsOne(node.getTerms(), union)) {
					for (IDLRestriction<Name, Klass, Role> subTerm : union)
						branchActions.add(new ConceptAddBranchAction<Name, Klass, Role>(parentTerm, node, subTerm));
					assert !branchActions.isEmpty();
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
						if (isTracing())
							logFiner("Created %d branches", branchCreationInfos.size());
						final ReasonerContinuationState contState = handleBranchCreation(branchCreationInfos, branchNode, node);
						if (contState != ReasonerContinuationState.CONTINUE)
							return contState;
					}
				}
			}
		}
		return ReasonerContinuationState.CONTINUE;
	}
}
