package net.cupmanager.jangular.route;

import net.cupmanager.jangular.Scope;

public interface EvaluationScopeSupplier<R extends Route<R>, U> {
	public Scope supply(R route, U u);
}
