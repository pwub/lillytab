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
 *
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.io;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataRange;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.*;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.util.SWRLTermUtil;
import java.util.*;
import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;
import org.semanticweb.owlapi.model.*;


/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class OWLAPISWRLLoader
{
	private final IDLTermFactory<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> _termFactory;
	private final ISWRLTermFactory<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> _swrlFactory;

	public OWLAPISWRLLoader(
		IDLTermFactory<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> _termFactory,
		ISWRLTermFactory<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> _swrlFactory)
	{
		this._termFactory = _termFactory;
		this._swrlFactory = _swrlFactory;
	}

	public IDLTermFactory<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> getTermFactory()
	{
		return _termFactory;
	}

	public ISWRLTermFactory<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> getSWRLTermFactory()
	{
		return _swrlFactory;
	}

	public ISWRLRule<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> convertRule(
		final SWRLRule sourceRule)
	{
		final BidiMap<IRI, String> varNames = new DualHashBidiMap<>();
		final Set<ISWRLAtomicTerm<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>>> bodyAtoms = convertAtoms(
			sourceRule.getBody(),
			varNames);
		final Set<ISWRLAtomicTerm<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>>> headAtoms = convertAtoms(
			sourceRule.getHead(),
			varNames);
		final ISWRLRule<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> targetRule = _swrlFactory.getSWRLRule(
			SWRLTermUtil.joinIntoIntersection(headAtoms, _swrlFactory),
			SWRLTermUtil.joinIntoIntersection(bodyAtoms, _swrlFactory));
		return targetRule;
	}

	public Set<ISWRLRule<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>>> getRules(
		final OWLOntology onto)
	{
		final Set<ISWRLRule<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>>> rules =
			new TreeSet<>();

		for (OWLLogicalAxiom logAx : onto.getLogicalAxioms()) {
			if (logAx instanceof SWRLRule) {
				final SWRLRule rule = (SWRLRule) logAx;
				final ISWRLRule<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> targetRule = convertRule(rule);
				rules.add(targetRule);
			}
		}
		return rules;
	}

	private ISWRLIArgument<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> handleIndividual(final SWRLIArgument arg, final BidiMap<IRI, String> varNames)
	{
		if (arg instanceof SWRLVariable) {
			final SWRLVariable var = (SWRLVariable) arg;
			final String varName = getVarName(var.getIRI(), varNames);
			final ISWRLVariable<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> indVar =
				_swrlFactory.getSWRLVariable(varName);
			return indVar;
		} else if (arg instanceof SWRLIndividualArgument) {
			final SWRLIndividualArgument ind = (SWRLIndividualArgument) arg;
			final ISWRLIArgument<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> indRef = _swrlFactory.
				getSWRLIndividualReference(
				ind.getIndividual());
			return indRef;
		} else {
			throw new IllegalArgumentException("Unknown SWRL individual type: " + arg.getClass());
		}
	}

	private ISWRLDArgument<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> handleData(
		final SWRLDArgument arg, final BidiMap<IRI, String> varNames)
	{
		if (arg instanceof SWRLVariable) {
			final SWRLVariable var = (SWRLVariable) arg;
			final String varName = getVarName(var.getIRI(), varNames);
			final ISWRLVariable<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> indVar =
				_swrlFactory.getSWRLVariable(varName);
			return indVar;
		} else if (arg instanceof SWRLLiteralArgument) {
			final SWRLLiteralArgument lit = (SWRLLiteralArgument) arg;
			final ISWRLDArgument<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> litRef = _swrlFactory.
				getSWRLLiteralReference(lit.getLiteral());
			return litRef;
		} else {
			throw new IllegalArgumentException("Unknown SWRL individual type: " + arg.getClass());
		}
	}

	private String getVarName(final IRI sourceName, final BidiMap<IRI, String> varNames)
	{
		String varName = varNames.get(sourceName);
		if (varName == null) {
			final String fragment = sourceName.getFragment();
			if ((fragment != null)
				&& (!fragment.isEmpty())
				&& (!varNames.containsValue(fragment))) {
				varNames.put(sourceName, varName);
				varName = fragment;
			} else {
				/*
				 * generate an indexed varname
				 */
				for (int i = 0; (varName == null) || (varNames.containsValue(varName)); ++i) {
					varName = String.format("v%d", i);
				}
			}
		}
		assert varName != null;
		return varName;
	}

	private Set<ISWRLAtomicTerm<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>>> convertAtoms(final Collection<SWRLAtom> sourceAtoms, final BidiMap<IRI, String> varNames)
	{
		final Set<ISWRLAtomicTerm<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>>> targetAtoms = new TreeSet<>();
		for (SWRLAtom sourceAtom : sourceAtoms) {
			if (sourceAtom instanceof SWRLClassAtom) {
				final SWRLClassAtom sourceClassAtom = (SWRLClassAtom) sourceAtom;
				if (!(sourceClassAtom.getPredicate() instanceof OWLClass)) {
					throw new IllegalArgumentException(
						"Complex class axioms in rules are not supported: " + sourceClassAtom.getPredicate());
				} else {
					final OWLClass owlClass = (OWLClass) sourceClassAtom.getPredicate();
					final SWRLIArgument arg = sourceClassAtom.getArgument();
					final ISWRLIArgument<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> ind = handleIndividual(
						arg, varNames);
					final ISWRLClassAtom<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> atom = _swrlFactory.
						getSWRLClassAtom(owlClass, ind);
					targetAtoms.add(atom);
				}
			} else if (sourceAtom instanceof SWRLObjectPropertyAtom) {
				final SWRLObjectPropertyAtom propAtom = (SWRLObjectPropertyAtom) sourceAtom;
				if (!(propAtom.getSimplified().getPredicate() instanceof OWLObjectProperty)) {
					throw new IllegalArgumentException(
						"Complex property expressions in rules are not supported: " + propAtom.getPredicate());
				} else {
					final OWLObjectProperty objProp = (OWLObjectProperty) propAtom.getSimplified().getPredicate();
					final SWRLIArgument arg1 = propAtom.getFirstArgument();
					final SWRLIArgument arg2 = propAtom.getSecondArgument();
					final ISWRLIArgument<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> ind1 = handleIndividual(
						arg1, varNames);
					final ISWRLIArgument<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> ind2 = handleIndividual(
						arg2, varNames);
					final ISWRLRoleAtom<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> atom = _swrlFactory.
						getSWRLObjectRoleAtom(objProp, ind1, ind2);
					targetAtoms.add(atom);
				}
			} else if (sourceAtom instanceof SWRLDataPropertyAtom) {
				final SWRLDataPropertyAtom propAtom = (SWRLDataPropertyAtom) sourceAtom;
				if (!(propAtom.getPredicate() instanceof OWLDataProperty)) {
					throw new IllegalArgumentException(
						"Complex data property expressions in rules are not supported: " + propAtom.getPredicate());
				} else {
					final OWLDataProperty dataProp = (OWLDataProperty) propAtom.getPredicate();
					final SWRLIArgument arg1 = propAtom.getFirstArgument();
					final SWRLDArgument arg2 = propAtom.getSecondArgument();
					final ISWRLIArgument<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> ind1 = handleIndividual(
						arg1, varNames);
					final ISWRLDArgument<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> ind2 = handleData(arg2,
																												   varNames);
					final ISWRLRoleAtom<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> atom = _swrlFactory.
						getSWRLDataRoleAtom(dataProp, ind1, ind2);
					targetAtoms.add(atom);
				}
			} else if (sourceAtom instanceof SWRLDataRangeAtom) {
				final SWRLDataRangeAtom rangeAtom = (SWRLDataRangeAtom) sourceAtom;
				final IDLDataRange<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> dataRange = OWLAPILoader.
					convertDataRange(_termFactory, rangeAtom.getPredicate());
				final SWRLDArgument arg = rangeAtom.getArgument();
				ISWRLDArgument<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> ind = handleData(arg, varNames);
				final ISWRLDataRangeAtom<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> atom =
					_swrlFactory.getSWRLDataRange(dataRange, ind);
				targetAtoms.add(atom);
			} else {
				throw new IllegalArgumentException("Unsupported atom type: " + sourceAtom.getClass());
			}
		}
		return targetAtoms;
	}
}
