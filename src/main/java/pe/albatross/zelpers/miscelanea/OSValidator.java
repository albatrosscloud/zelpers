package pe.albatross.zelpers.miscelanea;

public class OSValidator {

    private static final String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {
        return (OS.contains("win"));
    }

    public static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0 || OS.contains("mac") || OS.contains("sunos"));
    }

}
