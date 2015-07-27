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
package de.uniba.wiai.kinf.pw.projects.lillytab;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.io.OWLAPIDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.io.OWLAPILoader;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.EReasonerException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.Reasoner;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.ReasonerOptions;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLImplies;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import java.util.Collection;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class Main
	implements Runnable {

	static final Logger _logger = LoggerFactory.getLogger(Main.class);
	private final IDLTermFactory<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> _termFactory;
	private final IABoxFactory<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> _aboxFactory;
	private final Reasoner<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> _reasoner;


	public Main()
	{
		super();
		initLogger();
		_termFactory = new OWLAPIDLTermFactory(OWLManager.getOWLDataFactory());
		_aboxFactory = new ABoxFactory<>(_termFactory);
		ReasonerOptions options = new ReasonerOptions();
//		options._tracing = true;
		options.setTracing(true);
		_reasoner = new Reasoner<>(options);
	}


	/**
	 * @param args the command line arguments
	 */
	public static void main(final String[] args)
	{
		Main main = new Main();
		main.run();
	}


	@Override
	public void run()
	{
		try {
			final OWLOntologyManager ontMan = OWLManager.createOWLOntologyManager();

//			final IRI loadIRI = IRI.create(
//				"http://seals-test.sti2.at/tdrs-web/testdata/persistent/ca1cddfe-c728-4207-b33d-ca245642f4c9/712477b7-e1f9-4633-8d52-17d2b25af008/suite/anatomy-track1/component/target");
			final IRI loadIRI = IRI.create(
				"https://database.riken.jp/sw/download/ria94i~archives~semantics~ontology_def.owl-rdf.xml");


			_logger.info("Loading ontology from %s", loadIRI);
			final OWLOntology ontology = ontMan.loadOntology(loadIRI);
			_logger.info("Ontology loaded.");

			// final StringOutputTarget tos = new StringOutputTarget();

			final OWLAPILoader loader = new OWLAPILoader();
			_logger.info("Filling ABox from OWLAPI ontology...");
			final IABox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty> initialABox = loader.fillABox(ontology,
																											  _aboxFactory.createABox());
			// System.out.println(abox.toString());

			_logger.info("Starting classification ...");
			final Collection<IDLImplies<OWLIndividual, OWLLiteral, OWLClass, OWLProperty>> classifications = _reasoner.classify(
				initialABox);
			_logger.info("Classification result: %s", classifications);

		} catch (EInconsistencyException | EReasonerException | UnknownOWLOntologyException | OWLOntologyCreationException ex) {
			_logger.error("error during Reasoning", ex);
		}

	}


	private void initLogger()
	{
		LogManager.getLogManager().reset();
		java.util.logging.Logger.getLogger("").
			setLevel(Level.ALL);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.FINER);
		java.util.logging.Logger.getLogger("").addHandler(handler);
	}
}
