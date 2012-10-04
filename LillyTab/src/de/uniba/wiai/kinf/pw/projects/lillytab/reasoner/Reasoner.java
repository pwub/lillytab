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
package de.uniba.wiai.kinf.pw.projects.lillytab.reasoner;

import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.blocking.SubsetBlockingStrategy;
import de.uniba.wiai.kinf.pw.projects.lillytab.IReasonerResult;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.blocking.DoubleBlockingStrategy;
import de.uniba.wiai.kinf.pw.projects.lillytab.blocking.IBlockingStrategy;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentRBoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistencyException;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeID;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.EInconsistentABoxException;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.ITBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.IAssertedRBox;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.ENodeMergeException;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleProperty;
import de.uniba.wiai.kinf.pw.projects.lillytab.abox.NodeMergeInfo;
import de.dhke.projects.cutil.collections.tree.DecisionTree;
import de.dhke.projects.cutil.collections.tree.IDecisionTree;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.IntersectionCompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.ForAllCompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.ICompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.RoleRestrictionCompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.SomeCompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.SymmetricRoleCompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.TransitiveRoleCompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.UnionCompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.tbox.RoleType;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLRestriction;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLTermFactory;
import de.uniba.wiai.kinf.pw.projects.lillytab.terms.util.TermUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * <p> An implementation of an ALCO+GCis satisfiability tester. </p><p> This class serves as a consistency checker for
 * description logic {@link ABox}es. It employs a tablaux-method to expand the the ABox node graph and check for
 * possible inconsistencies. </p><p> The reasoners main method is {@link #checkConsistency(de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABox)
 * }. It will take a single ABox node as input and perform completion on the ABox. Completion is performed by applying
 * certain completion rules, which generate new concept terms ({@link IABoxNode#getTerms()}) for nodes, propagate
 * concept terms between nodes and may potentially even create new nodes in the ABox. After the tableaux completion, it
 * is possible to detect inconsistencies locally by comparing and individual node's concept terms for clashes. </p><p>
 * Some completion rules are non-deterministic and thus multiple different ABox-completions may result from the tablaux
 * expansion.. The reasoner will return all clash-free completitions of a particular ABox. </p>
 *
 * @param <Name>
 * @param <Klass>
 * @param <Role>
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class Reasoner<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends AbstractReasoner<Name, Klass, Role> {
	// private ABox<Name, Klass, Role> _initialAbox;

	private final ReasonerOptions _reasonerOptions;
	private INodeConsistencyChecker<Name, Klass, Role> _nodeConsistencyChecker;


	public Reasoner(
		final INodeConsistencyChecker<Name, Klass, Role> cChecker,
		final ReasonerOptions reasonerOptions)
	{
		_nodeConsistencyChecker = cChecker;
		_reasonerOptions = reasonerOptions;
	}


	public Reasoner(
		final INodeConsistencyChecker<Name, Klass, Role> cChecker)
	{
		this(cChecker, new ReasonerOptions());
	}


	public Reasoner(final ReasonerOptions options)
	{
		_nodeConsistencyChecker = new NodeConsistencyChecker<Name, Klass, Role>(options.TRACE);
		_reasonerOptions = options;
	}


	public Reasoner()
	{
		this(new ReasonerOptions());
	}


	protected List<ICompleter<Name, Klass, Role>> setupNonGeneratingCompleters(
		final IABox<Name, Klass, Role> abox)
	{
		final List<ICompleter<Name, Klass, Role>> nonGeneratingCompleters = new ArrayList<ICompleter<Name, Klass, Role>>();

		nonGeneratingCompleters.add(new RoleRestrictionCompleter<Name, Klass, Role>(getNodeConsistencyChecker(),
																					getReasonerOptions().TRACE));
		nonGeneratingCompleters.add(new IntersectionCompleter<Name, Klass, Role>(getNodeConsistencyChecker(),
																				 getReasonerOptions().TRACE));
		nonGeneratingCompleters.add(new ForAllCompleter<Name, Klass, Role>(getNodeConsistencyChecker(),
																		   getReasonerOptions().TRACE));
		nonGeneratingCompleters.add(new UnionCompleter<Name, Klass, Role>(getNodeConsistencyChecker(),
																		  getReasonerOptions().TRACE));
		nonGeneratingCompleters.add(new TransitiveRoleCompleter<Name, Klass, Role>(_nodeConsistencyChecker,
																				   getReasonerOptions().TRACE));
		nonGeneratingCompleters.add(new SymmetricRoleCompleter<Name, Klass, Role>(_nodeConsistencyChecker,
																				   getReasonerOptions().TRACE));
		return nonGeneratingCompleters;
	}


	protected List<ICompleter<Name, Klass, Role>> setupGeneratingCompleters(
		final IABox<Name, Klass, Role> abox)
	{
		final List<ICompleter<Name, Klass, Role>> generatingCompleters = new ArrayList<ICompleter<Name, Klass, Role>>();

		IBlockingStrategy<Name, Klass, Role> blockingStrategy;

		final IAssertedRBox<Name, Klass, Role> rbox = abox.getTBox().getRBox();

		final boolean needDoubleBlocking = rbox.hasInverseRoles();
		
		if (needDoubleBlocking)
			blockingStrategy = new DoubleBlockingStrategy<Name, Klass, Role>();
		else
			blockingStrategy = new SubsetBlockingStrategy<Name, Klass, Role>();


		generatingCompleters.add(new SomeCompleter<Name, Klass, Role>(getNodeConsistencyChecker(),
																	  blockingStrategy,
																	  getReasonerOptions().TRACE));
		return generatingCompleters;
	}


	protected void setupNodeConsistencyChecker()
	{
		setNodeConsistencyChecker(new NodeConsistencyChecker<Name, Klass, Role>(getReasonerOptions().TRACE));
	}

	/// <editor-fold defaultstate="collapsed" desc="Prepare functions">

	/**
	 * <p> Update the {@literal abox} for expansion. All global concepts that cannot be undolfded lazily are propagated
	 * into the existing nodes. <p></p> Scan the node for unfoldable concepts and unfoled them recursively. </p>
	 *
	 * @see ABoxNode#addUnfoldedDescription(de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression)
	 * @param abox
	 */
	private void prepareGlobalDescriptions(final IABox<Name, Klass, Role> abox)
		throws ENodeMergeException
	{
		final ITBox<Name, Klass, Role> tbox = abox.getTBox();
		final Set<IDLRestriction<Name, Klass, Role>> globalDescs = tbox.getGlobalDescriptions();

		Iterator<IABoxNode<Name, Klass, Role>> iter = abox.iterator();
		while (iter.hasNext()) {
			IABoxNode<Name, Klass, Role> currentNode = iter.next();
			assert abox.contains(currentNode);

			/*
			 * unfold existing terms
			 */
			NodeMergeInfo<Name, Klass, Role> unfoldResult = currentNode.unfoldAll();

			/*
			 * unfold global descriptions
			 */
			unfoldResult.append(currentNode.addUnfoldedDescriptions(globalDescs));

			if (!unfoldResult.getMergedNodes().isEmpty()) {
				/*
				 * a merge occured, refetch node
				 */
				currentNode = unfoldResult.getCurrentNode();
				assert abox.contains(currentNode);
				iter = abox.iterator();
			}
			if (getReasonerOptions().TRACE)
				logFinest("Unfolded concepts for node %s", currentNode);
		}
		if (getReasonerOptions().TRACE)
			logFine("Prepared global descriptions");
	}


	/**
	 * Create the initial branch for reasoning. This copied the ABox and initializes a new {@link Branch } object..
	 *
	 * @param abox The ABox to start with. A copy is made.
	 * @return An empty, freshly created branch for the supplied ABox.
	 */
	private Branch<Name, Klass, Role> prepareInitialBranch(final IABox<Name, Klass, Role> abox)
		throws ENodeMergeException, EInconsistentRBoxException
	{
		/*
		 * start with a copy of the initial abox
		 */
		final Branch<Name, Klass, Role> initialBranch = new Branch<Name, Klass, Role>(abox.clone(),
																					  _reasonerOptions.MERGE_TRACKING);
		prepareGlobalDescriptions(initialBranch.getABox());

		/**
		 * XXX - this does not belong here. Keep it, until I find a better way to handle functional roles.
		 *
		 */
		mergeFunctionalRoles(initialBranch.getABox());

		return initialBranch;
	}


	/**
	 * <p> Merge all successor nodes referenced by a functional role. </p>
	 */
	private void mergeFunctionalRoles(final IABox<Name, Klass, Role> abox)
		throws ENodeMergeException, EInconsistentRBoxException
	{
		final SortedSet<IABoxNode<Name, Klass, Role>> nodes = new TreeSet<IABoxNode<Name, Klass, Role>>(abox);
		Iterator<IABoxNode<Name, Klass, Role>> nodeIter = nodes.iterator();
		while (nodeIter.hasNext()) {
			final IABoxNode<Name, Klass, Role> node = nodeIter.next();
			boolean skip = false;
			for (Role role : node.getLinkMap().getOutgoingRoles()) {
				final Iterable<NodeID> succIDs = node.getLinkMap().getSuccessors(role);
				Iterator<NodeID> succIDiter = succIDs.iterator();
				if (abox.getTBox().getRBox().hasRoleProperty(role, RoleProperty.FUNCTIONAL)
				&& succIDiter.hasNext() && (succIDiter.next() != null) && succIDiter.hasNext()) {
					succIDiter = succIDs.iterator();
					/*
					 * more than one successor, merge
					 */
					final IABoxNode<Name, Klass, Role> succ0 = abox.getNode(succIDiter.next());
					assert succ0 != null;
					final IABoxNode<Name, Klass, Role> succ1 = abox.getNode(succIDiter.next());
					assert succ1 != null;

					final NodeMergeInfo<Name, Klass, Role> mergeInfo = abox.mergeNodes(succ0, succ1);
					/* remove the merged nodes from the TODO list */
					nodes.removeAll(mergeInfo.getMergedNodes());

					/*
					 * XXX - it may be possible to do this more efficiently.
					 * restart node iteration
					 */
					nodeIter = nodes.iterator();
					skip = true;
					break;
				}
			}

			if (!skip)
				nodeIter.remove();
		}
	}

//	private Queue<Branch<Name, Klass, Role>> prepareInitialBranchQueue(final Branch<Name, Klass, Role> initialBranch)
//	{
//		Queue<Branch<Name, Klass, Role>> branchQueue = new StackQueue<Branch<Name, Klass, Role>>();
//		// new Stac<Branch<Name, Klass, Role>>();
//		branchQueue.add(initialBranch);
//		return branchQueue;
//	}

	private BranchTree<Name, Klass, Role> prepareBranchTree(final Branch<Name, Klass, Role> initialBranch)
	{
		final BranchTree<Name, Klass, Role> branchTree = new BranchTree<Name, Klass, Role>();
		branchTree.fork(branchTree.getRoot(), initialBranch);
		return branchTree;
	}

	/// </editor-fold>

	/**
	 * <p> Check if the specified concept description is consistent with regard to the specified {@link IABox}, i.e. if
	 * there is a model of the specified concept. </p><p>
	 *
	 *
	 * @param abox An {@link IABox} representing background knowledge
	 * @param concept The concept whose consistency should be verified.
	 * @param stopAtFirstModel Stop consistency checking at first found model.
	 * @return A list of consistent completions of {@literal abox}.
	 * @throws EInconsistentOntologyException The concept is not consistent with regard to {@literal abox}
	 * @throws EReasonerException When a reasoner error occured.
	 *
	 */
	public Collection<? extends IReasonerResult<Name, Klass, Role>> checkConsistency(final IABox<Name, Klass, Role> abox,
																					 final IDLRestriction<Name, Klass, Role> concept,
																					 final boolean stopAtFirstModel)
		throws EReasonerException, EInconsistencyException 
	{
		Branch<Name, Klass, Role> initialBranch;
		initialBranch = prepareInitialBranch(abox);
		final IABoxNode<Name, Klass, Role> conceptNode = initialBranch.getABox().createNode(false);
		conceptNode.addUnfoldedDescription(concept);

		final List<ICompleter<Name, Klass, Role>> generatingCompleters = setupGeneratingCompleters(
			initialBranch.getABox());
		final List<ICompleter<Name, Klass, Role>> nonGeneratingCompleters = setupNonGeneratingCompleters(
			initialBranch.getABox());

		final BranchTree<Name, Klass, Role> branchTree = new BranchTree<Name, Klass, Role>();
		branchTree.fork(branchTree.getRoot(), initialBranch);

		return complete(branchTree, nonGeneratingCompleters, generatingCompleters, stopAtFirstModel);
	}


	/**
	 * <p> Perform tableaux expansion on a copy of the supplied ABox, testing for consistency. The input ABox will not
	 * be modified. </p><p> Returns the list of possible consistent completions of {@literal abox}. </p>
	 *
	 * @param abox The {@link ABox} to expand.
	 * @param stopAtFirstModel Stop at the first encountered model?
	 * @return A collection of consistent aboxes that are expansions of the input abox.
	 * @throws EInconsistentOntologyException No consistent ABox was found.
	 * @throws EReasonerException When a reasoner error occurred.
	 */
	public Collection<? extends IReasonerResult<Name, Klass, Role>> checkConsistency(final IABox<Name, Klass, Role> abox,
																					 final boolean stopAtFirstModel)
		throws EReasonerException, EInconsistencyException
	{
		try {
			final Branch<Name, Klass, Role> initialBranch = prepareInitialBranch(abox);

			final List<ICompleter<Name, Klass, Role>> generatingCompleters = setupGeneratingCompleters(
				initialBranch.getABox());
			final List<ICompleter<Name, Klass, Role>> nonGeneratingCompleters = setupNonGeneratingCompleters(
				initialBranch.getABox());

			return complete(initialBranch, nonGeneratingCompleters, generatingCompleters, stopAtFirstModel);
		} catch (ENodeMergeException ex) {
			throw new EInconsistentABoxNodeException(ex.getSourceNode());
		}
	}


	/**
	 * Determine, if presumedSub is a subclass of presumedSuper with regard to the given ABox.
	 *
	 * @param abox The abox to use as background knowledge
	 * @param presumedSub The presumed subconcept
	 * @param presumedSuper The presumed super concept
	 * @return {@literal true} if presumedSub is a proper subconcept of presumedSuper.
	 * @throws EReasonerException A reasoner error has occured.
	 */
	public boolean isSubClassOf(
		final IABox<Name, Klass, Role> abox,
		final IDLRestriction<Name, Klass, Role> presumedSub,
		final IDLRestriction<Name, Klass, Role> presumedSuper)
		throws EReasonerException, EInconsistencyException
	{
		assert isConsistent(abox);

		try {
			if (TermUtil.isSyntacticSubClass(presumedSub, presumedSuper, abox.getDLTermFactory()))
				return true;
			else {
				final Branch<Name, Klass, Role> initialBranch = prepareInitialBranch(abox);
				IABoxNode<Name, Klass, Role> node = initialBranch.getABox().createNode(false);
				final IDLTermFactory<Name, Klass, Role> termFactory = initialBranch.getABox().getDLTermFactory();
				final IDLRestriction<Name, Klass, Role> sub = TermUtil.toNNF(presumedSub, termFactory);
				IDLRestriction<Name, Klass, Role> negSuper = termFactory.getDLNegation(presumedSuper);
				negSuper = TermUtil.toNNF(negSuper, termFactory);

				/**
				 * track node merges
				 *
				 */
				node = node.addUnfoldedDescription(sub).getCurrentNode();
				node.addUnfoldedDescription(negSuper);
				try {

					final List<ICompleter<Name, Klass, Role>> generatingCompleters = setupGeneratingCompleters(
						initialBranch.getABox());
					final List<ICompleter<Name, Klass, Role>> nonGeneratingCompleters = setupNonGeneratingCompleters(
						initialBranch.getABox());

					complete(initialBranch, nonGeneratingCompleters, generatingCompleters, true);
				} catch (EInconsistentABoxException ex) {
					return true;
				}
				return false;
			}
		} catch (ENodeMergeException ex) {
			throw new EInconsistentABoxNodeException(ex.getSourceNode());
		}
	}


	private boolean isConsistent(Branch<Name, Klass, Role> initialBranch,
								 final List<ICompleter<Name, Klass, Role>> nonGeneratingCompleters,
								 final List<ICompleter<Name, Klass, Role>> generatingCompleters)
		throws EReasonerException, EInconsistencyException
	{
		try {
			complete(initialBranch, nonGeneratingCompleters, generatingCompleters, true);
		} catch (EInconsistentABoxException ex) {
			return false;
		}
		return true;
	}


	/**
	 * Check, if desc in the domain of role.
	 *
	 * @param abox The ABox to check against.
	 * @param desc The class expression to check
	 * @param role The role
	 * @return {@literal true} if {@literal desc} lies in the domain of {@literal role}
	 */
	public boolean isInDomain(final IABox<Name, Klass, Role> abox, final IDLRestriction<Name, Klass, Role> desc,
							  final Role role)
		throws EReasonerException, EInconsistencyException
	{
		try {
			final Collection<IDLRestriction<Name, Klass, Role>> roleDomains = abox.getTBox().getRBox().getRoleDomains().
				get(role);

			/*
			 * shortcut check first, avoid reasoning
			 */
			if ((roleDomains != null) && roleDomains.contains(desc))
				return true;
			else {
				final Branch<Name, Klass, Role> initialBranch = prepareInitialBranch(abox);

				/*
				 * remember to use createNode from branch here!
				 */
				final IABoxNode<Name, Klass, Role> node = initialBranch.getABox().createNode(false);
				node.addUnfoldedDescription(desc);
				final boolean isDataTypeRole = abox.getTBox().getRBox().hasRoleType(role, RoleType.DATA_PROPERTY);
				final IABoxNode<Name, Klass, Role> succ = initialBranch.getABox().createNode(isDataTypeRole);
				node.getLinkMap().getAssertedSuccessors().put(role, succ.getNodeID());

				final List<ICompleter<Name, Klass, Role>> generatingCompleters = setupGeneratingCompleters(
					initialBranch.getABox());
				final List<ICompleter<Name, Klass, Role>> nonGeneratingCompleters = setupNonGeneratingCompleters(
					initialBranch.getABox());

				return isConsistent(initialBranch, nonGeneratingCompleters, generatingCompleters);
			}
		} catch (ENodeMergeException ex) {
			throw new EInconsistentABoxNodeException(ex.getSourceNode());
		}
	}

//	/**
//	 * Determine, if {@literal desc} is exactly the domain of {@literal role}
//	 * @param abox The ABox to check against.
//	 * @param desc The description to check
//	 * @param role The role
//	 * @return {@literal true} if {@literal desc} is exactly the domain of {@literal role}
//	 */
//	public boolean isDomain(final IABox<Name, Klass, Role> abox, final IDLRestriction<Name, Klass, Role> desc,
//		final Role role)
//		throws EReasonerException
//	{
//		final Collection<IDLRestriction<Name, Klass, Role>> roleDomains = abox.getTBox().getRBox().getRoleDomains().
//			get(role);
//
//		/* shortcut check first, avoid reasoning */
//		if ((roleDomains != null) && roleDomains.contains(desc))
//			return true;
//		else {
//			/**
//			 * desc is the domain of role, if there cannot be an individual
//			 * with (not desc) and an arbitrary role-successor.
//			 **/
//			final Branch<Name, Klass, Role> initialBranch = prepareInitialBranch(abox);
//			/* remember to use createNode from branch here! */
//			final IABoxNode<Name, Klass, Role> node = initialBranch.getABox().createNode(false);
//			node.getTerms().add(
//				TermUtil.toNNF(abox.getDLTermFactory().getDLNegation(desc), abox.getDLTermFactory()));
//			final boolean isDataTypeRole = abox.getTBox().getRBox().hasRoleProperty(role, RoleProperty.DATA_PROPERTY);
//			final IABoxNode<Name, Klass, Role> succ = initialBranch.getABox().createNode(isDataTypeRole);
//			node.getSuccessorPairs().put(role, succ.getNodeID());
//			return !isConsistent(initialBranch);
//		}
//	}

	public boolean isInRange(final IABox<Name, Klass, Role> abox, final IDLRestriction<Name, Klass, Role> desc,
							 final Role role)
		throws EReasonerException, EInconsistencyException
	{
		try {
			final Collection<IDLRestriction<Name, Klass, Role>> roleDomains = abox.getTBox().getRBox().getRoleRanges().
				get(role);
			/*
			 * shortcut check first, avoid reasoning
			 */
			if ((roleDomains != null) && roleDomains.contains(desc))
				return true;
			{
				final Branch<Name, Klass, Role> initialBranch = prepareInitialBranch(abox);

				final IABoxNode<Name, Klass, Role> node = initialBranch.getABox().createNode(false);
				node.addUnfoldedDescription(desc);
				final boolean isDataTypeRole = abox.getTBox().getRBox().hasRoleType(role, RoleType.DATA_PROPERTY);
				final IABoxNode<Name, Klass, Role> succ = initialBranch.getABox().createNode(isDataTypeRole);
				succ.getLinkMap().getAssertedSuccessors().put(role, node.getNodeID());

				final List<ICompleter<Name, Klass, Role>> generatingCompleters = setupGeneratingCompleters(
					initialBranch.getABox());
				final List<ICompleter<Name, Klass, Role>> nonGeneratingCompleters = setupNonGeneratingCompleters(
					initialBranch.getABox());

				return isConsistent(initialBranch, nonGeneratingCompleters, generatingCompleters);
			}
		} catch (ENodeMergeException ex) {
			throw new EInconsistentABoxNodeException(ex.getSourceNode());
		}
	}


	public boolean isDisjoint(
		final IABox<Name, Klass, Role> abox,
		final IDLRestriction<Name, Klass, Role> desc1,
		final IDLRestriction<Name, Klass, Role> desc2)
		throws EReasonerException, EInconsistencyException
	{
		try {
			if (desc1.equals(desc2))
				/*
				 * terms are equal, cannot be disjoint
				 */
				return false;
			else if (TermUtil.isSyntacticNegation(desc1, desc2, abox.getDLTermFactory()))
				/*
				 * syntactic shortcut check
				 */
				return true;
			else {
				final Branch<Name, Klass, Role> initialBranch = prepareInitialBranch(abox);
				IABoxNode<Name, Klass, Role> node = initialBranch.getABox().createNode(false);
				/**
				 * Add both descriptions to the same node. If the resulting ABox is inconsistent, the concepts are
				 * disjoint
				 *
				 */
				node = node.addUnfoldedDescription(desc1).getCurrentNode();
				node.addUnfoldedDescription(desc2);

				final List<ICompleter<Name, Klass, Role>> generatingCompleters = setupGeneratingCompleters(
					initialBranch.getABox());
				final List<ICompleter<Name, Klass, Role>> nonGeneratingCompleters = setupNonGeneratingCompleters(
					initialBranch.getABox());

				return !isConsistent(initialBranch, generatingCompleters, nonGeneratingCompleters);
			}
		} catch (ENodeMergeException ex) {
			throw new EInconsistentABoxNodeException(ex.getSourceNode());
		}
	}

//	public boolean isRange(final IABox<Name, Klass, Role> abox, final IDLRestriction<Name, Klass, Role> desc,
//		final Role role)
//		throws EReasonerException
//	{
//		/* shortcut check first, avoid reasoning */
//		final Collection<IDLRestriction<Name, Klass, Role>> roleRanges = abox.getTBox().getRBox().getRoleRanges().
//			get(role);
//		if ((roleRanges != null) && roleRanges.contains(desc))
//			return true;
//		else {
//			/**
//			 * desc is the range of role, if there cannot be an individual
//			 * with (not desc) and an arbitrary role-predecessor.
//			 **/
//			final Branch<Name, Klass, Role> initialBranch = prepareInitialBranch(abox);
//			/* remember to use createNode from branch here! */
//			final IABoxNode<Name, Klass, Role> node = initialBranch.getABox().createNode(false);
//			node.getTerms().add(
//				TermUtil.toNNF(abox.getDLTermFactory().getDLNegation(desc), abox.getDLTermFactory()));
//			final boolean isDataTypeRole = abox.getTBox().getRBox().hasRoleProperty(role, RoleProperty.DATA_PROPERTY);
//			final IABoxNode<Name, Klass, Role> succ = initialBranch.getABox().createNode(isDataTypeRole);
//			node.getSuccessorPairs().put(role, succ.getNodeID());
//			return !isConsistent(initialBranch);
//		}
//	}
	/// <editor-fold desc="ABox completion">

	private Collection<? extends ReasonerResult<Name, Klass, Role>> complete(
		final Branch<Name, Klass, Role> initialBranch,
		final List<ICompleter<Name, Klass, Role>> nonGeneratingCompleters,
		final List<ICompleter<Name, Klass, Role>> generatingCompleters,
		final boolean stopAtFirstModel)
		throws EReasonerException, EInconsistencyException
	{
		BranchTree<Name, Klass, Role> branchTree = prepareBranchTree(initialBranch);
		return complete(branchTree, nonGeneratingCompleters, generatingCompleters, stopAtFirstModel);
	}


	/**
	 * Perform completion on all branches below {@literal branchPoint}.
	 *
	 * @param branchQueue A queue of branches to complete. Will be extended
	 * @param stopAtFirstModel Shall we stop at the first model or determinal ALL saturated tableaux
	 * @return A collection of consistent {@link IABox}es.
	 * @throws EInconsistentOntologyException No consistent ABox was found.
	 * @throws EReasonerException A reasoner error occured
	 */
	private Collection<? extends ReasonerResult<Name, Klass, Role>> complete(
		final BranchTree<Name, Klass, Role> branchTree,
		final List<ICompleter<Name, Klass, Role>> nonGeneratingCompleters,
		final List<ICompleter<Name, Klass, Role>> generatingCompleters,
		final boolean stopAtFirstModel)
		throws EReasonerException, EInconsistencyException
	{
		final Collection<ReasonerResult<Name, Klass, Role>> reasonerResults = new HashSet<ReasonerResult<Name, Klass, Role>>();

		EInconsistentABoxException lastEx = null;

		IDecisionTree.Node<Branch<Name, Klass, Role>> branchNode = pickBranch(branchTree);
		while ((branchNode != null) && ((!stopAtFirstModel) || reasonerResults.isEmpty())) {
			try {
				final ReasonerContinuationState contState = completeBranch(branchNode, nonGeneratingCompleters,
																		   generatingCompleters);
				DecisionTree.checkDecisionTree(branchTree);
				if (contState == ReasonerContinuationState.DONE) {
					/*
					 * store completed branch
					 */
					reasonerResults.add(branchNode.getData().dispose());
					/*
					 * remove branch from branch tree
					 */
					branchNode.remove();
				}
			} catch (EInconsistentABoxException ex) {
				DecisionTree.checkDecisionTree(branchTree);
				/*
				 * remove inconsistent branch from branch tree
				 */
				/*
				 * TODO: dependency directed backtracking
				 */
				final Branch<Name, Klass, Role> thisBranch = branchNode.getData();
				branchNode.remove();
				thisBranch.dispose();
				lastEx = ex;
			}
			/*
			 * pick next branch
			 */
			branchNode = pickBranch(branchTree);
		}

		if (getReasonerOptions().TRACE) {
			logFine("Reasoning complete");
			if (reasonerResults.isEmpty())
				logFiner("No models found");
			else {
				logFinest("The following models were found:");
				for (ReasonerResult<Name, Klass, Role> result : reasonerResults) {
					logFinest(result);
				}
			}
		}

		if (reasonerResults.isEmpty())
			throw lastEx;
		else {
			assert reasonerResults != null;
			assert !reasonerResults.isEmpty();
			return reasonerResults;
		}
	}


	/**
	 * <p> Pick the first branch from {@literal branchQueue} and perform ABox completion on its {@link IABox}. </p><p>
	 * Completition rules are applied according to the following algorithm. <ul> <li> A node is picked from the first
	 * branch's {@link Branch#getNonGeneratingQueue() }. </li><li> The list of {@link ICompleter}s from the {@link #_nonGeneratingCompleters}
	 * collection is traversed. </li><li> Each completer's {@link ICompleter#completeNode(de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode, de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.Branch, java.util.Collection)
	 * }
	 * method is invoked on the current node. This may re-add nodes either of both completion queues. </li><li> If the
	 * current completer's completion method returned {@literal true}, the next completer in the list will be used.
	 * Otherwise the current completion loop is aborted and completion restarts with picking a node from the
	 * non-generating queue. </li><li> The process is repeated until the non-generating queue is empty. </li><li> When
	 * no more non-generating rules can be applied, the first node is picked from the current branch's
	 *	   {@link Branch#getGeneratingQueue() }. </li><li> The list of {@link ICompleter}s from the {@link #_generatingCompleters}
	 * collection is traversed. </li><li> Each completer's {@link ICompleter#completeNode(de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode, de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.Branch, java.util.Collection)
	 * }
	 * method is invoked on the current node. This may create new nodes. </li><li> If non-generating completer returned {@literal false},
	 * this indicates a new node was generated. In this case, the algorithm returns to apply non-generating rules.
	 * </li><li> If no generating rule was applied, the next node is picked from the generating queue. </li><li> The
	 * process repeats until the generating queues is empty. </li> </ul> </p><p> Due to queue sorting, the current
	 * branch may change multiple times during the completion process. The function will return the first branch from
	 * the branch queue where no completion rules are applicable any more. </p>
	 *
	 * @param bt The {@link BranchTree}.
	 * @return A completed branch
	 * @throws EReasonerException, EInconsistentOntologyException
	 */
	private ReasonerContinuationState completeBranch(
		final IDecisionTree.Node<Branch<Name, Klass, Role>> branchNode,
		final List<ICompleter<Name, Klass, Role>> nonGeneratingCompleters,
		final List<ICompleter<Name, Klass, Role>> generatingCompleters)
		throws EReasonerException, EInconsistencyException
	{
		int nLoops = 0;

		assert branchNode != null;
		final Branch<Name, Klass, Role> branch = branchNode.getData();
		assert branch != null;
		if (getReasonerOptions().TRACE)
			logFine("Before completion: %s", branch);

		/**
		 * Iterate over both queues, performing completion steps in the required order (non-gen first, one generational
		 * step next)
		 *
		 */
		while (branch.hasMoreNonGeneratingNodes() || branch.hasMoreGeneratingNodes()) {
			while (branch.hasMoreNonGeneratingNodes()) {
				final IABoxNode<Name, Klass, Role> nextNonGenNode = branch.nextNonGeneratingNode();
				getNodeConsistencyChecker().checkConsistency(nextNonGenNode);
				/**
				 * Run any non-generating completers until one of them signals that we have to recheck to node queue or
				 * branch tree, or we run out of nodes on the non-generating queue.
				 */
				for (ICompleter<Name, Klass, Role> nonGenCompleter : nonGeneratingCompleters) {
					final ReasonerContinuationState contState = nonGenCompleter.completeNode(nextNonGenNode, branchNode);
					if (contState == ReasonerContinuationState.RECHECK_NODE)
						break;
					else if (contState == ReasonerContinuationState.RECHECK_BRANCH)
						return contState;
				}
			}
			if (getReasonerOptions().TRACE)
				logFiner("After non-generating steps: %s", branch);

			/**
			 * Pick the next node from the generating queue until at least one generating step took place or we run out
			 * of nodes.
			 *
			 */
			IABoxNode<Name, Klass, Role> nextGenNode;
			boolean wasGenerated = false;
			do {
				nextGenNode = branch.nextGeneratingNode();
				if (nextGenNode != null) {
					getNodeConsistencyChecker().checkConsistency(nextGenNode);
					/**
					 * Run any generating completers until one of them signals that we have to recheck to node queue or
					 * branch tree, or we run out of nodes on the non-generating queue.
					 */
					for (ICompleter<Name, Klass, Role> genCompleter : generatingCompleters) {
						final ReasonerContinuationState contState = genCompleter.completeNode(nextGenNode, branchNode);
						if (contState == ReasonerContinuationState.RECHECK_NODE)
							break;
						else if (contState == ReasonerContinuationState.RECHECK_BRANCH)
							return contState;
					}
				}
			} while ((nextGenNode != null) && (!wasGenerated));
			if (getReasonerOptions().LOG_PROGRESS) {
				if (getReasonerOptions().TRACE || (nLoops % 100 == 0))
					logFinest("Branch %s: %d iterations, %d nodes on queue", branchNode.getPath(), nLoops,
							  branch.getGeneratingQueue().size() + branch.getNonGeneratingQueue().size());
				++nLoops;
			}
			if (getReasonerOptions().TRACE)
				logFiner("After generating step: %s", branch);
		}

		return ReasonerContinuationState.DONE;
	}


	private IDecisionTree.Node<Branch<Name, Klass, Role>> pickBranch(final BranchTree<Name, Klass, Role> branchTree)
	{
		/*
		 * descend to leaf
		 */
		return branchTree.firstLeaf();
	}


	/**
	 * @return the _nodeConsistencyChecker
	 */
	public INodeConsistencyChecker<Name, Klass, Role> getNodeConsistencyChecker()
	{
		return _nodeConsistencyChecker;
	}


	/**
	 * @param nodeConsistencyChecker the _nodeConsistencyChecker to set
	 */
	public void setNodeConsistencyChecker(INodeConsistencyChecker<Name, Klass, Role> nodeConsistencyChecker)
	{
		this._nodeConsistencyChecker = nodeConsistencyChecker;
	}


	/**
	 * @return the _reasonerOptions
	 */
	protected ReasonerOptions getReasonerOptions()
	{
		return _reasonerOptions;
	}
/// </editor-fold>
}