package com.hanover.batch.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.hanover.batch.model.Employee;
import com.hanover.batch.processor.EmployeeItemProcessor;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private DataSource dataSource;
	
	@Bean
	public JdbcCursorItemReader<Employee> reader(){
		JdbcCursorItemReader<Employee> cursorItemReader = new JdbcCursorItemReader<>();
		cursorItemReader.setDataSource(dataSource);
		cursorItemReader.setSql("SELECT empid,empname,empsalary FROM employee");
		cursorItemReader.setRowMapper(new EmployeeRowMapper());
		return cursorItemReader;
	}
	
	@Bean
	public EmployeeItemProcessor processor(){
		return new EmployeeItemProcessor();
	}
	
	@Bean
	public FlatFileItemWriter<Employee> writer(){
		FlatFileItemWriter<Employee> writer = new FlatFileItemWriter<Employee>();
		writer.setResource(new ClassPathResource("employee.csv"));
		
		DelimitedLineAggregator<Employee> lineAggregator = new DelimitedLineAggregator<Employee>();
		lineAggregator.setDelimiter(",");
		
		BeanWrapperFieldExtractor<Employee>  fieldExtractor = new BeanWrapperFieldExtractor<Employee>();
		fieldExtractor.setNames(new String[]{"empId","empName","empSalary"});
		lineAggregator.setFieldExtractor(fieldExtractor);
		
		writer.setLineAggregator(lineAggregator);
		return writer;
	}
	
	@Bean
	public Step step1(){
		return stepBuilderFactory.get("step1").<Employee,Employee>chunk(10).reader(reader()).processor(processor()).writer(writer()).build();
	}

	@Bean
	public Job exportPerosnJob(){
		return jobBuilderFactory.get("exportPeronJob").incrementer(new RunIdIncrementer()).flow(step1()).end().build();
	}
}
