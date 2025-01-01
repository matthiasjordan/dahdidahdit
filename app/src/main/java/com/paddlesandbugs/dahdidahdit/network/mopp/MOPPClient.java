/****************************************************************************
    Dahdidahdit - an Android Morse trainer
    Copyright (C) 2021-2025 Matthias Jordan <matthias@paddlesandbugs.com>

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

package com.paddlesandbugs.dahdidahdit.network.mopp;


import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.paddlesandbugs.dahdidahdit.MorseCode;
import com.paddlesandbugs.dahdidahdit.network.Address;
import com.paddlesandbugs.dahdidahdit.sound.MorseTiming;

public class MOPPClient {


    private static final String LOG_TAG = "MOPPClient";

    private static final int DEFAULT_PORT = 7373;

    private final DatagramSocket datagramSocket;

    private final ExecutorService receiveExec = Executors.newSingleThreadExecutor();

    private final ExecutorService sendExec = Executors.newSingleThreadExecutor();

    private final UDPListener udpListener;

    private final Address address;

    private final Consumer<Exception> errorListener;

    private final WifiManager.MulticastLock lock;

    private volatile InetAddress inetAddress;


    public MOPPClient(Context context, Address address, Consumer<Packet> replyListener, Consumer<Exception> errorListener) throws SocketException, UnknownHostException {
        this.address = address;

        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifi != null) {
            lock = wifi.createMulticastLock("dahdidahdit");
            lock.acquire();
            Log.d(LOG_TAG, "Multicast lock acquired");
        } else {
            lock = null;
        }

        if (address.isBroadcast()) {
            this.datagramSocket = new DatagramSocket(address.getPort(DEFAULT_PORT));
        } else {
            this.datagramSocket = new DatagramSocket();
        }
        this.udpListener = new UDPListener(datagramSocket, replyListener, errorListener);
        this.receiveExec.execute(udpListener);
        this.errorListener = errorListener;
    }


    private static void simulateSigningMorseCode(Packet packet) throws InterruptedException {
        int wpm = packet.getWpm();
        MorseCode.CharacterList text = packet.getCharacters();
        int durationMs = MorseTiming.get(wpm, wpm).calcMs(text);
        Thread.sleep(durationMs); // Simulate op writing Morse code
    }


    public void close() {
        sendExec.shutdown();
        udpListener.stop();
        receiveExec.shutdown();
        datagramSocket.close();

        if ((lock != null) && lock.isHeld()) {
            lock.release();
            Log.d(LOG_TAG, "Multicast lock released");
        }
        Log.i(LOG_TAG, "Closed MOPPClient");
    }


    public void send(Packet packet, boolean simulateSigning) throws IOException {
        final int port = address.getPort(DEFAULT_PORT);

        sendExec.execute(() -> {

            if (inetAddress == null) {
                try {
                    inetAddress = InetAddress.getByName(address.getHostname());
                } catch (UnknownHostException e) {
                    errorListener.accept(e);
                    return;
                }
            }

            byte[] sdata = MOPPParser.toMOPP(packet);

            DatagramPacket pkt = new DatagramPacket(sdata, sdata.length, inetAddress, port);
            try {
                if (simulateSigning) {
                    simulateSigningMorseCode(packet);
                }
                datagramSocket.send(pkt);
                Log.i(LOG_TAG, "sent");
            } catch (IOException e) {
                errorListener.accept(e);
                Log.e(LOG_TAG, e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }


    private static class UDPListener implements Runnable {

        private final Consumer<Exception> errorListener;

        private final DatagramSocket datagramSocket;

        private final Consumer<Packet> listener;

        private final Set<String> localAdrs;

        private volatile boolean stop;


        public UDPListener(DatagramSocket datagramSocket, Consumer<Packet> replyListener, Consumer<Exception> errorListener) {
            this.datagramSocket = datagramSocket;
            this.listener = replyListener;
            this.errorListener = errorListener;
            this.localAdrs = Address.getLocalIpAddress();
        }


        public void stop() {
            stop = true;
        }


        @Override
        public void run() {
            while (!stop) {

                byte[] recieve_data = new byte[64];
                DatagramPacket rpkt = new DatagramPacket(recieve_data, recieve_data.length);
                try {
                    if (datagramSocket == null) {
                        continue;
                    }

                    datagramSocket.receive(rpkt);

                    String sourceHost = rpkt.getAddress().getHostAddress();

                    if (!localAdrs.contains(sourceHost)) {
                        // This packet ain't ours.
                        byte[] data = rpkt.getData();
                        final byte[] bytes = Arrays.copyOf(data, rpkt.getLength());

                        Packet packet = MOPPParser.fromMOPP(bytes);
                        MorseCode.CharacterList text = packet.getCharacters();
                        if ((text != null) && (text.size() != 0)) {
                            listener.accept(packet);
                            Log.i(LOG_TAG, "Parsed: " + text.asString());
                        }
                    }
                } catch (Exception e) {
                    if (!(e instanceof SocketException) && !(e instanceof SocketTimeoutException)) {
                        errorListener.accept(e);
                        Log.w(LOG_TAG, "Error while listening for an UDP Packet: " + e.getMessage());
                    }
                }
            }
            Log.i(LOG_TAG, "Left receive loop");
        }
    }

}
