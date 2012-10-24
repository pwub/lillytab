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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.io;

import de.uniba.wiai.kinf.pw.projects.lillytab.io.OWLAPIDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.util.SWRLTermUtil;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl.DLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.*;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.impl.SWRLTermFactory;
import java.util.*;
import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class OWLAPISWRLLoader {

	final IDLTermFactory<OWLObject, OWLClass, OWLProperty<?, ?>> _termFactory;
	final ISWRLTermFactory<OWLObject, OWLClass, OWLProperty<?, ?>> _swrlFactory;


	public OWLAPISWRLLoader()
	{
		this(
			new OWLAPIDLTermFactory(OWLManager.getOWLDataFactory()),
			new SWRLTermFactory<OWLObject, OWLClass, OWLProperty<?, ?>>());
	}


	public OWLAPISWRLLoader(
		final IDLTermFactory<OWLObject, OWLClass, OWLProperty<?, ?>> termFactory,
		final ISWRLTermFactory<OWLObject, OWLClass, OWLProperty<?, ?>> swrlFactory)
	{
		_termFactory = termFactory;
		_swrlFactory = swrlFactory;
	}


	public IDLTermFactory<OWLObject, OWLClass, OWLProperty<?, ?>> getTermFactory()
	{
		return _termFactory;
	}


	public ISWRLTermFactory<OWLObject, OWLClass, OWLProperty<?, ?>> getSWRLTermFactory()
	{
		return _swrlFactory;
	}


	private ISWRLIndividual<OWLObject, OWLClass, OWLProperty<?, ?>> handleIndividual(
		final SWRLIArgument arg,
		final BidiMap<IRI, String> varNames)
	{
		if (arg instanceof SWRLVariable) {
			final SWRLVariable var = (SWRLVariable) arg;
			final String varName = getVarName(var.getIRI(), varNames);
			final ISWRLVariable<OWLObject, OWLClass, OWLProperty<?, ?>> indVar =
				_swrlFactory.getSWRLVariable(varName);
			return indVar;
		} else if (arg instanceof SWRLIndividualArgument) {
			final SWRLIndividualArgument ind = (SWRLIndividualArgument) arg;
			final ISWRLIndividual<OWLObject, OWLClass, OWLProperty<?, ?>> indRef = _swrlFactory.getSWRLNominalReference(
				ind.getIndividual());
			return indRef;
		} else
			throw new IllegalArgumentException("Unknown SWRL individual type: " + arg.getClass());
	}


	private ISWRLIndividual<OWLObject, OWLClass, OWLProperty<?, ?>> handleData(
		final SWRLDArgument arg,
		final BidiMap<IRI, String> varNames)
	{
		if (arg instanceof SWRLVariable) {
			final SWRLVariable var = (SWRLVariable) arg;
			final String varName = getVarName(var.getIRI(), varNames);
			final ISWRLVariable<OWLObject, OWLClass, OWLProperty<?, ?>> indVar =
				_swrlFactory.getSWRLVariable(varName);
			return indVar;
		} else if (arg instanceof SWRLLiteralArgument) {
			final SWRLLiteralArgument lit = (SWRLLiteralArgument) arg;			
			final ISWRLIndividual<OWLObject, OWLClass, OWLProperty<?, ?>> litRef = _swrlFactory.getSWRLNominalReference(
				lit.getLiteral());
			return litRef;
		} else
			throw new IllegalArgumentException("Unknown SWRL individual type: " + arg.getClass());		
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
				for (int i = 0; (varName == null) || (varNames.containsValue(varName)); ++i)
					varName = String.format("v%d", i);
			}
		}
		assert varName != null;
		return varName;
	}


	private Set<ISWRLAtomicTerm<OWLObject, OWLClass, OWLProperty<?, ?>>> handleAtoms(
		final Collection<SWRLAtom> sourceAtoms,
		final BidiMap<IRI, String> varNames)
	{
		final Set<ISWRLAtomicTerm<OWLObject, OWLClass, OWLProperty<?, ?>>> targetAtoms = new TreeSet<ISWRLAtomicTerm<OWLObject, OWLClass, OWLProperty<?, ?>>>();
		for (SWRLAtom sourceAtom : sourceAtoms) {
			if (sourceAtom instanceof SWRLClassAtom) {
				final SWRLClassAtom sourceClassAtom = (SWRLClassAtom) sourceAtom;
				if (!(sourceClassAtom.getPredicate() instanceof OWLClass)) {
					throw new IllegalArgumentException(
						"Complex class axioms in rules are not supported: " + sourceClassAtom.getPredicate());
				} else {
					final OWLClass owlClass = (OWLClass) sourceClassAtom.getPredicate();
					final SWRLIArgument arg = sourceClassAtom.getArgument();
					final ISWRLIndividual<OWLObject, OWLClass, OWLProperty<?, ?>> ind = handleIndividual(arg, varNames);
					final ISWRLClassAtom<OWLObject, OWLClass, OWLProperty<?, ?>> atom = _swrlFactory.getSWRLClassAtom(
						owlClass, ind);
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
					final ISWRLIndividual<OWLObject, OWLClass, OWLProperty<?, ?>> ind1 = handleIndividual(arg1, varNames);
					final ISWRLIndividual<OWLObject, OWLClass, OWLProperty<?, ?>> ind2 = handleIndividual(arg2, varNames);
					final ISWRLRoleAtom<OWLObject, OWLClass, OWLProperty<?, ?>> atom = _swrlFactory.getSWRLRoleAtom(
						objProp, ind1, ind2);
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
					final ISWRLIndividual<OWLObject, OWLClass, OWLProperty<?, ?>> ind1 = handleIndividual(arg1, varNames);
					final ISWRLIndividual<OWLObject, OWLClass, OWLProperty<?, ?>> ind2 = handleData(arg2, varNames);
					final ISWRLRoleAtom<OWLObject, OWLClass, OWLProperty<?, ?>> atom = _swrlFactory.getSWRLRoleAtom(
						dataProp, ind1, ind2);
					targetAtoms.add(atom);
				}						
			} else {		
				throw new IllegalArgumentException("Unsupported atom type: " + sourceAtom.getClass());
			}
		}
		return targetAtoms;
	}

	private ISWRLRule<OWLObject, OWLClass, OWLProperty<?, ?>> handleRule(final SWRLRule sourceRule)
	{
		final BidiMap<IRI, String> varNames = new DualHashBidiMap<IRI, String>();
		final Set<ISWRLAtomicTerm<OWLObject, OWLClass, OWLProperty<?, ?>>> bodyAtoms
			= handleAtoms(sourceRule.getBody(), varNames);
		final Set<ISWRLAtomicTerm<OWLObject, OWLClass, OWLProperty<?, ?>>> headAtoms
			= handleAtoms(sourceRule.getHead(), varNames);
		final ISWRLRule<OWLObject, OWLClass, OWLProperty<?, ?>> targetRule
			= _swrlFactory.getSWRLRule(
				SWRLTermUtil.joinIntoIntersection(headAtoms, _swrlFactory),
				SWRLTermUtil.joinIntoIntersection(bodyAtoms, _swrlFactory)
		);
		return targetRule;
	}


	public Set<ISWRLRule<OWLObject, OWLClass, OWLProperty<?, ?>>> getRules(final OWLOntology onto)
	{
		final Set<ISWRLRule<OWLObject, OWLClass, OWLProperty<?, ?>>> rules =
			new TreeSet<ISWRLRule<OWLObject, OWLClass, OWLProperty<?, ?>>>();

		for (OWLLogicalAxiom logAx : onto.getLogicalAxioms()) {
			if (logAx instanceof SWRLRule) {
				final SWRLRule rule = (SWRLRule) logAx;
				final ISWRLRule<OWLObject, OWLClass, OWLProperty<?, ?>> targetRule = handleRule(rule);
				rules.add(targetRule);
			}
		}
		return rules;
	}
}
