package com.whiteboard.util;

import android.text.format.Formatter;
import android.util.Log;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

public final class NetworkUtils {

    private static final int MIN_PORT_NUMBER = 1100;
    private static final int MAX_PORT_NUMBER = 49151;

    private NetworkUtils() {

    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return Formatter.formatIpAddress(inetAddress.hashCode());
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("NetworkUtils", ex.toString());
        }
        return null;
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
