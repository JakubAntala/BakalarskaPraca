package ukf;

import jakarta.servlet.http.HttpSession;

public class RoleHelper {

    public static boolean hasAccess(HttpSession session, String... allowedRoles) {
        if (session == null || session.getAttribute("role") == null) {
            return false;
        }

        String userRole = (String) session.getAttribute("role");

        for (String role : allowedRoles) {
            if (role.equalsIgnoreCase(userRole)) {
                return true;
            }
        }
        return false;
    }

    public static String getUserRole(HttpSession session) {
        if (session == null) return null;
        return (String) session.getAttribute("role");
    }
}
