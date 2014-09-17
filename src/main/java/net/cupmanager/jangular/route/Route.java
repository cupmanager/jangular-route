package net.cupmanager.jangular.route;

public interface Route<R extends Route<R>> {

	public abstract R query(String key, Object value);

	public abstract R withoutQuery(String key);

	public abstract String query(String key);

	public abstract String pathPart(int index);

	public abstract R to(Object to);

	public abstract R up();

	public abstract String path();

	public abstract String toString();

	public abstract String queryString();

	public abstract int hashCode();

	public abstract boolean equals(Object obj);

	public abstract R pathPart(int idx, Object v);

}