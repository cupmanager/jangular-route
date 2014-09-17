package net.cupmanager.jangular.route;

import net.cupmanager.jangular.injection.EvaluationContext;

public interface EvaluationContextSupplier<R extends Route<R>, U> {
	public EvaluationContext supply(R route, U u);
}
