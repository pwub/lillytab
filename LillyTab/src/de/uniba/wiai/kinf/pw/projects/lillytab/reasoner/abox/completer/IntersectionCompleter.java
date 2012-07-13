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
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.*;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLIntersection;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import java.util.Iterator;
import java.util.SortedSet;


/**
 *
 * @param <Name>
 * @param <Klass>
 * @param <Role>
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class IntersectionCompleter<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends AbstractCompleter<Name, Klass, Role>
	implements ICompleter<Name, Klass, Role>
{
	public IntersectionCompleter(final INodeConsistencyChecker<Name, Klass, Role> cChecker, final boolean trace)
	{
		super(cChecker, trace);
	}

	;

	public IntersectionCompleter(final INodeConsistencyChecker<Name, Klass, Role> cChecker)
	{
		this(cChecker, false);
	}

	;


	/**
	 * <p>
	 * Search the concept set of {@literal node} (on {@literal branch})
	 * for intersections ({@link IDLIntersection}). Perform intersection
	 * completion by unfolding all parts of the intersection into the
	 * concept set of {@literal node}, e.g.
	 * <pre>
	 *   (and A B) =unfold=> (and A B), A, B
	 * </pre>
	 * </p><p>
	 * Any necessary node merges will be performed automatically.
	 * </p>
	 *
	 * @param node The node, whose concept set to search for Intersections.
	 * @param branch The branch the node is on.
	 * @param branchQueue The branch queue
	 * @return {@literal true}, if further completion rules should be applied to this node,
	 *	or {@literal false} if the node queue needs to be checked again before applying new rules.
	 * @throws EReasonerException
	 */
	public ReasonerContinuationState completeNode(final IABoxNode<Name, Klass, Role> node,
												  final IDecisionTree.Node<Branch<Name, Klass, Role>> branchNode) throws EReasonerException, EInconsistentABoxException
	{
		try {
			final Branch<Name, Klass, Role> branch = branchNode.getData();
			final IABox<Name, Klass, Role> abox = branch.getABox();
			NodeMergeInfo<Name, Klass, Role> mergeInfo = new NodeMergeInfo<Name, Klass, Role>(node, false);
			
			SortedSet<IDLTerm<Name, Klass, Role>> conceptTerms = node.getTerms().subSet(DLTermOrder.DL_INTERSECTION);
			Iterator<IDLTerm<Name, Klass, Role>> iter = conceptTerms.iterator();
			while (iter.hasNext()) {
				IDLTerm<Name, Klass, Role> term = iter.next();
				if (term instanceof IDLIntersection) {
					IDLIntersection<Name, Klass, Role> intersection = (IDLIntersection<Name, Klass, Role>) term;

					/* if not all subterms are already part of the concept set */
					if ((!node.getTerms().containsAll(intersection))) {
						/* update dependency map */
						for (IDLRestriction<Name, Klass, Role> subTerm : intersection) {
							if (!abox.getDependencyMap().containsKey(node, subTerm))
								abox.getDependencyMap().addParent(node, subTerm, node, intersection);
						}
						/* try to add subterm and its unfolding */
						final NodeMergeInfo<Name, Klass, Role> unfoldResult = node.addUnfoldedDescriptions(intersection);
						mergeInfo.append(unfoldResult);
						getNodeConsistencyChecker().checkConsistency(mergeInfo.getCurrentNode());
						
						if (!mergeInfo.getMergedNodes().isEmpty())
							/**
							 * the current node was merged away, stop processing,
							 * recheck queues
							 **/
							return ReasonerContinuationState.RECHECK_NODE;
						else if (mergeInfo.isModified(node)) {
							/* restart iteration for modified set, prevents ConcurrentModificationException */
							conceptTerms = node.getTerms().subSet(DLTermOrder.DL_INTERSECTION);
							iter = conceptTerms.iterator();
						}
					}
				}
			}
			return ReasonerContinuationState.CONTINUE;
		} catch (ENodeMergeException ex) {
			throw new EInconsistentABoxNodeException(ex.getSourceNode());
		}
	}
}
