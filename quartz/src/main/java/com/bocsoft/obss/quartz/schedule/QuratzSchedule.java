package com.bocsoft.obss.quartz.schedule;

import com.bocsoft.obss.quartz.config.QuartzTaskProperties;
import com.bocsoft.obss.quartz.job.CftBackupJob;
import com.bocsoft.obss.quartz.job.CftReceiveJob;
import com.bocsoft.obss.quartz.util.JobUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class QuratzSchedule implements SchedulerFactoryBeanCustomizer {
    private static List<QuartzTaskProperties.QuartzTask> taskList;
    private static Map<String, String> quartProp;
    private static String groupName;

    @Autowired
    QuartzProperties quartzProperties;

    @Autowired
    QuartzTaskProperties quartzTaskProperties;

    @PostConstruct
    public void initProp() {
        taskList = quartzTaskProperties.getTaskList();
        quartProp = quartzProperties.getProperties();
        groupName = quartProp.getOrDefault("org.quartz.group-name", null);
    }

    @Override
    public void customize(SchedulerFactoryBean schedulerFactoryBean) {
        String startup = quartProp.getOrDefault("org.quartz.auto-startup", "true");
        String delay = quartProp.getOrDefault("org.quartz.startup-delay", "0");
        String instanceName = quartProp.getOrDefault("org.quartz.scheduler.instanceName", "quartzScheduler");
        log.info("定时任务开关：{}，延迟{}秒启动！", startup, delay);
        schedulerFactoryBean.setAutoStartup(Boolean.parseBoolean(startup));
        schedulerFactoryBean.setStartupDelay(Integer.parseInt(delay));
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setSchedulerName(instanceName);
    }

    /**
     * 任务1
     * @return
     */
    @Bean
    public JobDetail job1() {
        return JobUtil.getJobDetail(taskList.get(0).getTaskName(), groupName, CftReceiveJob.class);
    }
    @Bean
    public Trigger trigger1() {
        return JobUtil.getTrigger(taskList.get(0).getTaskName(), taskList.get(0).getTaskCron(), groupName, job1());
    }

    /**
     * 任务2
     * @return
     */
    @Bean
    public JobDetail job2() {
        return JobUtil.getJobDetail(taskList.get(1).getTaskName(), CftBackupJob.class);
    }
    @Bean
    public Trigger trigger2() {
        return JobUtil.getTrigger(taskList.get(1).getTaskName(), taskList.get(1).getTaskCron(), job2());
    }
}