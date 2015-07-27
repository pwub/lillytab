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
import java.util.Map;
import java.util.Set;
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
public class AspectMapKeySetTest
{
	private Map<String, String> _baseMap;
	private AspectMap<String, String, Map<String, String>> _aspectMap;
	private Set<String> _keySet;
	private AspectCollectionHistoryListener<Map.Entry<String, String>, Map<String, String>> _listener;

	public AspectMapKeySetTest()
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
		_keySet = _aspectMap.keySet();
	}

	@After
	public void tearDown()
	{
		_keySet = null;
		_aspectMap = null;
		_listener = null;
		_baseMap = null;
	}

	/**
	 * Test of size method, of class AspectMapKeySet.
	 */
	@Test
	public void testSize()
	{
		assertEquals(4, _keySet.size());
		_keySet.remove("A");
		assertEquals(3, _keySet.size());
	}

	/**
	 * Test of isEmpty method, of class AspectMapKeySet.
	 */
	@Test
	public void testIsEmpty()
	{
		assertFalse(_keySet.isEmpty());
		_aspectMap.clear();
		assertTrue(_keySet.isEmpty());
	}

	/**
	 * Test of contains method, of class AspectMapKeySet.
	 */
	@Test
	public void testContains()
	{
		assertFalse(_keySet.contains("_"));
		assertTrue(_keySet.contains("A"));
		assertTrue(_keySet.contains("B"));
		assertTrue(_keySet.contains("C"));
		assertTrue(_keySet.contains("D"));
		assertFalse(_keySet.contains("E"));
	}

	/**
	 * Test of iterator method, of class AspectMapKeySet.
	 */
	@Test
	public void testIterator()
	{
		final List<String> testList = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));
		Iterator<String> iter = _keySet.iterator();
		while (iter.hasNext())
			assertTrue(testList.remove(iter.next()));
		assertTrue(testList.isEmpty());
	}

	@Test
	public void testIteratorRemove()
	{
		final List<String> testList = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));
		Iterator<String> iter = _keySet.iterator();
		String key = iter.next();
		iter.remove();
		testList.removeAll(_keySet);

		assertEquals(1, testList.size());
		assertTrue(testList.contains(key));

		assertEquals(1, _listener.beforeRemoveEvents.size());
		assertEquals(new DefaultMapEntry<>("A", "1"), _listener.beforeRemoveEvents.get(0).getItem());
		assertEquals(1, _listener.afterRemoveEvents.size());
		assertEquals(new DefaultMapEntry<>("A", "1"), _listener.afterRemoveEvents.get(0).getItem());
	}

	@Test
	public void testIteratorRemove_veto()
	{
		_listener.vetoRemove = true;
		Iterator<String> iter = _keySet.iterator();
		try {
			iter.next();
			fail("Remove veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertEquals(4, _keySet.size());
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());
	}

	/**
	 * Test of toArray method, of class AspectMapKeySet.
	 */
	@Test
	public void testToArray_0args()
	{
		Object[] array = _keySet.toArray();
		Arrays.sort(array);
		assertArrayEquals(new Object[]{"A", "B", "C", "D"}, array);
	}

	/**
	 * Test of toArray method, of class AspectMapKeySet.
	 */
	@Test
	public void testToArray_GenericType()
	{
		String[] array = _keySet.toArray(new String[_keySet.size()]);
		Arrays.sort(array);
		assertArrayEquals(new String[]{"A", "B", "C", "D"}, array);
	}

	/**
	 * Test of add method, of class AspectMapKeySet.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testAdd()
	{
		_keySet.add("A");
	}

	/**
	 * Test of remove method, of class AspectMapKeySet.
	 */
	@Test
	public void testRemove()
	{
		assertFalse(_keySet.remove("E"));
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());
		assertTrue(_keySet.remove("A"));
		assertEquals(1, _listener.beforeRemoveEvents.size());
		assertEquals(new DefaultMapEntry<>("A", "1"), _listener.beforeRemoveEvents.get(0).getItem());
		assertEquals(new DefaultMapEntry<>("A", "1"), _listener.afterRemoveEvents.get(0).getItem());
	}

	/**
	 * Test of remove method, of class AspectMapKeySet.
	 */
	@Test
	public void testRemove_veto()
	{
		_listener.vetoRemove = true;
		try {
			assertTrue(_keySet.remove("A"));
			fail("Veto exception not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());
	}

	/**
	 * Test of containsAll method, of class AspectMapKeySet.
	 */
	@Test
	public void testContainsAll()
	{
		assertTrue(_keySet.containsAll(Arrays.asList("A", "B", "C", "D")));
		assertTrue(_keySet.containsAll(Arrays.asList("A", "D")));
		assertFalse(_keySet.containsAll(Arrays.asList("A", "B", "C", "D", "E")));
		assertFalse(_keySet.containsAll(Arrays.asList("B", "C", "D", "E")));
		assertFalse(_keySet.containsAll(Arrays.asList("E", "F")));
	}

	/**
	 * Test of addAll method, of class AspectMapKeySet.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testAddAll()
	{
		_keySet.addAll(Arrays.asList("A", "F"));
	}

	/**
	 * Test of retainAll method, of class AspectMapKeySet.
	 */
	@Test
	public void testRetainAll()
	{
		assertFalse(_keySet.retainAll(Arrays.asList("A", "B", "C", "D", "E")));
		assertTrue(_keySet.retainAll(Arrays.asList("B", "C")));
		assertEquals(2, _keySet.size());
		assertFalse(_keySet.contains("A"));
		assertTrue(_keySet.contains("B"));
		assertTrue(_keySet.contains("C"));
		assertFalse(_keySet.contains("D"));
		assertFalse(_keySet.contains("E"));
		assertEquals(2, _aspectMap.size());

		List<Map.Entry<String, String>> targetEntries = new ArrayList<>();
		targetEntries.add(new DefaultMapEntry<>("A", "1"));
		targetEntries.add(new DefaultMapEntry<>("D", "4"));
		assertEquals(2, _listener.beforeRemoveEvents.size());
		assertTrue(targetEntries.contains(_listener.beforeRemoveEvents.get(0).getItem()));
		assertTrue(targetEntries.contains(_listener.beforeRemoveEvents.get(1).getItem()));
		assertEquals(2, _listener.afterRemoveEvents.size());
		assertTrue(targetEntries.contains(_listener.afterRemoveEvents.get(0).getItem()));
		assertTrue(targetEntries.contains(_listener.afterRemoveEvents.get(1).getItem()));
	}

	@Test
	public void testRetainAll_veto()
	{
		_listener.vetoRemove = true;
		assertFalse(_keySet.retainAll(Arrays.asList("A", "B", "C", "D", "E")));
		try {
			assertTrue(_keySet.retainAll(Arrays.asList("B", "C")));
			fail("Remove veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertEquals(4, _keySet.size());
	}

	/**
	 * Test of removeAll method, of class AspectMapKeySet.
	 */
	@Test
	public void testRemoveAll()
	{
		assertFalse(_keySet.removeAll(Arrays.asList("F")));
		assertTrue(_keySet.removeAll(Arrays.asList("B", "C")));
		assertEquals(2, _keySet.size());
		assertTrue(_keySet.contains("A"));
		assertFalse(_keySet.contains("B"));
		assertFalse(_keySet.contains("C"));
		assertTrue(_keySet.contains("D"));
		assertFalse(_keySet.contains("E"));
		assertEquals(2, _aspectMap.size());
	}

	@Test
	public void testRemoveAll_veto()
	{
		_listener.vetoRemove = true;
		assertFalse(_keySet.removeAll(Arrays.asList("E", "F")));
		try {
			assertTrue(_keySet.removeAll(Arrays.asList("B", "C")));
			fail("Remove veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertEquals(4, _keySet.size());
	}

	/**
	 * Test of clear method, of class AspectMapKeySet.
	 */
	@Test
	public void testClear()
	{
		assertFalse(_keySet.isEmpty());
		_keySet.clear();
		assertTrue(_keySet.isEmpty());
		assertTrue(_aspectMap.isEmpty());
		assertEquals(1, _listener.beforeClearEvents.size());
		assertEquals(1, _listener.afterClearEvents.size());

		/* second clear does nothing */
		_keySet.clear();
		assertEquals(1, _listener.beforeClearEvents.size());
		assertEquals(1, _listener.afterClearEvents.size());

	}

	/**
	 * Test of clear method, of class AspectMapKeySet.
	 */
	@Test
	public void testClear_veto()
	{
		_listener.vetoClear = true;
		assertFalse(_keySet.isEmpty());
		try {
			_keySet.clear();
			fail("Clear veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertFalse(_keySet.isEmpty());
		assertFalse(_aspectMap.isEmpty());

		_baseMap.clear();
		/* clear on empty set does nothing */
		_keySet.clear();

		assertTrue(_listener.beforeClearEvents.isEmpty());
		assertTrue(_listener.afterClearEvents.isEmpty());
	}
}
