/***************************************************************************
 *   Copyright 2006-2018 by Christian Ihle                                 *
 *   contact@kouchat.net                                                   *
 *                                                                         *
 *   This file is part of KouChat.                                         *
 *                                                                         *
 *   KouChat is free software; you can redistribute it and/or modify       *
 *   it under the terms of the GNU Lesser General Public License as        *
 *   published by the Free Software Foundation, either version 3 of        *
 *   the License, or (at your option) any later version.                   *
 *                                                                         *
 *   KouChat is distributed in the hope that it will be useful,            *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU      *
 *   Lesser General Public License for more details.                       *
 *                                                                         *
 *   You should have received a copy of the GNU Lesser General Public      *
 *   License along with KouChat.                                           *
 *   If not, see <http://www.gnu.org/licenses/>.                           *
 ***************************************************************************/

package org.dynamicsoft.vertochat.net;

import org.dynamicsoft.vertochat.Constants;
import org.dynamicsoft.vertochat.misc.ErrorHandler;
import org.dynamicsoft.vertochat.misc.User;
import org.dynamicsoft.vertochat.settings.Settings;
import org.dynamicsoft.vertochat.util.Tools;
import org.dynamicsoft.vertochat.util.Validate;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class can find information about the network interface
 * the operating system has chosen for multicast.
 *
 * @author Christian Ihle
 */
public class OperatingSystemNetworkInfo {

    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(OperatingSystemNetworkInfo.class.getName());

    /**
     * The message receiver.
     */
    private final MessageReceiver receiver;

    /**
     * The message sender.
     */
    private final MessageSender sender;

    /**
     * The application user.
     **/
    private final User me;

    /**
     * Default constructor.
     *
     * @param settings     The settings to use.
     * @param errorHandler The error handler to use.
     */
    public OperatingSystemNetworkInfo(final Settings settings, final ErrorHandler errorHandler) {
        Validate.notNull(settings, "Settings can not be null");
        Validate.notNull(errorHandler, "Error handler can not be null");

        receiver = new MessageReceiver(Constants.NETWORK_TEMP_IP, Constants.NETWORK_TEMP_PORT, errorHandler);
        sender = new MessageSender(Constants.NETWORK_TEMP_IP, Constants.NETWORK_TEMP_PORT, errorHandler);
        me = settings.getMe();
    }

    /**
     * Finds the network interface the operating system has chosen for
     * sending and receiving multicast messages.
     * <p>
     * <p>If no network interface has been found within 2 seconds,
     * then <code>null</code> is returned.
     *
     * @return The network interface, or <code>null</code>.
     */
    public NetworkInterface getOperatingSystemNetworkInterface() {
        LOG.fine("Trying to detect network interface used by operating system");

        final String message = createMessageToSend();
        final SimpleReceiverListener listener = new SimpleReceiverListener(message);
        connect(listener);
        sender.send(message);
        waitForMessage(listener);
        disconnect();
        final NetworkInterface networkInterface = findNetworkInterface(listener);

        LOG.fine("Detected network interface used by operating system: " + networkInterface);
        return networkInterface;
    }

    /**
     * Connects the sender and receiver to the network,
     * and registers the message listener.
     *
     * @param listener The message listener.
     */
    private void connect(final SimpleReceiverListener listener) {
        receiver.registerReceiverListener(listener);
        receiver.startReceiver(null);
        sender.startSender(null);
    }

    /**
     * Disconnects the sender and receiver from the network.
     */
    private void disconnect() {
        sender.stopSender();
        receiver.stopReceiver();
    }

    /**
     * Waits for up to 2 seconds for an ip address to be registered in the listener.
     *
     * @param listener The message listener.
     */
    private void waitForMessage(final SimpleReceiverListener listener) {
        for (int i = 0; i < 40; i++) {
            if (listener.getIpAddress() == null) {
                Tools.sleep(50);
            } else {
                break;
            }
        }
    }

    /**
     * Gets the ip address from the listener, and tries to convert
     * it into a network interface.
     * <p>
     * <p>Returns <code>null</code> if no ip address is found,
     * or the conversion fails.</p>
     *
     * @param listener The message listener.
     * @return The found network interface, or <code>null</code>.
     */
    @Nullable
    private NetworkInterface findNetworkInterface(final SimpleReceiverListener listener) {
        if (listener.getIpAddress() == null) {
            return null;
        }

        try {
            final InetAddress osAddress = InetAddress.getByName(listener.getIpAddress());
            return NetworkInterface.getByInetAddress(osAddress);
        } catch (final UnknownHostException e) {
            LOG.log(Level.SEVERE, e.toString(), e);
        } catch (final SocketException e) {
            LOG.log(Level.SEVERE, e.toString(), e);
        }

        return null;
    }

    /**
     * Returns a message with the text <code>getOperatingSystemNetworkInterface(user code)</code>,
     * where user code is taken from the application user.
     *
     * @return A message.
     */
    private String createMessageToSend() {
        return "getOperatingSystemNetworkInterface(" + me.getCode() + ")";
    }
}
