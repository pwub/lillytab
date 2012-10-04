/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner;

import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxFactory;
import de.dhke.projects.cutil.collections.CollectionFromIterable;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleType;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl.DLTermFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import org.junit.Test;

import static org.junit.Assert.*;


/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class LinkMapTest
{
	private IDLTermFactory<String, String, String> _termFactory;
	private IABoxFactory<String, String, String> _aboxFactory;
	private IABox<String, String, String> _abox;
	private IRBox<String, String, String> _rbox;

	public LinkMapTest()
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
		_termFactory = new DLTermFactory<String, String, String>();
		_aboxFactory = new ABoxFactory<String, String, String>(_termFactory);
		_abox = _aboxFactory.createABox();
		_rbox = _abox.getTBox().getRBox();
	}

	@After
	public void tearDown()
	{
		_rbox = null;
		_abox = null;
		_aboxFactory = null;
		_termFactory = null;
	}

	@Test
	public void testGetSuccessors()
		throws EInconsistencyException
	{
		IABoxNode<String, String, String> aNode = _abox.getOrAddNamedNode("a", false);
		IABoxNode<String, String, String> bNode = _abox.getOrAddNamedNode("b", false);
		_rbox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		aNode.getLinkMap().getAssertedSuccessors().put("r", bNode.getNodeID());

		assertTrue(aNode.getLinkMap().getSuccessors().contains(bNode.getNodeID()));
		assertTrue(CollectionFromIterable.decorate(aNode.getLinkMap().getSuccessors("r"), true).contains(bNode.
			getNodeID()));

		assertFalse(aNode.getLinkMap().getSuccessors().contains(aNode.getNodeID()));
		assertFalse(CollectionFromIterable.decorate(aNode.getLinkMap().getSuccessors("r"), true).contains(aNode.
			getNodeID()));		
	}

	@Test
	public void testGetSuccessorNodes()
		throws EInconsistencyException
	{
		IABoxNode<String, String, String> aNode = _abox.getOrAddNamedNode("a", false);
		IABoxNode<String, String, String> bNode = _abox.getOrAddNamedNode("b", false);
		_rbox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		aNode.getLinkMap().getAssertedSuccessors().put("r", bNode.getNodeID());

		assertTrue(CollectionFromIterable.decorate(aNode.getLinkMap().getSuccessorNodes(), true).contains(bNode));
		assertTrue(CollectionFromIterable.decorate(aNode.getLinkMap().getSuccessorNodes("r"), true).contains(bNode));

		assertFalse(CollectionFromIterable.decorate(aNode.getLinkMap().getSuccessorNodes(), true).contains(aNode));
		assertFalse(CollectionFromIterable.decorate(aNode.getLinkMap().getSuccessorNodes("r"), true).contains(aNode));

	}
	
	@Test
	public void testGetPredecessors()
		throws EInconsistencyException
	{
		IABoxNode<String, String, String> aNode = _abox.getOrAddNamedNode("a", false);
		IABoxNode<String, String, String> bNode = _abox.getOrAddNamedNode("b", false);
		_rbox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		aNode.getLinkMap().getAssertedSuccessors().put("r", bNode.getNodeID());

		assertTrue(bNode.getLinkMap().getPredecessors().contains(aNode.getNodeID()));
		assertTrue(CollectionFromIterable.decorate(bNode.getLinkMap().getPredecessors("r"), true).contains(aNode.
			getNodeID()));
		
		assertFalse(aNode.getLinkMap().getPredecessors().contains(aNode.getNodeID()));
		assertFalse(CollectionFromIterable.decorate(aNode.getLinkMap().getPredecessors("r"), true).contains(aNode.
			getNodeID()));		
	}

	@Test
	public void testGetPredecessorNodes()
		throws EInconsistencyException
	{
		IABoxNode<String, String, String> aNode = _abox.getOrAddNamedNode("a", false);
		IABoxNode<String, String, String> bNode = _abox.getOrAddNamedNode("b", false);
		_rbox.getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		aNode.getLinkMap().getAssertedSuccessors().put("r", bNode.getNodeID());

		assertTrue(CollectionFromIterable.decorate(bNode.getLinkMap().getPredecessorNodes(), true).contains(aNode));
		assertTrue(CollectionFromIterable.decorate(bNode.getLinkMap().getPredecessorNodes("r"), true).contains(aNode));
		
		assertFalse(CollectionFromIterable.decorate(bNode.getLinkMap().getPredecessorNodes(), true).contains(bNode));
		assertFalse(CollectionFromIterable.decorate(bNode.getLinkMap().getPredecessorNodes("r"), true).contains(bNode));	
	}	
}
