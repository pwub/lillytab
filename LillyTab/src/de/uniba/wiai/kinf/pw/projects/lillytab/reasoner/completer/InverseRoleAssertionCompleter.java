/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer;

import de.dhke.projects.cutil.collections.tree.IDecisionTree.Node;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IRABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.Branch;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EReasonerException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.INodeConsistencyChecker;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ReasonerContinuationState;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IRBox;

/**
 *
 * @param <Name> The type for nominals and values
 * @param <Klass> The type for DL classes
 * @param <Role> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class InverseRoleAssertionCompleter<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends AbstractCompleter<Name, Klass, Role> {

	public InverseRoleAssertionCompleter(final INodeConsistencyChecker<Name, Klass, Role> cChecker, final boolean trace)
	{
		super(cChecker, trace);
	}


	public InverseRoleAssertionCompleter(final INodeConsistencyChecker<Name, Klass, Role> cChecker)
	{
		this(cChecker, false);
	}


	@Override
	public ReasonerContinuationState completeNode(IABoxNode<Name, Klass, Role> node,
												  Node<Branch<Name, Klass, Role>> branchNode) throws EReasonerException
	{
		final IABox<Name, Klass, Role> abox = node.getABox();
		final IRABox<Name, Klass, Role> raBox = node.getRABox();
		final IRBox<Name, Klass, Role> rbox = abox.getTBox().getRBox();

		boolean hasChanged = false;

		for (Role outRole : raBox.getAssertedSuccessors().keySet()) {
			for (Role inverseRole : rbox.getInverseRoles(outRole)) {
				for (NodeID succID : raBox.getAssertedSuccessors().get(outRole)) {
					final IABoxNode<Name, Klass, Role> succ = abox.getNode(succID);
					assert succ != null;
					// add back link */
					if (! succ.getRABox().hasSuccessor(inverseRole, node)) {
						succ.getRABox().getAssertedSuccessors().put(inverseRole, node);
						branchNode.getData().touchNode(node);
						branchNode.getData().touch(succID);
						hasChanged = true;
					}
				}
			}
		}
		if (hasChanged)
			return ReasonerContinuationState.RECHECK_NODE;
		else
			return ReasonerContinuationState.CONTINUE;
	}
}
