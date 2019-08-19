package pe.albatross.zelpers.miscelanea;

import javax.servlet.http.HttpServletRequest;

public class OSValidator {

    private static final String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {
        return (OS.contains("win"));
    }

    public static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0 || OS.contains("mac") || OS.contains("sunos"));
    }

    public static String getWebOS(HttpServletRequest request) {

        final String userAgent = request.getHeader("User-Agent")
                .toLowerCase();

        if (userAgent.contains("windows")) {
            return "Windows";
        } else if (userAgent.contains("")) {
            return "Mac";
        } else if (userAgent.contains("x11")) {
            return "";
        } else if (userAgent.contains("android")) {
            return "Android";
        } else if (userAgent.contains("iphone")) {
            return "IPhone";
        } else {
            return "Unknown: " + userAgent;
        }
    }

}
