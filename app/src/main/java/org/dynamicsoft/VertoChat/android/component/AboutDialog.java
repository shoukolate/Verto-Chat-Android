
/***************************************************************************
 *   Copyright 2006-2016 by Christian Ihle                                 *
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

package org.dynamicsoft.VertoChat.android.component;

import org.dynamicsoft.VertoChat.android.R;
import org.dynamicsoft.VertoChat.util.Validate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.widget.TextView;

/**
 * Creates an "About" dialog with copyright information and links.
 *
 * @author Christian Ihle
 */
public class AboutDialog {

    private static final int PADDING = 5;

    /**
     * Creates and shows the about dialog.
     *
     * @param context The activity to create this dialog from.
     */
    public AboutDialog(final Context context) {
        Validate.notNull(context, "Context can not be null");
        final Context wrappedContext = new ContextThemeWrapper(context, R.style.Theme_VertoChat_Dialog);

        final PackageInfo packageInfo = getPackageInfo(wrappedContext);

        final String appVersion = packageInfo.versionName;
        final String appName = wrappedContext.getString(R.string.app_name);

        final String aboutTitle = appName + " v" + appVersion;
        final String aboutText = wrappedContext.getString(R.string.about_text);
        final TextView messageView = createMessageView(wrappedContext, aboutText);

        buildDialog(wrappedContext, aboutTitle, messageView);
    }

    private void buildDialog(final Context context, final String aboutTitle, final TextView messageView) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(aboutTitle);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.ic_dialog);
        builder.setPositiveButton(context.getString(android.R.string.ok), null);
        builder.setView(messageView);
        builder.create();

        builder.show();
    }

    private TextView createMessageView(final Context context, final String aboutText) {
        final TextView messageView = new TextView(context);
        final SpannableString message = new SpannableString(aboutText);
        final Resources resources = context.getResources();

        messageView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size));
        messageView.setPadding(PADDING, PADDING, PADDING, PADDING);
        messageView.setText(message);

        Linkify.addLinks(messageView, Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES);

        return messageView;
    }

    private PackageInfo getPackageInfo(final Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        }

        catch (final PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
