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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.collections15.keyvalue.DefaultMapEntry;
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
public class AspectMapValueCollectionTest
{
	private Map<String, String> _baseMap;
	private AspectMap<String, String, Map<String, String>> _aspectMap;
	private AspectCollectionHistoryListener<Map.Entry<String, String>, Map<String, String>> _listener;
	private Collection<String> _values;

	public AspectMapValueCollectionTest()
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
		_baseMap = new TreeMap<>();
		_baseMap.put("A", "1");
		_baseMap.put("B", "2");
		_baseMap.put("C", "3");
		_baseMap.put("D", "4");

		_aspectMap = AspectMap.decorate(_baseMap, this);
		_listener = new AspectCollectionHistoryListener<>();
		_aspectMap.getListeners().add(_listener);
		_values = _aspectMap.values();
	}

	@After
	public void tearDown()
	{
		_values = null;
		_listener = null;
		_aspectMap = null;
		_baseMap = null;
	}

	/**
	 * Test of size method, of class AspectMapValueCollection.
	 */
	@Test
	public void testSize()
	{
		assertEquals(4, _values.size());
		_baseMap.remove("A");
		assertEquals(3, _values.size());
	}

	/**
	 * Test of isEmpty method, of class AspectMapValueCollection.
	 */
	@Test
	public void testIsEmpty()
	{
		assertFalse(_values.isEmpty());
		_baseMap.clear();
		assertTrue(_values.isEmpty());
	}

	/**
	 * Test of contains method, of class AspectMapValueCollection.
	 */
	@Test
	public void testContains()
	{
		assertTrue(_values.contains("1"));
		assertTrue(_values.contains("2"));
		assertTrue(_values.contains("3"));
		assertTrue(_values.contains("4"));
		assertFalse(_values.contains("5"));
	}

	/**
	 * Test of iterator method, of class AspectMapValueCollection.
	 */
	@Test
	public void testIterator()
	{
		TestHelper.assertSequenceEquals(Arrays.asList("1", "2", "3", "4"), _values);
	}

	/**
	 * Test of iterator method, of class AspectMapValueCollection.
	 */
	@Test
	public void testIterator_remove()
	{
		_baseMap.put("_", "1");
		Iterator<String> iter = _values.iterator();
		/* this empty while loop is intentional */
		while (!iter.next().equals("1"));
		iter.remove();
		/* the other value stays. It is not really possible to identify, which one */
		assertTrue(_values.contains("1"));
	}

	/**
	 * Test of iterator method, of class AspectMapValueCollection.
	 */
	@Test
	public void testIterator_remove_veto()
	{
		_listener.vetoRemove = true;
		Iterator<String> iter = _values.iterator();
		/* this empty while loop is intentional */
		while (!iter.next().equals("1"));
		try {
			iter.remove();
			fail("Remove veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_values.contains("1"));
	}

	/**
	 * Test of toArray method, of class AspectMapValueCollection.
	 */
	@Test
	public void testToArray_0args()
	{
		assertArrayEquals(new Object[]{"1", "2", "3", "4"}, _values.toArray());
	}

	/**
	 * Test of toArray method, of class AspectMapValueCollection.
	 */
	@Test
	public void testToArray_GenericType()
	{
		assertArrayEquals(new String[]{"1", "2", "3", "4"}, _values.toArray(new String[_values.size()]));
	}

	/**
	 * Test of add method, of class AspectMapValueCollection.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testAdd()
	{
		_values.add("5");
	}

	/**
	 * Test of remove method, of class AspectMapValueCollection.
	 */
	@Test
	public void testRemove()
	{
		assertFalse(_values.remove("5"));
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());

		_aspectMap.put("_", "1");
		assertTrue(_values.remove("1"));
		assertFalse(_aspectMap.containsKey("A"));
		assertEquals("1", _aspectMap.get("_"));

		assertEquals(1, _listener.beforeRemoveEvents.size());
		assertEquals(new DefaultMapEntry<>("A", "1"), _listener.beforeRemoveEvents.get(0).getItem());
		assertEquals(1, _listener.afterRemoveEvents.size());
		assertEquals(new DefaultMapEntry<>("A", "1"), _listener.afterRemoveEvents.get(0).getItem());
	}

	/**
	 * Test of remove method, of class AspectMapValueCollection.
	 */
	@Test
	public void testRemove_veto()
	{
		_listener.vetoRemove = true;
		assertFalse(_values.remove("5"));
		try {
			_values.remove("1");
			fail("Remove veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_aspectMap.containsKey("A"));
	}

	/**
	 * Test of containsAll method, of class AspectMapValueCollection.
	 */
	@Test
	public void testContainsAll()
	{
		assertTrue(_values.containsAll(Arrays.asList("1", "2", "3")));
		assertTrue(_values.containsAll(Arrays.asList("1", "2", "3", "4")));
		assertFalse(_values.containsAll(Arrays.asList("1", "2", "3", "4", "5")));
		assertFalse(_values.containsAll(Arrays.asList("5", "6")));
	}

	/**
	 * Test of addAll method, of class AspectMapValueCollection.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testAddAll()
	{
		_values.addAll(Arrays.asList("@", "_"));
	}

	/**
	 * Test of removeAll method, of class AspectMapValueCollection.
	 */
	@Test
	public void testRemoveAll()
	{
		assertFalse(_values.removeAll(Arrays.asList("5", "6")));
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());
		_baseMap.put("_", "1");
		/* removeAll() drops ALL keys with matching values */
		assertTrue(_values.removeAll(Arrays.asList("1", "2")));
		assertFalse(_values.contains("1"));
		assertFalse(_values.contains("2"));
		assertFalse(_aspectMap.containsKey("A"));
		assertFalse(_aspectMap.containsKey("B"));
		assertFalse(_aspectMap.containsKey("_"));
		assertEquals(3, _listener.beforeRemoveEvents.size());
		TestHelper.eventListContains(new DefaultMapEntry<>("A", "1"), _listener.beforeRemoveEvents);
		TestHelper.eventListContains(new DefaultMapEntry<>("_", "1"), _listener.beforeRemoveEvents);
		TestHelper.eventListContains(new DefaultMapEntry<>("B", "2"), _listener.beforeRemoveEvents);
		assertEquals(3, _listener.afterRemoveEvents.size());
		TestHelper.eventListContains(new DefaultMapEntry<>("A", "1"), _listener.afterRemoveEvents);
		TestHelper.eventListContains(new DefaultMapEntry<>("_", "1"), _listener.afterRemoveEvents);
		TestHelper.eventListContains(new DefaultMapEntry<>("B", "2"), _listener.afterRemoveEvents);
	}

	/**
	 * Test of removeAll method, of class AspectMapValueCollection.
	 */
	@Test
	public void testRemoveAll_veto()
	{
		_listener.vetoRemove = true;
		assertFalse(_values.removeAll(Arrays.asList("5", "6")));
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());
		try {
			assertTrue(_values.removeAll(Arrays.asList("1", "2")));
			fail("Remove veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());
		assertTrue(_values.contains("1"));
		assertTrue(_values.contains("2"));
		assertTrue(_aspectMap.containsKey("A"));
		assertTrue(_aspectMap.containsKey("B"));
	}

	/**
	 * Test of retainAll method, of class AspectMapValueCollection.
	 */
	@Test
	public void testRetainAll()
	{
		assertFalse(_values.retainAll(Arrays.asList("1", "2", "3", "4")));
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());
		_baseMap.put("_", "1");
		assertTrue(_values.retainAll(Arrays.asList("2", "3", "4")));
		assertFalse(_values.contains("1"));
		assertTrue(_values.contains("2"));
		assertTrue(_values.contains("3"));
		assertTrue(_values.contains("4"));
		assertEquals(2, _listener.beforeRemoveEvents.size());
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("A", "1"),
												_listener.beforeRemoveEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("_", "1"),
												_listener.beforeRemoveEvents));
		assertEquals(2, _listener.afterRemoveEvents.size());
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("A", "1"),
												_listener.afterRemoveEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("_", "1"),
												_listener.afterRemoveEvents));
	}

	/**
	 * Test of retainAll method, of class AspectMapValueCollection.
	 */
	@Test
	public void testRetainAll_veto()
	{
		_listener.vetoRemove = true;
		assertFalse(_values.retainAll(Arrays.asList("1", "2", "3", "4")));
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());
		_baseMap.put("_", "1");
		try {
			assertTrue(_values.retainAll(Arrays.asList("2", "3", "4")));
			fail("Remove veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_values.contains("1"));
		assertTrue(_values.contains("2"));
		assertTrue(_values.contains("3"));
		assertTrue(_values.contains("4"));
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());
	}

	/**
	 * Test of clear method, of class AspectMapValueCollection.
	 */
	@Test
	public void testClear()
	{
		assertFalse(_aspectMap.isEmpty());
		assertFalse(_values.isEmpty());
		_values.clear();
		assertTrue(_aspectMap.isEmpty());
		assertTrue(_values.isEmpty());
		assertEquals(1, _listener.beforeClearEvents.size());
		assertEquals(1, _listener.afterClearEvents.size());

		/* second clear does nothing */
		_values.clear();
		assertEquals(1, _listener.beforeClearEvents.size());
		assertEquals(1, _listener.afterClearEvents.size());

	}

	/**
	 * Test of clear method, of class AspectMapValueCollection.
	 */
	@Test
	public void testClear_veto()
	{
		_listener.vetoClear = true;
		assertFalse(_aspectMap.isEmpty());
		assertFalse(_values.isEmpty());
		try {
			_values.clear();
			fail("Clear veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertFalse(_aspectMap.isEmpty());
		assertFalse(_values.isEmpty());

		_baseMap.clear();
		/* second clear does nothing */
		_values.clear();
		assertTrue(_listener.beforeClearEvents.isEmpty());
		assertTrue(_listener.afterClearEvents.isEmpty());
	}

}
