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
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IOperatorTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.visitor.IDLTermVisitor;


/**
 * 
 * Abstract implementation of an DL operator.
 * <p />
 * Operators inherit from {@link AbstractFixedTermList} and can thus only be modified via the internal backend list
 * available via {@link AbstractFixedTermList#getModifiableTermList()
 * }.
 * 
 *
 *
 * @param <Term> The type of the contained terms.
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public abstract class AbstractDLOperatorTerm<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>, Term extends IDLTerm<I, L, K, R>>
	extends AbstractFixedTermList<Term>
	implements IOperatorTerm<Term>, IDLTerm<I, L, K, R>
{
	private final String _operatorName;
	private final DLTermOrder _dlTermOrder;
	
	protected AbstractDLOperatorTerm(final DLTermOrder termOrder, final String operatorName, final int size)
	{
		super(size);
		_operatorName = operatorName;
		_dlTermOrder = termOrder;
	}
	
	@Override
	public String getOperatorName()
	{
		return _operatorName;
	}
	
	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj) {
			return true;
		}
		
		return ((obj instanceof IOperatorTerm)
			&& getOperatorName().equals(((IOperatorTerm) obj).getOperatorName())
			&& super.equals(obj));
	}
	
	@Override
	public int hashCode()
	{
		int hCode = getOperatorName().hashCode();
		return hCode + super.hashCode();
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(getOperatorName());
		for (Term term : this) {
			sb.append(" ");
			sb.append(term.toString());
		}
		sb.append(")");
		return sb.toString();
	}
	
	@Override
	public DLTermOrder getDLTermOrder()
	{
		return _dlTermOrder;
	}
	
	@Override
	public abstract IDLTerm<I, L, K, R> clone();
	
	@Override
	public void accept(
		IDLTermVisitor<I, L, K, R> visitor)
	{
		visitor.visit(this);
		visitor.visitEnter(this);
		for (Term term : this) {
			term.accept(visitor);
		}
		visitor.visitLeave(this);
	}
}
