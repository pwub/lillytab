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

import java.util.HashMap;
import java.util.Map;
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
public class AspectMapTest {
	private Map<String, String> _baseMap;
	private AspectMap<String, String, Map<String, String>> _aspectMap;
	private AspectCollectionHistoryListener<Map.Entry<String, String>, Map<String, String>> _listener;

    public AspectMapTest() {
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
		_baseMap = new HashMap<>();
		_baseMap.put("A", "1");
		_baseMap.put("B", "2");
		_baseMap.put("C", "3");
		_baseMap.put("D", "4");

		_aspectMap = AspectMap.decorate(_baseMap, this);
		_listener = new AspectCollectionHistoryListener<>();
		_aspectMap.getListeners().add(_listener);
    }

    @After
    public void tearDown() {
		_listener = null;
		_aspectMap = null;
		_baseMap = null;
    }

	/**
	 * Test of getDecoratee method, of class AspectMap.
	 */
	@Test
	public void testGetDecoratee()
	{
		assertSame(_baseMap, _aspectMap.getDecoratee());
	}

	/**
	 * Test of size method, of class AspectMap.
	 */
	@Test
	public void testSize()
	{
		assertEquals(4, _aspectMap.size());
	}

	/**
	 * Test of isEmpty method, of class AspectMap.
	 */
	@Test
	public void testIsEmpty()
	{
		assertFalse(_aspectMap.isEmpty());
		_baseMap.clear();;
		assertTrue(_aspectMap.isEmpty());
	}

	/**
	 * Test of containsKey method, of class AspectMap.
	 */
	@Test
	public void testContainsKey()
	{
		assertFalse(_aspectMap.containsKey("_"));
		assertTrue(_aspectMap.containsKey("A"));
		assertTrue(_aspectMap.containsKey("B"));
		assertTrue(_aspectMap.containsKey("C"));
		assertTrue(_aspectMap.containsKey("D"));
		assertFalse(_aspectMap.containsKey("E"));
	}

	/**
	 * Test of containsValue method, of class AspectMap.
	 */
	@Test
	public void testContainsValue()
	{
		assertFalse(_aspectMap.containsValue("0"));
		assertTrue(_aspectMap.containsValue("1"));
		assertTrue(_aspectMap.containsValue("2"));
		assertTrue(_aspectMap.containsValue("3"));
		assertTrue(_aspectMap.containsValue("4"));
		assertFalse(_aspectMap.containsValue("5"));
	}

	/**
	 * Test of get method, of class AspectMap.
	 */
	@Test
	public void testGet()
	{
		assertNull(_aspectMap.get("_"));
		assertEquals("1", _aspectMap.get("A"));
		assertEquals("2", _aspectMap.get("B"));
		assertEquals("3", _aspectMap.get("C"));
		assertEquals("4", _aspectMap.get("D"));
		assertNull(_aspectMap.get("E"));
	}

	/**
	 * Test of put method, of class AspectMap.
	 */
	@Test
	public void testPut()
	{
		assertEquals("1", _aspectMap.put("A", "1"));
		assertEquals(4, _aspectMap.size());
		assertEquals(1, _listener.beforeReplaceEvents.size());
		assertEquals(new DefaultMapEntry<>("A", "1"), _listener.beforeReplaceEvents.get(0).getItem());
		assertEquals(new DefaultMapEntry<>("A", "1"), _listener.beforeReplaceEvents.get(0).getNewItem());
		assertEquals(1, _listener.afterReplaceEvents.size());
		assertEquals(new DefaultMapEntry<>("A", "1"), _listener.afterReplaceEvents.get(0).getItem());
		assertEquals(new DefaultMapEntry<>("A", "1"), _listener.afterReplaceEvents.get(0).getNewItem());
		_aspectMap.put("E", "5");
		assertEquals("5", _baseMap.get("E"));
		/* no replace */
		assertEquals(1, _listener.beforeReplaceEvents.size());
		assertEquals(1, _listener.afterReplaceEvents.size());

		assertEquals(1, _listener.beforeAddEvents.size());
		assertEquals(new DefaultMapEntry<>("E", "5"), _listener.beforeAddEvents.get(0).getItem());
		assertEquals(1, _listener.afterAddEvents.size());
		assertEquals(new DefaultMapEntry<>("E", "5"), _listener.afterAddEvents.get(0).getItem());

		assertEquals("5", _aspectMap.get("E"));
	}

	/**
	 * Test of remove method, of class AspectMap.
	 */
	@Test
	public void testRemove()
	{
		assertFalse(_aspectMap.containsKey("_"));
		assertTrue(_aspectMap.containsKey("A"));

		assertNull(_aspectMap.remove("_"));
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertEquals("1", _aspectMap.remove("A"));
		assertEquals(1, _listener.beforeRemoveEvents.size());
		assertEquals(new DefaultMapEntry<>("A", "1"), _listener.beforeRemoveEvents.get(0).getItem());
		assertEquals(1, _listener.afterRemoveEvents.size());
		assertEquals(new DefaultMapEntry<>("A", "1"), _listener.afterRemoveEvents.get(0).getItem());

		assertFalse(_aspectMap.containsKey("_"));
		assertFalse(_aspectMap.containsKey("A"));	
	}

	/**
	 * Test of putAll method, of class AspectMap.
	 */
	@Test
	public void testPutAll()
	{
		assertTrue(_aspectMap.containsKey("A"));
		assertFalse(_aspectMap.containsKey("E"));

		Map<String, String> addMap = new HashMap<>();
		addMap.put("A", "1");
		addMap.put("E", "5");
		_aspectMap.putAll(addMap);
		assertEquals(1, _listener.beforeReplaceEvents.size());
		assertEquals(new DefaultMapEntry<>("A", "1"), _listener.beforeReplaceEvents.get(0).getItem());
		assertEquals(1, _listener.beforeAddEvents.size());
		assertEquals(new DefaultMapEntry<>("E", "5"), _listener.beforeAddEvents.get(0).getItem());
		assertEquals(1, _listener.afterReplaceEvents.size());
		assertEquals(new DefaultMapEntry<>("A", "1"), _listener.afterReplaceEvents.get(0).getItem());
		assertEquals(1, _listener.afterAddEvents.size());
		assertEquals(new DefaultMapEntry<>("E", "5"), _listener.afterAddEvents.get(0).getItem());
	}

	/**
	 * Test of clear method, of class AspectMap.
	 */
	@Test
	public void testClear()
	{
		assertFalse(_aspectMap.isEmpty());
		_aspectMap.clear();
		assertTrue(_aspectMap.isEmpty());
		assertTrue(_baseMap.isEmpty());
		assertEquals(1, _listener.beforeClearEvents.size());
		assertEquals(1, _listener.afterClearEvents.size());

		/* second clear does nothing */
		_aspectMap.clear();
		assertEquals(1, _listener.beforeClearEvents.size());
		assertEquals(1, _listener.afterClearEvents.size());
	}


	/**
	 * Test of clear method, of class AspectMap.
	 */
	@Test
	public void testClear_veto()
	{
		_listener.vetoClear = true;

		assertFalse(_aspectMap.isEmpty());
		try {
			_aspectMap.clear();
			fail("Clear veto not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertFalse(_aspectMap.isEmpty());
		assertFalse(_baseMap.isEmpty());

		_baseMap.clear();
		/* this does nothing */
		_aspectMap.clear();

		assertTrue(_listener.beforeClearEvents.isEmpty());
		assertTrue(_listener.afterClearEvents.isEmpty());
	}
}
