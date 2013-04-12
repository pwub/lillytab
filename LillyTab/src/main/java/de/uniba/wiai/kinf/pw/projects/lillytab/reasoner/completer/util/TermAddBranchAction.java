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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.util;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.Branch;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.INodeConsistencyChecker;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * A {@link IBranchAction} that adds a new concept to a node.
 *
 * @param <I> The type for nominals and values
 * @param <K> The type for DL classes
 * @param <R> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class TermAddBranchAction<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	extends AbstractBranchAction<I, L, K, R>
	implements IBranchAction<I, L, K, R> {

	private final Set<IDLRestriction<I, L, K, R>> _descriptions;


	public TermAddBranchAction(final TermEntry<I, L, K, R> parentEntry,
							   final IABoxNode<I, L, K, R> targetNode,
							   final IDLRestriction<I, L, K, R> description)
	{
		this(parentEntry, targetNode, Collections.singleton(description));

	}


	public TermAddBranchAction(final TermEntry<I, L, K, R> parentEntry, NodeID targetNodeID,
							   IDLRestriction<I, L, K, R> description)
	{
		this(parentEntry, targetNodeID, Collections.singleton(description));

	}


	public TermAddBranchAction(final TermEntry<I, L, K, R> parentEntry, NodeID targetNodeID,
							   Collection<IDLRestriction<I, L, K, R>> descriptions)
	{
		super(parentEntry, targetNodeID);
		_descriptions = new TreeSet<>(descriptions);
	}


	public TermAddBranchAction(final TermEntry<I, L, K, R> parentEntry,
							   IABoxNode<I, L, K, R> targetNode,
							   Collection<IDLRestriction<I, L, K, R>> descriptions)
	{
		this(parentEntry, targetNode.getNodeID(), descriptions);
	}


	@Override
	public NodeMergeInfo<I, L, K, R> commit(final Branch<I, L, K, R> branch)
		throws ENodeMergeException
	{

		final NodeID targetNodeID = getTargetNodeID();
		final IABox<I, L, K, R> abox = branch.getABox();
		assert abox.getNodeMap().containsKey(targetNodeID);
		final IABoxNode<I, L, K, R> currentNode = abox.getNode(targetNodeID);
	
		/* update dependency map */
		for (IDLRestriction<I, L, K, R> desc : _descriptions) {
			if (!branch.getABox().getDependencyMap().containsKey(currentNode, desc)) {
				// do NOT track parent relationship across OR branching.
				// abox.getDependencyMap().addParent(currentNode.getNodeID(), desc, getParentEntry());

				/**
				 * Add the newly added term to the list of governing terms if its an atomic term.
				 */
				abox.getDependencyMap().addGoverningTerm(currentNode, desc);
			}
		}

		final NodeMergeInfo<I, L, K, R> mergeInfo = currentNode.addTerms(_descriptions);
		return mergeInfo;
	}


	/**
	 * @return The set of new descriptions
	 */
	public Set<IDLRestriction<I, L, K, R>> getDescriptions()
	{
		return Collections.unmodifiableSet(_descriptions);
	}


	@Override
	public boolean isShouldCommit(Branch<I, L, K, R> branch,
								  INodeConsistencyChecker<I, L, K, R> cChecker)
	{
		final NodeID targetNodeID = getTargetNodeID();
		assert branch.getABox().getNodeMap().containsKey(targetNodeID);
		final IABoxNode<I, L, K, R> currentNode = branch.getABox().getNode(targetNodeID);
		if (currentNode.getTerms().containsAll(_descriptions)) /* no commit, if nothing is changed */ {
			return false;
		} else {
			return !cChecker.isExtraConsistent(branch.getABox(), currentNode, _descriptions).isFinallyInconsistent();
		}
	}


	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("BranchAction: Add restrictions ");
		sb.append(getDescriptions());
		sb.append(" to node ");
		sb.append(getTargetNodeID());
		return sb.toString();
	}
}
