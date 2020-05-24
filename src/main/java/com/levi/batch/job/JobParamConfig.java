package com.levi.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
//@Configuration
@RequiredArgsConstructor
public class JobParamConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job jobParamJob(final Step jobParamStep) {
        return jobBuilderFactory.get("jobParamJob")
                .start(jobParamStep)
                .build();

    }

    @Bean
    @JobScope
    public Step jobParamStep(@Value("#{jobParameters[requestDate]}") final String requestDate) {
        return stepBuilderFactory.get("jboParamStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info("JobParamConfig ::: jobParamStep");
                    log.info("JobParamConfig ::: requestData = {}", requestDate);
                    return RepeatStatus.FINISHED;
                }).build();
    }
}
