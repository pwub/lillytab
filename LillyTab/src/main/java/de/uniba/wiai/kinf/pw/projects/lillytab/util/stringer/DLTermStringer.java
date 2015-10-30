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
package de.uniba.wiai.kinf.pw.projects.lillytab.util.stringer;

import de.dhke.projects.cutil.stringer.AbstractAnnotationStringer;
import de.dhke.projects.cutil.stringer.IToStringConverter;
import de.dhke.projects.cutil.stringer.SupportsType;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLIndividualReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLLiteralReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRoleOperator;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IOperatorTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.visitor.IDLTermVisitor;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLProperty;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
@SupportsType({IDLTerm.class, TermEntry.class})
public class DLTermStringer
	extends AbstractAnnotationStringer {


	public DLTermStringer()
	{
	}


	@Override
	@SuppressWarnings("unchecked")
	public void append(StringBuilder sb, Object obj, final IToStringConverter backStringer)
	{
		if (obj instanceof IDLTerm) {
			final IDLTerm term = (IDLTerm) obj;
			final Stringer stringer = new Stringer(sb, backStringer);
			term.accept(stringer);
		} else if (obj instanceof TermEntry) {
			final TermEntry<?, ?, ?, ?> entry = (TermEntry<?, ?, ?, ?>) obj;
			sb.append("<");
			backStringer.append(sb, entry.getNodeID(), backStringer);
			sb.append(", ");
			backStringer.append(sb, entry.getTerm(), backStringer);
			sb.append(">");
		} else {
			backStringer.append(sb, obj, backStringer);
		}
	}

	class Stringer<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
		implements IDLTermVisitor<I, L, K, R> {

		final StringBuilder _sb;
		final IToStringConverter _backStringer;
		boolean lastWasTerm = false;


		Stringer(final StringBuilder target, final IToStringConverter backStringer)
		{
			_sb = target;
			_backStringer = backStringer;
		}


		Stringer(final IToStringConverter backStringer)
		{
			_sb = new StringBuilder();
			_backStringer = backStringer;
		}


		@Override
		public void visitEnter(IDLTerm<I, L, K, R> term)
		{
			lastWasTerm = false;
			_sb.append("(");
			if (term instanceof IOperatorTerm) {
				final IOperatorTerm<?> opTerm = (IOperatorTerm<?>) term;
				_sb.append(opTerm.getOperatorName());
				_sb.append(" ");
				if (term instanceof IDLRoleOperator) {
					@SuppressWarnings("unchecked")
					final IDLRoleOperator<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> roleOp =
						(IDLRoleOperator<OWLIndividual, OWLLiteral, OWLClass, OWLProperty>) opTerm;
					for (OWLProperty role : roleOp.getRoles()) {
						_backStringer.append(_sb, role, _backStringer);
						_sb.append(" ");
					}
				}
			}
		}


		@Override
		public void visit(final IDLTerm<I, L, K, R> term)
		{
			if (lastWasTerm)
				_sb.append(" ");
			lastWasTerm = true;

			if (term instanceof IDLClassReference) {
				final IDLClassReference<I, L, K, R> clsRef = (IDLClassReference<I, L, K, R>) term;
				_backStringer.append(_sb, clsRef.getElement(), _backStringer);
			} else if (term instanceof IDLLiteralReference) {
				final IDLLiteralReference<I, L, K, R> litRef = (IDLLiteralReference<I, L, K, R>) term;
				_backStringer.append(_sb, litRef.getLiteral(), _backStringer);
			} else if (term instanceof IDLIndividualReference) {
				final IDLIndividualReference<I, L, K, R> indRef = (IDLIndividualReference<I, L, K, R>) term;
				_backStringer.append(_sb, indRef.getIndividual(), _backStringer);
			} else if (term instanceof IAtom) {
				_sb.append(term);
			}
		}


		@Override
		public void visitLeave(final IDLTerm<I, L, K, R> term)
		{
			lastWasTerm = true;
			_sb.append(")");
		}


		public StringBuilder getStringBuilder()
		{
			return _sb;
		}
	}
}