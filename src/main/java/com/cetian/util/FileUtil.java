/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright [2014] [zangrong CetianTech]
 */
package com.cetian.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;

/**
 * @Description
 *
 *
 *
 * @author zangrong
 * @Date 2020-01-20 06:02
 */
@Slf4j
public class FileUtil {

    /**
     * 创建一个文件夹如果未找到的话
     * @param path
     * @return
     */
    public static boolean createDirIfNotExist(String path){
        return createDirIfNotExist(new File(path));
    }

    /**
     * 创建一个文件夹如果未找到的话
     * @param path
     * @return
     */
    public static boolean createDirIfNotExist(File path){
        boolean result = false;
        if (!path.exists()) {
            try{
                FileUtils.forceMkdir(path);
                result = true;
            }catch(Exception e){
                log.warn("", e);
            }
        }
        return result;
    }

    /**
     * 创建一个文件夹，如果存在则清空
     * @param path
     * @return
     */
    public static boolean createDirWithClear(String path){
        boolean result = false;
        File pathDir = new File(path);
        if (pathDir.exists()) {
            try{
                // 删除清空
                FileUtils.forceDelete(pathDir);
                // 重新创建文件夹
                FileUtils.forceMkdir(pathDir);
                result = true;
            }catch(Exception e){
                log.warn("", e);
            }
        }
        return result;
    }

    /**
     * 计算指定文件夹下的文件数
     * @param dir 文件夹路径
     * @param pattern 文件特征
     * @param maxDepth 遍历层级
     * @return
     */
    public static long countFiles(String dir, String pattern, int maxDepth){
        long count = 0;
        Path dirPath = Paths.get(dir);
        BiPredicate<Path, BasicFileAttributes> xmlMatcher = (path, attributes) -> String.valueOf(path).contains(pattern);
        try{
            count = Files.find(dirPath, maxDepth, xmlMatcher).count();
        }catch(Exception ex){
            log.warn("", ex);
        }
        return count;
    }

    /**
     * 路径拼接，解决以下问题
     * 1、相邻路径间是否多余或缺少 / 分隔符的问题
     * 2、以及windows系统和linux / \ 的问题，统一为 /
     * @param parent 父目录
     * @param parts
     * @return
     */
    public static String joinPath(String parent, String... parts){
        parent = RegExUtils.replaceAll(parent, "\\\\", "/");
        // 如果parts为空则直接返回
        if (ArrayUtils.isEmpty(parts)) {
            return parent;
        }

        if (parent.endsWith("/")) {
            parent = StringUtils.substring(parent, 0, parent.length() - 1);
        }
        for (String part : parts) {
            part = RegExUtils.removeAll(part, "\\\\");
            part = RegExUtils.removeAll(part, "/");
            parent = parent + "/" + part;
        }
        return parent;
    }

    /**
     * 获取文件名，不含扩展名
     * @param file
     * @return
     */
    public static String getFileName(File file){
        String fileName = null;
        try{
            fileName = file.getName();
            int i = StringUtils.lastIndexOf(fileName, ".");
            if (i >= 0) {
                fileName = StringUtils.substring(fileName, 0, i);
            }
        }catch(Exception e){
            log.warn("", e);
        }
        return fileName;
    }

    /**
     * 获取文件扩展名，不含点
     * @param file
     * @return
     */
    public static String getExtensionName(File file){
        String extensionName = null;
        try{
            extensionName = file.getName();
            int i = StringUtils.lastIndexOf(extensionName, ".");
            if (i == -1) {
                return "";
            }else if (i == extensionName.length() - 1) {
                return "";
            }else if (i >= 0 ) {
                extensionName = StringUtils.substring(extensionName, i+1, extensionName.length());
            }
        }catch(Exception e){
            log.warn("", e);
        }
        return extensionName;
    }

    /**
     * 获取文件扩展名
     * @param filename
     * @return
     */
    public static String getExtensionName(String filename){
        String extensionName = null;
        try{
            if (filename == null){
                return extensionName;
            }
            int i = StringUtils.lastIndexOf(filename, ".");
            if (i == -1) {
                return "";
            }else if (i == filename.length() - 1) {
                return "";
            }else if (i >= 0 ) {
                extensionName = StringUtils.substring(filename, i + 1, filename.length());
            }
        }catch(Exception e){
            log.warn("", e);
        }
        return extensionName;
    }

}
