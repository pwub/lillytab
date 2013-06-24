/**
 * (c) 2009-2013 Otto-Friedrich-University Bamberg
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
package de.uniba.wiai.kinf.pw.projects.lillytab;

import de.dhke.projects.lutil.LoggingClass;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
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
import java.util.logging.Logger;
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

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class Main
	extends LoggingClass
	implements Runnable {

	/**
	 * @param args the command line arguments
	 */
	public static void main(final String[] args)
	{
		Main main = new Main();
		main.run();
	}
	private final IDLTermFactory<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> _termFactory;
	private final IABoxFactory<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> _aboxFactory;
	private final Reasoner<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> _reasoner;


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


	private void initLogger()
	{
		LogManager.getLogManager().reset();
		Logger.getLogger("").
			setLevel(Level.ALL);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.FINER);
		Logger.getLogger("").addHandler(handler);
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


			logInfo("Loading ontology from %s", loadIRI);
			final OWLOntology ontology = ontMan.loadOntology(loadIRI);
			logInfo("Ontology loaded.");

			// final StringOutputTarget tos = new StringOutputTarget();

			final OWLAPILoader loader = new OWLAPILoader();
			logInfo("Filling ABox from OWLAPI ontology...");
			final IABox<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>> initialABox = loader.fillABox(ontology,
																											  _aboxFactory.createABox());
			// System.out.println(abox.toString());

			logInfo("Starting classification ...");
			final Collection<IDLImplies<OWLIndividual, OWLLiteral, OWLClass, OWLProperty<?, ?>>> classifications = _reasoner.classify(
				initialABox);
			logInfo("Classification result: %s", classifications);

		} catch (ENodeMergeException ex) {
			logThrowing(ex);
		} catch (EInconsistencyException ex) {
			logThrowing(ex);
		} catch (EReasonerException | UnknownOWLOntologyException | OWLOntologyCreationException ex) {
			logThrowing(ex);
		}

	}
}
