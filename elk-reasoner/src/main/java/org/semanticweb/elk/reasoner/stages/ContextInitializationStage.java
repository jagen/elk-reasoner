/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.stages;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;

// TODO: add progress monitor, make concurrent if possible

/**
 * A {@link ReasonerStage} which purpose is to ensure that no context is
 * assigned to {@link IndexedClassExpression}s of the current ontology
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
class ContextInitializationStage extends AbstractReasonerStage {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(ContextInitializationStage.class);

	/**
	 * The counter for deleted contexts
	 */
	private int deletedContexts_;
	/**
	 * The number of contexts
	 */
	private int maxContexts_;

	/**
	 * The state of the iterator of the input to be processed
	 */
	private Iterator<IndexedClassExpression> todo = null;

	public ContextInitializationStage(AbstractReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	public String getName() {
		return "Context Initialization";
	}

	@Override
	public boolean done() {
		return reasoner.doneContextReset;
	}

	@Override
	public List<ReasonerStage> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public void execute() throws ElkInterruptedException {
		if (todo == null)
			initComputation();
		try {
			progressMonitor.start(getName());
			for (;;) {
				if (!todo.hasNext())
					break;
				IndexedClassExpression ice = todo.next();
				ice.resetContext();
				deletedContexts_++;
				progressMonitor.report(deletedContexts_, maxContexts_);
				if (interrupted())
					continue;
			}
		} finally {
			progressMonitor.finish();
		}
		reasoner.doneContextReset = true;
	}

	@Override
	void initComputation() {
		super.initComputation();
		todo = reasoner.ontologyIndex.getIndexedClassExpressions().iterator();
		maxContexts_ = reasoner.ontologyIndex.getIndexedClassExpressions()
				.size();
		deletedContexts_ = 0;
	}

	@Override
	public void printInfo() {
		if (deletedContexts_ > 0 && LOGGER_.isDebugEnabled())
			LOGGER_.debug("Contexts deleted:" + deletedContexts_);
	}

}