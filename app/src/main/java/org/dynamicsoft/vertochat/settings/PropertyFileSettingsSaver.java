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

import org.dynamicsoft.vertochat.Constants;
import org.dynamicsoft.vertochat.message.CoreMessages;
import org.dynamicsoft.vertochat.misc.ErrorHandler;
import org.dynamicsoft.vertochat.misc.User;
import org.dynamicsoft.vertochat.util.IOTools;
import org.dynamicsoft.vertochat.util.PropertyTools;
import org.dynamicsoft.vertochat.util.Tools;
import org.dynamicsoft.vertochat.util.Validate;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Saves settings to <code>~/.vertochat/vertochat.ini</code>.
 *
 * @author Christian Ihle
 */
public class PropertyFileSettingsSaver implements SettingsSaver {

    private static final Logger LOG = Logger.getLogger(PropertyFileSettingsSaver.class.getName());

    private final IOTools ioTools = new IOTools();
    private final PropertyTools propertyTools = new PropertyTools();

    private final Settings settings;
    private final CoreMessages coreMessages;
    private final ErrorHandler errorHandler;

    public PropertyFileSettingsSaver(final Settings settings, final CoreMessages coreMessages,
                                     final ErrorHandler errorHandler) {
        Validate.notNull(settings, "Settings can not be null");
        Validate.notNull(coreMessages, "Core messages can not be null");
        Validate.notNull(errorHandler, "Error handler can not be null");

        this.settings = settings;
        this.coreMessages = coreMessages;
        this.errorHandler = errorHandler;
    }

    /**
     * Saves the current settings to file. Creates any missing folders or files.
     */
    @Override
    public void saveSettings() {
        final Properties properties = new Properties();
        final User me = settings.getMe();

        properties.put(PropertyFileSettings.NICK_NAME.getKey(), Tools.emptyIfNull(me.getNick()));
        properties.put(PropertyFileSettings.OWN_COLOR.getKey(), String.valueOf(settings.getOwnColor()));
        properties.put(PropertyFileSettings.SYS_COLOR.getKey(), String.valueOf(settings.getSysColor()));
        properties.put(PropertyFileSettings.LOGGING.getKey(), String.valueOf(settings.isLogging()));
        properties.put(PropertyFileSettings.SOUND.getKey(), String.valueOf(settings.isSound()));
        properties.put(PropertyFileSettings.BROWSER.getKey(), Tools.emptyIfNull(settings.getBrowser()));
        properties.put(PropertyFileSettings.SMILEYS.getKey(), String.valueOf(settings.isSmileys()));
        properties.put(PropertyFileSettings.LOOK_AND_FEEL.getKey(), Tools.emptyIfNull(settings.getLookAndFeel()));
        properties.put(PropertyFileSettings.BALLOONS.getKey(), String.valueOf(settings.isBalloons()));
        properties.put(PropertyFileSettings.NETWORK_INTERFACE.getKey(), Tools.emptyIfNull(settings.getNetworkInterface()));

        try {
            ioTools.createFolder(Constants.APP_FOLDER);
            propertyTools.saveProperties(PropertyFileSettingsLoader.SETTINGS_FILE, properties,
                    coreMessages.getMessage("core.settings.file.comment", Constants.APP_NAME));
        } catch (final IOException e) {
            LOG.log(Level.SEVERE, "Failed to save settings", e);
            errorHandler.showError(coreMessages.getMessage("core.settings.errorPopup.saveFailed", e));
        }
    }
}
