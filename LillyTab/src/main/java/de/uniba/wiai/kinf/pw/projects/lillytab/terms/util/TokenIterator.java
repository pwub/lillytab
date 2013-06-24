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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.NoSuchElementException;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class TokenIterator {

	private Reader _reader;
	private int _nextChar;
	private long _nRead = 0;
	private ArrayDeque<String> _pushedTokens = new ArrayDeque<>();


	public TokenIterator(final Reader reader)
	{
		try {
			_reader = reader;
			_nextChar = getNextChar();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}


	public TokenIterator(final String str)
	{
		this(new StringReader(str));
	}


	private int getNextChar() throws IOException
	{
		int nextChar = _reader.read();
		++_nRead;
		return nextChar;
	}


	public boolean hasNext()
	{
		return (!_pushedTokens.isEmpty()) || (_nextChar != -1);
	}


	public long getPosition()
	{
		return _nRead;
	}


	public void pushBack(String token)
	{
		_pushedTokens.add(token);
	}


	public String next()
		throws ParseException
	{
		if (!hasNext()) {
			throw new NoSuchElementException();
		} else if (!_pushedTokens.isEmpty()) {
			return _pushedTokens.removeLast();
		} else {
			String token = "";
			try {
				while (Character.isSpaceChar((char) _nextChar)) {
					_nextChar = getNextChar();
				}

				if (((char) _nextChar == '_') || Character.isLetterOrDigit((char) _nextChar)) {
					while (((char) _nextChar == '_') || Character.isLetterOrDigit((char) _nextChar)) {
						token += (char) _nextChar;
						_nextChar = getNextChar();
					}
					return token;
				} else {
					char thisChar = (char) _nextChar;
					_nextChar = getNextChar();
					return String.valueOf(thisChar);
				}
			} catch (IOException ex) {
				ParseException pex = new ParseException("IO error", (int) getPosition());
				pex.initCause(pex);
				throw pex;
			}
		}
	}


	public void remove()
	{
		throw new UnsupportedOperationException("Cannot remove from TokenIterator");
	}
}
