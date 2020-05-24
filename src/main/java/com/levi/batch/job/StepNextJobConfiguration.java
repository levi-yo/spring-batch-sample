package com.levi.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
//@Configuration
@RequiredArgsConstructor
public class StepNextJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job stepNextJob(final Step step1, final Step step2, final Step step3) {
        return jobBuilderFactory.get("stepNextJob")
                .start(step1)
                .next(step2)
                .next(step3)
                .build();
    }

    @Bean
    @JobScope
    public Step step1(@Value("#{jobParameters[version]}") final String version,
                      Tasklet tasklet) {
        log.info("StepNextJobConfiguration ::: step1 = {}", version);
        return stepBuilderFactory.get("step1")
                .tasklet(tasklet)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet tasklet(@Value("#{jobParameters[version]}") final String version) {
        return (contribution, chunkContext) -> {
            log.info("StepNextJobConfiguration ::: tasklet = {}", version);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
                .tasklet((contribution, chunkContext) -> {
                    log.info("StepNextJobConfiguration ::: step2");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step step3() {
        return stepBuilderFactory.get("step3")
                .tasklet((contribution, chunkContext) -> {
                    log.info("StepNextJobConfiguration ::: step3");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
