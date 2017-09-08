package com.vwo.mobile.network;

import java.util.Map;

/**
 * Created by aman on 08/09/17.
 */

public class NetworkUtils {

    public static class Headers {
        public static final String HEADER_CONTENT_TYPE = "Content-type";
        public static final String CONTENT_TYPE_JSON = "application/json";
        public static final String CONTENT_TYPE_PLAIN = "text/plain";
        public static final String CONTENT_TYPE_FORM_URL_ENCODED = "application/x-www-form-urlencoded";

        public static final String HEADER_CHARSET = "charset";
        public static final String CHARSET_DEFAULT = "utf-8";

        public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
        public static final String ENCODING_GZIP = "gzip";

        public static final String HEADER_CACHE_CONTROL = "Cache-Control";
        public static final String CACHE_NO = "no-cache";

        public static String parseCharset(Map<String, String> headers, String defaultCharset) {
            String contentType = headers.get(HEADER_CONTENT_TYPE);
            if (contentType != null) {
                String[] params = contentType.split(";");
                for (int i = 1; i < params.length; i++) {
                    String[] pair = params[i].trim().split("=");
                    if (pair.length == 2) {
                        if (pair[0].equals("charset")) {
                            return pair[1];
                        }
                    }
                }
            }

            return defaultCharset;
        }

        public static String parseCharset(Map<String, String> headers) {
            return parseCharset(headers, CHARSET_DEFAULT);
        }
    }

}
