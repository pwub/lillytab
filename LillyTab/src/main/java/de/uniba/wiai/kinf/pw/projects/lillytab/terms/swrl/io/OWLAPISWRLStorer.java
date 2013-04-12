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

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLArgument;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLAtomicTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLClassAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLIndividualReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLIntersection;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLLiteralReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLRoleAtom;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLRule;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLVariable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLDArgument;
import org.semanticweb.owlapi.model.SWRLDataFactory;
import org.semanticweb.owlapi.model.SWRLIArgument;
import org.semanticweb.owlapi.model.SWRLRule;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class OWLAPISWRLStorer {

	private final SWRLDataFactory _dataFactory;


	public OWLAPISWRLStorer()
	{
		this(OWLManager.getOWLDataFactory());
	}


	public OWLAPISWRLStorer(final OWLDataFactory dataFactory)
	{
		_dataFactory = dataFactory;
	}


	public SWRLAtom convertAtom(ISWRLAtomicTerm<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> sourceAtom)
	{
		final Map<String, IRI> varMap = new HashMap<>();

		if (sourceAtom instanceof ISWRLClassAtom) {
			final ISWRLClassAtom<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> classAtom = (ISWRLClassAtom<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>>) sourceAtom;
			return _dataFactory.
				getSWRLClassAtom(classAtom.getKlass(), makeIndividual(varMap, classAtom.getIndividual()));
		} else if (sourceAtom instanceof ISWRLRoleAtom) {
			final ISWRLRoleAtom<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> roleAtom = (ISWRLRoleAtom<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>>) sourceAtom;
			roleAtom.getRole();

			if (roleAtom.getRole() instanceof OWLObjectProperty) {
				final SWRLIArgument arg1 = makeIndividual(varMap, roleAtom.getFirstIndividual());
				final SWRLIArgument arg2 = makeIndividual(varMap, roleAtom.getSecondIndividual());
				return _dataFactory.getSWRLObjectPropertyAtom((OWLObjectProperty) roleAtom.getRole(), arg1, arg2);
			} else if (roleAtom.getRole() instanceof OWLDataProperty) {
				final SWRLIArgument arg1 = makeIndividual(varMap, roleAtom.getFirstIndividual());
				final SWRLDArgument arg2 = makeDataLiteral(varMap, roleAtom.getSecondIndividual());
				return _dataFactory.getSWRLDataPropertyAtom((OWLDataProperty) roleAtom.getRole(), arg1, arg2);
			} else {
				throw new IllegalArgumentException(String.format("Unknown property type `%s'", roleAtom.getRole().
					getClass()));
			}
		} else {
			throw new IllegalArgumentException(String.format("Unsupported SWRL atom type `%s'", sourceAtom.
				getClass()));
		}
	}


	public Set<SWRLAtom> convertAtoms(
		final Collection<? extends ISWRLAtomicTerm<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>>> sourceAtoms)
	{
		SortedSet<SWRLAtom> atoms = new TreeSet<>();
		for (ISWRLAtomicTerm<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> sourceAtom : sourceAtoms) {
			final SWRLAtom convertedAtom = convertAtom(sourceAtom);
			atoms.add(convertedAtom);
		}
		return atoms;
	}


	public SWRLRule convertRule(final ISWRLRule<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> sourceRule,
								final Set<OWLAnnotation> annotations)
	{
		final Set<SWRLAtom> headTerms;
		if (sourceRule.getHead() instanceof ISWRLIntersection) {
			headTerms = convertAtoms(
				(ISWRLIntersection<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>>) sourceRule.getHead());
		} else {
			headTerms = Collections.
				singleton(convertAtom(
				(ISWRLAtomicTerm<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>>) sourceRule.getHead()));
		}
		final Set<SWRLAtom> bodyTerms;
		if (sourceRule.getBody() instanceof ISWRLIntersection) {
			bodyTerms = convertAtoms(
				(ISWRLIntersection<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>>) sourceRule.getBody());
		} else {
			bodyTerms = Collections.
				singleton(convertAtom(
				(ISWRLAtomicTerm<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>>) sourceRule.getBody()));
		}
		final SWRLRule rule = _dataFactory.getSWRLRule(bodyTerms, headTerms, annotations);
		return rule;
	}


	public SWRLRule convertRule(final ISWRLRule<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> sourceRule)
	{
		final Set<SWRLAtom> headTerms;
		if (sourceRule.getHead() instanceof ISWRLIntersection) {
			headTerms = convertAtoms(
				(ISWRLIntersection<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>>) sourceRule.getHead());
		} else {
			headTerms = Collections.
				singleton(convertAtom(
				(ISWRLAtomicTerm<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>>) sourceRule.getHead()));
		}
		final Set<SWRLAtom> bodyTerms;
		if (sourceRule.getBody() instanceof ISWRLIntersection) {
			bodyTerms = convertAtoms(
				(ISWRLIntersection<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>>) sourceRule.getBody());
		} else {
			bodyTerms = Collections.
				singleton(convertAtom(
				(ISWRLAtomicTerm<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>>) sourceRule.getBody()));
		}
		final SWRLRule rule = _dataFactory.getSWRLRule(bodyTerms, headTerms);
		return rule;
	}


	private SWRLIArgument makeIndividual(final Map<String, IRI> varMap,
										 final ISWRLArgument<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> indRef)
	{
		if (indRef instanceof ISWRLIndividualReference) {
			final ISWRLIndividualReference<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> nomRef = (ISWRLIndividualReference<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>>) indRef;
			if (nomRef.getIndividual() instanceof OWLIndividual) {
				return _dataFactory.getSWRLIndividualArgument((OWLIndividual) nomRef.getIndividual());
			} else {
				throw new IllegalArgumentException(String.format("Unsupported nominal type `%s'",
																 nomRef.getIndividual().
					getClass()));
			}
		} else if (indRef instanceof ISWRLVariable) {
			final ISWRLVariable<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> var = (ISWRLVariable<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>>) indRef;

			final String varName = var.getVariableName();
			IRI varIRI = varMap.get(varName);
			if (varIRI == null) {
				final String varNameFragment;
				try {
					varNameFragment = URLEncoder.encode(varName, "UTF-8");
				} catch (UnsupportedEncodingException ex) {
					throw new IllegalArgumentException(String.format("Could not encode `%s'", varName), ex);
				}
				varIRI = IRI.create("http://www.example.org/#" + varNameFragment);
				varMap.put(varName, varIRI);
			}
			return _dataFactory.getSWRLVariable(varIRI);
		} else {
			throw new IllegalArgumentException(String.format("Unsupported individual type `%s'", indRef.getClass()));
		}
	}


	private SWRLDArgument makeDataLiteral(final Map<String, IRI> varMap,
										  final ISWRLArgument<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> indRef)
	{
		if (indRef instanceof ISWRLLiteralReference) {
			final ISWRLLiteralReference<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> litRef = (ISWRLLiteralReference<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>>) indRef;
			return _dataFactory.getSWRLLiteralArgument(litRef.getObject());
		} else if (indRef instanceof ISWRLVariable) {
			final ISWRLVariable<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> var = (ISWRLVariable<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>>) indRef;

			final String varName = var.getVariableName();
			IRI varIRI = varMap.get(varName);
			if (varIRI == null) {
				final String varNameFragment;
				try {
					varNameFragment = URLEncoder.encode(varName, "UTF-8");
				} catch (UnsupportedEncodingException ex) {
					throw new IllegalArgumentException(String.format("Could not encode `%s'", varName), ex);
				}
				varIRI = IRI.create("http://www.example.org/#" + varNameFragment);
				varMap.put(varName, varIRI);
			}
			return _dataFactory.getSWRLVariable(varIRI);
		} else {
			throw new IllegalArgumentException(String.format("Unsupported individual type `%s'", indRef.getClass()));
		}
	}
}
