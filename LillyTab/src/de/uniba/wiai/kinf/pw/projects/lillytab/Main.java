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
package de.uniba.wiai.kinf.pw.projects.lillytab;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.owlapi.OWLAPILoader;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.Reasoner;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ReasonerOptions;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl.DLTermFactory;
import de.dhke.projects.lutil.LoggingClass;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentABoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.*;
import java.util.Collection;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObject;
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
	implements Runnable
{
	private final IDLTermFactory<OWLObject, OWLClass, OWLProperty<?, ?>> _termFactory;
	private final IABoxFactory<OWLObject, OWLClass, OWLProperty<?, ?>> _aboxFactory;
	private final Reasoner<OWLObject, OWLClass, OWLProperty<?, ?>> _reasoner;

	public Main()
	{
		super();
		initLogger();
		_termFactory = new DLTermFactory<OWLObject, OWLClass, OWLProperty<?, ?>>();
		_aboxFactory = new ABoxFactory<OWLObject, OWLClass, OWLProperty<?, ?>>(_termFactory);
		ReasonerOptions options = new ReasonerOptions();
//		options.TRACE = true;
		options.TRACE = false;
		_reasoner = new Reasoner<OWLObject, OWLClass, OWLProperty<?, ?>>(options);
	}

	private void initLogger()
	{
		LogManager.getLogManager().reset();
		Logger.getLogger("").
			setLevel(Level.ALL);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.ALL);
		Logger.getLogger("").addHandler(handler);
	}

	public void run()
	{
		try {
			final OWLOntologyManager ontMan = OWLManager.createOWLOntologyManager();
			
			//final OWLOntology ontology = ontMan.loadOntology(URI.create(
			//	"http://webrum.uni-mannheim.de/math/lski/anatomy09/mouse_anatomy_2008.owl"));
			// final OWLOntology ontology = ontMan.loadOntology(URI.create(
			//	"http://nb.vse.cz/~svabo/oaei2009/data/sigkdd.owl"));
			final OWLOntology ontology = ontMan.loadOntology(IRI.create("http://www.tssg.org/public/ontologies/omg/mof/2004/EMOF.owl"));
//				"http://webrum.uni-mannheim.de/math/lski/anatomy09/nci_anatomy_2008.owl"));
			
			// final StringOutputTarget tos = new StringOutputTarget();

			final OWLAPILoader loader = new OWLAPILoader();
			final IABox<OWLObject, OWLClass, OWLProperty<?, ?>> initialABox = loader.fillABox(ontology, _aboxFactory.createABox());
			logInfo("Ontology loaded...");
			// System.out.println(abox.toString());

			logInfo("Adding individuals...");
			int individualNumber = 0;
			for (OWLClass klass: ontology.getClassesInSignature()) {
				final IDLClassReference<OWLObject, OWLClass, OWLProperty<?, ?>> klassRef = _termFactory.getDLClassReference(klass);
				OWLIndividual individual = ontMan.getOWLDataFactory().getOWLNamedIndividual(IRI.create("http://www.example.org/#" + individualNumber));
				++individualNumber;
				IABoxNode<OWLObject, OWLClass, OWLProperty<?, ?>> node = initialABox.getOrAddNamedNode(individual, false);
				node.addUnfoldedDescription(klassRef);
				initialABox.add(node);
			}
			
			logInfo("Starting consistency check...");
			final Collection<? extends IReasonerResult<OWLObject, OWLClass, OWLProperty<?, ?>>> aboxes = _reasoner.checkConsistency(initialABox);
			logInfo("Found consistent ABox...");
			for (IReasonerResult<OWLObject, OWLClass, OWLProperty<?, ?>> result: aboxes) {
				final IABox<OWLObject, OWLClass, OWLProperty<?, ?>> abox = result.getABox();
				logInfo(abox.toString());
			}

		} catch (EInconsistentABoxException ex) {
			getLogger().log(Level.SEVERE, "", ex);
		} catch (EReasonerException ex) {
			getLogger().log(Level.SEVERE, "", ex);
		} catch (UnknownOWLOntologyException ex) {
			getLogger().log(Level.SEVERE, "", ex);
		} catch (OWLOntologyCreationException ex) {
			getLogger().log(Level.SEVERE, "", ex);
		}

	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(final String[] args)
	{
		Main main = new Main();
		main.run();
	}
}
