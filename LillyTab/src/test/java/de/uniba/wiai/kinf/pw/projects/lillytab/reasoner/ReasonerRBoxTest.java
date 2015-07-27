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
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentRBoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleProperty;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleType;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
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
 * @author peterw
 */
public class ReasonerRBoxTest {

	private final IDLTermFactory<String, String, String, String> _termFactory = new SimpleStringDLTermFactory();
	private final IABoxFactory<String, String, String, String> _aboxFactory = new ABoxFactory<>(
		_termFactory);
	private IABox<String, String, String, String> _abox;
	private Reasoner<String, String, String, String> _reasoner;
	private SimpleKRSSParser _parser;


	public ReasonerRBoxTest()
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
	public void testFunctionalRole()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getAssertedRBox().setRoleProperty("r", RoleProperty.FUNCTIONAL);
		final IABoxNode<String, String, String, String> node = _abox.createNode(false);
		node.addTerm(_parser.parse("(some r A)"));
		node.addTerm(_parser.parse("(some r B)"));
		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(
			_abox);
		for (IReasonerResult<String, String, String, String> result : results) {
			final IABox<String, String, String, String> abox = result.getABox();
			assertEquals(2, abox.size());
		}
	}


	@Test
	public void testFunctionalRoleDualLinks()
		throws ParseException, EReasonerException, ENodeMergeException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getAssertedRBox().setRoleProperty("r", RoleProperty.FUNCTIONAL);
		final IABoxNode<String, String, String, String> a = _abox.createNode(false);
		final IABoxNode<String, String, String, String> b = _abox.createNode(false);
		final IABoxNode<String, String, String, String> c = _abox.createNode(false);
		a.getRABox().getAssertedSuccessors().put("r", b.getNodeID());
		a.getRABox().getAssertedSuccessors().put("r", c.getNodeID());
		assertTrue(_reasoner.isConsistent(_abox));
	}


	@Test
	public void testFunctionalRoleDualLinksInconsistent()
		throws ParseException, EReasonerException, ENodeMergeException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getAssertedRBox().setRoleProperty("r", RoleProperty.FUNCTIONAL);
		final IABoxNode<String, String, String, String> a = _abox.createNode(false);
		final IABoxNode<String, String, String, String> b = _abox.createNode(false);
		b.addTerm(_parser.parse("C"));
		final IABoxNode<String, String, String, String> c = _abox.createNode(false);
		c.addTerm(_parser.parse("(not C)"));
		a.getRABox().getAssertedSuccessors().put("r", b.getNodeID());
		a.getRABox().getAssertedSuccessors().put("r", c.getNodeID());
		assertFalse(_reasoner.isConsistent(_abox));
	}


	@Test
	public void testFunctionalRolePropagation()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getAssertedRBox().setRoleProperty("r", RoleProperty.FUNCTIONAL);
		final IABoxNode<String, String, String, String> a = _abox.createNode(false);
		final IABoxNode<String, String, String, String> b = _abox.createNode(false);
		a.getRABox().getAssertedSuccessors().put("r", b.getNodeID());
		a.addTerm(_parser.parse("(some r A)"));
		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(
			_abox);
		for (IReasonerResult<String, String, String, String> result : results) {
			final IABox<String, String, String, String> abox = result.getABox();
			assertEquals(2, abox.size());
			assertTrue(abox.getNode(b.getNodeID()).getTerms().contains(_termFactory.getDLClassReference("A")));
		}
	}


//	@Test
//	public void testInverseFunctionalRoleDualLinks()
//		throws ParseException, EReasonerException, ENodeMergeException, EInconsistencyException
//	{
//		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
//		_abox.getAssertedRBox().setRoleProperty("r", RoleProperty.INVERSE_FUNCTIONAL);
//		final IABoxNode<String, String, String, String> a = _abox.createNode(false);
//		final IABoxNode<String, String, String, String> b = _abox.createNode(false);
//		final IABoxNode<String, String, String, String> c = _abox.createNode(false);
//		a.getRABox().getAssertedSuccessors().put("r", c.getNodeID());
//		b.getRABox().getAssertedSuccessors().put("r", c.getNodeID());
//		assertTrue(_reasoner.isConsistent(_abox));
//	}


//	@Test
//	public void testInverseFunctionalInconsistentRoleDualLinks()
//		throws ParseException, EReasonerException, ENodeMergeException, EInconsistencyException
//	{
//		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
//		_abox.getAssertedRBox().setRoleProperty("r", RoleProperty.INVERSE_FUNCTIONAL);
//		final IABoxNode<String, String, String, String> a = _abox.createNode(false);
//		a.addTerm(_parser.parse("A"));
//		final IABoxNode<String, String, String, String> b = _abox.createNode(false);
//		b.addTerm(_parser.parse("(not A)"));
//		final IABoxNode<String, String, String, String> c = _abox.createNode(false);
//		a.getRABox().getAssertedSuccessors().put("r", c.getNodeID());
//		b.getRABox().getAssertedSuccessors().put("r", c.getNodeID());
//		assertFalse(_reasoner.isConsistent(_abox));
//	}


	@Test
	public void testFunctionalRoleMerge()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		/**
		 * A node with two ephemeral successors that later get merged into one.
		 *
		 * c --r-> a \-r-> b
		 *
		 * r is functional, this a and b need to be merged.
		 *
		 *
		 */
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getAssertedRBox().setRoleProperty("r", RoleProperty.FUNCTIONAL);
		final IABoxNode<String, String, String, String> a = _abox.getOrAddIndividualNode("a");
		final IABoxNode<String, String, String, String> b = _abox.getOrAddIndividualNode("b");
		final IABoxNode<String, String, String, String> c = _abox.createNode(false);

		c.getRABox().getAssertedSuccessors().put("r", a.getNodeID());
		c.addTerm(_parser.parse("(some r {b})"));

		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(
			_abox);
		for (IReasonerResult<String, String, String, String> result : results) {
			assertEquals(2, result.getABox().size());
		}
	}


	@Test
	public void testFunctionalRoleMerge2()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getAssertedRBox().setRoleProperty("r", RoleProperty.FUNCTIONAL);
		final IABoxNode<String, String, String, String> a = _abox.getOrAddIndividualNode("a");
		final IABoxNode<String, String, String, String> b = _abox.createNode(false);
		final IABoxNode<String, String, String, String> c = _abox.createNode(false);

		a.getRABox().getAssertedSuccessors().put("r", b.getNodeID());
		a.getRABox().getAssertedSuccessors().put("r", c.getNodeID());
		a.addTerm(_parser.parse("(only r {b})"));
		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(
			_abox);
		for (IReasonerResult<String, String, String, String> result : results) {
			assertEquals(2, result.getABox().size());
		}
	}


	@Test
	public void testSubRoleLink()
		throws EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getAssertedRBox().addRole("sub", RoleType.OBJECT_PROPERTY);
		IABoxNode<String, String, String, String> a0 = _abox.getOrAddIndividualNode("a0");
		IABoxNode<String, String, String, String> a1 = _abox.getOrAddIndividualNode("a1");
		_abox.getAssertedRBox().addSubRole("r", "sub");
		a0.getRABox().getAssertedSuccessors().put("sub", a1.getNodeID());
		assertTrue(a0.getRABox().hasSuccessor("r", a1));
	}


	@Test
	public void testSubRoleExistsBlock()
		throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		IABoxNode<String, String, String, String> a0 = _abox.getOrAddIndividualNode("a0");
		a0.addTerm(_parser.parse("(only r A)"));
		IABoxNode<String, String, String, String> a1 = _abox.getOrAddIndividualNode("a1");
		a1.addTerm(_parser.parse("A"));

		_abox.getAssertedRBox().addRole("sub", RoleType.OBJECT_PROPERTY);
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getAssertedRBox().addSubRole("r", "sub");
		a0.getRABox().getAssertedSuccessors().put("sub", a1.getNodeID());

		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(
			_abox);
		assertEquals(1, results.size());
		final IReasonerResult<String, String, String, String> result = results.iterator().next();
		for (NodeID succID : result.getABox().getNode(a0.getNodeID()).getRABox().getSuccessors("sub")) {
			assertEquals(a1.getNodeID(), succID);
		}
	}


	@Test
	public void testTransitiveRole()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getAssertedRBox().setRoleProperty("r", RoleProperty.TRANSITIVE);
		final IDLClassExpression<String, String, String, String> A = _parser.parse("A");
		IABoxNode<String, String, String, String> a = _abox.getOrAddIndividualNode("a");
		a.addTerm(_parser.parse("(only r A)"));
		IABoxNode<String, String, String, String> b = _abox.getOrAddIndividualNode("b");
		IABoxNode<String, String, String, String> c = _abox.getOrAddIndividualNode("c");
		a.getRABox().getAssertedSuccessors().put("r", b.getNodeID());
		b.getRABox().getAssertedSuccessors().put("r", c.getNodeID());

		Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(_abox);
		assertEquals(1, results.size());
		for (IReasonerResult<String, String, String, String> result : results) {
			final IABox<String, String, String, String> abox = result.getABox();
			a = abox.getIndividualNode("a");
			b = abox.getIndividualNode("b");
			c = abox.getIndividualNode("c");
			assertTrue(c.getTerms().contains(_parser.parse(("A"))));
			assertTrue(c.getTerms().contains(_parser.parse(("(only r A)"))));

			// assertFalse(a.getTerms().contains(A));
			assertTrue(b.getTerms().contains(A));
			assertTrue(c.getTerms().contains(A));
		}
	}


//	@Test
//	public void testSymmetricRole()
//		throws ParseException, EReasonerException, EInconsistencyException
//	{
//		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
//		_abox.getAssertedRBox().setRoleProperty("r", RoleProperty.SYMMETRIC);
//		final IDLClassExpression<String, String, String, String> A = _parser.parse("A");
//		IABoxNode<String, String, String, String> a = _abox.getOrAddIndividualNode("a");
//		IABoxNode<String, String, String, String> b = _abox.getOrAddIndividualNode("b");
//		_abox.getTBox().add(_parser.parse("(only r B)"));
//		a.getRABox().getAssertedSuccessors().put("r", b.getNodeID());
//
//		Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
//			checkConsistency(_abox);
//		assertEquals(1, results.size());
//		for (IReasonerResult<String, String, String, String> result : results) {
//			final IABox<String, String, String, String> abox = result.getABox();
//			a = abox.getIndividualNode("a");
//			b = abox.getIndividualNode("b");
//			assertTrue(a.getRABox().getAssertedSuccessors().containsValue("r", b.getNodeID()));
//			assertTrue(b.getRABox().getAssertedSuccessors().containsValue("r", a.getNodeID()));
//			assertTrue(a.getTerms().contains(_parser.parse("B")));
//		}
//	}


	@Test(expected = EInconsistentRBoxException.class)
	public void testTransitiveFunctionalInconsistency()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getAssertedRBox().setRoleProperty("r", RoleProperty.TRANSITIVE);
		_abox.getAssertedRBox().setRoleProperty("r", RoleProperty.FUNCTIONAL);

		final IABoxNode<String, String, String, String> aNode = _abox.createIndividualNode();
		final IABoxNode<String, String, String, String> bNode = _abox.createIndividualNode();
		final IABoxNode<String, String, String, String> cNode = _abox.createIndividualNode();
		aNode.getRABox().getAssertedSuccessors().put("r", bNode);
		bNode.getRABox().getAssertedSuccessors().put("r", cNode);
		_reasoner.checkConsistency(_abox);
	}


	@Test
	public void testFunctionalExistsMerge()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getAssertedRBox().setRoleProperty("r", RoleProperty.FUNCTIONAL);
		_abox.getTBox().add(_parser.parse("(some r (and A {a}))"));

		final IABoxNode<String, String, String, String> aNode = _abox.createIndividualNode();
		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.checkConsistency(
			_abox);
		assertEquals(1, results.size());
		final IABox<String, String, String, String> abox = results.iterator().next().getABox();
		assertEquals(2, abox.size());
		assertTrue(abox.getIndividualNode("a").getRABox().hasSuccessor("r", abox.getIndividualNode("a")));
	}
}