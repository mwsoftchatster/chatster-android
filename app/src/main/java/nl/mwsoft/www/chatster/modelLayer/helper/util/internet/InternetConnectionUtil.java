package nl.mwsoft.www.chatster.modelLayer.helper.util.internet;


import java.io.IOException;

public class InternetConnectionUtil {

    public InternetConnectionUtil() {
    }

    public boolean hasInternetConnection() {

        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }
}
