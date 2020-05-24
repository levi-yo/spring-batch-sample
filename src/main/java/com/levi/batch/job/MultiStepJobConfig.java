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
public class MultiStepJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job multiStepJob(
            final Step multiStep,
            final Step multiStep2
            ) {
        return jobBuilderFactory.get("multiStepJob")
                .start(multiStep)
                .next(multiStep2)
                .build();

    }

    @Bean
    @JobScope
    public Step multiStep(@Value("#{jobParameters[requestDate]}") final String requestDate) {
        return stepBuilderFactory.get("multiStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info("MultiStepJobConfig ::: multiStep");
                    log.info("MultiStepJobConfig ::: requestData = {}", requestDate);
//                    throw new IllegalArgumentException("invalid argument");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    @JobScope
    public Step multiStep2(@Value("#{jobParameters[requestDate]}") final String requestDate) {
        return stepBuilderFactory.get("multiStep2")
                .tasklet((contribution, chunkContext) -> {
                    log.info("MultiStepJobConfig ::: multiStep2");
                    log.info("MultiStepJobConfig ::: requestData = {}", requestDate);
                    return RepeatStatus.FINISHED;
                }).build();
    }
}
