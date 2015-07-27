/**
 * (c) 2009-2014 Otto-Friedrich-University Bamberg
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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentABoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class EInconsistentABoxNodeException
	extends EInconsistentABoxException {

	private static final long serialVersionUID = 5797316829722764432L;
	private final IABoxNode<?, ?, ?, ?> _node;


	/**
	 * Creates a new instance of
	 * <code>EInconsistentABoxNodeException</code> without detail message.
	 * 
	 * @param node The affected node
	 */
	public EInconsistentABoxNodeException(final IABoxNode<?, ?, ?, ?> node)
	{
		super(node.getABox());
		_node = node;
	}


	/**
	 * Constructs an instance of
	 * <code>EInconsistentABoxNodeException</code> with the specified detail message.
	 *
	 * @param node The affected node
	 * @param msg the detail message.
	 */
	public EInconsistentABoxNodeException(final IABoxNode<?, ?, ?, ?> node, String msg)
	{
		super(node.getABox(), msg);
		_node = node;
	}


	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(getClass());
		sb.append(": ");
		sb.append("at node ");
		sb.append(getABoxNode().getNodeID());
		return sb.toString();
	}


	IABoxNode<?, ?, ?, ?> getABoxNode()
	{
		return _node;
	}
}
