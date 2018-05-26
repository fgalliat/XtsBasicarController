package com.xtase.file;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

/**
 * Created by fgalliat on 26/05/18.
 */
public class FileUtils {


    public static String cat(String file) throws Exception {
        String buff = "";
        String line;
        BufferedReader in = new BufferedReader(new InputStreamReader(
                new FileInputStream(file)));
        while ((line = in.readLine()) != null) {
            buff += line + "\n";
        }
        in.close();
        return buff;
    }

    public static void write(String file, String content) throws Exception {
        FileOutputStream fOut = new FileOutputStream(file);
        fOut.write(content.getBytes());
        fOut.flush();
        fOut.close();
    }


}
