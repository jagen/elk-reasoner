/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors;

import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.LeftReflexiveSubPropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.PropertyChainInitialization;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ReflexivePropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ReflexiveToldSubObjectProperty;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.RightReflexiveSubPropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ToldReflexiveProperty;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ToldSubPropertyChain;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ObjectPropertyInferenceVisitor<I, O> {

	public O visit(ToldSubPropertyChain inference, I input);
	
	public O visit(PropertyChainInitialization inference, I input);
	
	public O visit(ToldReflexiveProperty inference, I input);
	
	public O visit(ReflexiveToldSubObjectProperty inference, I input);
	
	public O visit(ReflexivePropertyChainInference inference, I input);
	
	public O visit(LeftReflexiveSubPropertyChainInference inference, I input);
	
	public O visit(RightReflexiveSubPropertyChainInference inference, I input);
	
	public static ObjectPropertyInferenceVisitor<?, ?> DUMMY = new ObjectPropertyInferenceVisitor<Void, Void>() {

		@Override
		public Void visit(PropertyChainInitialization inference, Void input) {
			// no-op
			return null;
		}

		@Override
		public Void visit(ToldReflexiveProperty inference, Void input) {
			// no-op
			return null;
		}

		@Override
		public Void visit(ReflexiveToldSubObjectProperty inference, Void input) {
			// no-op
			return null;
		}

		@Override
		public Void visit(ReflexivePropertyChainInference inference, Void input) {
			// no-op
			return null;
		}

		@Override
		public Void visit(LeftReflexiveSubPropertyChainInference inference,
				Void input) {
			// no-op
			return null;
		}

		@Override
		public Void visit(RightReflexiveSubPropertyChainInference inference,
				Void input) {
			// no-op
			return null;
		}

		@Override
		public Void visit(ToldSubPropertyChain inference, Void input) {
			// no-op
			return null;
		}
		
	};
}