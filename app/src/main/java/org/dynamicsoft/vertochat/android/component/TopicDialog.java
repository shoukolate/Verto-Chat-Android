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

package org.dynamicsoft.vertochat.android.component;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import org.dynamicsoft.vertochat.android.R;
import org.dynamicsoft.vertochat.android.chatwindow.AndroidUserInterface;
import org.dynamicsoft.vertochat.util.Validate;

/**
 * Creates a dialog for showing and setting the topic.
 *
 * @author Christian Ihle
 */
public class TopicDialog {

    public TopicDialog(final Context context, final AndroidUserInterface androidUserInterface) {
        Validate.notNull(context, "Context can not be null");
        Validate.notNull(androidUserInterface, "AndroidUserInterface can not be null");

        final LayoutInflater inflater = LayoutInflater.from(context);

        final View topicDialog = inflater.inflate(R.layout.topic_dialog, null);
        final EditText topicDialogInput = topicDialog.findViewById(R.id.topicDialogInput);

        blockNewLinesInTheInput(topicDialogInput);
        setCurrentTopicInTheInput(androidUserInterface, topicDialogInput);
        selectAllTheTextInTheInput(topicDialogInput);

        final AlertDialog alertDialog = createTopicDialog(androidUserInterface, context, topicDialog, topicDialogInput);
        makeSoftwareKeyboardVisible(alertDialog);

        alertDialog.show();
    }

    private void blockNewLinesInTheInput(final EditText topicDialogInput) {
        topicDialogInput.addTextChangedListener(new NoNewLineTextWatcher());
    }

    private void setCurrentTopicInTheInput(final AndroidUserInterface ui, final EditText topicDialogInput) {
        topicDialogInput.setText(ui.getTopic());
    }

    private void selectAllTheTextInTheInput(final EditText topicDialogInput) {
        topicDialogInput.setSelection(0, topicDialogInput.getText().length());
    }

    private AlertDialog createTopicDialog(final AndroidUserInterface ui, final Context context, final View topicDialog,
                                          final EditText topicDialogInput) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setView(topicDialog);
        builder.setTitle(R.string.topic);
        builder.setIcon(R.drawable.ic_dialog);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                ui.changeTopic(topicDialogInput.getText().toString());
            }
        });

        builder.setNegativeButton(android.R.string.cancel, null);

        return builder.create();
    }

    /**
     * The software keyboard is hidden on opening the dialog on some devices.
     * Settings this input mode before showing the dialog seems to fix this.
     * <p>
     * It does not seem to force the software keyboard on devices with a hardware keyboard.
     */
    private void makeSoftwareKeyboardVisible(final AlertDialog alertDialog) {
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}
