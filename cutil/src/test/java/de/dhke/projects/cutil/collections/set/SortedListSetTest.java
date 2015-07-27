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
package de.dhke.projects.cutil.collections.set;

import java.util.Arrays;
import org.junit.*;
import static org.junit.Assert.*;


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class SortedListSetTest
{
	private SortedListSet<String> _set;
	
	public SortedListSetTest()
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
		_set = new SortedListSet<>();
	}
	
	@After
	public void tearDown()
	{
		_set = null;
	}

	/**
	 * Test of get method, of class SortedListSet.
	 */
	@Test
	public void testGet()
	{
	}

	/**
	 * Test of size method, of class SortedListSet.
	 */
	@Test
	public void testSize()
	{
	}

	/**
	 * Test of add method, of class SortedListSet.
	 */
	@Test
	public void testAdd()
	{
		_set.add("D");
		_set.add("B");
		_set.add("A");
		assertEquals(3, _set.size());
		assertArrayEquals(new String[]{"A", "B", "D"}, _set.toArray(new String[]{}));
		
		_set.add("C");
		assertEquals(4, _set.size());
		assertArrayEquals(new String[]{"A", "B", "C", "D"}, _set.toArray(new String[]{}));
	}

	/**
	 * Test of addAll method, of class SortedListSet.
	 */
	@Test
	public void testAddAll()
	{
		_set.addAll(Arrays.asList("D", "A"));
		assertEquals(2, _set.size());
		assertArrayEquals(new String[]{"A", "D"}, _set.toArray(new String[]{}));

		_set.addAll(Arrays.asList("C", "B"));
		assertEquals(4, _set.size());
		assertArrayEquals(new String[]{"A", "B", "C", "D"}, _set.toArray(new String[]{}));
	}
	
	@Test
	public void testRemove_Object()
	{
		_set.add("A");
		_set.add("B");
		_set.add("C");
		_set.add("D");
		
		assertEquals(4, _set.size());
		assertArrayEquals(new String[]{"A", "B", "C", "D"}, _set.toArray(new String[]{}));

		boolean wasRemoved = _set.remove("C");
		assertTrue(wasRemoved);

		assertEquals(3,  _set.size());
		assertArrayEquals(new String[]{"A", "B", "D"}, _set.toArray(new String[]{}));				
	}
	
	@Test
	public void testRemove_int()
	{
		_set.add("A");
		_set.add("B");
		_set.add("C");
		_set.add("D");
		
		assertEquals(4, _set.size());
		assertArrayEquals(new String[]{"A", "B", "C", "D"}, _set.toArray(new String[]{}));

		String removedObj = _set.remove(2);
		assertEquals("C", removedObj);

		assertEquals(3,  _set.size());
		assertArrayEquals(new String[]{"A", "B", "D"}, _set.toArray(new String[]{}));
	}
	
	@Test
	public void testRemoveAll()
	{
		_set.add("A");
		_set.add("B");
		_set.add("C");
		_set.add("D");
		
		_set.removeAll(Arrays.asList("A", "C"));;
		assertEquals(2, _set.size());
		assertArrayEquals(new String[]{"B", "D"}, _set.toArray(new String[]{}));
	}
	
	public void testRetainAll()
	{
		_set.add("A");
		_set.add("B");
		_set.add("C");
		_set.add("D");
		
		_set.retainAll(Arrays.asList("A", "C"));;
		assertEquals(2, _set.size());
		assertArrayEquals(new String[]{"A", "C"}, _set.toArray(new String[]{}));		
	}
}
