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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
public class CopyOnWriteMultiMapTest
{
	private final IMultiMapFactory<String, String, MultiMap<String, String>> _factory = new MultiHashMapFactory<>();
	private MultiMap<String, String> _baseMap;
	private CopyOnWriteMultiMap<String, String> _cowMap;

	public CopyOnWriteMultiMapTest()
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
	}

	@After
	public void tearDown()
	{
		_baseMap = null;
		_cowMap = null;
	}

	/**
	 * Test of isWasCopied method, of class GenericCopyOnWriteMultiMap.
	 */
	@Test
	public void testIsWasCopied()
	{
		assertTrue(_cowMap.copy());
		assertTrue(_cowMap.isWasCopied());
	}

	/**
	 * Test of copy method, of class GenericCopyOnWriteMultiMap.
	 */
	@Test
	public void testCopy()
	{
		assertTrue(_cowMap.copy());
		assertNotSame(_baseMap, _cowMap.getDecoratee());
	}

	/**
	 * Test of remove method, of class GenericCopyOnWriteMultiMap.
	 */
	@Test
	public void testRemove_Object_Object()
	{
		assertEquals("a", _cowMap.remove("1", "a"));
		assertEquals("b", _cowMap.remove("2", "b"));
		assertFalse(_cowMap.containsValue("1", "a"));
		assertTrue(_cowMap.containsValue("1", "A"));
		assertFalse(_cowMap.containsValue("2", "b"));
		assertTrue(_cowMap.containsValue("2", "B"));

		assertTrue(_baseMap.containsValue("1", "a"));
		assertTrue(_baseMap.containsValue("1", "A"));
		assertTrue(_baseMap.containsValue("2", "b"));
		assertTrue(_baseMap.containsValue("2", "B"));
	}

	/**
	 * Test of size method, of class GenericCopyOnWriteMultiMap.
	 */
	@Test
	public void testSize_Object()
	{
		assertEquals(3, _baseMap.size());
		assertEquals(3, _cowMap.size());
		assertEquals(Arrays.asList("a", "A"), _cowMap.remove("1"));
		assertEquals(2, _cowMap.size());
		assertEquals(3, _baseMap.size());

	}

	/**
	 * Test of size method, of class GenericCopyOnWriteMultiMap.
	 */
	@Test
	public void testSize_0args()
	{
		assertEquals(2, _baseMap.size("1"));
		assertEquals(2, _cowMap.size("1"));
		assertEquals("a", _cowMap.remove("1", "a"));
		assertEquals(2, _baseMap.size("1"));
		assertEquals(1, _cowMap.size("1"));
	}

	/**
	 * Test of get method, of class GenericCopyOnWriteMultiMap.
	 */
	@Test
	public void testGet()
	{
		assertEquals(2, _cowMap.get("1").size());
		assertTrue(_cowMap.get("1").containsAll(Arrays.asList("a", "A")));
		assertEquals(2, _cowMap.get("2").size());
		assertTrue(_cowMap.get("2").containsAll(Arrays.asList("b", "B")));
		assertEquals(2, _cowMap.get("3").size());
		assertTrue(_cowMap.get("3").containsAll(Arrays.asList("c", "C")));
	}

	/**
	 * Test of containsValue method, of class GenericCopyOnWriteMultiMap.
	 */
	@Test
	public void testContainsValue_Object()
	{
		assertTrue(_cowMap.containsValue("A"));
		assertTrue(_cowMap.containsValue("a"));
		assertTrue(_cowMap.containsValue("B"));
		assertTrue(_cowMap.containsValue("b"));
		assertTrue(_cowMap.containsValue("C"));
		assertTrue(_cowMap.containsValue("c"));
	}

	/**
	 * Test of containsValue method, of class GenericCopyOnWriteMultiMap.
	 */
	@Test
	public void testContainsValue_Object_Object()
	{
		assertTrue(_cowMap.containsValue("1", "A"));
		assertTrue(_cowMap.containsValue("1", "a"));
		assertTrue(_cowMap.containsValue("2", "B"));
		assertTrue(_cowMap.containsValue("2", "b"));
		assertTrue(_cowMap.containsValue("3", "C"));
		assertTrue(_cowMap.containsValue("3", "c"));
	}

	/**
	 * Test of put method, of class GenericCopyOnWriteMultiMap.
	 */
	@Test
	public void testPut()
	{
		assertEquals("@", _cowMap.put("2", "@"));
		assertTrue(_cowMap.get("2").containsAll(Arrays.asList("b", "B", "@")));
		assertEquals(Arrays.asList("b", "B"), _baseMap.get("2"));
	}

	/**
	 * Test of remove method, of class GenericCopyOnWriteMultiMap.
	 */
	@Test
	public void testRemove_Object()
	{
		assertEquals(Arrays.asList("b", "B"), _baseMap.get("2"));
		assertEquals(Arrays.asList("b", "B"), _cowMap.remove("2"));
		assertTrue(_cowMap.get("2").isEmpty());
		assertEquals(Arrays.asList("b", "B"), _baseMap.get("2"));
	}

	/**
	 * Test of values method, of class GenericCopyOnWriteMultiMap.
	 */
	@Test
	public void testValues()
	{
		assertEquals(6, _cowMap.values().size());
		assertTrue(_cowMap.values().containsAll(Arrays.asList("a", "A", "B", "b", "c", "C")));

		assertTrue(_cowMap.values().remove("B"));
		assertEquals(5, _cowMap.values().size());
		assertTrue(_cowMap.values().containsAll(Arrays.asList("a", "A", "b", "c", "C")));

		assertEquals(6, _baseMap.values().size());
		assertTrue(_baseMap.values().containsAll(Arrays.asList("a", "A", "b", "c", "C")));
	}

	/**
	 * Test of isEmpty method, of class GenericCopyOnWriteMultiMap.
	 */
	@Test
	public void testIsEmpty()
	{
		assertFalse(_cowMap.isEmpty());
		_cowMap.clear();
		;
		assertTrue(_cowMap.isEmpty());
		assertFalse(_baseMap.isEmpty());
	}

	/**
	 * Test of containsKey method, of class GenericCopyOnWriteMultiMap.
	 */
	@Test
	public void testContainsKey()
	{
		assertTrue(_cowMap.containsKey("1"));
		assertTrue(_cowMap.containsKey("2"));
		assertTrue(_cowMap.containsKey("3"));
		assertFalse(_cowMap.containsKey("a"));
		assertFalse(_cowMap.containsKey("@"));
	}

	/**
	 * Test of putAll method, of class GenericCopyOnWriteMultiMap.
	 */
	@Test
	public void testPutAll_Map()
	{
		Map<String, String> newEntries = new HashMap<>();
		newEntries.put("1", "α");
		newEntries.put("2", "β");
		newEntries.put("3", "γ");

		_cowMap.putAll(newEntries);
		assertEquals(3, _cowMap.get("1").size());
		assertTrue(_cowMap.get("1").containsAll(Arrays.asList("a", "A", "α")));
		assertEquals(3, _cowMap.get("2").size());
		assertTrue(_cowMap.get("2").containsAll(Arrays.asList("b", "B", "β")));
		assertEquals(3, _cowMap.get("3").size());
		assertTrue(_cowMap.get("3").containsAll(Arrays.asList("c", "C", "γ")));

		assertEquals(Arrays.asList("a", "A"), _baseMap.get("1"));
		assertEquals(Arrays.asList("b", "B"), _baseMap.get("2"));
		assertEquals(Arrays.asList("c", "C"), _baseMap.get("3"));
	}

	/**
	 * Test of putAll method, of class GenericCopyOnWriteMultiMap.
	 */
	@Test
	public void testPutAll_MultiMap()
	{
		MultiMap<String, String> newEntries = _factory.getInstance();
		newEntries.put("1", "α");
		newEntries.put("1", "alpha");
		newEntries.put("2", "β");
		newEntries.put("2", "beta");
		newEntries.put("3", "γ");
		newEntries.put("3", "gamma");

		_cowMap.putAll(newEntries);
		assertEquals(4, _cowMap.get("1").size());
		assertTrue(_cowMap.get("1").containsAll(Arrays.asList("a", "A", "α", "alpha")));
		assertEquals(4, _cowMap.get("2").size());
		assertTrue(_cowMap.get("2").containsAll(Arrays.asList("b", "B", "β", "beta")));
		assertEquals(4, _cowMap.get("3").size());
		assertTrue(_cowMap.get("3").containsAll(Arrays.asList("c", "C", "γ", "gamma")));

		assertEquals(Arrays.asList("a", "A"), _baseMap.get("1"));
		assertEquals(Arrays.asList("b", "B"), _baseMap.get("2"));
		assertEquals(Arrays.asList("c", "C"), _baseMap.get("3"));
	}

	/**
	 * Test of putAll method, of class GenericCopyOnWriteMultiMap.
	 */
	@Test
	public void testPutAll_null_Collection()
	{
		_cowMap.putAll("1", Arrays.asList("α", "alpha"));
		assertEquals(4, _cowMap.get("1").size());
		assertTrue(_cowMap.get("1").containsAll(Arrays.asList("a", "A", "α", "alpha")));
		assertEquals(Arrays.asList("a", "A"), _baseMap.get("1"));
	}

	/**
	 * Test of iterator method, of class GenericCopyOnWriteMultiMap.
	 */
	@Test
	public void testIterator()
	{
		Iterator<String> iter = _cowMap.iterator("3");
		assertTrue(iter.hasNext());
		assertEquals("c", iter.next());
		assertEquals("C", iter.next());
		assertFalse(iter.hasNext());

		assertTrue(_cowMap.copy());
		iter = _cowMap.iterator("3");
		assertTrue(iter.hasNext());
		assertEquals("c", iter.next());
		iter.remove();
		assertEquals("C", iter.next());
		assertFalse(iter.hasNext());

		iter = _cowMap.iterator("3");
		assertTrue(iter.hasNext());
		assertEquals("C", iter.next());
		assertFalse(iter.hasNext());

		iter = _baseMap.iterator("3");
		assertTrue(iter.hasNext());
		assertEquals("c", iter.next());
		assertEquals("C", iter.next());
		assertFalse(iter.hasNext());
	}

	/**
	 * Test of clear method, of class GenericCopyOnWriteMultiMap.
	 */
	@Test
	public void testClear()
	{
		_cowMap.clear();
		assertTrue(_cowMap.isEmpty());
		assertTrue(_cowMap.keySet().isEmpty());
		assertTrue(_cowMap.values().isEmpty());
		// assertTrue(_cowMap.entrySet().isEmpty());
		assertFalse(_baseMap.isEmpty());
	}

	/**
	 * Test of keySet method, of class GenericCopyOnWriteMultiMap.
	 */
	@Test
	public void testKeySet()
	{
		assertEquals(3, _cowMap.keySet().size());
		assertTrue(_cowMap.keySet().containsAll(Arrays.asList("1", "2", "3")));

		assertTrue(_cowMap.keySet().remove("1"));
		assertEquals(2, _cowMap.keySet().size());
		assertTrue(_cowMap.keySet().containsAll(Arrays.asList("2", "3")));

		assertEquals(6, _baseMap.values().size());
		assertTrue(_baseMap.keySet().containsAll(Arrays.asList("1", "2", "3")));
	}

	/**
	 * Test of map method, of class GenericCopyOnWriteMultiMap.
	 */
	@Test(expected=UnsupportedOperationException.class)
	public void testMap()
	{
		/* this test will fail */
		assertNotNull(_cowMap.map());
	}

	/**
	 * Test of getDecoratee method, of class GenericCopyOnWriteMultiMap.
	 */
	@Test
	public void testGetDecoratee()
	{
		assertSame(_baseMap, _cowMap.getDecoratee());
		assertTrue(_cowMap.copy());
		assertNotSame(_baseMap, _cowMap.getDecoratee());
	}

	/**
	 * Test of entrySet method, of class GenericCopyOnWriteMultiMap.
	 */
	@Test
	public void testEntrySet()
	{
		@SuppressWarnings("unchecked")
		final List<DefaultMapEntry<String, Collection<String>>> referenceList = Arrays.asList(
			new DefaultMapEntry<String, Collection<String>>("1", Arrays.asList("a", "A")),
			new DefaultMapEntry<String, Collection<String>>("2", Arrays.asList("b", "B")),
			new DefaultMapEntry<String, Collection<String>>("3", Arrays.asList("c", "C")));
		assertEquals(3, _cowMap.entrySet().size());
		assertTrue(_cowMap.entrySet().containsAll(referenceList));

		/**
		 * This test is not part of the protocol, but tests the current implementation.
		 */
		boolean gotException = false;
		try {
			Iterator<Map.Entry<String, Collection<String>>> iter = _cowMap.entrySet().iterator();
			assertTrue(iter.hasNext());
			iter.next();
			iter.remove();
		} catch (UnsupportedOperationException ex) {
			gotException = true;
		}
		assertTrue(gotException);

		assertTrue(_cowMap.copy());
		Iterator<Map.Entry<String, Collection<String>>> iter = _cowMap.entrySet().iterator();
		assertTrue(iter.hasNext());
		iter.next();
		iter.remove();		
		assertEquals(2, _cowMap.entrySet().size());
		assertFalse(_cowMap.entrySet().containsAll(referenceList));

		assertEquals(3, _baseMap.entrySet().size());
		assertTrue(_baseMap.entrySet().containsAll(referenceList));

	}
}
