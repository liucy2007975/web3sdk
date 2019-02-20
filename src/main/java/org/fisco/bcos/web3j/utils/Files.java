package org.fisco.bcos.web3j.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * File utility functions.
 */
public class Files {
    static Logger logger = LoggerFactory.getLogger(Files.class);
    private Files() { }

    public static byte[] readBytes(File file) throws IOException {
        byte[] bytes = new byte[(int) file.length()];
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            int count = 0;
            while ((count = fileInputStream.read(bytes)) > 0) {
                
            }
        }catch (Exception e)
        {
            logger.error("readBytes error :", e);
            throw e;
        }finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
        return bytes;
    }

    public static String readString(File file) throws IOException {
        return new String(readBytes(file));
    }
}
