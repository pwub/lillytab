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
package de.dhke.projects.cutil.collections.map;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author peterw
 */
public class LinkedMultiMapTest {

	private LinkedMultiMap<String, Integer> _linkedMap;


	public LinkedMultiMapTest()
	{
	}


	@BeforeClass
	public static void setUpClass()
	{
	}


	@AfterClass
	public static void tearDownClass()
	{
	}


	@Before
	public void setUp()
	{
		_linkedMap = new LinkedMultiMap<>();
	}


	@After
	public void tearDown()
	{
		_linkedMap = null;
	}


	@Test
	public void testForwardAdd()
	{
		_linkedMap.getForwardMap().put("A", Integer.valueOf(1));
		assertTrue(_linkedMap.getReverseMap().containsValue(Integer.valueOf(1), "A"));
		assertFalse(_linkedMap.getReverseMap().containsValue(Integer.valueOf(2), "B"));

		_linkedMap.getForwardMap().put("B", Integer.valueOf(2));
		assertTrue(_linkedMap.getReverseMap().containsValue(Integer.valueOf(1), "A"));
		assertTrue(_linkedMap.getReverseMap().containsValue(Integer.valueOf(2), "B"));
	}


	@Test
	public void testForwardRemove()
	{
		_linkedMap.getForwardMap().put("A", Integer.valueOf(1));
		_linkedMap.getForwardMap().put("B", Integer.valueOf(2));
		assertTrue(_linkedMap.getReverseMap().containsValue(Integer.valueOf(1), "A"));
		assertTrue(_linkedMap.getReverseMap().containsValue(Integer.valueOf(2), "B"));

		assertEquals(Integer.valueOf(1), _linkedMap.getForwardMap().remove("A", Integer.valueOf(1)));
		assertFalse(_linkedMap.getReverseMap().containsValue(Integer.valueOf(1), "A"));
		assertTrue(_linkedMap.getReverseMap().containsValue(Integer.valueOf(2), "B"));
	}


	@Test
	public void testForwardClear()
	{
		_linkedMap.getForwardMap().put("A", Integer.valueOf(1));
		_linkedMap.getForwardMap().put("B", Integer.valueOf(2));
		assertTrue(_linkedMap.getReverseMap().containsValue(Integer.valueOf(1), "A"));
		assertTrue(_linkedMap.getReverseMap().containsValue(Integer.valueOf(2), "B"));

		_linkedMap.getForwardMap().clear();
		assertFalse(_linkedMap.getReverseMap().containsValue(Integer.valueOf(1), "A"));
		assertFalse(_linkedMap.getReverseMap().containsValue(Integer.valueOf(2), "B"));
	}


	@Test
	public void testBackwardAdd()
	{
		_linkedMap.getReverseMap().put(Integer.valueOf(1), "A");
		assertTrue(_linkedMap.getReverseMap().containsValue(Integer.valueOf(1), "A"));
		assertFalse(_linkedMap.getReverseMap().containsValue(Integer.valueOf(2), "B"));

		_linkedMap.getReverseMap().put(Integer.valueOf(2), "B");
		assertTrue(_linkedMap.getReverseMap().containsValue(Integer.valueOf(1), "A"));
		assertTrue(_linkedMap.getReverseMap().containsValue(Integer.valueOf(2), "B"));
	}


	@Test
	public void testBackwardRemove()
	{
		_linkedMap.getReverseMap().put(Integer.valueOf(1), "A");
		_linkedMap.getReverseMap().put(Integer.valueOf(2), "B");
		assertTrue(_linkedMap.getForwardMap().containsValue("A", Integer.valueOf(1)));
		assertTrue(_linkedMap.getForwardMap().containsValue("B", Integer.valueOf(2)));

		assertEquals(Integer.valueOf(1), _linkedMap.getForwardMap().remove("A", Integer.valueOf(1)));
		assertFalse(_linkedMap.getForwardMap().containsValue("A", Integer.valueOf(1)));
		assertTrue(_linkedMap.getForwardMap().containsValue("B", Integer.valueOf(2)));
	}


	@Test
	public void testBackwardClear()
	{
		_linkedMap.getReverseMap().put(Integer.valueOf(1), "A");
		_linkedMap.getReverseMap().put(Integer.valueOf(2), "B");
		assertTrue(_linkedMap.getForwardMap().containsValue("A", Integer.valueOf(1)));
		assertTrue(_linkedMap.getForwardMap().containsValue("B", Integer.valueOf(2)));

		_linkedMap.getReverseMap().clear();
		assertFalse(_linkedMap.getForwardMap().containsValue("A", Integer.valueOf(1)));
		assertFalse(_linkedMap.getForwardMap().containsValue("B", Integer.valueOf(2)));
	}
}