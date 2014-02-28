/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.semanticweb.elk.reasoner.saturation.conclusions.implementation;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.SubConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.LinkedBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.SubsumerBackwardLinkRule;

/**
 * An implementation for {@link BackwardLink}
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 */
public class BackwardLinkImpl extends AbstractConclusion implements
		BackwardLink {

	/**
	 * the source {@link IndexedClassExpression} of this
	 * {@link BackwardLinkImpl}; the root of the source implies this link.
	 */
	private final IndexedClassExpression source_;

	/**
	 * the {@link IndexedObjectProperty} in the existential restriction
	 * corresponding to this link
	 */
	private final IndexedObjectProperty relation_;

	public BackwardLinkImpl(IndexedClassExpression source,
			IndexedObjectProperty relation) {
		this.relation_ = relation;
		this.source_ = source;
	}

	@Override
	public void applyNonRedundantRules(RuleVisitor ruleAppVisitor,
			ContextPremises premises, ConclusionProducer producer) {

		ruleAppVisitor.visit(SubsumerBackwardLinkRule.getInstance(), this,
				premises, producer);

		// apply all backward link rules of the context
		LinkedBackwardLinkRule backLinkRule = premises
				.getBackwardLinkRuleHead();
		while (backLinkRule != null) {
			backLinkRule.accept(ruleAppVisitor, this, premises, producer);
			backLinkRule = backLinkRule.next();
		}
	}

	@Override
	public IndexedClassExpression getSourceRoot(
			IndexedClassExpression rootWhereStored) {
		return source_;
	}

	@Override
	public IndexedObjectProperty getSubRoot() {
		return relation_;
	}

	@Override
	public String toString() {
		return (relation_ + "<-" + source_);
	}

	@Override
	public <I, O> O accept(ConclusionVisitor<I, O> visitor, I input) {
		return accept((SubConclusionVisitor<I, O>) visitor, input);
	}

	@Override
	public <I, O> O accept(SubConclusionVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public IndexedObjectProperty getRelation() {
		return relation_;
	}

	@Override
	public IndexedClassExpression getSource() {
		return source_;
	}

}