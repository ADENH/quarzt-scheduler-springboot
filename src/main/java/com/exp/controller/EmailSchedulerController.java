package com.exp.controller;

import com.exp.dto.request.EmailRequest;
import com.exp.dto.request.EmailResponse;
import com.exp.serviceimpl.NotificationJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.UUID;

@Slf4j
@RestController
public class EmailSchedulerController {

    @Autowired
    private Scheduler scheduler;

    @PostMapping("send")
    public ResponseEntity<EmailResponse> scheduleEmail( @RequestBody EmailRequest emailRequest){
        try{
            ZonedDateTime dateTime = ZonedDateTime.of(emailRequest.getDateTime(), emailRequest.getTimeZone());
            if(dateTime.isBefore(ZonedDateTime.now())){
                EmailResponse emailResponse = new EmailResponse(false,
                        "dateTime harus setelah ini");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(emailResponse);
            }
            JobDetail jobDetail = buildJobDetail(emailRequest);
            Trigger trigger = buildTrigger(jobDetail, dateTime);
            scheduler.scheduleJob(jobDetail,trigger);
            EmailResponse emailResponse = new EmailResponse(true,
                    jobDetail.getKey().getName(), jobDetail.getKey().getGroup(),
                    "success send notif");
            return ResponseEntity.ok(emailResponse);
        }catch (SchedulerException se){
            log.error(se.getMessage());
            EmailResponse emailResponse = new EmailResponse(false,
                    "error scheduling notification");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(emailResponse);
        }
    }

    private JobDetail buildJobDetail(EmailRequest scheduleEmailRequest){
        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("subject",scheduleEmailRequest.getSubject());
        jobDataMap.put("body", scheduleEmailRequest.getBody());
        jobDataMap.put("email", scheduleEmailRequest.getEmail());

        return JobBuilder.newJob(NotificationJob.class)
                .withIdentity(UUID.randomUUID().toString(),"notification-jobs")
                .withDescription("send notification job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildTrigger(JobDetail jobDetail, ZonedDateTime startAt){
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "notification-triggers")
                .withDescription("send notification")
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }
}
