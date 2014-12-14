/**
 * Created by Eden on 12/13/2014.
 */
public class WiFileLauncher {
    static WiFile wflWiFile;
    static BackgroundWiFile wflBackground;

    public static void main(String[] args) {
        wflWiFile = new WiFile();
        wflBackground = new BackgroundWiFile(wflWiFile);
    }
}
