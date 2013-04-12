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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;


/**
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class EIllegalTermTypeException
	extends IllegalArgumentException
{
	private static final long serialVersionUID = 8056229236386543851L;
	private final IDLTerm<?, ?, ?, ?> _term;	


	/**
	 * Creates a new instance of <code>EIllegalTermTypeException</code> without detail message.
	 * 
	 * @param term The conflicting term
	 */
	public EIllegalTermTypeException(final IDLTerm<?, ?, ?, ?> term)
	{
		_term = term;
	}

	/**
	 * Constructs an instance of <code>EIllegalTermTypeException</code> with the specified detail message.
	 * @param term The conflicting term
	 * @param msg the detail message.
	 */
	public EIllegalTermTypeException(final IDLTerm<?, ?, ?, ?> term, final String msg)
	{
		super(msg);
		_term = term;
	}

	public IDLTerm<?, ?, ?, ?> getTerm()
	{
		return _term;
	}
}
