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
package de.uniba.wiai.kinf.pw.projects.lillytab.io;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentRBoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IIndividualABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IAssertedRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleProperty;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleType;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.EUnsupportedDatatypeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLAllRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLDataSomeRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLImplies;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLIndividualReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLLiteralReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLObjectIntersection;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLObjectNegation;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLObjectUnion;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLSomeRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataNegation;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataRange;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.TermUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.Namespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Loaders are classes that perform the transformation between an external representation of an ontology (ABox + TBox)
 * into the internal representation {@link IABoxNode} of an ABox loader that supports transformation of an
 * <p />
 * See the OWLAPI documentation
 * <a href="http://owlapi.sourcefource.net">OWLAPI documentation</a>
 *
 * @see OWLOntology
 *
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public class OWLAPILoader
{
	static final Logger _logger = LoggerFactory.getLogger(OWLAPILoader.class);
	private boolean _isIgnoreUnsupportedAxioms = true;

	public OWLAPILoader(boolean ignoreUnsupportedAxioms)
	{
		this();
		_isIgnoreUnsupportedAxioms = ignoreUnsupportedAxioms;
	}

	public OWLAPILoader()
	{
	}

	/// <editor-fold defaultstate="collapsed" desc="data range conversion">
	public static IDLDataRange<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> convertDatatype(
		final IDLTermFactory<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> termFactory,
		final OWLDatatype datatype)
		throws EUnsupportedDatatypeException
	{
		final IRI datatypeIRI = datatype.getIRI();
		/*
		 * XXX -workaround for broken owlapi manchester syntax parsing
		 */
		final IRI incorrectLiteralIRI = IRI.create(Namespaces.XSD + "Literal");
		if (datatype.isTopDatatype() || (datatype.asOWLDatatype().getIRI().equals(incorrectLiteralIRI))) {
			return termFactory.getDLTopDatatype();
		} else {
			return new OWLAPIDataType(datatypeIRI);
		}
	}

	public static IDLDataRange<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> convertDataRange(
		final IDLTermFactory<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> termFactory,
		final OWLDataRange dataRange)
	{
		if (dataRange instanceof OWLDataComplementOf) {
			final OWLDataComplementOf complementOf = (OWLDataComplementOf) dataRange;
			final IDLDataRange<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> subExp = convertDataRange(
				termFactory,
				complementOf.getDataRange());
			final IDLDataNegation<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> negation = termFactory.
				getDLDataNegation(subExp);
			return negation;
		} else if (dataRange instanceof OWLDataOneOf) {
			final OWLDataOneOf oneOf = (OWLDataOneOf) dataRange;
			Set<IDLDataRange<OWLIndividual, OWLLiteral, OWLClass, OWLProperty>> literals = new HashSet<>();
			for (OWLLiteral literal : oneOf.getValues()) {
				final IDLLiteralReference<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> nominal = termFactory.
					getDLLiteralReference(
					literal);
				literals.add(nominal);
			}
			final IDLDataRange<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> desc = TermUtil.
				joinToDataUnion(literals,
								termFactory);
			return desc;
		} else if (dataRange instanceof OWLDatatype) {
			try {
				assert dataRange.isDatatype();
				return convertDatatype(termFactory, dataRange.asOWLDatatype());
			} catch (EUnsupportedDatatypeException ex) {
				// XXX - propagate?
				throw new IllegalArgumentException(ex);
			}
		} else if (dataRange instanceof OWLDataUnionOf) {
			final OWLDataUnionOf unionOf = (OWLDataUnionOf) dataRange;
			final List<IDLDataRange<OWLIndividual, OWLLiteral, OWLClass, OWLProperty>> terms = new ArrayList<>();
			for (OWLDataRange subRange : unionOf.getOperands()) {
				final IDLDataRange<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> subDesc = convertDataRange(
					termFactory, subRange);
				terms.add(subDesc);
			}
			final IDLDataRange<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> desc = TermUtil.
				joinToDataUnion(terms, termFactory);
			return desc;
		} else if (dataRange instanceof OWLDataIntersectionOf) {
			final OWLDataIntersectionOf intersection = (OWLDataIntersectionOf) dataRange;
			final List<IDLDataRange<OWLIndividual, OWLLiteral, OWLClass, OWLProperty>> terms = new ArrayList<>();
			for (OWLDataRange subRange : intersection.getOperands()) {
				final IDLDataRange<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> subDesc = convertDataRange(
					termFactory, subRange);
				terms.add(subDesc);
			}
			final IDLDataRange<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> desc = TermUtil.
				joinToDataIntersection(terms, termFactory);
			return desc;
		} else {
			throw new IllegalArgumentException("Unsupported data range type: " + dataRange.getClass().toString());
		}
	}
	/// </editor-fold>
	public static IDLClassExpression<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> makeDLDescription(
		final IDLTermFactory<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> termFactory, final OWLObjectOneOf owlOneOf)
	{
		/*
		 * object one of
		 */
		final Set<IDLClassExpression<OWLIndividual, OWLLiteral, OWLClass, OWLProperty>> nomimals = new HashSet<>();
		for (OWLIndividual individual : owlOneOf.getIndividuals()) {
			IDLIndividualReference<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> nominal = termFactory.
				getDLIndividualReference(individual);
			nomimals.add(nominal);
		}
		final IDLClassExpression<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> desc;
		assert !nomimals.isEmpty();
		if (nomimals.size() == 1) {
			desc = nomimals.iterator().next();
		} else {
			desc = termFactory.getDLObjectUnion(nomimals);
		}
		return desc;
	}

	public static IDLDataRange<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> makeDLRestrictionTerm(
		final IDLTermFactory<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> termFactory, final OWLDataOneOf owlOneOf)
	{
		/*
		 * data one of
		 */
		final Set<IDLDataRange<OWLIndividual, OWLLiteral, OWLClass, OWLProperty>> literals = new HashSet<>();
		for (OWLLiteral literal : owlOneOf.getValues()) {
			IDLLiteralReference<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> nominal = termFactory.
				getDLLiteralReference(literal);
			literals.add(nominal);
		}

		final IDLDataRange<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> desc;
		assert !literals.isEmpty();
		if (literals.size() == 1) {
			desc = literals.iterator().next();
		} else {
			desc = termFactory.getDLDataUnion(literals);
		}
		return desc;
	}

	public IABox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> fillABox(
		final OWLOntology ontology, final IABox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> targetAbox) throws ENodeMergeException, EInconsistencyException
	{
		for (OWLAxiom axiom : ontology.getAxioms()) {
			try {
				processAxiom(targetAbox, axiom);
			} catch (IllegalArgumentException ex) {
				if (isIsIgnoreUnsupportedAxioms()) {
					_logger.warn(String.format("Unknown axiom of type '%s' ignored.", axiom.getClass()), ex);
				} else {
					throw ex;
				}
			}
		}

		return targetAbox;
	}

	/**
	 * @return the _isIgnoreUnsupportedAxioms
	 */
	public boolean isIsIgnoreUnsupportedAxioms(
		)
	{
		return _isIgnoreUnsupportedAxioms;
	}

	/**
	 * @param isIgnoreUnsupportedAxioms the _isIgnoreUnsupportedAxioms to set
	 */
	public void setIsIgnoreUnsupportedAxioms(
		boolean isIgnoreUnsupportedAxioms)
	{
		this._isIgnoreUnsupportedAxioms = isIgnoreUnsupportedAxioms;
	}

	/// <editor-fold defaultstate="collapsed" desc="makeDLDescription()">
	private static IDLClassExpression<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> makeDLRestrictionTerm(
		final IABox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> abox, final OWLBooleanClassExpression owlDesc)
		throws EInconsistentRBoxException
	{
		if (owlDesc instanceof OWLObjectComplementOf) {
			/*
			 * negation
			 */
			final OWLObjectComplementOf owlComplement = (OWLObjectComplementOf) owlDesc;
			final IDLClassExpression<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> operand = makeDLClassDescription(
				abox,
				owlComplement.getOperand());
			final IDLObjectNegation<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> negation =
				abox.getDLTermFactory().getDLObjectNegation(operand);
			return negation;
		} else if (owlDesc instanceof OWLNaryBooleanClassExpression) {
			final Set<IDLClassExpression<OWLIndividual, OWLLiteral, OWLClass, OWLProperty>> operands = new HashSet<>();
			for (OWLClassExpression owlSubDesc : ((OWLNaryBooleanClassExpression) owlDesc).getOperands()) {
				operands.add(makeDLClassDescription(abox, owlSubDesc));
			}
			if (owlDesc instanceof OWLObjectIntersectionOf) {
				/*
				 * intersection
				 */
				final IDLObjectIntersection<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> intersection = abox.
					getDLTermFactory().getDLObjectIntersection(
					operands);
				return intersection;
			} else if (owlDesc instanceof OWLObjectUnionOf) {
				/*
				 * union
				 */
				final IDLObjectUnion<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> union = abox.
					getDLTermFactory().getDLObjectUnion(
					operands);
				return union;
			}
		}
		throw new IllegalArgumentException("Unsupported boolean description type: " + owlDesc.getClass().
			toString());
	}

	private static IDLClassExpression<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> makeDLRestrictionTerm(
		final IABox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> abox, final OWLRestriction owlDesc)
		throws EInconsistentRBoxException
	{
		/**
		 * Dissect incoming property expression into IDLTerms.
		 *
		 * This method is quite long, since we have to distinguish between data and object properties.
		 */
		/**
		 * OBJECT PROPERTIES
		 *
		 */
		if (owlDesc instanceof OWLObjectSomeValuesFrom) {
			/*
			 * object some restriction
			 */
			final OWLObjectSomeValuesFrom owlSomeRestriction = (OWLObjectSomeValuesFrom) owlDesc;
			final OWLProperty property = owlSomeRestriction.getProperty().asOWLObjectProperty();
			final IDLClassExpression<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> subject = makeDLClassDescription(
				abox,
				owlSomeRestriction.getFiller());
			final IDLSomeRestriction<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> someRestriction = abox.
				getDLTermFactory().
				getDLObjectSomeRestriction(property, subject);
			abox.getAssertedRBox().addRole(property, RoleType.OBJECT_PROPERTY);
			return someRestriction;
		} else if (owlDesc instanceof OWLObjectAllValuesFrom) {
			/*
			 * object all restrictions
			 */
			final OWLObjectAllValuesFrom owlAllRestriction = (OWLObjectAllValuesFrom) owlDesc;
			final OWLProperty property = owlAllRestriction.getProperty().asOWLObjectProperty();
			final IDLClassExpression<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> filler = makeDLClassDescription(
				abox,
				owlAllRestriction.getFiller());

			final IDLAllRestriction<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> allRestriction = abox.
				getDLTermFactory().
				getDLObjectAllRestriction(property, filler);
			abox.getAssertedRBox().addRole(property, RoleType.OBJECT_PROPERTY);

			return allRestriction;
		} else if (owlDesc instanceof OWLObjectHasValue) {
			/*
			 * value restrictions
			 */
			final OWLObjectHasValue owlValueRestriction = (OWLObjectHasValue) owlDesc;
			final OWLClassExpression owlSomeValuesFrom = owlValueRestriction.asSomeValuesFrom();
			/*
			 * convert to someValuesFrom
			 */
			assert !(owlSomeValuesFrom instanceof OWLObjectHasValue);
			final IDLClassExpression<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> desc = makeDLClassDescription(
				abox,
				owlSomeValuesFrom);
			return desc;
			/**
			 * DATA PROPERTIES
			 *
			 */
		} else if (owlDesc instanceof OWLDataSomeValuesFrom) {
			/*
			 * data some restriction
			 */
			final OWLDataSomeValuesFrom owlSomeRestriction = (OWLDataSomeValuesFrom) owlDesc;
			final OWLProperty property = owlSomeRestriction.getProperty().asOWLDataProperty();
			final IDLDataRange<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> subject = convertDataRange(
				abox.getDLTermFactory(),
				owlSomeRestriction.getFiller());
			final IDLDataSomeRestriction<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> someRestriction = abox.
				getDLTermFactory().
				getDLDataSomeRestriction(property, subject);
			abox.getAssertedRBox().addRole(property, RoleType.DATA_PROPERTY);
			return someRestriction;
		} else if (owlDesc instanceof OWLDataAllValuesFrom) {
			/*
			 * data some restriction
			 */
			final OWLDataAllValuesFrom owlAllRestriction = (OWLDataAllValuesFrom) owlDesc;
			final OWLProperty property = owlAllRestriction.getProperty().asOWLDataProperty();
			final IDLDataRange<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> subject = convertDataRange(
				abox.getDLTermFactory(),
				owlAllRestriction.getFiller());
			final IDLAllRestriction<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> allRestriction = abox.
				getDLTermFactory().
				getDLDataAllRestriction(property, subject);
			abox.getAssertedRBox().addRole(property, RoleType.DATA_PROPERTY);
			return allRestriction;
		} else if (owlDesc instanceof OWLDataHasValue) {
			/*
			 * value restrictions
			 */
			final OWLDataHasValue owlValueRestriction = (OWLDataHasValue) owlDesc;
			final OWLClassExpression owlSomeValuesFrom = owlValueRestriction.asSomeValuesFrom();
			/*
			 * convert to someValuesFrom
			 */
			assert !(owlSomeValuesFrom instanceof OWLObjectHasValue);
			final IDLClassExpression<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> desc = makeDLClassDescription(
				abox,
				owlSomeValuesFrom);
			return desc;
		}
		throw new IllegalArgumentException("Unsupported property expression type: " + owlDesc.getClass().
			toString());
	}

	private static IDLClassExpression<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> makeDLClassDescription(
		final IABox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> abox, final OWLClassExpression owlDesc)
		throws EInconsistentRBoxException
	{
		if (owlDesc.isOWLThing()) {
			return abox.getDLTermFactory().getDLThing();
		} else if (owlDesc.isOWLNothing()) {
			return abox.getDLTermFactory().getDLNothing();
		} else if (owlDesc instanceof OWLClass) {
			return abox.getDLTermFactory().getDLClassReference((OWLClass) owlDesc);
		}
		if (owlDesc instanceof OWLBooleanClassExpression) {
			return makeDLRestrictionTerm(abox, (OWLBooleanClassExpression) owlDesc);
		} else if (owlDesc instanceof OWLRestriction) {
			return makeDLRestrictionTerm(abox, (OWLRestriction) owlDesc);
		} else if (owlDesc instanceof OWLObjectOneOf) {
			return makeDLDescription(abox.getDLTermFactory(), (OWLObjectOneOf) owlDesc);
		} else {
			throw new IllegalArgumentException("Unsupported property expression type: " + owlDesc.getClass().
				toString());
		}
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="axiom processing">
	private static void processClassAssertionAxiom(
		final IABox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> abox, final OWLClassAssertionAxiom axiom)
		throws ENodeMergeException, EInconsistencyException
	{
		final OWLIndividual individual = axiom.getIndividual();
		final IIndividualABoxNode<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> node = abox.
			getOrAddIndividualNode(
			individual);
		final OWLClassExpression owlDesc = axiom.getClassExpression();
		final IDLClassExpression<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> desc = makeDLClassDescription(
			abox, owlDesc);
		node.addClassTerm(desc);
	}

	private static void processDataPropertyAssertionAxiom(
		final IABox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> abox, final OWLDataPropertyAssertionAxiom axiom)
		throws ENodeMergeException, EInconsistencyException
	{
		final IAssertedRBox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> rbox = abox.getTBox().
			getAssertedRBox();
		final OWLIndividual source = axiom.getSubject();
		final OWLProperty property = axiom.getProperty().asOWLDataProperty();
		final OWLLiteral target = axiom.getObject();

		rbox.addRole(property, RoleType.DATA_PROPERTY);

		final IABoxNode<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> sourceNode = abox.
			getOrAddIndividualNode(source);
		final IABoxNode<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> targetNode = abox.getOrAddDatatypeNode(
			target);
		if (!sourceNode.getRABox().getAssertedSuccessors().containsValue(property, targetNode.getNodeID())) {
			/*
			 * add link
			 */
			sourceNode.getRABox().getAssertedSuccessors().put(property, targetNode.getNodeID());
		}
	}

	private static void processDataPropertyDomainAxiom(
		final IABox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> abox, final OWLDataPropertyDomainAxiom axiom)
		throws EInconsistencyException
	{
		final IAssertedRBox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> rbox = abox.getAssertedRBox();
		final OWLProperty property = axiom.getProperty().asOWLDataProperty();
		final OWLClassExpression owlDomain = axiom.getDomain();
		rbox.addRole(property, RoleType.DATA_PROPERTY);

		/*
		 * add domain to RBox
		 */
		rbox.getRoleDomains().put(property, makeDLClassDescription(abox, owlDomain));
	}

	private static void processDataPropertyRangeAxiom(
		final IABox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> abox, final OWLDataPropertyRangeAxiom axiom)
		throws EInconsistencyException
	{
		final IAssertedRBox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> rbox = abox.getAssertedRBox();
		final OWLProperty property = axiom.getProperty().asOWLDataProperty();
		final OWLDataRange owlRange = axiom.getRange();
		rbox.addRole(property, RoleType.DATA_PROPERTY);

		/*
		 * add range to RBox
		 */
		rbox.getRoleRanges().put(property, convertDataRange(abox.getDLTermFactory(), owlRange));
	}

	private static void processDisjointClassesAxiom(
		final IABox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> abox, final OWLDisjointClassesAxiom axiom)
		throws EInconsistentRBoxException
	{
		/**
		 * We transform disjoin class axioms into several implications:
		 *
		 * Given a set of classes A1, ..., An and
		 *
		 * (disjoint A1 ... An)
		 *
		 * we add
		 *
		 * (subClassOf A1 (and (not A2) ... (not An))) (subClassOf A2 (and (not A1) (not A3) ... (not An))) ...
		 * (subClassOf An (and (not A1) (not An-1)))
		 *
		 * to the global axiom set.
		 *
		 * If an Ak is simple, this will make sure, the implication will be gathered up by the lazy unfolding algorithm.
		 *
		 * If an Ak is not simple, the implications set will be converted to NNF before reasoning.
		 */
		final Set<OWLClassExpression> owlDescs = axiom.getClassExpressions();
		for (OWLClassExpression owlDesc : owlDescs) {
			final Set<IDLClassExpression<OWLIndividual, OWLLiteral, OWLClass, OWLProperty>> otherDescs = new HashSet<>(
				owlDescs.size() - 1);
			for (OWLClassExpression otherOWLDesc : owlDescs) {
				if (otherOWLDesc != owlDesc) {
					final IDLClassExpression<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> negation = abox.
						getDLTermFactory().getDLObjectNegation(
						makeDLClassDescription(abox, otherOWLDesc));
					otherDescs.add(negation);
				}
			}
			final IDLClassExpression<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> sub = makeDLClassDescription(
				abox, owlDesc);
			IDLClassExpression<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> sup;
			sup = TermUtil.joinToIntersection(otherDescs, abox.getDLTermFactory());

			final IDLImplies<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> implies = abox.getDLTermFactory().
				getDLImplies(sub,
							 sup);
			abox.getTBox().add(implies);
		}
	}

	private static void processEquivalentClassesAxiom(
		final IABox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> abox, final OWLEquivalentClassesAxiom axiom)
		throws EInconsistentRBoxException
	{
		for (OWLSubClassOfAxiom subClassAxiom : axiom.asOWLSubClassOfAxioms()) {
			processSubClassAxiom(abox, subClassAxiom);
		}
	}

	private static void processObjectPropertyAssertionAxiom(final IABox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> abox, final OWLObjectPropertyAssertionAxiom axiom)
		throws ENodeMergeException, EInconsistencyException
	{
		final IAssertedRBox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> rbox = abox.getAssertedRBox();
		final OWLIndividual source = axiom.getSubject();
		final OWLProperty property = axiom.getProperty().asOWLObjectProperty();
		final OWLIndividual target = axiom.getObject();

		rbox.addRole(property, RoleType.OBJECT_PROPERTY);
		final IABoxNode<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> sourceNode = abox.
			getOrAddIndividualNode(
			source);
		final IABoxNode<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> targetNode = abox.
			getOrAddIndividualNode(
			target);
		if (!sourceNode.getRABox().getAssertedSuccessors().containsValue(property, targetNode.getNodeID())) {
			/*
			 * add link
			 */
			sourceNode.getRABox().getAssertedSuccessors().put(property, targetNode.getNodeID());
		}
	}

	private static void processObjectPropertyDomainAxiom(
		final IABox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> abox, final OWLObjectPropertyDomainAxiom axiom)
		throws EInconsistencyException
	{
		final IAssertedRBox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> rbox = abox.getAssertedRBox();
		final OWLProperty property = axiom.getProperty().asOWLObjectProperty();
		final OWLClassExpression owlDomain = axiom.getDomain();
		rbox.addRole(property, RoleType.OBJECT_PROPERTY);

		/*
		 * add domain to RBox
		 */
		rbox.getRoleDomains().put(property, makeDLClassDescription(abox, owlDomain));
	}

	private static void processObjectPropertyRangeAxiom(
		final IABox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> abox, final OWLObjectPropertyRangeAxiom axiom)
		throws EInconsistencyException
	{
		final IAssertedRBox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> rbox = abox.getAssertedRBox();
		final OWLProperty property = axiom.getProperty().asOWLObjectProperty();
		final OWLClassExpression owlRange = axiom.getRange();
		rbox.addRole(property, RoleType.OBJECT_PROPERTY);

		/*
		 * add range to RBox
		 */
		rbox.getRoleRanges().put(property, makeDLClassDescription(abox, owlRange));
	}

	private static void processSubClassAxiom(
		final IABox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> abox, final OWLSubClassOfAxiom axiom)
		throws EInconsistentRBoxException
	{
		final IDLClassExpression<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> sub = makeDLClassDescription(
			abox,
			axiom.getSubClass());
		final IDLClassExpression<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> sup = makeDLClassDescription(
			abox, axiom.getSuperClass());
		final IDLClassExpression<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> implies = abox.
			getDLTermFactory().getDLImplies(
			sub,
			sup);
		abox.getTBox().add(implies);
	}

	private static void processTransitiveObjectPropertyAxiom(
		final IABox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> abox, final OWLTransitiveObjectPropertyAxiom axiom)
		throws EInconsistencyException
	{
		final OWLProperty property = axiom.getProperty().asOWLObjectProperty();
		abox.getAssertedRBox().addRole(property, RoleType.OBJECT_PROPERTY);
		abox.getAssertedRBox().setRoleProperty(property, RoleProperty.TRANSITIVE);
	}

	private static void processSymmetricObjectPropertyAxiom(
		final IABox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> abox, final OWLSymmetricObjectPropertyAxiom axiom)
		throws EInconsistencyException
	{
		final OWLProperty property = axiom.getProperty().asOWLObjectProperty();
		abox.getAssertedRBox().addRole(property, RoleType.OBJECT_PROPERTY);
		abox.getAssertedRBox().setRoleProperty(property, RoleProperty.SYMMETRIC);
	}

	private static void processFunctionalDataPropertyAxiom(
		final IABox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> abox, final OWLFunctionalDataPropertyAxiom axiom)
		throws EInconsistencyException
	{
		final OWLProperty property = axiom.getProperty().asOWLDataProperty();
		abox.getAssertedRBox().addRole(property, RoleType.DATA_PROPERTY);
		abox.getAssertedRBox().setRoleProperty(property, RoleProperty.FUNCTIONAL);
	}

	private static void processFunctionalObjectPropertyAxiom(final IABox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> abox, final OWLFunctionalObjectPropertyAxiom axiom)
		throws EInconsistencyException
	{
		final OWLProperty property = axiom.getProperty().asOWLObjectProperty();
		abox.getAssertedRBox().addRole(property, RoleType.OBJECT_PROPERTY);
		abox.getAssertedRBox().setRoleProperty(property, RoleProperty.FUNCTIONAL);
	}

	private static void processSubObjectPropertyOfAxiom(final IABox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> abox, final OWLSubObjectPropertyOfAxiom ax)
		throws EInconsistencyException
	{
		final IAssertedRBox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> rbox = abox.getAssertedRBox();

		if (!(ax.getSubProperty() instanceof OWLObjectProperty)) {
			throw new IllegalArgumentException("Complex property expressions are not supported: " + ax.getSubProperty());
		}
		if (!(ax.getSuperProperty() instanceof OWLObjectProperty)) {
			throw new IllegalArgumentException(
				"Complex property expressions are not supported: " + ax.getSuperProperty());
		}

		final OWLObjectProperty subProp = ax.getSubProperty().asOWLObjectProperty();
		final OWLObjectProperty supProp = ax.getSuperProperty().asOWLObjectProperty();

		if (!rbox.getRoles().contains(subProp)) {
			rbox.addRole(subProp, RoleType.OBJECT_PROPERTY);
		}
		if (!rbox.getRoles().contains(supProp)) {
			rbox.addRole(supProp, RoleType.OBJECT_PROPERTY);
		}
		rbox.addSubRole(supProp, subProp);
	}

	private static void processSubDataPropertyOfAxiom(final IABox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> abox, final OWLSubDataPropertyOfAxiom ax) throws EInconsistencyException
	{
		final IAssertedRBox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> rbox = abox.getAssertedRBox();

		if (!(ax.getSubProperty() instanceof OWLDataProperty)) {
			throw new IllegalArgumentException("Complex data expressions are not supported: " + ax.getSubProperty());
		}
		if (!(ax.getSuperProperty() instanceof OWLDataProperty)) {
			throw new IllegalArgumentException("Complex data expressions are not supported: " + ax.getSuperProperty());
		}

		final OWLDataProperty subProp = ax.getSubProperty().asOWLDataProperty();
		final OWLDataProperty supProp = ax.getSuperProperty().asOWLDataProperty();

		if (!rbox.getRoles().contains(subProp)) {
			rbox.addRole(subProp, RoleType.DATA_PROPERTY);
		}
		if (!rbox.getRoles().contains(supProp)) {
			rbox.addRole(supProp, RoleType.DATA_PROPERTY);
		}
		rbox.addSubRole(supProp, subProp);
	}

	private static void processAxiom(final IABox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> abox, final OWLAxiom axiom) throws EInconsistencyException, ENodeMergeException
	{
		/*
		 * ABox axioms
		 */
		if (axiom instanceof OWLClassAssertionAxiom) {
			processClassAssertionAxiom(abox, (OWLClassAssertionAxiom) axiom);
		} else if (axiom instanceof OWLDataPropertyAssertionAxiom) {
			processDataPropertyAssertionAxiom(abox, (OWLDataPropertyAssertionAxiom) axiom);
		} else if (axiom instanceof OWLObjectPropertyAssertionAxiom) {
			processObjectPropertyAssertionAxiom(abox, (OWLObjectPropertyAssertionAxiom) axiom);
			/*
			 * TBox, logical axioms
			 */
		} else if (axiom instanceof OWLDisjointClassesAxiom) {
			processDisjointClassesAxiom(abox, (OWLDisjointClassesAxiom) axiom);
		} else if (axiom instanceof OWLEquivalentClassesAxiom) {
			processEquivalentClassesAxiom(abox, (OWLEquivalentClassesAxiom) axiom);
		} else if (axiom instanceof OWLSubClassOfAxiom) {
			processSubClassAxiom(abox, (OWLSubClassOfAxiom) axiom);
			/*
			 * TBox, property axioms
			 */
		} else if (axiom instanceof OWLDataPropertyDomainAxiom) {
			processDataPropertyDomainAxiom(abox, (OWLDataPropertyDomainAxiom) axiom);
		} else if (axiom instanceof OWLDataPropertyRangeAxiom) {
			processDataPropertyRangeAxiom(abox, (OWLDataPropertyRangeAxiom) axiom);
		} else if (axiom instanceof OWLObjectPropertyDomainAxiom) {
			processObjectPropertyDomainAxiom(abox, (OWLObjectPropertyDomainAxiom) axiom);
		} else if (axiom instanceof OWLObjectPropertyRangeAxiom) {
			processObjectPropertyRangeAxiom(abox, (OWLObjectPropertyRangeAxiom) axiom);
		} else if (axiom instanceof OWLTransitiveObjectPropertyAxiom) {
			processTransitiveObjectPropertyAxiom(abox, (OWLTransitiveObjectPropertyAxiom) axiom);
		} else if (axiom instanceof OWLSymmetricObjectPropertyAxiom) {
			processSymmetricObjectPropertyAxiom(abox, (OWLSymmetricObjectPropertyAxiom) axiom);
		} else if (axiom instanceof OWLFunctionalDataPropertyAxiom) {
			processFunctionalDataPropertyAxiom(abox, (OWLFunctionalDataPropertyAxiom) axiom);
		} else if (axiom instanceof OWLFunctionalObjectPropertyAxiom) {
			processFunctionalObjectPropertyAxiom(abox, (OWLFunctionalObjectPropertyAxiom) axiom);
		} else if (axiom instanceof OWLSubObjectPropertyOfAxiom) {
			processSubObjectPropertyOfAxiom(abox, (OWLSubObjectPropertyOfAxiom) axiom);
		} else if (axiom instanceof OWLSubDataPropertyOfAxiom) {
			processSubDataPropertyOfAxiom(abox, (OWLSubDataPropertyOfAxiom) axiom);
		} else if (axiom instanceof OWLAnnotationAxiom) {
			/*
			 * ignore
			 */
			// return;
		} else if (axiom instanceof OWLDeclarationAxiom) {
			/*
			 * ignore
			 */
			// return;
		} else {
			throw new IllegalArgumentException("Unsupported axiom type: " + axiom.getClass());
		}
	}
	/// </editor-fold>
	}
