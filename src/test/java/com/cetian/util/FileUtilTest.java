package com.cetian.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright [2014] [zangrong CetianTech]
 */
@Slf4j
class FileUtilTest {

    @Test
    void joinPath() {
        log.info("test Linux path parent with / end");
        String parent1 = "/home/test/";
        String[] parts1 = ArrayUtils.toArray("/one", "two");
        String path1 = FileUtil.joinPath(parent1, parts1);
        assertEquals("/home/test/one/two", path1, "path join error");

        log.info("test Windows path parent with / end");
        String parentWindows = "C:\\ABC\\test";
        String[] parts2 = ArrayUtils.toArray("\\one", "/two");
        String path2 = FileUtil.joinPath(parentWindows, parts2);
        assertEquals("C:/ABC/test/one/two", path2, "path join error");

    }

    @Test
    void getExtensionName() {
        String filename1 = "test.jpeg";
        String extensionName1 = FileUtil.getExtensionName(filename1);
        assertEquals("jpeg", extensionName1, "extension name error");

        String filename2 = ".jpeg";
        String extensionName2 = FileUtil.getExtensionName(filename2);
        assertEquals("jpeg", extensionName2, "extension name error");

        String filename3 = "";
        String extensionName3 = FileUtil.getExtensionName(filename3);
        assertEquals("", extensionName3, "extension name error");

        String filename4 = null;
        String extensionName4 = FileUtil.getExtensionName(filename4);
        assertEquals(null, extensionName4, "extension name error");
    }


}