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
public class AspectMultiMapEntrySetTest
{
	private MultiMap<String, String> _baseMap;
	private AspectMultiMap<String, String, MultiMap<String, String>> _aspectMap;
	private AspectCollectionHistoryListener<Map.Entry<String, String>, MultiMap<String, String>> _listener;
	private Set<Map.Entry<String, Collection<String>>> _entrySet;

	public AspectMultiMapEntrySetTest()
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
		_entrySet = _aspectMap.entrySet();
	}

	@After
	public void tearDown()
	{
		_entrySet = null;
		_listener = null;
		_aspectMap = null;
		_baseMap = null;
	}

	/**
	 * Test of size method, of class AspectMultiMapEntrySet.
	 */
	@Test
	public void testSize()
	{
		assertEquals(3, _entrySet.size());
		assertNotNull(_baseMap.remove("2"));
		assertEquals(2, _entrySet.size());
		assertNotNull(_baseMap.remove("1", "a"));
		assertEquals(2, _entrySet.size());
	}

	/**
	 * Test of isEmpty method, of class AspectMultiMapEntrySet.
	 */
	@Test
	public void testIsEmpty()
	{
		assertFalse(_entrySet.isEmpty());
		_baseMap.clear();
		assertTrue(_entrySet.isEmpty());
	}

	/**
	 * Test of contains method, of class AspectMultiMapEntrySet.
	 */
	@Test
	public void testContains()
	{
		assertTrue(_entrySet.contains(new DefaultMapEntry<String, Collection<String>>("1", Arrays.asList("a", "A"))));
		assertTrue(_entrySet.contains(new DefaultMapEntry<String, Collection<String>>("2", Arrays.asList("b", "B"))));
		assertTrue(_entrySet.contains(new DefaultMapEntry<String, Collection<String>>("3", Arrays.asList("c", "C"))));
		assertFalse(_entrySet.contains(new DefaultMapEntry<String, Collection<String>>("1", Arrays.asList("a"))));
		assertFalse(_entrySet.contains(new DefaultMapEntry<String, Collection<String>>("2", Arrays.asList("a", "A"))));
	}

	/**
	 * Test of iterator method, of class AspectMultiMapEntrySet.
	 */
	@Test
	public void testIterator()
	{
	}

	/**
	 * Test of iterator method, of class AspectMultiMapEntrySet.
	 */
	@Test
	public void testIterator_remove()
	{
	}

	/**
	 * Test of iterator method, of class AspectMultiMapEntrySet.
	 */
	@Test
	public void testIterator_remove_veto()
	{
	}

	/**
	 * Test of toArray method, of class AspectMultiMapEntrySet.
	 */
	@Test
	public void testToArray_0args()
	{
		assertArrayEquals(_baseMap.entrySet().toArray(), _entrySet.toArray());
	}

	/**
	 * Test of toArray method, of class AspectMultiMapEntrySet.
	 */
	@Test
	public void testToArray_GenericType()
	{
		assertArrayEquals(_baseMap.entrySet().toArray(new Map.Entry[0]), _entrySet.toArray(new Map.Entry[_entrySet.size()]));
	}

	/**
	 * Test of add method, of class AspectMultiMapEntrySet.
	 */
	@Test(expected=UnsupportedOperationException.class)
	public void testAdd()
	{
		Collection<Map.Entry<String, Collection<String>>> addList = new ArrayList<>();
		addList.add(new DefaultMapEntry<String, Collection<String>>("_", Arrays.asList("a", "A")));
		addList.add(new DefaultMapEntry<String, Collection<String>>("@", Arrays.asList("b", "B")));
		_entrySet.addAll(addList);
	}

	/**
	 * Test of remove method, of class AspectMultiMapEntrySet.
	 */
	@Test
	public void testRemove()
	{
		assertFalse(_entrySet.remove(new DefaultMapEntry<String, Collection<String>>("1", Arrays.asList("a"))));
		assertFalse(_entrySet.remove(new DefaultMapEntry<String, Collection<String>>("1", Arrays.asList("A"))));
		assertTrue(_entrySet.remove(new DefaultMapEntry<String, Collection<String>>("1", Arrays.asList("a", "A"))));
		assertFalse(_aspectMap.containsValue("1", "a"));
		assertFalse(_aspectMap.containsValue("1", "A"));
		assertEquals(2, _listener.beforeRemoveEvents.size());
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("1", "a"), _listener.beforeRemoveEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("1", "A"), _listener.beforeRemoveEvents));
		assertEquals(2, _listener.afterRemoveEvents.size());
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("1", "a"), _listener.afterRemoveEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("1", "A"), _listener.afterRemoveEvents));
	}

	/**
	 * Test of remove method, of class AspectMultiMapEntrySet.
	 */
	@Test
	public void testRemove_veto()
	{
		_listener.vetoRemove = true;
		assertFalse(_entrySet.remove(new DefaultMapEntry<String, Collection<String>>("1", Arrays.asList("a"))));
		assertFalse(_entrySet.remove(new DefaultMapEntry<String, Collection<String>>("1", Arrays.asList("A"))));
		try {
			_entrySet.remove(new DefaultMapEntry<String, Collection<String>>("1", Arrays.asList("a", "A")));
			fail("Remove veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_aspectMap.containsValue("1", "a"));
		assertTrue(_aspectMap.containsValue("1", "A"));
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());
	}

	/**
	 * Test of containsAll method, of class AspectMultiMapEntrySet.
	 */
	@Test
	public void testContainsAll()
	{
		List<Map.Entry<String, Collection<String>>> targetList = new ArrayList<>();
		assertTrue(_entrySet.containsAll(targetList));
		targetList.add(new DefaultMapEntry<String, Collection<String>>("1", Arrays.asList("a", "A")));
		assertTrue(_entrySet.containsAll(targetList));
		targetList.add(new DefaultMapEntry<String, Collection<String>>("2", Arrays.asList("b", "B")));
		assertTrue(_entrySet.containsAll(targetList));
		targetList.add(new DefaultMapEntry<String, Collection<String>>("3", Arrays.asList("c", "C")));
		assertTrue(_entrySet.containsAll(targetList));
		targetList.add(new DefaultMapEntry<String, Collection<String>>("4", Arrays.asList("d", "D")));
		assertFalse(_entrySet.containsAll(targetList));
		targetList.remove(0);
		assertFalse(_entrySet.containsAll(targetList));
	}

	/**
	 * Test of addAll method, of class AspectMultiMapEntrySet.
	 */
	@Test(expected=UnsupportedOperationException.class)
	public void testAddAll()
	{
		List<Map.Entry<String, Collection<String>>> targetList = new ArrayList<>();
		targetList.add(new DefaultMapEntry<String, Collection<String>>("1", Arrays.asList("a", "A")));
		targetList.add(new DefaultMapEntry<String, Collection<String>>("2", Arrays.asList("b", "B")));
		_entrySet.addAll(targetList);
	}

	/**
	 * Test of retainAll method, of class AspectMultiMapEntrySet.
	 */
	@Test
	public void testRetainAll()
	{
		List<Map.Entry<String, Collection<String>>> targetList = new ArrayList<>();
		targetList.add(new DefaultMapEntry<String, Collection<String>>("1", Arrays.asList("a", "A")));
		targetList.add(new DefaultMapEntry<String, Collection<String>>("2", Arrays.asList("b", "B")));
		targetList.add(new DefaultMapEntry<String, Collection<String>>("3", Arrays.asList("c", "C")));
		targetList.add(new DefaultMapEntry<String, Collection<String>>("4", Arrays.asList("d", "D")));
		assertFalse(_entrySet.retainAll(targetList));
		/* remove "2" -> "b", "B" from target list */
		targetList.remove(1);
		assertTrue(_entrySet.retainAll(targetList));

		assertFalse(_aspectMap.containsValue("2", "b"));
		assertFalse(_aspectMap.containsValue("2", "B"));

		assertEquals(2, _listener.beforeRemoveEvents.size());
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("2", "b"), _listener.beforeRemoveEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("2", "B"), _listener.beforeRemoveEvents));
		assertEquals(2, _listener.afterRemoveEvents.size());
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("2", "b"), _listener.afterRemoveEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("2", "B"), _listener.afterRemoveEvents));
	}

	/**
	 * Test of retainAll method, of class AspectMultiMapEntrySet.
	 */
	@Test
	public void testRetainAll_veto()
	{
		_listener.vetoRemove = true;
		List<Map.Entry<String, Collection<String>>> targetList = new ArrayList<>();
		targetList.add(new DefaultMapEntry<String, Collection<String>>("1", Arrays.asList("a", "A")));
		targetList.add(new DefaultMapEntry<String, Collection<String>>("2", Arrays.asList("b", "B")));
		targetList.add(new DefaultMapEntry<String, Collection<String>>("3", Arrays.asList("c", "C")));
		targetList.add(new DefaultMapEntry<String, Collection<String>>("4", Arrays.asList("d", "D")));
		assertFalse(_entrySet.retainAll(targetList));
		/* remove "2" -> "b", "B" from target list */
		targetList.remove(1);
		try {
			_entrySet.retainAll(targetList);
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
	 * Test of removeAll method, of class AspectMultiMapEntrySet.
	 */
	@Test
	public void testRemoveAll()
	{
		List<Map.Entry<String, Collection<String>>> targetList = new ArrayList<>();
		assertFalse(_entrySet.removeAll(targetList));
		targetList.add(new DefaultMapEntry<String, Collection<String>>("4", Arrays.asList("d", "D")));
		assertFalse(_entrySet.removeAll(targetList));
		targetList.add(new DefaultMapEntry<String, Collection<String>>("2", Arrays.asList("b", "B")));
		assertTrue(_entrySet.removeAll(targetList));

		assertFalse(_aspectMap.containsValue("2", "b"));
		assertFalse(_aspectMap.containsValue("2", "B"));

		assertEquals(2, _listener.beforeRemoveEvents.size());
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("2", "b"), _listener.beforeRemoveEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("2", "B"), _listener.beforeRemoveEvents));
		assertEquals(2, _listener.afterRemoveEvents.size());
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("2", "b"), _listener.afterRemoveEvents));
		assertTrue(TestHelper.eventListContains(new DefaultMapEntry<>("2", "B"), _listener.afterRemoveEvents));
	}

	/**
	 * Test of removeAll method, of class AspectMultiMapEntrySet.
	 */
	@Test
	public void testRemoveAll_veto()
	{
		_listener.vetoRemove = true;
		List<Map.Entry<String, Collection<String>>> targetList = new ArrayList<>();
		assertFalse(_entrySet.removeAll(targetList));
		targetList.add(new DefaultMapEntry<String, Collection<String>>("4", Arrays.asList("d", "D")));
		assertFalse(_entrySet.removeAll(targetList));
		targetList.add(new DefaultMapEntry<String, Collection<String>>("2", Arrays.asList("b", "B")));
		try {
			_entrySet.removeAll(targetList);
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
	 * Test of clear method, of class AspectMultiMapEntrySet.
	 */
	@Test
	public void testClear()
	{
		assertFalse(_entrySet.isEmpty());
		_entrySet.clear();
		assertTrue(_entrySet.isEmpty());
		assertEquals(1, _listener.beforeClearEvents.size());
		assertEquals(1, _listener.afterClearEvents.size());

		/* second clear does nothing */
		_entrySet.clear();
		assertEquals(1, _listener.beforeClearEvents.size());
		assertEquals(1, _listener.afterClearEvents.size());
	}

	/**
	 * Test of clear method, of class AspectMultiMapEntrySet.
	 */
	@Test
	public void testClear_veto()
	{
		_listener.vetoClear = true;
		assertFalse(_entrySet.isEmpty());
		try {
			_entrySet.clear();
			fail("Clear veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertFalse(_entrySet.isEmpty());
		assertFalse(_aspectMap.isEmpty());

		assertTrue(_listener.beforeClearEvents.isEmpty());
		assertTrue(_listener.afterClearEvents.isEmpty());

		_baseMap.clear();
		/* clear on empty set does nothing */
		_entrySet.clear();
		assertTrue(_listener.beforeClearEvents.isEmpty());
		assertTrue(_listener.afterClearEvents.isEmpty());
	}
}
