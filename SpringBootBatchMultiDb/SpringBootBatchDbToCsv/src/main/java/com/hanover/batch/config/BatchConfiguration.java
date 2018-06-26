package com.hanover.batch.config;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.hanover.batch.model.Employee;
import com.hanover.batch.processor.EmployeeItemProcessor;

@Configuration
@EnableBatchProcessing
@ComponentScan(basePackageClasses=DefaultBatchConfigurer.class)
public class BatchConfiguration {
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	/*@Autowired
	private DataSource dataSource;*/
	
	@Autowired
	@Qualifier("datasource1")
	DataSource datasource1;
	
	@Autowired
	@Qualifier("datasource2")
	DataSource datasource2;
	
	@Bean
	public JdbcCursorItemReader<Employee> reader(){
		JdbcCursorItemReader<Employee> cursorItemReader = new JdbcCursorItemReader<>();
		cursorItemReader.setDataSource(datasource1);
		cursorItemReader.setSql("SELECT empid,empname,empsalary FROM employee");
		cursorItemReader.setRowMapper(new EmployeeRowMapper());
		return cursorItemReader;
	}
	
	@Bean
	public EmployeeItemProcessor processor(){
		return new EmployeeItemProcessor();
	}
	
	/*@Bean
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
	}*/
	
	
	@Bean
	public JdbcBatchItemWriter<Employee> writer1(){
		JdbcBatchItemWriter<Employee> writer = new JdbcBatchItemWriter<>();
		writer.setDataSource(datasource1);
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Employee>());
		writer.setSql("INSERT INTO employee_new(empid,empname,empsalary) VALUES(:empId, :empName, :empSalary)");
		return writer;
	}
	
	@Bean
	public JdbcBatchItemWriter<Employee> writer2(){
		JdbcBatchItemWriter<Employee> writer = new JdbcBatchItemWriter<>();
		writer.setDataSource(datasource2);
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Employee>());
		writer.setSql("INSERT INTO employee(empid,empname,empsalary) VALUES(:empId, :empName, :empSalary)");
		return writer;
	}
	
	@Bean
	public CompositeItemWriter<Employee> compositeWriter(){
		CompositeItemWriter<Employee> writer = new CompositeItemWriter<>();
		List<ItemWriter<? super Employee>> writerList = new ArrayList<>();
		writerList.add(writer1());
		writerList.add(writer2());
		writer.setDelegates(writerList);
		return writer;
	}
	
	
	
	@Bean
	public Step step1(){
		return stepBuilderFactory.get("step1").<Employee,Employee>chunk(10).reader(reader()).processor(processor()).writer(compositeWriter()).build();
	}

	@Bean
	public Job exportPerosnJob(){
		return jobBuilderFactory.get("exportPeronJob").incrementer(new RunIdIncrementer()).flow(step1()).end().build();
	}
}
