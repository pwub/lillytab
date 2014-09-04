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
 **/
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner;

import de.uniba.wiai.kinf.pw.projects.lillytab.IReasoner;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.io.OWLAPIDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.io.OWLAPILoader;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ReasonerDatatypeTest {

	private OWLOntologyManager _ontoManager = OWLManager.createOWLOntologyManager();
	private String _ontologyURI = "http://www.example.org/ontologies/";
	private PrefixManager _nsManager = new DefaultPrefixManager(_ontologyURI);
	private OWLDataFactory _dataFactory = _ontoManager.getOWLDataFactory();
	private OWLAPILoader _loader;
	private IDLTermFactory<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> _termFactory = new OWLAPIDLTermFactory(
		_dataFactory);
	private IABoxFactory<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> _aboxFactory = new ABoxFactory<>(
		_termFactory);
	private IABox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> _abox;
	private IReasoner<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> _reasoner;

	public ReasonerDatatypeTest()
	{
	}


	@BeforeClass
	public static void setUpClass() throws Exception
	{
	}


	@AfterClass
	public static void tearDownClass() throws Exception
	{
	}


	@Before
	public void setUp()
	{
		_loader = new OWLAPILoader(false);
		_abox = _aboxFactory.createABox();
		_reasoner = new Reasoner<>();
	}


	@After
	public void tearDown()
	{
		_loader = null;
		_abox = null;
		_reasoner = null;
	}
	

	@Test(expected = EInconsistencyException.class)
	public void testDataOneOfWrongLiteral()
		throws OWLOntologyCreationException, OWLOntologyChangeException, EInconsistencyException, EReasonerException
	{
		/* regression test */
		final Set<OWLAxiom> axioms = new HashSet<>();
		final OWLIndividual ind = _dataFactory.getOWLAnonymousIndividual();
		final OWLLiteral litA = _dataFactory.getOWLLiteral("a");
		final OWLLiteral litB = _dataFactory.getOWLLiteral("b");
		
		final OWLDataRange dataRange = _dataFactory.getOWLDataOneOf(litA);
		final OWLDataProperty dataProp = _dataFactory.getOWLDataProperty("dp", _nsManager);
		final OWLDataPropertyRangeAxiom rangeAxiom = _dataFactory.getOWLDataPropertyRangeAxiom(dataProp, dataRange);
		axioms.add(rangeAxiom);
		
		final OWLDataPropertyAssertionAxiom dpAssAx = _dataFactory.getOWLDataPropertyAssertionAxiom(dataProp, ind, litB);
		axioms.add(dpAssAx);

		final OWLOntology onto = _ontoManager.createOntology(axioms, IRI.create(_ontologyURI));

		_loader.fillABox(onto, _abox);
		_reasoner.checkConsistency(_abox);
	}


	@Test(expected = EInconsistencyException.class)
	public void testDatatypeNegationClash() throws OWLOntologyCreationException, OWLOntologyChangeException, EInconsistencyException, EReasonerException
	{
		final Set<OWLAxiom> axioms = new HashSet<>();
		final OWLIndividual ind = _dataFactory.getOWLAnonymousIndividual();
		final OWLLiteral litA = _dataFactory.getOWLLiteral("a");
		final OWLLiteral litB = _dataFactory.getOWLLiteral("b");
		
		final OWLDataRange dataRange = _dataFactory.getOWLDataOneOf(litA);
		final OWLDataProperty dataProp = _dataFactory.getOWLDataProperty("dp", _nsManager);
		final OWLDataPropertyRangeAxiom rangeAxiom = _dataFactory.getOWLDataPropertyRangeAxiom(dataProp, dataRange);
		axioms.add(rangeAxiom);
		
		final OWLDataPropertyAssertionAxiom dpAssAx = _dataFactory.getOWLDataPropertyAssertionAxiom(dataProp, ind, litB);
		axioms.add(dpAssAx);

		final OWLOntology onto = _ontoManager.createOntology(axioms, IRI.create(_ontologyURI));

		_loader.fillABox(onto, _abox);
		_reasoner.checkConsistency(_abox);
	}	
	
	@Test(expected = EInconsistencyException.class)
	public void testDataIntersection() throws ParseException, ENodeMergeException, ENodeMergeException, EReasonerException, EInconsistencyException, OWLOntologyCreationException
	{
		final Set<OWLAxiom> axioms = new HashSet<>();
		final OWLIndividual ind = _dataFactory.getOWLAnonymousIndividual();
		final OWLLiteral litA = _dataFactory.getOWLLiteral("a");
		final OWLDataOneOf dataOneOf = _dataFactory.getOWLDataOneOf(litA);
		final OWLDatatype dt = _dataFactory.getOWLDatatype(OWL2Datatype.XSD_DOUBLE.getIRI());
		final OWLDataIntersectionOf union = _dataFactory.getOWLDataIntersectionOf(dataOneOf, dt);
		final OWLDataProperty dataProp = _dataFactory.getOWLDataProperty("dp", _nsManager);
		final OWLDataSomeValuesFrom someValuesFrom = _dataFactory.getOWLDataSomeValuesFrom(dataProp, union);
		final OWLClassAssertionAxiom clsAx = _dataFactory.getOWLClassAssertionAxiom(someValuesFrom, ind);
		axioms.add(clsAx);
		
		final OWLOntology onto = _ontoManager.createOntology(axioms, IRI.create(_ontologyURI));

		_loader.fillABox(onto, _abox);
		_reasoner.checkConsistency(_abox);

	}
	
	@Test(expected = EInconsistencyException.class)
	public void testDataNegation() throws OWLOntologyCreationException, ENodeMergeException, EInconsistencyException, EReasonerException
	{
		final Set<OWLAxiom> axioms = new HashSet<>();
		
		final OWLIndividual ind = _dataFactory.getOWLAnonymousIndividual();
		final OWLClassAssertionAxiom clsAssert = _dataFactory.getOWLClassAssertionAxiom(_dataFactory.getOWLThing(), ind);
		axioms.add(clsAssert);
				
		final OWLDataProperty dataProp = _dataFactory.getOWLDataProperty("dp", _nsManager);
		final OWLDataPropertyAssertionAxiom propAssert 
			= _dataFactory.getOWLDataPropertyAssertionAxiom(dataProp, ind, 1);
		axioms.add(propAssert);
		
		final OWLDatatype intDatatype = _dataFactory.getIntegerOWLDatatype();
		final OWLDataRange dataNegation = _dataFactory.getOWLDataComplementOf(intDatatype);
		final OWLDataPropertyRangeAxiom rangeAx = _dataFactory.getOWLDataPropertyRangeAxiom(dataProp, dataNegation);
		axioms.add(rangeAx);
		
		final OWLOntology onto = _ontoManager.createOntology(axioms, IRI.create(_ontologyURI));
		
		_loader.fillABox(onto, _abox);
		_reasoner.checkConsistency(_abox);
	}
	
	@Test
	public void testDataDoubleNegation() throws OWLOntologyCreationException, ENodeMergeException, EInconsistencyException, EReasonerException
	{
		final Set<OWLAxiom> axioms = new HashSet<>();
		
		final OWLIndividual ind = _dataFactory.getOWLAnonymousIndividual();
		final OWLClassAssertionAxiom clsAssert = _dataFactory.getOWLClassAssertionAxiom(_dataFactory.getOWLThing(), ind);
		axioms.add(clsAssert);
				
		final OWLDataProperty dataProp = _dataFactory.getOWLDataProperty("dp", _nsManager);
		final OWLDataPropertyAssertionAxiom propAssert 
			= _dataFactory.getOWLDataPropertyAssertionAxiom(dataProp, ind, 1);
		axioms.add(propAssert);
		
		final OWLDatatype intDatatype = _dataFactory.getIntegerOWLDatatype();
		final OWLDataRange dataNegation = _dataFactory.getOWLDataComplementOf(intDatatype);
		final OWLDataRange dataDoubleNegation = _dataFactory.getOWLDataComplementOf(dataNegation);
		final OWLDataPropertyRangeAxiom rangeAx = _dataFactory.getOWLDataPropertyRangeAxiom(dataProp, dataDoubleNegation);
		axioms.add(rangeAx);
		
		final OWLOntology onto = _ontoManager.createOntology(axioms, IRI.create(_ontologyURI));
		
		_loader.fillABox(onto, _abox);
		_reasoner.checkConsistency(_abox);
	}

	@Test(expected = EInconsistencyException.class)
	public void testDataDoubleNegationClash() throws OWLOntologyCreationException, ENodeMergeException, EInconsistencyException, EReasonerException
	{
		final Set<OWLAxiom> axioms = new HashSet<>();
		
		final OWLIndividual ind = _dataFactory.getOWLAnonymousIndividual();
		final OWLClassAssertionAxiom clsAssert = _dataFactory.getOWLClassAssertionAxiom(_dataFactory.getOWLThing(), ind);
		axioms.add(clsAssert);
				
		final OWLDataProperty dataProp = _dataFactory.getOWLDataProperty("dp", _nsManager);
		final OWLDataPropertyAssertionAxiom propAssert 
			= _dataFactory.getOWLDataPropertyAssertionAxiom(dataProp, ind, 0.1);
		axioms.add(propAssert);
		
		final OWLDatatype intDatatype = _dataFactory.getIntegerOWLDatatype();
		final OWLDataRange dataNegation = _dataFactory.getOWLDataComplementOf(intDatatype);
		final OWLDataRange dataDoubleNegation = _dataFactory.getOWLDataComplementOf(dataNegation);
		final OWLDataPropertyRangeAxiom rangeAx = _dataFactory.getOWLDataPropertyRangeAxiom(dataProp, dataDoubleNegation);
		axioms.add(rangeAx);
		
		final OWLOntology onto = _ontoManager.createOntology(axioms, IRI.create(_ontologyURI));
		
		_loader.fillABox(onto, _abox);
		_reasoner.checkConsistency(_abox);
	}
	
	@Test
	public void testDataDoubleIncludeInteger() throws OWLOntologyCreationException, ENodeMergeException, EInconsistencyException, EReasonerException
	{
		final Set<OWLAxiom> axioms = new HashSet<>();
		
		final OWLIndividual ind = _dataFactory.getOWLAnonymousIndividual();
		final OWLClassAssertionAxiom clsAssert = _dataFactory.getOWLClassAssertionAxiom(_dataFactory.getOWLThing(), ind);
		axioms.add(clsAssert);
				
		final OWLDataProperty dataProp = _dataFactory.getOWLDataProperty("dp", _nsManager);
		final OWLDataPropertyAssertionAxiom propAssert 
			= _dataFactory.getOWLDataPropertyAssertionAxiom(dataProp, ind, 1);
		axioms.add(propAssert);
		
		final OWLDatatype doubleDatatype = _dataFactory.getDoubleOWLDatatype();
		final OWLDataPropertyRangeAxiom rangeAx = _dataFactory.getOWLDataPropertyRangeAxiom(dataProp, doubleDatatype);
		axioms.add(rangeAx);
		
		final OWLOntology onto = _ontoManager.createOntology(axioms, IRI.create(_ontologyURI));
		
		_loader.fillABox(onto, _abox);
		_reasoner.checkConsistency(_abox);
	}	
}
