package com.grape.common;

import lombok.Getter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class DateFormat {

        private static final DateTimeFormatter FORMATTER
                = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        public String getNow() {
            return LocalDateTime.now().format(FORMATTER);
        }
}
