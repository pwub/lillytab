/**
 * (c) 2009-2012 Otto-Friedrich-University Bamberg
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

import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.impl.DLTermFactory;
import java.text.ParseException;


/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class SimpleKRSSParser
{
	private final IDLTermFactory<String, String, String> _termFactory;

	public SimpleKRSSParser(final IDLTermFactory<String, String, String> termFactory)
	{
		_termFactory = termFactory;
	}

	public SimpleKRSSParser()
	{
		_termFactory = new DLTermFactory<String, String, String>();
	}

	private static void checkNextToken(final TokenIterator tokenIter, final String token)
		throws ParseException
	{
		final boolean isEqual = tokenIter.next().equalsIgnoreCase(token);
		assert isEqual;
	}

	public IDLRestriction<String, String, String> parse(final TokenIterator tokenIter)
		throws ParseException
	{
		String token = tokenIter.next();
		if (token.equals("(")) {
			/* subterm */
			final String operator = tokenIter.next();
			if (operator.equalsIgnoreCase("not")) {
				final IDLRestriction<String, String, String> t = parse(tokenIter);
				checkNextToken(tokenIter, ")");
				return _termFactory.getDLNegation(t);
			} else if (operator.equalsIgnoreCase("or")) {
				final IDLRestriction<String, String, String> t0 = parse(tokenIter);
				final IDLRestriction<String, String, String> t1 = parse(tokenIter);
				checkNextToken(tokenIter, ")");
				return _termFactory.getDLUnion(t0, t1);
			} else if (operator.equalsIgnoreCase("and")) {
				final IDLRestriction<String, String, String> t0 = parse(tokenIter);
				final IDLRestriction<String, String, String> t1 = parse(tokenIter);
				checkNextToken(tokenIter, ")");
				return _termFactory.getDLIntersection(t0, t1);
			} else if (operator.equalsIgnoreCase("implies")) {
				final IDLRestriction<String, String, String> t0 = parse(tokenIter);
				final IDLRestriction<String, String, String> t1 = parse(tokenIter);
				checkNextToken(tokenIter, ")");
				return _termFactory.getDLImplies(t0, t1);
			} else if (operator.equalsIgnoreCase("some")) {
				final String role = tokenIter.next();
				final IDLRestriction<String, String, String> t = parse(tokenIter);
				checkNextToken(tokenIter, ")");
				return _termFactory.getDLSomeRestriction(role, t);
			} else if (operator.equalsIgnoreCase("only")) {
				final String role = tokenIter.next();
				final IDLRestriction<String, String, String> t = parse(tokenIter);
				checkNextToken(tokenIter, ")");
				return _termFactory.getDLAllRestriction(role, t);
			}
				throw new ParseException(String.format("Unknown operator: %s", operator), (int) tokenIter.getPosition());
		} else if (token.equalsIgnoreCase("_Thing_"))
			return _termFactory.getDLThing();
		else if (token.equalsIgnoreCase("_Nothing_"))
			return _termFactory.getDLNothing();
		else if (token.equals("{")) {
			final String nominal = tokenIter.next();
				checkNextToken(tokenIter, "}");
			return _termFactory.getDLNominalReference(nominal);
		} else
			return _termFactory.getDLClassReference(token);
	}

	public IDLRestriction<String, String, String> parse(final String input)
		throws ParseException
	{
		return parse(new TokenIterator(input));
	}
}
