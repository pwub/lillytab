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
package de.uniba.wiai.kinf.pw.projects.lillytab.abox;

/**
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public class EInconsistentABoxException
	extends EInconsistencyException {

	static final long serialVersionUID = 7993354832275434864L;
	final IABox<?, ?, ?, ?> _abox;


	/**
	 * Creates a new instance of
	 * <code>EInconsistentABoxException</code> without detail message.
	 * 
	 * @param abox The affected abox.
	 */
	public EInconsistentABoxException(final IABox<?, ?, ?, ?> abox)
	{
		_abox = abox;
	}


	/**
	 * Constructs an instance of
	 * <code>EInconsistentABoxException</code> with the specified detail message.
	 *
	 * @param abox The affected abox.
	 * @param msg the detail message.
	 */
	public EInconsistentABoxException(final IABox<?, ?, ?, ?> abox, String msg)
	{
		super(msg);
		_abox = abox;
	}

	public IABox<?, ?, ?, ?> getABox()
	{
		return _abox;
	}
}
