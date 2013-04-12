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
package  de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl;
 
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLDummyTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLLiteralReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.util.IToStringFormatter;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class DLLiteralReference<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	implements IDLLiteralReference<I, L, K, R> {

	private final L _literal;


	protected DLLiteralReference(final L literal)
	{
		_literal = literal;
	}


	@Override
	public L getLiteral()
	{
		return _literal;
	}


	@Override
	public DLTermOrder getDLTermOrder()
	{
		return DLTermOrder.DL_LITERAL_REFERENCE;
	}


	@Override
	public IDLLiteralReference<I, L, K, R> clone()
	{
		return this;
	}


	@Override
	public int compareTo(final IDLTerm<I, L, K, R> o)
	{
		int compare = getDLTermOrder().compareTo(o);
		if (compare == 0) {
			assert o instanceof IDLLiteralReference;
			IDLLiteralReference<I, L, K, R> otherNRef = (IDLLiteralReference<I, L, K, R>) o;
			compare = getLiteral().compareTo(otherNRef.getLiteral());
		}
		return compare;
	}


	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(_literal.toString());
		sb.append("}");
		return sb.toString();
	}


	@Override
	public String toString(IToStringFormatter entityFormatter)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(entityFormatter.toString(_literal));
		sb.append("}");
		return sb.toString();
	}


	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		} else {
			return ((obj instanceof IDLLiteralReference)
				&& getLiteral().equals(((IDLLiteralReference<?, ?, ?, ?>) obj).getLiteral()));
		}
	}


	@Override
	public int hashCode()
	{
		return getLiteral().hashCode();
	}


	@Override
	public IDLTerm<I, L, K, R> getBefore()
	{
		return new DLDummyTerm<>(DLTermOrder.DL_BEFORE_LITERAL_REFERENCE);
	}


	@Override
	public IDLTerm<I, L, K, R> getAfter()
	{
		return new DLDummyTerm<>(DLTermOrder.DL_AFTER_LITERAL_REFERENCE);
	}
}
