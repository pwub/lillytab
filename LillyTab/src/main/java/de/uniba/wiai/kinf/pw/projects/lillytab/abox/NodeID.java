/**
 * (c) 2009-2013 Otto-Friedrich-University Bamberg
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

/**
 * 
 * Representation of the node ID of an {@link IABoxNode}.
 * <p />
 * This is basically a replacable wrapper around an integer serial number.
 * 
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public final class NodeID
	implements Comparable<NodeID> {

	private final int _id;


	public NodeID(int id)
	{
		_id = id;
	}


	public int getID()
	{
		return _id;
	}


	@Override
	public int compareTo(final NodeID o)
	{
		return getID() - o.getID();
	}


	@Override
	public int hashCode()
	{
		return 13 + _id * 23;
	}


	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj) {
			return true;
		}
		return ((obj instanceof NodeID) && obj.getClass().isInstance(this) && (((NodeID) obj)._id == _id));
	}


	@Override
	public String toString()
	{
		return String.valueOf(_id);
	}
}
