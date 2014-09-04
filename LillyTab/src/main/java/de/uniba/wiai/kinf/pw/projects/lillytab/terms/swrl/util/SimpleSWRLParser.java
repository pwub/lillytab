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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.util;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLArgument;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLAtomicTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLDArgument;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLIArgument;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLRule;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.swrl.ISWRLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.TokenIterator;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class SimpleSWRLParser {
	final ISWRLTermFactory<String, String, String, String> _swrlFactory;


	public SimpleSWRLParser(final ISWRLTermFactory<String, String, String, String> swrlFactory)
	{
		_swrlFactory = swrlFactory;
	}


	public ISWRLAtomicTerm<String, String, String, String> parseAtom(final String input)
		throws ParseException
	{
		return parseAtom(new TokenIterator(input));
	}


	public ISWRLAtomicTerm<String, String, String, String> parseAtom(final TokenIterator tokenIter)
		throws ParseException
	{
		checkNextToken(tokenIter, "(");
		final String element = tokenIter.next();
		final ISWRLArgument<String, String, String, String> ind1 = parseIndividual(tokenIter);
		final String nextToken = tokenIter.next();
		if (nextToken.equalsIgnoreCase(")")) {
			return _swrlFactory.getSWRLClassAtom(element, (ISWRLIArgument<String, String, String, String>) ind1);
		} else {
			tokenIter.pushBack(nextToken);
			final ISWRLArgument<String, String, String, String> ind2 = parseIndividual(tokenIter);
			checkNextToken(tokenIter, ")");
			if (ind2 instanceof ISWRLIArgument) {
				/* XXX - this makes variables ALWAYS be object references */
				return _swrlFactory.getSWRLObjectRoleAtom(element, (ISWRLIArgument<String, String, String, String>) ind1,
														  (ISWRLIArgument<String, String, String, String>) ind2);
			} else if (ind2 instanceof ISWRLDArgument) {
				return _swrlFactory.getSWRLDataRoleAtom(element, (ISWRLIArgument<String, String, String, String>) ind1,
														(ISWRLDArgument<String, String, String, String>) ind2);

			} else
				throw new ParseException("Unknown role object type: " + ind2.getClass(), (int) tokenIter.getPosition());
		}
	}


	public ISWRLTerm<String, String, String, String> parseTermList(final TokenIterator tokenIter)
		throws ParseException
	{
		List<ISWRLAtomicTerm<String, String, String, String>> list = new ArrayList<>();
		String nextToken = ",";

		while (nextToken.equalsIgnoreCase(",")) {
			ISWRLAtomicTerm<String, String, String, String> atom = parseAtom(tokenIter);
			list.add(atom);
			if (!tokenIter.hasNext()) {
				nextToken = "";
			} else {
				nextToken = tokenIter.next();
			}
		}
		if (!nextToken.isEmpty()) {
			tokenIter.pushBack(nextToken);
		}

		if (list.size() > 1) {
			return _swrlFactory.getSWRLIntersection(list);
		} else {
			return list.get(0);
		}

	}


	public ISWRLTerm<String, String, String, String> parseTermList(final String input)
		throws ParseException
	{
		return parseTermList(new TokenIterator(input));
	}


	public ISWRLRule<String, String, String, String> parseRule(final TokenIterator tokenIter)
		throws ParseException
	{
		ISWRLTerm<String, String, String, String> head = parseTermList(tokenIter);
		checkNextToken(tokenIter, ":");
		checkNextToken(tokenIter, "-");
		ISWRLTerm<String, String, String, String> body = parseTermList(tokenIter);
		checkNextToken(tokenIter, ".");
		return _swrlFactory.getSWRLRule(head, body);
	}


	public ISWRLRule<String, String, String, String> parseRule(final String input)
		throws ParseException
	{
		return parseRule(new TokenIterator(input));
	}


	private static void checkNextToken(final TokenIterator tokenIter, final String expected)
		throws ParseException
	{
		String nextToken = tokenIter.next();
		final boolean isEqual = nextToken.equalsIgnoreCase(expected);
		if (!isEqual) {
			throw new ParseException(String.format("Expected Token `%s', got `%s'", expected, nextToken),
									 (int) tokenIter.getPosition());
		}
	}


	private ISWRLArgument<String, String, String, String> parseIndividual(final TokenIterator tokenIter)
		throws ParseException
	{
		String token = tokenIter.next();

		if (token.equalsIgnoreCase("?")) {
			final String varName = tokenIter.next();
			return _swrlFactory.getSWRLVariable(varName);
		} else if (token.equals("{")) {
			final String indName;
			String next = tokenIter.next();
			final boolean isDataLiteral;
			if (next.equals("\"")) {
				isDataLiteral = true;
				indName = tokenIter.next();
				checkNextToken(tokenIter, "\"");
			} else {
				isDataLiteral = false;
				indName = next;
			}
			checkNextToken(tokenIter, "}");
			if (isDataLiteral)
				return _swrlFactory.getSWRLLiteralReference(indName);
			else
				return _swrlFactory.getSWRLIndividualReference(indName);

		} else {
			throw new ParseException("Expected individual", (int) tokenIter.getPosition());
		}
	}
}
