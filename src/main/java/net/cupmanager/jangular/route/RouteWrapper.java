package net.cupmanager.jangular.route;

import com.google.common.base.Function;

public class RouteWrapper<T extends Route<T>> implements Route<T> {
	
	private Route route;
	private Function<Route, T> constructor;
	
	protected RouteWrapper(Route route, Function<Route,T> constructor) {
		this.route = route;
		this.constructor = constructor;
	}

	public T query(String key, Object value) {
		return constructor.apply(route.query(key, value));
	}

	public String query(String key) {
		return route.query(key);
	}

	public T withoutQuery(String key) {
		return constructor.apply(route.withoutQuery(key));
	}
	
	public String pathPart(int index) {
		return route.pathPart(index);
	}

	public T to(Object to) {
		return constructor.apply(route.to(to));
	}

	public T up() {
		return constructor.apply(route.up());
	}
	
	public T pathPart(int idx, Object v) {
		return constructor.apply(route.pathPart(idx, v));
	}

	public String path() {
		return route.path();
	}

	public String toString() {
		return route.toString();
	}

	public String queryString() {
		return route.queryString();
	}

	public int hashCode() {
		return route.hashCode();
	}

	public boolean equals(Object obj) {
		if (obj instanceof RouteWrapper) {
			RouteWrapper rw = (RouteWrapper) obj;
			return route.equals(rw.route);
		} else {
			return false;
		}
	}
	
}
