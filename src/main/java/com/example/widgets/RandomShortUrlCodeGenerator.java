package com.example.widgets;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class RandomShortUrlCodeGenerator implements ShortUrlCodeGenerator {

    private static final int CODE_LENGTH = 7;
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @Override
    public String generateCode() {
        StringBuilder builder = new StringBuilder(CODE_LENGTH);
        for (int index = 0; index < CODE_LENGTH; index++) {
            builder.append(BASE62.charAt(ThreadLocalRandom.current().nextInt(BASE62.length())));
        }
        return builder.toString();
    }
}
