package com.example.nexor.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class SendKeys extends AsyncTask<Object, Void, Void> {

    private static Socket s;
    private boolean mod_ctrl = false;
    private boolean mod_shift = false;
    private boolean mod_super = false;
    private static HashMap<String, Boolean> MODIFIERS = new HashMap<String, Boolean>() {
        {
            put("Ctrl", false);
            put("Super_L", false);
            put("Alt", false);
        }
    };

    private static HashMap<String, String> CONVERT = new HashMap<String, String>() {
        {
            put("∅", "Escape");
            put("⏎", "Return");
            put("Ϡ", "Super_L");
            put(" ", "KP_Space");
        }
    };

    private static InetAddress getLocalHostLANAddress() throws UnknownHostException {
        try {
            InetAddress candidateAddress = null;
            // Iterate all NICs (network interface cards)...
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                // Iterate all IP addresses assigned to each card...
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {

                        if (inetAddr.isSiteLocalAddress()) {
                            // Found non-loopback site-local address. Return it immediately...
                            return inetAddr;
                        } else if (candidateAddress == null) {
                            // Found non-loopback address, but not necessarily site-local.
                            // Store it as a candidate to be returned if site-local address is not subsequently found...
                            candidateAddress = inetAddr;
                            // Note that we don't repeatedly assign non-loopback non-site-local addresses as candidates,
                            // only the first. For subsequent iterations, candidate will be non-null.
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                // We did not find a site-local address, but we found some other non-loopback address.
                // Server might have a non-site-local address assigned to its NIC (or it might be running
                // IPv6 which deprecates the "site-local" concept).
                // Return this non-loopback candidate address...
                return candidateAddress;
            }
            // At this point, we did not find a non-loopback address.
            // Fall back to returning whatever InetAddress.getLocalHost() returns...
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress;
        } catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }

    private void connect() {
        if (s != null) {
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        InetAddress ia = null;
        try {
            ia = getLocalHostLANAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Log.e("kekinzin", ia.getHostAddress());
        final byte[] byte_addr = ia.getAddress();
        for (int i = 1; i <= 254; i++) {
            byte_addr[3] = (byte) i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        InetAddress address = InetAddress.getByAddress(byte_addr);
                        if (address.isReachable(1000)) {
                            s = new Socket(address, 2717);
                            Log.e("kekinzin", address.getHostAddress());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
//            byte[] kek = new byte[3];
//            s.getInputStream().read(kek);
//            if(new String(kek).equals("kek")) {
//                Log.e("kekinzin", "!!!");
//                s.close();
//                s = new Socket("192.168.0.25", 2717);
//            }
    }

    @Override
    protected Void doInBackground(Object... os) {
        if (s == null) {
            connect();
        }

        if (s != null) {
            try {
                tcp(s, os);
            } catch (NullPointerException e) {
                connect();
            }
        }
        return null;
    }

    public void tcp(Socket s, Object... os) {
        try {
            if (os[0] instanceof Integer) {
                s.getOutputStream().write((int) os[0]);
            } else if (os[0] instanceof String) {
                String raw_in = (String) os[0];
                if (MODIFIERS.containsKey(raw_in)) {
                    MODIFIERS.put(raw_in, !MODIFIERS.get(raw_in));
                    return;
                }
                String treated = CONVERT.containsKey(raw_in) ? CONVERT.get(raw_in) : raw_in;
                if (MODIFIERS.containsKey(treated)) {
                    Log.e("kekaozao", treated);
                    MODIFIERS.put(treated, !MODIFIERS.get(treated));
                    return;
                }
                for (Map.Entry<String, Boolean> e : MODIFIERS.entrySet()) {
                    boolean activated = e.getValue();
                    if (activated) {
                        String mod = e.getKey();
                        treated = mod + "+" + treated;
                        MODIFIERS.put(mod, false);
                    }
                }
                if(!raw_in.contains("click") && !raw_in.startsWith("x:")) {
                    treated = "key " + treated;
                }
                if (treated.equals("key alt+tab")) {
                    treated = "down alt key Tab keyup alt";
                }
                s.getOutputStream().write(treated.getBytes());
            }
            Log.i("keyboard", os[0] + "");
            s.getOutputStream().write("\n".getBytes());
            s.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
            connect();
        }
    }
}
