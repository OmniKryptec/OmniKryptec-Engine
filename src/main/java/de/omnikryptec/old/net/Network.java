/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.old.net;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import de.omnikryptec.old.util.logger.Logger;

/**
 * Some Network Constants and methods
 *
 * @author Panzer1119
 */
public class Network {

    /**
     * Lowest possible Port number
     */
    public static final int PORT_MIN = 0x400;
    /**
     * Standard Port number
     */
    public static final int PORT_STANDARD = 1234;
    /**
     * Highest possible Port number
     */
    public static final int PORT_MAX = 0xFFFF;
    /**
     * Standard UDP Port for a local network
     */
    public static final int PORT_LOCAL_NETWORK_STANDARD = 8888;

    /**
     * All TCP Ports that are currently in use
     */
    private static final HashMap<Integer, AdvancedServerSocket> registeredTCPPorts = new HashMap<>();
    /**
     * All UDP Ports that are currently in use
     */
    private static final HashMap<Integer, Object> registeredUDPPorts = new HashMap<>();

    /**
     * Maximum number of Threads in a Client ThreadPool
     */
    public static final int THREADPOOL_SIZE_CLIENT_MAX = 15;
    /**
     * Standard number of Threads in a Client ThreadPool
     */
    public static final int THREADPOOL_SIZE_CLIENT_STANDARD = 7;
    /**
     * Maximum number of Threads in a Server
     */
    public static final int THREADPOOL_SIZE_SERVER_MAX = 15;
    /**
     * Standard number of Threads in a Server
     */
    public static final int THREADPOOL_SIZE_SERVER_STANDARD = 7;

    /**
     * Milliseconds to wait between each time a Stream is null
     */
    public static final int STREAM_DELAY_TIME = 10;
    /**
     * Maximum time to wait for a Stream to get not null in milliseconds
     */
    public static final int STREAM_TIME_MAX = 5000;

    /**
     * Standard maximum number of (re)connections
     */
    public static final int CONNECTION_TRIES_MAX_STANDARD = 3;
    /**
     * Standard delay time between each (re)connection try in milliseconds
     */
    public static final int CONNECTION_DELAY_TIME_STANDARD = 1000;
    /**
     * Standard maximum number of tries per connection checks
     */
    public static final int CONNECTION_CHECK_TRIES_MAX_STANDARD = 3;
    /**
     * Standard maximum time between sending the Ping and receiving the Pong in
     * milliseconds
     */
    public static final int CONNECTION_ANSWER_TIME_STANDARD = 1000;
    /**
     * Standard delay time between each answer check in milliseconds
     */
    public static final int CONNECTION_ANSWER_DELAY_TIME_STANDARD = 100;
    /**
     * Standard delay time between each connection check in milliseconds
     */
    public static final int CONNECTION_CHECK_DELAY_TIME_STANDARD = 500;
    /**
     * Standard delay time between each new connection check in milliseconds
     */
    public static final int CONNECTION_CHECK_TIMER_DELAY_STANDARD = 5000;

    /**
     * Standard delay time between each try to kill the Thread
     */
    public static final int THREAD_KILL_DELAY_TIME_STANDARD = 10;
    /**
     * Standard maximum time to wait for a Thread to die
     */
    public static final int THREAD_KILL_MAX_TIME_STANDARD = 100;

    /**
     * Generates a (hopefully) unique ID for Messages
     *
     * @return New ID
     */
    public static final long generateID() {
        return System.nanoTime();
    }

    /**
     * Checks if the given TCP Port is already in use
     *
     * @param port TCP Port to get checked
     * @return <tt>true</tt> if the TCP Port is already in use
     */
    public static final boolean existsTCPPort(int port) {
        return registeredTCPPorts.containsKey(port);
    }

    /**
     * Checks if the given TCP Port is not in use and between the lowest and highest
     * possible Port number
     *
     * @param port TCP Port to get checked
     * @return <tt>true</tt> if the TCP Port is not in use and is a possible Port
     * number
     */
    public static final boolean checkTCPPort(int port) {
        if (existsTCPPort(port)) {
            return false;
        }
        return ((port >= PORT_MIN) && (port <= PORT_MAX));
    }

    /**
     * Registers a TCP Port to be used from now
     *
     * @param port TCP Port to get registered
     * @return <tt>true</tt> if the TCP Port was successfully registered
     */
    public static final boolean registerTCPPort(int port) {
        return registerTCPPort(port, null);
    }

    /**
     * Registers a TCP Port to be used from now
     *
     * @param port TCP Port to get registered
     * @param serverSocket AdvancedServerSocket
     * @return <tt>true</tt> if the TCP Port was successfully registered
     */
    public static final boolean registerTCPPort(int port, AdvancedServerSocket serverSocket) {
        if (!checkTCPPort(port)) {
            return false;
        }
        registeredTCPPorts.put(port, serverSocket);
        return true;
    }

    /**
     * Unregisters a TCP Port to no longer be used
     *
     * @param port TCP Port to get unregistered
     * @return <tt>true</tt> if the TCP Port was successfully unregistered
     */
    public static final boolean unregisterTCPPort(int port) {
        if (!existsTCPPort(port)) {
            return false;
        }
        registeredTCPPorts.remove(port);
        return true;
    }

    /**
     * Returns the AdvancedServerSocket associated with the given TCP Port
     *
     * @param port TCP Port
     * @return AdvancedServerSocket
     */
    public static final AdvancedServerSocket getObjectFromTCPPort(int port) {
        return registeredTCPPorts.get(port);
    }
    
    /**
     * Checks if the given UDP Port is already in use
     *
     * @param port UDP Port to get checked
     * @return <tt>true</tt> if the UDP Port is already in use
     */
    public static final boolean existsUDPPort(int port) {
        return registeredUDPPorts.containsKey(port);
    }
    
    /**
     * Checks if the given UDP Port is not in use and between the lowest and highest
     * possible Port number
     *
     * @param port UDP Port to get checked
     * @return <tt>true</tt> if the UDP Port is not in use and is a possible Port
     * number
     */
    public static final boolean checkUDPPort(int port) {
        if (existsUDPPort(port)) {
            return false;
        }
        return ((port >= PORT_MIN) && (port <= PORT_MAX));
    }

    /**
     * Registers a UDP Port to be used from now
     *
     * @param port UDP Port to get registered
     * @return <tt>true</tt> if the UDP Port was successfully registered
     */
    public static final boolean registerUDPPort(int port) {
        return registerUDPPort(port, null);
    }

    /**
     * Registers a UDP Port to be used from now
     *
     * @param port UDP Port to get registered
     * @param serverSocket AdvancedServerSocket
     * @return <tt>true</tt> if the UDP Port was successfully registered
     */
    public static final boolean registerUDPPort(int port, AdvancedServerSocket serverSocket) {
        if (!checkUDPPort(port)) {
            return false;
        }
        registeredUDPPorts.put(port, serverSocket);
        return true;
    }

    /**
     * Unregisters a UDP Port to no longer be used
     *
     * @param port UDP Port to get unregistered
     * @return <tt>true</tt> if the UDP Port was successfully unregistered
     */
    public static final boolean unregisterUDPPort(int port) {
        if (!existsUDPPort(port)) {
            return false;
        }
        registeredUDPPorts.remove(port);
        return true;
    }

    /**
     * Returns the Object associated with the given UDP Port
     *
     * @param port UDP Port
     * @return Object
     */
    public static final Object getObjectFromUDPPort(int port) {
        return registeredUDPPorts.get(port);
    }
    
    /**
     * Formats an InetAddress
     *
     * @param inetAddress InetAddress to get formatted
     * @return Formatted InetAddress
     */
    public final static String formatInetAddress(InetAddress inetAddress) {
        return String.format("\"%s\"", ((inetAddress != null) ? inetAddress.getHostAddress() : ""));
    }

    /**
     * Formats an InetAddress and a Port
     *
     * @param inetAddress InetAddress to get formatted
     * @param port Port to get formatted
     * @return Formatted InetAddress and Port
     */
    public final static String formatInetAddressAndPort(InetAddress inetAddress, int port) {
        return String.format("\"%s:%d\"", ((inetAddress != null) ? inetAddress.getHostAddress() : ""), port);
    }

    /**
     * Closes a Socket until its really closed
     * @param socket Socket to close
     * @return <tt>true</tt> if the Socket was successfully closed
     */
    public static final boolean closeSocket(Socket socket) {
        if (socket == null) {
            return true;
        }
        try {
            socket.close();
            while (!socket.isClosed()) {
                socket.close();
            }
            return true;
        } catch (Exception ex) {
            if (Logger.isDebugMode()) {
                Logger.logErr("Error while closing Socket: " + ex, ex);
            }
            return false;
        }
    }

    /**
     * Closes a ServerSocket until its really closed
     * @param serverSocket ServerSocket to close
     * @return <tt>true</tt> if the ServerSocket was successfully closed
     */
    public static final boolean closeServerSocket(ServerSocket serverSocket) {
        if (serverSocket == null) {
            return true;
        }
        try {
            serverSocket.close();
            while (!serverSocket.isClosed()) {
                serverSocket.close();
            }
            return true;
        } catch (Exception ex) {
            if (Logger.isDebugMode()) {
                Logger.logErr("Error while closing ServerSocket: " + ex, ex);
            }
            return false;
        }
    }

}
