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
package de.dhke.projects.cutil.collections;

import java.util.Collection;
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class MultiMapUtilTest {
	
	public MultiMapUtilTest()
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
	}
	

	@After
	public void tearDown()
	{
	}


	/**
	 * Test of deepEquals method, of class MultiMapUtil.
	 */
	@Test
	public void testDeepEquals()
	{

	}


	/**
	 * Test of deepHashCode method, of class MultiMapUtil.
	 */
	@Test
	public void testDeepHashCode_MapEntry()
	{

	}


	/**
	 * Test of deepHashCode method, of class MultiMapUtil.
	 */
	@Test
	public void testDeepHashCode_MultiMap()
	{
	}


	/**
	 * Test of getTransitive method, of class MapUtil.
	 */
	@Test
	public void testGetTransitive_MultiMap_GenericType()
	{
		MultiMap<String, String> aMultiMap = new MultiHashMap<>();
		aMultiMap.put("A", "B");
		aMultiMap.put("B", "C");
		Collection<String> result = MultiMapUtil.getTransitive(aMultiMap, "A");
		assertEquals(2, result.size());
		assertTrue(result.contains("B"));
		assertTrue(result.contains("C"));
		
		aMultiMap.put("C", "D");
		result = MultiMapUtil.getTransitive(aMultiMap, "A");
		assertEquals(3, result.size());
		assertTrue(result.contains("B"));
		assertTrue(result.contains("C"));
		assertTrue(result.contains("D"));
		
		aMultiMap.put("A", "E");
		result = MultiMapUtil.getTransitive(aMultiMap, "A");
		assertEquals(4, result.size());
		assertTrue(result.contains("B"));
		assertTrue(result.contains("C"));
		assertTrue(result.contains("D"));
		assertTrue(result.contains("E"));
		
		aMultiMap.put("C", "F");
		result = MultiMapUtil.getTransitive(aMultiMap, "A");
		assertEquals(5, result.size());
		assertTrue(result.contains("B"));
		assertTrue(result.contains("C"));
		assertTrue(result.contains("D"));
		assertTrue(result.contains("E"));
		assertTrue(result.contains("F"));

		aMultiMap.put("C", "A");
		result = MultiMapUtil.getTransitive(aMultiMap, "A");
		assertEquals(6, result.size());
		assertTrue(result.contains("A"));
		assertTrue(result.contains("B"));
		assertTrue(result.contains("C"));
		assertTrue(result.contains("D"));
		assertTrue(result.contains("E"));
		assertTrue(result.contains("F"));
	
	}
}
