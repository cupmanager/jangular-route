package net.cupmanager.jangular.route;

import org.apache.commons.lang.StringUtils;

public class RouteUtil {
	public static boolean matchesAny(String string, String[] prefixes) {
		for (String prefix : prefixes) {
			if (string.matches(prefix+".*")) {
				return true;
			}
		}
		return false;
	}

	public static int parseInt(String query, int i) {
		if (StringUtils.isNumeric(query)) {
			return Integer.parseInt(query);
		} else {
			return i;
		}
	}
}
