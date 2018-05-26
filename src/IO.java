import com.xtase.file.FileUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class IO {

	public static String cat(String file) throws Exception {
		return FileUtils.cat(file);
	}

	public static void write(String file, String content) throws Exception {
		FileUtils.write(file, content);
	}

}