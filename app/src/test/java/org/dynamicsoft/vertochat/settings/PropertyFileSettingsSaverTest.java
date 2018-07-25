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
import org.dynamicsoft.vertochat.message.CoreMessages;
import org.dynamicsoft.vertochat.misc.ErrorHandler;
import org.dynamicsoft.vertochat.util.IOTools;
import org.dynamicsoft.vertochat.util.PropertyTools;
import org.dynamicsoft.vertochat.util.TestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Test of {@link PropertyFileSettingsSaver}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class PropertyFileSettingsSaverTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private PropertyFileSettingsSaver settingsSaver;

    private Settings settings;

    private IOTools ioTools;
    private PropertyTools propertyTools;
    private CoreMessages coreMessages;
    private ErrorHandler errorHandler;

    @Before
    public void setUp() {
        settings = new Settings();
        coreMessages = new CoreMessages();
        errorHandler = mock(ErrorHandler.class);

        settingsSaver = new PropertyFileSettingsSaver(settings, coreMessages, errorHandler);

        ioTools = TestUtils.setFieldValueWithMock(settingsSaver, "ioTools", IOTools.class);
        propertyTools = TestUtils.setFieldValueWithMock(settingsSaver, "propertyTools", PropertyTools.class);
        TestUtils.setFieldValueWithMock(settingsSaver, "LOG", Logger.class); // To avoid log output in tests
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new PropertyFileSettingsSaver(null, coreMessages, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfCoreMessagesIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Core messages can not be null");

        new PropertyFileSettingsSaver(settings, null, errorHandler);
    }

    @Test
    public void constructorShouldThrowExceptionIfErrorHandlerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Error handler can not be null");

        new PropertyFileSettingsSaver(settings, coreMessages, null);
    }

    @Test
    public void saveSettingsShouldCreateVertoChatFolderBeforeSaving() throws IOException {
        settingsSaver.saveSettings();

        final InOrder inOrder = inOrder(ioTools, propertyTools);

        inOrder.verify(ioTools).createFolder(Constants.APP_FOLDER);
        inOrder.verify(propertyTools).saveProperties(eq(Constants.APP_FOLDER + "vertochat.ini"),
                any(Properties.class),
                eq("VertoChat Settings"));
    }

    @Test
    public void saveSettingsShouldShowErrorOnException() throws IOException {
        doThrow(new IOException("Don't save")).when(propertyTools).saveProperties(
                anyString(), any(Properties.class), anyString());

        settingsSaver.saveSettings();

        verify(errorHandler).showError("Settings could not be saved:\njava.io.IOException: Don't save");
    }

    @Test
    public void saveSettingsShouldNotShowErrorWhenOK() throws IOException {
        settingsSaver.saveSettings();

        verify(errorHandler, never()).showError(anyString());
    }

    @Test
    public void saveSettingsShouldConvertAllValuesToStringsToAvoidClassCastException() throws IOException {
        settings.getMe().setNick("Linda");
        settings.setOwnColor(100);
        settings.setSysColor(-200);
        settings.setSound(false);
        settings.setLogging(true);
        settings.setSmileys(false);
        settings.setBalloons(true);
        settings.setBrowser("firefox");
        settings.setLookAndFeel("starwars");
        settings.setNetworkInterface("wlan2");

        settingsSaver.saveSettings();

        final ArgumentCaptor<Properties> propertiesCaptor = ArgumentCaptor.forClass(Properties.class);

        verify(propertyTools).saveProperties(anyString(), propertiesCaptor.capture(), anyString());

        final Properties properties = propertiesCaptor.getValue();

        assertEquals(10, properties.size());

        assertEquals("Linda", properties.get(PropertyFileSettings.NICK_NAME.getKey()));
        assertEquals("100", properties.get(PropertyFileSettings.OWN_COLOR.getKey()));
        assertEquals("-200", properties.get(PropertyFileSettings.SYS_COLOR.getKey()));
        assertEquals("false", properties.get(PropertyFileSettings.SOUND.getKey()));
        assertEquals("true", properties.get(PropertyFileSettings.LOGGING.getKey()));
        assertEquals("false", properties.get(PropertyFileSettings.SMILEYS.getKey()));
        assertEquals("true", properties.get(PropertyFileSettings.BALLOONS.getKey()));
        assertEquals("firefox", properties.get(PropertyFileSettings.BROWSER.getKey()));
        assertEquals("starwars", properties.get(PropertyFileSettings.LOOK_AND_FEEL.getKey()));
        assertEquals("wlan2", properties.get(PropertyFileSettings.NETWORK_INTERFACE.getKey()));
    }

    @Test
    public void saveSettingsShouldHandleNullStringsToAvoidNullPointerException() throws IOException {
        settings.getMe().setNick(null);
        settings.setBrowser(null);
        settings.setLookAndFeel(null);
        settings.setNetworkInterface(null);

        settingsSaver.saveSettings();

        final ArgumentCaptor<Properties> propertiesCaptor = ArgumentCaptor.forClass(Properties.class);

        verify(propertyTools).saveProperties(anyString(), propertiesCaptor.capture(), anyString());

        final Properties properties = propertiesCaptor.getValue();

        assertEquals(10, properties.size());

        assertEquals("", properties.get(PropertyFileSettings.NICK_NAME.getKey()));
        assertEquals("", properties.get(PropertyFileSettings.BROWSER.getKey()));
        assertEquals("", properties.get(PropertyFileSettings.LOOK_AND_FEEL.getKey()));
        assertEquals("", properties.get(PropertyFileSettings.NETWORK_INTERFACE.getKey()));
    }
}
