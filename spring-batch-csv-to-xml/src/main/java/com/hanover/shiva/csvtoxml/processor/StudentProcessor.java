package com.hanover.shiva.csvtoxml.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import com.hanover.shiva.csvtoxml.model.Student;

@Slf4j
public class StudentProcessor implements ItemProcessor<Student, Student> {

  @Override
  public Student process(Student item) throws Exception {
    System.out.println("Student processed : " + item);
    return item;
  }
}
