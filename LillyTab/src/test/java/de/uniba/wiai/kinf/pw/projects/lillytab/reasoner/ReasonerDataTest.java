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

import de.uniba.wiai.kinf.pw.projects.lillytab.IReasonerResult;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IDatatypeABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IIndividualABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.EIllegalTermTypeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleType;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLLiteralReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataIntersection;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataUnion;
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
 * @author peterw
 */
public class ReasonerDataTest {


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

	private final IDLTermFactory<String, String, String, String> _termFactory = new SimpleStringDLTermFactory();
	private final IABoxFactory<String, String, String, String> _aboxFactory = new ABoxFactory<>(
		_termFactory);
	private IABox<String, String, String, String> _abox;
	private Reasoner<String, String, String, String> _reasoner;
	private SimpleKRSSParser _parser;


	public ReasonerDataTest()
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
	public void testDataSome() throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.DATA_PROPERTY);

		final IIndividualABoxNode<String, String, String, String> ind = _abox.createIndividualNode();
		ind.addClassTerm(_parser.parse("(some r {\"a\"})"));

		Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.checkConsistency(_abox);
		assertEquals(1, results.size());
		final IABox<String, String, String, String> result = results.iterator().next().getABox();
		assertEquals(2, result.size());
		final IDatatypeABoxNode<String, String, String, String> aNode = result.getDatatypeNode("a");
		assertTrue(aNode.getTerms().contains(_parser.parseRestriction("{\"a\"}")));
	}


	@Test(expected = EIllegalTermTypeException.class)
	public void testDataSomeIndividual() throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.DATA_PROPERTY);

		final IDatatypeABoxNode<String, String, String, String> ind = _abox.createDatatypeNode();
		ind.addTerm(_parser.parse("(some r {a})"));
	}


	@Test
	public void testDataUnion()
		throws ParseException, ENodeMergeException, ENodeMergeException, EReasonerException, EInconsistencyException
	{
		final IDatatypeABoxNode<String, String, String, String> lit = _abox.createDatatypeNode();

		final IDLLiteralReference<String, String, String, String> litA =
			_termFactory.getDLLiteralReference("a");
		final IDLLiteralReference<String, String, String, String> litB =
			_termFactory.getDLLiteralReference("b");
		final IDLDataUnion<String, String, String, String> union = _termFactory.getDLDataUnion(litA, litB);

		lit.addDataTerm(union);
		Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.checkConsistency(_abox,
																												   false);
		assertEquals(2, results.size());
	}


	@Test(expected = EInconsistencyException.class)
	public void testDataIntersectionClash()
		throws ParseException, ENodeMergeException, ENodeMergeException, EReasonerException, EInconsistencyException
	{
		final IDatatypeABoxNode<String, String, String, String> lit = _abox.createDatatypeNode();

		final IDLLiteralReference<String, String, String, String> litA =
			_termFactory.getDLLiteralReference("a");
		final IDLLiteralReference<String, String, String, String> litB =
			_termFactory.getDLLiteralReference("b");
		final IDLDataIntersection<String, String, String, String> intersection = _termFactory.getDLDataIntersection(litA,
																													litB);

		lit.addDataTerm(intersection);
		Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.checkConsistency(_abox,
																												   false);
	}


}