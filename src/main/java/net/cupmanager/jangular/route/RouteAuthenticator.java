package net.cupmanager.jangular.route;


public interface RouteAuthenticator<R extends Route<R>, U> {
	public boolean isAllowed(R route, U user);
}
