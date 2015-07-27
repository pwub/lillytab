/**
 * (c) 2009-2014 Peter Wullinger
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
package de.dhke.projects.cutil.collections.tree;

import de.dhke.projects.cutil.collections.tree.IDecisionTree.Node;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
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
public class DecisionTreeTest {

	private DecisionTree<String> _decisionTree;

	public DecisionTreeTest()
	{
	}

	@BeforeClass
	public static void setUpClass() throws Exception
	{
	}

	@AfterClass
	public static void tearDownClass() throws Exception
	{
	}

	@Before
	public void setUp()
	{
		_decisionTree = new DecisionTree<>();
	}

	@After
	public void tearDown()
	{
		_decisionTree = null;
	}


	/**
	 * Test of getRoot method, of class DecisionTree.
	 */
	@Test
	public void testGetRoot()
	{
		assertNotNull(_decisionTree.getRoot());
		/* the root node has a "null" data object */
		assertFalse(_decisionTree.getRoot().hasData());
		assertFalse(_decisionTree.hasData(_decisionTree.getRoot()));

		assertSame(_decisionTree.fork(_decisionTree.getRoot(), "1"), _decisionTree.getRoot());
		DecisionTree.checkDecisionTree(_decisionTree);

		assertEquals("1", _decisionTree.getRoot().getData());
		assertTrue(_decisionTree.getRoot().hasData());
		assertFalse(_decisionTree.getRoot().hasChildren());
	}

	/**
	 * Test of getChildren method, of class DecisionTree.
	 **/
	@Test
	public void testGetChildren()
	{
		assertTrue(_decisionTree.getRoot().getChildren().isEmpty());
		final Node<String> n1 = _decisionTree.fork(_decisionTree.getRoot(), "A");
		DecisionTree.checkDecisionTree(_decisionTree);
		assertTrue(_decisionTree.getRoot().getChildren().isEmpty());
		final Node<String> n2 = _decisionTree.fork(_decisionTree.getRoot(), "B");
		DecisionTree.checkDecisionTree(_decisionTree);
		assertFalse(_decisionTree.getRoot().getChildren().isEmpty());
		assertEquals(2, _decisionTree.getRoot().getChildren().size());

		assertTrue(_decisionTree.getRoot().getChildren().contains(n1));
		assertTrue(_decisionTree.getRoot().getChildren().contains(n2));
	}

	/**
	 * Test of hasChildren method, of class DecisionTree.
	 **/
	@Test
	public void testHasChildren()
	{
		assertFalse(_decisionTree.getRoot().hasChildren());
		final Node<String> n1 = _decisionTree.fork(_decisionTree.getRoot(), "A");
		DecisionTree.checkDecisionTree(_decisionTree);
		assertFalse(_decisionTree.getRoot().hasChildren());
		assertFalse(n1.hasChildren());
		final Node<String> n2 = _decisionTree.fork(_decisionTree.getRoot(), "B");
		DecisionTree.checkDecisionTree(_decisionTree);
		assertTrue(_decisionTree.getRoot().hasChildren());
		// assertFalse(n1.hasChildren());
		assertFalse(n2.hasChildren());
	}

	/**
	 * Test of getParent method, of class DecisionTree.
	 */
	@Test
	public void testGetParent()
	{
		assertNull(_decisionTree.getRoot().getParent());
		final Node<String> n1 = _decisionTree.fork(_decisionTree.getRoot(), "A");
		DecisionTree.checkDecisionTree(_decisionTree);
		assertNull(n1.getParent());
		assertNull(_decisionTree.getParent(n1));
		final Node<String> n2 = _decisionTree.fork(_decisionTree.getRoot(), "B");
		DecisionTree.checkDecisionTree(_decisionTree);

		assertEquals(_decisionTree.getRoot(), n2.getParent());
		assertEquals(_decisionTree.getRoot(), _decisionTree.getParent(n2));

		final Node<String> n3 = _decisionTree.fork(n2, "C");
		DecisionTree.checkDecisionTree(_decisionTree);
		final Node<String> n4 = _decisionTree.branch(n3, "D");
		DecisionTree.checkDecisionTree(_decisionTree);
		assertEquals(n3.getParent(), n4.getParent());
	}

	/**
	 * Test of hasData method, of class DecisionTree.
	 */
	@Test
	public void testHasData()
	{
		assertFalse(_decisionTree.getRoot().hasData());
		_decisionTree.fork(_decisionTree.getRoot(), "A");
		DecisionTree.checkDecisionTree(_decisionTree);
		assertTrue(_decisionTree.getRoot().hasData());
		_decisionTree.fork(_decisionTree.getRoot(), "B");
		DecisionTree.checkDecisionTree(_decisionTree);
		assertFalse(_decisionTree.getRoot().hasData());

		for (Node<String> child : _decisionTree.getRoot().getChildren()) {
			assertTrue(child.hasData());
		}
	}

	/**
	 * Test of getData method, of class DecisionTree.
	 */
	@Test
	public void testGetData()
	{
		assertNull(_decisionTree.getRoot().getData());
		_decisionTree.fork(_decisionTree.getRoot(), "A");
		DecisionTree.checkDecisionTree(_decisionTree);
		assertEquals("A", _decisionTree.getRoot().getData());
		_decisionTree.fork(_decisionTree.getRoot(), "B");
		DecisionTree.checkDecisionTree(_decisionTree);
		assertFalse(_decisionTree.getRoot().hasData());

		for (Node<String> child : _decisionTree.getRoot().getChildren()) {
			assertTrue(child.hasData());
		}
	}

	/**
	 * Test of fork method, of class DecisionTree.
	 */
	@Test
	public void testCreate_IDecisionTreeNode()
	{
		/* TODO */
	}

	/**
	 * Test of fork method, of class DecisionTree.
	 */
	@Test
	public void testfork_IDecisionTreeNode_GenericType()
	{
		final Node<String> n1 = _decisionTree.fork(_decisionTree.getRoot(), "A");
		DecisionTree.checkDecisionTree(_decisionTree);
		assertSame(n1, _decisionTree.getRoot());
		assertEquals(n1, _decisionTree.getRoot());
		assertTrue(n1.hasData());
		assertEquals("A", n1.getData());

		final Node<String> n2 = _decisionTree.fork(_decisionTree.getRoot(), "B");
		DecisionTree.checkDecisionTree(_decisionTree);
		assertEquals("B", n2.getData());
		assertSame(_decisionTree.getRoot(), n2.getParent());
		assertEquals(_decisionTree.getRoot(), n2.getParent());
	}

	/**
	 * Test of branch method, of class DecisionTree.
	 */
	@Test
	public void testbranch_IDecisionTreeNode_GenericType()
	{
		final Node<String> n1 = _decisionTree.branch(_decisionTree.getRoot(), "A");
		DecisionTree.checkDecisionTree(_decisionTree);
		assertSame(n1, _decisionTree.getRoot());
		assertEquals(n1, _decisionTree.getRoot());
		assertTrue(n1.hasData());
		assertEquals("A", n1.getData());

		final Node<String> n2 = _decisionTree.branch(_decisionTree.getRoot(), "B");
		DecisionTree.checkDecisionTree(_decisionTree);
		assertEquals("B", n2.getData());
		assertSame(_decisionTree.getRoot(), n2.getParent());
		assertEquals(_decisionTree.getRoot(), n2.getParent());

		final Node<String> n3 = _decisionTree.branch(n2, "C");
		assertEquals("C", n3.getData());
		assertEquals(n2.getParent(), n3.getParent());
	}

	/**
	 * Test of remove method, of class DecisionTree.
	 */
	@Test
	public void testRemove()
	{
		try {
			_decisionTree.getRoot().remove();
			throw new AssertionError("Should not be able to remove empty root");
		} catch (IllegalArgumentException ex) {
			/* ignore */
		}
		DecisionTree.checkDecisionTree(_decisionTree);
		assertNotNull(_decisionTree.getRoot());
		try {
			_decisionTree.remove(_decisionTree.getRoot());
			throw new AssertionError("Should not be able to remove empty root");
		} catch (IllegalArgumentException ex) {
			/* ignore */
		}
		DecisionTree.checkDecisionTree(_decisionTree);
		assertNotNull(_decisionTree.getRoot());

		_decisionTree.fork(_decisionTree.getRoot(), "1");
		DecisionTree.checkDecisionTree(_decisionTree);
		_decisionTree.remove(_decisionTree.getRoot());
		DecisionTree.checkDecisionTree(_decisionTree);
		assertFalse(_decisionTree.getRoot().hasData());
		assertNull(_decisionTree.getRoot().getData());

		_decisionTree.fork(_decisionTree.getRoot(), "1");
		DecisionTree.checkDecisionTree(_decisionTree);
		_decisionTree.getRoot().remove();
		DecisionTree.checkDecisionTree(_decisionTree);
		assertFalse(_decisionTree.getRoot().hasData());
		assertNull(_decisionTree.getRoot().getData());

		_decisionTree.fork(_decisionTree.getRoot(), "1");
		DecisionTree.checkDecisionTree(_decisionTree);
		Node<String> n21 = _decisionTree.fork(_decisionTree.getRoot(), "2.1");
		Node<String> n221 = _decisionTree.fork(n21, "2.2.1");
		Node<String> n222 = _decisionTree.branch(n221, "2.2.2");
		_decisionTree.fork(n21, "2.2.3");
		DecisionTree.checkDecisionTree(_decisionTree);
		_decisionTree.fork(n21, "2.3");
		DecisionTree.checkDecisionTree(_decisionTree);

		Node<String> parent = n222.getParent();
		n222.remove();
		DecisionTree.checkDecisionTree(_decisionTree);
		assertFalse(parent.getChildren().contains(n222));
	}
	
	/**
	 * Test of remove method, of class DecisionTree.
	 */
	@Test
	public void testRemoveLast()
	{
		_decisionTree.branch(_decisionTree.getRoot(), "1.1");
		Node<String> n1 = _decisionTree.branch(_decisionTree.getRoot(), "1.2.1");
		Node<String> n2 = _decisionTree.branch(n1, "1.2.2.1");
		n2.branch("1.2.2.2");
		
		Iterator<? extends Node<String>> iter = _decisionTree.iterator();
		iter.next();
		iter.next();
		Node<String> toRemove = iter.next();
		toRemove.remove();
		
		iter = _decisionTree.iterator();
		iter.next();
		toRemove = iter.next();
		toRemove.remove();
	
		iter = _decisionTree.iterator();
		iter.next();
		toRemove = iter.next();
		toRemove.remove();
		
		iter = _decisionTree.iterator();
		toRemove = iter.next();
		toRemove.remove();
		assertFalse(_decisionTree.getRoot().hasData());
		assertFalse(_decisionTree.getRoot().hasChildren());
	}

	/**
	 * Test of getPath method, of class DecisionTree.
	 */
	@Test
	public void testGetPath()
	{
		_decisionTree.fork(_decisionTree.getRoot(), "1");
		final Node<String> n21 = _decisionTree.fork(_decisionTree.getRoot(), "2.1");
		DecisionTree.checkDecisionTree(_decisionTree);
		final Node<String> n221 = _decisionTree.fork(n21, "2.2.1");
		DecisionTree.checkDecisionTree(_decisionTree);
		final Node<String> n222 = _decisionTree.branch(n221, "2.2.2");
		DecisionTree.checkDecisionTree(_decisionTree);
		final Node<String> n23 = _decisionTree.fork(n21, "2.3");
		DecisionTree.checkDecisionTree(_decisionTree);

		final List<? extends Node<String>> path = n222.getPath();
		assertArrayEquals(new Object[]{_decisionTree.getRoot(), n222.getParent(), n222}, path.toArray());
	}

	/**
	 * Test of iteratorTest method, of class DecisionTree.
	 */
	@Test
	public void iteratorTest()
	{
		_decisionTree.fork(_decisionTree.getRoot(), "1");
		final Node<String> n21 = _decisionTree.fork(_decisionTree.getRoot(), "2.1");
		DecisionTree.checkDecisionTree(_decisionTree);
		final Node<String> n221 = _decisionTree.fork(n21, "2.2.1");
		DecisionTree.checkDecisionTree(_decisionTree);
		final Node<String> n222 = _decisionTree.branch(n221, "2.2.2");
		DecisionTree.checkDecisionTree(_decisionTree);
		final Node<String> n23 = _decisionTree.fork(n21, "2.3");
		DecisionTree.checkDecisionTree(_decisionTree);

		Iterator<? extends Node<String>> iter = _decisionTree.iterator();
		assertTrue(iter.hasNext());
		assertEquals("1", iter.next().getData());
		assertTrue(iter.hasNext());
		assertEquals("2.1", iter.next().getData());
		assertTrue(iter.hasNext());
		assertEquals("2.2.1", iter.next().getData());
		assertTrue(iter.hasNext());
		assertEquals("2.2.2", iter.next().getData());
		assertTrue(iter.hasNext());
		assertEquals("2.3", iter.next().getData());
		assertFalse(iter.hasNext());
		try {
			iter.next();
			assertFalse("Iterator did not throw Exception when accessed after its end", true);
		} catch (NoSuchElementException ex) {
			/* ignore */
		}

		n222.remove();
		iter = _decisionTree.iterator();
		assertTrue(iter.hasNext());
		assertEquals("1", iter.next().getData());
		assertTrue(iter.hasNext());
		assertEquals("2.1", iter.next().getData());
		assertTrue(iter.hasNext());
		assertEquals("2.3", iter.next().getData());
		assertTrue(iter.hasNext());
		assertEquals("2.2.1", iter.next().getData());
		assertFalse(iter.hasNext());
	}
}
