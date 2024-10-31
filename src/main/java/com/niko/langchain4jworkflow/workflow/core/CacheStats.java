package com.niko.langchain4jworkflow.workflow.core;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CacheStats {
    private long hitCount;
    private long missCount;
    private long evictionCount;
    private long size;

    public double getHitRate() {
        long total = hitCount + missCount;
        return total == 0 ? 0.0 : (double) hitCount / total;
    }
}
