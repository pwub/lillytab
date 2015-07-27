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
package de.dhke.projects.cutil.collections.cow;

import de.dhke.projects.cutil.collections.factories.IMultiMapFactory;
import de.dhke.projects.cutil.collections.factories.MultiHashMapFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections15.MultiMap;
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
public class CopyOnWriteMultiMapEntrySetTest
{
	private final IMultiMapFactory<String, String, MultiMap<String, String>> _factory = new MultiHashMapFactory<>();
	private MultiMap<String, String> _baseMap;
	private CopyOnWriteMultiMap<String, String> _cowMap;
	private Set<Map.Entry<String, Collection<String>>> _entrySet;

	public CopyOnWriteMultiMapEntrySetTest()
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
		_baseMap = _factory.getInstance();
		_baseMap.put("1", "a");
		_baseMap.put("1", "A");
		_baseMap.put("2", "b");
		_baseMap.put("2", "B");
		_baseMap.put("3", "c");
		_baseMap.put("3", "C");
		_cowMap = new CopyOnWriteMultiMap<>(_baseMap, _factory);
		_entrySet = _cowMap.entrySet();
	}

	@After
	public void tearDown()
	{
		_baseMap = null;
		_cowMap = null;
		_entrySet = null;
	}

	/**
	 * Test of size method, of class CopyOnWriteMultiMapEntrySet.
	 */
	@Test
	public void testSize()
	{
		assertEquals(3, _entrySet.size());
	}

	/**
	 * Test of isEmpty method, of class CopyOnWriteMultiMapEntrySet.
	 */
	@Test
	public void testIsEmpty()
	{
		assertFalse(_entrySet.isEmpty());
		_cowMap.clear();
		assertTrue(_entrySet.isEmpty());
	}

	/**
	 * Test of contains method, of class CopyOnWriteMultiMapEntrySet.
	 */
	@Test
	public void testContains()
	{
		Map.Entry<String, Collection<String>> e =
			new DefaultMapEntry<String, Collection<String>>("1", Arrays.asList("a", "A"));
		assertTrue(_entrySet.contains(e));
	}

	/**
	 * Test of iterator method, of class CopyOnWriteMultiMapEntrySet.
	 */
	@Test
	public void testIterator()
	{
		List<Map.Entry<String, Collection<String>>> referenceList = new ArrayList<>();
		referenceList.add(new DefaultMapEntry<String, Collection<String>>("1", Arrays.asList("a", "A")));
		referenceList.add(new DefaultMapEntry<String, Collection<String>>("2", Arrays.asList("b", "B")));
		referenceList.add(new DefaultMapEntry<String, Collection<String>>("3", Arrays.asList("c", "C")));
		Iterator<Map.Entry<String, Collection<String>>> iter = _entrySet.iterator();
		assertTrue(iter.hasNext());
		assertTrue(referenceList.contains(iter.next()));
		assertTrue(iter.hasNext());
		assertTrue(referenceList.contains(iter.next()));
		assertTrue(iter.hasNext());
		assertTrue(referenceList.contains(iter.next()));
		assertFalse(iter.hasNext());

	}

	/**
	 * Test of toArray method, of class CopyOnWriteMultiMapEntrySet.
	 */
	@Test
	public void testToArray_0args()
	{
		List<Map.Entry<String, Collection<String>>> referenceList = new ArrayList<>();
		referenceList.add(new DefaultMapEntry<String, Collection<String>>("1", Arrays.asList("a", "A")));
		referenceList.add(new DefaultMapEntry<String, Collection<String>>("2", Arrays.asList("b", "B")));
		referenceList.add(new DefaultMapEntry<String, Collection<String>>("3", Arrays.asList("c", "C")));
		Object[] resultArray = _entrySet.toArray();
		assertEquals(3, resultArray.length);
		assertTrue(referenceList.containsAll(Arrays.asList(resultArray)));
	}

	/**
	 * Test of toArray method, of class CopyOnWriteMultiMapEntrySet.
	 */
	@Test
	public void testToArray_null()
	{
		List<Map.Entry<String, Collection<String>>> referenceList = new ArrayList<>();
		referenceList.add(new DefaultMapEntry<String, Collection<String>>("1", Arrays.asList("a", "A")));
		referenceList.add(new DefaultMapEntry<String, Collection<String>>("2", Arrays.asList("b", "B")));
		referenceList.add(new DefaultMapEntry<String, Collection<String>>("3", Arrays.asList("c", "C")));
		Map.Entry[] resultArray = _entrySet.toArray(new Map.Entry[]{});
		assertEquals(3, resultArray.length);
		assertTrue(referenceList.containsAll(Arrays.asList(resultArray)));
	}

	/**
	 * Test of add method, of class CopyOnWriteMultiMapEntrySet.
	 */
	@Test
	public void testAdd()
	{
		Map.Entry<String, Collection<String>> entry = new DefaultMapEntry<String, Collection<String>>("4", Arrays.asList("d", "D"));
		assertTrue(_entrySet.add(entry));
		assertTrue(_cowMap.containsKey(entry.getKey()));
		assertTrue(_cowMap.containsValue("4", "d"));
		assertTrue(_cowMap.containsValue("4", "D"));
		assertFalse(_baseMap.containsKey(entry.getKey()));
		assertFalse(_baseMap.containsValue("4", "d"));
		assertFalse(_baseMap.containsValue("4", "D"));
	}

	/**
	 * Test of remove method, of class CopyOnWriteMultiMapEntrySet.
	 */
	@Test
	public void testRemove()
	{
		Map.Entry<String, Collection<String>> entry = new DefaultMapEntry<String, Collection<String>>("3", Arrays.asList("c", "C"));
		assertTrue(_entrySet.remove(entry));
		assertFalse(_cowMap.containsKey(entry.getKey()));
		assertFalse(_cowMap.containsValue("3", "c"));
		assertFalse(_cowMap.containsValue("3", "C"));
		assertTrue(_baseMap.containsKey(entry.getKey()));
		assertTrue(_baseMap.containsValue("3", "c"));
		assertTrue(_baseMap.containsValue("3", "C"));
	}

	/**
	 * Test of containsAll method, of class CopyOnWriteMultiMapEntrySet.
	 */
	@Test
	public void testContainsAll()
	{
		List<Map.Entry<String, Collection<String>>> referenceList = new ArrayList<>();
		referenceList.add(new DefaultMapEntry<String, Collection<String>>("1", Arrays.asList("a", "A")));
		assertTrue(_entrySet.containsAll(referenceList));
		referenceList.add(new DefaultMapEntry<String, Collection<String>>("2", Arrays.asList("b", "B")));
		assertTrue(_entrySet.containsAll(referenceList));
		referenceList.add(new DefaultMapEntry<String, Collection<String>>("3", Arrays.asList("c", "C")));
		assertTrue(_entrySet.containsAll(referenceList));
		referenceList.add(new DefaultMapEntry<String, Collection<String>>("4", Arrays.asList("d", "D")));
		assertFalse(_entrySet.containsAll(referenceList));
		assertFalse(_baseMap.containsValue("4", "d"));
		assertFalse(_baseMap.containsValue("4", "D"));
	}

	/**
	 * Test of addAll method, of class CopyOnWriteMultiMapEntrySet.
	 */
	@Test(expected=UnsupportedOperationException.class)
	public void testAddAll()
	{
		List<Map.Entry<String, Collection<String>>> referenceList = new ArrayList<>();
		referenceList.add(new DefaultMapEntry<String, Collection<String>>("4", Arrays.asList("d", "D")));
		assertTrue(_entrySet.addAll(referenceList));
		assertTrue(_cowMap.containsValue("4", "d"));
		assertTrue(_cowMap.containsValue("4", "D"));
		assertFalse(_baseMap.containsValue("4", "d"));
		assertFalse(_baseMap.containsValue("4", "D"));
	}

	/**
	 * Test of retainAll method, of class CopyOnWriteMultiMapEntrySet.
	 */
	@Test
	public void testRetainAll()
	{
		List<Map.Entry<String, Collection<String>>> referenceList = new ArrayList<>();
		referenceList.add(new DefaultMapEntry<String, Collection<String>>("2", Arrays.asList("b", "B")));
		referenceList.add(new DefaultMapEntry<String, Collection<String>>("3", Arrays.asList("c", "C")));
		_entrySet.retainAll(referenceList);
		assertFalse(_cowMap.containsValue("1", "a"));
		assertFalse(_cowMap.containsValue("1", "A"));
		assertTrue(_cowMap.containsValue("2", "b"));
		assertTrue(_cowMap.containsValue("2", "B"));
		assertTrue(_cowMap.containsValue("3", "c"));
		assertTrue(_cowMap.containsValue("3", "C"));
		assertTrue(_baseMap.containsValue("1", "a"));
		assertTrue(_baseMap.containsValue("1", "A"));
		assertTrue(_baseMap.containsValue("2", "b"));
		assertTrue(_baseMap.containsValue("2", "B"));
		assertTrue(_baseMap.containsValue("3", "c"));
		assertTrue(_baseMap.containsValue("3", "C"));
	}

	/**
	 * Test of removeAll method, of class CopyOnWriteMultiMapEntrySet.
	 */
	@Test
	public void testRemoveAll()
	{
		List<Map.Entry<String, Collection<String>>> referenceList = new ArrayList<>();
		referenceList.add(new DefaultMapEntry<String, Collection<String>>("2", Arrays.asList("b", "B")));
		referenceList.add(new DefaultMapEntry<String, Collection<String>>("3", Arrays.asList("c", "C")));
		_entrySet.removeAll(referenceList);
		assertTrue(_cowMap.containsValue("1", "a"));
		assertTrue(_cowMap.containsValue("1", "A"));
		assertFalse(_cowMap.containsValue("2", "b"));
		assertFalse(_cowMap.containsValue("2", "B"));
		assertFalse(_cowMap.containsValue("3", "c"));
		assertFalse(_cowMap.containsValue("3", "C"));
		assertTrue(_baseMap.containsValue("1", "a"));
		assertTrue(_baseMap.containsValue("1", "A"));
		assertTrue(_baseMap.containsValue("2", "b"));
		assertTrue(_baseMap.containsValue("2", "B"));
		assertTrue(_baseMap.containsValue("3", "c"));
		assertTrue(_baseMap.containsValue("3", "C"));
	}

	/**
	 * Test of clear method, of class CopyOnWriteMultiMapEntrySet.
	 */
	@Test
	public void testClear()
	{
		_entrySet.clear();
		assertTrue(_cowMap.isEmpty());
		assertFalse(_baseMap.isEmpty());
	}
}
