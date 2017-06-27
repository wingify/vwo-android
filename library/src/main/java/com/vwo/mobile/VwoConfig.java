package com.vwo.mobile;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aman on 22/06/17.
 */

public class VwoConfig {
    // This variable
    private Map<String, String> customSegmentationMapping;

    private VwoConfig(Map<String, String> customSegmentationMapping) {
        this.customSegmentationMapping = customSegmentationMapping;
    }

    public Map<String, String> getCustomSegmentationMapping() {
        return customSegmentationMapping;
    }

    public void setCustomSegmentationMapping(Map<String, String> customSegmentationMapping) {
        this.customSegmentationMapping = customSegmentationMapping;
    }

    public static class Builder {
        // This variable
        private Map<String, String> customSegmentationMapping;

        public Builder from() {
            return this;
        }

        public VwoConfig build() {
            return new VwoConfig(customSegmentationMapping);
        }

        public Builder setCustomSegmentationMapping(Map<String, String> customSegmentationMapping) {
            if(customSegmentationMapping == null) {
                throw new NullPointerException("Mapping cannot be null");
            }
            this.customSegmentationMapping = customSegmentationMapping;
            return this;
        }
    }
}
