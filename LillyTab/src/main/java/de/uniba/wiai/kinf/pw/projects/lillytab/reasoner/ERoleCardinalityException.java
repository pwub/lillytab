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

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;

/**
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public class ERoleCardinalityException
	extends EInconsistentABoxNodeException {

	private static final long serialVersionUID = 4746925830498913470L;
	private Object _role;


	/**
	 * Creates a new instance of
	 * <code>ERoleCardinalityException</code> without detail message.
	 * 
	 * @param node The affected node
	 * @param role The affected property/role
	 */
	public ERoleCardinalityException(final IABoxNode<?, ?, ?, ?> node, final Object role)
	{
		super(node);
		_role = role;
	}


	/**
	 * Constructs an instance of
	 * <code>ERoleCardinalityException</code> with the specified detail message.
	 *
	 * @param node The affected node
	 * @param role The affected property/role
	 * @param msg the detail message.
	 */
	public ERoleCardinalityException(final IABoxNode<?, ?, ?, ?> node, final Object role, final String msg)
	{
		super(node, msg);
		_role = role;
	}


	public Object getRole()
	{
		return _role;
	}
}
