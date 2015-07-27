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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
public class AspectMultiMapKeySetTest
{
	private MultiMap<String, String> _baseMap;
	private AspectMultiMap<String, String, MultiMap<String, String>> _aspectMap;
	private AspectCollectionHistoryListener<Map.Entry<String, String>, MultiMap<String, String>> _listener;
	private Set<String> _keySet;

	public AspectMultiMapKeySetTest()
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
		_aspectMap = AspectMultiMap.decorate(_baseMap,  this);
		_listener = new AspectCollectionHistoryListener<>();
		_aspectMap.getListeners().add(_listener);
		_keySet = _aspectMap.keySet();
	}

	@After
	public void tearDown()
	{
		_keySet = null;
		_listener = null;
		_aspectMap = null;
		_baseMap = null;
	}

	/**
	 * Test of size method, of class AspectMultiMapKeySet.
	 */
	@Test
	public void testSize()
	{
		assertEquals(3, _keySet.size());
		_baseMap.remove("1");
		assertEquals(2, _keySet.size());
	}

	/**
	 * Test of isEmpty method, of class AspectMultiMapKeySet.
	 */
	@Test
	public void testIsEmpty()
	{
		assertFalse(_keySet.isEmpty());
		_baseMap.clear();
		assertTrue(_keySet.isEmpty());
	}

	/**
	 * Test of contains method, of class AspectMultiMapKeySet.
	 */
	@Test
	public void testContains()
	{
		assertTrue(_keySet.contains("1"));
		assertTrue(_keySet.contains("2"));
		assertTrue(_keySet.contains("3"));
		assertFalse(_keySet.contains("_"));
	}

	/**
	 * Test of iterator method, of class AspectMultiMapKeySet.
	 */
	@Test
	public void testIterator()
	{
		List<String> keys = new ArrayList<>();
		keys.add("1");
		keys.add("2");
		keys.add("3");
		Iterator<String> iter = _keySet.iterator();
		while (iter.hasNext())
			assertTrue(keys.remove(iter.next()));
		assertFalse(iter.hasNext());
		assertTrue(keys.isEmpty());
	}

	/**
	 * Test of iterator method, of class AspectMultiMapKeySet.
	 */
	@Test
	public void testIterator_remove()
	{
		Iterator<String> iter = _keySet.iterator();
		iter.next();
		String key = iter.next();
		/* we need to create a copy, as the removal drops the values from the returned collection */
		Collection<String> values = new ArrayList<>(_aspectMap.get(key));
		iter.remove();

		assertFalse(_aspectMap.containsKey(key));

		assertEquals(2, _listener.beforeRemoveEvents.size());
		for (String value : values)
			assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>(key, value),
													_listener.beforeRemoveEvents));
		assertEquals(2, _listener.afterRemoveEvents.size());
		for (String value : values)
			assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>(key, value),
													_listener.afterRemoveEvents));
	}

	/**
	 * Test of iterator method, of class AspectMultiMapKeySet.
	 */
	@Test
	public void testIterator_remove_veto()
	{
		_listener.vetoRemove = true;
		Iterator<String> iter = _keySet.iterator();
		iter.next();
		String key = iter.next();
		Collection<String> values = new ArrayList<>(_aspectMap.get(key));
		try {
			iter.remove();
			fail("Remove veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_aspectMap.containsKey(key));
		TestHelper.assertSequenceEquals(_aspectMap.get(key), values);
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());
	}

	/**
	 * Test of toArray method, of class AspectMultiMapKeySet.
	 */
	@Test
	public void testToArray_0args()
	{
		assertArrayEquals(_baseMap.keySet().toArray(), _keySet.toArray());
	}

	/**
	 * Test of toArray method, of class AspectMultiMapKeySet.
	 */
	@Test
	public void testToArray_GenericType()
	{
		assertArrayEquals(_baseMap.keySet().toArray(new String[0]), _keySet.toArray(new String[_keySet.size()]));
	}

	/**
	 * Test of add method, of class AspectMultiMapKeySet.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testAdd()
	{
		_keySet.add("_");
	}

	/**
	 * Test of remove method, of class AspectMultiMapKeySet.
	 */
	@Test
	public void testRemove()
	{
		assertFalse(_keySet.remove("_"));
		assertTrue(_keySet.remove("1"));
		assertFalse(_aspectMap.containsKey("1"));

		assertEquals(2, _listener.beforeRemoveEvents.size());
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("1", "a"),
												_listener.beforeRemoveEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("1", "A"),
												_listener.beforeRemoveEvents));
		assertEquals(2, _listener.afterRemoveEvents.size());
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("1", "a"),
												_listener.afterRemoveEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("1", "A"),
												_listener.afterRemoveEvents));
	}

	/**
	 * Test of remove method, of class AspectMultiMapKeySet.
	 */
	@Test
	public void testRemove_veto()
	{
		_listener.vetoRemove = true;
		assertFalse(_keySet.remove("_"));
		try {
			_keySet.remove("1");
			fail("Remove veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_aspectMap.containsKey("1"));

		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());
	}

	/**
	 * Test of containsAll method, of class AspectMultiMapKeySet.
	 */
	@Test
	public void testContainsAll()
	{
		assertTrue(_keySet.containsAll(Arrays.asList("1", "2", "3")));
		assertTrue(_keySet.containsAll(Arrays.asList("1", "3")));
		assertTrue(_keySet.containsAll(Arrays.asList("2")));
		assertFalse(_keySet.containsAll(Arrays.asList("1", "2", "3", "4")));
		assertFalse(_keySet.containsAll(Arrays.asList("4", "5", "6")));
	}

	/**
	 * Test of addAll method, of class AspectMultiMapKeySet.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testAddAll()
	{
		_keySet.addAll(Arrays.asList(":", "_"));
	}

	/**
	 * Test of retainAll method, of class AspectMultiMapKeySet.
	 */
	@Test
	public void testRetainAll()
	{
		assertFalse(_keySet.retainAll(Arrays.asList("1", "2", "3")));
		assertTrue(_keySet.retainAll(Arrays.asList("1", "3")));
		assertFalse(_aspectMap.containsKey("2"));
		assertEquals(2, _listener.beforeRemoveEvents.size());
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("2", "b"),
												_listener.beforeRemoveEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("2", "B"),
												_listener.beforeRemoveEvents));
		assertEquals(2, _listener.afterRemoveEvents.size());
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("2", "b"),
												_listener.afterRemoveEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("2", "B"),
												_listener.afterRemoveEvents));
	}

	/**
	 * Test of retainAll method, of class AspectMultiMapKeySet.
	 */
	@Test
	public void testRetainAll_veto()
	{
		_listener.vetoRemove = true;
		assertFalse(_keySet.retainAll(Arrays.asList("1", "2", "3")));
		try {
			_keySet.retainAll(Arrays.asList("1", "3"));
			fail("Remove veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_aspectMap.containsKey("2"));
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());
	}

	/**
	 * Test of removeAll method, of class AspectMultiMapKeySet.
	 */
	@Test
	public void testRemoveAll()
	{
		assertFalse(_keySet.removeAll(Arrays.asList("4", "5")));
		assertTrue(_keySet.removeAll(Arrays.asList("2", "4")));
		assertFalse(_aspectMap.containsKey("2"));
		assertEquals(2, _listener.beforeRemoveEvents.size());
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("2", "b"),
												_listener.beforeRemoveEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("2", "B"),
												_listener.beforeRemoveEvents));
		assertEquals(2, _listener.afterRemoveEvents.size());
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("2", "b"),
												_listener.afterRemoveEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("2", "B"),
												_listener.afterRemoveEvents));
	}

	/**
	 * Test of removeAll method, of class AspectMultiMapKeySet.
	 */
	@Test
	public void testRemoveAll_veto()
	{
		_listener.vetoRemove = true;
		assertFalse(_keySet.removeAll(Arrays.asList("4", "5")));
		try {
			_keySet.removeAll(Arrays.asList("2", "4"));
			fail("Remove veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_aspectMap.containsValue("2", "b"));
		assertTrue(_aspectMap.containsValue("2", "B"));
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());
	}

	/**
	 * Test of clear method, of class AspectMultiMapKeySet.
	 */
	@Test
	public void testClear()
	{
		assertFalse(_aspectMap.isEmpty());
		_keySet.clear();
		assertTrue(_keySet.isEmpty());
		assertTrue(_aspectMap
			.isEmpty());
		assertEquals(1, _listener.beforeClearEvents.size());
		assertEquals(1, _listener.afterClearEvents.size());

		/* second clear does nothing */
		_keySet.clear();
		assertEquals(1, _listener.beforeClearEvents.size());
		assertEquals(1, _listener.afterClearEvents.size());
	}

	/**
	 * Test of clear method, of class AspectMultiMapKeySet.
	 */
	@Test
	public void testClear_veto()
	{
		_listener.vetoClear = true;
		assertFalse(_aspectMap.isEmpty());
		try {
			_keySet.clear();
			fail("Clear veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertFalse(_keySet.isEmpty());
		
		_baseMap.clear();
		/* second clear does nothing */
		_keySet.clear();
		assertTrue(_listener.beforeClearEvents.isEmpty());
		assertTrue(_listener.afterClearEvents.isEmpty());
	}
}
