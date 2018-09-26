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
import de.uniba.wiai.kinf.pw.projects.lillytab.IReasonerResult;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentRBoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IDatatypeABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IIndividualABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.EIllegalTermTypeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleType;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleKRSSParser;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleStringDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.TermUtil;
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
 * @author Peter Wullinger <wullinger@rz.uni-kiel.de>
 */
public class ReasonerTest
{
	private final IDLTermFactory<String, String, String, String> _termFactory = new SimpleStringDLTermFactory();
	private final IABoxFactory<String, String, String, String> _aboxFactory = new ABoxFactory<>(
		_termFactory);
	private IABox<String, String, String, String> _abox;
	private Reasoner<String, String, String, String> _reasoner;
	private SimpleKRSSParser _parser;

	public ReasonerTest()
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
	public void testIntersection() throws ParseException, EReasonerException, EInconsistencyException
	{
		IABoxNode<String, String, String, String> aNode = _abox.getOrAddIndividualNode("a");
		aNode.addTerm(_parser.parse("(and A B)"));

		Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(_abox);
		IDLClassReference<String, String, String, String> A = _termFactory.getDLClassReference("B");
		IDLClassReference<String, String, String, String> B = _termFactory.getDLClassReference("B");
		for (IReasonerResult<String, String, String, String> result : results) {
			final IABox<String, String, String, String> returnedABox = result.getABox();
			aNode = returnedABox.getOrAddIndividualNode("a");
			assertTrue(aNode.getTerms().contains(A));
			assertTrue(aNode.getTerms().contains(B));
		}
	}

	@Test
	public void testUnion() throws ParseException, EReasonerException, EInconsistencyException
	{
		IDLTermFactory<String, String, String, String> termFactory = new SimpleStringDLTermFactory();
		SimpleKRSSParser parser = new SimpleKRSSParser(termFactory);

		final IABox<String, String, String, String> abox = _aboxFactory.createABox();

		IABoxNode<String, String, String, String> aNode = abox.getOrAddIndividualNode("a");
		aNode.addTerm(parser.parse("(or A B)"));

		Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.checkConsistency(abox);
		IDLClassReference<String, String, String, String> A = termFactory.getDLClassReference("A");
		IDLClassReference<String, String, String, String> B = termFactory.getDLClassReference("B");
		// assertEquals(2, aboxes.size());
		for (IReasonerResult<String, String, String, String> result : results) {
			final IABox<String, String, String, String> returnedABox = result.getABox();
			aNode = returnedABox.getOrAddIndividualNode("a");
			assertTrue(aNode.getTerms().contains(A) || aNode.getTerms().contains(B));
		}
	}

	@Test(expected = EInconsistencyException.class)
	public void testUnionInconsistency()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		IDLTermFactory<String, String, String, String> termFactory = new SimpleStringDLTermFactory();
		SimpleKRSSParser parser = new SimpleKRSSParser(termFactory);

		final IABox<String, String, String, String> abox = _aboxFactory.createABox();
		final IABoxNode<String, String, String, String> aNode = abox.getOrAddIndividualNode("a");
		aNode.addTerm(parser.parse("(or A B)"));
		aNode.addTerm(parser.parse("(not A)"));
		aNode.addTerm(parser.parse("(not B)"));

		Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.checkConsistency(abox);
		assertTrue(results.isEmpty());
	}

	@Test(expected = EInconsistencyException.class)
	public void testOnly()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);

		final IABoxNode<String, String, String, String> aNode = _abox.getOrAddIndividualNode("a");
		final IABoxNode<String, String, String, String> bNode = _abox.getOrAddIndividualNode("b");
		aNode.getRABox().getAssertedSuccessors().put("r", bNode.getNodeID());

		bNode.addTerm(_parser.parse("(not B)"));
		aNode.addTerm(TermUtil.toNNF(_parser.parse("(only r B)"), _termFactory));

		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(
			_abox);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testExistsNewNode()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		IABoxNode<String, String, String, String> aNode = _abox.getOrAddIndividualNode("a");
		IABoxNode<String, String, String, String> bNode = _abox.getOrAddIndividualNode("b");
		aNode.getRABox().getAssertedSuccessors().put("r", bNode.getNodeID());

		aNode.addTerm(TermUtil.toNNF(_parser.parse("(some r (not B))"), _termFactory));
		bNode.addTerm(_parser.parse("B"));

		Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(_abox);
		/* all ABoxes must have exactly one generated node */
		for (IReasonerResult<String, String, String, String> result : results) {
			int anonCount = 0;
			for (IABoxNode<String, String, String, String> node : result.getABox()) {
				if (node.isAnonymous()) {
					++anonCount;
				}
			}
			assertEquals(1, anonCount);
		}
	}

	@Test(expected = EInconsistencyException.class)
	public void testUnionSubClass()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		final IABoxNode<String, String, String, String> node = _abox.createNode(false);
		node.addTerm(TermUtil.toNNF(_parser.parse("(not (implies A (or A B)))"), _termFactory));

		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(
			_abox);
		assertTrue(results.isEmpty());
	}

	@Test(expected = EInconsistencyException.class)
	public void testGeneratedExistsOnly()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		final IABoxNode<String, String, String, String> aNode = _abox.getOrAddIndividualNode("a");

		aNode.addTerm(TermUtil.toNNF(_parser.parse("(only r B)"), _termFactory));
		aNode.addTerm(TermUtil.toNNF(_parser.parse("(some r (not B))"), _termFactory));

		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(
			_abox);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testExistsNoGenerate()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		final IABoxNode<String, String, String, String> aNode = _abox.getOrAddIndividualNode("a");
		final IABoxNode<String, String, String, String> bNode = _abox.getOrAddIndividualNode("b");

		aNode.addTerm(TermUtil.toNNF(_parser.parse("(some r B)"), _termFactory));
		bNode.addTerm(_termFactory.getDLClassReference("B"));
		_reasoner.checkConsistency(_abox);

	}

	@Test(expected = EInconsistencyException.class)
	public void testCyclicOnly()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		IABoxNode<String, String, String, String> aNode = _abox.getOrAddIndividualNode("a");
		IABoxNode<String, String, String, String> bNode = _abox.getOrAddIndividualNode("b");
		aNode.getRABox().getAssertedSuccessors().put("r", bNode.getNodeID());
		bNode.getRABox().getAssertedSuccessors().put("r", aNode.getNodeID());

		aNode.addTerm(TermUtil.toNNF(_parser.parse("(only r (and (not B) (only r A)))"), _termFactory));
		_abox.getTBox().add(_parser.parse("(implies A (only r B))"));

		Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(_abox);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testForAllPropagate()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		/**
		 * Check, if forall rules properly propagate to all successors.
		 */
		IABoxNode<String, String, String, String> aNode = _abox.getOrAddIndividualNode("a");
		IABoxNode<String, String, String, String> bNode = _abox.getOrAddIndividualNode("b");
		IABoxNode<String, String, String, String> cNode = _abox.getOrAddIndividualNode("c");

		aNode.getRABox().getAssertedSuccessors().put("r", bNode.getNodeID());
		aNode.getRABox().getAssertedSuccessors().put("r", cNode.getNodeID());

		aNode.addTerm(_parser.parse("(only r B)"));

		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(
			_abox);
		assertEquals(1, results.size());
		final IDLClassReference<String, String, String, String> B = _termFactory.getDLClassReference("B");
		for (IReasonerResult<String, String, String, String> result : results) {
			assertEquals(3, result.getABox().size());
			assertTrue(result.getABox().getIndividualNode("b").getTerms().contains(B));
			assertTrue(result.getABox().getIndividualNode("c").getTerms().contains(B));
		}
	}

	@Test(expected = EInconsistencyException.class)
	public void testRangeClash()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		/**
		 * Given a role with explicit range assertion "NOT B", and an ABox
		 *
		 * {a}, -> {b}, B
		 *
		 * check if reasoning detects a clash.
		 *
		 */
		final IABoxNode<String, String, String, String> aNode = _abox.getOrAddIndividualNode("a");
		final IABoxNode<String, String, String, String> bNode = _abox.getOrAddIndividualNode("b");
		_abox.getAssertedRBox().getRoleRanges().put("r", _termFactory.getDLClassReference("B"));

		aNode.getRABox().getAssertedSuccessors().put("r", bNode.getNodeID());

		_abox.getAssertedRBox().getRoleRanges().put("r", _parser.parse(("(NOT B)")));
		Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(_abox);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testRangePropagation()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		/**
		 * Given a role with explicit range assertion "B", and an ABox
		 *
		 * {a}, -> {b}
		 *
		 * check if {b} is a an instance of B after reasoning.
		 *
		 */
		final IABoxNode<String, String, String, String> aNode = _abox.getOrAddIndividualNode("a");
		final IABoxNode<String, String, String, String> bNode = _abox.getOrAddIndividualNode("b");

		aNode.getRABox().getAssertedSuccessors().put("r", bNode.getNodeID());

		_abox.getAssertedRBox().getRoleRanges().put("r", _termFactory.getDLClassReference("B"));
		assertFalse(_abox.getIndividualNode("b").getTerms().contains(_termFactory.getDLClassReference("B")));
		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(
			_abox);
		for (IReasonerResult<String, String, String, String> result : results) {
			assertEquals(2, result.getABox().size());
			assertTrue(
				result.getABox().getIndividualNode("b").getTerms().contains(_termFactory.getDLClassReference("B")));
		}
	}

	@Test(expected = EInconsistencyException.class)
	public void testDomainClash()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		/**
		 * Given a role with explicit domain assertion "A", and an ABox
		 *
		 * {a}, (NOT A) -> {b},
		 *
		 * check if reasoning detects a clash.
		 *
		 */
		final IABoxNode<String, String, String, String> aNode = _abox.getOrAddIndividualNode("a");
		aNode.addTerm(_termFactory.getDLClassReference("A"));
		final IABoxNode<String, String, String, String> bNode = _abox.getOrAddIndividualNode("b");
		bNode.addTerm(_termFactory.getDLClassReference("B"));

		aNode.getRABox().getAssertedSuccessors().put("r", bNode.getNodeID());

		_abox.getAssertedRBox().getRoleDomains().put("r", _parser.parse(("(NOT A)")));
		_reasoner.checkConsistency(_abox);
	}

	@Test
	public void testDomainPropagation()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		/**
		 * Given a role with explicit domain assertion "A", and an Abox
		 *
		 * {a} -> {b},
		 *
		 * check if {a} is an instanceof of A after reasoning.
		 *
		 */
		final IABoxNode<String, String, String, String> aNode = _abox.getOrAddIndividualNode("a");
		final IABoxNode<String, String, String, String> bNode = _abox.getOrAddIndividualNode("b");

		aNode.getRABox().getAssertedSuccessors().put("r", bNode.getNodeID());

		_abox.getAssertedRBox().getRoleDomains().put("r", _termFactory.getDLClassReference("A"));
		assertFalse(_abox.getIndividualNode("a").getTerms().contains(_termFactory.getDLClassReference("A")));
		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(
			_abox);
		for (IReasonerResult<String, String, String, String> result : results) {
			assertEquals(2, result.getABox().size());
			assertTrue(
				result.getABox().getIndividualNode("a").getTerms().contains(_termFactory.getDLClassReference("A")));
		}
	}

	//	}
	@Test(expected = EInconsistencyException.class)
	public void testSimpleInconsistentDescendency()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("containedIn", RoleType.OBJECT_PROPERTY);
		/**
		 * Simple inconsistency. TBox demands a successor, local axiom set forbids it.
		 */
		_abox.getTBox().add(_parser.parse("(implies SpatialObject (some containedIn SpatialObject))"));
		IABoxNode<String, String, String, String> a = _abox.getOrAddIndividualNode("a");
		IABoxNode<String, String, String, String> b = _abox.getOrAddIndividualNode("b");

		a.addTerm(_parser.parse("SpatialObject"));
		b.addTerm(_parser.parse("SpatialObject"));
		b.getRABox().getAssertedSuccessors().put("containedIn", a.getNodeID());
		b.addTerm(_parser.parse("(only containedIn _Nothing_)"));

		Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(_abox);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testNominalMerge()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		/**
		 * 0: (a0) -r-> 2: (b) 1: (a1, (SOME r THING))
		 *
		 * (IMPLIES (A ONLY r {b}))
		 *
		 * Generates a successor for 1: a1 that is later merged with 2: {b} because of the TBox axiom.
		 *
		 */
		_abox.getTBox().add(_parser.parse("(IMPLIES A (ONLY r {b}))"));
		IABoxNode<String, String, String, String> a0 = _abox.getOrAddIndividualNode("a0");
		a0.addTerm(_parser.parse("A"));
		IABoxNode<String, String, String, String> a1 = _abox.getOrAddIndividualNode("a1");
		a1.addTerm(_parser.parse("(and A (some r _Thing_))"));
		IABoxNode<String, String, String, String> b0 = _abox.getOrAddIndividualNode("b");
		a0.getRABox().getAssertedSuccessors().put("r", b0.getNodeID());

		Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(_abox);
		assertEquals(results.size(), 1);
		for (IReasonerResult<String, String, String, String> result : results) {
			final IABox<String, String, String, String> abox = result.getABox();
			assertEquals(3, abox.size());

			assertEquals(1, abox.getIndividualNode("a0").getRABox().getAssertedSuccessors().size("r"));
			assertEquals(1, abox.getIndividualNode("a0").getRABox().getAssertedSuccessors().get("r").size());
			assertEquals(abox.getIndividualNode("b").getNodeID(),
						 abox.getIndividualNode("a0").getRABox().getAssertedSuccessors().get("r").iterator().next());
			assertTrue(abox.getIndividualNode("a0").getRABox().getAssertedPredecessors().isEmpty());

			assertEquals(1, abox.getIndividualNode("a1").getRABox().getAssertedSuccessors().size("r"));
			assertEquals(1, abox.getIndividualNode("a1").getRABox().getAssertedSuccessors().get("r").size());
			assertEquals(abox.getIndividualNode("b").getNodeID(),
						 abox.getIndividualNode("a1").getRABox().getAssertedSuccessors().get("r").iterator().next());
			assertTrue(abox.getIndividualNode("a0").getRABox().getAssertedPredecessors().isEmpty());

			assertTrue(abox.getIndividualNode("b").getRABox().getAssertedSuccessors().isEmpty());
			assertEquals(2, abox.getIndividualNode("b").getRABox().getAssertedPredecessors().size("r"));
			assertTrue(abox.getIndividualNode("b").getRABox().getAssertedPredecessors().containsValue("r",
																									  abox.
				getIndividualNode(
				"a0").
				getNodeID()));
			assertTrue(abox.getIndividualNode("b").getRABox().getAssertedPredecessors().containsValue("r",
																									  abox.
				getIndividualNode(
				"a1").
				getNodeID()));
		}
	}

	@Test(expected = EInconsistencyException.class)
	public void testNominalInconsistency()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().add(_parser.parse("(IMPLIES {a} (NOT {a}))"));
		_abox.getOrAddIndividualNode("a");

		Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(_abox);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testNominalMultiMerge()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
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
		IABoxNode<String, String, String, String> a = _abox.getOrAddIndividualNode("a");
		a.addTerm(_parser.parse("A"));
		IABoxNode<String, String, String, String> b = _abox.getOrAddIndividualNode("b");
		b.addTerm(_parser.parse("A"));

		a.getRABox().getAssertedSuccessors().put("r", b.getNodeID());

		Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(_abox);
		assertEquals(1, results.size());
		for (IReasonerResult<String, String, String, String> result : results) {
			final IABox<String, String, String, String> abox = result.getABox();
			assertEquals(1, abox.size());
			assertTrue(abox.getIndividualNode("a").getRABox().getAssertedSuccessors().containsValue("r",
																									abox.
				getIndividualNode(
				"a").
				getNodeID()));
		}
	}

	@Test
	public void testNominalUnblock()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getTBox().add(_parser.parse("(SOME r B)"));
		IABoxNode<String, String, String, String> a = _abox.getOrAddIndividualNode("a");
		a.addTerm(_parser.parse("A"));
		a.addTerm(_parser.parse("SOME r {b}"));

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
		final IABoxNode<String, String, String, String> a = _abox.getOrAddIndividualNode("a");
		a.addTerm(_parser.parse("A"));
		Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(_abox);
		assertEquals(1, results.size());
		for (IReasonerResult<String, String, String, String> result : results) {
			final IABox<String, String, String, String> abox = result.getABox();
			assertTrue(abox.getIndividualNode("a").getTerms().contains(_parser.parse("A")));
			assertTrue(abox.getIndividualNode("a").getTerms().contains(_parser.parse("B")));
			assertTrue(abox.getIndividualNode("a").getTerms().contains(_parser.parse("C")));
			assertTrue(abox.getIndividualNode("a").getTerms().contains(_parser.parse("D")));
			assertTrue(abox.getIndividualNode("a").getTerms().contains(_parser.parse("E")));
			assertFalse(abox.getIndividualNode("a").getTerms().contains(_parser.parse("F")));
			assertFalse(abox.getIndividualNode("a").getTerms().contains(_parser.parse("G")));
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
		final IABoxNode<String, String, String, String> a = _abox.getOrAddIndividualNode("a");
		Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(_abox);
		assertEquals(1, results.size());
		for (IReasonerResult<String, String, String, String> result : results) {
			final IABox<String, String, String, String> abox = result.getABox();
			assertTrue(abox.getIndividualNode("a").getTerms().contains(_parser.parse("{a}")));
			assertTrue(abox.getIndividualNode("a").getTerms().contains(_parser.parse("B")));
			assertTrue(abox.getIndividualNode("a").getTerms().contains(_parser.parse("{c}")));
			assertTrue(abox.getIndividualNode("a").getTerms().contains(_parser.parse("{d}")));
			assertTrue(abox.getIndividualNode("a").getTerms().contains(_parser.parse("E")));
			assertFalse(abox.getIndividualNode("a").getTerms().contains(_parser.parse("{f}")));
			assertFalse(abox.getIndividualNode("a").getTerms().contains(_parser.parse("G")));
		}
	}



	@Test
	public void testUnionTermsAlreadyPresent()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		/* regression test */
		IDLTermFactory<String, String, String, String> termFactory = new SimpleStringDLTermFactory();
		SimpleKRSSParser parser = new SimpleKRSSParser(termFactory);

		final IABox<String, String, String, String> abox = _aboxFactory.createABox();

		final IABoxNode<String, String, String, String> aNode = abox.getOrAddIndividualNode("a");
		aNode.addTerm(parser.parse("(or A B)"));
		aNode.addTerm(parser.parse("A"));
		aNode.addTerm(parser.parse("B"));

		Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.checkConsistency(abox);
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
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		final IABoxNode<String, String, String, String> a = _abox.getOrAddIndividualNode("a");
		final IABoxNode<String, String, String, String> other = _abox.createNode(false);
		a.getRABox().getAssertedSuccessors().put("r", other.getNodeID());

		assertTrue(_reasoner.isSubClassOf(_abox, _parser.parse("{a}"), _parser.parse("_Thing_")));
		assertFalse(_reasoner.isSubClassOf(_abox, _parser.parse("_Thing_"), _parser.parse("{a}")));
	}

	@Test
	public void testIsSubClassOfNominalExists()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		final IABoxNode<String, String, String, String> a = _abox.getOrAddIndividualNode("a");
		final IABoxNode<String, String, String, String> other = _abox.createNode(false);
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
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
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
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getAssertedRBox().getRoleDomains().put("r", _parser.parse("(not D)"));
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
	 _abox.getAssertedRBox().getRoleDomains().put("r", _parser.parse("A"));
	 assertTrue(_reasoner.isDomain(_abox, _parser.parse("A"), "r"));
	 assertFalse(_reasoner.isDomain(_abox, _parser.parse("C"), "r"));
	 assertFalse(_reasoner.isDomain(_abox, _parser.parse("D"), "r"));
	 }
	 */

	@Test(expected = EIllegalTermTypeException.class)
	public void testDatatypeNode()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		final IABoxNode<String, String, String, String> a = _abox.createNode(true);
		a.addTerm(_parser.parse("(some r A)"));
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
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getTBox().add(_parser.parse("(ONLY r {b})"));
		IABoxNode<String, String, String, String> a0 = _abox.getOrAddIndividualNode("a0");
		a0.addTerm(_parser.parse("A"));
		IABoxNode<String, String, String, String> a1 = _abox.getOrAddIndividualNode("a1");
		a1.addTerm(_parser.parse("(and A (some r _Thing_))"));
		IABoxNode<String, String, String, String> b0 = _abox.getOrAddIndividualNode("b");
		a0.getRABox().getAssertedSuccessors().put("r", b0.getNodeID());

		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(
			_abox);
		assertEquals(1, results.size());
		final IReasonerResult<String, String, String, String> result = results.iterator().next();
		assertEquals(1, result.getMergeMap().size());
		assertTrue(result.getMergeMap().containsValue(b0.getNodeID()));
	}


	@Test
	public void testSomeNominalMerge()
		throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		IABoxNode<String, String, String, String> a0 = _abox.getOrAddIndividualNode("a");
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		a0.addTerm(_parser.parse("(some r {a})"));
		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(
			_abox);
		assertEquals(1, results.size());
		final IReasonerResult<String, String, String, String> result = results.iterator().next();
		assertEquals(1, result.getABox().size());
		assertNotNull(result.getABox().getIndividualNode("a"));
	}

	@Test
	public void testSomeNominalMergeExists()
		throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		IABoxNode<String, String, String, String> a0 = _abox.getOrAddIndividualNode("a");
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		a0.addTerm(_parser.parse("(some r (and {a} {b}))"));
		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(
			_abox);
		assertEquals(1, results.size());
		final IReasonerResult<String, String, String, String> result = results.iterator().next();
		assertEquals(1, result.getABox().size());
		assertNotNull(result.getABox().getIndividualNode("a"));
		assertNotNull(result.getABox().getIndividualNode("b"));
	}

	@Test
	public void testNominalUnfoldMerge()
		throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		IABoxNode<String, String, String, String> a = _abox.getOrAddIndividualNode("a");
		IABoxNode<String, String, String, String> b = _abox.createNode(false);
		IABoxNode<String, String, String, String> c = _abox.createNode(false);

		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		a.getRABox().getAssertedSuccessors().put("r", b.getNodeID());
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		a.getRABox().getAssertedSuccessors().put("r", c.getNodeID());
		NodeMergeInfo<String, String, String, String> mergeInfo = b.addTerm(_parser.parse("{a}"));
		assertEquals(2, _abox.size());
		mergeInfo = c.addTerm(_parser.parse("{a}"));
		assertEquals(1, _abox.size());

		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(
			_abox);
		assertEquals(1, results.size());
		final IReasonerResult<String, String, String, String> result = results.iterator().next();
		assertEquals(1, result.getABox().size());
		assertNotNull(result.getABox().getIndividualNode("a"));
	}

	@Test
	public void testNominalToNominalMerge()
		throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		IABoxNode<String, String, String, String> a = _abox.getOrAddIndividualNode("a");
		IABoxNode<String, String, String, String> b = _abox.getOrAddIndividualNode("b");

		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		a.getRABox().getAssertedSuccessors().put("r", b.getNodeID());
		NodeMergeInfo<String, String, String, String> mergeInfo = b.addTerm(_parser.parse("{a}"));
		assertEquals(1, _abox.size());

		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(
			_abox);
		assertEquals(1, results.size());
		final IReasonerResult<String, String, String, String> result = results.iterator().next();
		assertEquals(1, result.getABox().size());
		assertNotNull(result.getABox().getIndividualNode("a"));
		assertNotNull(result.getABox().getIndividualNode("b"));
	}

	@Test
	public void testSomeNominalMergeForAllImplication()
		throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getTBox().add(_parser.parse("(implies A (or {a} {b}))"));
		_abox.getTBox().add(_parser.parse("(only r A)"));

		IABoxNode<String, String, String, String> a = _abox.createNode(false);
		IABoxNode<String, String, String, String> b = _abox.createNode(false);

		a.getRABox().getAssertedSuccessors().put("r", b);

		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(
			_abox,
			false);
		assertEquals(2, results.size());
		for (IReasonerResult<String, String, String, String> result : results) {
			final IABox<String, String, String, String> abox = result.getABox();
			assertEquals(2, abox.size());
			assertTrue(abox.getNode(a.getNodeID()).getRABox().hasSuccessor("r", b.getNodeID()));
		}
	}

	@Test
	public void testDoubleUnion()
		throws EReasonerException, ENodeMergeException, ParseException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);

		IABoxNode<String, String, String, String> a = _abox.createNode(false);
		a.addTerm(_parser.parse("(or C0 D0)"));
		a.addTerm(_parser.parse("(or C1 D1)"));

		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(
			_abox,
			false);
		assertEquals(4, results.size());
	}

	@Test(expected = EInconsistencyException.class)
	public void testDependencyDirectedBacktracking()
		throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);

		IABoxNode<String, String, String, String> a = _abox.createNode(false);
		/* create 750 unions. Without DDB, consistency takes forever */
		for (int i = 0; i < 750; ++i) {
			a.addTerm(_parser.parse(String.format("(or C%04d D%04d)", i, i)));
		}
		a.addTerm(_parser.parse("(some r (and C D))"));
		a.addTerm(_parser.parse("(only r (not C))"));

		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
			checkConsistency(
			_abox);
		assertTrue(results.isEmpty());
	}

//	@Test
//	public void testInverseRolePropagation()
//		throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
//	{
//		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
//		_abox.getAssertedRBox().addRole("rinv", RoleType.OBJECT_PROPERTY);
//		_abox.getAssertedRBox().addInverseRole("r", "rinv");
//
//		final IABoxNode<String, String, String, String> a = _abox.createNode(false);
//		a.addTerm(_parser.parse("(some r (only rinv A))"));
//		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
//			checkConsistency(
//			_abox);
//		assertEquals(1, results.size());
//		final IABox<String, String, String, String> abox = results.iterator().next().getABox();
//		abox.getNode(a.getNodeID()).getTerms().contains(_parser.parse("A"));
//	}
//
//	@Test
//	public void testInverseRoleDoubleBlocking()
//		throws ENodeMergeException, ParseException, EReasonerException, EInconsistencyException
//	{
//		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
//		_abox.getAssertedRBox().addRole("rinv", RoleType.OBJECT_PROPERTY);
//		_abox.getAssertedRBox().addInverseRole("r", "rinv");
//
//		final IABoxNode<String, String, String, String> a = _abox.createNode(false);
//		_abox.getTBox().add(_parser.parse("(some r _Thing_)"));
//		_abox.getTBox().add(_parser.parse("(some rinv _Thing_)"));
//		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.
//			checkConsistency(
//			_abox);
//		assertEquals(1, results.size());
//		final IABox<String, String, String, String> abox = results.iterator().next().getABox();
//		assertEquals(4, abox.size());
//	}

	@Test
	public void testDataPropertyWithObjectSuccessor()
		throws ENodeMergeException, EInconsistentRBoxException, EReasonerException, EInconsistencyException
	{
		final IIndividualABoxNode<String, String, String, String> a = _abox.createIndividualNode();
		final IIndividualABoxNode<String, String, String, String> b = _abox.createIndividualNode();
		a.getRABox().getAssertedSuccessors().put("r", b);
		_abox.getAssertedRBox().addRole("r", RoleType.DATA_PROPERTY);
		assertFalse(_reasoner.isConsistent(_abox));
	}

	@Test
	public void testObjectPropertyWithDataSuccessor()
		throws ENodeMergeException, EInconsistentRBoxException, EReasonerException, EInconsistencyException
	{
		final IIndividualABoxNode<String, String, String, String> a = _abox.createIndividualNode();
		final IDatatypeABoxNode<String, String, String, String> b = _abox.createDatatypeNode();
		a.getRABox().getAssertedSuccessors().put("r", b);
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		assertFalse(_reasoner.isConsistent(_abox));
	}


}
