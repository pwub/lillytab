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
package de.uniba.wiai.kinf.pw.projects.lillytab.abox;

import de.dhke.projects.lutil.LoggingClass;
import java.util.Collection;
import java.util.Map;

/**
 * <p>
 * </p>
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 * 
 * @param <Name> The type for named individuals.
 * @param <Klass> Type for class names
 * @param <Role> Type for role names
 */
public abstract class AbstractAboxNode<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends LoggingClass
	implements IABoxNode<Name, Klass, Role> {

	/**
	 * The (unmodifiable) node ID of this node.
	 **/
	private final NodeID _id;
	/**
	 * if the current node is a datatype node
	 **/
	private final boolean _isDatatypeNode;

	/**
	 * <p>
	 * Create a new, anonymous ABox node with the given id.
	 * </p>
	 *
	 * @param id The id of the new anonymous node.
	 * @param isDatatypeNode If the new node should be a datatype node.
	 */
	protected AbstractAboxNode(final int id, final boolean isDatatypeNode)
	{
		_id = new NodeID(id);
		_isDatatypeNode = isDatatypeNode;
	}

	/**
	 * <p>
	 * Create a new, anonymous ABox node with the given id.
	 * </p>
	 *
	 * @param id The id of the new anonymous node.
	 * @param isDatatypeNode If the new node should be a datatype node.
	 */
	protected AbstractAboxNode(final NodeID id, final boolean isDatatypeNode)
	{
		_id = id;
		_isDatatypeNode = isDatatypeNode;
	}

	/**
	 *
	 * @return The {@link NodeID} of the current node.
	 */
	public NodeID getNodeID()
	{
		return _id;
	}

	/**
	 *
	 * @return {@literal true} if the current node is a datatype node
	 * and cannot have successors.
	 */
	public boolean isDatatypeNode()
	{
		return _isDatatypeNode;
	}

	public boolean isAnonymous()
	{
		return getNames().isEmpty();
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		if ((obj instanceof IABoxNode) && (obj.getClass().isInstance(this))) {
			IABoxNode other = (IABoxNode) obj;
			return getNodeID().equals(other.getNodeID());
		} else
			return false;
	}

	@Override
	public int hashCode()
	{
		return _id.hashCode();
	}

	public int compareTo(final IABoxNode<Name, Klass, Role> o)
	{
		assert o != null;
		if (isAnonymous()) {
			/* this is anonymous */
			if (o.isAnonymous())
				return getNodeID().compareTo(o.getNodeID());
			else
				return 1;
		} else {
			/* this is named */
			if (o.isAnonymous())
				return -1;
			else {
				/* Since the primary name is subject to change, we compare by ID here, too */
				return getNodeID().compareTo(o.getNodeID());
			}
		}
	}

	public String toString(final String prefix)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getNodeID().toString());
		sb.append(": names: ");
		sb.append(getNames().toString());
		sb.append(", terms: ");
		sb.append(getTerms().toString());
//		if (isBlocked()) {
//			sb.append(" (blocked by ");
//			sb.append(getBlocker().getNodeID().toString());
//			sb.append(")");
//		}
		sb.append("\n");
		sb.append(prefix);
		sb.append("\tlinks: [");
		for (Map.Entry<Role, Collection<NodeID>> sEntry : getSuccessors().entrySet()) {
			sb.append("(");
			Role role = sEntry.getKey();
			sb.append(role);

			sb.append(" -> " + sEntry.getValue().toString() + ")");
		}

		sb.append("]\n");
		return sb.toString();
	}
}
