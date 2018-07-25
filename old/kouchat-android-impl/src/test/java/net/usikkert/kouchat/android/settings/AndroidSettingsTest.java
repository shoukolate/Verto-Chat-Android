
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

package org.dynamicsoft.vertochat.android.settings;

import static org.junit.Assert.*;

import org.dynamicsoft.vertochat.event.SettingsListener;
import org.dynamicsoft.vertochat.settings.Setting;

import org.junit.Before;
import org.junit.Test;

/**
 * Test of {@link AndroidSettings}.
 *
 * @author Christian Ihle
 */
public class AndroidSettingsTest {

    private AndroidSettings settings;

    private Setting lastChangedSetting;

    @Before
    public void setUp() {
        settings = new AndroidSettings();

        settings.addSettingsListener(new SettingsListener() {
            public void settingChanged(final Setting setting) {
                lastChangedSetting = setting;
            }
        });
    }

    @Test
    public void setWakeLockEnabledShouldNotNotifyListenersIfSettingIsUnchanged() {
        assertFalse(settings.isWakeLockEnabled());

        settings.setWakeLockEnabled(false);

        assertFalse(settings.isWakeLockEnabled());
        assertNull(lastChangedSetting);
    }

    @Test
    public void setWakeLockEnabledShouldNotifyListenersIfSettingIsChanged() {
        assertFalse(settings.isWakeLockEnabled());

        settings.setWakeLockEnabled(true);

        assertTrue(settings.isWakeLockEnabled());
        assertEquals(AndroidSetting.WAKE_LOCK, lastChangedSetting);
    }
}
