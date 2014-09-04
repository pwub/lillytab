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
package de.uniba.wiai.kinf.pw.projects.lillytab.terms.util;

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLNodeTerm;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.datarange.IDLDataRange;
import java.text.ParseException;


/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class SimpleKRSSParser
{
	private final IDLTermFactory<String, String, String, String> _termFactory;

	public SimpleKRSSParser(final IDLTermFactory<String, String, String, String> termFactory)
	{
		_termFactory = termFactory;
	}

	public SimpleKRSSParser()
	{
		this(new SimpleStringDLTermFactory());
	}

	public IDLNodeTerm<String, String, String, String> parseRestriction(final String input)
		throws ParseException
	{
		return parseRestriction(new TokenIterator(input));
	}

	public IDLNodeTerm<String, String, String, String> parseRestriction(final TokenIterator tokenIter)
		throws ParseException
	{
		String next1 = tokenIter.next();
		if (next1.equals("{")) {
			String next2 = tokenIter.next();
			if (next2.equals("\"")) {
				String literal = tokenIter.next();
				checkNextToken(tokenIter, "\"");
				checkNextToken(tokenIter, "}");
				return _termFactory.getDLLiteralReference(literal);
			} else {
				tokenIter.pushBack(next2);
			}
		}
		tokenIter.pushBack(next1);
		return parse(tokenIter);
	}

	public IDLClassExpression<String, String, String, String> parse(final TokenIterator tokenIter)
		throws ParseException
	{
		String token = tokenIter.next();
		switch (token) {
			case "(":
				/* subterm */
				final String operator = tokenIter.next().toLowerCase();
				switch (operator) {
					case "not": {
						final IDLClassExpression<String, String, String, String> t = parse(tokenIter);
						checkNextToken(tokenIter, ")");
						return _termFactory.getDLObjectNegation(t);
					}
					case "or": {
						final IDLClassExpression<String, String, String, String> t0 = parse(tokenIter);
						final IDLClassExpression<String, String, String, String> t1 = parse(tokenIter);
						checkNextToken(tokenIter, ")");
						return _termFactory.getDLObjectUnion(t0, t1);
					}
					case "and": {
						final IDLClassExpression<String, String, String, String> t0 = parse(tokenIter);
						final IDLClassExpression<String, String, String, String> t1 = parse(tokenIter);
						checkNextToken(tokenIter, ")");
						return _termFactory.getDLObjectIntersection(t0, t1);
					}
					case "implies": {
						final IDLClassExpression<String, String, String, String> t0 = parse(tokenIter);
						final IDLClassExpression<String, String, String, String> t1 = parse(tokenIter);
						checkNextToken(tokenIter, ")");
						return _termFactory.getDLImplies(t0, t1);
					}
					case "some": {
						final String role = tokenIter.next();
						final IDLNodeTerm<String, String, String, String> t = parseRestriction(tokenIter);
						checkNextToken(tokenIter, ")");
						if (t instanceof IDLDataRange) {
							return _termFactory.getDLDataSomeRestriction(role,
																		 (IDLDataRange<String, String, String, String>) t);
						} else if (t instanceof IDLClassExpression) {
							return _termFactory.getDLObjectSomeRestriction(role,
																		   (IDLClassExpression<String, String, String, String>) t);
						} else {
							throw new ParseException("Unknown restriction term: " + t, (int) tokenIter.getPosition());
						}
					}
					case "only": {
						final String role = tokenIter.next();
						final IDLNodeTerm<String, String, String, String> t = parseRestriction(tokenIter);
						checkNextToken(tokenIter, ")");
						if (t instanceof IDLDataRange) {
							return _termFactory.getDLDataAllRestriction(role,
																		(IDLDataRange<String, String, String, String>) t);
						} else if (t instanceof IDLClassExpression) {
							return _termFactory.getDLObjectAllRestriction(role,
																		  (IDLClassExpression<String, String, String, String>) t);
						} else {
							throw new ParseException("Unknown restriction term: " + t, (int) tokenIter.getPosition());
						}
					}
				}
				throw new ParseException(String.format("Unknown operator: %s", operator), (int) tokenIter.getPosition());
			case "{":
				final String individual = tokenIter.next();
				checkNextToken(tokenIter, "}");
				return _termFactory.getDLIndividualReference(individual);
			default:
				return _termFactory.getDLClassReference(token);
		}
	}

	public IDLClassExpression<String, String, String, String> parse(final String input)
		throws ParseException
	{
		return parse(new TokenIterator(input));
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
}
