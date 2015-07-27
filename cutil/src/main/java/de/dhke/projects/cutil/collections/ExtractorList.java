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
package de.dhke.projects.cutil.collections;

import de.dhke.projects.cutil.IDecorator;
import java.util.AbstractList;
import java.util.List;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.list.TransformedList;


/**
 * Wrapper list that supports transformed reading from
 * another list.
 * <p />
 * Can bee seen as as the "reader" variant of {@link TransformedList},
 * which transforms items <em>ADDED</em> to a list.
 * 
 * @param <I> 
 * @param <O> 
 * @param <L> 
 * @author Peter Wullinger <java@dhke.de>
 */
public class ExtractorList<I, O>
	extends AbstractList<O>
	implements List<O>, IDecorator<List<? extends I>>
{
	private final List<? extends I> _baseList;
	private final Transformer<I, O> _transformer;

	protected ExtractorList(List<? extends I> baseList, Transformer<I, O> transformer)
	{
		_baseList = baseList;
		_transformer = transformer;
	}

	public static <I, O> ExtractorList<I, O> decorate(List<? extends I> baseCollection, Transformer<I, O> transformer)
	{
		return new ExtractorList<>(baseCollection, transformer);
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

	@Override
	public List<? extends I> getDecoratee()
	{
		return _baseList;
	}
}
