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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class EUnsupportedDatatypeException
	extends EDataExpressionException {

	private static final long serialVersionUID = -2730634164429301722L;
	/* the involved datatype */
	private final Object _dataTypeID;


	/**
	 * Creates a new instance of
	 * <code>EUnsupportedDatatypeException</code> without detail message.
	 * 
	 * @param dataTypeID The id of the datatype.
	 */
	public EUnsupportedDatatypeException(final Object dataTypeID)
	{
		_dataTypeID = dataTypeID;
	}


	/**
	 * Constructs an instance of
	 * <code>EUnsupportedDatatypeException</code> with the specified detail message.
	 *
	 * @param dataTypeID the id of the datatype.
	 * @param msg the detail message.
	 */
	public EUnsupportedDatatypeException(final Object dataTypeID, final String msg)
	{
		super(msg);
		_dataTypeID = dataTypeID;
	}


	public Object getDataTypeID()
	{
		return _dataTypeID;
	}
}
