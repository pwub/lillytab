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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.ITerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IUnaryOperator;

/**
 *
 * @param <Term> The type of the wrapped term.
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class AbstractUnaryOperator<Term extends ITerm>
	extends AbstractOperatorTerm<Term>
	implements IUnaryOperator<Term>
{
	public AbstractUnaryOperator(final DLTermOrder termOrder, final String operatorName, final Term term)
	{
		super(termOrder, operatorName, 1);
		getModifiableTermList().set(0, term);
	}

	public AbstractUnaryOperator(final DLTermOrder termOrder, final String operatorName)
	{
		super(termOrder, operatorName, 1);
	}

	public Term getTerm()
	{
		return get(0);
	}
}
