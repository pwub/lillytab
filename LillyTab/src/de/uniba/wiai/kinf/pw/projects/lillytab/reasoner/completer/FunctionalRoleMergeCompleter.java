/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer;

import de.dhke.projects.cutil.collections.tree.IDecisionTree.Node;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IRABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.Branch;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EReasonerException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.INodeConsistencyChecker;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ReasonerContinuationState;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleProperty;
import java.util.Iterator;


/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class FunctionalRoleMergeCompleter<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends AbstractCompleter<Name, Klass, Role>
{
	public FunctionalRoleMergeCompleter(final INodeConsistencyChecker<Name, Klass, Role> cChecker, final boolean trace)
	{
		super(cChecker, trace);
	}

	public FunctionalRoleMergeCompleter(final INodeConsistencyChecker<Name, Klass, Role> cChecker)
	{
		this(cChecker, false);
	}

	@Override
	public ReasonerContinuationState completeNode(IABoxNode<Name, Klass, Role> node,
												  Node<Branch<Name, Klass, Role>> branchNode) throws EReasonerException
	{
		try {
			final IABox<Name, Klass, Role> abox = node.getABox();
			final IRBox<Name, Klass, Role> rbox = abox.getTBox().getRBox();
			final IRABox<Name, Klass, Role> raBox = node.getRABox();
			for (final Role outRole : raBox.getOutgoingRoles()) {
				if (rbox.hasRoleProperty(outRole, RoleProperty.FUNCTIONAL)) {
					Iterator<IABoxNode<Name, Klass, Role>> succIter = raBox.getSuccessorNodes(outRole).iterator();
					assert succIter.hasNext();
					IABoxNode<Name, Klass, Role> firstNode = succIter.next();
					while (succIter.hasNext()) {
						final IABoxNode<Name, Klass, Role> nextNode = succIter.next();
						final NodeMergeInfo<Name, Klass, Role> mergeInfo = abox.mergeNodes(nextNode, firstNode);
						branchNode.getData().touchNode(mergeInfo.getCurrentNode());

						succIter = raBox.getSuccessorNodes(outRole).iterator();
						firstNode = succIter.next();
					}
				}
			}

			for (final Role inRole : raBox.getIncomingRoles()) {
				if (rbox.hasRoleProperty(inRole, RoleProperty.INVERSE_FUNCTIONAL)) {
					Iterator<IABoxNode<Name, Klass, Role>> predIter = raBox.getPredecessorNodes(inRole).iterator();
					assert predIter.hasNext();
					IABoxNode<Name, Klass, Role> firstNode = predIter.next();
					while (predIter.hasNext()) {
						final IABoxNode<Name, Klass, Role> nextNode = predIter.next();
						final NodeMergeInfo<Name, Klass, Role> mergeInfo = abox.mergeNodes(nextNode, firstNode);
						branchNode.getData().touchNode(mergeInfo.getCurrentNode());

						predIter = raBox.getPredecessorNodes(inRole).iterator();
						firstNode = predIter.next();
					}
				}
			}

			return ReasonerContinuationState.RECHECK_NODE;
		} catch (EInconsistencyException ex) {
			return ReasonerContinuationState.INCONSISTENT;
		}
	}
}
