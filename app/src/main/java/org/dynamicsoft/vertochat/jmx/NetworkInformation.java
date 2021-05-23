/***************************************************************************
 *   Copyright 2006-2019 by Christian Ihle                                 *
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

package org.dynamicsoft.vertochat.jmx;

import org.dynamicsoft.vertochat.misc.ErrorHandler;
import org.dynamicsoft.vertochat.net.ConnectionWorker;
import org.dynamicsoft.vertochat.net.NetworkUtils;
import org.dynamicsoft.vertochat.net.OperatingSystemNetworkInfo;
import org.dynamicsoft.vertochat.settings.Settings;
import org.dynamicsoft.vertochat.util.Validate;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * This is a JMX MBean for the network service.
 *
 * @author Christian Ihle
 */
public class NetworkInformation implements NetworkInformationMBean {

    private final NetworkUtils networkUtils = new NetworkUtils();

    /**
     * Information and control of the network.
     */
    private final ConnectionWorker connectionWorker;

    private final Settings settings;
    private final ErrorHandler errorHandler;

    /**
     * Constructor.
     *
     * @param connectionWorker To get information about the network, and control the network.
     * @param settings         The settings to use.
     * @param errorHandler     The error handler to use.
     */
    public NetworkInformation(final ConnectionWorker connectionWorker, final Settings settings,
                              final ErrorHandler errorHandler) {
        Validate.notNull(connectionWorker, "Connection worker can not be null");
        Validate.notNull(settings, "Settings can not be null");
        Validate.notNull(errorHandler, "Error handler can not be null");

        this.connectionWorker = connectionWorker;
        this.settings = settings;
        this.errorHandler = errorHandler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String showCurrentNetwork() {
        final NetworkInterface networkInterface = connectionWorker.getCurrentNetworkInterface();

        if (networkInterface == null) {
            return "No current network interface.";
        } else {
            return networkUtils.getNetworkInterfaceInfo(networkInterface);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String showOperatingSystemNetwork() {
        final OperatingSystemNetworkInfo osNicInfo = new OperatingSystemNetworkInfo(settings, errorHandler);
        final NetworkInterface osInterface = osNicInfo.getOperatingSystemNetworkInterface();

        if (osInterface == null) {
            return "No network interface detected.";
        } else {
            return networkUtils.getNetworkInterfaceInfo(osInterface);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] showUsableNetworks() {
        final List<String> list = new ArrayList<>();

        final Enumeration<NetworkInterface> networkInterfaces = networkUtils.getNetworkInterfaces();

        if (networkInterfaces == null) {
            return new String[]{"No network interfaces detected."};
        }

        while (networkInterfaces.hasMoreElements()) {
            final NetworkInterface netif = networkInterfaces.nextElement();

            if (networkUtils.isUsable(netif)) {
                list.add(networkUtils.getNetworkInterfaceInfo(netif));
            }
        }

        if (list.size() == 0) {
            return new String[]{"No usable network interfaces detected."};
        }

        return list.toArray(new String[list.size()]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] showAllNetworks() {
        final List<String> list = new ArrayList<>();

        final Enumeration<NetworkInterface> networkInterfaces = networkUtils.getNetworkInterfaces();

        if (networkInterfaces == null) {
            return new String[]{"No network interfaces detected."};
        }

        while (networkInterfaces.hasMoreElements()) {
            final NetworkInterface netif = networkInterfaces.nextElement();
            list.add(networkUtils.getNetworkInterfaceInfo(netif));
        }

        return list.toArray(new String[list.size()]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() {
        connectionWorker.stop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect() {
        connectionWorker.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBeanName() {
        return "Network";
    }
}
