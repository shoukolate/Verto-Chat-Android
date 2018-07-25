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

package org.dynamicsoft.vertochat.android.util;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closer;

import org.dynamicsoft.vertochat.android.filetransfer.AndroidFileUtils;
import org.dynamicsoft.vertochat.util.Tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static junit.framework.Assert.assertTrue;

/**
 * Android file related utility methods.
 *
 * @author Christian Ihle
 */
public final class FileUtils {

    private static final String VERTOCHAT_FILE = "vertochat-1600x1600.png";

    private FileUtils() {
        // Only static methods here
    }

    /**
     * Copies the file <code>vertochat-1600x1600.png</code> from assets to the root of the SD card,
     * if it's not already there. It will also be added to the media database.
     * <p>
     * <p>This will fail if the SD card is unmounted.</p>
     *
     * @param instrumentation Test instrumentation.
     * @param activity        The activity under test.
     */
    public static void copyVertoChatImageFromAssetsToSdCard(final Instrumentation instrumentation, final Activity activity) {
        final File externalStorageDirectory = Environment.getExternalStorageDirectory();

        copyVertoChatImageFromAssetsToStorage(instrumentation, activity, externalStorageDirectory, true);
    }

    /**
     * Copies the file <code>vertochat-1600x1600.png</code> from assets to the cache directory of the internal storage,
     * if it's not already there. It will not be added to the media database.
     * <p>
     * <p>The internal storage should be available even if the SD card is unmounted.</p>
     *
     * @param instrumentation Test instrumentation.
     * @param activity        The activity under test.
     */
    public static void copyVertoChatImageFromAssetsToInternalStorage(final Instrumentation instrumentation, final Activity activity) {
        final File cacheDir = activity.getCacheDir();

        copyVertoChatImageFromAssetsToStorage(instrumentation, activity, cacheDir, false);
    }

    /**
     * Returns a representation of <code>vertochat-1600x1600.png</code> with a <code>content://</code> uri
     * that can be used to get the actual file on the SD card.
     *
     * @param activity The activity under test.
     * @return <code>vertochat-1600x1600.png</code>.
     */
    public static AndroidFile getVertoChatImageFromSdCardWithContentUri(final Activity activity) {
        final Cursor cursor = getCursorForVertoChatImageFromExternalStorage(activity);

        return getVertoChatImageFromStorage(cursor);
    }

    /**
     * Returns a representation of <code>vertochat-1600x1600.png</code> with a <code>file://</code> uri
     * that can be used to get the actual file on the SD card.
     *
     * @return <code>vertochat-1600x1600.png</code>.
     */
    public static AndroidFile getVertoChatImageFromSdCardWithFileUri() {
        final File externalStorageDirectory = Environment.getExternalStorageDirectory();
        final File image = new File(externalStorageDirectory, VERTOCHAT_FILE);

        return new AndroidFile(image);
    }

    /**
     * Returns a representation of <code>vertochat-1600x1600.png</code> than can be used to get the
     * actual file on the internal storage.
     *
     * @param activity The activity under test.
     * @return <code>vertochat-1600x1600.png</code>.
     */
    public static AndroidFile getVertoChatImageFromInternalStorage(final Activity activity) {
        final File cacheDir = activity.getCacheDir();
        final File image = new File(cacheDir, VERTOCHAT_FILE);

        return new AndroidFile(image);
    }

    /**
     * Creates a new unique file with the following pattern: <code>vertochat-current_time.extension</code>.
     *
     * @param image The image to get the extension from.
     * @return A new unique file. Should not exist on the file system.
     */
    public static File createNewFile(final AndroidFile image) {
        final File externalStorageDirectory = Environment.getExternalStorageDirectory();
        final String fileName = "vertochat-" + System.currentTimeMillis() + image.getExtension();

        return new File(externalStorageDirectory, fileName);
    }

    /**
     * Deletes the file from the SD card, and removes it from the media database.
     *
     * @param contentResolver The content resolver with the media database.
     * @param file            The file to delete. Can be <code>null</code>.
     */
    public static void deleteFileFromSdCard(final ContentResolver contentResolver, final File file) {
        if (file == null || !file.exists()) {
            return;
        }

        final Uri from = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        final String where = MediaStore.MediaColumns.DATA + " = ?";
        final String[] whereArguments = {file.getPath()};
        final String orderBy = MediaStore.Images.Media._ID + " ASC LIMIT 1";

        // SELECT * FROM images WHERE (_data = ?) ORDER BY _id ASC LIMIT 1
        final Cursor cursor = contentResolver.query(from, null, where, whereArguments, orderBy);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToNext();

            final int contentId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
            final Uri contentUri = ContentUris.withAppendedId(from, contentId);

            final int numberOfDeletedRows = contentResolver.delete(contentUri, null, null);

            if (numberOfDeletedRows == 1) {
                file.delete();
            }
        }
    }

    private static void copyVertoChatImageFromAssetsToStorage(final Instrumentation instrumentation,
                                                              final Activity activity,
                                                              final File storageDirectory,
                                                              final boolean addFileToDatabase) {
        final File fileToStore = new File(storageDirectory, VERTOCHAT_FILE);

        if (!fileToStore.exists()) {
            copyFileToDevice(fileToStore, instrumentation);

            if (addFileToDatabase) {
                addFileToDatabase(activity, fileToStore);
            }
        }
    }

    private static AndroidFile getVertoChatImageFromStorage(final Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {
            throw new RuntimeException("No files in the database");
        }

        cursor.moveToFirst();

        return new AndroidFile(cursor);
    }

    private static Cursor getCursorForVertoChatImageFromExternalStorage(final Activity activity) {
        final ContentResolver contentResolver = activity.getContentResolver();

        final Uri from = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        final String where = MediaStore.Images.Media.DISPLAY_NAME + " = ?";
        final String[] whereArguments = {VERTOCHAT_FILE};
        final String orderBy = MediaStore.Images.Media._ID + " ASC LIMIT 1";

        // SELECT * FROM images WHERE (_display_name = ?) ORDER BY _id ASC LIMIT 1
        return contentResolver.query(from, null, where, whereArguments, orderBy);
    }

    private static void copyFileToDevice(final File fileToStore, final Instrumentation instrumentation) {
        final Closer closer = Closer.create();
        final AssetManager assets = instrumentation.getContext().getResources().getAssets();

        try {
            final InputStream inputStream = closer.register(assets.open(fileToStore.getName()));
            final FileOutputStream outputStream = closer.register(new FileOutputStream(fileToStore));

            ByteStreams.copy(inputStream, outputStream);
            outputStream.flush();
            assertTrue("Should exist: " + fileToStore, fileToStore.exists());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                closer.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void addFileToDatabase(final Activity activity, final File fileToScan) {
        new AndroidFileUtils().addFileToMediaDatabase(activity, fileToScan);
        Tools.sleep(1000); // To give the scanner time to finish
    }
}
