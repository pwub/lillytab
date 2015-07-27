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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner;

import de.uniba.wiai.kinf.pw.projects.lillytab.IReasonerResult;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IIndividualABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleKRSSParser;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleStringDLTermFactory;
import java.text.ParseException;
import java.util.Collection;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 *
 * @author peter
 */
public class ReasonerTBoxGenerationTest
{
	private final IDLTermFactory<String, String, String, String> _termFactory = new SimpleStringDLTermFactory();
	private final IABoxFactory<String, String, String, String> _aboxFactory = new ABoxFactory<>(
		_termFactory);
	private IABox<String, String, String, String> _abox;
	private Reasoner<String, String, String, String> _reasoner;
	private SimpleKRSSParser _parser;

	public ReasonerTBoxGenerationTest()
	{
	}

	@BeforeClass
	public static void setUpClass()
		throws Exception
	{
		LogManager.getLogManager().reset();
		Logger.getLogger("").setLevel(Level.ALL);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.ALL);
		Logger.getLogger("").addHandler(handler);
	}

	@AfterClass
	public static void tearDownClass()
		throws Exception
	{
	}

	@Before
	public void setUp()
	{
		final ReasonerOptions reasonerOptions = new ReasonerOptions();
		reasonerOptions.setTracing(false);
		// reasonerOptions._tracing = true;
		reasonerOptions.setMergeTracking(true);
		_reasoner = new Reasoner<>(reasonerOptions);
		_parser = new SimpleKRSSParser(_termFactory);
		_abox = _aboxFactory.createABox();
	}

	@After
	public void tearDown()
	{
		_reasoner = null;
		_abox = null;
		_parser = null;
	}

	@Test
	public void testTBoxUpdate() throws ENodeMergeException, EReasonerException, ParseException, EInconsistencyException
	{
		final IIndividualABoxNode<String, String, String, String> node = _abox.createIndividualNode();
		node.addClassTerm(_parser.parse("A"));
		final Collection<? extends IReasonerResult<String, String, String, String>> result1 = _reasoner.
			checkConsistency(_abox);
		assertEquals(1, result1.size());
		final IABox<String, String, String, String> abox1 = result1.iterator().next().getABox();
		assertFalse(abox1.hasMoreGeneratingNodes());
		assertFalse(abox1.hasMoreNonGeneratingNodes());
		abox1.getTBox().add(_parser.parse("(implies A B)"));
		assertTrue(abox1.hasMoreGeneratingNodes());
		assertTrue(abox1.hasMoreNonGeneratingNodes());
	}
	
	@Test
	public void testTBoxNoUpdate() throws ENodeMergeException, EReasonerException, ParseException, EInconsistencyException
	{
		final IIndividualABoxNode<String, String, String, String> node = _abox.createIndividualNode();
		node.addClassTerm(_parser.parse("A"));
		final Collection<? extends IReasonerResult<String, String, String, String>> result1 = _reasoner.
			checkConsistency(_abox);
		assertEquals(1, result1.size());
		final IABox<String, String, String, String> abox1 = result1.iterator().next().getABox();
		_reasoner.checkConsistency(abox1);
	}	
}