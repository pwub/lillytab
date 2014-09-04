/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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