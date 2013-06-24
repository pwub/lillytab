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

/**
 * (c) 2009-2012 Otto-Friedrich-University Bamberg
 *
 * $Id$
 *
 * Use, modification and restribution of this file are covered by the terms of the Artistic License 2.0.
 *
 * You should have received a copy of the license terms in a file named "LICENSE" together with this software package.
 *
 * Disclaimer of Warranty: THE PACKAGE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS "AS IS' AND WITHOUT ANY
 * EXPRESS OR IMPLIED WARRANTIES. THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT ARE DISCLAIMED TO THE EXTENT PERMITTED BY YOUR LOCAL LAW. UNLESS REQUIRED BY LAW, NO COPYRIGHT
 * HOLDER OR CONTRIBUTOR WILL BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING IN ANY
 * WAY OUT OF THE USE OF THE PACKAGE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ABoxNodeEvent;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentABoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ITermSetListener;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.TermChangeEvent;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABoxFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.SimpleStringDLTermFactory;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ABoxNotificationTest {
	private static final IDLTermFactory<String, String, String, String> _termFactory = new SimpleStringDLTermFactory();
	private static final IABoxFactory<String, String, String, String> _aboxFactory = new ABoxFactory<>(
		_termFactory);


	@BeforeClass
	public static void setUpClass()
		throws Exception
	{
	}


	@AfterClass
	public static void tearDownClass()
		throws Exception
	{
	}
	private IABox<String, String, String, String> _abox;
	private TermSetCountListener _listener;


	public ABoxNotificationTest()
	{
	}


	@Before
	public void setUp()
	{
		_abox = _aboxFactory.createABox();
		_listener = new TermSetCountListener();
		_abox.getTermSetListeners().add(_listener);
	}


	@After
	public void tearDown()
	{
		if (_abox != null) {
			_abox.getTermSetListeners().clear();
		}
		_abox = null;
		_listener = null;
	}


	@Test
	public void testTermAdded() throws EInconsistentABoxException
	{
		final IABoxNode<String, String, String, String> node = _abox.createNode(false);
		final IDLClassExpression<String, String, String, String> A = _termFactory.getDLClassReference("A");
		node.addTerm(A);

		assertEquals(0, _listener.removedHistory.size());
		assertEquals(0, _listener.clearedHistory.size());
		assertEquals(2, _listener.addedHistory.size());

		Set<IDLTerm<String, String, String, String>> terms = new HashSet<>();
		for (TermChangeEvent<String, String, String, String> ev : _listener.addedHistory) {
			terms.add(ev.getTerm());
		}
		assertTrue(terms.contains(A));
		assertTrue(terms.contains(_termFactory.getDLThing()));
	}


	@Test
	public void testTermAddedTBox() throws EInconsistentABoxException
	{
		final IDLClassExpression<String, String, String, String> A = _termFactory.getDLClassReference("A");
		final IDLClassExpression<String, String, String, String> B = _termFactory.getDLClassReference("B");
		_abox.getTBox().add(_termFactory.getDLImplies(A, B));
		final IABoxNode<String, String, String, String> node = _abox.createNode(false);
		node.addTerm(A);

		assertEquals(0, _listener.removedHistory.size());
		assertEquals(0, _listener.clearedHistory.size());
		assertEquals(3, _listener.addedHistory.size());

		Set<IDLTerm<String, String, String, String>> terms = new HashSet<>();
		for (TermChangeEvent<String, String, String, String> ev : _listener.addedHistory) {
			terms.add(ev.getTerm());
		}
		assertTrue(terms.contains(_termFactory.getDLThing()));
		assertTrue(terms.contains(A));
		assertTrue(terms.contains(B));
	}


	@Test
	public void testTermRemoved()
		throws EInconsistentABoxException
	{
		final IDLClassExpression<String, String, String, String> A = _termFactory.getDLClassReference("A");
		final IDLClassExpression<String, String, String, String> B = _termFactory.getDLClassReference("B");
		final IABoxNode<String, String, String, String> node = _abox.createNode(false);
		node.addTerm(A);
		node.addTerm(B);

		_listener.addedHistory.clear();
		assertTrue(node.getTerms().remove(B));
		assertTrue(node.getTerms().contains(A));
		assertFalse(node.getTerms().contains(B));
		assertEquals(0, _listener.addedHistory.size());
		assertEquals(1, _listener.removedHistory.size());
		assertEquals(0, _listener.clearedHistory.size());
		assertEquals(B, _listener.removedHistory.get(0).getTerm());
	}


	@Test
	public void testTermSetCleared()
		throws EInconsistentABoxException
	{
		final IDLClassExpression<String, String, String, String> A = _termFactory.getDLClassReference("A");
		final IDLClassExpression<String, String, String, String> B = _termFactory.getDLClassReference("B");
		final IABoxNode<String, String, String, String> node = _abox.createNode(false);
		node.addTerm(A);
		node.addTerm(B);

		_listener.addedHistory.clear();
		node.getTerms().clear();
		assertFalse(node.getTerms().contains(A));
		assertFalse(node.getTerms().contains(B));
		assertTrue(node.getTerms().isEmpty());
		assertEquals(0, _listener.addedHistory.size());
		assertEquals(0, _listener.removedHistory.size());
		assertEquals(1, _listener.clearedHistory.size());
	}

	class TermSetCountListener
		implements ITermSetListener<String, String, String, String> {

		public final List<TermChangeEvent<String, String, String, String>> addedHistory = new ArrayList<>();
		public final List<TermChangeEvent<String, String, String, String>> removedHistory = new ArrayList<>();
		public final List<ABoxNodeEvent<String, String, String, String>> clearedHistory = new ArrayList<>();


		@Override
		public void termAdded(TermChangeEvent<String, String, String, String> ev)
		{
			addedHistory.add(ev);
		}


		@Override
		public void termRemoved(TermChangeEvent<String, String, String, String> ev)
		{
			removedHistory.add(ev);
		}


		@Override
		public void termSetCleared(ABoxNodeEvent<String, String, String, String> ev)
		{
			clearedHistory.add(ev);
		}
	}
}
