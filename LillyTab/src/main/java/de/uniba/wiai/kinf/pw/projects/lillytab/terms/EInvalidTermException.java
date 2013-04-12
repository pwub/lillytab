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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms;

/**
 * Exception raised, when an invalid term structure is encountered.
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class EInvalidTermException extends RuntimeException {

	private static final long serialVersionUID = -7039639122925059341L;


	/**
	 * Creates a new instance of
	 * <code>EInvalidTermException</code> without detail message.
	 */
	public EInvalidTermException()
	{
	}


	/**
	 * Constructs an instance of
	 * <code>EInvalidTermException</code> with the specified detail message.
	 *
	 * @param msg the detail message.
	 */
	public EInvalidTermException(final String msg)
	{
		super(msg);
	}


	/**
	 * Constructs an instance of
	 * <code>EInvalidTermException$</code> with the specified detail message and wrapping an inner exception.
	 *
	 * @param msg the detail mesage
	 * @param cause the cause of the new exception
	 */
	public EInvalidTermException(final String msg, final Throwable cause)
	{
		super(msg, cause);
	}


	/**
	 * Constructs an instance of
	 * <code>EInvalidTermException$</code> wrapping an inner exception.
	 *
	 * @param cause the cause of the new exception
	 */
	public EInvalidTermException(final Throwable cause)
	{
		super(cause);
	}
}
