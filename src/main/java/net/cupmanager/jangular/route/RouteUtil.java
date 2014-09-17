package net.cupmanager.jangular.route;

public class RouteUtil {
	public static boolean matchesAny(String string, String[] prefixes) {
		for (String prefix : prefixes) {
			if (string.matches(prefix+".*")) {
				return true;
			}
		}
		return false;
	}
}
