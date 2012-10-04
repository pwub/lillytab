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
package de.uniba.wiai.kinf.pw.projects.lillytab.io;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleProperty;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLAllRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLImplies;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLIntersection;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLNegation;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLNominalReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLSomeRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLUnion;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.TermUtil;
import de.dhke.projects.lutil.LoggingClass;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IAssertedRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleType;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.EUnsupportedDatatypeException;
import java.util.HashSet;
import java.util.Set;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.Namespaces;

/**
 * <p> Loaders are classes that perform the transformation between an external representation of an ontology (ABox +
 * TBox) into the internal representation {@link IABoxNode} of an ABox loader that supports transformation of an
 *
 * @see <a href="http://owlapi.sourcefource.net">OWLAPI</a>}{@link OWLOntology} </p>
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class OWLAPILoader
	extends LoggingClass {

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

	private IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> convertDatatype(
		final IABox<OWLObject, OWLClass, OWLProperty<?, ?>> abox,
		final OWLDatatype datatype)
		throws EUnsupportedDatatypeException
	{
		final IRI datatypeIRI = datatype.getIRI();
		/*
		 * XXX -workaround for broken owlapi manchester syntax parsing
		 */
		final IRI incorrectLiteralIRI = IRI.create(Namespaces.XSD + "Literal");
		if (datatype.isTopDatatype() || (datatype.asOWLDatatype().getIRI().equals(incorrectLiteralIRI))) {
			return abox.getDLTermFactory().getDLThing();
		} else {
			return new OWLAPIDataType(datatypeIRI);
		}
	}


	private IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> convertDataRange(
		final IABox<OWLObject, OWLClass, OWLProperty<?, ?>> abox, final OWLDataRange dataRange)
	{
		if (dataRange instanceof OWLDataComplementOf) {
			final OWLDataComplementOf complementOf = (OWLDataComplementOf) dataRange;
			final IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> subDesc = convertDataRange(abox,
																									complementOf.getDataRange());
			final IDLNegation<OWLObject, OWLClass, OWLProperty<?, ?>> negation = abox.getDLTermFactory().getDLNegation(
				subDesc);
			return negation;
		} else if (dataRange instanceof OWLDataOneOf) {
			final OWLDataOneOf oneOf = (OWLDataOneOf) dataRange;
			Set<IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>>> nomimals = new HashSet<IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>>>();
			for (OWLLiteral literal : oneOf.getValues()) {
				IDLNominalReference<OWLObject, OWLClass, OWLProperty<?, ?>> nominal = abox.getDLTermFactory().
					getDLNominalReference(
					literal);
				nomimals.add(nominal);
			}
			final IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> desc = TermUtil.joinToUnion(nomimals,
																									 abox.getDLTermFactory());
			return desc;
		} else if (dataRange instanceof OWLDatatype) {
			try {
				assert dataRange.isDatatype();
				return convertDatatype(abox, dataRange.asOWLDatatype());
			} catch (EUnsupportedDatatypeException ex) {
				// XXX - propagate?
				throw new IllegalArgumentException(ex);
			}
		} else {
			throw new IllegalArgumentException("Unsupported data range type: " + dataRange.getClass().toString());
		}
	}
	/// </editor-fold>

	/// <editor-fold defaultstate="collapsed" desc="makeDLDescription()">

	private IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> makeDLDescription(
		final IABox<OWLObject, OWLClass, OWLProperty<?, ?>> abox, final OWLBooleanClassExpression owlDesc)
	{
		if (owlDesc instanceof OWLObjectComplementOf) {
			/*
			 * negation
			 */
			final OWLObjectComplementOf owlComplement = (OWLObjectComplementOf) owlDesc;
			final IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> operand = makeDLDescription(abox,
																									 owlComplement.getOperand());
			final IDLNegation<OWLObject, OWLClass, OWLProperty<?, ?>> negation =
				abox.getDLTermFactory().getDLNegation(operand);
			return negation;
		} else if (owlDesc instanceof OWLNaryBooleanClassExpression) {
			final Set<IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>>> operands = new HashSet<IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>>>();
			for (OWLClassExpression owlSubDesc : ((OWLNaryBooleanClassExpression) owlDesc).getOperands())
				operands.add(makeDLDescription(abox, owlSubDesc));
			if (owlDesc instanceof OWLObjectIntersectionOf) {
				/*
				 * intersection
				 */
				final IDLIntersection<OWLObject, OWLClass, OWLProperty<?, ?>> intersection = abox.getDLTermFactory().
					getDLIntersection(
					operands);
				return intersection;
			} else if (owlDesc instanceof OWLObjectUnionOf) {
				/*
				 * union
				 */
				final IDLUnion<OWLObject, OWLClass, OWLProperty<?, ?>> union = abox.getDLTermFactory().getDLUnion(
					operands);
				return union;
			}
		}
		throw new IllegalArgumentException("Unsupported boolean description type: " + owlDesc.getClass().
			toString());
	}


	private IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> makeDLDescription(
		final IABox<OWLObject, OWLClass, OWLProperty<?, ?>> abox, final OWLRestriction owlDesc)
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
			final OWLProperty<?, ?> property = owlSomeRestriction.getProperty().asOWLObjectProperty();
			final IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> subject = makeDLDescription(abox,
																									 owlSomeRestriction.getFiller());
			final IDLSomeRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> someRestriction = abox.getDLTermFactory().
				getDLSomeRestriction(property, subject);
			return someRestriction;
		} else if (owlDesc instanceof OWLObjectAllValuesFrom) {
			/*
			 * object all restrictions
			 */
			final OWLObjectAllValuesFrom owlAllRestriction = (OWLObjectAllValuesFrom) owlDesc;
			final OWLProperty<?, ?> property = owlAllRestriction.getProperty().asOWLObjectProperty();
			final IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> filler = makeDLDescription(abox,
																									owlAllRestriction.getFiller());

			final IDLAllRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> allRestriction = abox.getDLTermFactory().
				getDLAllRestriction(property, filler);
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
			final IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> desc = makeDLDescription(abox,
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
			final OWLProperty<?, ?> property = owlSomeRestriction.getProperty().asOWLDataProperty();
			final IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> subject = convertDataRange(abox,
																									owlSomeRestriction.getFiller());
			final IDLSomeRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> someRestriction = abox.getDLTermFactory().
				getDLSomeRestriction(property, subject);
			return someRestriction;
		} else if (owlDesc instanceof OWLDataAllValuesFrom) {
			/*
			 * data some restriction
			 */
			final OWLDataAllValuesFrom owlAllRestriction = (OWLDataAllValuesFrom) owlDesc;
			final OWLProperty<?, ?> property = owlAllRestriction.getProperty().asOWLDataProperty();
			final IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> subject = convertDataRange(abox,
																									owlAllRestriction.getFiller());
			final IDLAllRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> allRestriction = abox.getDLTermFactory().
				getDLAllRestriction(property, subject);
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
			final IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> desc = makeDLDescription(abox,
																								  owlSomeValuesFrom);
			return desc;
		}
		throw new IllegalArgumentException("Unsupported property expression type: " + owlDesc.getClass().
			toString());
	}


	private IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> makeDLDescription(
		final IABox<OWLObject, OWLClass, OWLProperty<?, ?>> abox, final OWLObjectOneOf owlOneOf)
	{
		/*
		 * object one of
		 */
		Set<IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>>> nomimals = new HashSet<IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>>>();
		for (OWLIndividual individual : owlOneOf.getIndividuals()) {
			IDLNominalReference<OWLObject, OWLClass, OWLProperty<?, ?>> nominal = abox.getDLTermFactory().
				getDLNominalReference(
				individual);
			nomimals.add(nominal);
		}
		IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> desc;
		assert !nomimals.isEmpty();
		if (nomimals.size() == 1)
			desc = nomimals.iterator().next();
		else
			desc = abox.getDLTermFactory().getDLUnion(nomimals);
		return desc;
	}


	private IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> makeDLDescription(
		final IABox<OWLObject, OWLClass, OWLProperty<?, ?>> abox, final OWLDataOneOf owlOneOf)
	{
		/*
		 * data one of
		 */
		Set<IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>>> nomimals = new HashSet<IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>>>();
		for (OWLLiteral literal : owlOneOf.getValues()) {
			IDLNominalReference<OWLObject, OWLClass, OWLProperty<?, ?>> nominal = abox.getDLTermFactory().
				getDLNominalReference(
				literal);
			nomimals.add(nominal);
		}

		final IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> desc;
		assert !nomimals.isEmpty();
		if (nomimals.size() == 1)
			desc = nomimals.iterator().next();
		else
			desc = abox.getDLTermFactory().getDLUnion(nomimals);
		return desc;
	}


	private IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> makeDLDescription(
		final IABox<OWLObject, OWLClass, OWLProperty<?, ?>> abox, final OWLClassExpression owlDesc)
	{
		if (owlDesc.isOWLThing())
			return abox.getDLTermFactory().getDLThing();
		else if (owlDesc.isOWLNothing())
			return abox.getDLTermFactory().getDLNothing();
		else if (owlDesc instanceof OWLClass)
			return abox.getDLTermFactory().getDLClassReference((OWLClass) owlDesc);
		if (owlDesc instanceof OWLBooleanClassExpression)
			return makeDLDescription(abox, (OWLBooleanClassExpression) owlDesc);
		else if (owlDesc instanceof OWLRestriction)
			return makeDLDescription(abox, (OWLRestriction) owlDesc);
		else if (owlDesc instanceof OWLObjectOneOf) {
			return makeDLDescription(abox, (OWLObjectOneOf) owlDesc);
		} else if (owlDesc instanceof OWLDataOneOf) {
			return makeDLDescription(abox, (OWLDataOneOf) owlDesc);
		}
		throw new IllegalArgumentException("Unsupported property expression type: " + owlDesc.getClass().
			toString());
	}

	/// </editor-fold>
	/// <editor-fold defaultstate="collapsed" desc="axiom processing">

	private void processClassAssertionAxiom(final IABox<OWLObject, OWLClass, OWLProperty<?, ?>> abox,
											final OWLClassAssertionAxiom axiom)
		throws EInconsistencyException
	{
		final OWLIndividual individual = axiom.getIndividual();
		final IABoxNode<OWLObject, OWLClass, OWLProperty<?, ?>> node = abox.getOrAddNamedNode(individual, false);
		final OWLClassExpression owlDesc = axiom.getClassExpression();
		final IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> desc = makeDLDescription(abox, owlDesc);
		node.addUnfoldedDescription(desc);
	}


	private void processDataPropertyAssertionAxiom(final IABox<OWLObject, OWLClass, OWLProperty<?, ?>> abox,
												   final OWLDataPropertyAssertionAxiom axiom)
		throws EInconsistencyException
	{
		final IAssertedRBox<OWLObject, OWLClass, OWLProperty<?, ?>> rbox = abox.getTBox().getRBox();
		final OWLIndividual source = axiom.getSubject();
		final OWLProperty<?, ?> property = axiom.getProperty().asOWLDataProperty();
		final OWLObject target = axiom.getObject();

		if (! rbox.getRoles().contains(property))
			rbox.addRole(property, RoleType.DATA_PROPERTY);
		
		final IABoxNode<OWLObject, OWLClass, OWLProperty<?, ?>> sourceNode = abox.getOrAddNamedNode(source, false);
		final IABoxNode<OWLObject, OWLClass, OWLProperty<?, ?>> targetNode = abox.getOrAddNamedNode(target, true);
		if (!sourceNode.getLinkMap().getAssertedSuccessors().containsValue(property, targetNode.getNodeID())) {
			/*
			 * add link
			 */
			sourceNode.getLinkMap().getAssertedSuccessors().put(property, targetNode.getNodeID());
		}
	}


	private void processDataPropertyDomainAxiom(final IABox<OWLObject, OWLClass, OWLProperty<?, ?>> abox,
												final OWLDataPropertyDomainAxiom axiom)
		throws EInconsistencyException
	{
		final IAssertedRBox<OWLObject, OWLClass, OWLProperty<?, ?>> rbox = abox.getTBox().getRBox();
		final OWLProperty<?, ?> property = axiom.getProperty().asOWLDataProperty();
		final OWLClassExpression owlDomain = axiom.getDomain();
		/*
		 * add domain to RBox
		 */
		if (! rbox.getRoles().contains(property))
			rbox.addRole(property, RoleType.DATA_PROPERTY);
		rbox.getRoleDomains().put(property, makeDLDescription(abox, owlDomain));
	}


	private void processDataPropertyRangeAxiom(final IABox<OWLObject, OWLClass, OWLProperty<?, ?>> abox,
											   final OWLDataPropertyRangeAxiom axiom)
		throws EInconsistencyException
	{
		final IAssertedRBox<OWLObject, OWLClass, OWLProperty<?, ?>> rbox = abox.getTBox().getRBox();
		final OWLProperty<?, ?> property = axiom.getProperty().asOWLDataProperty();
		final OWLDataRange owlRange = axiom.getRange();
		/*
		 * add range to RBox
		 */
		if (! rbox.getRoles().contains(property))
			rbox.addRole(property, RoleType.DATA_PROPERTY);
		
		rbox.getRoleRanges().put(property, convertDataRange(abox, owlRange));
	}


	private void processDisjointClassesAxiom(final IABox<OWLObject, OWLClass, OWLProperty<?, ?>> abox,
											 final OWLDisjointClassesAxiom axiom)
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
			final Set<IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>>> otherDescs = new HashSet<IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>>>(
				owlDescs.size() - 1);
			for (OWLClassExpression otherOWLDesc : owlDescs) {
				if (otherOWLDesc != owlDesc) {
					final IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> negation = abox.getDLTermFactory().
						getDLNegation(
						makeDLDescription(abox, otherOWLDesc));
					otherDescs.add(negation);
				}
			}
			final IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> sub = makeDLDescription(abox, owlDesc);
			IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> sup;
			sup = TermUtil.joinToIntersection(otherDescs, abox.getDLTermFactory());

			final IDLImplies<OWLObject, OWLClass, OWLProperty<?, ?>> implies = abox.getDLTermFactory().getDLImplies(sub,
																													sup);
			abox.getTBox().add(implies);
		}
	}


	private void processEquivalentClassesAxiom(final IABox<OWLObject, OWLClass, OWLProperty<?, ?>> abox,
											   final OWLEquivalentClassesAxiom axiom)
	{
		for (OWLSubClassOfAxiom subClassAxiom : axiom.asOWLSubClassOfAxioms())
			processSubClassAxiom(abox, subClassAxiom);
	}


	private void processObjectPropertyAssertionAxiom(final IABox<OWLObject, OWLClass, OWLProperty<?, ?>> abox,
													 final OWLObjectPropertyAssertionAxiom axiom)
		throws EInconsistencyException
	{
		final IAssertedRBox<OWLObject, OWLClass, OWLProperty<?, ?>> rbox = abox.getTBox().getRBox();
		final OWLIndividual source = axiom.getSubject();
		final OWLProperty<?, ?> property = axiom.getProperty().asOWLObjectProperty();
		final OWLObject target = axiom.getObject();

		if (! rbox.getRoles().contains(property)) 
			rbox.addRole(property, RoleType.OBJECT_PROPERTY);
		final IABoxNode<OWLObject, OWLClass, OWLProperty<?, ?>> sourceNode = abox.getOrAddNamedNode(source, false);
		final IABoxNode<OWLObject, OWLClass, OWLProperty<?, ?>> targetNode = abox.getOrAddNamedNode(target, false);
		if (!sourceNode.getLinkMap().getAssertedSuccessors().containsValue(property, targetNode.getNodeID())) {
			/*
			 * add link
			 */
			sourceNode.getLinkMap().getAssertedSuccessors().put(property, targetNode.getNodeID());
		}
	}


	private void processObjectPropertyDomainAxiom(final IABox<OWLObject, OWLClass, OWLProperty<?, ?>> abox,
												  final OWLObjectPropertyDomainAxiom axiom)
		throws EInconsistencyException
	{
		final IAssertedRBox<OWLObject, OWLClass, OWLProperty<?, ?>> rbox = abox.getTBox().getRBox();
		final OWLProperty<?, ?> property = axiom.getProperty().asOWLObjectProperty();
		final OWLClassExpression owlDomain = axiom.getDomain();
		/*
		 * add domain to RBox
		 */
		if (!rbox.getRoles().contains(property))
			rbox.addRole(property, RoleType.OBJECT_PROPERTY);

		rbox.getRoleDomains().put(property, makeDLDescription(abox, owlDomain));
	}


	private void processObjectPropertyRangeAxiom(final IABox<OWLObject, OWLClass, OWLProperty<?, ?>> abox,
												 final OWLObjectPropertyRangeAxiom axiom)
		throws EInconsistencyException
	{
		final IAssertedRBox<OWLObject, OWLClass, OWLProperty<?, ?>> rbox = abox.getTBox().getRBox();
		final OWLProperty<?, ?> property = axiom.getProperty().asOWLObjectProperty();
		final OWLClassExpression owlRange = axiom.getRange();
		/*
		 * add range to RBox
		 *
		 */
		if (!rbox.getRoles().contains(property))
			rbox.addRole(property, RoleType.OBJECT_PROPERTY);

		rbox.getRoleRanges().put(property, makeDLDescription(abox, owlRange));
	}


	private void processSubClassAxiom(final IABox<OWLObject, OWLClass, OWLProperty<?, ?>> abox,
									  final OWLSubClassOfAxiom axiom)
	{
		final IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> sub = makeDLDescription(abox,
																							 axiom.getSubClass());
		final IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> sup = makeDLDescription(abox, axiom.getSuperClass());
		final IDLRestriction<OWLObject, OWLClass, OWLProperty<?, ?>> implies = abox.getDLTermFactory().getDLImplies(
			sub,
			sup);
		abox.getTBox().add(implies);
	}


	private void processTransitiveObjectPropertyAxiom(final IABox<OWLObject, OWLClass, OWLProperty<?, ?>> abox,
													  final OWLTransitiveObjectPropertyAxiom axiom)
		throws EInconsistencyException
	{
		final OWLProperty<?, ?> property = axiom.getProperty().asOWLObjectProperty();
		abox.getTBox().getRBox().addRole(property, RoleType.OBJECT_PROPERTY);
		abox.getTBox().getRBox().setRoleProperty(property, RoleProperty.TRANSITIVE);
	}


	private void processSymmetricObjectPropertyAxiom(final IABox<OWLObject, OWLClass, OWLProperty<?, ?>> abox,
													 final OWLSymmetricObjectPropertyAxiom axiom)
		throws EInconsistencyException
	{
		final OWLProperty<?, ?> property = axiom.getProperty().asOWLObjectProperty();
		abox.getTBox().getRBox().addRole(property, RoleType.OBJECT_PROPERTY);
		abox.getTBox().getRBox().setRoleProperty(property, RoleProperty.SYMMETRIC);
	}


	private void processFunctionalDataPropertyAxiom(final IABox<OWLObject, OWLClass, OWLProperty<?, ?>> abox,
													final OWLFunctionalDataPropertyAxiom axiom)
		throws EInconsistencyException
	{
		final OWLProperty<?, ?> property = axiom.getProperty().asOWLDataProperty();
		abox.getTBox().getRBox().addRole(property, RoleType.DATA_PROPERTY);
		abox.getTBox().getRBox().setRoleProperty(property, RoleProperty.FUNCTIONAL);
	}


	private void processFunctionalObjectPropertyAxiom(final IABox<OWLObject, OWLClass, OWLProperty<?, ?>> abox,
													  final OWLFunctionalObjectPropertyAxiom axiom)
		throws EInconsistencyException
	{
		final OWLProperty<?, ?> property = axiom.getProperty().asOWLObjectProperty();
		abox.getTBox().getRBox().addRole(property, RoleType.OBJECT_PROPERTY);
		abox.getTBox().getRBox().setRoleProperty(property, RoleProperty.FUNCTIONAL);
	}


	private void processAxiom(final IABox<OWLObject, OWLClass, OWLProperty<?, ?>> abox, final OWLAxiom axiom)
		throws EInconsistencyException, ENodeMergeException
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
		} else
			throw new IllegalArgumentException("Unsupported axiom type: " + axiom.getClass());
	}
/// </editor-fold>


	public IABox<OWLObject, OWLClass, OWLProperty<?, ?>> fillABox(final OWLOntology ontology,
																  final IABox<OWLObject, OWLClass, OWLProperty<?, ?>> targetAbox)
		throws EInconsistencyException
	{
		for (OWLAxiom axiom : ontology.getAxioms()) {
			try {
				processAxiom(targetAbox, axiom);
			} catch (IllegalArgumentException ex) {
				if (isIsIgnoreUnsupportedAxioms()) {
					logThrowing(ex);
					logWarning("Unknown axiom of type '%s' ignored.", axiom.getClass());
				} else
					throw ex;
			}
		}

		return targetAbox;
	}


	/**
	 * @return the _isIgnoreUnsupportedAxioms
	 */
	public boolean isIsIgnoreUnsupportedAxioms()
	{
		return _isIgnoreUnsupportedAxioms;
	}


	/**
	 * @param isIgnoreUnsupportedAxioms the _isIgnoreUnsupportedAxioms to set
	 */
	public void setIsIgnoreUnsupportedAxioms(boolean isIgnoreUnsupportedAxioms)
	{
		this._isIgnoreUnsupportedAxioms = isIgnoreUnsupportedAxioms;
	}


	private void processSubObjectPropertyOfAxiom(final IABox<OWLObject, OWLClass, OWLProperty<?, ?>> abox,
												 final OWLSubObjectPropertyOfAxiom ax)
		throws EInconsistencyException
	{
		final IAssertedRBox<OWLObject, OWLClass, OWLProperty<?, ?>> rbox = abox.getTBox().getRBox();

		if (!(ax.getSubProperty() instanceof OWLObjectProperty))
			throw new IllegalArgumentException("Complex property expressions are not supported: " + ax.getSubProperty());
		if (!(ax.getSuperProperty() instanceof OWLObjectProperty))
			throw new IllegalArgumentException(
				"Complex property expressions are not supported: " + ax.getSuperProperty());

		final OWLObjectProperty subProp = ax.getSubProperty().asOWLObjectProperty();
		final OWLObjectProperty supProp = ax.getSuperProperty().asOWLObjectProperty();

		if (!rbox.getRoles().contains(subProp))
			rbox.addRole(subProp, RoleType.OBJECT_PROPERTY);
		if (!rbox.getRoles().contains(supProp))
			rbox.addRole(supProp, RoleType.OBJECT_PROPERTY);
		rbox.addSubRole(supProp, subProp);
	}


	private void processSubDataPropertyOfAxiom(final IABox<OWLObject, OWLClass, OWLProperty<?, ?>> abox,
											   final OWLSubDataPropertyOfAxiom ax)
		throws EInconsistencyException
	{
		final IAssertedRBox<OWLObject, OWLClass, OWLProperty<?, ?>> rbox = abox.getTBox().getRBox();

		if (!(ax.getSubProperty() instanceof OWLDataProperty))
			throw new IllegalArgumentException("Complex data expressions are not supported: " + ax.getSubProperty());
		if (!(ax.getSuperProperty() instanceof OWLDataProperty))
			throw new IllegalArgumentException("Complex data expressions are not supported: " + ax.getSuperProperty());

		final OWLDataProperty subProp = ax.getSubProperty().asOWLDataProperty();
		final OWLDataProperty supProp = ax.getSuperProperty().asOWLDataProperty();

		if (!rbox.getRoles().contains(subProp))
			rbox.addRole(subProp, RoleType.DATA_PROPERTY);
		if (!rbox.getRoles().contains(supProp))
			rbox.addRole(supProp, RoleType.DATA_PROPERTY);
		rbox.addSubRole(supProp, subProp);
	}
}
