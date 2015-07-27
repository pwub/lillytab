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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.collections15.MultiMap;
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
public class AspectMultiMapTest
{
	private MultiMap<String, String> _baseMap;
	private AspectMultiMap<String, String, MultiMap<String, String>> _aspectMap;
	private AspectCollectionHistoryListener<Map.Entry<String, String>, MultiMap<String, String>> _listener;

	public AspectMultiMapTest()
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
	}

	@After
	public void tearDown()
	{
		_listener = null;
		_aspectMap = null;
		_baseMap = null;
	}

	/**
	 * Test of remove method, of class AspectMultiMap.
	 */
	@Test
	public void testRemove_Object_Object()
	{
		assertNull(_aspectMap.remove("1", "B"));
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());

		assertEquals("B", _aspectMap.remove("2", "B"));
		assertFalse(_aspectMap.containsValue("2", "B"));
		assertTrue(_aspectMap.containsValue("2", "b"));
		assertEquals(1, _listener.beforeRemoveEvents.size());
		assertEquals(new DefaultMapEntry<>("2", "B"), _listener.beforeRemoveEvents.get(0).getItem());
		assertEquals(1, _listener.afterRemoveEvents.size());
		assertEquals(new DefaultMapEntry<>("2", "B"), _listener.afterRemoveEvents.get(0).getItem());
	}

	/**
	 * Test of remove method, of class AspectMultiMap.
	 */
	@Test
	public void testRemove_Object_Object_veto()
	{
		_listener.vetoRemove = true;
		assertNull(_aspectMap.remove("1", "B"));
		try {
			assertEquals("B", _aspectMap.remove("2", "B"));
			fail("Remove veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}

		assertTrue(_aspectMap.containsValue("2", "B"));
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());
	}

	/**
	 * Test of size method, of class AspectMultiMap.
	 */
	@Test
	public void testSize_Object()
	{
		assertEquals(2, _aspectMap.size("1"));
		assertEquals(2, _aspectMap.size("2"));
		assertEquals(2, _aspectMap.size("3"));
		assertNotNull(_aspectMap.remove("1", "A"));
		assertEquals(1, _aspectMap.size("1"));
	}

	/**
	 * Test of size method, of class AspectMultiMap.
	 */
	@Test
	public void testSize_0args()
	{
		assertEquals(3, _aspectMap.size());
		TestHelper.assertSequenceEquals(Arrays.asList("a", "A"), _aspectMap.remove("1"));
		assertEquals(2, _aspectMap.size());
	}

	/**
	 * Test of get method, of class AspectMultiMap.
	 */
	@Test
	public void testGet()
	{
		TestHelper.assertSequenceEquals(Arrays.asList("a", "A"), _aspectMap.get("1"));
		assertEquals("b", _aspectMap.remove("2", "b"));
		TestHelper.assertSequenceEquals(Arrays.asList("B"), _aspectMap.get("2"));
		Collection<String> values = _aspectMap.get("3");
		assertTrue(values.remove("c"));
		assertFalse(_aspectMap.containsValue("3", "c"));
	}

	/**
	 * Test of get method, of class AspectMultiMap.
	 */
	@Test
	public void testGet_remove()
	{
		Collection<String> values = _aspectMap.get("1");
		TestHelper.assertSequenceEquals(Arrays.asList("a", "A"), values);
		values.remove("a");
		assertFalse(_aspectMap.containsValue("1", "a"));
		assertEquals(1, _listener.beforeRemoveEvents.size());
		assertEquals(new DefaultMapEntry<>("1", "a"), _listener.beforeRemoveEvents.get(0).getItem());
		assertEquals(1, _listener.afterRemoveEvents.size());
		assertEquals(new DefaultMapEntry<>("1", "a"), _listener.afterRemoveEvents.get(0).getItem());
	}

		/**
	 * Test of get method, of class AspectMultiMap.
	 */
	@Test
	public void testGet_remove_veto()
	{
		_listener.vetoRemove = true;
		Collection<String> values = _aspectMap.get("1");
		TestHelper.assertSequenceEquals(Arrays.asList("a", "A"), values);
		try {
			values.remove("a");
			fail("Remove veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_aspectMap.containsValue("1", "a"));
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());
	}



	/**
	 * Test of containsValue method, of class AspectMultiMap.
	 */
	@Test
	public void testContainsValue_Object()
	{
		assertTrue(_aspectMap.containsValue("a"));
		assertTrue(_aspectMap.containsValue("B"));
		assertTrue(_aspectMap.containsValue("b"));
		assertTrue(_aspectMap.containsValue("B"));
		assertTrue(_aspectMap.containsValue("c"));
		assertTrue(_aspectMap.containsValue("C"));
		assertFalse(_aspectMap.containsValue("d"));
		assertFalse(_aspectMap.containsValue("D"));
	}

	/**
	 * Test of containsValue method, of class AspectMultiMap.
	 */
	@Test
	public void testContainsValue_Object_Object()
	{
		assertTrue(_aspectMap.containsValue("1", "a"));
		assertTrue(_aspectMap.containsValue("1", "A"));
		assertTrue(_aspectMap.containsValue("2", "b"));
		assertTrue(_aspectMap.containsValue("2", "B"));
		assertTrue(_aspectMap.containsValue("3", "c"));
		assertTrue(_aspectMap.containsValue("3", "C"));
		assertFalse(_aspectMap.containsValue("1", "b"));
		assertFalse(_aspectMap.containsValue("2", "A"));
	}

	/**
	 * Test of put method, of class AspectMultiMap.
	 */
	@Test
	public void testPut()
	{
		assertEquals("_", _aspectMap.put("1", "_"));
		TestHelper.assertSequenceEquals(Arrays.asList("a", "A", "_"), _aspectMap.get("1"));

		assertEquals(1, _listener.beforeAddEvents.size());
		assertEquals(new DefaultMapEntry<>("1", "_"), _listener.beforeAddEvents.get(0).getItem());
		assertEquals(1, _listener.afterAddEvents.size());
		assertEquals(new DefaultMapEntry<>("1", "_"), _listener.afterAddEvents.get(0).getItem());
	}

		/**
	 * Test of put method, of class AspectMultiMap.
	 */
	@Test
	public void testPut_veto()
	{
		_listener.vetoAdd = true;
		try {
			assertEquals("_", _aspectMap.put("1", "_"));
			fail("Add veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertFalse(_aspectMap.containsValue("1", "_"));

		assertTrue(_listener.beforeAddEvents.isEmpty());
		assertTrue(_listener.afterAddEvents.isEmpty());
	}

	/**
	 * Test of remove method, of class AspectMultiMap.
	 */
	@Test
	public void testRemove_Object()
	{
		assertNull(_aspectMap.remove("0"));
		TestHelper.assertSequenceEquals(Arrays.asList("a", "A"), _aspectMap.remove("1"));
		assertEquals(2, _listener.beforeRemoveEvents.size());
		assertEquals(new DefaultMapEntry<>("1", "a"), _listener.beforeRemoveEvents.get(0).getItem());
		assertEquals(new DefaultMapEntry<>("1", "A"), _listener.beforeRemoveEvents.get(1).getItem());
	}

	/**
	 * Test of remove method, of class AspectMultiMap.
	 */
	@Test
	public void testRemove_Object_veto()
	{
		_listener.vetoRemove = true;
		assertNull(_aspectMap.remove("0"));
		try {
			_aspectMap.remove("1");
			fail("Remove veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
	}

	/**
	 * Test of isEmpty method, of class AspectMultiMap.
	 */
	@Test
	public void testIsEmpty()
	{
		assertFalse(_aspectMap.isEmpty());
		_baseMap.clear();
		assertTrue(_aspectMap.isEmpty());
	}

	/**
	 * Test of containsKey method, of class AspectMultiMap.
	 */
	@Test
	public void testContainsKey()
	{
		assertTrue(_aspectMap.containsKey("1"));
		assertTrue(_aspectMap.containsKey("2"));
		assertTrue(_aspectMap.containsKey("3"));
		assertFalse(_aspectMap.containsKey("4"));
	}

	/**
	 * Test of putAll method, of class AspectMultiMap.
	 */
	@Test
	public void testPutAll_Map()
	{
		/* use tree map so item checks below works. HashMap reorders entries */
		Map<String, String> addMap = new TreeMap<>();
		addMap.put("1", "α");
		addMap.put("2", "β");
		_aspectMap.putAll(addMap);
		TestHelper.assertSequenceEquals(Arrays.asList("a", "A", "α"), _aspectMap.get("1"));
		TestHelper.assertSequenceEquals(Arrays.asList("b", "B", "β"), _aspectMap.get("2"));
		assertEquals(2, _listener.beforeAddEvents.size());
		assertEquals(new DefaultMapEntry<>("1", "α"), _listener.beforeAddEvents.get(0).getItem());
		assertEquals(new DefaultMapEntry<>("2", "β"), _listener.beforeAddEvents.get(1).getItem());
		assertEquals(2, _listener.afterAddEvents.size());
		assertEquals(new DefaultMapEntry<>("1", "α"), _listener.afterAddEvents.get(0).getItem());
		assertEquals(new DefaultMapEntry<>("2", "β"), _listener.afterAddEvents.get(1).getItem());
	}

	@Test
	public void testPutAll_Map_veto()
	{
		Map<String, String> addMap = new HashMap<>();
		addMap.put("1", "α");
		addMap.put("2", "β");
		_listener.vetoAdd = true;
		try {
			_aspectMap.putAll(addMap);
			fail("Add veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_listener.beforeAddEvents.isEmpty());
		assertTrue(_listener.afterAddEvents.isEmpty());
	}

	/**
	 * Test of putAll method, of class AspectMultiMap.
	 */
	@Test
	public void testPutAll_MultiMap()
	{
		MultiMap<String, String> addMap = new MultiHashMap<>();
		addMap.put("1", "α");
		addMap.put("1", "alpha");
		addMap.put("2", "β");
		addMap.put("2", "beta");
		_aspectMap.putAll(addMap);
		TestHelper.assertSequenceEquals(Arrays.asList("a", "A", "α", "alpha"), _aspectMap.get("1"));
		TestHelper.assertSequenceEquals(Arrays.asList("b", "B", "β", "beta"), _aspectMap.get("2"));
		assertEquals(4, _listener.beforeAddEvents.size());
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("1", "α"), _listener.beforeAddEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("1", "alpha"), _listener.beforeAddEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("2", "β"), _listener.beforeAddEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("2", "beta"), _listener.beforeAddEvents));
		assertEquals(4, _listener.afterAddEvents.size());
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("1", "α"), _listener.afterAddEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("1", "alpha"), _listener.afterAddEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("2", "β"), _listener.afterAddEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("2", "beta"), _listener.afterAddEvents));
	}

	@Test
	public void testPutAll_MultiMap_veto()
	{
		MultiMap<String, String> addMap = new MultiHashMap<>();
		addMap.put("1", "α");
		addMap.put("1", "alpha");
		addMap.put("2", "β");
		addMap.put("2", "beta");
		_listener.vetoAdd = true;
		try {
			_aspectMap.putAll(addMap);
			fail("Add veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_listener.beforeAddEvents.isEmpty());
		assertTrue(_listener.afterAddEvents.isEmpty());
	}

	/**
	 * Test of putAll method, of class AspectMultiMap.
	 */
	@Test
	public void testPutAll_GenericType_Collection()
	{
		_aspectMap.putAll("2", Collections.<String>emptyList());
		assertTrue(_listener.beforeAddEvents.isEmpty());
		assertTrue(_listener.afterAddEvents.isEmpty());

		Collection<String> values = Arrays.asList("β", "beta");
		_aspectMap.putAll("2", values);
		TestHelper.assertSequenceEquals(Arrays.asList("b", "B", "β", "beta"), _aspectMap.get("2"));
		assertEquals(2, _listener.beforeAddEvents.size());
		assertEquals(new DefaultMapEntry<>("2", "β"), _listener.beforeAddEvents.get(0).getItem());
		assertEquals(new DefaultMapEntry<>("2", "beta"), _listener.beforeAddEvents.get(1).getItem());
		assertEquals(2, _listener.afterAddEvents.size());
		assertEquals(new DefaultMapEntry<>("2", "β"), _listener.afterAddEvents.get(0).getItem());
		assertEquals(new DefaultMapEntry<>("2", "beta"), _listener.afterAddEvents.get(1).getItem());
	}

		/**
	 * Test of putAll method, of class AspectMultiMap.
	 */
	@Test
	public void testPutAll_GenericType_Collection_veto()
	{
		_listener.vetoAdd = true;

		_aspectMap.putAll("2", Collections.<String>emptyList());
		assertTrue(_listener.beforeAddEvents.isEmpty());
		assertTrue(_listener.afterAddEvents.isEmpty());

		Collection<String> values = Arrays.asList("β", "beta");
		try {
			_aspectMap.putAll("2", values);
			fail("Add veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_listener.beforeAddEvents.isEmpty());
		assertTrue(_listener.afterAddEvents.isEmpty());
	}

	/**
	 * Test of iterator method, of class AspectMultiMap.
	 */
	@Test
	public void testIterator()
	{
		Iterator<String> valueIterator = _aspectMap.iterator("1");
		assertTrue(valueIterator.hasNext());
		assertEquals("a", valueIterator.next());
		assertTrue(valueIterator.hasNext());
		assertEquals("A", valueIterator.next());
		assertFalse(valueIterator.hasNext());

		valueIterator = _aspectMap.iterator("2");
		assertTrue(valueIterator.hasNext());
		assertEquals("b", valueIterator.next());
		assertTrue(valueIterator.hasNext());
		assertEquals("B", valueIterator.next());
		assertFalse(valueIterator.hasNext());
	
	}

	@Test
	public void testIterator_remove()
	{
		Iterator<String> valueIterator = _aspectMap.iterator("1");
		assertTrue(valueIterator.hasNext());
		assertEquals("a", valueIterator.next());
		assertTrue(valueIterator.hasNext());
		valueIterator.remove();
		assertTrue(valueIterator.hasNext());
		assertEquals("A", valueIterator.next());
		assertFalse(valueIterator.hasNext());
		assertFalse(_aspectMap.containsValue("1", "a"));

		assertEquals(1, _listener.beforeRemoveEvents.size());
		assertEquals(new DefaultMapEntry<>("1", "a"), _listener.beforeRemoveEvents.get(0).getItem());
		assertEquals(1, _listener.afterRemoveEvents.size());
		assertEquals(new DefaultMapEntry<>("1", "a"), _listener.afterRemoveEvents.get(0).getItem());
	}

	@Test
	public void testIterator_remove_veto()
	{
		_listener.vetoRemove = true;
		Iterator<String> valueIterator = _aspectMap.iterator("1");
		assertTrue(valueIterator.hasNext());
		assertEquals("a", valueIterator.next());
		assertTrue(valueIterator.hasNext());
		try {
			valueIterator.remove();
			fail("Removed veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(valueIterator.hasNext());
		assertEquals("A", valueIterator.next());
		assertFalse(valueIterator.hasNext());
		assertTrue(_aspectMap.containsValue("1", "a"));

		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());
	}

	/**
	 * Test of clear method, of class AspectMultiMap.
	 */
	@Test
	public void testClear()
	{
		assertFalse(_aspectMap.isEmpty());
		assertFalse(_baseMap.isEmpty());
		_aspectMap.clear();
		assertTrue(_aspectMap.isEmpty());
		assertTrue(_baseMap.isEmpty());
		assertEquals(1, _listener.beforeClearEvents.size());
		assertEquals(1, _listener.afterClearEvents.size());

		/* second clear does nothing */
		_aspectMap.clear();
		assertEquals(1, _listener.beforeClearEvents.size());
		assertEquals(1, _listener.afterClearEvents.size());
	}

	/**
	 * Test of clear method, of class AspectMultiMap.
	 */
	@Test
	public void testClear_veto()
	{
		_listener.vetoClear = true;
		assertFalse(_aspectMap.isEmpty());
		assertFalse(_baseMap.isEmpty());
		try {
			_aspectMap.clear();
			fail("Clear veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertFalse(_aspectMap.isEmpty());
		assertFalse(_baseMap.isEmpty());

		_baseMap.clear();
		/* clear on empty map does nothing */
		_aspectMap.clear();

		assertTrue(_listener.beforeClearEvents.isEmpty());
		assertTrue(_listener.afterClearEvents.isEmpty());
	}

	/**
	 * Test of map method, of class AspectMultiMap.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testMap()
	{
		_aspectMap.map();
	}

	/**
	 * Test of getDecoratee method, of class AspectMultiMap.
	 */
	@Test
	public void testGetDecoratee()
	{
		assertSame(_baseMap, _aspectMap.getDecoratee());
	}
}
