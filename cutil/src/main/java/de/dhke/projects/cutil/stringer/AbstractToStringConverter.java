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
package de.dhke.projects.cutil.stringer;

/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public abstract class AbstractToStringConverter
	implements IToStringConverter {

	@Override
	public Object lazy(Object obj, final IToStringConverter backStringer)
	{
		return new LazyStringer(obj, this, backStringer);
	}


	@Override
	public Object lazy(Object obj)
	{
		return lazy(obj, this);
	}


	@Override
	public String toString(Object obj)
	{
		return toString(obj, this);
	}


	@Override
	public void append(StringBuilder sb, Object obj)
	{
		append(sb, obj, this);
	}


	@Override
	public String toString(Object obj, final IToStringConverter backStringer)
	{
		final StringBuilder sb = new StringBuilder();
		append(sb, obj, backStringer);
		return sb.toString();
	}
}
