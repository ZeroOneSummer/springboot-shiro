package com.bocsoft.obss.quartz.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.time.ZoneId;
import java.util.TimeZone;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JobUtil {

    public static JobDetail getJobDetail(String jobName, Class<? extends Job> jobClass) {
        return getJobDetail(jobName, null, jobClass);
    }

    public static Trigger getTrigger(String jobName, String cron, JobDetail jobDetail) {
        return getTrigger(jobName, cron, null, jobDetail);
    }

    public static JobDetail getJobDetail(String jobName, String jobGroup, Class<? extends Job> jobClass) {
        return JobBuilder.newJob(jobClass).withIdentity(jobName + "Job", jobGroup).storeDurably().build();
    }

    public static Trigger getTrigger(String jobName, String cron, String cronGroup, JobDetail jobDetail) {
        TimeZone timeZone = TimeZone.getTimeZone(ZoneId.systemDefault());
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron).inTimeZone(timeZone);
        return TriggerBuilder.newTrigger().forJob(jobDetail).withIdentity(jobName + "Trigger", cronGroup).withSchedule(cronScheduleBuilder).build();
    }

}
