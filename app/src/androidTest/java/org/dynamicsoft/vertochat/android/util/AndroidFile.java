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

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import org.dynamicsoft.vertochat.util.Tools;

import java.io.File;

/**
 * Stores details about a file in the context of the Android file system.
 *
 * @author Christian Ihle
 */
public class AndroidFile {

    private final Uri uri;
    private final File file;
    private final String extension;
    private final String baseName;

    public AndroidFile(final Cursor cursor) {
        final String uriPath = MediaStore.Images.Media.EXTERNAL_CONTENT_URI + "/" +
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
        final String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));

        this.uri = Uri.parse(uriPath);
        this.file = new File(path);
        this.extension = Tools.getFileExtension(getName());
        this.baseName = Tools.getFileBaseName(getName());
    }

    public AndroidFile(final File file) {
        this.uri = Uri.fromFile(file);
        this.file = file;
        this.extension = Tools.getFileExtension(getName());
        this.baseName = Tools.getFileBaseName(getName());
    }

    public Uri getUri() {
        return uri;
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return file.getName();
    }

    public String getExtension() {
        return extension;
    }

    public String getBaseName() {
        return baseName;
    }
}
