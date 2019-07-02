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

package org.dynamicsoft.vertochat.settings;

import org.jetbrains.annotations.NonNls;

/**
 * Enum with keys for the different settings in <code>vertochat.ini</code>.
 *
 * @author Christian Ihle
 */
public enum PropertyFileSettings {

    NICK_NAME("nick"),
    OWN_COLOR("owncolor"),
    SYS_COLOR("syscolor"),
    LOGGING("logging"),
    BALLOONS("balloons"),
    SYSTEM_TRAY("systemTray"),
    BROWSER("browser"),
    LOOK_AND_FEEL("lookAndFeel"),
    NETWORK_INTERFACE("networkInterface"),
    SOUND("sound"),
    SMILEYS("smileys");

    private final String key;

    PropertyFileSettings(@NonNls final String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
