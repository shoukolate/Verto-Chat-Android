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

import org.dynamicsoft.vertochat.ui.UserInterface;
import org.dynamicsoft.vertochat.util.TestUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.Timer;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Test of {@link DayTimer}.
 *
 * @author Christian Ihle
 */
public class DayTimerTest {

    private DayTimer dayTimer;

    private Timer timer;

    @Before
    public void setUp() {
        dayTimer = new DayTimer(mock(UserInterface.class));

        timer = mock(Timer.class);
        TestUtils.setFieldValue(dayTimer, "timer", timer);
    }

    @Test
    public void startTimerShouldScheduleAtFixedRate() {
        dayTimer.startTimer();

        verify(timer).scheduleAtFixedRate(eq(dayTimer), any(Date.class), eq(1000L * 60L * 60L));
    }

    @Test
    public void stopTimerShouldCancel() {
        dayTimer.stopTimer();

        verify(timer).cancel();
    }
}
