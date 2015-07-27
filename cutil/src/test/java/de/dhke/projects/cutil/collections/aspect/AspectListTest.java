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
package de.dhke.projects.cutil.collections.aspect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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
public class AspectListTest
{
	private List<String> _baseList;
	private AspectList<String, List<String>> _aspectList;
	private AspectCollectionHistoryListener<String, List<String>> _listener;


	public AspectListTest()
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
		_baseList = new ArrayList<>();
		_aspectList = AspectList.decorate(_baseList, this);

		_baseList.add("1");
		_baseList.add("2");
		_baseList.add("3");
		_baseList.add("4");

		_listener = new AspectCollectionHistoryListener<>();
		_aspectList.getListeners().add(_listener);
	}


	@After
	public void tearDown()
	{
		_listener = null;
		_aspectList = null;
		_baseList = null;
	}


	/**
	 * Test of decorate method, of class AspectList.
	 */
	@Test
	public void testDecorate()
	{
		AspectList<String, List<String>> aspectList = AspectList.decorate(_baseList, this);
		assertNotSame(_baseList, aspectList);
		assertTrue(aspectList instanceof AspectList);
	}


	/**
	 * Test of size method, of class AspectList.
	 */
	@Test
	public void testSize()
	{
		assertEquals(4, _aspectList.size());
		_aspectList.remove(2);
		assertEquals(3, _aspectList.size());
		assertEquals(3, _baseList.size());
	}


	/**
	 * Test of isEmpty method, of class AspectList.
	 */
	@Test
	public void testIsEmpty()
	{
		assertFalse(_aspectList.isEmpty());
		assertFalse(_aspectList.isEmpty());
		_aspectList.clear();
		assertTrue(_aspectList.isEmpty());
		assertTrue(_baseList.isEmpty());
	}


	/**
	 * Test of contains method, of class AspectList.
	 */
	@Test
	public void testContains()
	{
		assertTrue(_aspectList.contains("1"));
		assertTrue(_aspectList.contains("2"));
		assertTrue(_aspectList.contains("3"));
		assertTrue(_aspectList.contains("4"));
		assertFalse(_aspectList.contains("5"));
		assertTrue(_baseList.contains("1"));
		assertTrue(_baseList.contains("2"));
		assertTrue(_baseList.contains("3"));
		assertTrue(_baseList.contains("4"));
		assertFalse(_baseList.contains("5"));
	}


	/**
	 * Test of iterator method, of class AspectList.
	 */
	@Test
	public void testIterator()
	{
		Iterator<String> iter = _aspectList.iterator();
		assertEquals("1", iter.next());
		assertEquals("2", iter.next());
		assertEquals("3", iter.next());
		assertEquals("4", iter.next());

		iter = _aspectList.iterator();
	}


	/**
	 * Test of iterator method, of class AspectList.
	 */
	@Test
	public void testIteratorRemove_veto()
	{
		Iterator<String> iter = _aspectList.iterator();
		assertEquals("1", iter.next());

		try {
			_listener.vetoRemove = true;
			iter.remove();
			fail("Veto exception not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_aspectList.contains("1"));
	}


	/**
	 * Test of toArray method, of class AspectList.
	 */
	@Test
	public void testToArray_0args()
	{
		assertArrayEquals(new String[]{"1", "2", "3", "4"}, _aspectList.toArray());
	}


	/**
	 * Test of toArray method, of class AspectList.
	 */
	@Test
	public void testToArray_GenericType()
	{
		assertArrayEquals(new String[]{"1", "2", "3", "4"}, _aspectList.toArray(new String[_aspectList.size()]));
	}


	/**
	 * Test of add method, of class AspectList.
	 */
	@Test
	public void testAdd()
	{
		_aspectList.add("5");
		assertEquals(1, _listener.beforeAddEvents.size());
		assertEquals("5", _listener.beforeAddEvents.get(0).getItem());
		assertEquals(1, _listener.afterAddEvents.size());
		assertEquals("5", _listener.afterAddEvents.get(0).getItem());
		_aspectList.add("2");
		assertEquals(2, _listener.beforeAddEvents.size());
		assertEquals("5", _listener.beforeAddEvents.get(0).getItem());
		assertEquals("2", _listener.beforeAddEvents.get(1).getItem());
		assertEquals(2, _listener.afterAddEvents.size());
		assertEquals("5", _listener.afterAddEvents.get(0).getItem());
		assertEquals("2", _listener.afterAddEvents.get(1).getItem());
	}


	@Test
	public void testAddVeto()
	{
		try {
			_listener.vetoAdd = true;
			_aspectList.add("5");
			fail("Veto exception not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertFalse(_aspectList.contains("5"));
	}


	/**
	 * Test of remove method, of class AspectList.
	 */
	@Test
	public void testRemove()
	{
		assertFalse(_aspectList.remove("7"));
		assertTrue(_aspectList.remove("3"));
		assertEquals("3", _listener.beforeRemoveEvents.get(0).getItem());
		assertEquals("3", _listener.afterRemoveEvents.get(0).getItem());
	}


	/**
	 * Test of remove method, of class AspectList.
	 */
	@Test
	public void testRemove_veto()
	{
		try {
			_listener.vetoRemove = true;
			_aspectList.remove("3");
			fail("Veto exception not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_aspectList.contains("3"));
	}


	/**
	 * Test of containsAll method, of class AspectList.
	 */
	@Test
	public void testContainsAll()
	{
		assertTrue(_aspectList.containsAll(Arrays.asList("1", "2", "3", "4")));
		assertFalse(_aspectList.containsAll(Arrays.asList("1", "2", "3", "4", "5")));
	}


	/**
	 * Test of addAll method, of class AspectList.
	 */
	@Test
	public void testAddAll()
	{
		assertTrue(_aspectList.addAll(Arrays.asList("5", "6")));
		assertEquals(2, _listener.beforeAddEvents.size());
		assertEquals("5", _listener.beforeAddEvents.get(0).getItem());
		assertEquals("6", _listener.beforeAddEvents.get(1).getItem());
		assertEquals(2, _listener.afterAddEvents.size());
		assertEquals("5", _listener.afterAddEvents.get(0).getItem());
		assertEquals("6", _listener.afterAddEvents.get(1).getItem());
		assertTrue(_aspectList.contains("5"));
		assertTrue(_aspectList.contains("6"));
		assertFalse(_aspectList.contains("7"));
	}


	@Test
	public void testAddAll_veto()
	{
		_listener.vetoAdd = true;
		try {
			_aspectList.addAll(Arrays.asList("5", "6"));
			fail("Veto exception not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_listener.beforeAddEvents.isEmpty());
		assertTrue(_listener.afterAddEvents.isEmpty());
		assertFalse(_aspectList.contains("5"));
		assertFalse(_aspectList.contains("6"));
		assertFalse(_aspectList.contains("7"));
	}


	/**
	 * Test of removeAll method, of class AspectList.
	 */
	@Test
	public void testRemoveAll()
	{
	{
		assertFalse(_aspectList.removeAll(Arrays.asList("5", "6")));
		_aspectList.removeAll(Arrays.asList("2", "4"));
		assertFalse(_aspectList.contains("2"));
		assertFalse(_aspectList.contains("4"));
		assertEquals(4, _listener.beforeRemoveEvents.size());
		assertEquals("5", _listener.beforeRemoveEvents.get(0).getItem());
		assertEquals("6", _listener.beforeRemoveEvents.get(1).getItem());
		assertEquals("2", _listener.beforeRemoveEvents.get(2).getItem());
		assertEquals("4", _listener.beforeRemoveEvents.get(3).getItem());
		assertEquals(2, _listener.afterRemoveEvents.size());
		assertEquals("2", _listener.afterRemoveEvents.get(0).getItem());
		assertEquals("4", _listener.afterRemoveEvents.get(1).getItem());
	}
	}


	@Test
	public void testRemoveAll_veto()
	{
		_listener.vetoRemove = true;
		try {
			_aspectList.removeAll(Arrays.asList("2", "4"));
			fail("Veto exception not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_aspectList.contains("2"));
		assertTrue(_aspectList.contains("4"));
		assertTrue(_listener.beforeAddEvents.isEmpty());
		assertTrue(_listener.afterAddEvents.isEmpty());
	}


	/**
	 * Test of retainAll method, of class AspectList.
	 */
	@Test
	public void testRetainAll()
	{
		assertFalse(_aspectList.retainAll(Arrays.asList("1", "2", "3", "4")));
		assertTrue(_aspectList.retainAll(Arrays.asList("2", "3")));
		assertFalse(_aspectList.contains("1"));
		assertFalse(_aspectList.contains("4"));
	}

	/**
	 * Test of retainAll method, of class AspectList.
	 */
	@Test
	public void testRetainAll_veto()
	{
		_listener.vetoRemove = false;
		_aspectList.retainAll(Arrays.asList("1", "2", "3", "4"));
		_listener.vetoRemove = true;
		try {
			assertTrue(_aspectList.retainAll(Arrays.asList("2", "3")));
			fail("Veto exception not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_aspectList.contains("1"));
		assertTrue(_aspectList.contains("4"));
	}

	/**
	 * Test of clear method, of class AspectList.
	 */
	@Test
	public void testClear()
	{
		_aspectList.clear();
		assertTrue(_aspectList.isEmpty());
		assertEquals(1, _listener.beforeClearEvents.size());
		assertEquals(1, _listener.afterClearEvents.size());

		_aspectList.clear();
		assertEquals(1, _listener.beforeClearEvents.size());
		assertEquals(1, _listener.afterClearEvents.size());
	}

	/**
	 * Test of clear method, of class AspectList.
	 */
	@Test
	public void testClear_veto()
	{
		_listener.vetoClear = true;
		try {
			_aspectList.clear();
			fail("Veto exception not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertFalse(_aspectList.isEmpty());

		_baseList.clear();
		_aspectList.clear();
	}

	/**
	 * Test of getDecoratee method, of class AspectList.
	 */
	@Test
	public void testGetDecoratee()
	{
		assertSame(_baseList, _aspectList.getDecoratee());
	}
}
