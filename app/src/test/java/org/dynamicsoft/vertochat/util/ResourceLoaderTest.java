/***************************************************************************
 *   Copyright 2006-2019 by Christian Ihle                                 *
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

package org.dynamicsoft.vertochat.util;

import org.dynamicsoft.vertochat.junit.ExpectedException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Test of {@link ResourceLoader}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class ResourceLoaderTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ResourceLoader resourceLoader;

    @Before
    public void setUp() {
        resourceLoader = new ResourceLoader();
    }

    @Test
    public void getResourceShouldThrowExceptionIfPathIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Path can not be empty");

        resourceLoader.getResource(null);
    }

    @Test
    public void getResourceShouldThrowExceptionIfPathIsBlank() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Path can not be empty");

        resourceLoader.getResource(" ");
    }

    @Test
    public void getResourceShouldReturnValidUrlIfFileExists() {
        assertNotNull(resourceLoader.getResource("/test-messages.properties"));
    }

    @Test
    public void getResourceShouldReturnNullIfFileDoesNotExist() {
        assertNull(resourceLoader.getResource("/sounds/unknown.wav"));
    }
}
