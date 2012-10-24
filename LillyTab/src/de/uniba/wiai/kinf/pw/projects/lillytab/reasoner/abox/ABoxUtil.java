/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox;

import de.dhke.projects.cutil.collections.iterator.MultiMapItemIterable;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentABoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import java.util.Map;


/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ABoxUtil
{
	private ABoxUtil()
	{
	}

	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> boolean isLinkConsistent(
		final IABox<Name, Klass, Role> abox)
	{
		for (final IABoxNode<Name, Klass, Role> node : abox) {
			for (final Map.Entry<Role, NodeID> predEntry : MultiMapItemIterable.decorate(node.getRABox().
				getAssertedPredecessors())) {
				final IABoxNode<Name, Klass, Role> pred = abox.getNode(predEntry.getValue());
				if (pred == null)
					return false;
				if (!pred.getRABox().getAssertedSuccessors().containsValue(predEntry.getKey(), node.getNodeID()))
					return false;
			}

			for (final Map.Entry<Role, NodeID> succEntry : MultiMapItemIterable.decorate(node.getRABox().
				getAssertedSuccessors())) {
				final IABoxNode<Name, Klass, Role> succ = abox.getNode(succEntry.getValue());
				if (succ == null)
					return false;
				if (!succ.getRABox().getAssertedPredecessors().containsValue(succEntry.getKey(), node.getNodeID()))
					return false;
			}
		}
		return true;
	}

	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> void checkLinkConsistency(
		final IABox<Name, Klass, Role> abox)
		throws EInconsistentABoxException
	{
		for (final IABoxNode<Name, Klass, Role> node : abox) {
			final NodeID nodeID = node.getNodeID();
			for (final Map.Entry<Role, NodeID> predEntry : MultiMapItemIterable.decorate(node.getRABox().
				getAssertedPredecessors())) {
				final IABoxNode<Name, Klass, Role> pred = abox.getNode(predEntry.getValue());
				if (pred == null)
					throw new EInconsistentABoxException(abox, String.format("node with ID `%s' not part of abox", nodeID));
				if (!pred.getRABox().getAssertedSuccessors().containsValue(predEntry.getKey(), nodeID))
					throw new EInconsistentABoxException(abox, String.format(
						"node `%s' does not assert `%s'-back-link to `%s'", predEntry.getValue(), predEntry.getKey(),
						node));
			}

			for (final Map.Entry<Role, NodeID> succEntry : MultiMapItemIterable.decorate(node.getRABox().
				getAssertedSuccessors())) {
				final IABoxNode<Name, Klass, Role> succ = abox.getNode(succEntry.getValue());
				if (succ == null)
					throw new EInconsistentABoxException(abox, String.format("node with ID `%s' not part of abox", nodeID));
				if (!succ.getRABox().getAssertedPredecessors().containsValue(succEntry.getKey(), nodeID))
					throw new EInconsistentABoxException(abox, String.format(
						"node `%s' does not assert `%s'-predecessor back-link to `%s'", succEntry.getValue(), succEntry.
						getKey(),
						node));
			}
		}

	}
}
