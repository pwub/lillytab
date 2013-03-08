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
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassReference;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleKRSSParser;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleStringDLTermFactory;
import java.text.ParseException;
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
public class ABoxNodeTest {

	private IDLTermFactory<String, String, String> _termFactory;
	private IABoxFactory<String, String, String> _aboxFactory;
	private IABox<String, String, String> _abox;
	private IABoxNode<String, String, String> _aboxNode;
	private SimpleKRSSParser _parser;


	public ABoxNodeTest()
	{
	}


	@BeforeClass
	public static void setUpClass()
		throws Exception
	{
	}


	@AfterClass
	public static void tearDownClass()
		throws Exception
	{
	}


	@Before
	public void setUp()
		throws EReasonerException, EInconsistencyException
	{
		_termFactory = new SimpleStringDLTermFactory();
		_parser = new SimpleKRSSParser(_termFactory);
		_aboxFactory = new ABoxFactory<String, String, String>(_termFactory);
		_abox = _aboxFactory.createABox();
		_aboxNode = _abox.getOrAddNamedNode("Node", false);
		_aboxNode.addUnfoldedDescription(_termFactory.getDLClassReference("A"));
	}


	@After
	public void tearDown()
	{
		_aboxNode = null;
		_abox = null;
		_termFactory = null;
		_parser = null;
	}


	/**
	 * Test of clone method, of class IABoxNode.
	 */
	@Test
	public void testClone()
		throws ENodeMergeException
	{
		final IABox<String, String, String> box2 = _aboxFactory.createABox();
		final IABoxNode<String, String, String> klone = _aboxNode.clone(box2);
		box2.add(klone);
		assertNotSame(_aboxNode, klone);
		assertEquals(_aboxNode, klone);

		assertSame(box2, klone.getABox());
		assertSame(_abox, _aboxNode.getABox());

		assertTrue(klone.getTerms().containsAll(_aboxNode.getTerms()));
		assertEquals(klone.getTerms().size(), _aboxNode.getTerms().size());
		IDLClassReference<String, String, String> B = _termFactory.getDLClassReference("B");
		klone.addUnfoldedDescription(B);

		assertFalse(_aboxNode.getTerms().contains(B));
	}


	/**
	 * Test of getTerms method, of class IABoxNode.
	 */
	@Test
	public void testGetConceptTerms()
	{
		assertTrue(_aboxNode.getTerms().contains(_termFactory.getDLClassReference("A")));
		assertTrue(_aboxNode.getTerms().contains(_termFactory.getDLNominalReference("Node")));
		assertTrue(_aboxNode.getTerms().contains(_termFactory.getDLThing()));
		assertEquals(3, _aboxNode.getTerms().size());
	}


	/**
	 * Test of getABox method, of class IABoxNode.
	 */
	@Test
	public void testGetABox()
	{
		assertSame(_abox, _aboxNode.getABox());
	}


	/**
	 * Test of getSuccessorPairs method, of class IABoxNode.
	 */
	@Test
	public void testGetSuccessors()
		throws EReasonerException, ENodeMergeException
	{
		final IABox<String, String, String> box2 = _aboxFactory.createABox();
		IABoxNode<String, String, String> node2 = _abox.createNode(false);
		_aboxNode.getRABox().getAssertedSuccessors().put("r", node2.getNodeID());
		assertTrue(_aboxNode.getRABox().getAssertedSuccessors().get("r").contains(node2.getNodeID()));
		IABoxNode<String, String, String> klone = _aboxNode.clone(box2);
		assertTrue(klone.getRABox().getAssertedSuccessors().get("r").contains(node2.getNodeID()));
	}


	/**
	 * Test of getPredecessorPairs method, of class IABoxNode.
	 */
	@Test
	public void testGetPredecessors()
		throws EReasonerException, ENodeMergeException
	{
		final IABox<String, String, String> box2 = _aboxFactory.createABox();
		final IABoxNode<String, String, String> node2 = _abox.createNode(false);

		_aboxNode.getRABox().getAssertedSuccessors().put("r", node2.getNodeID());

		assertTrue(node2.getRABox().getAssertedPredecessors().get("r").contains(_aboxNode.getNodeID()));
	}


	@Test
	public void testDeepHashCode()
		throws EReasonerException, ENodeMergeException
	{
		final IABoxNode<String, String, String> node1 = _abox.createNode(false);
		node1.addUnfoldedDescription(_termFactory.getDLClassReference("A"));
		final IABoxNode<String, String, String> node2 = _abox.createNode(false);
		node2.addUnfoldedDescription(_termFactory.getDLClassReference("A"));

		assertTrue(node1.deepHashCode() == node2.deepHashCode());

		/* not necessarily */
		node2.addUnfoldedDescription(_termFactory.getDLClassReference("B"));
		assertFalse(node1.deepHashCode() == node2.deepHashCode());

		node1.addUnfoldedDescription(_termFactory.getDLClassReference("B"));
		assertTrue(node1.deepHashCode() == node2.deepHashCode());

		IABoxNode<String, String, String> node3 = _abox.createNode(false);
		node1.getRABox().getAssertedSuccessors().put("r", node3.getNodeID());

		/* not necessarily */
		assertFalse(node1.deepHashCode() == node2.deepHashCode());

		node2.getRABox().getAssertedSuccessors().put("r", node3.getNodeID());
		assertTrue(node1.deepHashCode() == node2.deepHashCode());
	}


	@Test
	public void testDeepEquals()
		throws EReasonerException, ENodeMergeException
	{
		final IABoxNode<String, String, String> node1 = _abox.createNode(false);
		node1.addUnfoldedDescription(_termFactory.getDLClassReference("A"));
		final IABoxNode<String, String, String> node2 = _abox.createNode(false);
		node2.addUnfoldedDescription(_termFactory.getDLClassReference("A"));

		assertTrue(node1.deepEquals(node2));

		/* not necessarily */
		node2.addUnfoldedDescription(_termFactory.getDLClassReference("B"));
		assertFalse(node1.deepEquals(node2));

		node1.addUnfoldedDescription(_termFactory.getDLClassReference("B"));
		assertTrue(node1.deepEquals(node2));

		IABoxNode<String, String, String> node3 = _abox.createNode(false);
		node1.getRABox().getAssertedSuccessors().put("r", node3.getNodeID());

		/* not necessarily */
		assertFalse(node1.deepEquals(node2));

		node2.getRABox().getAssertedSuccessors().put("r", node3.getNodeID());
		assertTrue(node1.deepEquals(node2));
	}


	@Test
	public void recursiveUnfoldTest()
		throws EReasonerException, ParseException, ENodeMergeException
	{
		final IABoxNode<String, String, String> node1 = _abox.createNode(false);
		_abox.getTBox().add(_parser.parse("(implies A B)"));
		_abox.getTBox().add(_parser.parse("(implies B C)"));
		node1.addUnfoldedDescription(_termFactory.getDLClassReference("A"));
		assertTrue(node1.getTerms().contains(_termFactory.getDLClassReference("C")));
	}


	@Test
	public void bigRecursiveUnfoldTest()
		throws EReasonerException, ParseException, ENodeMergeException
	{
		final int n = 2048;

		for (int i = 0; i < n; ++i) {
			final IDLRestriction<String, String, String> res = _parser.parse(
				String.format("(implies C%s C%s)", i, i + 1));
			_abox.getTBox().add(res);
		}
		final IABoxNode<String, String, String> node1 = _abox.createNode(false);

		node1.addUnfoldedDescription(_termFactory.getDLClassReference("C0"));
		assertTrue(node1.getTerms().contains(_termFactory.getDLClassReference(String.format("C%s", n))));
	}


	@Test
	public void nnfUnfoldTest()
		throws ParseException, ENodeMergeException
	{
		final IABoxNode<String, String, String> node1 = _abox.createNode(false);
		node1.addUnfoldedDescription(_parser.parse("(implies A B)"));
		assertFalse(node1.getTerms().contains(_parser.parse("(implies A B)")));
		assertTrue(node1.getTerms().contains(_parser.parse("(or (not A) B)")));
	}
}
