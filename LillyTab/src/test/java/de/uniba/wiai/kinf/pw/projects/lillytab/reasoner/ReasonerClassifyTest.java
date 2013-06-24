/**
 * (c) 2009-2013 Otto-Friedrich-University Bamberg
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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner;

import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleType;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLImplies;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleKRSSParser;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleStringDLTermFactory;
import java.text.ParseException;
import java.util.Collection;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author peter
 */
public class ReasonerClassifyTest {


	@BeforeClass
	public static void setUpClass()
	{
		LogManager.getLogManager().reset();
		Logger.getLogger("").setLevel(Level.ALL);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.ALL);
		Logger.getLogger("").addHandler(handler);
	}


	@AfterClass
	public static void tearDownClass()
	{
	}

	private final IDLTermFactory<String, String, String, String> _termFactory = new SimpleStringDLTermFactory();
	private final IABoxFactory<String, String, String, String> _aboxFactory = new ABoxFactory<>(
		_termFactory);
	private IABox<String, String, String, String> _abox;
	private Reasoner<String, String, String, String> _reasoner;
	private SimpleKRSSParser _parser;


	public ReasonerClassifyTest()
	{
	}


	@Before
	public void setUp()
	{
		final ReasonerOptions reasonerOptions = new ReasonerOptions();
		reasonerOptions.setTracing(false);;
		// reasonerOptions._tracing = true;
		reasonerOptions.setMergeTracking(true);
		_reasoner = new Reasoner<>(reasonerOptions);
		_parser = new SimpleKRSSParser(_termFactory);
		_abox = _aboxFactory.createABox();
	}


	@After
	public void tearDown()
	{
		_reasoner = null;
		_abox = null;
		_parser = null;
	}


	@Test
	public void inconsistentClassify() throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getTBox().add(_parser.parse("(implies A (not B))"));
		_abox.getTBox().add(_parser.parse("(implies B A)"));
		Collection<IDLImplies<String, String, String, String>> clsTerms = _reasoner.classify(_abox);
	}


	@Test
	public void someRSubClassPropagationTest() throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getRBox().getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getTBox().add(_parser.parse("(implies A (some r _Thing_))"));
		_abox.getTBox().add(_parser.parse("(implies (some r _Thing_) B)"));
		Collection<IDLImplies<String, String, String, String>> clsTerms = _reasoner.classify(_abox);
		assertTrue(clsTerms.contains(_parser.parse("(implies A B)")));
		assertFalse(clsTerms.contains(_parser.parse("(implies A A)")));
		assertFalse(clsTerms.contains(_parser.parse("(implies B B)")));
	}


	@Test
	public void onlySubClassPropagationTest()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getRBox().getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getTBox().add(_parser.parse("(implies A (only r C))"));
		_abox.getTBox().add(_parser.parse("(implies (only r C) B)"));
		Collection<IDLImplies<String, String, String, String>> clsTerms = _reasoner.classify(_abox);
		assertTrue(clsTerms.contains(_parser.parse("(implies A B)")));
		assertFalse(clsTerms.contains(_parser.parse("(implies A C)")));
		assertFalse(clsTerms.contains(_parser.parse("(implies B C)")));
		assertFalse(clsTerms.contains(_parser.parse("(implies A A)")));
		assertFalse(clsTerms.contains(_parser.parse("(implies B B)")));
	}


	@Test
	public void complexSubClassPropagationTest()
		throws ParseException, EReasonerException, EInconsistencyException
	{
		_abox.getRBox().getAssertedRBox().addRole("r", RoleType.OBJECT_PROPERTY);
		_abox.getTBox().add(_parser.parse("(implies A (or (some r D) C))"));
		_abox.getTBox().add(_parser.parse("(implies (not (and (only r (not D)) (not C))) B)"));
		Collection<IDLImplies<String, String, String, String>> clsTerms = _reasoner.classify(_abox);
		assertTrue(clsTerms.contains(_parser.parse("(implies A B)")));
		assertFalse(clsTerms.contains(_parser.parse("(implies A C)")));
		assertFalse(clsTerms.contains(_parser.parse("(implies A D)")));
		assertFalse(clsTerms.contains(_parser.parse("(implies B C)")));
		assertFalse(clsTerms.contains(_parser.parse("(implies B D)")));
		assertFalse(clsTerms.contains(_parser.parse("(implies A A)")));
		assertFalse(clsTerms.contains(_parser.parse("(implies B B)")));
	}
}
