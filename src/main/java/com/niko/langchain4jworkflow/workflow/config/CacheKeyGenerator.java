package com.niko.langchain4jworkflow.workflow.config;

import org.apache.commons.codec.digest.DigestUtils;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CacheKeyGenerator {
    private static final String SEPARATOR = ":";

    public static String generateKey(String... parts) {
        String key = Arrays.stream(parts)
                .filter(part -> part != null && !part.isEmpty())
                .collect(Collectors.joining(SEPARATOR));
        return DigestUtils.md5Hex(key);
    }

    public static String generateKey(Object... parts) {
        return generateKey(Arrays.stream(parts)
                .map(String::valueOf)
                .toArray(String[]::new));
    }
}
