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

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.Branch;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.EInconsistentABoxNodeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.INodeConsistencyChecker;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * <p>
 * A {@link IBranchAction} that adds a new concept to a node.
 * </p>
 *
 * @param <Name>
 * @param <Klass>
 * @param <Role>
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ConceptAddBranchAction<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends AbstractBranchAction<Name, Klass, Role>
	implements IBranchAction<Name, Klass, Role>
{
	private final Set<IDLRestriction<Name, Klass, Role>> _descriptions;

	public ConceptAddBranchAction(final TermEntry<Name, Klass, Role> parentEntry,
		final IABoxNode<Name, Klass, Role> targetNode, final IDLRestriction<Name, Klass, Role> description)
	{
		this(parentEntry, targetNode, Collections.singleton(description));

	}

	public ConceptAddBranchAction(final TermEntry<Name, Klass, Role> parentEntry, NodeID targetNodeID, IDLRestriction<Name, Klass, Role> description)
	{	
		this(parentEntry, targetNodeID, Collections.singleton(description));

	}

	public ConceptAddBranchAction(final TermEntry<Name, Klass, Role> parentEntry, NodeID targetNodeID, Collection<IDLRestriction<Name, Klass, Role>> descriptions)
	{
		super(parentEntry, targetNodeID);
		_descriptions = new TreeSet<IDLRestriction<Name, Klass, Role>>(descriptions);
	}

	public ConceptAddBranchAction(final TermEntry<Name, Klass, Role> parentEntry, IABoxNode<Name, Klass, Role> targetNode, Collection<IDLRestriction<Name, Klass, Role>> descriptions)
	{
		this(parentEntry, targetNode.getNodeID(), descriptions);
	}

	public NodeMergeInfo<Name, Klass, Role> commit(final Branch<Name, Klass, Role> branch)
		throws EInconsistentABoxNodeException
	{

		final NodeID targetNodeID = getTargetNodeID();
		final IABox<Name, Klass, Role> abox = branch.getABox();
		assert abox.getNodeMap().containsKey(targetNodeID);
		final IABoxNode<Name, Klass, Role> currentNode = abox.getNode(targetNodeID);
		
		/* update dependency map */
		for (IDLRestriction<Name, Klass, Role> desc: _descriptions) {
			if (! branch.getABox().getDependencyMap().containsKey(currentNode, desc)) {
				abox.getDependencyMap().addParent(currentNode.getNodeID(), desc, getParentEntry());
				/* update governing list */
				abox.getDependencyMap().addGoverningTerm(currentNode, desc);
			}
		}

		try {
			final NodeMergeInfo<Name, Klass, Role> mergeInfo = currentNode.addUnfoldedDescriptions(_descriptions);
			return mergeInfo;
		} catch (ENodeMergeException ex) {
			throw new EInconsistentABoxNodeException(currentNode);
		}
	}

	/**
	 * @return The set of new descriptions
	 */
	public Set<IDLRestriction<Name, Klass, Role>> getDescriptions()
	{
		return _descriptions;
	}

	public boolean isShouldCommit(Branch<Name, Klass, Role> branch,
								  INodeConsistencyChecker<Name, Klass, Role> cChecker)
	{
		final NodeID targetNodeID = getTargetNodeID();
		assert branch.getABox().getNodeMap().containsKey(targetNodeID);
		final IABoxNode<Name, Klass, Role> currentNode = branch.getABox().getNode(targetNodeID);
		if (currentNode.getTerms().containsAll(_descriptions))
			/* no commit, if nothing is changed */
			return false;
		else
			return cChecker.isExtraConsistent(branch.getABox(), currentNode.getTerms(), _descriptions);
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("BranchAction: Add concepts ");
		sb.append(getDescriptions());
		sb.append(" to node ");
		sb.append(getTargetNodeID());
		return sb.toString();
	}


}
