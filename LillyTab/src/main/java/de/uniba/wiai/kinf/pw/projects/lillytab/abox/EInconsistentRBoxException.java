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

import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IAssertedRBox;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class EInconsistentRBoxException
	extends EInconsistencyException {
	private static final long serialVersionUID = -3077859787691729338L;

	final IAssertedRBox<?, ?, ?, ?> _rbox;


	/**
	 * Creates a new instance of
	 * <code>EInconsistentABoxException</code> without detail message.
	 * 
	 * @param rbox The affected rbox
	 */
	public EInconsistentRBoxException(final IAssertedRBox<?, ?, ?, ?> rbox)
	{
		_rbox = rbox;
	}


	/**
	 * Constructs an instance of
	 * <code>EInconsistentABoxException</code> with the specified detail message.
	 *
	 * @param rbox The affected rbox
	 * @param msg the detail message.
	 */
	public EInconsistentRBoxException(final IAssertedRBox<?, ?, ?, ?> rbox, String msg)
	{
		super(msg);
		_rbox = rbox;
	}

	public IAssertedRBox<?, ?, ?, ?> getRBox()
	{
		return _rbox;
	}
}
