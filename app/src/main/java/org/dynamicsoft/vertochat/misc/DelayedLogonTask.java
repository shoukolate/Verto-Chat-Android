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

package org.dynamicsoft.vertochat.misc;

import org.dynamicsoft.vertochat.net.NetworkService;
import org.dynamicsoft.vertochat.util.Validate;

import java.util.TimerTask;

/**
 * This timer task updates the  {@link ChatState} to set the status to logged on if the
 * client was successful in connecting to the network.
 * <p>
 * <p>This must be delayed, as the initial chat state (like current users and topic) should have time to
 * be initialized before logon is set as completed. After logon is set as completed then all changes
 * to users are treated as regular events, like topic changed, user logged on, and more.</p>
 *
 * @author Christian Ihle
 */
public class DelayedLogonTask extends TimerTask {

    private final NetworkService networkService;
    private final ChatState chatState;

    public DelayedLogonTask(final NetworkService networkService, final ChatState chatState) {
        Validate.notNull(networkService, "NetworkService can not be null");
        Validate.notNull(chatState, "ChatState can not be null");

        this.networkService = networkService;
        this.chatState = chatState;
    }

    @Override
    public void run() {
        if (networkService.isNetworkUp()) {
            chatState.setLogonCompleted(true);
        }
    }
}
