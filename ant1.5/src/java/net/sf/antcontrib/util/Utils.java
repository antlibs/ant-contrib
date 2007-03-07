package net.sf.antcontrib.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {

	public static void close(InputStream is) {
		try {
			if (is != null) {
				is.close();
			}
		}
		catch (IOException e) {
			;
		}
		
	}

	public static void close(OutputStream os) {
		try {
			if (os != null) {
				os.close();
			}
		}
		catch (IOException e) {
			;
		}
		
	}
}
