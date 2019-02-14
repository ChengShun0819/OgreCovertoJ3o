/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author shun.fang
 */
public class FileUtility {

    public static String getExtensionName(String file) {
        int startIndex = file.lastIndexOf('.') + 1;
        if (startIndex == 0) {
            return "";
        } else {
            return file.substring(startIndex);
        }
    }

    public static String getWithoutExtensionName(String file) {
        return FilenameUtils.removeExtension(file);
    }

    public static void copyFile(File srcFile, File dest) {
        if (!dest.exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            Files.copy(srcFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger.getLogger(FileUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void copyDirectory(File srcFile, File destFile) {
        try {
            if (!destFile.exists()) {
                destFile.mkdirs();
            }
            String[] fileList = srcFile.list();
            for (int fileNum = 0; fileNum < fileList.length; fileNum++) {
                File targetFile = new File(srcFile + File.separator + fileList[fileNum]);
                File dirFile = new File(destFile + File.separator + fileList[fileNum]);
                if (targetFile.isFile()) {
                    Files.copy(targetFile.toPath(), dirFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                } else if (targetFile.isDirectory()) {
                    copyDirectory(targetFile, dirFile);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(FileUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath);
            FileUtils.deleteDirectory(new File(folderPath));
        } catch (IOException ex) {
            Logger.getLogger(FileUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);
                delFolder(path + "/" + tempList[i]);
                flag = true;
            }
        }
        return flag;
    }
    
    public static String getCurrentDirectory() {
        File now_directory = new File("null");
        String current_path = now_directory.getAbsolutePath().replaceAll("null", "");
        return current_path;
    }
}
