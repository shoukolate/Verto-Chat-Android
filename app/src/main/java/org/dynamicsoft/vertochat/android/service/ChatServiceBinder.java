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

package org.dynamicsoft.vertochat.android.service;

import android.os.Binder;

import org.dynamicsoft.vertochat.android.chatwindow.AndroidUserInterface;
import org.dynamicsoft.vertochat.util.Validate;

/**
 * Binder for accessing the {@link AndroidUserInterface}.
 *
 * @author Christian Ihle
 */
public class ChatServiceBinder extends Binder {

    private AndroidUserInterface androidUserInterface;

    public ChatServiceBinder(final AndroidUserInterface androidUserInterface) {
        Validate.notNull(androidUserInterface, "AndroidUserInterface can not be null");
        this.androidUserInterface = androidUserInterface;
    }

    public AndroidUserInterface getAndroidUserInterface() {
        return androidUserInterface;
    }

    /**
     * Cleanup to avoid memory leaks. Binders are not garbage collected easily, so it's important that
     * they don't reference any objects that must be garbage collected.
     */
    public void onDestroy() {
        androidUserInterface = null;
    }
}
