package com.vwo.mobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by aman on Tue 09/01/18 18:01.
 */

public class TestUtils {

    public static String readJsonFile(Class clazz, String name) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(clazz.getClassLoader().getResourceAsStream(name)));
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();
        while (line != null) {
            sb.append(line);
            line = br.readLine();
        }

        return sb.toString();
    }
}
