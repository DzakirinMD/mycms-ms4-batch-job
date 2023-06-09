package com.mycmsms4batchjob.configuration;

import com.mycmsms4batchjob.entity.Customer;
import com.mycmsms4batchjob.processor.CustomerItemProcessor;
import com.mycmsms4batchjob.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class SpringBatchConfig {

    // not using @Autowired because only have 1 constructor.
    // @Autowired
    private JobBuilderFactory jobBuilderFactory;

    // @Autowired
    private StepBuilderFactory stepBuilderFactory;

    // @Autowired
    private CustomerRepository customerRepository;

    // start::reader[]
    @Bean
    public FlatFileItemReader<Customer> reader() {
        FlatFileItemReader<Customer> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new FileSystemResource("src/main/resources/customers.csv"));
        flatFileItemReader.setName("csvReaders");
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setLineMapper(lineMapper());

        return flatFileItemReader;
    }

    private LineMapper<Customer> lineMapper() {
        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");
        BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Customer.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }
    // end::reader[]

    // start::processor[]
    @Bean
    public CustomerItemProcessor processor() {
        return new CustomerItemProcessor();
    }
    // end::processor[]

    // start::writer[]
    @Bean
    public RepositoryItemWriter<Customer> writer() {
        RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);
        writer.setMethodName("save");

        return writer;
    }
    // end::writer[]

    // start::step[]
    @Bean
    public Step step1(){

        // process 10 record at a time. taskExecutor is used to process in 10 thread parallel
        return stepBuilderFactory.get("csv-step").<Customer,Customer>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
//                .taskExecutor(taskExecutor())
                .build();
    }
    // end::step[]

    // start::job[]
    /**
     * Batch job to process the step
     */
    @Bean
    public Job runCSVJob() {
        return jobBuilderFactory.get("importCustomer")
                .flow(step1())
                .end()
                .listener(jobCompletionListener()) // Add a job completion listener
                .build();
    }
    // end::job[]

    // start::listener[]
    @Bean
    public JobCompletionListener jobCompletionListener() {
        return new JobCompletionListener();
    }
    // end::listener[]

    // start::taskexecutor[]
    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;
    }
    // end::taskexecutor[]
}