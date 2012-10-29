/**
 * (c) 2009-2012 Otto-Friedrich-University Bamberg
 *
 * $Id$
 *
 * Use, modification and restribution of this file are covered by the terms of the Artistic License 2.0.
 *
 * You should have received a copy of the license terms in a file named "LICENSE" together with this software package.
 *
 * Disclaimer of Warranty: THE PACKAGE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS "AS IS' AND WITHOUT ANY
 * EXPRESS OR IMPLIED WARRANTIES. THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT ARE DISCLAIMED TO THE EXTENT PERMITTED BY YOUR LOCAL LAW. UNLESS REQUIRED BY LAW, NO COPYRIGHT
 * HOLDER OR CONTRIBUTOR WILL BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING IN ANY
 * WAY OUT OF THE USE OF THE PACKAGE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
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
import de.dhke.projects.cutil.collections.tree.IDecisionTree;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.ABox;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.IntersectionCompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.ForAllCompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.FunctionalRoleMergeCompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.ICompleter;
import de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.completer.InverseRoleAssertionCompleter;
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
 * @param <Name> The type for nominals and values
 * @param <Klass> The type for DL classes
 * @param <Role> The type for properties (roles)
 * @author Peter Wullinger <peter.wullinger@uni-bamberg.de>
 */
public class Reasoner<Name extends Comparable<? super Name>, Klass extends Comparable<? super Klass>, Role extends Comparable<? super Role>>
	extends AbstractReasoner<Name, Klass, Role>
{
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
		final IAssertedRBox<Name, Klass, Role> rbox = abox.getTBox().getRBox();

		final List<ICompleter<Name, Klass, Role>> nonGeneratingCompleters = new ArrayList<ICompleter<Name, Klass, Role>>();

		nonGeneratingCompleters.add(new RoleRestrictionCompleter<Name, Klass, Role>(getNodeConsistencyChecker(),
																					getReasonerOptions().TRACE));
		nonGeneratingCompleters.add(new IntersectionCompleter<Name, Klass, Role>(getNodeConsistencyChecker(),
																				 getReasonerOptions().TRACE));
		nonGeneratingCompleters.add(new ForAllCompleter<Name, Klass, Role>(getNodeConsistencyChecker(),
																		   getReasonerOptions().TRACE));
		if (!rbox.getRoles(RoleProperty.TRANSITIVE).isEmpty()) {
			nonGeneratingCompleters.add(new TransitiveRoleCompleter<Name, Klass, Role>(_nodeConsistencyChecker,
																					   getReasonerOptions().TRACE));
		}
		if (!rbox.getRoles(RoleProperty.SYMMETRIC).isEmpty()) {
			nonGeneratingCompleters.add(new SymmetricRoleCompleter<Name, Klass, Role>(_nodeConsistencyChecker,
																					  getReasonerOptions().TRACE));
		}

		nonGeneratingCompleters.add(new UnionCompleter<Name, Klass, Role>(getNodeConsistencyChecker(),
																		  getReasonerOptions().TRACE));

		final boolean needDoubleBlocking = rbox.hasInverseRoles();
		if (needDoubleBlocking)
			nonGeneratingCompleters.
				add(new InverseRoleAssertionCompleter<Name, Klass, Role>(_nodeConsistencyChecker,
																		 getReasonerOptions().TRACE));


		if ((!rbox.getRoles(RoleProperty.FUNCTIONAL).isEmpty()) || (!rbox.getRoles(RoleProperty.INVERSE_FUNCTIONAL).
			isEmpty())) {
			nonGeneratingCompleters.add(new FunctionalRoleMergeCompleter<Name, Klass, Role>(_nodeConsistencyChecker,
																							getReasonerOptions().TRACE));
		}

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
	 * @see IABoxNode#addUnfoldedDescription(de.uniba.wiai.kinf.pw.projects.lillytab.terms.IDLClassExpression)
	 * @param abox
	 */
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

		/**
		 * Make sure, existing terms have been considered for lazy unfolding.
		 *
		 */
		initialBranch.getABox().unfoldAll();

		return initialBranch;
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
	@Override
	public Collection<? extends IReasonerResult<Name, Klass, Role>> checkConsistency(final IABox<Name, Klass, Role> abox,
																					 final IDLRestriction<Name, Klass, Role> concept,
																					 final boolean stopAtFirstModel)
		throws EReasonerException, EInconsistencyException
	{
		// logInfo("REASONER CALL TO isInRange(%s, %s, %s)", abox.getID(), concept, stopAtFirstModel);

		Branch<Name, Klass, Role> initialBranch;
		try {
			initialBranch = prepareInitialBranch(abox);
			final IABoxNode<Name, Klass, Role> conceptNode = initialBranch.getABox().createNode(false);
			conceptNode.addUnfoldedDescription(concept);
		} catch (ENodeMergeException ex) {
			// XXX - better exception */
			throw new EInconsistentABoxException(abox, "Node merge problem");
		}

		final List<ICompleter<Name, Klass, Role>> generatingCompleters = setupGeneratingCompleters(
			initialBranch.getABox());
		final List<ICompleter<Name, Klass, Role>> nonGeneratingCompleters = setupNonGeneratingCompleters(
			initialBranch.getABox());

		final BranchTree<Name, Klass, Role> branchTree = new BranchTree<Name, Klass, Role>();
		branchTree.fork(branchTree.getRoot(), initialBranch);

		return complete(branchTree, nonGeneratingCompleters, generatingCompleters, stopAtFirstModel);
	}

	@Override
	public Collection<? extends IReasonerResult<Name, Klass, Role>> checkConsistency(final IABox<Name, Klass, Role> abox,
																					 final boolean stopAtFirstModel)
		throws EReasonerException, EInconsistencyException
	{
		// logInfo("REASONER CALL TO checkConsistency(%s, %s)", abox.getID(), stopAtFirstModel);
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

	@Override
	public boolean isSubClassOf(
		final IABox<Name, Klass, Role> abox,
		final IDLRestriction<Name, Klass, Role> presumedSub,
		final IDLRestriction<Name, Klass, Role> presumedSuper)
		throws EReasonerException, EInconsistencyException
	{
		// logInfo("REASONER CALL TO isSubClassOf(%s, %s, %s)", abox.getID(), presumedSub, presumedSuper);
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
				 */
				node = node.addUnfoldedDescription(sub).getCurrentNode();
				/* node = */ node.addUnfoldedDescription(negSuper).getCurrentNode();
				final List<ICompleter<Name, Klass, Role>> generatingCompleters = setupGeneratingCompleters(
					initialBranch.getABox());
				final List<ICompleter<Name, Klass, Role>> nonGeneratingCompleters = setupNonGeneratingCompleters(
					initialBranch.getABox());

				final Collection<? extends IReasonerResult<Name, Klass, Role>> results =
					complete(initialBranch, nonGeneratingCompleters, generatingCompleters, true);

				return results.isEmpty();
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
		Collection<? extends IReasonerResult<Name, Klass, Role>> results = complete(initialBranch,
																					nonGeneratingCompleters,
																					generatingCompleters, true);
		return !results.isEmpty();
	}

	@Override
	public boolean isInDomain(final IABox<Name, Klass, Role> abox, final IDLRestriction<Name, Klass, Role> desc,
							  final Role role)
		throws EReasonerException, EInconsistencyException
	{
		// logInfo("REASONER CALL TO isInDomain(%s, %s, %s)", abox.getID(), desc, role);
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
				node.getRABox().getAssertedSuccessors().put(role, succ.getNodeID());

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

	@Override
	public boolean isInRange(final IABox<Name, Klass, Role> abox, final IDLRestriction<Name, Klass, Role> desc,
							 final Role role)
		throws EReasonerException, EInconsistencyException
	{
		logInfo("REASONER CALL TO isInRange(%s, %s, %s)", abox.getID(), desc, role);
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
				succ.getRABox().getAssertedSuccessors().put(role, node.getNodeID());

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

	@Override
	public boolean isDisjoint(
		final IABox<Name, Klass, Role> abox,
		final IDLRestriction<Name, Klass, Role> desc1,
		final IDLRestriction<Name, Klass, Role> desc2)
		throws EReasonerException, EInconsistencyException
	{
		logInfo("REASONER CALL TO isDisjoint(%s, %s, %s)", abox.getID(), desc1, desc2);
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

		IDecisionTree.Node<Branch<Name, Klass, Role>> branchNode = pickBranch(branchTree);
		while ((branchNode != null) && ((!stopAtFirstModel) || reasonerResults.isEmpty())) {
			final ReasonerContinuationState contState = completeBranch(branchNode,
																	   nonGeneratingCompleters,
																	   generatingCompleters);
			if (contState == ReasonerContinuationState.DONE) {
				/* store completed branch, but only if it is consistent. */
				if (!branchNode.getData().getConsistencyInfo().isInconsistent())
					reasonerResults.add(branchNode.getData().dispose());
				/* remove the current branch */
				branchNode.remove();
			} else {
				/* prune branch tree according to the culprits recorded for the current branch */
				pruneBranchTree(branchTree, branchNode);
			}

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

		return reasonerResults;
	}

	/**
	 * <p> Pick the first branch from {@literal branchQueue} and perform ABox completion on its {@link IABox}. </p><p>
	 * Completition rules are applied according to the following algorithm. <ul> <li> A node is picked from the first
	 * branch's {@link Branch#getNonGeneratingQueue() }. </li><li> The list of {@link ICompleter}s from the
	 * {@link #_nonGeneratingCompleters} collection is traversed. </li><li> Each completer's {@link ICompleter#completeNode(de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode, de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.Branch, java.util.Collection)
	 * }
	 * method is invoked on the current node. This may re-add nodes either of both completion queues. </li><li> If the
	 * current completer's completion method returned {@literal true}, the next completer in the list will be used.
	 * Otherwise the current completion loop is aborted and completion restarts with picking a node from the
	 * non-generating queue. </li><li> The process is repeated until the non-generating queue is empty. </li><li> When
	 * no more non-generating rules can be applied, the first node is picked from the current branch's
	 *	   {@link Branch#getGeneratingQueue() }. </li><li> The list of {@link ICompleter}s from the
	 * {@link #_generatingCompleters} collection is traversed. </li><li> Each completer's {@link ICompleter#completeNode(de.uniba.wiai.kinf.pw.projects.lillytab.abox.IABoxNode, de.uniba.wiai.kinf.pw.projects.lillytab.reasoner.abox.Branch, java.util.Collection)
	 * }
	 * method is invoked on the current node. This may create new nodes. </li><li> If non-generating completer returned
	 * {@literal false}, this indicates a new node was generated. In this case, the algorithm returns to apply
	 * non-generating rules. </li><li> If no generating rule was applied, the next node is picked from the generating
	 * queue. </li><li> The process repeats until the generating queues is empty. </li> </ul> </p><p> Due to queue
	 * sorting, the current branch may change multiple times during the completion process. The function will return the
	 * first branch from the branch queue where no completion rules are applicable any more. </p>
	 *
	 * @param bt The {@link BranchTree}.
	 * @return A completed branch
	 * @throws EReasonerException, EInconsistentOntologyException
	 */
	private ReasonerContinuationState completeBranch(
		final IDecisionTree.Node<Branch<Name, Klass, Role>> branchNode,
		final List<ICompleter<Name, Klass, Role>> nonGeneratingCompleters,
		final List<ICompleter<Name, Klass, Role>> generatingCompleters)
		throws EReasonerException
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
				final ConsistencyInfo<Name, Klass, Role> cInfo = getNodeConsistencyChecker().
					isConsistent(nextNonGenNode);
				if (cInfo.isFinallyInconsistent()) {
					branch.upgradeConsistencyInfo(cInfo);
					return ReasonerContinuationState.INCONSISTENT;
				} else {
					/**
					 * Run any non-generating completers until one of them signals that we have to recheck to node queue
					 * or branch tree, or we run out of nodes on the non-generating queue.
					 */
					for (ICompleter<Name, Klass, Role> nonGenCompleter : nonGeneratingCompleters) {
						final ReasonerContinuationState contState = nonGenCompleter.completeNode(
							nextNonGenNode, branchNode);
						if (contState == ReasonerContinuationState.INCONSISTENT)
							return contState;
						else if (contState == ReasonerContinuationState.RECHECK_NODE)
							break;
					}
				}
			}
			if (getReasonerOptions().TRACE)
				logFiner("After non-generating steps: %s", branch);

			/**
			 * Pick the next node from the generating queue until at least one generating step took place or we run out
			 * of nodes.
			 */
			IABoxNode<Name, Klass, Role> nextGenNode;
			boolean wasGenerated = false;
			do {
				nextGenNode = branch.nextGeneratingNode();
				if (nextGenNode != null) {
					final ConsistencyInfo<Name, Klass, Role> cInfo = getNodeConsistencyChecker().isConsistent(
						nextGenNode);
					if (cInfo.isFinallyInconsistent()) {
						branch.upgradeConsistencyInfo(cInfo);
						return ReasonerContinuationState.INCONSISTENT;
					} else {
						/**
						 * Run any generating completers until one of them signals that we have to recheck to node queue
						 * or branch tree, or we run out of nodes on the non-generating queue.
						 *
						 */
						for (ICompleter<Name, Klass, Role> genCompleter : generatingCompleters) {
							final ReasonerContinuationState contState = genCompleter.completeNode(nextGenNode,
																								  branchNode);
							if ((contState == ReasonerContinuationState.RECHECK_BRANCH) || (contState == ReasonerContinuationState.INCONSISTENT))
								return contState;
							else if (contState == ReasonerContinuationState.RECHECK_NODE)
								wasGenerated = true;
						}
					}
				}
			} while ((nextGenNode != null) && (!wasGenerated));
			if ((getReasonerOptions().LOG_PROGRESS) && (nLoops % 100 == 0)) {
				logFine("Branch %s: %d iterations, %d nodes on queue", branch.getABox().getID(), nLoops,
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

	private void pruneBranchTree(
		final BranchTree<Name, Klass, Role> branchTree,
		final IDecisionTree.Node<Branch<Name, Klass, Role>> clashNode)
	{
		/**
		 * implement dependency directed backtracking: remove braches when the contain the culprit terms for current
		 * clash. (as determined by the clash information
		 */
		if ((clashNode != null) && (clashNode.getData() != null)) {
			final ConsistencyInfo<Name, Klass, Role> cInfo = clashNode.getData().getConsistencyInfo();
			IDecisionTree.Node<Branch<Name, Klass, Role>> nextNode = pickBranch(branchTree);
			int pruneCount = 0;
			while ((nextNode != null) && cInfo.hasClashingTerms(nextNode.getData().getABox())) {
				++pruneCount;
				if ((_reasonerOptions.TRACE) && (pruneCount > 1))
					logFiner("DDB-pruning performed for, removed `%s'", nextNode);
				nextNode.remove();
				nextNode = pickBranch(branchTree);
			}
		}
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
