package com.bingbingpa.job;

import com.bingbingpa.listener.JobResultListener;
import com.bingbingpa.listener.StepResultListener;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j // log 사용을 위한 lombok 어노테이션
@RequiredArgsConstructor // 생성자 DI를 위한 lombok 어노테이션
@Configuration
public class JobListenerConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory; 

    @Bean
    public Job demoJob(){
        return jobBuilderFactory.get("listenerJob")
                .incrementer(new RunIdIncrementer())
                .listener(new JobResultListener())
                .start(stepOne())
                .next(stepTwo())
                .build();
    }

    @Bean
    @JobScope
    public Step stepOne(){
        return stepBuilderFactory.get("stepOne")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> Start!");
                    return RepeatStatus.FINISHED;
                })
                .listener(new StepResultListener())
                .build();
    }
    
    @Bean
    @JobScope
    public Step stepTwo(){
        return stepBuilderFactory.get("stepTwo")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> Stop!");
                    return RepeatStatus.FINISHED;
                })
                .listener(new StepResultListener())
                .build();
    } 
}