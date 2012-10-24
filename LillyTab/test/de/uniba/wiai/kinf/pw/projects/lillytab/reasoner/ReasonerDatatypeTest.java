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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner;

import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentABoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.io.OWLAPILoader;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.io.OWLAPIDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl.DLTermFactory;
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
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ReasonerDatatypeTest {
	private OWLOntologyManager _ontoManager = OWLManager.createOWLOntologyManager();
	private final String _ontologyURI = "http://www.example.org/ontologies/";
	private PrefixManager _nsManager = new DefaultPrefixManager(_ontologyURI);
	private OWLDataFactory _dataFactory = _ontoManager.getOWLDataFactory();
	private OWLAPILoader _loader;
	private final IDLTermFactory<OWLObject, OWLClass, OWLProperty<?, ?>> _termFactory = new OWLAPIDLTermFactory(
		_dataFactory);
	private final IABoxFactory<OWLObject, OWLClass, OWLProperty<?, ?>> _aboxFactory = new ABoxFactory<OWLObject, OWLClass, OWLProperty<?, ?>>(_termFactory);
	private final IABox<OWLObject, OWLClass, OWLProperty<?, ?>> abox = _aboxFactory.createABox();

    public ReasonerDatatypeTest() {
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
    public void setUp() {
		_loader = new OWLAPILoader(false);
    }

    @After
    public void tearDown() {
		_loader = null;
    }

	@Test
	public void testSingleDataUnion() throws OWLOntologyCreationException, OWLOntologyChangeException, EInconsistencyException
	{
		/* regression test */
		final Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		final OWLLiteral indA = _dataFactory.getOWLStringLiteral("a");
		final OWLDataRange dataRange = _dataFactory.getOWLDataOneOf(indA);
		final OWLDataProperty dataProp = _dataFactory.getOWLDataProperty("dp", _nsManager);
		final OWLDataPropertyRangeAxiom rangeAxiom = _dataFactory.getOWLDataPropertyRangeAxiom(dataProp, dataRange);
		axioms.add(rangeAxiom);

		final OWLOntology onto = _ontoManager.createOntology(axioms, IRI.create(_ontologyURI));

		_loader.fillABox(onto, abox);
	}

}
