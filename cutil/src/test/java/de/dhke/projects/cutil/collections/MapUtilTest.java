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
import java.util.HashMap;
import java.util.Map;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class MapUtilTest {
	
	public MapUtilTest()
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
	 * Test of deepEquals method, of class MapUtil.
	 */
	@Test
	public void testDeepEquals()
	{
		Map<String, String> aMap = new HashMap<>();
		Map<String, String> bMap = new HashMap<>();
		aMap.put("A", "a");
		assertFalse(MapUtil.deepEquals(aMap, bMap));
		bMap.put("A", "a");
		assertTrue(MapUtil.deepEquals(aMap, bMap));
		bMap.put("B", "b");
		assertFalse(MapUtil.deepEquals(aMap, bMap));
		aMap.put("B", "b");
		assertTrue(MapUtil.deepEquals(aMap, bMap));		
	}


	/**
	 * Test of getTransitive method, of class MapUtil.
	 */
	@Test
	public void testGetTransitive_Map_GenericType()
	{
		Map<String, String> aMap = new HashMap<>();
		aMap.put("A", "B");
		aMap.put("B", "C");
		
		Collection<String> result = MapUtil.getTransitive(aMap, "A");
		assertEquals(2, result.size());
		assertTrue(result.contains("B"));
		assertTrue(result.contains("C"));
		aMap.put("C", "D");
		
		result = MapUtil.getTransitive(aMap, "A");
		assertEquals(3, result.size());
		assertTrue(result.contains("B"));
		assertTrue(result.contains("C"));
		assertTrue(result.contains("D"));
	}
}
