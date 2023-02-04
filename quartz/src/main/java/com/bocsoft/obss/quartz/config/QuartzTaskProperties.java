package com.bocsoft.obss.quartz.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "zero.quartz")
public class QuartzTaskProperties {
    private List<QuartzTask> taskList = Collections.emptyList();

    @Setter
    @Getter
    public static class QuartzTask {
        private String taskName;
        private String taskCron;
        private Map<String, Object> taskParams = Collections.emptyMap();
    }
}
