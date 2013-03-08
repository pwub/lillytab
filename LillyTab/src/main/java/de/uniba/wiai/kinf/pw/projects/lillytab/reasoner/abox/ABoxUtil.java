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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox;

import de.dhke.projects.cutil.collections.iterator.MultiMapEntryIterable;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentABoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import java.util.Map;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ABoxUtil {

	private ABoxUtil()
	{
	}


	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> boolean isLinkConsistent(
		final IABox<Name, Klass, Role> abox)
	{
		for (final IABoxNode<Name, Klass, Role> node : abox) {
			for (final Map.Entry<Role, NodeID> predEntry : MultiMapEntryIterable.decorate(node.getRABox().
				getAssertedPredecessors())) {
				final IABoxNode<Name, Klass, Role> pred = abox.getNode(predEntry.getValue());
				if (pred == null) {
					return false;
				}
				if (!pred.getRABox().getAssertedSuccessors().containsValue(predEntry.getKey(), node.getNodeID())) {
					return false;
				}
			}

			for (final Map.Entry<Role, NodeID> succEntry : MultiMapEntryIterable.decorate(node.getRABox().
				getAssertedSuccessors())) {
				final IABoxNode<Name, Klass, Role> succ = abox.getNode(succEntry.getValue());
				if (succ == null) {
					return false;
				}
				if (!succ.getRABox().getAssertedPredecessors().containsValue(succEntry.getKey(), node.getNodeID())) {
					return false;
				}
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
			for (final Map.Entry<Role, NodeID> predEntry : MultiMapEntryIterable.decorate(node.getRABox().
				getAssertedPredecessors())) {
				final IABoxNode<Name, Klass, Role> pred = abox.getNode(predEntry.getValue());
				if (pred == null) {
					throw new EInconsistentABoxException(abox, String.format("node with ID `%s' not part of abox",
																			 nodeID));
				}
				if (!pred.getRABox().getAssertedSuccessors().containsValue(predEntry.getKey(), nodeID)) {
					throw new EInconsistentABoxException(abox, String.format(
						"node `%s' does not assert `%s'-back-link to `%s'", predEntry.getValue(), predEntry.getKey(),
						node));
				}
			}

			for (final Map.Entry<Role, NodeID> succEntry : MultiMapEntryIterable.decorate(node.getRABox().
				getAssertedSuccessors())) {
				final IABoxNode<Name, Klass, Role> succ = abox.getNode(succEntry.getValue());
				if (succ == null) {
					throw new EInconsistentABoxException(abox, String.format("node with ID `%s' not part of abox",
																			 nodeID));
				}
				if (!succ.getRABox().getAssertedPredecessors().containsValue(succEntry.getKey(), nodeID)) {
					throw new EInconsistentABoxException(abox, String.format(
						"node `%s' does not assert `%s'-predecessor back-link to `%s'", succEntry.getValue(), succEntry.
						getKey(),
						node));
				}
			}
		}
	}


	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>> void checkNodeMapConsistency(
		final IABox<Name, Klass, Role> abox)
		throws EInconsistentABoxException
	{

		for (Map.Entry<Object, IABoxNode<Name, Klass, Role>> me : abox.getNodeMap().entrySet()) {
			final IABoxNode<Name, Klass, Role> node = me.getValue();
			if (!abox.equals(node.getABox())) {
				throw new EInconsistentABoxException(abox, String.format(
					"Node map entry %s links node outside of ABox: %s", me.getKey(), node));
			}
			if (me.getKey() instanceof NodeID) {
				if (!me.getKey().equals(node.getNodeID())) {
					throw new EInconsistentABoxException(abox, String.format(
						"NodeID %s does not link back to node with same ID: %s", me.getKey(), node));
				}
			} else if (!node.getNames().contains(me.getKey())) {
				throw new EInconsistentABoxException(abox, String.format(
					"NodeID name %s does not link back to node with same name: %s", me.getKey(), node));
			}
		}
	}
}
