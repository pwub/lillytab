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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.impl;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLElementReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.ITerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.ITermList;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLArgument;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLClassAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLIntersection;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLRoleAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLRule;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLVariable;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;


/**
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public class SWRLRule<I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>>
	implements ISWRLRule<I, L, K, R>
{
	private final ISWRLTerm<I, L, K, R> _head;
	private final ISWRLTerm<I, L, K, R> _body;
	private final Set<ISWRLVariable<I, L, K, R>> _variables = new TreeSet<>();

	protected SWRLRule(final ISWRLTerm<I, L, K, R> head, final ISWRLTerm<I, L, K, R> body)
	{
		if ((!(head instanceof ISWRLClassAtom)) && (!(head instanceof ISWRLRoleAtom)) && (!(head instanceof ISWRLIntersection))) {
			throw new IllegalArgumentException("Only atoms or intersections allowed on SWRL rule body");
		} else {
			_head = head;
		}

		if ((!(body instanceof ISWRLClassAtom)) && (!(body instanceof ISWRLRoleAtom)) && (!(body instanceof ISWRLIntersection))) {
			throw new IllegalArgumentException("Only atoms or intersections allowed on SWRL rule body");
		} else {
			_body = body;
		}

		updateVariableSet();
		checkRuleValidity();
	}

	@Override
	public ISWRLTerm<I, L, K, R> getHead()
	{
		return _head;
	}

	@Override
	public ISWRLTerm<I, L, K, R> getBody()
	{
		return _body;
	}

	@Override
	public Collection<? extends ISWRLVariable<I, L, K, R>> getVariables()
	{
		return Collections.unmodifiableCollection(_variables);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj instanceof ISWRLRule) {
			ISWRLRule<?, ?, ?, ?> other = (ISWRLRule<?, ?, ?, ?>) obj;
			return getBody().equals(other.getBody()) && getHead().equals(other.getHead());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode(
		)
	{
		return 13 + getBody().hashCode() + 41 * getHead().hashCode();
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(getHead());
		sb.append(":- ");
		sb.append(getBody());
		sb.append(".");
		return sb.toString();
	}

	@Override
	public ISWRLRule<I, L, K, R> clone()
	{
		return this;
	}

	@Override
	public int compareTo(ISWRLRule<I, L, K, R> o)
	{
		int compare = _head.compareTo(o.getHead());
		if (compare == 0) {
			compare = _body.compareTo(o.getBody());
		}
		return compare;
	}

		private static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> Set<String> extractVariableNames(ISWRLTerm<I, L, K, R> term)
	{
		Set<String> varNames = new TreeSet<>();
		return extractVariableNames(term, varNames);
	}

	private static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> Set<String> extractVariableNames(ISWRLTerm<I, L, K, R> term, Set<String> varNames)
	{
		if (term instanceof ITermList) {
			@SuppressWarnings("unchecked")
			final ITermList<ISWRLTerm<I, L, K, R>> termList = (ITermList<ISWRLTerm<I, L, K, R>>) term;
			for (ISWRLTerm<I, L, K, R> subTerm : termList) {
				extractVariableNames(subTerm, varNames);
			}
		} else if (term instanceof IDLElementReference) {
			/* got an element reference, see if it referres to a variable */
			final IDLElementReference<?> elemRef = (IDLElementReference<?>) term;
			final Object element = elemRef.getElement();
			if (element instanceof ISWRLVariable) {
				varNames.add(((ISWRLVariable) element).getVariableName());
			}
		}
		return varNames;
	}

	private static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> Set<ISWRLVariable<I, L, K, R>> extractVariables(
		ISWRLTerm<I, L, K, R> term)
	{
		Set<ISWRLVariable<I, L, K, R>> varNames = new TreeSet<>();
		return extractVariables(term, varNames);
	}

	@SuppressWarnings("unchecked")
	private static <I extends Comparable<? super I>, L extends Comparable<? super L>, K extends Comparable<? super K>, R extends Comparable<? super R>> Set<ISWRLVariable<I, L, K, R>> extractVariables(
		ISWRLTerm<I, L, K, R> term, Set<ISWRLVariable<I, L, K, R>> vars)
	{
		if (term instanceof ITermList) {
			final ITermList<?> termList = (ITermList<?>) term;
			for (ITerm subTerm : termList) {
				if (subTerm instanceof ISWRLTerm) {
					extractVariables((ISWRLTerm<I, L, K, R>) subTerm, vars);
				}
			}
		} else if (term instanceof ISWRLClassAtom) {
			/* got an element reference, see if it referres to a variable */
			final ISWRLClassAtom<?, ?, ?, ?> classAtom = (ISWRLClassAtom<?, ?, ?, ?>) term;
			final Object element = classAtom.getIndividual();
			if (element instanceof ISWRLVariable) {
				vars.add((ISWRLVariable<I, L, K, R>) element);
			}
		} else if (term instanceof ISWRLRoleAtom) {
			final ISWRLRoleAtom<?, ?, ?, ?> roleAtom = (ISWRLRoleAtom<?, ?, ?, ?>) term;
			for (ISWRLArgument<?, ?, ?, ?> e : roleAtom) {
				if (e instanceof ISWRLVariable) {
					vars.add((ISWRLVariable<I, L, K, R>) e);
				}
			}
		}
		return vars;
	}

	private void checkRuleValidity()
	{
//		final Set<ISWRLVariable<I, L, K, R>> bodyVars = extractVariables(_body);
//		final Set<ISWRLVariable<I, L, K, R>> headVars = extractVariables(_head);
//		for (ISWRLVariable<I, L, K, R> headVar: headVars) {
//			if (! bodyVars.contains(headVar))
//				throw new EInvalidTermException(String.format("Invalid SWRL Rule, variable %s in head, but not in body: %s", headVar, this));
//		}
	}

	private void updateVariableSet()
	{
		/* fill variables set */
		extractVariables(_body, _variables);
		extractVariables(_head, _variables);
	}
}
