
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.dynamicsoft.vertochat.android.R;
import org.dynamicsoft.vertochat.android.chatwindow.AndroidUserInterface;
import org.dynamicsoft.vertochat.junit.ExpectedException;
import org.dynamicsoft.vertochat.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowContextThemeWrapper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Test of {@link TopicDialog}.
 *
 * @author Christian Ihle
 */
@RunWith(RobolectricTestRunner.class)
public class TopicDialogTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ShadowAlertDialog shadowDialog;
    private AlertDialog dialog;

    private TextView dialogMessage;
    private EditText dialogInput;

    private AndroidUserInterface ui;

    @Before
    public void setUp() {
        ui = mock(AndroidUserInterface.class);
        when(ui.getTopic()).thenReturn("The original topic");

        new TopicDialog(Robolectric.application, ui); // Dialog would be shown after this

        dialog = ShadowAlertDialog.getLatestAlertDialog();
        shadowDialog = Robolectric.shadowOf(dialog);

        final View dialogView = shadowDialog.getView();
        dialogMessage = (TextView) dialogView.findViewById(R.id.topicDialogMessage);
        dialogInput = (EditText) dialogView.findViewById(R.id.topicDialogInput);
    }

    @Test
    public void constructorShouldThrowExceptionIfContextIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Context can not be null");

        new TopicDialog(null, ui);
    }

    @Test
    public void constructorShouldThrowExceptionIfAndroidUserInterfaceIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("AndroidUserInterface can not be null");

        new TopicDialog(Robolectric.application, null);
    }

    @Test
    public void dialogTitleShouldBeSet() {
        assertEquals("Topic", shadowDialog.getTitle());
    }

    @Test
    public void dialogIconShouldBeSet() {
        assertEquals(R.drawable.ic_dialog, shadowDialog.getShadowAlertController().getIconId());
    }

    @Test
    public void dialogThemeShouldBeSet() {
        final ContextThemeWrapper context = (ContextThemeWrapper) dialog.getContext();
        final ContextThemeWrapper baseContext = (ContextThemeWrapper) context.getBaseContext();
        final ShadowContextThemeWrapper shadowBaseContext = (ShadowContextThemeWrapper) Robolectric.shadowOf(baseContext);
        final int themeResId = shadowBaseContext.callGetThemeResId();

        assertEquals(R.style.Theme_Default_Dialog, themeResId);
    }

    @Test
    public void dialogMessageShouldBeSet() {
        assertEquals("Set or change the current topic.", dialogMessage.getText());
    }

    @Test
    public void dialogInputShouldHaveTheTopicFromAndroidUserInterface() {
        assertEquals("The original topic", dialogInput.getText().toString());
        verify(ui).getTopic();
    }

    @Test
    public void dialogInputShouldSetTextHint() {
        assertEquals("Enter topic", dialogInput.getHint());
    }

    @Test
    public void dialogInputShouldSetTheTopicAsSelected() {
        assertEquals(0, dialogInput.getSelectionStart());
        assertEquals("The original topic".length(), dialogInput.getSelectionEnd());
    }

    @Test
    public void dialogInputShouldHaveNoNewLineTextWatcher() {
        final List<TextWatcher> textWatchers = getTextWatchers(dialogInput);

        assertEquals(1, textWatchers.size());
        assertEquals(NoNewLineTextWatcher.class, textWatchers.get(0).getClass());
    }

    @Test
    public void dialogShouldHaveCancelButtonThatDoesNothingButClose() {
        final Button button = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        assertEquals("Cancel", button.getText());

        button.performClick();

        verify(ui, never()).changeTopic(anyString());
        assertFalse(dialog.isShowing());
    }

    @Test
    public void dialogShouldHaveOKButtonThatSetsTheCurrentTopicAndCloses() {
        dialogInput.setText("New topic");

        final Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        assertEquals("OK", button.getText());

        button.performClick();

        verify(ui).changeTopic("New topic");
        assertFalse(dialog.isShowing());
    }

    @Test
    public void dialogWindowShouldSetSoftKeyboardVisible() {
        final WindowManager.LayoutParams attributes = dialog.getWindow().getAttributes();

        assertEquals(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE, attributes.softInputMode);
    }

    @SuppressWarnings("unchecked")
    private List<TextWatcher> getTextWatchers(final EditText editText) {
        return TestUtils.getFieldValue(editText, List.class, "mListeners");
    }
}
