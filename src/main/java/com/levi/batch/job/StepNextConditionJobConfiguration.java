package com.levi.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
//@Configuration
@RequiredArgsConstructor
public class StepNextConditionJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job stepNextConditionJob(final Step stepCondition1, final Step stepCondition2, final Step stepCondition3) {
        return jobBuilderFactory.get("stepNextConditionJob")
                .start(stepCondition1)
                    .on("FAILED")
                    .to(stepCondition3)
                    .on("*") // step3의 결과와 관계 없이
                    .end()
                .from(stepCondition1)
                    .on("*") // stepCondition1 스텝이 FAILED이 아닌 모든 경우
                    .to(stepCondition2)
                    .next(stepCondition3)
                    .on("*")
                    .end()
                .end()
                .build();
    }

    @Bean
    public Step stepCondition1() {
        return stepBuilderFactory.get("stepCondition1")
                .tasklet((contribution, chunkContext) -> {
                    log.info("StepNextJobConfiguration ::: stepCondition1");

                    // on("FAILED")의 FAILED가 ExitStatus.FAILED 이다.
                    contribution.setExitStatus(ExitStatus.FAILED);

                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step stepCondition2() {
        return stepBuilderFactory.get("stepCondition2")
                .tasklet((contribution, chunkContext) -> {
                    log.info("StepNextJobConfiguration ::: stepCondition2");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step stepCondition3() {
        return stepBuilderFactory.get("stepCondition3")
                .tasklet((contribution, chunkContext) -> {
                    log.info("StepNextJobConfiguration ::: stepCondition3");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
