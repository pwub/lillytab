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
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IDependencyMap;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermEntry;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleProperty;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleType;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleKRSSParser;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleStringDLTermFactory;
import java.text.ParseException;
import java.util.Collection;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
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
public class DependencyTrackingTest {
	private final IDLTermFactory<String, String, String, String> _termFactory = new SimpleStringDLTermFactory();
	private final IABoxFactory<String, String, String, String> _aboxFactory = new ABoxFactory<>(
		_termFactory);
	private IABox<String, String, String, String> _abox;
	private Reasoner<String, String, String, String> _reasoner;
	private SimpleKRSSParser _parser;


		public DependencyTrackingTest()
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
		reasonerOptions.setTracing(true);
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


	//	@Test
	@Test
	public void testExistsTracking()
		throws EInconsistencyException, EReasonerException, ParseException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		IABoxNode<String, String, String, String> aNode = _abox.createNode(false);
		final IDLClassExpression<String, String, String, String> b = _parser.parse("{b}");
		final IDLClassExpression<String, String, String, String> some = _parser.parse("(some r {b})");
		aNode.addTerm(some);

		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.checkConsistency(_abox,
																												 false);
		assertEquals(1, results.size());
		final IABox<String, String, String, String> abox = results.iterator().next().getABox();
		final IABoxNode<String, String, String, String> bNode = abox.getIndividualNode("b");

		assertTrue(abox.getDependencyMap().containsValue(bNode, b, aNode, some));
	}


	@Test
	public void testExistsTrackingFunctionalRole()
		throws EInconsistencyException, EReasonerException, ParseException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getAssertedRBox().setRoleProperty("r", RoleProperty.FUNCTIONAL);
		IABoxNode<String, String, String, String> aNode = _abox.createNode(false);
		final IDLClassExpression<String, String, String, String> b = _parser.parse("{b}");
		final IDLClassExpression<String, String, String, String> some = _parser.parse("(some r {b})");
		aNode.addTerm(some);

		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.checkConsistency(_abox,
																												 false);
		assertEquals(1, results.size());
		final IABox<String, String, String, String> abox = results.iterator().next().getABox();
		final IABoxNode<String, String, String, String> bNode = abox.getIndividualNode("b");

		assertTrue(abox.getDependencyMap().containsValue(bNode, b, aNode, some));
	}


	@Test
	public void testExistsTrackingMerge()
		throws EInconsistencyException, EReasonerException, ParseException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		IABoxNode<String, String, String, String> aNode = _abox.createNode(false);
		IABoxNode<String, String, String, String> bNode = _abox.getOrAddIndividualNode("b");
		final IDLClassExpression<String, String, String, String> b = _parser.parse("{b}");
		final IDLClassExpression<String, String, String, String> some = _parser.parse("(some r {b})");
		aNode.addTerm(some);

		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.checkConsistency(_abox,
																												 false);
		assertEquals(1, results.size());
		final IABox<String, String, String, String> abox = results.iterator().next().getABox();
		bNode = abox.getIndividualNode("b");

		assertEquals(2, abox.size());
		assertTrue(abox.getDependencyMap().containsValue(bNode, b, aNode, some));
	}


	@Test
	public void testForAllTracking()
		throws EInconsistencyException, EReasonerException, ParseException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		IABoxNode<String, String, String, String> aNode = _abox.createNode(false);
		IABoxNode<String, String, String, String> bNode = _abox.getOrAddIndividualNode("b");
		aNode.getRABox().getAssertedSuccessors().put("r", bNode.getNodeID());
		final IDLClassExpression<String, String, String, String> b = _parser.parse("{b}");
		final IDLClassExpression<String, String, String, String> only = _parser.parse("(only r {b})");
		aNode.addTerm(only);

		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.checkConsistency(_abox,
																												 false);
		assertEquals(1, results.size());
		final IABox<String, String, String, String> abox = results.iterator().next().getABox();
		bNode = abox.getIndividualNode("b");

		assertTrue(abox.getDependencyMap().containsValue(bNode, b, aNode, only));
	}


	@Test
	public void testForAllTrackingMerge()
		throws EInconsistencyException, EReasonerException, ParseException
	{
		_abox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		IABoxNode<String, String, String, String> aNode = _abox.createNode(false);
		IABoxNode<String, String, String, String> bNode = _abox.getOrAddIndividualNode("b");
		IABoxNode<String, String, String, String> cNode = _abox.getOrAddIndividualNode("c");
		aNode.getRABox().getAssertedSuccessors().put("r", cNode.getNodeID());
		final IDLClassExpression<String, String, String, String> b = _parser.parse("{b}");
		final IDLClassExpression<String, String, String, String> only = _parser.parse("(only r {b})");
		aNode.addTerm(only);

		assertEquals(3, _abox.size());

		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.checkConsistency(_abox,
																												 false);
		assertEquals(1, results.size());
		final IABox<String, String, String, String> abox = results.iterator().next().getABox();
		bNode = abox.getIndividualNode("b");

		assertTrue(abox.getDependencyMap().containsValue(bNode, b, aNode, only));
		assertEquals(2, abox.size());
	}
	@Test
	public void testIntersectionTracking()
		throws EInconsistencyException, EReasonerException, ParseException
	{
		IABoxNode<String, String, String, String> aNode = _abox.createNode(false);
		final IDLClassExpression<String, String, String, String> A = _parser.parse("A");
		final IDLClassExpression<String, String, String, String> B = _parser.parse("B");
		final IDLClassExpression<String, String, String, String> andAB = _parser.parse("(and A B)");
		aNode.addTerm(andAB);
		final Collection<? extends IReasonerResult<String, String, String, String>> results = _reasoner.checkConsistency(_abox,
																												 false);
		assertEquals(1, results.size());
		final IABox<String, String, String, String> abox = results.iterator().next().getABox();
		aNode = abox.getNode(aNode.getNodeID());

		assertTrue(abox.getDependencyMap().containsValue(aNode, A, aNode, andAB));
		assertTrue(abox.getDependencyMap().containsValue(aNode, B, aNode, andAB));
	}


	@Test
	public void testIntersectionMergeTracking()
		throws EInconsistencyException, EReasonerException, ParseException
	{
		IABoxNode<String, String, String, String> aNode = _abox.createNode(false);
		IABoxNode<String, String, String, String> bNode = _abox.getIndividualNode("b");
		final IDLClassExpression<String, String, String, String> A = _parser.parse("A");
		final IDLClassExpression<String, String, String, String> b = _parser.parse("{b}");
		final IDLClassExpression<String, String, String, String> andAB = _parser.parse("(and A {b})");
		aNode.addTerm(andAB);
		final Collection<? extends IReasonerResult<String, String, String, String>> aboxes = _reasoner.checkConsistency(_abox,
																												false);
		assertEquals(1, aboxes.size());
		final IABox<String, String, String, String> abox = aboxes.iterator().next().getABox();
		bNode = abox.getIndividualNode("b");

		assertTrue(abox.getDependencyMap().containsValue(bNode, A, bNode, andAB));
		assertTrue(abox.getDependencyMap().containsValue(bNode, b, bNode, andAB));
	}


	//	@Test
	@Test
	public void testNoTrackingExistingTerm()
		throws EInconsistencyException, EReasonerException, ParseException
	{
		IABoxNode<String, String, String, String> aNode = _abox.createNode(false);
		_abox.getTBox().add(_parser.parse("(implies A B)"));
		aNode.addTerm(_parser.parse("B"));
		aNode.addTerm(_parser.parse("A"));

		final TermEntry<String, String, String, String> aTerm = _abox.getTermEntryFactory().getEntry(aNode, _parser.parse("A"));
		final TermEntry<String, String, String, String> bTerm = _abox.getTermEntryFactory().getEntry(aNode, _parser.parse("B"));
		assertFalse(_abox.getDependencyMap().hasChild(aTerm, bTerm));
		assertFalse(_abox.getDependencyMap().hasChild(bTerm, aTerm));
		assertFalse(_abox.getDependencyMap().hasChild(aNode, _parser.parse("(implies A B)"), aNode, _parser.parse("A")));
		assertFalse(_abox.getDependencyMap().hasChild(aNode, _parser.parse("(implies A B)"), aNode, _parser.parse("B")));
		assertFalse(_abox.getDependencyMap().hasChild(aNode, _parser.parse("A"), aNode, _parser.parse("(implies A B)")));
		assertFalse(_abox.getDependencyMap().hasChild(aNode, _parser.parse("B"), aNode, _parser.parse("(implies A B)")));
	}

	class DependencyMatcher
		extends BaseMatcher<IABox<String, String, String, String>> {

		private final TermEntry<String, String, String, String> _child;
		private final TermEntry<String, String, String, String> _parent;


		DependencyMatcher(TermEntry<String, String, String, String> child, final TermEntry<String, String, String, String> parent)
		{
			_child = child;
			_parent = parent;
		}


		DependencyMatcher(
			final NodeID childNodeID,
			final IDLTerm<String, String, String, String> childTerm,
			final NodeID parentNodeID,
			final IDLTerm<String, String, String, String> parentTerm)
		{
			_child = _abox.getTermEntryFactory().getEntry(childNodeID, childTerm);
			_parent = _abox.getTermEntryFactory().getEntry(parentNodeID, parentTerm);
		}


		DependencyMatcher(
			final IABoxNode<?, ?, ?, ?> childNode,
			final IDLTerm<String, String, String, String> childTerm,
			final IABoxNode<?, ?, ?, ?> parentNode,
			final IDLTerm<String, String, String, String> parentTerm)
		{
			_child = _abox.getTermEntryFactory().getEntry(childNode.getNodeID(), childTerm);
			_parent = _abox.getTermEntryFactory().getEntry(parentNode.getNodeID(), parentTerm);
		}


		@Override
		public boolean matches(Object o)
		{
			if (o instanceof IABox) {
				@SuppressWarnings("unchecked")
				final IABox<String, String, String, String> abox = (IABox<String, String, String, String>) o;
				IDependencyMap<String, String, String, String> dependencyMap = abox.getDependencyMap();
				return dependencyMap.containsValue(_child, _parent);
			} else {
				return false;
			}
		}


		@Override
		public void describeTo(Description d)
		{
			d.appendText(String.format("Dependency '%s' -> '%s'", _child, _parent));
		}
	}

	class OnlyOneOf<T>
		extends BaseMatcher<T> {

		private final Iterable<Matcher<? extends T>> _matchers;


		OnlyOneOf(final Iterable<Matcher<? extends T>> matchers)
		{
			_matchers = matchers;
		}


		@Override
		public boolean matches(Object o)
		{
			int matchCount = 0;
			for (Matcher<? extends T> matcher : _matchers) {
				if (matcher.matches(o)) {
					++matchCount;
				}
				/* stop matching on second match */
				if (matchCount > 1) {
					return false;
				}
			}
			return matchCount == 1;
		}


		@Override
		public void describeTo(Description d)
		{
			d.appendText(String.format("OnlyOneOf(%s)", _matchers));
		}
	}
}
