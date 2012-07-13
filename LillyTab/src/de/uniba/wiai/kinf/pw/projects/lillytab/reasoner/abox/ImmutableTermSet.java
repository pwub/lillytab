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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox;

import de.dhke.projects.cutil.IDecorator;
import de.dhke.projects.cutil.collections.immutable.GenericImmutableSet;
import de.dhke.projects.cutil.collections.immutable.GenericImmutableSortedSet;
import de.dhke.projects.cutil.collections.immutable.IImmutable;
import de.dhke.projects.cutil.collections.immutable.ImmutableIterator;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ITermSet;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.DLTermOrder;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTerm;
import java.util.Iterator;
import java.util.SortedSet;

/**
 *
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class ImmutableTermSet<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends GenericImmutableSet<IDLTerm<Name, Klass, Role>, ITermSet<Name, Klass, Role>>
	implements ITermSet<Name, Klass, Role>, IDecorator<ITermSet<Name, Klass, Role>>, IImmutable<ITermSet<Name, Klass, Role>>
{
	protected ImmutableTermSet(final ITermSet<Name, Klass, Role> baseSet)
	{
		super(baseSet, null);
	}

	public static <Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	ImmutableTermSet<Name, Klass, Role> decorate(final ITermSet<Name, Klass, Role> baseSet)
	{
		return new ImmutableTermSet<Name, Klass, Role>(baseSet);
	}

	public SortedSet<IDLTerm<Name, Klass, Role>> subSet(DLTermOrder termType)
	{
		return GenericImmutableSortedSet.decorate(subSet(termType));
	}

	public ITermSet<Name, Klass, Role> getImmutable()
	{
		return this;
	}


	public <T extends IDLTerm<Name, Klass, Role>> Iterator<T> iterator(Class<? extends T> klass)
	{
		return ImmutableIterator.decorate(getDecoratee().iterator(klass));
	}


	public <T extends IDLTerm<Name, Klass, Role>> Iterator<T> iterator(DLTermOrder termType,
																	   Class<? extends T> klass)
	{
		return ImmutableIterator.decorate(getDecoratee().iterator(termType, klass));
	}

}
