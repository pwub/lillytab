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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IUnaryOperator;

/**
 *
 * @param <Term> The type of the wrapped term.
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public abstract class AbstractDLUnaryOperator<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>, Term extends IDLTerm<I, L, K, R>>
	extends AbstractDLOperatorTerm<I, L, K, R, Term>
	implements IUnaryOperator<Term> {

	public AbstractDLUnaryOperator(final DLTermOrder termOrder, final String operatorName, final Term term)
	{
		super(termOrder, operatorName, 1);
		getModifiableTermList().set(0, term);
	}


	public AbstractDLUnaryOperator(final DLTermOrder termOrder, final String operatorName)
	{
		super(termOrder, operatorName, 1);
	}
	
	@Override
	public Term getTerm()
	{
		return get(0);
	}
}
