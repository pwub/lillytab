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
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLClassAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLDataRangeAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLRoleAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLRule;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.visitor.ISWRLTermVisitor;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
@SupportsType({ISWRLTerm.class, ISWRLRule.class})
public class SWRLTermStringer
	extends AbstractAnnotationStringer {

	public SWRLTermStringer()
	{
	}

	class Stringer<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
		implements ISWRLTermVisitor<I, L, K, R> {

		boolean lastWasTerm = false;
		private final StringBuilder _sb;
		private final IToStringConverter _backStringer;


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
		public void visitEnter(final ISWRLTerm<I, L, K, R> term)
		{
			lastWasTerm = false;
			_sb.append("(");

		}


		@Override
		public void visit(final ISWRLTerm<I, L, K, R> term)
		{
			if (lastWasTerm)
				_sb.append(", ");
			lastWasTerm = true;

			if (term instanceof ISWRLClassAtom) {
				final ISWRLClassAtom<I, L, K, R> clsAtom = (ISWRLClassAtom<I, L, K, R>) term;
				_sb.append("(");
				_backStringer.append(_sb, clsAtom.getKlass());
				_sb.append(" ");
				_sb.append(clsAtom.getIndividual());
				_sb.append(")");
			} else if (term instanceof ISWRLRoleAtom) {
				final ISWRLRoleAtom<I, L, K, R> roleAtom = (ISWRLRoleAtom<I, L, K, R>) term;
				_sb.append("(");
				_backStringer.append(_sb, roleAtom.getRole());
				_sb.append(" ");
				_sb.append(roleAtom.getFirstIndividual());
				_sb.append(" ");
				_sb.append(roleAtom.getSecondIndividual());
				_sb.append(")");
			} else if (term instanceof ISWRLDataRangeAtom) {
				final ISWRLDataRangeAtom<I, L, K, R> rangeAtom = (ISWRLDataRangeAtom<I, L, K, R>) term;
				_sb.append("(");
				_backStringer.append(_sb, rangeAtom.getDataRange());
				_sb.append(" ");
				_backStringer.append(_sb, rangeAtom.getIndividual());
				_sb.append(")");
			}
		}


		@Override
		public void visitLeave(final ISWRLTerm<I, L, K, R> term)
		{
			lastWasTerm = true;
			_sb.append(")");
		}
	}


	@Override
	@SuppressWarnings("unchecked")
	public void append(StringBuilder sb, Object obj, final IToStringConverter backStringer)
	{
		if (obj instanceof ISWRLTerm) {
			final ISWRLTerm term = (ISWRLTerm) obj;
			final Stringer stringer = new Stringer(sb, backStringer);
			term.accept(stringer);
		} else if (obj instanceof ISWRLRule) {
			final ISWRLRule rule = (ISWRLRule) obj;
			final Stringer stringer = new Stringer(sb, backStringer);

			append(sb, rule.getHead(), backStringer);
			sb.append(":- ");
			append(sb, rule.getBody(), backStringer);
			sb.append(".");
		} else
			backStringer.append(sb, obj);
	}
}