package com.vwo.mobile.utils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by aman on 26/04/17.
 */

public class VWOLogger {

    static public Logger getLogger(String name) {
        Logger logger = Logger.getLogger(name);
        // suppress the logging output to the console
        Handler[] handlers = logger.getHandlers();
        for (Handler handler : handlers) {
            logger.removeHandler(handler);
        }

        logger.setLevel(Level.ALL);
        ConsoleHandler consoleHTML = new ConsoleHandler();

        // create an HTML formatter
        Formatter formatterHTML = new HTMLLogFormatter();
        consoleHTML.setFormatter(formatterHTML);
        logger.addHandler(consoleHTML);
        return logger;
    }
}
