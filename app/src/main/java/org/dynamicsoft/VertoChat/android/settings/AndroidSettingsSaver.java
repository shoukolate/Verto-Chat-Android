
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

package org.dynamicsoft.VertoChat.android.settings;

import org.dynamicsoft.VertoChat.settings.SettingsSaver;

import android.util.Log;

/**
 * A {@link SettingsSaver} for Android that does nothing.
 *
 * <p>Saving settings is only done in the settings screen.</p>
 *
 * @author Christian Ihle
 */
public class AndroidSettingsSaver implements SettingsSaver {

    private static final String TAG = "AndroidSettingsSaver";

    /**
     * Not implemented.
     */
    @Override
    public void saveSettings() {
        Log.i(TAG, "Ignoring call to saveSettings()");
    }
}
