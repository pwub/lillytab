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
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.IReasonerResult;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleProperty;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleType;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.TermUtil;
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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ReasonerTest
{
	private final IDLTermFactory<String, String, String> _termFactory = new SimpleStringDLTermFactory();
	private final IABoxFactory<String, String, String> _aboxFactory = new ABoxFactory<String, String, String>(
		_termFactory);
	private IABox<String, String, String> _abox;
	private Reasoner<String, String, String> _reasoner;
	private SimpleKRSSParser _parser;

	public ReasonerTest()
	{
	}

	@BeforeClass
	public static void setUpClass() throws Exception
	{
		LogManager.getLogManager().reset();
		Logger.getLogger("").setLevel(Level.ALL);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.ALL);
		Logger.getLogger("").addHandler(handler);
	}

	@AfterClass
	public static void tearDownClass() throws Exception
	{
	}

	@Before
	public void setUp()
	{
		final ReasonerOptions reasonerOptions = new ReasonerOptions();
		reasonerOptions.TRACE = true;
		reasonerOptions.MERGE_TRACKING = true;
		_reasoner = new Reasoner<String, String, String>(reasonerOptions);
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
	public void testIntersection()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		IABoxNode<String, String, String> aNode = _abox.getOrAddNamedNode("a", false);
		aNode.addUnfoldedDescription(_parser.parse("(and A B)"));

		Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		IDLClassReference<String, String, String> A = _termFactory.getDLClassReference("B");
		IDLClassReference<String, String, String> B = _termFactory.getDLClassReference("B");
		for (IReasonerResult<String, String, String> result : results) {
			final IABox<String, String, String> returnedABox = result.getABox();
			aNode = returnedABox.getOrAddNamedNode("a", false);
			assertTrue(aNode.getTerms().contains(A));
			assertTrue(aNode.getTerms().contains(B));
		}
	}

	@Test
	public void testUnion()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		IDLTermFactory<String, String, String> termFactory = new SimpleStringDLTermFactory();
		SimpleKRSSParser parser = new SimpleKRSSParser(termFactory);

		final IABox<String, String, String> abox = _aboxFactory.createABox();

		IABoxNode<String, String, String> aNode = abox.getOrAddNamedNode("a", false);
		aNode.addUnfoldedDescription(parser.parse("(or A B)"));

		Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(abox);
		IDLClassReference<String, String, String> A = termFactory.getDLClassReference("A");
		IDLClassReference<String, String, String> B = termFactory.getDLClassReference("B");
		// assertEquals(2, aboxes.size());
		for (IReasonerResult<String, String, String> result : results) {
			final IABox<String, String, String> returnedABox = result.getABox();
			aNode = returnedABox.getOrAddNamedNode("a", false);
			assertTrue(aNode.getTerms().contains(A) || aNode.getTerms().contains(B));
		}
	}

	@Test
	public void testUnionInconsistency()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		IDLTermFactory<String, String, String> termFactory = new SimpleStringDLTermFactory();
		SimpleKRSSParser parser = new SimpleKRSSParser(termFactory);

		final IABox<String, String, String> abox = _aboxFactory.createABox();
		final IABoxNode<String, String, String> aNode = abox.getOrAddNamedNode("a", false);
		aNode.addUnfoldedDescription(parser.parse("(or A B)"));
		aNode.addUnfoldedDescription(parser.parse("(not A)"));
		aNode.addUnfoldedDescription(parser.parse("(not B)"));

		Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(abox);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testOnly()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);

		final IABoxNode<String, String, String> aNode = _abox.getOrAddNamedNode("a", false);
		final IABoxNode<String, String, String> bNode = _abox.getOrAddNamedNode("b", false);
		aNode.getRABox().getAssertedSuccessors().put("r", bNode.getNodeID());

		bNode.addUnfoldedDescription(_parser.parse("(not B)"));
		aNode.addUnfoldedDescription(TermUtil.toNNF(_parser.parse("(only r B)"), _termFactory));

		final Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testExistsNewNode()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		IABoxNode<String, String, String> aNode = _abox.getOrAddNamedNode("a", false);
		IABoxNode<String, String, String> bNode = _abox.getOrAddNamedNode("b", false);
		aNode.getRABox().getAssertedSuccessors().put("r", bNode.getNodeID());

		aNode.addUnfoldedDescription(TermUtil.toNNF(_parser.parse("(some r (not B))"), _termFactory));
		bNode.addUnfoldedDescription(_parser.parse("B"));

		Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		/* all ABoxes must have exactly one generated node */
		for (IReasonerResult<String, String, String> result : results) {
			int anonCount = 0;
			for (IABoxNode<String, String, String> node : result.getABox())
				if (node.isAnonymous())
					++anonCount;
			assertEquals(1, anonCount);
		}
	}

	@Test
	public void testUnionSubClass()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		final IABoxNode<String, String, String> node = _abox.createNode(false);
		node.addUnfoldedDescription(TermUtil.toNNF(_parser.parse("(not (implies A (or A B)))"), _termFactory));

		final Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testGeneratedExistsOnly()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		final IABoxNode<String, String, String> aNode = _abox.getOrAddNamedNode("a", false);

		aNode.addUnfoldedDescription(TermUtil.toNNF(_parser.parse("(only r B)"), _termFactory));
		aNode.addUnfoldedDescription(TermUtil.toNNF(_parser.parse("(some r (not B))"), _termFactory));

		final Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testExistsNoGenerate()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		final IABoxNode<String, String, String> aNode = _abox.getOrAddNamedNode("a", false);
		final IABoxNode<String, String, String> bNode = _abox.getOrAddNamedNode("b", false);

		aNode.addUnfoldedDescription(TermUtil.toNNF(_parser.parse("(some r B)"), _termFactory));
		bNode.addUnfoldedDescription(_termFactory.getDLClassReference("B"));
		_reasoner.checkConsistency(_abox);

	}

	@Test
	public void testCyclicOnly()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		IABoxNode<String, String, String> aNode = _abox.getOrAddNamedNode("a", false);
		IABoxNode<String, String, String> bNode = _abox.getOrAddNamedNode("b", false);
		aNode.getRABox().getAssertedSuccessors().put("r", bNode.getNodeID());
		bNode.getRABox().getAssertedSuccessors().put("r", aNode.getNodeID());

		aNode.addUnfoldedDescription(TermUtil.toNNF(_parser.parse("(only r (and (not B) (only r A)))"), _termFactory));
		_abox.getTBox().add(_parser.parse("(implies A (only r B))"));

		Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testForAllPropagate()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		/**
		 * Check, if forall rules properly propagate to all successors.
		 */
		IABoxNode<String, String, String> aNode = _abox.getOrAddNamedNode("a", false);
		IABoxNode<String, String, String> bNode = _abox.getOrAddNamedNode("b", false);
		IABoxNode<String, String, String> cNode = _abox.getOrAddNamedNode("c", false);

		aNode.getRABox().getAssertedSuccessors().put("r", bNode.getNodeID());
		aNode.getRABox().getAssertedSuccessors().put("r", cNode.getNodeID());

		aNode.addUnfoldedDescription(_parser.parse("(only r B)"));

		final Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		assertEquals(1, results.size());
		final IDLClassReference<String, String, String> B = _termFactory.getDLClassReference("B");
		for (IReasonerResult<String, String, String> result : results) {
			assertEquals(3, result.getABox().size());
			assertTrue(result.getABox().getNode("b").getTerms().contains(B));
			assertTrue(result.getABox().getNode("c").getTerms().contains(B));
		}
	}

	@Test
	public void testRangeClash()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		/**
		 * Given a role with explicit range assertion "NOT B", and an ABox
		 *
		 * {a}, -> {b}, B
		 *
		 * check if reasoning detects a clash.
		 *
		 */
		final IABoxNode<String, String, String> aNode = _abox.getOrAddNamedNode("a", false);
		final IABoxNode<String, String, String> bNode = _abox.getOrAddNamedNode("b", false);
		_abox.getTBox().getRBox().getRoleRanges().put("r", _termFactory.getDLClassReference("B"));

		aNode.getRABox().getAssertedSuccessors().put("r", bNode.getNodeID());

		_abox.getTBox().getRBox().getRoleRanges().put("r", _parser.parse(("(NOT B)")));
		Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testRangePropagation()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		/**
		 * Given a role with explicit range assertion "B", and an ABox
		 *
		 * {a}, -> {b}
		 *
		 * check if {b} is a an instance of B after reasoning.
		 *
		 */
		final IABoxNode<String, String, String> aNode = _abox.getOrAddNamedNode("a", false);
		final IABoxNode<String, String, String> bNode = _abox.getOrAddNamedNode("b", false);

		aNode.getRABox().getAssertedSuccessors().put("r", bNode.getNodeID());

		_abox.getTBox().getRBox().getRoleRanges().put("r", _termFactory.getDLClassReference("B"));
		assertFalse(_abox.getNode("b").getTerms().contains(_termFactory.getDLClassReference("B")));
		final Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		for (IReasonerResult<String, String, String> result : results) {
			assertEquals(2, result.getABox().size());
			assertTrue(result.getABox().getNode("b").getTerms().contains(_termFactory.getDLClassReference("B")));
		}
	}

	@Test
	public void testDomainClash()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		/**
		 * Given a role with explicit domain assertion "A", and an ABox
		 *
		 * {a}, (NOT A) -> {b},
		 *
		 * check if reasoning detects a clash.
		 *
		 */
		final IABoxNode<String, String, String> aNode = _abox.getOrAddNamedNode("a", false);
		aNode.addUnfoldedDescription(_termFactory.getDLClassReference("A"));
		final IABoxNode<String, String, String> bNode = _abox.getOrAddNamedNode("b", false);
		bNode.addUnfoldedDescription(_termFactory.getDLClassReference("B"));

		aNode.getRABox().getAssertedSuccessors().put("r", bNode.getNodeID());

		_abox.getTBox().getRBox().getRoleDomains().put("r", _parser.parse(("(NOT A)")));
		_reasoner.checkConsistency(_abox);
	}

	@Test
	public void testDomainPropagation()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		/**
		 * Given a role with explicit domain assertion "A", and an Abox
		 *
		 * {a} -> {b},
		 *
		 * check if {a} is an instanceof of A after reasoning.
		 *
		 */
		final IABoxNode<String, String, String> aNode = _abox.getOrAddNamedNode("a", false);
		final IABoxNode<String, String, String> bNode = _abox.getOrAddNamedNode("b", false);

		aNode.getRABox().getAssertedSuccessors().put("r", bNode.getNodeID());

		_abox.getTBox().getRBox().getRoleDomains().put("r", _termFactory.getDLClassReference("A"));
		assertFalse(_abox.getNode("a").getTerms().contains(_termFactory.getDLClassReference("A")));
		final Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		for (IReasonerResult<String, String, String> result : results) {
			assertEquals(2, result.getABox().size());
			assertTrue(result.getABox().getNode("a").getTerms().contains(_termFactory.getDLClassReference("A")));
		}
	}

//	@Test
//	public void testBlockNamed()
//		throws ParseException, EReasonerException, EInconsistentABoxException
//	{
//		/**
//		 * Start with three nodes:
//		 *
//		 * {a}
//		 * {b} -r-> {c}
//		 *
//		 * Make sure, that {c} is not blocked by {a}.
//		 *
//		 **/
//		IABoxNode<String, String, String> aNode = _abox.getOrAddNamedNode("a", false);
//		IABoxNode<String, String, String> bNode = _abox.getOrAddNamedNode("b", false);
//		IABoxNode<String, String, String> cNode = _abox.getOrAddNamedNode("c", false);
//
//		_abox.getTBox().add(_parser.parse("(some r B)"));
//		Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
//		for (IReasonerResult<String, String, String> result : results) {
//			final IABox<String, String, String> abox = result.getABox();
//			aNode = abox.getNode("a");
//			assertEquals(1, aNode.getRABox().getAssertedSuccessors().get("r").size());
//			bNode = abox.getNode("b");
//			assertEquals(1, bNode.getRABox().getAssertedSuccessors().get("r").size());
//			cNode = abox.getNode("c");
//			assertEquals(1, cNode.getRABox().getAssertedSuccessors().get("r").size());
//
//			NodeID blockedNodeID = cNode.getRABox().getAssertedSuccessors().get("r").iterator().next();
//			IABoxNode<String, String, String> blockedNode = abox.getNode(blockedNodeID);
//			assertNotNull(_reasoner.getBlockingStrategy().getBlocker(blockedNode));
//			assertTrue(_reasoner.getBlockingStrategy().isBlocked(blockedNode));
//		}
//
//	}
//
//	@Test
//	public void testBlockAnonynmous()
//		throws ParseException, EReasonerException, EInconsistentABoxException
//	{
//		/**
//		 * Start with two nodes, the first named, the second anonymous.
//		 * Use a global some-restriction.
//		 *
//		 * Make sure the first node blocks the second node.
//		 **/
//		IABoxNode<String, String, String> aNode = _abox.getOrAddNamedNode("a", false);
//		IABoxNode<String, String, String> bNode = _abox.createNode(false);
//
//		_abox.getTBox().add(_parser.parse("(some r B)"));
//		Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
//		for (IReasonerResult<String, String, String> result : results) {
//			final IABox<String, String, String> abox = result.getABox();
//			aNode = abox.getNode("a");
//			assertEquals(1, aNode.getRABox().getAssertedSuccessors().get("r").size());
//			bNode = abox.getNode(bNode.getNodeID());
//			assertNull(bNode.getRABox().getAssertedSuccessors().get("r"));
//			assertNotNull(_reasoner.getBlockingStrategy().getBlocker(bNode));
//			assertTrue(_reasoner.getBlockingStrategy().isBlocked(bNode));
//		}
//	}
//
//	@Test
//	public void testBlock()
//		throws ParseException, EReasonerException, EInconsistentABoxException
//	{
//		/**
//		 * Start with two named nodes
//		 *
//		 * Make sure the first node blocks the second node.
//		 **/
//		IABoxNode<String, String, String> aNode = _abox.getOrAddNamedNode("a", false);
//		IABoxNode<String, String, String> bNode = _abox.getOrAddNamedNode("b", false);
//
//		_abox.getTBox().add(_parser.parse("(some r B)"));
//		Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
//		for (IReasonerResult<String, String, String> result : results) {
//			final IABox<String, String, String> abox = result.getABox();
//			aNode = abox.getNode("a");
//			assertEquals(1, aNode.getRABox().getAssertedSuccessors().get("r").size());
//			bNode = abox.getNode(bNode.getNodeID());
//			assertEquals(1, bNode.getRABox().getAssertedSuccessors().get("r").size());
//			
//			NodeID blockedNodeID = bNode.getRABox().getAssertedSuccessors().get("r").iterator().next();
//			IABoxNode<String, String, String> blockedNode = abox.getNode(blockedNodeID);
//			assertNotNull(_reasoner.getBlockingStrategy().getBlocker(blockedNode));
//			assertTrue(_reasoner.getBlockingStrategy().isBlocked(blockedNode));
//		}
//	}
//
//
//	@Test
//	public void testCyclicBlock()
//		throws ParseException, EReasonerException, EInconsistentABoxException
//	{
//		/**
//		 * Start with a single node and a generating TBox rule that
//		 * applies to this node and generates a successor that
//		 * may be blocked by the initial node.
//		 *
//		 * Test if the successor is properly blocked by the initial node.
//		 *
//		 **/
//		IABoxNode<String, String, String> aNode = _abox.getOrAddNamedNode("a", false);
//		aNode.addUnfoldedDescription(_parser.parse("A"));
//
//		_abox.getTBox().add(_parser.parse("(some r A)"));
//		Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
//		for (IReasonerResult<String, String, String> result : results) {
//			final IABox<String, String, String> abox = result.getABox();
//			aNode = abox.getNode("a");
//			assertEquals(1, aNode.getRABox().getAssertedSuccessors().get("r").size());
//			NodeID blockedNodeID = aNode.getRABox().getAssertedSuccessors().get("r").iterator().next();
//			IABoxNode<String, String, String> blockedNode = abox.getNode(blockedNodeID);
//			assertEquals(aNode, _reasoner.getBlockingStrategy().getBlocker(blockedNode));
//		}
//
//	}
	@Test
	public void testSimpleInconsistentDescendency()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("containedIn", RoleType.OBJECT_PROPERTY);
		/**
		 * Simple inconsistency. TBox demands a successor, local axiom set forbids it.
		 */
		_abox.getTBox().add(_parser.parse("(implies SpatialObject (some containedIn SpatialObject))"));
		IABoxNode<String, String, String> a = _abox.getOrAddNamedNode("a", false);
		IABoxNode<String, String, String> b = _abox.getOrAddNamedNode("b", false);

		a.addUnfoldedDescription(_parser.parse("SpatialObject"));
		b.addUnfoldedDescription(_parser.parse("SpatialObject"));
		b.getRABox().getAssertedSuccessors().put("containedIn", a.getNodeID());
		b.addUnfoldedDescription(_parser.parse("(only containedIn _Nothing_)"));

		Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testNominalMerge()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		/**
		 * 0: (a0) -r-> 2: (b) 1: (a1, (SOME r THING))
		 *
		 * (IMPLIES (A ONLY r {b}))
		 *
		 * Generates a successor for 1: a1 that is later merged with 2: {b} because of the TBox axiom.
		 *
		 */
		_abox.getTBox().add(_parser.parse("(IMPLIES A (ONLY r {b}))"));
		IABoxNode<String, String, String> a0 = _abox.getOrAddNamedNode("a0", false);
		a0.addUnfoldedDescription(_parser.parse("A"));
		IABoxNode<String, String, String> a1 = _abox.getOrAddNamedNode("a1", false);
		a1.addUnfoldedDescription(_parser.parse("(and A (some r _Thing_))"));
		IABoxNode<String, String, String> b0 = _abox.getOrAddNamedNode("b", false);
		a0.getRABox().getAssertedSuccessors().put("r", b0.getNodeID());

		Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		assertEquals(results.size(), 1);
		for (IReasonerResult<String, String, String> result : results) {
			final IABox<String, String, String> abox = result.getABox();
			assertEquals(3, abox.size());

			assertEquals(1, abox.getNode("a0").getRABox().getAssertedSuccessors().size("r"));
			assertEquals(1, abox.getNode("a0").getRABox().getAssertedSuccessors().get("r").size());
			assertEquals(abox.getNode("b").getNodeID(),
						 abox.getNode("a0").getRABox().getAssertedSuccessors().get("r").iterator().next());
			assertTrue(abox.getNode("a0").getRABox().getAssertedPredecessors().isEmpty());

			assertEquals(1, abox.getNode("a1").getRABox().getAssertedSuccessors().size("r"));
			assertEquals(1, abox.getNode("a1").getRABox().getAssertedSuccessors().get("r").size());
			assertEquals(abox.getNode("b").getNodeID(),
						 abox.getNode("a1").getRABox().getAssertedSuccessors().get("r").iterator().next());
			assertTrue(abox.getNode("a0").getRABox().getAssertedPredecessors().isEmpty());

			assertTrue(abox.getNode("b").getRABox().getAssertedSuccessors().isEmpty());
			assertEquals(2, abox.getNode("b").getRABox().getAssertedPredecessors().size("r"));
			assertTrue(abox.getNode("b").getRABox().getAssertedPredecessors().containsValue("r", abox.getNode("a0").
				getNodeID()));
			assertTrue(abox.getNode("b").getRABox().getAssertedPredecessors().containsValue("r", abox.getNode("a1").
				getNodeID()));
		}
	}

	@Test
	public void testNominalInconsistency()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().add(_parser.parse("(IMPLIES {a} (NOT {a}))"));
		_abox.getOrAddNamedNode("a", false);

		Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testNominalMultiMerge()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		/**
		 * 0: ({a}, A) -r-> 1: ({b}, A)
		 *
		 * (SOME r B), (IMPLIES B (AND {a} {b}))
		 *
		 * A new node 2: (B) will be generated. The unfolding of the second TBox term, will merge this new node either
		 * into 0 or 1, which in term will get merged into each other.
		 *
		 * The result is a single node that is its own r-successor.
		 *
		 * Tests dual merge and especially the append() functionality of merge infos.
		 *
		 */
		_abox.getTBox().add(_parser.parse("(SOME r B)"));
		_abox.getTBox().add(_parser.parse("(IMPLIES B (AND {a} {b}))"));
		IABoxNode<String, String, String> a = _abox.getOrAddNamedNode("a", false);
		a.addUnfoldedDescription(_parser.parse("A"));
		IABoxNode<String, String, String> b = _abox.getOrAddNamedNode("b", false);
		b.addUnfoldedDescription(_parser.parse("A"));

		a.getRABox().getAssertedSuccessors().put("r", b.getNodeID());

		Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		assertEquals(1, results.size());
		for (IReasonerResult<String, String, String> result : results) {
			final IABox<String, String, String> abox = result.getABox();
			assertEquals(1, abox.size());
			assertTrue(abox.getNode("a").getRABox().getAssertedSuccessors().containsValue("r", abox.getNode("a").
				getNodeID()));
		}
	}

	@Test
	public void testNominalUnblock()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getTBox().add(_parser.parse("(SOME r B)"));
		IABoxNode<String, String, String> a = _abox.getOrAddNamedNode("a", false);
		a.addUnfoldedDescription(_parser.parse("A"));
		a.addUnfoldedDescription(_parser.parse("SOME r {b}"));

	}

	@Test
	public void testUnfold()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		/**
		 * Create an subconcept chain and check, if it is properly unfolded
		 *
		 */
		_abox.getTBox().add(_parser.parse("(implies A B)"));
		_abox.getTBox().add(_parser.parse("(implies B C)"));
		/* intentional repetition */
		_abox.getTBox().add(_parser.parse("(implies B D)"));
		_abox.getTBox().add(_parser.parse("(implies C E)"));
		_abox.getTBox().add(_parser.parse("(implies F G)"));
		final IABoxNode<String, String, String> a = _abox.getOrAddNamedNode("a", false);
		a.addUnfoldedDescription(_parser.parse("A"));
		Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		assertEquals(1, results.size());
		for (IReasonerResult<String, String, String> result : results) {
			final IABox<String, String, String> abox = result.getABox();
			assertTrue(abox.getNode("a").getTerms().contains(_parser.parse("A")));
			assertTrue(abox.getNode("a").getTerms().contains(_parser.parse("B")));
			assertTrue(abox.getNode("a").getTerms().contains(_parser.parse("C")));
			assertTrue(abox.getNode("a").getTerms().contains(_parser.parse("D")));
			assertTrue(abox.getNode("a").getTerms().contains(_parser.parse("E")));
			assertFalse(abox.getNode("a").getTerms().contains(_parser.parse("F")));
			assertFalse(abox.getNode("a").getTerms().contains(_parser.parse("G")));
		}
	}

	@Test
	public void testNominalUnfold()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		/**
		 * Create an subconcept chain and check, if it is properly unfolded
		 *
		 */
		_abox.getTBox().add(_parser.parse("(implies {a} B)"));
		_abox.getTBox().add(_parser.parse("(implies B {c})"));
		/* intentional repetition */
		_abox.getTBox().add(_parser.parse("(implies B {d})"));
		_abox.getTBox().add(_parser.parse("(implies {c} E)"));
		_abox.getTBox().add(_parser.parse("(implies {f} G)"));
		final IABoxNode<String, String, String> a = _abox.getOrAddNamedNode("a", false);
		Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		assertEquals(1, results.size());
		for (IReasonerResult<String, String, String> result : results) {
			final IABox<String, String, String> abox = result.getABox();
			assertTrue(abox.getNode("a").getTerms().contains(_parser.parse("{a}")));
			assertTrue(abox.getNode("a").getTerms().contains(_parser.parse("B")));
			assertTrue(abox.getNode("a").getTerms().contains(_parser.parse("{c}")));
			assertTrue(abox.getNode("a").getTerms().contains(_parser.parse("{d}")));
			assertTrue(abox.getNode("a").getTerms().contains(_parser.parse("E")));
			assertFalse(abox.getNode("a").getTerms().contains(_parser.parse("{f}")));
			assertFalse(abox.getNode("a").getTerms().contains(_parser.parse("G")));
		}
	}

	@Test
	public void testTransitiveRole()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getTBox().getRBox().setRoleProperty("r", RoleProperty.TRANSITIVE);
		final IDLRestriction<String, String, String> A = _parser.parse("A");
		IABoxNode<String, String, String> a = _abox.getOrAddNamedNode("a", false);
		a.addUnfoldedDescription(_parser.parse("(only r A)"));
		IABoxNode<String, String, String> b = _abox.getOrAddNamedNode("b", false);
		IABoxNode<String, String, String> c = _abox.getOrAddNamedNode("c", false);
		a.getRABox().getAssertedSuccessors().put("r", b.getNodeID());
		b.getRABox().getAssertedSuccessors().put("r", c.getNodeID());

		Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		assertEquals(1, results.size());
		for (IReasonerResult<String, String, String> result : results) {
			final IABox<String, String, String> abox = result.getABox();
			a = abox.getNode("a");
			b = abox.getNode("b");
			c = abox.getNode("c");
			assertTrue(a.getRABox().getAssertedSuccessors().containsValue("r", c.getNodeID()));

			// assertFalse(a.getTerms().contains(A));
			assertTrue(b.getTerms().contains(A));
			assertTrue(c.getTerms().contains(A));
		}
	}

	@Test
	public void testSymmetricRole()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getTBox().getRBox().setRoleProperty("r", RoleProperty.SYMMETRIC);
		final IDLRestriction<String, String, String> A = _parser.parse("A");
		IABoxNode<String, String, String> a = _abox.getOrAddNamedNode("a", false);
		IABoxNode<String, String, String> b = _abox.getOrAddNamedNode("b", false);
		_abox.getTBox().add(_parser.parse("(only r B)"));
		a.getRABox().getAssertedSuccessors().put("r", b.getNodeID());

		Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		assertEquals(1, results.size());
		for (IReasonerResult<String, String, String> result : results) {
			final IABox<String, String, String> abox = result.getABox();
			a = abox.getNode("a");
			b = abox.getNode("b");
			assertTrue(a.getRABox().getAssertedSuccessors().containsValue("r", b.getNodeID()));
			assertTrue(b.getRABox().getAssertedSuccessors().containsValue("r", a.getNodeID()));
			assertTrue(a.getTerms().contains(_parser.parse("B")));
			assertTrue(a.getTerms().contains(_parser.parse("B")));
		}
	}

	@Test
	public void testUnionTermsAlreadyPresent()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		/* regression test */
		IDLTermFactory<String, String, String> termFactory = new SimpleStringDLTermFactory();
		SimpleKRSSParser parser = new SimpleKRSSParser(termFactory);

		final IABox<String, String, String> abox = _aboxFactory.createABox();

		final IABoxNode<String, String, String> aNode = abox.getOrAddNamedNode("a", false);
		aNode.addUnfoldedDescription(parser.parse("(or A B)"));
		aNode.addUnfoldedDescription(parser.parse("A"));
		aNode.addUnfoldedDescription(parser.parse("B"));

		Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(abox);
	}

	@Test
	public void testIsSubClassOfSimple()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		assertFalse(_reasoner.isSubClassOf(_abox, _parser.parse("A"), _parser.parse("B")));
		assertFalse(_reasoner.isSubClassOf(_abox, _parser.parse("A"), _parser.parse("(not B)")));
	}

	@Test
	public void testIsSubClassOfChainedModusPonens()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().add(_parser.parse("(implies A B)"));
		_abox.getTBox().add(_parser.parse("(implies B C)"));

		assertTrue(_reasoner.isSubClassOf(_abox, _parser.parse("A"), _parser.parse("A")));
		assertTrue(_reasoner.isSubClassOf(_abox, _parser.parse("B"), _parser.parse("B")));
		assertTrue(_reasoner.isSubClassOf(_abox, _parser.parse("C"), _parser.parse("C")));

		assertTrue(_reasoner.isSubClassOf(_abox, _parser.parse("A"), _parser.parse("B")));
		assertFalse(_reasoner.isSubClassOf(_abox, _parser.parse("B"), _parser.parse("A")));
		assertTrue(_reasoner.isSubClassOf(_abox, _parser.parse("B"), _parser.parse("C")));
		assertFalse(_reasoner.isSubClassOf(_abox, _parser.parse("C"), _parser.parse("B")));

		assertTrue(_reasoner.isSubClassOf(_abox, _parser.parse("A"), _parser.parse("C")));
		assertFalse(_reasoner.isSubClassOf(_abox, _parser.parse("C"), _parser.parse("A")));

		assertTrue(_reasoner.isSubClassOf(_abox, _parser.parse("B"), _termFactory.getDLThing()));
	}

	@Test
	public void testIsSubClassOfNominal()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		final IABoxNode<String, String, String> a = _abox.getOrAddNamedNode("a", false);
		final IABoxNode<String, String, String> other = _abox.createNode(false);
		a.getRABox().getAssertedSuccessors().put("r", other.getNodeID());

		assertTrue(_reasoner.isSubClassOf(_abox, _parser.parse("{a}"), _parser.parse("_Thing_")));
		assertFalse(_reasoner.isSubClassOf(_abox, _parser.parse("_Thing_"), _parser.parse("{a}")));
	}

	@Test
	public void testIsSubClassOfNominalExists()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		final IABoxNode<String, String, String> a = _abox.getOrAddNamedNode("a", false);
		final IABoxNode<String, String, String> other = _abox.createNode(false);
		a.getRABox().getAssertedSuccessors().put("r", other.getNodeID());

		assertTrue(_reasoner.isSubClassOf(_abox, _parser.parse("{a}"), _parser.parse("(some r _Thing_)")));
		assertFalse(_reasoner.isSubClassOf(_abox, _parser.parse("(some r _Thing_)"), _parser.parse("{a}")));
	}

	/*
	 @Test
	 public void testIsInRange()
	 throws ParseException, EReasonerException
	 {
	 _abox.getTBox().add(_parser.parse("(only r A)"));
	 _abox.getTBox().add(_parser.parse("(implies B A)"));
	 _abox.getTBox().add(_parser.parse("(implies A (not C))"));
	 assertTrue(_reasoner.isInRange(_abox, _parser.parse("B"), "r"));
	 assertFalse(_reasoner.isInRange(_abox, _parser.parse("C"), "r"));
	 }

	 @Test
	 public void testIsRange()
	 throws ParseException, EReasonerException
	 {
	 _abox.getTBox().add(_parser.parse("(only r A)"));
	 _abox.getTBox().add(_parser.parse("(implies B A)"));
	 _abox.getTBox().add(_parser.parse("(implies A (not C))"));
	 assertTrue(_reasoner.isRange(_abox, _parser.parse("A"), "r"));
	 assertFalse(_reasoner.isRange(_abox, _parser.parse("B"), "r"));
	 assertFalse(_reasoner.isRange(_abox, _parser.parse("C"), "r"));
	 }
	 */
	@Test
	public void testIsInDomain()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getTBox().add(_parser.parse("(implies (some r _Thing_) A)"));
		_abox.getTBox().add(_parser.parse("(implies C A)"));
		_abox.getTBox().add(_parser.parse("(implies A (not D))"));
		assertTrue(_reasoner.isInDomain(_abox, _parser.parse("A"), "r"));
		assertTrue(_reasoner.isInDomain(_abox, _parser.parse("C"), "r"));
		assertFalse(_reasoner.isInDomain(_abox, _parser.parse("D"), "r"));
	}

	@Test
	public void testIsInDomainExplicit()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getTBox().getRBox().getRoleDomains().put("r", _parser.parse("(not D)"));
		assertTrue(_reasoner.isInDomain(_abox, _parser.parse("A"), "r"));
		assertTrue(_reasoner.isInDomain(_abox, _parser.parse("C"), "r"));
		assertFalse(_reasoner.isInDomain(_abox, _parser.parse("D"), "r"));
	}


	/*
	 @Test
	 public void testIsDomain()
	 throws ParseException, EReasonerException
	 {
	 _abox.getTBox().add(_parser.parse("(implies (some r _Thing_) A)"));
	 _abox.getTBox().add(_parser.parse("(implies C A)"));
	 _abox.getTBox().add(_parser.parse("(implies A (not D))"));
	 assertTrue(_reasoner.isDomain(_abox, _parser.parse("A"), "r"));
	 assertFalse(_reasoner.isDomain(_abox, _parser.parse("C"), "r"));
	 assertFalse(_reasoner.isDomain(_abox, _parser.parse("D"), "r"));
	 }

	 @Test
	 public void testIsDomainExplicit()
	 throws ParseException, EReasonerException
	 {
	 _abox.getTBox().getRBox().getRoleDomains().put("r", _parser.parse("A"));
	 assertTrue(_reasoner.isDomain(_abox, _parser.parse("A"), "r"));
	 assertFalse(_reasoner.isDomain(_abox, _parser.parse("C"), "r"));
	 assertFalse(_reasoner.isDomain(_abox, _parser.parse("D"), "r"));
	 }
	 */
	@Test
	public void testFunctionalRole()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getTBox().getRBox().setRoleProperty("r", RoleProperty.FUNCTIONAL);
		final IABoxNode<String, String, String> node = _abox.createNode(false);
		node.addUnfoldedDescription(_parser.parse("(some r A)"));
		node.addUnfoldedDescription(_parser.parse("(some r B)"));
		final Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		for (IReasonerResult<String, String, String> result : results) {
			final IABox<String, String, String> abox = result.getABox();
			assertEquals(2, abox.size());
		}
	}

	@Test
	public void testFunctionalRoleDualLinks()
		throws ParseException, EReasonerException, ENodeMergeException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getTBox().getRBox().setRoleProperty("r", RoleProperty.FUNCTIONAL);
		final IABoxNode<String, String, String> a = _abox.createNode(false);
		final IABoxNode<String, String, String> b = _abox.createNode(false);
		final IABoxNode<String, String, String> c = _abox.createNode(false);
		a.getRABox().getAssertedSuccessors().put("r", b.getNodeID());
		a.getRABox().getAssertedSuccessors().put("r", c.getNodeID());
		assertTrue(_reasoner.isConsistent(_abox));
	}

	@Test
	public void testFunctionalRoleDualLinksInconsistent()
		throws ParseException, EReasonerException, ENodeMergeException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getTBox().getRBox().setRoleProperty("r", RoleProperty.FUNCTIONAL);
		final IABoxNode<String, String, String> a = _abox.createNode(false);
		final IABoxNode<String, String, String> b = _abox.createNode(false);
		b.addUnfoldedDescription(_parser.parse("C"));
		final IABoxNode<String, String, String> c = _abox.createNode(false);
		c.addUnfoldedDescription(_parser.parse("(not C)"));
		a.getRABox().getAssertedSuccessors().put("r", b.getNodeID());
		a.getRABox().getAssertedSuccessors().put("r", c.getNodeID());
		assertFalse(_reasoner.isConsistent(_abox));
	}

	@Test
	public void testFunctionalRolePropagation()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getTBox().getRBox().setRoleProperty("r", RoleProperty.FUNCTIONAL);
		final IABoxNode<String, String, String> a = _abox.createNode(false);
		final IABoxNode<String, String, String> b = _abox.createNode(false);
		a.getRABox().getAssertedSuccessors().put("r", b.getNodeID());
		a.addUnfoldedDescription(_parser.parse("(some r A)"));
		final Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		for (IReasonerResult<String, String, String> result : results) {
			final IABox<String, String, String> abox = result.getABox();
			assertEquals(2, abox.size());
			assertTrue(abox.getNode(b.getNodeID()).getTerms().contains(_termFactory.getDLClassReference("A")));
		}
	}

	@Test
	public void testInverseFunctionalRoleDualLinks()
		throws ParseException, EReasonerException, ENodeMergeException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getTBox().getRBox().setRoleProperty("r", RoleProperty.INVERSE_FUNCTIONAL);
		final IABoxNode<String, String, String> a = _abox.createNode(false);
		final IABoxNode<String, String, String> b = _abox.createNode(false);
		final IABoxNode<String, String, String> c = _abox.createNode(false);
		a.getRABox().getAssertedSuccessors().put("r", c.getNodeID());
		b.getRABox().getAssertedSuccessors().put("r", c.getNodeID());
		assertTrue(_reasoner.isConsistent(_abox));
	}

	@Test
	public void testInverseFunctionalInconsistentRoleDualLinks()
		throws ParseException, EReasonerException, ENodeMergeException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getTBox().getRBox().setRoleProperty("r", RoleProperty.INVERSE_FUNCTIONAL);
		final IABoxNode<String, String, String> a = _abox.createNode(false);
		a.addUnfoldedDescription(_parser.parse("A"));
		final IABoxNode<String, String, String> b = _abox.createNode(false);
		b.addUnfoldedDescription(_parser.parse("(not A)"));
		final IABoxNode<String, String, String> c = _abox.createNode(false);
		a.getRABox().getAssertedSuccessors().put("r", c.getNodeID());
		b.getRABox().getAssertedSuccessors().put("r", c.getNodeID());
		assertFalse(_reasoner.isConsistent(_abox));
	}

	@Test
	public void testDatatypeNode()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		final IABoxNode<String, String, String> a = _abox.createNode(true);
		a.addUnfoldedDescription(_parser.parse("(some r A)"));
		Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		assertTrue(results.isEmpty());
	}

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
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getTBox().getRBox().setRoleProperty("r", RoleProperty.FUNCTIONAL);
		final IABoxNode<String, String, String> a = _abox.getOrAddNamedNode("a", false);
		final IABoxNode<String, String, String> b = _abox.getOrAddNamedNode("b", false);
		final IABoxNode<String, String, String> c = _abox.createNode(false);

		c.getRABox().getAssertedSuccessors().put("r", a.getNodeID());
		c.addUnfoldedDescription(_parser.parse("(some r {b})"));

		final Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		for (IReasonerResult<String, String, String> result : results) {
			assertEquals(2, result.getABox().size());
		}
	}

	@Test
	public void testFunctionalRoleMerge2()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getTBox().getRBox().setRoleProperty("r", RoleProperty.FUNCTIONAL);
		final IABoxNode<String, String, String> a = _abox.getOrAddNamedNode("a", false);
		final IABoxNode<String, String, String> b = _abox.createNode(false);
		final IABoxNode<String, String, String> c = _abox.createNode(false);

		a.getRABox().getAssertedSuccessors().put("r", b.getNodeID());
		a.getRABox().getAssertedSuccessors().put("r", c.getNodeID());
		a.addUnfoldedDescription(_parser.parse("(only r {b})"));
		final Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		for (IReasonerResult<String, String, String> result : results) {
			assertEquals(2, result.getABox().size());
		}
	}

	@Test
	public void testMergeTracking()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		/**
		 * 0: (a0) -r-> 2: (b) 1: (a1, (SOME r THING))
		 *
		 * (IMPLIES (A ONLY r {b}))
		 *
		 * Generates a successor for 1: a1 that is later merged with 2: {b} because of the TBox axiom.
		 *
		 */
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getTBox().add(_parser.parse("(ONLY r {b})"));
		IABoxNode<String, String, String> a0 = _abox.getOrAddNamedNode("a0", false);
		a0.addUnfoldedDescription(_parser.parse("A"));
		IABoxNode<String, String, String> a1 = _abox.getOrAddNamedNode("a1", false);
		a1.addUnfoldedDescription(_parser.parse("(and A (some r _Thing_))"));
		IABoxNode<String, String, String> b0 = _abox.getOrAddNamedNode("b", false);
		a0.getRABox().getAssertedSuccessors().put("r", b0.getNodeID());

		final Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		assertEquals(1, results.size());
		final IReasonerResult<String, String, String> result = results.iterator().next();
		assertEquals(1, result.getMergeMap().size());
		assertTrue(result.getMergeMap().containsValue(b0.getNodeID()));
	}

	@Test
	public void testSubRoleLink() throws EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getTBox().getRBox().addRole("sub", RoleType.OBJECT_PROPERTY);
		IABoxNode<String, String, String> a0 = _abox.getOrAddNamedNode("a0", false);
		IABoxNode<String, String, String> a1 = _abox.getOrAddNamedNode("a1", false);
		_abox.getTBox().getRBox().addSubRole("r", "sub");
		a0.getRABox().getAssertedSuccessors().put("sub", a1.getNodeID());
		assertTrue(a0.getRABox().hasSuccessor("r", a1));
	}

	@Test
	public void testSubRoleExistsBlock()
		throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		IABoxNode<String, String, String> a0 = _abox.getOrAddNamedNode("a0", false);
		a0.addUnfoldedDescription(_parser.parse("(only r A)"));
		IABoxNode<String, String, String> a1 = _abox.getOrAddNamedNode("a1", false);
		a1.addUnfoldedDescription(_parser.parse("A"));

		_abox.getTBox().getRBox().addRole("sub", RoleType.OBJECT_PROPERTY);
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getTBox().getRBox().addSubRole("r", "sub");
		a0.getRABox().getAssertedSuccessors().put("sub", a1.getNodeID());

		final Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		assertEquals(1, results.size());
		final IReasonerResult<String, String, String> result = results.iterator().next();
		for (NodeID succID : result.getABox().getNode(a0.getNodeID()).getRABox().getSuccessors("sub"))
			assertEquals(a1.getNodeID(), succID);
	}

	@Test
	public void testSomeNominalMerge()
		throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		IABoxNode<String, String, String> a0 = _abox.getOrAddNamedNode("a", false);
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		a0.addUnfoldedDescription(_parser.parse("(some r {a})"));
		final Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		assertEquals(1, results.size());
		final IReasonerResult<String, String, String> result = results.iterator().next();
		assertEquals(1, result.getABox().size());
		assertNotNull(result.getABox().getNode("a"));
	}

	@Test
	public void testSomeNominalMergeExists()
		throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		IABoxNode<String, String, String> a0 = _abox.getOrAddNamedNode("a", false);
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		a0.addUnfoldedDescription(_parser.parse("(some r (and {a} {b}))"));
		final Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		assertEquals(1, results.size());
		final IReasonerResult<String, String, String> result = results.iterator().next();
		assertEquals(1, result.getABox().size());
		assertNotNull(result.getABox().getNode("a"));
		assertNotNull(result.getABox().getNode("b"));
	}

	@Test
	public void testNominalUnfoldMerge()
		throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		IABoxNode<String, String, String> a = _abox.getOrAddNamedNode("a", false);
		IABoxNode<String, String, String> b = _abox.createNode(false);
		IABoxNode<String, String, String> c = _abox.createNode(false);

		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		a.getRABox().getAssertedSuccessors().put("r", b.getNodeID());
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		a.getRABox().getAssertedSuccessors().put("r", c.getNodeID());
		NodeMergeInfo<String, String, String> mergeInfo = b.addUnfoldedDescription(_parser.parse("{a}"));
		assertEquals(2, _abox.size());
		mergeInfo = c.addUnfoldedDescription(_parser.parse("{a}"));
		assertEquals(1, _abox.size());

		final Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		assertEquals(1, results.size());
		final IReasonerResult<String, String, String> result = results.iterator().next();
		assertEquals(1, result.getABox().size());
		assertNotNull(result.getABox().getNode("a"));
	}

	@Test
	public void testNominalToNominalMerge()
		throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		IABoxNode<String, String, String> a = _abox.getOrAddNamedNode("a", false);
		IABoxNode<String, String, String> b = _abox.getOrAddNamedNode("b", false);

		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		a.getRABox().getAssertedSuccessors().put("r", b.getNodeID());
		NodeMergeInfo<String, String, String> mergeInfo = b.addUnfoldedDescription(_parser.parse("{a}"));
		assertEquals(1, _abox.size());

		final Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		assertEquals(1, results.size());
		final IReasonerResult<String, String, String> result = results.iterator().next();
		assertEquals(1, result.getABox().size());
		assertNotNull(result.getABox().getNode("a"));
		assertNotNull(result.getABox().getNode("b"));
	}

	@Test
	public void testSomeNominalMergeForAllImplication()
		throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getTBox().add(_parser.parse("(implies A (or {a} {b}))"));
		_abox.getTBox().add(_parser.parse("(only r A)"));

		IABoxNode<String, String, String> a = _abox.createNode(false);
		IABoxNode<String, String, String> b = _abox.createNode(true);

		a.getRABox().getAssertedSuccessors().put("r", b);

		final Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		assertEquals(2, results.size());
		for (IReasonerResult<String, String, String> result : results) {
			final IABox<String, String, String> abox = result.getABox();
			assertEquals(2, abox.size());
			assertTrue(abox.getNode(a.getNodeID()).getRABox().hasSuccessor("r", b.getNodeID()));
		}
	}

	@Test
	public void testDoubleUnion() throws EReasonerException, ENodeMergeException, ParseException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);

		IABoxNode<String, String, String> a = _abox.createNode(false);
		a.addUnfoldedDescription(_parser.parse("(or C0 D0)"));
		a.addUnfoldedDescription(_parser.parse("(or C1 D1)"));

		final Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		assertEquals(4, results.size());
	}

	@Test
	public void testDependencyDirectedBacktracking() throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);

		IABoxNode<String, String, String> a = _abox.createNode(false);
		/* create 750 unions. Without DDB, consistency takes forever */
		for (int i = 0; i < 750; ++i)
			a.addUnfoldedDescription(_parser.parse(String.format("(or C%04d D%04d)", i, i)));
		a.addUnfoldedDescription(_parser.parse("(some r (and C D))"));
		a.addUnfoldedDescription(_parser.parse("(only r (not C))"));

		final Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testInverseRolePropagation()
		throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().getRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getTBox().getRBox().addRole("rinv", RoleType.OBJECT_PROPERTY);
		_abox.getTBox().getRBox().addInverseRole("r", "rinv");

		final IABoxNode<String, String, String> a = _abox.createNode(false);
		a.addUnfoldedDescription(_parser.parse("(some r (only rinv A))"));
		final Collection<? extends IReasonerResult<String, String, String>> results = _reasoner.checkConsistency(_abox);
		assertEquals(1, results.size());
		final IABox<String, String, String> abox = results.iterator().next().getABox();
		abox.getNode(a.getNodeID()).getTerms().contains(_parser.parse("A"));
	}
}
