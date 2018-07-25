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

import org.dynamicsoft.vertochat.misc.User;
import org.dynamicsoft.vertochat.settings.Settings;
import org.dynamicsoft.vertochat.util.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test of {@link MessageParser}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class MessageParserTest {

    private MessageParser messageParser;

    private Logger log;

    @Before
    public void setUp() {
        final Settings settings = mock(Settings.class);
        when(settings.getMe()).thenReturn(new User("Test", 1234));

        messageParser = new MessageParser(mock(MessageResponder.class), settings);

        TestUtils.setFieldValue(messageParser, "loggedOn", true);
        log = TestUtils.setFieldValueWithMock(messageParser, "LOG", Logger.class);
    }

    @Test
    public void messageArrivedShouldLogIfUnableToFindNecessaryDetailsInMessage() {
        messageParser.messageArrived("Error", "192.168.1.1");

        final ArgumentCaptor<Exception> exceptionCaptor = ArgumentCaptor.forClass(Exception.class);

        verify(log).log(eq(Level.SEVERE),
                eq("Failed to parse message. message=Error, ipAddress=192.168.1.1"),
                exceptionCaptor.capture());

        checkException(exceptionCaptor, StringIndexOutOfBoundsException.class, "String index out of range: -1");
    }

    @Test
    public void messageArrivedShouldLogIfUnableToParseUserCodeInMessage() {
        messageParser.messageArrived("a16320462!LOGON#Christian:", "192.168.1.1");

        final ArgumentCaptor<Exception> exceptionCaptor = ArgumentCaptor.forClass(Exception.class);

        verify(log).log(eq(Level.SEVERE),
                eq("Failed to parse message. message=a16320462!LOGON#Christian:, ipAddress=192.168.1.1"),
                exceptionCaptor.capture());

        checkException(exceptionCaptor, NumberFormatException.class, "For input string: \"a16320462\"");
    }

    @Test
    public void messageArrivedShouldLogIfPrivateChatPortCouldNotBeParsed() {
        messageParser.messageArrived("16320462!CLIENT#Christian:(VertoChat v1.3.0 Swing)[2688]{Linux}<a40657>",
                "192.168.1.1");

        final ArgumentCaptor<Exception> exceptionCaptor = ArgumentCaptor.forClass(Exception.class);

        verify(log).log(eq(Level.WARNING),
                eq("Failed to parse private chat port. " +
                        "message=16320462!CLIENT#Christian:(VertoChat v1.3.0 Swing)[2688]{Linux}<a40657>, " +
                        "ipAddress=192.168.1.1"),
                exceptionCaptor.capture());

        checkException(exceptionCaptor, NumberFormatException.class, "For input string: \"a40657\"");
    }

    @Test
    public void messageArrivedShouldLogIfTimeSinceLogonCouldNotBeParsed() {
        messageParser.messageArrived("16320462!CLIENT#Christian:(VertoChat v1.3.0 Swing)[a2688]{Linux}<40657>",
                "192.168.1.1");

        final ArgumentCaptor<Exception> exceptionCaptor = ArgumentCaptor.forClass(Exception.class);

        verify(log).log(eq(Level.SEVERE),
                eq("Failed to parse message. " +
                        "message=16320462!CLIENT#Christian:(VertoChat v1.3.0 Swing)[a2688]{Linux}<40657>, " +
                        "ipAddress=192.168.1.1"),
                exceptionCaptor.capture());

        checkException(exceptionCaptor, NumberFormatException.class, "For input string: \"a2688\"");
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void checkException(final ArgumentCaptor<Exception> exceptionCaptor,
                                final Class<? extends Exception> expectedException,
                                final String expectedMessage) {
        final Exception exception = exceptionCaptor.getValue();

        assertEquals(expectedException, exception.getClass());
        assertEquals(expectedMessage, exception.getMessage());
    }
}
