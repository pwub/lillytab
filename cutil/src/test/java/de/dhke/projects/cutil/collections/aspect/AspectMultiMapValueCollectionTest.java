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
import org.apache.commons.collections15.Bag;
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.bag.HashBag;
import org.apache.commons.collections15.keyvalue.DefaultMapEntry;
import org.apache.commons.collections15.multimap.MultiHashMap;
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
public class AspectMultiMapValueCollectionTest
{
	private MultiMap<String, String> _baseMap;
	private AspectMultiMap<String, String, MultiMap<String, String>> _aspectMap;
	private AspectCollectionHistoryListener<Map.Entry<String, String>, MultiMap<String, String>> _listener;
	private Collection<String> _values;

	public AspectMultiMapValueCollectionTest()
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
		_baseMap = new MultiHashMap<>();
		_baseMap.put("1", "a");
		_baseMap.put("1", "A");
		_baseMap.put("2", "b");
		_baseMap.put("2", "B");
		_baseMap.put("3", "c");
		_baseMap.put("3", "C");
		_aspectMap = AspectMultiMap.decorate(_baseMap, this);
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
	 * Test of size method, of class AspectMultiMapValueCollection.
	 */
	@Test
	public void testSize()
	{
		assertEquals(6, _values.size());
		_aspectMap.remove("1");
		assertEquals(4, _values.size());
	}

	/**
	 * Test of isEmpty method, of class AspectMultiMapValueCollection.
	 */
	@Test
	public void testIsEmpty()
	{
		assertFalse(_values.isEmpty());
		_aspectMap.clear();
		assertTrue(_values.isEmpty());
	}

	/**
	 * Test of contains method, of class AspectMultiMapValueCollection.
	 */
	@Test
	public void testContains()
	{
		assertTrue(_values.contains("a"));
		assertTrue(_values.contains("A"));
		assertTrue(_values.contains("b"));
		assertTrue(_values.contains("B"));
		assertTrue(_values.contains("c"));
		assertTrue(_values.contains("C"));
		assertFalse(_values.contains("d"));
		assertFalse(_values.contains("D"));
		_aspectMap.put("_", "a");
		_aspectMap.remove("1");
		assertTrue(_values.contains("a"));
		assertTrue(_values.remove("a"));
		assertFalse(_values.contains("a"));
		assertFalse(_aspectMap.containsKey("1"));
	}

	/**
	 * Test of iterator method, of class AspectMultiMapValueCollection.
	 */
	@Test
	public void testIterator()
	{
		_aspectMap.put("_", "a");
		Bag<String> values = new HashBag<>();
		Iterator<String> iter = _values.iterator();
		while (iter.hasNext())
			values.add(iter.next());
		assertEquals(2, values.getCount("a"));
		assertEquals(1, values.getCount("A"));
		assertEquals(1, values.getCount("b"));
		assertEquals(1, values.getCount("B"));
		assertEquals(1, values.getCount("c"));
		assertEquals(1, values.getCount("C"));
		assertEquals(0, values.getCount("d"));
		assertEquals(0, values.getCount("D"));
		assertEquals(_values.size(), values.size());
	}

	/**
	 * Test of iterator method, of class AspectMultiMapValueCollection.
	 */
	@Test
	public void testIterator_remove()
	{
		Iterator<String> iter = _values.iterator();
		/* this empty while loop is intentional */
		while (!iter.next().endsWith("b"));
		iter.remove();
		assertTrue(_aspectMap.containsValue("2", "B"));
		assertFalse(_aspectMap.containsValue("2", "b"));

		assertEquals(1, _listener.beforeRemoveEvents.size());
		assertEquals(new DefaultMapEntry<>("2", "b"), _listener.beforeRemoveEvents.get(0).getItem());
		assertEquals(1, _listener.afterRemoveEvents.size());
		assertEquals(new DefaultMapEntry<>("2", "b"), _listener.afterRemoveEvents.get(0).getItem());
	}

	/**
	 * Test of iterator method, of class AspectMultiMapValueCollection.
	 */
	@Test
	public void testIterator_remove_veto()
	{
		_listener.vetoRemove = true;
		Iterator<String> iter = _values.iterator();
		iter.next();
		try {
			iter.remove();
			fail("Remove veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());

		assertTrue(_values.contains("a"));
		assertTrue(_values.contains("A"));
		assertTrue(_values.contains("b"));
		assertTrue(_values.contains("B"));
		assertTrue(_values.contains("c"));
		assertTrue(_values.contains("C"));
		assertFalse(_values.contains("d"));
		assertFalse(_values.contains("D"));
	}

	/**
	 * Test of toArray method, of class AspectMultiMapValueCollection.
	 */
	@Test
	public void testToArray_0args()
	{
		assertArrayEquals(_baseMap.values().toArray(), _values.toArray());
	}

	/**
	 * Test of toArray method, of class AspectMultiMapValueCollection.
	 */
	@Test
	public void testToArray_GenericType()
	{
		assertArrayEquals(_baseMap.values().toArray(new String[0]), _values.toArray(new String[_values.size()]));
	}

	/**
	 * Test of add method, of class AspectMultiMapValueCollection.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testAdd()
	{
		_values.add("_");
	}

	/**
	 * Test of remove method, of class AspectMultiMapValueCollection.
	 */
	@Test
	public void testRemove()
	{
		assertFalse(_values.remove("_"));
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());
		_baseMap.put("_", "a");
		assertTrue(_values.remove("a"));
		/* second item remains */
		assertTrue(_values.contains("a"));
		assertEquals(1, _listener.beforeRemoveEvents.size());
		assertEquals(1, _listener.afterRemoveEvents.size());
		if (_aspectMap.containsValue("_", "a")) {
			assertEquals(new DefaultMapEntry<>("1", "a"), _listener.beforeRemoveEvents.get(0).getItem());
			assertEquals(new DefaultMapEntry<>("1", "a"), _listener.afterRemoveEvents.get(0).getItem());
		} else {
			assertEquals(new DefaultMapEntry<>("_", "a"), _listener.beforeRemoveEvents.get(0).getItem());
			assertEquals(new DefaultMapEntry<>("_", "a"), _listener.afterRemoveEvents.get(0).getItem());
		}
	}

	@Test
	public void testRemove_veto()
	{
		_listener.vetoRemove = true;
		assertFalse(_values.remove("_"));
		try {
			_values.remove("a");
			fail("Remove veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_values.contains("a"));
		assertTrue(_aspectMap.containsValue("1", "a"));
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());
	}

	/**
	 * Test of containsAll method, of class AspectMultiMapValueCollection.
	 */
	@Test
	public void testContainsAll()
	{
		assertTrue(_values.containsAll(Arrays.asList("a")));
		assertFalse(_values.containsAll(Arrays.asList("e")));
		assertFalse(_values.containsAll(Arrays.asList("a", "A", "d", "D", "e", "E")));
		assertFalse(_values.containsAll(Arrays.asList("a", "A", "d", "D", "e")));
		assertTrue(_values.containsAll(Arrays.asList("a", "A", "C", "c")));
		assertTrue(_values.containsAll(Arrays.asList("a", "A", "b", "C", "c")));
	}

	/**
	 * Test of addAll method, of class AspectMultiMapValueCollection.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testAddAll()
	{
		_values.addAll(Arrays.asList("e"));
	}

	/**
	 * Test of removeAll method, of class AspectMultiMapValueCollection.
	 */
	@Test
	public void testRemoveAll()
	{
		_baseMap.put("_", "a");
		_baseMap.put("_", "A");

		assertFalse(_values.removeAll(Arrays.asList("d", "D")));
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());

		assertTrue(_values.removeAll(Arrays.asList("a", "A")));
		assertFalse(_values.contains("a"));
		assertFalse(_values.contains("A"));
		assertFalse(_aspectMap.containsValue("_", "a"));
		assertFalse(_aspectMap.containsValue("1", "a"));
		assertFalse(_aspectMap.containsValue("_", "A"));
		assertFalse(_aspectMap.containsValue("1", "A"));
		assertEquals(4, _listener.beforeRemoveEvents.size());
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("1", "a"),
												_listener.beforeRemoveEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("_", "a"),
												_listener.beforeRemoveEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("1", "A"),
												_listener.beforeRemoveEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("_", "A"),
												_listener.beforeRemoveEvents));
		assertEquals(4, _listener.afterRemoveEvents.size());
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("1", "a"),
												_listener.afterRemoveEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("_", "a"),
												_listener.afterRemoveEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("1", "A"),
												_listener.afterRemoveEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("_", "A"),
												_listener.afterRemoveEvents));
	}

	@Test
	public void testRemoveAll_veto()
	{
		_listener.vetoRemove = true;
		assertFalse(_values.removeAll(Arrays.asList("d", "D")));
		try {
			_values.removeAll(Arrays.asList("a", "A"));
			fail("Remove veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_values.contains("a"));
		assertTrue(_values.contains("A"));
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());
	}

	/**
	 * Test of retainAll method, of class AspectMultiMapValueCollection.
	 */
	@Test
	public void testRetainAll()
	{
		_baseMap.put("_", "a");
		_baseMap.put("_", "A");

		assertFalse(_values.retainAll(Arrays.asList("a", "A", "b", "B", "c", "C")));
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());

		assertTrue(_values.retainAll(Arrays.asList("b", "B", "c", "C")));
		assertFalse(_values.contains("a"));
		assertFalse(_values.contains("A"));
		assertFalse(_aspectMap.containsValue("_", "a"));
		assertFalse(_aspectMap.containsValue("1", "a"));
		assertFalse(_aspectMap.containsValue("_", "A"));
		assertFalse(_aspectMap.containsValue("1", "A"));
		assertEquals(4, _listener.beforeRemoveEvents.size());
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("1", "a"),
												_listener.beforeRemoveEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("_", "a"),
												_listener.beforeRemoveEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("1", "A"),
												_listener.beforeRemoveEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("_", "A"),
												_listener.beforeRemoveEvents));
		assertEquals(4, _listener.afterRemoveEvents.size());
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("1", "a"),
												_listener.afterRemoveEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("_", "a"),
												_listener.afterRemoveEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("1", "A"),
												_listener.afterRemoveEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("_", "A"),
												_listener.afterRemoveEvents));
	}

	@Test
	public void testRetainAll_veto()
	{
		_listener.vetoRemove = true;
		assertFalse(_values.retainAll(Arrays.asList("a", "A", "b", "B", "c", "C")));
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());
		try {
			_values.retainAll(Arrays.asList("b", "B", "c", "C"));
			fail("Remove veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());

		assertTrue(_values.contains("a"));
		assertTrue(_values.contains("A"));
		assertTrue(_aspectMap.containsValue("1", "a"));
		assertTrue(_aspectMap.containsValue("1", "A"));
	}

	/**
	 * Test of clear method, of class AspectMultiMapValueCollection.
	 */
	@Test
	public void testClear()
	{
		_values.clear();

		assertTrue(_aspectMap.isEmpty());
		assertTrue(_values.isEmpty());

		assertEquals(1, _listener.beforeClearEvents.size());
		assertEquals(1, _listener.afterClearEvents.size());

		/* clear on empty set does nothing */
		_values.clear();
		assertEquals(1, _listener.beforeClearEvents.size());
		assertEquals(1, _listener.afterClearEvents.size());
	}


	@Test
	public void testClear_veto()
	{
		_listener.vetoClear = true;
		try {
			_values.clear();
			fail("Clear veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}

		assertFalse(_aspectMap.isEmpty());
		assertFalse(_values.isEmpty());

		_baseMap.clear();
		_values.clear();;

		assertTrue(_listener.beforeClearEvents.isEmpty());
		assertTrue(_listener.afterClearEvents.isEmpty());
	}

	/**
	 * Test of equals method, of class AspectMultiMapValueCollection.
	 */
	@Test
	public void testEquals()
	{
		assertTrue(_values.equals(_baseMap.values()));
	}
}
