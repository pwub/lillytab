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
public class AspectMapEntrySetTest {
	private Map<String, String> _baseMap;
	private AspectMap<String, String, Map<String, String>> _aspectMap;
	private Set<Map.Entry<String, String>> _entrySet;
	private AspectCollectionHistoryListener<Map.Entry<String, String>, Map<String, String>> _listener;

    public AspectMapEntrySetTest() {
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
    public void setUp() {
		_baseMap = new TreeMap<>();
		_baseMap.put("A", "1");
		_baseMap.put("B", "2");
		_baseMap.put("C", "3");
		_baseMap.put("D", "4");

		_aspectMap = AspectMap.decorate(_baseMap, this);
		_listener = new AspectCollectionHistoryListener<>();
		_aspectMap.getListeners().add(_listener);
		_entrySet = _aspectMap.entrySet();
    }

    @After
    public void tearDown() {
		_entrySet = null;
		_listener = null;
		_aspectMap = null;
		_baseMap = null;
    }

	/**
	 * Test of size method, of class AspectMapEntrySet.
	 */
	@Test
	public void testSize()
	{
		assertEquals(4, _entrySet.size());
	}

	/**
	 * Test of isEmpty method, of class AspectMapEntrySet.
	 */
	@Test
	public void testIsEmpty()
	{
		assertFalse(_entrySet.isEmpty());
		_aspectMap.clear();
		assertTrue(_aspectMap.isEmpty());
		assertTrue(_entrySet.isEmpty());
	}

	/**
	 * Test of contains method, of class AspectMapEntrySet.
	 */
	@Test
	public void testContains()
	{
		assertTrue(_entrySet.contains(new DefaultMapEntry<>("A", "1")));
		assertTrue(_entrySet.contains(new DefaultMapEntry<>("B", "2")));
		assertTrue(_entrySet.contains(new DefaultMapEntry<>("C", "3")));
		assertTrue(_entrySet.contains(new DefaultMapEntry<>("D", "4")));
		assertFalse(_entrySet.contains(new DefaultMapEntry<>("E", "5")));
	}

	/**
	 * Test of iterator method, of class AspectMapEntrySet.
	 */
	@Test
	public void testIterator()
	{
		List<Map.Entry<String, String>> targetList = new ArrayList<>();
		targetList.add(new DefaultMapEntry<>("A", "1"));
		targetList.add(new DefaultMapEntry<>("B", "2"));
		targetList.add(new DefaultMapEntry<>("C", "3"));
		targetList.add(new DefaultMapEntry<>("D", "4"));
		Iterator<Map.Entry<String, String>> iter = _entrySet.iterator();
		while (iter.hasNext())
			assertTrue(targetList.remove(iter.next()));
		assertTrue(targetList.isEmpty());
	}

	@Test
	public void testIteratorRemove()
	{
		List<Map.Entry<String, String>> targetList = new ArrayList<>();
		targetList.add(new DefaultMapEntry<>("A", "1"));
		targetList.add(new DefaultMapEntry<>("B", "2"));
		targetList.add(new DefaultMapEntry<>("C", "3"));
		targetList.add(new DefaultMapEntry<>("D", "4"));
		Iterator<Map.Entry<String, String>> iter = _entrySet.iterator();
		iter.next();
		/* need to copy, treemap modifies its entries */
		Map.Entry<String, String> entry = new DefaultMapEntry<>(iter.next());
		iter.remove();

		assertFalse(_entrySet.contains(entry));
		assertFalse(_aspectMap.containsKey(entry.getKey()));
		assertEquals(1, _listener.beforeRemoveEvents.size());
		assertEquals(entry, _listener.beforeRemoveEvents.get(0).getItem());
		assertEquals(1, _listener.afterRemoveEvents.size());
		assertEquals(entry, _listener.afterRemoveEvents.get(0).getItem());
	}

	@Test
	public void testIteratorRemove_veto()
	{
		List<Map.Entry<String, String>> targetList = new ArrayList<>();
		targetList.add(new DefaultMapEntry<>("A", "1"));
		targetList.add(new DefaultMapEntry<>("B", "2"));
		targetList.add(new DefaultMapEntry<>("C", "3"));
		targetList.add(new DefaultMapEntry<>("D", "4"));
		Iterator<Map.Entry<String, String>> iter = _entrySet.iterator();
		iter.next();
		Map.Entry<String, String> entry = iter.next();
		_listener.vetoRemove = true;
		try {
			iter.remove();
			fail("Remove veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}

		assertTrue(_entrySet.contains(entry));
		assertTrue(_aspectMap.containsKey(entry.getKey()));
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());
	}


	/**
	 * Test of toArray method, of class AspectMapEntrySet.
	 */
	@Test
	public void testToArray_0args()
	{
		assertArrayEquals(_baseMap.entrySet().toArray(), _entrySet.toArray());
	}

	/**
	 * Test of toArray method, of class AspectMapEntrySet.
	 */
	@Test
	public void testToArray_GenericType()
	{
		assertArrayEquals(
			_baseMap.entrySet().toArray(new Map.Entry[0]),
			_entrySet.toArray(new Map.Entry[_entrySet.size()])
		);
	}

	/**
	 * Test of add method, of class AspectMapEntrySet.
	 */
	@Test(expected=UnsupportedOperationException.class)
	public void testAdd()
	{
		_entrySet.add(new DefaultMapEntry<>("E", "5"));
	}

	/**
	 * Test of remove method, of class AspectMapEntrySet.
	 */
	@Test
	public void testRemove()
	{
		assertFalse(_entrySet.remove(new DefaultMapEntry<>("E", "5")));
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());
		assertTrue(_entrySet.remove(new DefaultMapEntry<>("A", "1")));
		assertFalse(_aspectMap.containsKey("A"));
		assertEquals(1, _listener.beforeRemoveEvents.size());
		assertEquals(new DefaultMapEntry<>("A", "1"), _listener.beforeRemoveEvents.get(0).getItem());
		assertEquals(1, _listener.afterRemoveEvents.size());
		assertEquals(new DefaultMapEntry<>("A", "1"), _listener.afterRemoveEvents.get(0).getItem());
	}


	/**
	 * Test of remove method, of class AspectMapEntrySet.
	 */
	@Test
	public void testRemove_veto()
	{
		_listener.vetoRemove = true;
		assertFalse(_entrySet.remove(new DefaultMapEntry<>("E", "5")));
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());
		try {
			assertTrue(_entrySet.remove(new DefaultMapEntry<>("A", "1")));
			fail("Remove veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_aspectMap.containsKey("A"));
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());
	}

	/**
	 * Test of containsAll method, of class AspectMapEntrySet.
	 */
	@Test
	public void testContainsAll()
	{
		List<Map.Entry<String, String>> targetList = new ArrayList<>();
		targetList.add(new DefaultMapEntry<>("A", "1"));
		targetList.add(new DefaultMapEntry<>("B", "2"));
		targetList.add(new DefaultMapEntry<>("C", "3"));
		targetList.add(new DefaultMapEntry<>("D", "4"));
		assertTrue(_entrySet.containsAll(targetList));
		targetList.add(new DefaultMapEntry<>("E", "5"));
		assertFalse(_entrySet.containsAll(targetList));
	}

	/**
	 * Test of addAll method, of class AspectMapEntrySet.
	 */
	@Test(expected=UnsupportedOperationException.class)
	public void testAddAll()
	{
		_entrySet.add(new DefaultMapEntry<>("E", "5"));
	}

		/**
	 * Test of retainAll method, of class AspectMapEntrySet.
	 */
	@Test
	public void testRetainAll()
	{
		List<Map.Entry<String, String>> targetList = new ArrayList<>();
		targetList.add(new DefaultMapEntry<>("A", "1"));
		targetList.add(new DefaultMapEntry<>("B", "2"));
		targetList.add(new DefaultMapEntry<>("C", "3"));
		targetList.add(new DefaultMapEntry<>("D", "4"));
		targetList.add(new DefaultMapEntry<>("E", "5"));
		assertFalse(_entrySet.retainAll(targetList));

		targetList = new ArrayList<>();
		targetList.add(new DefaultMapEntry<>("B", "2"));
		targetList.add(new DefaultMapEntry<>("C", "3"));
		targetList.add(new DefaultMapEntry<>("E", "5"));
		assertTrue(_entrySet.retainAll(targetList));

		assertEquals(2, _entrySet.size());
		assertTrue(targetList.containsAll(_entrySet));

		assertEquals(2, _listener.beforeRemoveEvents.size());
		assertEquals(new DefaultMapEntry<>("A", "1"), _listener.beforeRemoveEvents.get(0).getItem());;
		assertEquals(new DefaultMapEntry<>("D", "4"), _listener.beforeRemoveEvents.get(1).getItem());;
		assertEquals(2, _listener.afterRemoveEvents.size());
		assertEquals(new DefaultMapEntry<>("A", "1"), _listener.afterRemoveEvents.get(0).getItem());;
		assertEquals(new DefaultMapEntry<>("D", "4"), _listener.afterRemoveEvents.get(1).getItem());;
	}


	/**
	 * Test of retainAll method, of class AspectMapEntrySet.
	 */
	@Test
	public void testRetainAll_veto()
	{
		_listener.vetoRemove = true;

		List<Map.Entry<String, String>> targetList = new ArrayList<>();
		targetList.add(new DefaultMapEntry<>("A", "1"));
		targetList.add(new DefaultMapEntry<>("B", "2"));
		targetList.add(new DefaultMapEntry<>("C", "3"));
		targetList.add(new DefaultMapEntry<>("D", "4"));
		targetList.add(new DefaultMapEntry<>("E", "5"));
		assertFalse(_entrySet.retainAll(targetList));

		targetList = new ArrayList<>();
		targetList.add(new DefaultMapEntry<>("B", "2"));
		targetList.add(new DefaultMapEntry<>("C", "3"));
		targetList.add(new DefaultMapEntry<>("E", "5"));
		try {
			assertTrue(_entrySet.retainAll(targetList));
			fail("Remove veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}

		assertEquals(4, _entrySet.size());

	}


	/**
	 * Test of removeAll method, of class AspectMapEntrySet.
	 */
	@Test
	public void testRemoveAll()
	{
		List<Map.Entry<String, String>> targetList = new ArrayList<>();
		targetList.add(new DefaultMapEntry<>("E", "5"));
		targetList.add(new DefaultMapEntry<>("F", "6"));
		assertFalse(_entrySet.removeAll(targetList));

		targetList.add(new DefaultMapEntry<>("B", "2"));
		targetList.add(new DefaultMapEntry<>("C", "3"));
		assertTrue(_entrySet.removeAll(targetList));

		assertEquals(2, _entrySet.size());
		assertTrue(_entrySet.contains(new DefaultMapEntry<>("A", "1")));
		assertFalse(_entrySet.contains(new DefaultMapEntry<>("B", "2")));
		assertFalse(_entrySet.contains(new DefaultMapEntry<>("C", "3")));
		assertTrue(_entrySet.contains(new DefaultMapEntry<>("D", "4")));
		assertFalse(_entrySet.contains(new DefaultMapEntry<>("E", "5")));

		assertEquals(2, _listener.beforeRemoveEvents.size());
		assertEquals(new DefaultMapEntry<>("B", "2"), _listener.beforeRemoveEvents.get(0).getItem());;
		assertEquals(new DefaultMapEntry<>("C", "3"), _listener.beforeRemoveEvents.get(1).getItem());;
		assertEquals(2, _listener.afterRemoveEvents.size());
		assertEquals(new DefaultMapEntry<>("B", "2"), _listener.afterRemoveEvents.get(0).getItem());;
		assertEquals(new DefaultMapEntry<>("C", "3"), _listener.afterRemoveEvents.get(1).getItem());;
	}

		/**
	 * Test of removeAll method, of class AspectMapEntrySet.
	 */
	@Test
	public void testRemoveAll_veto()
	{
		_listener.vetoRemove = true;
		List<Map.Entry<String, String>> targetList = new ArrayList<>();
		targetList.add(new DefaultMapEntry<>("E", "5"));
		targetList.add(new DefaultMapEntry<>("F", "6"));
		assertFalse(_entrySet.removeAll(targetList));

		targetList.add(new DefaultMapEntry<>("B", "2"));
		targetList.add(new DefaultMapEntry<>("C", "3"));
		try {
			_entrySet.removeAll(targetList);
			fail("Remove veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}

		assertEquals(4, _entrySet.size());
		assertTrue(_entrySet.contains(new DefaultMapEntry<>("A", "1")));
		assertTrue(_entrySet.contains(new DefaultMapEntry<>("B", "2")));
		assertTrue(_entrySet.contains(new DefaultMapEntry<>("C", "3")));
		assertTrue(_entrySet.contains(new DefaultMapEntry<>("D", "4")));
		assertFalse(_entrySet.contains(new DefaultMapEntry<>("E", "5")));

		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());
	}

	/**
	 * Test of clear method, of class AspectMapEntrySet.
	 */
	@Test
	public void testClear()
	{
		assertFalse(_entrySet.isEmpty());
		_entrySet.clear();
		assertTrue(_entrySet.isEmpty());
		assertTrue(_aspectMap.isEmpty());
		assertEquals(1, _listener.beforeClearEvents.size());
		assertEquals(1, _listener.afterClearEvents.size());

		/* second clear does nothing */
		_entrySet.clear();
		assertEquals(1, _listener.beforeClearEvents.size());
		assertEquals(1, _listener.afterClearEvents.size());
	}

	/**
	 * Test of clear method, of class AspectMapEntrySet.
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
	}

}
