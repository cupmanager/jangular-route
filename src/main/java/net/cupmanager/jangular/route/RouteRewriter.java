package net.cupmanager.jangular.route;


public interface RouteRewriter<R extends Route<R>, U> {
	public R rewrite(R route, U user);
}
