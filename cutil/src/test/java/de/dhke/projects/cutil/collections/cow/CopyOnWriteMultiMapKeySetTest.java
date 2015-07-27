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
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.collections15.MultiMap;
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
public class CopyOnWriteMultiMapKeySetTest
{
	private final IMultiMapFactory<String, String, MultiMap<String, String>> _factory = new MultiHashMapFactory<>();
	private MultiMap<String, String> _baseMap;
	private CopyOnWriteMultiMap<String, String> _cowMap;
	private Set<String> _keySet;

	public CopyOnWriteMultiMapKeySetTest()
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
		_keySet = _cowMap.keySet();
	}

	@After
	public void tearDown()
	{
		_baseMap = null;
		_cowMap = null;
		_keySet = null;
	}

	/**
	 * Test of size method, of class CopyOnWriteMultiMapKeySet.
	 */
	@Test
	public void testSize()
	{
		assertEquals(3, _keySet.size());
		_cowMap.remove("1");
		assertEquals(2, _keySet.size());
		_cowMap.remove("1", "a");
		assertEquals(2, _keySet.size());
	}

	/**
	 * Test of isEmpty method, of class CopyOnWriteMultiMapKeySet.
	 */
	@Test
	public void testIsEmpty()
	{
		assertFalse(_keySet.isEmpty());
		_cowMap.clear();
		assertTrue(_keySet.isEmpty());
	}

	/**
	 * Test of contains method, of class CopyOnWriteMultiMapKeySet.
	 */
	@Test
	public void testContains()
	{
		assertTrue(_keySet.contains("1"));
		assertTrue(_keySet.contains("2"));
		assertTrue(_keySet.contains("3"));
		assertFalse(_keySet.contains("4"));
	}

	/**
	 * Test of iterator method, of class CopyOnWriteMultiMapKeySet.
	 */
	@Test
	public void testIteratorCopyBefore()
	{
		Iterator<String> iter = _keySet.iterator();
		String[] referenceArray = {
			"1", "2", "3"
		};
		Arrays.sort(referenceArray);
		for (int i = 0; i < _keySet.size(); ++i) {
			assertTrue(iter.hasNext());
			assertTrue(Arrays.binarySearch(referenceArray, iter.next()) >= 0);
		}
		assertFalse(iter.hasNext());

		_cowMap.copy();
		iter = _keySet.iterator();
		String first = iter.next();
		iter.remove();
		assertFalse(_cowMap.containsKey(first));
	}


	/**
	 * Test of iterator method, of class CopyOnWriteMultiMapKeySet.
	 */
	@Test
	public void testIteratorNoCopyBefore()
	{
		Iterator<String> iter = _keySet.iterator();
		String[] referenceArray = {
			"1", "2", "3"
		};
		Arrays.sort(referenceArray);
		for (int i = 0; i < _keySet.size(); ++i) {
			assertTrue(iter.hasNext());
			assertTrue(Arrays.binarySearch(referenceArray, iter.next()) >= 0);
		}
		assertFalse(iter.hasNext());

		// _cowMap.copy();
		iter = _keySet.iterator();
		String first = iter.next();
		iter.remove();
		assertFalse(_cowMap.containsKey(first));
	}
	
	/**
	 * Test of toArray method, of class CopyOnWriteMultiMapKeySet.
	 */
	@Test
	public void testToArray_0args()
	{
		Object[] referenceArray = {
			"1", "2", "3"
		};
		Arrays.sort(referenceArray);
		Object[] returnedValue = _keySet.toArray();
		Arrays.sort(returnedValue);
		assertArrayEquals(referenceArray, returnedValue);
	}

	/**
	 * Test of toArray method, of class CopyOnWriteMultiMapKeySet.
	 */
	@Test
	public void testToArray_null()
	{
		String[] referenceArray = {
			"1", "2", "3"
		};
		Arrays.sort(referenceArray);
		String[] returnedValue = _keySet.toArray(new String[]{});
		Arrays.sort(returnedValue);
		assertArrayEquals(referenceArray, returnedValue);
	}

	/**
	 * Test of add method, of class CopyOnWriteMultiMapKeySet.
	 */
	@Test(expected=UnsupportedOperationException.class)
	public void testAdd()
	{
		_keySet.add("4");
	}

	/**
	 * Test of remove method, of class CopyOnWriteMultiMapKeySet.
	 */
	@Test
	public void testRemove()
	{
		_keySet.remove("2");
		assertFalse(_cowMap.containsKey("2"));
		assertTrue(_baseMap.containsKey("2"));
	}

	/**
	 * Test of containsAll method, of class CopyOnWriteMultiMapKeySet.
	 */
	@Test
	public void testContainsAll()
	{
		assertTrue(_keySet.containsAll(Arrays.asList("1", "2")));
	}

	/**
	 * Test of addAll method, of class CopyOnWriteMultiMapKeySet.
	 */
	@Test(expected=UnsupportedOperationException.class)
	public void testAddAll()
	{
		_keySet.addAll(Arrays.asList("4", "5"));
	}

	/**
	 * Test of retainAll method, of class CopyOnWriteMultiMapKeySet.
	 */
	@Test
	public void testRetainAll()
	{
		assertTrue(_keySet.retainAll(Arrays.asList("1", "2")));
		assertFalse(_keySet.retainAll(Arrays.asList("1", "2")));
		assertTrue(_cowMap.containsKey("1"));
		assertTrue(_cowMap.containsKey("2"));
		assertFalse(_cowMap.containsKey("3"));
		assertTrue(_baseMap.containsKey("1"));
		assertTrue(_baseMap.containsKey("2"));
		assertTrue(_baseMap.containsKey("3"));
	}

	/**
	 * Test of removeAll method, of class CopyOnWriteMultiMapKeySet.
	 */
	@Test
	public void testRemoveAll()
	{
		assertTrue(_keySet.removeAll(Arrays.asList("1", "2")));
		assertFalse(_keySet.removeAll(Arrays.asList("1", "2")));
		assertFalse(_cowMap.containsKey("1"));
		assertFalse(_cowMap.containsKey("2"));
		assertTrue(_cowMap.containsKey("3"));
		assertTrue(_baseMap.containsKey("1"));
		assertTrue(_baseMap.containsKey("2"));
		assertTrue(_baseMap.containsKey("3"));
	}

	/**
	 * Test of clear method, of class CopyOnWriteMultiMapKeySet.
	 */
	@Test
	public void testClear()
	{
		_keySet.clear();
		assertTrue(_cowMap.isEmpty());
		assertFalse(_baseMap.isEmpty());
	}
}
