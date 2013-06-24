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

/**
 * (c) 2009-2012 Otto-Friedrich-University Bamberg
 *
 * $Id$
 *
 * Use, modification and restribution of this file are covered by the terms of the Artistic License 2.0.
 *
 * You should have received a copy of the license terms in a file named "LICENSE" together with this software package.
 *
 * Disclaimer of Warranty: THE PACKAGE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS "AS IS' AND WITHOUT ANY
 * EXPRESS OR IMPLIED WARRANTIES. THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT ARE DISCLAIMED TO THE EXTENT PERMITTED BY YOUR LOCAL LAW. UNLESS REQUIRED BY LAW, NO COPYRIGHT
 * HOLDER OR CONTRIBUTOR WILL BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING IN ANY
 * WAY OUT OF THE USE OF THE PACKAGE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
import de.uniba.wiai.kinf.pw.projects.lillytab.IReasonerResult;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.io.OWLAPIDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.io.OWLAPILoader;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleType;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
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
import org.semanticweb.owlapi.vocab.XSDVocabulary;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class OWLAPILoaderTest {


	@BeforeClass
	public static void setUpClass()
		throws Exception
	{
	}


	@AfterClass
	public static void tearDownClass()
		throws Exception
	{
	}

	private OWLOntologyManager _ontoManager = OWLManager.createOWLOntologyManager();
	private final String _ontologyURI = "http://www.example.org/ontologies/";
	private PrefixManager _nsManager = new DefaultPrefixManager(_ontologyURI);
	private OWLDataFactory _dataFactory = _ontoManager.getOWLDataFactory();
	private OWLAPILoader _loader;
	private final IDLTermFactory<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> _termFactory = new OWLAPIDLTermFactory(
		_dataFactory);
	private final IABoxFactory<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> _aboxFactory = new ABoxFactory<>(_termFactory);
	private IABox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> _abox;
	private Reasoner<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> _reasoner;


	public OWLAPILoaderTest()
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


	@Test
	public void testStringLiteral() throws OWLOntologyCreationException, OWLOntologyChangeException, EReasonerException, EInconsistencyException
	{
		/* regression test */
		final Set<OWLAxiom> axioms = new HashSet<>();

		final OWLIndividual individual = _dataFactory.getOWLAnonymousIndividual();
		final OWLLiteral literal = _dataFactory.getOWLLiteral("a", OWL2Datatype.XSD_STRING);
		assertEquals(XSDVocabulary.STRING.getIRI(), literal.getDatatype().getIRI());

		final OWLDataProperty dataProp = _dataFactory.getOWLDataProperty("dp", _nsManager);
		final OWLDataPropertyRangeAxiom rangeAxiom = _dataFactory.getOWLDataPropertyRangeAxiom(dataProp,
																							   literal.getDatatype());
		axioms.add(rangeAxiom);

		final OWLDataPropertyAssertionAxiom dataPropAssertion = _dataFactory.getOWLDataPropertyAssertionAxiom(dataProp,
																											  individual,
																											  literal);
		axioms.add(dataPropAssertion);

		final OWLOntology onto = _ontoManager.createOntology(axioms, IRI.create(_ontologyURI));
		_loader.fillABox(onto, _abox);
		_reasoner.checkConsistency(_abox);
	}


	@Test
	public void testTrueBooleanLiteral() throws OWLOntologyCreationException, OWLOntologyChangeException, EReasonerException, EInconsistencyException
	{
		final Set<OWLAxiom> axioms = new HashSet<>();

		final OWLIndividual individual = _dataFactory.getOWLAnonymousIndividual();
		final OWLLiteral literal = _dataFactory.getOWLLiteral("true", OWL2Datatype.XSD_BOOLEAN);
		assertEquals(XSDVocabulary.BOOLEAN.getIRI(), literal.getDatatype().getIRI());

		final OWLDataProperty dataProp = _dataFactory.getOWLDataProperty("dp", _nsManager);
		final OWLDataPropertyRangeAxiom rangeAxiom = _dataFactory.getOWLDataPropertyRangeAxiom(dataProp,
																							   literal.getDatatype());
		axioms.add(rangeAxiom);

		final OWLDataPropertyAssertionAxiom dataPropAssertion = _dataFactory.getOWLDataPropertyAssertionAxiom(dataProp,
																											  individual,
																											  literal);
		axioms.add(dataPropAssertion);

		final OWLOntology onto = _ontoManager.createOntology(axioms, IRI.create(_ontologyURI));
		_loader.fillABox(onto, _abox);
		_reasoner.checkConsistency(_abox);
	}


	@Test
	public void test1BooleanLiteral()
		throws OWLOntologyCreationException, OWLOntologyChangeException, EReasonerException, EInconsistencyException
	{
		final Set<OWLAxiom> axioms = new HashSet<>();

		final OWLIndividual individual = _dataFactory.getOWLAnonymousIndividual();
		final OWLLiteral literal = _dataFactory.getOWLLiteral("1", OWL2Datatype.XSD_BOOLEAN);
		assertEquals(XSDVocabulary.BOOLEAN.getIRI(), literal.getDatatype().getIRI());

		final OWLDataProperty dataProp = _dataFactory.getOWLDataProperty("dp", _nsManager);
		final OWLDataPropertyRangeAxiom rangeAxiom = _dataFactory.getOWLDataPropertyRangeAxiom(dataProp,
																							   literal.getDatatype());
		axioms.add(rangeAxiom);

		final OWLDataPropertyAssertionAxiom dataPropAssertion = _dataFactory.getOWLDataPropertyAssertionAxiom(dataProp,
																											  individual,
																											  literal);
		axioms.add(dataPropAssertion);

		final OWLOntology onto = _ontoManager.createOntology(axioms, IRI.create(_ontologyURI));
		_loader.fillABox(onto, _abox);
		_reasoner.checkConsistency(_abox);
	}


	@Test
	public void testFalseBooleanLiteral()
		throws OWLOntologyCreationException, OWLOntologyChangeException, EReasonerException, EInconsistencyException
	{
		final Set<OWLAxiom> axioms = new HashSet<>();

		final OWLIndividual individual = _dataFactory.getOWLAnonymousIndividual();
		final OWLLiteral literal = _dataFactory.getOWLLiteral("false", OWL2Datatype.XSD_BOOLEAN);
		assertEquals(XSDVocabulary.BOOLEAN.getIRI(), literal.getDatatype().getIRI());

		final OWLDataProperty dataProp = _dataFactory.getOWLDataProperty("dp", _nsManager);
		final OWLDataPropertyRangeAxiom rangeAxiom = _dataFactory.getOWLDataPropertyRangeAxiom(dataProp,
																							   literal.getDatatype());
		axioms.add(rangeAxiom);

		final OWLDataPropertyAssertionAxiom dataPropAssertion = _dataFactory.getOWLDataPropertyAssertionAxiom(dataProp,
																											  individual,
																											  literal);
		axioms.add(dataPropAssertion);

		final OWLOntology onto = _ontoManager.createOntology(axioms, IRI.create(_ontologyURI));
		_loader.fillABox(onto, _abox);
		_reasoner.checkConsistency(_abox);
	}


	@Test
	public void test0BooleanLiteral()
		throws OWLOntologyCreationException, OWLOntologyChangeException, EReasonerException, EInconsistencyException
	{
		final Set<OWLAxiom> axioms = new HashSet<>();

		final OWLIndividual individual = _dataFactory.getOWLAnonymousIndividual();
		final OWLLiteral literal = _dataFactory.getOWLLiteral("0", OWL2Datatype.XSD_BOOLEAN);
		assertEquals(XSDVocabulary.BOOLEAN.getIRI(), literal.getDatatype().getIRI());

		final OWLDataProperty dataProp = _dataFactory.getOWLDataProperty("dp", _nsManager);
		final OWLDataPropertyRangeAxiom rangeAxiom = _dataFactory.getOWLDataPropertyRangeAxiom(dataProp,
																							   literal.getDatatype());
		axioms.add(rangeAxiom);

		final OWLDataPropertyAssertionAxiom dataPropAssertion = _dataFactory.getOWLDataPropertyAssertionAxiom(dataProp,
																											  individual,
																											  literal);
		axioms.add(dataPropAssertion);

		final OWLOntology onto = _ontoManager.createOntology(axioms, IRI.create(_ontologyURI));
		_loader.fillABox(onto, _abox);
		_reasoner.checkConsistency(_abox);
	}


	@Test(expected = EInconsistencyException.class)
	public void testIncorrectBooleanLiteral()
		throws OWLOntologyCreationException, OWLOntologyChangeException, EReasonerException, EInconsistencyException
	{
		final Set<OWLAxiom> axioms = new HashSet<>();

		final OWLIndividual individual = _dataFactory.getOWLAnonymousIndividual();
		final OWLLiteral literal = _dataFactory.getOWLLiteral("xyz");
//		assertEquals(XSDVocabulary.BOOLEAN.getIRI(), literal.getDatatype().getIRI());

		final OWLDataProperty dataProp = _dataFactory.getOWLDataProperty("dp", _nsManager);
		final OWLDataPropertyRangeAxiom rangeAxiom = _dataFactory.getOWLDataPropertyRangeAxiom(dataProp,
																							   _dataFactory.getBooleanOWLDatatype());
		axioms.add(rangeAxiom);

		final OWLDataPropertyAssertionAxiom dataPropAssertion = _dataFactory.getOWLDataPropertyAssertionAxiom(dataProp,
																											  individual,
																											  literal);
		axioms.add(dataPropAssertion);

		final OWLOntology onto = _ontoManager.createOntology(axioms, IRI.create(_ontologyURI));
		_loader.fillABox(onto, _abox);

		final Collection<? extends IReasonerResult<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>>> results = _reasoner.checkConsistency(
			_abox);
	}
	

	@Test
	public void testDataUnion()
		throws OWLOntologyCreationException, ENodeMergeException, EInconsistencyException, EReasonerException
	{
		final Set<OWLAxiom> axioms = new HashSet<>();
		
		final OWLLiteral lit1 = _dataFactory.getOWLLiteral("a");
		final OWLLiteral lit2 = _dataFactory.getOWLLiteral("b");
		final OWLDataOneOf union = _dataFactory.getOWLDataOneOf(lit1, lit2);
		
		final OWLDataProperty dataProp = _dataFactory.getOWLDataProperty("dp", _nsManager);
		
		final OWLDataSomeValuesFrom dataPropAssertionAxiom = _dataFactory.getOWLDataSomeValuesFrom(dataProp,
																										   union);
		final OWLIndividual ind = _dataFactory.getOWLAnonymousIndividual();
		final OWLClassAssertionAxiom classAssertAx = _dataFactory.getOWLClassAssertionAxiom(dataPropAssertionAxiom, ind);
		axioms.add(classAssertAx);
		
		_abox.getAssertedRBox().addRole(dataProp, RoleType.DATA_PROPERTY);
		
		final OWLOntology onto = _ontoManager.createOntology(axioms, IRI.create(_ontologyURI));
		_loader.fillABox(onto, _abox);

		final Collection<? extends IReasonerResult<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>>> results = _reasoner.checkConsistency(
			_abox, false);
		assertEquals(2, results.size());
		
	}
}