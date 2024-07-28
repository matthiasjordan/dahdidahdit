/****************************************************************************
    Dahdidahdit - an Android Morse trainer
    Copyright (C) 2021-2024 Matthias Jordan <matthias@paddlesandbugs.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
****************************************************************************/

package com.paddlesandbugs.dahdidahdit.network;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;

import com.paddlesandbugs.dahdidahdit.Utils;

public class Address {

    private final String hostname;

    private final Integer port;


    public Address(String hostname, Integer port) throws ParseException {
        if (Utils.isEmpty(hostname)) {
            throw new ParseException("hostname must not be empty", 0);
        }
        this.hostname = hostname;
        this.port = port;
    }


    public Address(String hostname) throws ParseException {
        this(hostname, null);
    }


    public String getHostname() {
        return hostname;
    }


    public boolean isBroadcast() {
        return hostname.endsWith(".255");
    }


    public int getPort(int defaultPort) {
        if (port == null) {
            return defaultPort;
        } else {
            return port;
        }
    }


    public static Address parse(String input) throws ParseException {
        if (input == null) {
            return null;
        }
        String[] parts = input.split(":");
        if (parts.length == 1) {
            return new Address(parts[0]);
        } else if (parts.length == 2) {
            try {
                int port = Integer.parseInt(parts[1]);
                return new Address(parts[0], port);
            } catch (NumberFormatException e) {
                throw new ParseException("port must be an integer", 0);
            }
        }

        throw new ParseException("Could not parse address", 0);
    }


    public static Set<String> getLocalIpAddress() {
        TreeSet<String> adrs = new TreeSet<>();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        adrs.add(inetAddress.getHostAddress().toString());
                    }
                }
            }
        } catch (Exception ex) {
            // Things happen
        } return adrs;
    }
}
