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

/**
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public class EReasonerException extends Exception {

	private static final long serialVersionUID = -4086286245682367183L;


	/**
	 * Creates a new instance of
	 * <code>EReasonerException</code> without detail message.
	 */
	public EReasonerException()
	{
	}


	/**
	 * Constructs an instance of
	 * <code>EReasonerException</code> with the specified detail message.
	 *
	 * @param msg the detail message.
	 */
	public EReasonerException(final String msg)
	{
		super(msg);
	}


	/**
	 * Constructs an instance of
	 * <code>ReasonerException$</code> with the specified detail message and wrapping an inner exception.
	 *
	 * @param msg the detail mesage
	 * @param cause the cause of the new exception
	 */
	public EReasonerException(final String msg, final Throwable cause)
	{
		super(msg, cause);
	}


	/**
	 * Constructs an instance of
	 * <code>ReasonerException$</code> wrapping an inner exception.
	 *
	 * @param cause the cause of the new exception
	 */
	public EReasonerException(final Throwable cause)
	{
		super(cause);
	}
}
