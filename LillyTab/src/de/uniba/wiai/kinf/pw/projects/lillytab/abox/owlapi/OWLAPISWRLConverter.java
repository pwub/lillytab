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
package de.uniba.wiai.kinf.pw.projects.lillytab.abox.owlapi;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.ITermList;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.*;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;
import java.util.TreeSet;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class OWLAPISWRLConverter {
	public final static String URI_ENCODING = "utf-8";
	private final SWRLDataFactory _dataFactory;
	
	public OWLAPISWRLConverter(final SWRLDataFactory dataFactory)
	{
		_dataFactory = dataFactory;
	}
	
	public OWLAPISWRLConverter()
	{
		this(OWLManager.getOWLDataFactory());
	}
	
	public IRI
	getIRIforVariable(final ISWRLVariable<OWLObject, OWLClass, OWLProperty<?, ?>> var)
	{
		try {
			final String varName = var.getVariableName();
			final String encodedVarName = URLEncoder.encode(varName, "utf-8");
			final IRI iri = IRI.create("#" + encodedVarName);
			return iri;
		} catch (UnsupportedEncodingException ex) {
			throw new IllegalArgumentException(ex);
		}			
	}
	
	public SWRLVariable getVariable(final ISWRLVariable<OWLObject, OWLClass, OWLProperty<?, ?>> var)
	{
		final IRI variableIRI = getIRIforVariable(var);
		return _dataFactory.getSWRLVariable(variableIRI);
	}
	
	public SWRLIndividualArgument getNominal(final ISWRLNominalReference<OWLObject, OWLClass, OWLProperty<?, ?>> nomRef)
	{
		assert nomRef.getNominal() instanceof OWLNamedIndividual;
		OWLNamedIndividual nom = (OWLNamedIndividual)nomRef.getNominal();
		return _dataFactory.getSWRLIndividualArgument(nom);
	}
	
	public SWRLIArgument getIndividual(final ISWRLIndividual<OWLObject, OWLClass, OWLProperty<?, ?>> ind)
	{
		if (ind instanceof ISWRLVariable)
			return getVariable((ISWRLVariable<OWLObject, OWLClass, OWLProperty<?, ?>>)ind);
		else if (ind instanceof ISWRLNominalReference)
			return getNominal((ISWRLNominalReference<OWLObject, OWLClass, OWLProperty<?, ?>>)ind);
		else
			throw new IllegalArgumentException(String.format("Unknown SWRL individual type: %s", ind.getClass()));
	}
	
	public SWRLDArgument getDataIndividual(final ISWRLIndividual<OWLObject, OWLClass, OWLProperty<?, ?>> ind)
	{
		if (ind instanceof ISWRLVariable)
			return getVariable((ISWRLVariable<OWLObject, OWLClass, OWLProperty<?, ?>>)ind);
		else 
			return getDataValue((ISWRLNominalReference<OWLObject, OWLClass, OWLProperty<?, ?>>)ind);			
	}

	public SWRLLiteralArgument getDataValue(final ISWRLNominalReference<OWLObject, OWLClass, OWLProperty<?, ?>> nomRef)
	{
		assert nomRef.getNominal() instanceof OWLLiteral;
		OWLLiteral literal = (OWLLiteral)nomRef.getNominal();
		return _dataFactory.getSWRLLiteralArgument(literal);		
	}

	public SWRLObjectPropertyAtom getObjectPropertyAtom(final ISWRLRoleAtom<OWLObject, OWLClass, OWLProperty<?, ?>> roleAtom)
	{
		assert roleAtom.getRole().isOWLObjectProperty();
		final OWLObjectProperty prop = (OWLObjectProperty)roleAtom.getRole();
		final SWRLIArgument first = getIndividual(roleAtom.getFirstIndividual());
		final SWRLIArgument second = getIndividual(roleAtom.getSecondIndividual());
		return _dataFactory.getSWRLObjectPropertyAtom(prop, first, first);
	}
	
	public SWRLDataPropertyAtom getDataPropertyAtom(final ISWRLRoleAtom<OWLObject, OWLClass, OWLProperty<?, ?>> roleAtom)
	{
		assert roleAtom.getRole().isOWLDataProperty();
		final OWLDataProperty prop = (OWLDataProperty)roleAtom.getRole();
		final SWRLIArgument first = getIndividual(roleAtom.getFirstIndividual());
		final SWRLDArgument second = getDataIndividual(roleAtom.getSecondIndividual());
		return _dataFactory.getSWRLDataPropertyAtom(prop, first, second);
	}
	
	public SWRLBinaryAtom<?, ?> getPropertyAtom(final ISWRLRoleAtom<OWLObject, OWLClass, OWLProperty<?, ?>> roleAtom)
	{
		if (roleAtom.getRole().isDataPropertyExpression())
			return getDataPropertyAtom(roleAtom);
		else if (roleAtom.getRole().isObjectPropertyExpression())
			return getObjectPropertyAtom(roleAtom);
		else
			throw new IllegalArgumentException(String.format("Unknown role type: %s", roleAtom.getRole().getClass()));
	}
	
	public SWRLAtom getAtom(ISWRLAtomicTerm<OWLObject, OWLClass, OWLProperty<?, ?>> atom)		
	{
		if (atom instanceof ISWRLClassAtom) {
			final ISWRLClassAtom<OWLObject, OWLClass, OWLProperty<?, ?>> clsAtom =
				(ISWRLClassAtom<OWLObject, OWLClass, OWLProperty<?, ?>>)atom;
			final SWRLIArgument arg = getIndividual(clsAtom.getIndividual());
			return _dataFactory.getSWRLClassAtom(clsAtom.getKlass(), arg);
		} else if (atom instanceof ISWRLRoleAtom) {
			final ISWRLRoleAtom<OWLObject, OWLClass, OWLProperty<?, ?>> roleAtom =
				(ISWRLRoleAtom<OWLObject, OWLClass, OWLProperty<?, ?>>)atom;
			return getPropertyAtom(roleAtom);
		} else
			throw new IllegalArgumentException(String.format("Unknown SWRL atom type: %s", atom.getClass()));
	}
	
	public Set<SWRLAtom> getAtoms(final ITermList<? extends ISWRLAtomicTerm<OWLObject, OWLClass, OWLProperty<?, ?>>> termList)
	{
		Set<SWRLAtom> termSet = new TreeSet<SWRLAtom>();
		for (ISWRLAtomicTerm<OWLObject, OWLClass, OWLProperty<?, ?>> atom: termList) {
			final SWRLAtom swrlAtom = getAtom(atom);
			termSet.add(swrlAtom);
		}
		return termSet;
	}
	
	public SWRLRule getRule(final ISWRLRule<OWLObject, OWLClass, OWLProperty<?, ?>> rule)
	{
		assert rule.getHead() instanceof ISWRLIntersection;
		final ISWRLIntersection<OWLObject, OWLClass, OWLProperty<?, ?>> head = (ISWRLIntersection<OWLObject, OWLClass, OWLProperty<?, ?>>)rule.getHead();
		assert rule.getBody() instanceof ISWRLIntersection;
		final ISWRLIntersection<OWLObject, OWLClass, OWLProperty<?, ?>> body = (ISWRLIntersection<OWLObject, OWLClass, OWLProperty<?, ?>>)rule.getBody();
		
		final Set<SWRLAtom> headAtoms = getAtoms(head);
		final Set<SWRLAtom> bodyAtoms = getAtoms(body);
		
		return _dataFactory.getSWRLRule(bodyAtoms, headAtoms);
	}
}
