package com.mycmsms4batchjob.configuration;

import com.mycmsms4batchjob.domain.Customer;
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
//    @Autowired
    private JobBuilderFactory jobBuilderFactory;

//    @Autowired
    private StepBuilderFactory stepBuilderFactory;

//    @Autowired
    private CustomerRepository customerRepository;

    // start::reader[]
    @Bean
    public FlatFileItemReader<Customer> reader() {
        FlatFileItemReader<Customer> flatFileItemReader = new FlatFileItemReader<>();
        // csv consist of 1000 rows. completed in 4 second
//        flatFileItemReader.setResource(new FileSystemResource("/Users/dzakirinmuhammad/IdeaProjects/my-cms/microservices/batch-job/src/main/resources/customers.csv"));
        flatFileItemReader.setResource(new FileSystemResource("src/main/resources/customers.csv"));
        flatFileItemReader.setName("csvReaders");
        // This will skip the first row (the csv header)
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setLineMapper(lineMapper());

        return flatFileItemReader;
    }

    private LineMapper<Customer> lineMapper() {

        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

        // Line tokenizer will extract the value from csv file
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        // using comma as delimiter
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        // set the file header
        lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country" , "dob");

        // fieldSetMapper will map csv value to Customer object
        BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Customer.class);

        // sending both lineTokenizer and FieldSetMapper to LineMapper
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

        // Telling the writer to use customerRepository.save() method to write the csv data to the DB
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
//                .next(anotherStep)
                .end()
                .build();
    }
    // end::job[]

    // start::taskexecutor[]
    /**
     * THIS IS NOT REQUIRE TO BE USED !!!!
     * Asynchronous job to create 10 thread will execute parallel/concurrently in processing the batch
     * @return asyncTaskExecutor that will run the thread in random order
     */
    @Bean
    public TaskExecutor taskExecutor() {

        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);

        return  asyncTaskExecutor;
    }
    // end::taskexecutor[]
}
