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
import java.util.Iterator;
import org.apache.commons.collections15.MultiMap;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class CopyOnWriteMultiMapValueCollectionTest
{
	private final IMultiMapFactory<String, String, MultiMap<String, String>> _factory = new MultiHashMapFactory<>();
	private MultiMap<String, String> _baseMap;
	private CopyOnWriteMultiMap<String, String> _cowMap;
	private Collection<String> _valueCollection;

	public CopyOnWriteMultiMapValueCollectionTest()
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
		_valueCollection = _cowMap.values();
	}

	@After
	public void tearDown()
	{
		_baseMap = null;
		_cowMap = null;
		_valueCollection = null;
	}

	/**
	 * Test of size method, of class CopyOnWriteMultiMapValueCollection.
	 */
	@Test
	public void testSize()
	{
		assertEquals(6, _valueCollection.size());
	}

	/**
	 * Test of isEmpty method, of class CopyOnWriteMultiMapValueCollection.
	 */
	@Test
	public void testIsEmpty()
	{
		_cowMap.clear();
		assertTrue(_valueCollection.isEmpty());
	}

	/**
	 * Test of contains method, of class CopyOnWriteMultiMapValueCollection.
	 */
	@Test
	public void testContains()
	{
		assertTrue(_valueCollection.contains("a"));
		assertTrue(_valueCollection.contains("A"));
		assertTrue(_valueCollection.contains("b"));
		assertTrue(_valueCollection.contains("B"));
		assertTrue(_valueCollection.contains("c"));
		assertTrue(_valueCollection.contains("C"));
		assertFalse(_valueCollection.contains("d"));
		assertFalse(_valueCollection.contains("D"));
	}

	/**
	 * Test of iterator method, of class CopyOnWriteMultiMapValueCollection.
	 */
	@Test
	public void testIterator()
	{
		Iterator<String> iter = _valueCollection.iterator();
		String[] referenceArray = {
			"a", "A", "b", "B", "c", "C"
		};
		Arrays.sort(referenceArray);
		for (int i = 0; i < _valueCollection.size(); ++i) {
			assertTrue(iter.hasNext());
			assertTrue(Arrays.binarySearch(referenceArray, iter.next()) >= 0);
		}
		assertFalse(iter.hasNext());

		_cowMap.copy();
		iter = _valueCollection.iterator();
		String first = iter.next();
		iter.remove();
		assertFalse(_cowMap.containsValue(first));
	}

	/**
	 * Test of toArray method, of class CopyOnWriteMultiMapValueCollection.
	 */
	@Test
	public void testToArray_0args()
	{
		Object[] referenceArray = {
			"a", "A", "b", "B", "c", "C"
		};
		Arrays.sort(referenceArray);
		Object[] returnedValue = _valueCollection.toArray();
		Arrays.sort(returnedValue);
		assertArrayEquals(referenceArray, returnedValue);
	}

	/**
	 * Test of toArray method, of class CopyOnWriteMultiMapValueCollection.
	 */
	@Test
	public void testToArray_null()
	{
		String[] referenceArray = {
			"a", "A", "b", "B", "c", "C"
		};
		Arrays.sort(referenceArray);
		String[] returnedValue = _valueCollection.toArray(new String[]{});
		Arrays.sort(returnedValue);
		assertArrayEquals(referenceArray, returnedValue);
	}

	/**
	 * Test of add method, of class CopyOnWriteMultiMapValueCollection.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testAdd()
	{
		_valueCollection.add("d");
	}

	/**
	 * Test of remove method, of class CopyOnWriteMultiMapValueCollection.
	 */
	@Test
	public void testRemove()
	{
		String[] referenceArray = {
			"a", "A", "b", "B", "C"
		};
		Arrays.sort(referenceArray);
		_valueCollection.remove("c");
		String[] returnedValue = _valueCollection.toArray(new String[]{});
		Arrays.sort(returnedValue);
		assertArrayEquals(referenceArray, returnedValue);
		assertFalse(_cowMap.containsValue("3", "c"));
		assertTrue(_cowMap.containsValue("3", "C"));
	}

	/**
	 * Test of containsAll method, of class CopyOnWriteMultiMapValueCollection.
	 */
	@Test
	public void testContainsAll()
	{
		String[] referenceArray = {
			"a", "A", "b", "B", "c", "C"
		};
		assertTrue(_valueCollection.containsAll(Arrays.asList(referenceArray)));
		String[] falseReferenceArray = {
			"a", "A", "b", "B", "c", "C", "d", "D"
		};
		assertFalse(_valueCollection.containsAll(Arrays.asList(falseReferenceArray)));
	}

	/**
	 * Test of addAll method, of class CopyOnWriteMultiMapValueCollection.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testAddAll()
	{
		_valueCollection.addAll(Arrays.asList("d", "D"));
	}

	/**
	 * Test of retainAll method, of class CopyOnWriteMultiMapValueCollection.
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testRetainAll()
	{
		String[] referenceArray = {
			"a", "A", "b", "B"
		};
		assertTrue(_valueCollection.retainAll(Arrays.asList(referenceArray)));
		assertTrue(_valueCollection.containsAll(Arrays.asList(referenceArray)));
		assertEquals(4, _valueCollection.size());
	}

	/**
	 * Test of removeAll method, of class CopyOnWriteMultiMapValueCollection.
	 */
	@Test
	public void testRemoveAll()
	{
		String[] referenceArray = {
			"a", "A", "b", "B"
		};
		assertTrue(_valueCollection.removeAll(Arrays.asList("c", "C")));
		assertTrue(_valueCollection.containsAll(Arrays.asList(referenceArray)));
		assertEquals(4, _valueCollection.size());
	}

	/**
	 * Test of clear method, of class CopyOnWriteMultiMapValueCollection.
	 */
	@Test
	public void testClear()
	{
		_valueCollection.clear();
		_cowMap.clear();
		assertEquals(0, _valueCollection.size());
	}
}
