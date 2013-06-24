/**
 * (c) 2009-2013 Peter Wullinger
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
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.iterators.TransformIterator;

/**
 * Wrapper collection that supports transformed reading from
 * another collection.
 * 
 * @param <I> 
 * @param <O> 
 * @param <C> 
 * @author Peter Wullinger <java@dhke.de>
 */
public class ExtractorCollection<I, O, C extends Collection<I>>
	extends AbstractCollection<O>
	implements Collection<O>, IDecorator<C>
{
	private final C _baseCollection;
	private final Transformer<I, O> _transformer;

	protected ExtractorCollection(C baseCollection, Transformer<I, O> transformer)
	{
		_baseCollection = baseCollection;
		_transformer = transformer;
	}

	public static <I, O, C extends Collection<I>> ExtractorCollection<I, O, C> decorate(C baseCollection, Transformer<I, O> transformer)
	{
		return new ExtractorCollection<>(baseCollection, transformer);
	}

	@Override
	public Iterator<O> iterator()
	{
		return new TransformIterator<>(_baseCollection.iterator(), _transformer);
	}

	@Override
	public int size()
	{
		return _baseCollection.size();
	}

	public C getDecoratee()
	{
		return _baseCollection;
	}

}
