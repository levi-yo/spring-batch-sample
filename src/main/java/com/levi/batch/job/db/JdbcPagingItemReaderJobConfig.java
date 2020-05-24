package com.levi.batch.job.db;

import com.levi.batch.entity.Pay;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JdbcPagingItemReaderJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job jdbcPagingItemReaderJob(final Step jdbcPagingItemReaderStep) {
        return jobBuilderFactory.get("jdbcPagingItemReaderJob")
                .start(jdbcPagingItemReaderStep)
                .build();
    }

    @Bean
    public Step jdbcPagingItemReaderStep(final ItemReader<Pay> jdbcPagingItemReader) {
        return stepBuilderFactory.get("jdbcPagingItemReaderStep")
                .<Pay, Pay>chunk(10)
                .reader(jdbcPagingItemReader)
                .writer(items -> {
                    items.forEach(i -> log.info("jdbcPagingItemReaderStep :::: {}", i.toString()));
                })
                .build();
    }

    @Bean
    public JdbcPagingItemReader<Pay> jdbcPagingItemReader(final PagingQueryProvider pagingQueryProvider) {
        return new JdbcPagingItemReaderBuilder<Pay>()
                .pageSize(10)
                .fetchSize(10)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Pay.class))
                .queryProvider(pagingQueryProvider)
                .parameterValues(Map.of("amount", 2000))
                .name("jdbcPagingItemReader")
                .build();
    }

    @Bean
    public PagingQueryProvider pagingQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean sqlPagingQueryProviderFactoryBean = new SqlPagingQueryProviderFactoryBean();
        sqlPagingQueryProviderFactoryBean.setDataSource(dataSource);
        sqlPagingQueryProviderFactoryBean.setSelectClause("id, amount, tx_name, tx_date_time");
        sqlPagingQueryProviderFactoryBean.setFromClause("from pay");
        sqlPagingQueryProviderFactoryBean.setWhereClause("where amount >= :amount");
        sqlPagingQueryProviderFactoryBean.setSortKeys(Map.of("id", Order.ASCENDING));
        return sqlPagingQueryProviderFactoryBean.getObject();
    }

}
