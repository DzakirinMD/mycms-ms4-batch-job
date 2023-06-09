## Batch Job
A common paradigm in batch processing is to ingest data, transform it, and then pipe it out somewhere else.

![Spring Batch](./img/BatchStereotypes.png)

Job Repository -> maintain state of job

## API Docs
Once the application is running on port 50003, access the API docs from web using [swagger-ui](http://localhost:50004/swagger-ui) or import to your Postman using this link http://localhost:50003/api-docs

batch version
java version
spring boot version 2.7.12


<h1>Dependency Version</h1>

| Project Dependency  | Version |
|---------------------|---------|
| `Spring Boot`       | 2.7.12  |
| `Java`              | 11      |
| `spring-batch-core` | 4.3.8   |


- JobBuilderFactory : Convenient factory for a JobBuilder which sets the JobRepository automatically. We will be autowiring this bean provided by Spring Batch.
- StepBuilderFactory : Convenient factory for a StepBuilder which sets the JobRepository and PlatformTransactionManager automatically. We will be autowiring this bean provided by Spring Batch.
- Job : Batch domain object representing a job. A job execution may consist of multiple steps which need to be excuted. We are executing a single step named moveFilesStep. Also we start the job using the autowired JobBuilderFactory.
- Step : Batch domain interface representing the configuration of a step. In this step we call the moveFilesTasklet to execute the task of moving the files from source to destination directory.
- Tasklet : The Tasklet is a simple interface with just one method execute. Using this we can perform single tasks like executing queries, deleting files. In our case we will be moving the files from source to destination directory.