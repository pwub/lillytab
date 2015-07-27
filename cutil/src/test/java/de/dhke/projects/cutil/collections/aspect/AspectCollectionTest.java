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
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
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
public class AspectCollectionTest
{
	private Collection<Integer> _baseList;
	private AspectCollection<Integer, Collection<Integer>> _aspectCollection;
	private AspectCollectionHistoryListener<Integer, Collection<Integer>> _listener;


	public AspectCollectionTest()
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
		_baseList = new ArrayList<>();
		_aspectCollection = AspectCollection.decorate(_baseList, this);

		_baseList.add(1);
		_baseList.add(2);
		_baseList.add(3);
		_baseList.add(4);

		_listener = new AspectCollectionHistoryListener<>();
		_aspectCollection.getListeners().add(_listener);
	}


	@After
	public void tearDown()
	{
		_listener = null;
		_aspectCollection = null;
		_baseList = null;
	}


	/**
	 * Test of decorate method, of class AspectList.
	 */
	@Test
	public void testDecorate()
	{
		AspectCollection<Integer, Collection<Integer>> aspectList = AspectCollection.decorate(_baseList, this);
		assertNotSame(_baseList, aspectList);
		assertTrue(aspectList instanceof AspectCollection);
	}


	/**
	 * Test of size method, of class AspectList.
	 */
	@Test
	public void testSize()
	{
		assertEquals(4, _aspectCollection.size());
		assertTrue(_aspectCollection.remove(2));
		assertEquals(3, _aspectCollection.size());
		assertEquals(3, _baseList.size());
	}


	/**
	 * Test of isEmpty method, of class AspectList.
	 */
	@Test
	public void testIsEmpty()
	{
		assertFalse(_aspectCollection.isEmpty());
		assertFalse(_aspectCollection.isEmpty());
		_aspectCollection.clear();
		assertTrue(_aspectCollection.isEmpty());
		assertTrue(_baseList.isEmpty());
	}


	/**
	 * Test of contains method, of class AspectList.
	 */
	@Test
	public void testContains()
	{
		assertTrue(_aspectCollection.contains(1));
		assertTrue(_aspectCollection.contains(2));
		assertTrue(_aspectCollection.contains(3));
		assertTrue(_aspectCollection.contains(4));
		assertFalse(_aspectCollection.contains(5));
		assertTrue(_baseList.contains(1));
		assertTrue(_baseList.contains(2));
		assertTrue(_baseList.contains(3));
		assertTrue(_baseList.contains(4));
		assertFalse(_baseList.contains(5));
	}


	/**
	 * Test of iterator method, of class AspectList.
	 */
	@Test
	public void testIterator()
	{
		Iterator<Integer> iter = _aspectCollection.iterator();
		assertEquals(Integer.valueOf(1), iter.next());
		assertEquals(Integer.valueOf(2), iter.next());
		assertEquals(Integer.valueOf(3), iter.next());
		assertEquals(Integer.valueOf(4), iter.next());

		iter = _aspectCollection.iterator();
	}


	/**
	 * Test of iterator method, of class AspectList.
	 */
	@Test
	public void testIteratorRemoveVeto()
	{
		Iterator<Integer> iter = _aspectCollection.iterator();
		assertEquals(Integer.valueOf(1), iter.next());

		try {
			_listener.vetoRemove = true;
			iter.remove();
			fail("Veto exception not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_aspectCollection.contains(1));
	}


	/**
	 * Test of toArray method, of class AspectList.
	 */
	@Test
	public void testToArray_0args()
	{
		assertArrayEquals(new Integer[]{1, 2, 3, 4}, _aspectCollection.toArray());
	}


	/**
	 * Test of toArray method, of class AspectList.
	 */
	@Test
	public void testToArray_GenericType()
	{
		assertArrayEquals(new Integer[]{1, 2, 3, 4}, _aspectCollection.toArray(new Integer[_aspectCollection.size()]));
	}


	/**
	 * Test of add method, of class AspectList.
	 */
	@Test
	public void testAdd()
	{
		_aspectCollection.add(5);
		assertEquals(1, _listener.beforeAddEvents.size());
		assertEquals(Integer.valueOf(5), _listener.beforeAddEvents.get(0).getItem());
		assertEquals(1, _listener.afterAddEvents.size());
		assertEquals(Integer.valueOf(5), _listener.afterAddEvents.get(0).getItem());
		_aspectCollection.add(2);
		assertEquals(2, _listener.beforeAddEvents.size());
		assertEquals(Integer.valueOf(2), _listener.beforeAddEvents.get(1).getItem());
		assertEquals(2, _listener.afterAddEvents.size());
		assertEquals(Integer.valueOf(2), _listener.afterAddEvents.get(1).getItem());
	}


	@Test
	public void testAddVeto()
	{
		try {
			_listener.vetoAdd = true;
			_aspectCollection.add(5);
			fail("Veto exception not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertFalse(_aspectCollection.contains(5));
	}


	/**
	 * Test of remove method, of class AspectList.
	 */
	@Test
	public void testRemove()
	{
		assertFalse(_aspectCollection.remove(7));
		assertTrue(_aspectCollection.remove(3));
		assertEquals(1, _listener.beforeRemoveEvents.size());
		assertEquals(Integer.valueOf(3), _listener.beforeRemoveEvents.get(0).getItem());
		assertEquals(1, _listener.afterRemoveEvents.size());
		assertEquals(Integer.valueOf(3), _listener.afterRemoveEvents.get(0).getItem());
	}


	/**
	 * Test of remove method, of class AspectList.
	 */
	@Test
	public void testRemoveVeto()
	{
		try {
			_listener.vetoRemove = true;
			_aspectCollection.remove(3);
			fail("Veto exception not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_aspectCollection.contains(3));
	}


	/**
	 * Test of containsAll method, of class AspectList.
	 */
	@Test
	public void testContainsAll()
	{
		assertTrue(_aspectCollection.containsAll(Arrays.asList(1, 2, 3, 4)));
		assertFalse(_aspectCollection.containsAll(Arrays.asList(1, 2, 3, 4, 5)));
	}


	/**
	 * Test of addAll method, of class AspectList.
	 */
	@Test
	public void testAddAll()
	{
		assertTrue(_aspectCollection.addAll(Arrays.asList(5, 6)));
		assertTrue(_aspectCollection.addAll(Arrays.asList(6, 7)));
		assertEquals(4, _listener.beforeAddEvents.size());
		assertEquals(Integer.valueOf(5), _listener.beforeAddEvents.get(0).getItem());
		assertEquals(Integer.valueOf(6), _listener.beforeAddEvents.get(1).getItem());
		assertEquals(Integer.valueOf(6), _listener.beforeAddEvents.get(2).getItem());
		assertEquals(Integer.valueOf(7), _listener.beforeAddEvents.get(3).getItem());
		assertEquals(4, _listener.afterAddEvents.size());
		assertEquals(Integer.valueOf(5), _listener.afterAddEvents.get(0).getItem());
		assertEquals(Integer.valueOf(6), _listener.afterAddEvents.get(1).getItem());
		assertEquals(Integer.valueOf(6), _listener.afterAddEvents.get(2).getItem());
		assertEquals(Integer.valueOf(7), _listener.beforeAddEvents.get(3).getItem());
		assertTrue(_aspectCollection.contains(5));
		assertTrue(_aspectCollection.contains(6));
		assertTrue(_aspectCollection.contains(7));
		assertFalse(_aspectCollection.contains(8));
	}


	@Test
	public void testAddAllVeto()
	{
		_listener.vetoAdd = true;
		try {
			_aspectCollection.addAll(Arrays.asList(5, 6));
			fail("Veto exception not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_listener.beforeAddEvents.isEmpty());
		assertTrue(_listener.afterAddEvents.isEmpty());
		assertFalse(_aspectCollection.contains(5));
		assertFalse(_aspectCollection.contains(6));
		assertFalse(_aspectCollection.contains(7));
	}


	/**
	 * Test of removeAll method, of class AspectList.
	 */
	@Test
	public void testRemoveAll()
	{
		assertFalse(_aspectCollection.removeAll(Arrays.asList(5, 6)));
		_aspectCollection.removeAll(Arrays.asList(2, 4));
		assertFalse(_aspectCollection.contains(2));
		assertFalse(_aspectCollection.contains(4));
		assertEquals(4, _listener.beforeRemoveEvents.size());
		assertEquals(Integer.valueOf(5), _listener.beforeRemoveEvents.get(0).getItem());
		assertEquals(Integer.valueOf(6), _listener.beforeRemoveEvents.get(1).getItem());
		assertEquals(Integer.valueOf(2), _listener.beforeRemoveEvents.get(2).getItem());
		assertEquals(Integer.valueOf(4), _listener.beforeRemoveEvents.get(3).getItem());
		assertEquals(2, _listener.afterRemoveEvents.size());
		assertEquals(Integer.valueOf(2), _listener.afterRemoveEvents.get(0).getItem());
		assertEquals(Integer.valueOf(4), _listener.afterRemoveEvents.get(1).getItem());
	}


	@Test
	public void testRemoveAllVeto()
	{
		_listener.vetoRemove = true;
		try {
			_aspectCollection.removeAll(Arrays.asList(2, 4));
			fail("Veto exception not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_aspectCollection.contains(2));
		assertTrue(_aspectCollection.contains(4));
		assertTrue(_listener.beforeRemoveEvents.isEmpty());
		assertTrue(_listener.afterRemoveEvents.isEmpty());
	}


	/**
	 * Test of retainAll method, of class AspectList.
	 */
	@Test
	public void testRetainAll()
	{
		assertFalse(_aspectCollection.retainAll(Arrays.asList(1, 2, 3, 4)));
		assertTrue(_aspectCollection.retainAll(Arrays.asList(2, 3)));
		assertFalse(_aspectCollection.contains(1));
		assertFalse(_aspectCollection.contains(4));
	}

	/**
	 * Test of retainAll method, of class AspectList.
	 */
	@Test
	public void testRetainAllVeto()
	{
		_listener.vetoRemove = false;
		_aspectCollection.retainAll(Arrays.asList(1, 2, 3, 4));
		_listener.vetoRemove = true;
		try {
			assertTrue(_aspectCollection.retainAll(Arrays.asList(2, 3)));
			fail("Veto exception not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertTrue(_aspectCollection.contains(1));
		assertTrue(_aspectCollection.contains(4));
	}

	/**
	 * Test of clear method, of class AspectList.
	 */
	@Test
	public void testClear()
	{
		_aspectCollection.clear();
		assertTrue(_aspectCollection.isEmpty());
		assertEquals(1, _listener.beforeClearEvents.size());
		assertEquals(1, _listener.afterClearEvents.size());

		_aspectCollection.clear();
		assertEquals(1, _listener.beforeClearEvents.size());
		assertEquals(1, _listener.afterClearEvents.size());
	}

	/**
	 * Test of clear method, of class AspectList.
	 */
	@Test
	public void testClear_veto()
	{
		_listener.vetoClear = true;
		try {
			_aspectCollection.clear();
			fail("Veto exception not raised");
		} catch (AssertionError ex) {
			/* ignore */
		}
		assertFalse(_aspectCollection.isEmpty());

		_baseList.clear();
		_aspectCollection.clear();
	}

	/**
	 * Test of getDecoratee method, of class AspectList.
	 */
	@Test
	public void testGetDecoratee()
	{
		assertSame(_baseList, _aspectCollection.getDecoratee());
	}
}
