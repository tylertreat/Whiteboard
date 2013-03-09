package com.whiteboard.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import com.clarionmedia.infinitum.context.ContextFactory;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

public final class NetworkUtils {

    private static final int MIN_PORT_NUMBER = 1100;
    private static final int MAX_PORT_NUMBER = 49151;

    private NetworkUtils() {

    }

    public static String getNetworkIpAddress() {
        Context context = ContextFactory.newInstance().getAndroidContext();
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();

        return String.format(
                "%d.%d.%d.%d",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff),
                (ip >> 24 & 0xff));
    }

    public static int getAvailablePort() throws IOException {
        for (int port = MIN_PORT_NUMBER; port <= MAX_PORT_NUMBER; port++) {
            if (isPortAvailable(port))
                return port;
        }
        throw new IOException("No ports available!");
    }

    private static boolean isPortAvailable(int port) {
        if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
            throw new IllegalArgumentException("Invalid start port: " + port);
        }

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                /* should not be thrown */
                }
            }
        }

        return false;
    }

}
