/**
 * (c) 2009-2012 Peter Wullinger
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
package de.dhke.projects.cutil.collections;

import de.dhke.projects.cutil.IDecorator;
import java.util.AbstractList;
import java.util.List;
import org.apache.commons.collections15.Transformer;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ExtractorList<I, O, L extends List<I>>
	extends AbstractList<O>
	implements List<O>, IDecorator<L>
{
	private final L _baseList;
	private final Transformer<I, O> _transformer;

	protected ExtractorList(L baseList, Transformer<I, O> transformer)
	{
		_baseList = baseList;
		_transformer = transformer;
	}

	public static <I, O, L extends List<I>> ExtractorList<I, O, L> decorate(L baseCollection, Transformer<I, O> transformer)
	{
		return new ExtractorList<I, O, L>(baseCollection, transformer);
	}


	@Override
	public O get(int index)
	{
		return _transformer.transform(_baseList.get(index));
	}

	@Override
	public int size()
	{
		return _baseList.size();
	}

	public L getDecoratee()
	{
		return _baseList;
	}

}
