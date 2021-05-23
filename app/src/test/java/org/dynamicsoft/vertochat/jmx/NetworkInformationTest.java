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

package org.dynamicsoft.vertochat.jmx;

import org.dynamicsoft.vertochat.junit.ExpectedException;
import org.dynamicsoft.vertochat.misc.ErrorHandler;
import org.dynamicsoft.vertochat.net.ConnectionWorker;
import org.dynamicsoft.vertochat.settings.Settings;
import org.junit.Rule;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Test of {@link NetworkInformation}.
 *
 * @author Christian Ihle
 */
public class NetworkInformationTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void constructorShouldThrowExceptionIfConnectionWorkerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Connection worker can not be null");

        new NetworkInformation(null, mock(Settings.class), mock(ErrorHandler.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfSettingsIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Settings can not be null");

        new NetworkInformation(mock(ConnectionWorker.class), null, mock(ErrorHandler.class));
    }

    @Test
    public void constructorShouldThrowExceptionIfErrorHandlerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Error handler can not be null");

        new NetworkInformation(mock(ConnectionWorker.class), mock(Settings.class), null);
    }
}
