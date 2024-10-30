package com.example.batch;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.UUID;

@SpringBootApplication
public class BatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchApplication.class, args);
    }

    @Bean
    ApplicationRunner runner(JobLauncher jobLauncher, Job job) {
        return args -> {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("uuid", UUID.randomUUID().toString())
                    .toJobParameters();
            var run = jobLauncher.run(job, jobParameters);
            var instanceId = run.getJobInstance().getInstanceId();
            System.out.println("instanceId = " + instanceId);
        };
    }

    @Bean
    @StepScope
	Tasklet tasklet(@Value("#{jobParameters['uuid']}") String uuid) {
        return (contribution, chunkContext) -> {
            System.out.println("Hello World the uuid is  " + uuid);
            return RepeatStatus.FINISHED;
        };
    }
	@Bean
	Job job(JobRepository jobRepository, Step step) {
        return new JobBuilder("job", jobRepository)
                .start(step)
                .build();
    }
	@Bean
	    Step step(JobRepository jobRepository, Tasklet tasklet, PlatformTransactionManager tx) {
        return new StepBuilder("step1", jobRepository)
                .tasklet(tasklet, tx)
                .build();
    }

//    @Bean
//    Job job(JobBuilder jobBuilder, PlatformTransactionManager tx,StepBuilder stepBuilder) {
////		var step = (Step) stepBuilder.tasklet(new Tasklet() {
////			@Override
////			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
////				System.out.println("Hello World");
////				return RepeatStatus.FINISHED;
////			}
////		}, tx).build();
//		var step = (Step) stepBuilder.tasklet((contribution, chunkContext) -> {
//                            System.out.println("Hello World");
//            return RepeatStatus.FINISHED;
//        }, tx).build();
//        return jobBuilder
//                .start(step)
//				.build();
//    }

}
