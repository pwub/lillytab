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

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.*;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.impl.SWRLTermFactory;
import java.util.Set;
import org.junit.*;
import static org.junit.Assert.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.*;


/**
 *
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public class OWLAPISWRLLoaderTest
{
	private OWLOntologyManager _ontoMan;
	private OWLAPISWRLLoader _loader;
	public OWLAPISWRLLoaderTest()
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
		_ontoMan = OWLManager.createOWLOntologyManager();
		_loader = new OWLAPISWRLLoader(new OWLAPIDLTermFactory(_ontoMan.getOWLDataFactory()),
									   new SWRLTermFactory<OWLIndividual, OWLLiteral, OWLClass, OWLProperty>());
	}

	@After
	public void tearDown()
	{
		_ontoMan = null;
		_loader = null;
	}

	@Test
	public void testClassPropertyRule() throws OWLOntologyCreationException
	{
		final String owlXML = "<Ontology \n"
			+ " xmlns='http://www.w3.org/2003/05/owl-xml'\n"
			+ " xmlns:owlx='http://www.w3.org/2003/05/owl-xml'\n"
			+ "	xmlns:swrlx='http://www.w3.org/2003/11/swrlx'\n"
			+ "	xmlns:ruleml='http://www.w3.org/2003/11/ruleml'\n"
			+ "	name='http://www.example.org/ontology/'\n"
			+ ">\n"
			+ "	<DLSafeRule>\n"
			+ "		<Body>\n"
			+ "			<ClassAtom>\n"
			+ "				<Class IRI='#A' />\n"
			+ "				<Variable IRI='urn:swrl:#x' />\n"
			+ "			</ClassAtom>\n"
			+ "		</Body>\n"
			+ "		<Head>\n"
			+ "			<ObjectPropertyAtom>\n"
			+ "				<ObjectProperty IRI='#r' />\n"
			+ "				<Variable IRI='urn:swrl:#x' />\n"
			+ "				<Individual IRI='#a' />\n"
			+ "			</ObjectPropertyAtom>\n"
			+ "		</Head>\n"
			+ " </DLSafeRule>\n"
			+ "</Ontology>\n";
		final OWLOntologyDocumentSource ontoSource = new StringDocumentSource(owlXML);
		final OWLOntology onto = _ontoMan.loadOntologyFromOntologyDocument(ontoSource);
		final Set<ISWRLRule<OWLIndividual, OWLLiteral, OWLClass, OWLProperty>> rules = _loader.getRules(onto);
		assertEquals(1, rules.size());
		final ISWRLRule<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> rule = rules.iterator().next();
		assertTrue(rule.getBody() instanceof ISWRLClassAtom);
		final ISWRLClassAtom<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> body = (ISWRLClassAtom<OWLIndividual, OWLLiteral, OWLClass, OWLProperty>) rule.
			getBody();
		assertTrue(body.getIndividual() instanceof ISWRLVariable);
		final ISWRLVariable<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> var = (ISWRLVariable<OWLIndividual, OWLLiteral, OWLClass, OWLProperty>) body.
			getIndividual();
		assertTrue(rule.getHead() instanceof ISWRLRoleAtom);
		final ISWRLRoleAtom<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> head = (ISWRLRoleAtom<OWLIndividual, OWLLiteral, OWLClass, OWLProperty>) rule.
			getHead();
		assertEquals(head.getFirstIndividual(), body.getIndividual());
		assertTrue(head.getSecondIndividual() instanceof ISWRLIndividualReference);
	}

	@Test
	public void testClassClassRule() throws OWLOntologyCreationException
	{
		final String owlXML = "<Ontology \n"
			+ " xmlns='http://www.w3.org/2003/05/owl-xml'\n"
			+ " xmlns:owlx='http://www.w3.org/2003/05/owl-xml'\n"
			+ "	xmlns:swrlx='http://www.w3.org/2003/11/swrlx'\n"
			+ "	xmlns:ruleml='http://www.w3.org/2003/11/ruleml'\n"
			+ "	name='http://www.example.org/ontology/'\n"
			+ ">\n"
			+ "	<DLSafeRule>\n"
			+ "		<Body>\n"
			+ "			<ClassAtom>\n"
			+ "				<Class IRI='#A' />\n"
			+ "				<Variable IRI='urn:swrl:#x' />\n"
			+ "			</ClassAtom>\n"
			+ "		</Body>\n"
			+ "		<Head>\n"
			+ "			<ClassAtom>\n"
			+ "				<Class IRI='#B' />\n"
			+ "				<Individual IRI='#a' />\n"
			+ "			</ClassAtom>\n"
			+ "		</Head>\n"
			+ " </DLSafeRule>\n"
			+ "</Ontology>\n";
		final OWLOntologyDocumentSource ontoSource = new StringDocumentSource(owlXML);
		final OWLOntology onto = _ontoMan.loadOntologyFromOntologyDocument(ontoSource);
		final Set<ISWRLRule<OWLIndividual, OWLLiteral, OWLClass, OWLProperty>> rules = _loader.getRules(onto);
		assertEquals(1, rules.size());
		final ISWRLRule<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> rule = rules.iterator().next();
		assertTrue(rule.getBody() instanceof ISWRLClassAtom);
		final ISWRLClassAtom<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> body = (ISWRLClassAtom<OWLIndividual, OWLLiteral, OWLClass, OWLProperty>) rule.
			getBody();
		assertTrue(body.getIndividual() instanceof ISWRLVariable);
		final ISWRLVariable<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> var = (ISWRLVariable<OWLIndividual, OWLLiteral, OWLClass, OWLProperty>) body.
			getIndividual();
		assertTrue(rule.getHead() instanceof ISWRLClassAtom);
		final ISWRLClassAtom<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> head = (ISWRLClassAtom<OWLIndividual, OWLLiteral, OWLClass, OWLProperty>) rule.
			getHead();
		assertTrue(head.getIndividual() instanceof ISWRLIndividualReference);
		final ISWRLIndividualReference<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> nom = (ISWRLIndividualReference<OWLIndividual, OWLLiteral, OWLClass, OWLProperty>) head.
			getIndividual();
	}
}
