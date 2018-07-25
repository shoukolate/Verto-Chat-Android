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

package org.dynamicsoft.vertochat.android;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import org.dynamicsoft.vertochat.Constants;
import org.dynamicsoft.vertochat.android.controller.MainChatController;
import org.dynamicsoft.vertochat.android.util.RobotiumTestUtils;

/**
 * Test of the about dialog.
 *
 * @author Christian Ihle
 */
public class AboutTest extends ActivityInstrumentationTestCase2<MainChatController> {

    private Solo solo;

    public AboutTest() {
        super(MainChatController.class);
    }

    public void setUp() {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void test01AboutInMenuShouldOpenDialog() {
        RobotiumTestUtils.openMenu(solo);
        solo.clickOnText("About");

        assertTrue(solo.searchText("VertoChat v" + Constants.APP_VERSION));
        assertTrue(solo.searchText("Copyright"));
        assertTrue(solo.searchText("LGPLv3"));

        solo.clickOnButton("OK");
        assertTrue(solo.searchText("Welcome to VertoChat"));
    }

    public void test99Quit() {
        RobotiumTestUtils.quit(solo);
    }

    public void tearDown() {
        solo.finishOpenedActivities();

        solo = null;
        setActivity(null);

        System.gc();
    }
}
