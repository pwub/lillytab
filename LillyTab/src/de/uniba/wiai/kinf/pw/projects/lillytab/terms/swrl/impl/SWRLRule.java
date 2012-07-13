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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.impl;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.EInvalidTermException;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLElementReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.ITerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.ITermList;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IToStringFormatter;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLClassAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLIndividual;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLIntersection;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLRoleAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLRule;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLVariable;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;


/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class SWRLRule<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	implements ISWRLRule<Name, Klass, Role>
{
	private final ISWRLTerm<Name, Klass, Role> _head;
	private final ISWRLTerm<Name, Klass, Role> _body;
	private final Set<ISWRLVariable<Name, Klass, Role>> _variables = new TreeSet<ISWRLVariable<Name, Klass, Role>>();

	protected SWRLRule(final ISWRLTerm<Name, Klass, Role> head, final ISWRLTerm<Name, Klass, Role> body)
	{
		if ((! (head instanceof ISWRLClassAtom)) && (! (head instanceof ISWRLIntersection)))
			throw new IllegalArgumentException("Only Class atoms or intersections allowed on SWRL rule body");
		else
			_head = head;
		
		if ((! (body instanceof ISWRLClassAtom)) && (! (body instanceof ISWRLIntersection)))
			throw new IllegalArgumentException("Only Class atoms or intersections allowed on SWRL rule body");
		else
			_body = body;

		checkRuleValidity();
	}

	private void checkRuleValidity()
	{
		final Set<ISWRLVariable<Name, Klass, Role>> bodyVars = extractVariables(_body);
		final Set<ISWRLVariable<Name, Klass, Role>> headVars = extractVariables(_head);
		for (ISWRLVariable<Name, Klass, Role> headVar: headVars) {
			if (! bodyVars.contains(headVar))
				throw new EInvalidTermException(String.format("Invalid SWRL Rule, variable %s in head, but not in body: %s", headVar, this));
		}
		
		/* fill variables set */
		extractVariables(_body, _variables);
		extractVariables(_head, _variables);
		
		/**
		 * XXX - TODO, check graph connectivity of rule
		 **/
	}

	private static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
		Set<String> extractVariableNames(ISWRLTerm<Name, Klass, Role> term)
	{
		Set<String> varNames = new TreeSet<String>();
		return extractVariableNames(term, varNames);
	}

	private static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
		Set<String> extractVariableNames(ISWRLTerm<Name, Klass, Role> term, Set<String> varNames)
	{
		if (term instanceof ITermList) {
			@SuppressWarnings("unchecked")
			final ITermList<ISWRLTerm<Name, Klass, Role>> termList = (ITermList<ISWRLTerm<Name, Klass, Role>>)term;
			for (ISWRLTerm<Name, Klass, Role> subTerm: termList)
				extractVariableNames(subTerm, varNames);
		} else if (term instanceof IDLElementReference) {
			/* got an element reference, see if it referres to a variable */
			final IDLElementReference<?> elemRef = (IDLElementReference<?>)term;
			final Object element = elemRef.getElement();
			if (element instanceof ISWRLVariable)
				varNames.add(((ISWRLVariable)element).getVariableName());
		}
		return varNames;
	}

	private static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
		Set<ISWRLVariable<Name, Klass, Role>> extractVariables(ISWRLTerm<Name, Klass, Role> term)
	{
		Set<ISWRLVariable<Name, Klass, Role>> varNames = new TreeSet<ISWRLVariable<Name, Klass, Role>>();
		return extractVariables(term, varNames);
	}

	@SuppressWarnings("unchecked")
	private static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
		Set<ISWRLVariable<Name, Klass, Role>> extractVariables(ISWRLTerm<Name, Klass, Role> term, Set<ISWRLVariable<Name, Klass, Role>> vars)
	{
		if (term instanceof ITermList) {
			final ITermList<?> termList = (ITermList<?>)term;
			for (ITerm subTerm: termList)
				if (subTerm instanceof ISWRLTerm)
					extractVariables((ISWRLTerm<Name, Klass, Role>)subTerm, vars);
		} else if (term instanceof ISWRLClassAtom) {
			/* got an element reference, see if it referres to a variable */
			final ISWRLClassAtom<?, ?, ?> classAtom = (ISWRLClassAtom<?, ?, ?>)term;
			final Object element = classAtom.getIndividual();
			if (element instanceof ISWRLVariable)
				vars.add((ISWRLVariable<Name, Klass, Role>)element);
		} else if (term instanceof ISWRLRoleAtom) {
			final ISWRLRoleAtom<?, ?, ?> roleAtom = (ISWRLRoleAtom<?, ?, ?>)term;
			for (ISWRLIndividual<?, ?, ?> e: roleAtom) {
				if (e instanceof ISWRLVariable)
					vars.add((ISWRLVariable<Name, Klass, Role>)e);
			}
		}
		return vars;
	}


	public ISWRLTerm<Name, Klass, Role> getHead()
	{
		return _head;
	}

	public ISWRLTerm<Name, Klass, Role> getBody()
	{
		return _body;
	}

	public Collection<? extends ISWRLVariable<Name, Klass, Role>> getVariables()
	{
		return _variables;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj instanceof ISWRLRule) {
			ISWRLRule<?, ?, ?> other = (ISWRLRule<?, ?, ?>)obj;
			return getBody().equals(other.getBody()) && getHead().equals(other.getHead());
		} else
			return false;
	}

	@Override
	public int hashCode()
	{
		return 13 + getBody().hashCode() + 41 * getHead().hashCode();
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(getHead());
		sb.append(": ");		
		sb.append(getBody());
		sb.append(".");
		return sb.toString();
	}
	
	public String toString(final IToStringFormatter formatter)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(getHead().toString(formatter));
		sb.append(": ");
		sb.append(getBody().toString(formatter));
		sb.append(".");
		return sb.toString();
	}
	
	@Override
	public ISWRLRule<Name, Klass, Role> clone()
	{
		return this;
	}
}
