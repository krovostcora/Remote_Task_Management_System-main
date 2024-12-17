public class UserSession {
    private static String loggedInUser;

    public static boolean isLoggedIn() {
        return loggedInUser != null;
    }

    public static void setLoggedInUser(String username) {
        loggedInUser = username;
    }

    public static String getLoggedInUser() {
        return loggedInUser;
    }

    public static void logout() {
        loggedInUser = null;
    }
}