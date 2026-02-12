package com.grape.utils;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Component
public class DateFormatter {

        private static final DateTimeFormatter FORMATTER
                = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        public LocalDateTime getNow() {
            return LocalDateTime.now();
        }

        public String getNowFormat() {
            return LocalDateTime.now().format(FORMATTER);
        }

        public LocalDateTime getMariaDbDateTime() {
            return LocalDateTime.parse(getNowFormat(), FORMATTER);
        }
}
