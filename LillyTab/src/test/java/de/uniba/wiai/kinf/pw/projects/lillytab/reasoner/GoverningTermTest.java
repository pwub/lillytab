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

import de.uniba.wiai.kinf.pw.projects.lillytab.IReasonerResult;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IIndividualABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleProperty;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleType;
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
 * @author Peter Wullinger <java@dhke.de>
 */
public class GoverningTermTest {

	private final IDLTermFactory<String, String, String, String> _termFactory = new SimpleStringDLTermFactory();
	private final IABoxFactory<String, String, String, String> _aboxFactory = new ABoxFactory<>(
		_termFactory);
	private IABox<String, String, String, String> _abox;
	private Reasoner<String, String, String, String> _reasoner;
	private SimpleKRSSParser _parser;


	public GoverningTermTest()
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
	public void testIntersectionGovTermMove()
		throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		final IIndividualABoxNode<String, String, String, String> node = _abox.createIndividualNode();
		node.addClassTerm(_parser.parse("(and A B)"));
		_abox.getDependencyMap().addGoverningTerm(node, _parser.parse("(and A B)"));
		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(_abox);
		assertEquals(1, results.size());
		final IABox<String, String, String, String> result = results.iterator().next().getABox();
		assertTrue(result.getDependencyMap().hasGoverningTerm(node, _parser.parse("A")));
		assertTrue(result.getDependencyMap().hasGoverningTerm(node, _parser.parse("B")));
		assertFalse(result.getDependencyMap().hasGoverningTerm(node, _parser.parse("(and A B)")));
	}


	@Test
	public void testUnionGovTermIntroductionWasGovTerm()
		throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		final IIndividualABoxNode<String, String, String, String> node = _abox.createIndividualNode();
		node.addClassTerm(_parser.parse("(or A B)"));

		_reasoner.getReasonerOptions().setSemanticBranching(false);
		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(_abox, false);
		assertEquals(2, results.size());

		for (IReasonerResult<String, String, String, String> result : results) {
			final IABox<String, String, String, String> abox = result.getABox();
			assertTrue(abox.getDependencyMap().hasGoverningTerm(node, _parser.parse("A")) ^ abox.getDependencyMap().
				hasGoverningTerm(node, _parser.parse("B")));
			assertFalse(abox.getDependencyMap().hasGoverningTerm(node, _parser.parse("(or A B)")));
		}
	}


	@Test
	public void testUnionGovTermIntroductionNoGovTerm()
		throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		final IIndividualABoxNode<String, String, String, String> node = _abox.createIndividualNode();
		node.addClassTerm(_parser.parse("(or A B)"));
		_abox.getDependencyMap().addGoverningTerm(node, _parser.parse("(or A B)"));

		_reasoner.getReasonerOptions().setSemanticBranching(false);
		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(_abox, false);
		assertEquals(2, results.size());

		for (IReasonerResult<String, String, String, String> result : results) {
			final IABox<String, String, String, String> abox = result.getABox();
			assertTrue(abox.getDependencyMap().hasGoverningTerm(node.getNodeID(), _parser.parse("A")) ^ abox.
				getDependencyMap().hasGoverningTerm(node, _parser.parse("B")));
			assertFalse(abox.getDependencyMap().hasGoverningTerm(node.getNodeID(), _parser.parse("(or A B)")));
		}
	}


	@Test
	public void testSomeTermWasGovTerm()
		throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		final IIndividualABoxNode<String, String, String, String> node = _abox.createIndividualNode();
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);

		node.addClassTerm(_parser.parse("(some r A)"));
		_abox.getDependencyMap().addGoverningTerm(node, _parser.parse("(some r A)"));
		assertTrue(_abox.getDependencyMap().hasGoverningTerm(node, _parser.parse("(some r A)")));

		_reasoner.getReasonerOptions().setSemanticBranching(false);
		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(_abox, false);
		assertEquals(1, results.size());

		for (IReasonerResult<String, String, String, String> result : results) {
			final IABox<String, String, String, String> abox = result.getABox();
			final IABoxNode<String, String, String, String> node2 = abox.getNode(node.getNodeID());
			assertEquals(1, node2.getRABox().getSuccessorNodes().size());
			final IABoxNode<String, String, String, String> succ = node2.getRABox().getSuccessorNodes().iterator().
				next();
			assertTrue(abox.getDependencyMap().hasGoverningTerm(succ, _parser.parse("A")));
			assertFalse(abox.getDependencyMap().hasGoverningTerm(node2, _parser.parse("(some r A)")));
		}
	}


	@Test
	public void testFunctionalSomeTermWasGovTerm()
		throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		final IIndividualABoxNode<String, String, String, String> node = _abox.createIndividualNode();
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getAssertedRBox().setRoleProperty("r", RoleProperty.FUNCTIONAL);

		node.addClassTerm(_parser.parse("(some r A)"));
		_abox.getDependencyMap().addGoverningTerm(node, _parser.parse("(some r A)"));
		assertTrue(_abox.getDependencyMap().hasGoverningTerm(node, _parser.parse("(some r A)")));

		_reasoner.getReasonerOptions().setSemanticBranching(false);
		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(_abox, false);
		assertEquals(1, results.size());

		for (IReasonerResult<String, String, String, String> result : results) {
			final IABox<String, String, String, String> abox = result.getABox();
			final IABoxNode<String, String, String, String> node2 = abox.getNode(node.getNodeID());
			assertEquals(1, node2.getRABox().getSuccessorNodes().size());
			final IABoxNode<String, String, String, String> succ = node2.getRABox().getSuccessorNodes().iterator().
				next();
			assertTrue(abox.getDependencyMap().hasGoverningTerm(succ, _parser.parse("A")));
			assertFalse(abox.getDependencyMap().hasGoverningTerm(node2, _parser.parse("(some r A)")));
		}
	}


	@Test
	public void testFunctionalSomeTermNoGovTerm()
		throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		final IIndividualABoxNode<String, String, String, String> node = _abox.createIndividualNode();
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getAssertedRBox().setRoleProperty("r", RoleProperty.FUNCTIONAL);

		node.addClassTerm(_parser.parse("(some r A)"));

		assertFalse(_abox.getDependencyMap().hasGoverningTerm(node, _parser.parse("(some r A)")));
		_reasoner.getReasonerOptions().setSemanticBranching(false);
		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(_abox, false);
		assertEquals(1, results.size());

		for (IReasonerResult<String, String, String, String> result : results) {
			final IABox<String, String, String, String> abox = result.getABox();
			final IABoxNode<String, String, String, String> node2 = abox.getNode(node.getNodeID());
			assertEquals(1, node2.getRABox().getSuccessorNodes().size());
			final IABoxNode<String, String, String, String> succ = node2.getRABox().getSuccessorNodes().iterator().
				next();
			assertFalse(abox.getDependencyMap().hasGoverningTerm(succ, _parser.parse("A")));
			assertFalse(abox.getDependencyMap().hasGoverningTerm(node2, _parser.parse("(some r A)")));
		}
	}


	@Test
	public void testSomeTermNoGovTerm()
		throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		final IIndividualABoxNode<String, String, String, String> node = _abox.createIndividualNode();
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);

		node.addClassTerm(_parser.parse("(some r A)"));

		assertFalse(_abox.getDependencyMap().hasGoverningTerm(node, _parser.parse("(some r A)")));
		_reasoner.getReasonerOptions().setSemanticBranching(false);
		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(_abox, false);
		assertEquals(1, results.size());

		for (IReasonerResult<String, String, String, String> result : results) {
			final IABox<String, String, String, String> abox = result.getABox();
			final IABoxNode<String, String, String, String> node2 = abox.getNode(node.getNodeID());
			assertEquals(1, node2.getRABox().getSuccessorNodes().size());
			final IABoxNode<String, String, String, String> succ = node2.getRABox().getSuccessorNodes().iterator().
				next();
			assertTrue(abox.getDependencyMap().hasGoverningTerm(succ, _parser.parse("A")));
			assertFalse(abox.getDependencyMap().hasGoverningTerm(node2, _parser.parse("(some r A)")));
		}
	}


	@Test
	public void testForAllTermNoSuccWasGovTerm()
		throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		final IIndividualABoxNode<String, String, String, String> node = _abox.createIndividualNode();
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);

		node.addClassTerm(_parser.parse("(only r A)"));
		_abox.getDependencyMap().addGoverningTerm(node, _parser.parse("(only r A)"));
		assertTrue(_abox.getDependencyMap().hasGoverningTerm(node, _parser.parse("(only r A)")));
		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(_abox, false);
		assertEquals(1, results.size());

		final IABox<String, String, String, String> result = results.iterator().next().getABox();
		assertTrue(result.getDependencyMap().hasGoverningTerm(node.getNodeID(), _parser.parse("(only r A)")));
	}


	@Test
	public void testForAllTermNoSuccNoGovTerm()
		throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		final IIndividualABoxNode<String, String, String, String> node = _abox.createIndividualNode();
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);

		node.addClassTerm(_parser.parse("(only r A)"));
		assertFalse(_abox.getDependencyMap().hasGoverningTerm(node, _parser.parse("(only r A)")));
		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(_abox, false);
		assertEquals(1, results.size());

		final IABox<String, String, String, String> result = results.iterator().next().getABox();
		assertFalse(result.getDependencyMap().hasGoverningTerm(node.getNodeID(), _parser.parse("(only r A)")));
	}


	@Test
	public void testForAllTermFunctionalSuccGovTerm()
		throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getAssertedRBox().setRoleProperty("r", RoleProperty.FUNCTIONAL);

		final IIndividualABoxNode<String, String, String, String> node = _abox.createIndividualNode();
		final IIndividualABoxNode<String, String, String, String> node2 = _abox.createIndividualNode();
		node.getRABox().getAssertedSuccessors().put("r", node2);

		node.addClassTerm(_parser.parse("(only r A)"));
		_abox.getDependencyMap().addGoverningTerm(node, _parser.parse("(only r A)"));
		assertTrue(_abox.getDependencyMap().hasGoverningTerm(node, _parser.parse("(only r A)")));
		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(_abox, false);
		assertEquals(1, results.size());

		final IABox<String, String, String, String> result = results.iterator().next().getABox();
		assertFalse(result.getDependencyMap().hasGoverningTerm(node.getNodeID(), _parser.parse("(only r A)")));
		assertTrue(result.getDependencyMap().hasGoverningTerm(node2.getNodeID(), _parser.parse("A")));
	}


	@Test
	public void testForAllTermTwoSuccNoGovTerm()
		throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		final IIndividualABoxNode<String, String, String, String> node = _abox.createIndividualNode();
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		final IIndividualABoxNode<String, String, String, String> node2 = _abox.createIndividualNode();
		final IIndividualABoxNode<String, String, String, String> node3 = _abox.createIndividualNode();
		node.getRABox().getAssertedSuccessors().put("r", node2);
		node.getRABox().getAssertedSuccessors().put("r", node3);

		node.addClassTerm(_parser.parse("(only r A)"));
		assertFalse(_abox.getDependencyMap().hasGoverningTerm(node, _parser.parse("(only r A)")));
		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(_abox, false);
		assertEquals(1, results.size());

		final IABox<String, String, String, String> result = results.iterator().next().getABox();
		assertFalse(result.getDependencyMap().hasGoverningTerm(node2.getNodeID(), _parser.parse("A")));
		assertFalse(result.getDependencyMap().hasGoverningTerm(node3.getNodeID(), _parser.parse("A")));
	}


	@Test
	public void testForAllTermTwoSuccWasGovTerm()
		throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		final IIndividualABoxNode<String, String, String, String> node = _abox.createIndividualNode();
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		final IIndividualABoxNode<String, String, String, String> node2 = _abox.createIndividualNode();
		final IIndividualABoxNode<String, String, String, String> node3 = _abox.createIndividualNode();
		node.getRABox().getAssertedSuccessors().put("r", node2);
		node.getRABox().getAssertedSuccessors().put("r", node3);

		node.addClassTerm(_parser.parse("(only r A)"));
		_abox.getDependencyMap().addGoverningTerm(node, _parser.parse("(only r A)"));
		assertTrue(_abox.getDependencyMap().hasGoverningTerm(node, _parser.parse("(only r A)")));

		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(_abox, false);
		assertEquals(1, results.size());

		final IABox<String, String, String, String> result = results.iterator().next().getABox();
		assertTrue(_abox.getDependencyMap().hasGoverningTerm(node, _parser.parse("(only r A)")));
		assertFalse(result.getDependencyMap().hasGoverningTerm(node2.getNodeID(), _parser.parse("A")));
		assertFalse(result.getDependencyMap().hasGoverningTerm(node3.getNodeID(), _parser.parse("A")));
	}


	@Test
	public void testComplexTermPropagation1()
		throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		final IIndividualABoxNode<String, String, String, String> node = _abox.createIndividualNode();
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getAssertedRBox().setRoleProperty("r", RoleProperty.FUNCTIONAL);
		final IIndividualABoxNode<String, String, String, String> node2 = _abox.createIndividualNode();
		node.getRABox().getAssertedSuccessors().put("r", node2);

		node.addClassTerm(_parser.parse("(only r (some r (some r A)))"));
		_abox.getDependencyMap().addGoverningTerm(node, _parser.parse("(only r (some r (some r A)))"));
		assertTrue(_abox.getDependencyMap().hasGoverningTerm(node, _parser.parse("(only r (some r (some r A)))")));

		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(_abox, false);
		assertEquals(1, results.size());

		final IABox<String, String, String, String> result = results.iterator().next().getABox();
		final IABoxNode<String, String, String, String> n = result.getNode(node.getNodeID());
		assertFalse(result.getDependencyMap().hasGoverningTerm(n, _parser.parse("(only r (some r (some r A)))")));
		assertFalse(result.getDependencyMap().hasGoverningTerm(n, _parser.parse("(some r (some r A))")));
		assertEquals(1, n.getRABox().getSuccessorNodes().size());
		final IABoxNode<String, String, String, String> n2 = n.getRABox().getSuccessorNodes().iterator().next();
		assertFalse(result.getDependencyMap().hasGoverningTerm(n2, _parser.parse("(some r A)")));
		assertEquals(1, n2.getRABox().getSuccessorNodes().size());
		final IABoxNode<String, String, String, String> n3 = n2.getRABox().getSuccessorNodes().iterator().next();
		assertFalse(result.getDependencyMap().hasGoverningTerm(n3, _parser.parse("(some r A)")));
		assertEquals(1, n3.getRABox().getSuccessorNodes().size());
		final IABoxNode<String, String, String, String> n4 = n3.getRABox().getSuccessorNodes().iterator().next();
		assertTrue(result.getDependencyMap().hasGoverningTerm(n4, _parser.parse("A")));
	}
}
