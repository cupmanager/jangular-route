package net.cupmanager.jangular.route;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

class RouteImpl implements Route<RouteImpl> {
	
	private List<String> pathParts = new ArrayList<String>();
	private Map<String,String> query = new LinkedHashMap<String,String>();
	private String prefix;
	
	RouteImpl(String path, String prefix) {
		this.prefix = prefix;
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		String[] partsQuery = path.split("\\?");
		String parts = partsQuery[0];
		if (path.isEmpty()) {
			pathParts = new ArrayList<String>();
		} else {
			pathParts = Arrays.asList(parts.split("/"));
		}
		
		if (partsQuery.length > 1) {
			String q = partsQuery[1];
			String[] kvs = q.split("&");
			for (String kv : kvs) {
				String[] _kv = kv.split("=");
				String key = _kv[0];
				String value = _kv.length > 1 ? _kv[1] : null;
				query.put(key, value);
			}
		}
	}
	
	RouteImpl(List<String> pathParts, Map<String,String> query, String prefix) {
		this.pathParts = new ArrayList<String>(pathParts);
		this.query = new LinkedHashMap<String,String>(query);
		this.prefix = prefix;
	}
	
	
	@Override
	public RouteImpl query(String key, Object value) {
		if (value == null) return this;
		Map<String, String> map = new LinkedHashMap<String,String>(query);
		map.put(key,""+value);
		return new RouteImpl(pathParts, map, prefix);
	}
	
	@Override
	public RouteImpl withoutQuery(String key) {
		Map<String, String> map = new LinkedHashMap<String,String>(query);
		map.remove(key);
		return new RouteImpl(pathParts, map, prefix);
	}

	@Override
	public String query(String key) {
		return query.get(key);
	}
	
	@Override
	public String pathPart(int index) {
		if (index < pathParts.size()) {
			return pathParts.get(index);
		} else {
			return null;
		}
	}
	
	@Override
	public RouteImpl pathPart(int idx, Object v) {
		RouteImpl r = new RouteImpl(pathParts, query, prefix);
		if (idx < pathParts.size()) {
			r.pathParts.set(idx, v.toString());
			while (idx < r.pathParts.size()-1) {
				r.pathParts.remove(idx+1);
			}
		} else if (idx == pathParts.size()) {
			r.pathParts.add(v.toString());
		} else {
			throw new RuntimeException("Cant set pathpart this deep (i="+idx+", size="+pathParts.size()+")");
		}
		return r;
	}

	
	@Override
	public RouteImpl to(Object to) {
		String topath = to.toString();
		if (!topath.startsWith("/")) {
			String p = path();
			if (!p.endsWith("/")) {
				p += "/";
			}
			topath = p + topath;
		}
		RouteImpl r = new RouteImpl(topath, prefix);
		if (r.query.isEmpty()) {
			r.query = new LinkedHashMap<String,String>(query);
		}
		return r;
	}
	
	@Override
	public RouteImpl up() {
		return new RouteImpl(
				pathParts.subList(0, pathParts.size()-1), 
				query, 
				prefix);
	}
	
	@Override
	public String path() {
		return "/" + StringUtils.join(pathParts, "/");
	}
	
	@Override
	public String toString() {
		return prefix + path() + queryString();
	}
	
	@Override
	public String queryString() {
		String url = "";
		boolean first = true;
		for (Map.Entry<String, String> e : query.entrySet()) {
			url += first ? "?" : "&";
			first = false;
			url += e.getKey();
			if (e.getValue() != null) {
				url += "=" + e.getValue();
			}
		}
		return url;
	}
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pathParts == null) ? 0 : pathParts.hashCode());
		result = prime * result + ((query == null) ? 0 : query.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RouteImpl other = (RouteImpl) obj;
		if (pathParts == null) {
			if (other.pathParts != null)
				return false;
		} else if (!pathParts.equals(other.pathParts))
			return false;
		if (query == null) {
			if (other.query != null)
				return false;
		} else if (!query.equals(other.query))
			return false;
		return true;
	}
	
	
}