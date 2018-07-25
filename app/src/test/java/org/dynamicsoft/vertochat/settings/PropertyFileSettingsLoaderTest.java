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
import org.dynamicsoft.vertochat.junit.ExpectedException;
import org.dynamicsoft.vertochat.util.PropertyTools;
import org.dynamicsoft.vertochat.util.TestUtils;
import org.dynamicsoft.vertochat.util.Tools;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test of {@link PropertyFileSettingsLoader}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class PropertyFileSettingsLoaderTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private PropertyFileSettingsLoader loader;

    private Settings settings;
    private PropertyTools propertyTools;
    private Logger log;

    @Before
    public void setUp() {
        loader = new PropertyFileSettingsLoader();

        settings = new Settings();

        propertyTools = TestUtils.setFieldValueWithMock(loader, "propertyTools", PropertyTools.class);
        log = TestUtils.setFieldValueWithMock(loader, "LOG", Logger.class); // To avoid log output in tests
    }

    @Test
    public void loadSettingsShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        loader.loadSettings(null);
    }

    @Test
    public void loadSettingsShouldHandleMissingPropertiesAndKeepDefaultValues() throws IOException {
        when(propertyTools.loadProperties(anyString())).thenReturn(new Properties());

        loader.loadSettings(settings);

        SettingsTest.verifyDefaultValues(settings);
    }

    @Test
    public void loadSettingsShouldLoadAllSettingsFromProperties() throws IOException {
        final Properties properties = new Properties();

        properties.setProperty(PropertyFileSettings.NICK_NAME.getKey(), "Kenny");
        properties.setProperty(PropertyFileSettings.OWN_COLOR.getKey(), "-1234");
        properties.setProperty(PropertyFileSettings.SYS_COLOR.getKey(), "5678");
        properties.setProperty(PropertyFileSettings.SOUND.getKey(), "false");
        properties.setProperty(PropertyFileSettings.LOGGING.getKey(), "true");
        properties.setProperty(PropertyFileSettings.SMILEYS.getKey(), "false");
        properties.setProperty(PropertyFileSettings.BALLOONS.getKey(), "true");
        properties.setProperty(PropertyFileSettings.BROWSER.getKey(), "opera");
        properties.setProperty(PropertyFileSettings.LOOK_AND_FEEL.getKey(), "sega");
        properties.setProperty(PropertyFileSettings.NETWORK_INTERFACE.getKey(), "eth5");

        assertEquals(10, properties.size());

        when(propertyTools.loadProperties(anyString())).thenReturn(properties);

        loader.loadSettings(settings);

        Assert.assertEquals("Kenny", settings.getMe().getNick());

        assertEquals(-1234, settings.getOwnColor());
        assertEquals(5678, settings.getSysColor());

        assertFalse(settings.isSound());
        assertTrue(settings.isLogging());
        assertFalse(settings.isSmileys());
        assertTrue(settings.isBalloons());

        assertEquals("opera", settings.getBrowser());
        assertEquals("sega", settings.getLookAndFeel());
        assertEquals("eth5", settings.getNetworkInterface());
    }

    @Test
    public void loadSettingsShouldHandleBooleansWithStrangeValues() throws IOException {
        final Properties properties = new Properties();

        properties.setProperty(PropertyFileSettings.SOUND.getKey(), "yeah");
        properties.setProperty(PropertyFileSettings.LOGGING.getKey(), "nah");
        properties.setProperty(PropertyFileSettings.SMILEYS.getKey(), "nope");
        properties.setProperty(PropertyFileSettings.BALLOONS.getKey(), "yey");

        when(propertyTools.loadProperties(anyString())).thenReturn(properties);

        loader.loadSettings(settings);

        assertFalse(settings.isSound());
        assertFalse(settings.isLogging());
        assertFalse(settings.isSmileys());
        assertFalse(settings.isBalloons());
    }

    @Test
    public void loadSettingsShouldIgnoreMissingNickName() throws IOException {
        when(propertyTools.loadProperties(anyString())).thenReturn(new Properties());

        final String defaultNickName = settings.getMe().getNick();
        assertTrue(Tools.isValidNick(defaultNickName));

        loader.loadSettings(settings);

        Assert.assertEquals(defaultNickName, settings.getMe().getNick());
    }

    @Test
    public void loadSettingsShouldIgnoreInvalidNickName() throws IOException {
        final Properties properties = new Properties();
        properties.setProperty(PropertyFileSettings.NICK_NAME.getKey(), "@Boss");

        when(propertyTools.loadProperties(anyString())).thenReturn(properties);

        final String defaultNickName = settings.getMe().getNick();
        assertTrue(Tools.isValidNick(defaultNickName));

        loader.loadSettings(settings);

        Assert.assertEquals(defaultNickName, settings.getMe().getNick());
    }

    @Test
    public void loadSettingsShouldHandleFileNotFound() throws IOException {
        doThrow(new FileNotFoundException("No file")).when(propertyTools).loadProperties(anyString());

        loader.loadSettings(settings);

        verify(propertyTools).loadProperties(Constants.APP_FOLDER + "vertochat.ini");
        verify(log).log(Level.WARNING,
                "Could not find " + Constants.APP_FOLDER + "vertochat.ini, using default settings.");
        SettingsTest.verifyDefaultValues(settings);
    }

    @Test
    public void loadSettingsShouldHandleIOException() throws IOException {
        final IOException ioException = new IOException("Unknown error");
        doThrow(ioException).when(propertyTools).loadProperties(anyString());

        loader.loadSettings(settings);

        verify(propertyTools).loadProperties(Constants.APP_FOLDER + "vertochat.ini");
        verify(log).log(Level.SEVERE, "java.io.IOException: Unknown error", ioException);
        SettingsTest.verifyDefaultValues(settings);
    }
}
