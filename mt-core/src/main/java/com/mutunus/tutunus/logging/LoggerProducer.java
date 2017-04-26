package com.mutunus.tutunus.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoggerProducer {

    public static Logger getLogger(Class<?> clazz) {
        Logger logger = LoggerFactory.getLogger(clazz);
        return logger;
    }

}