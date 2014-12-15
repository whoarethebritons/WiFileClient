/**
 * Created by Eden on 12/13/2014.
 */
public class WiFileLauncher {
    static WiFile wflWiFile;
    static BackgroundWiFile wflBackground;

    public static void main(String[] args) {
        //the gui application
        wflWiFile = new WiFile();
        //the system tray icon
        //needs WiFile param in order to see if window is open
        wflBackground = new BackgroundWiFile(wflWiFile);
    }
}
