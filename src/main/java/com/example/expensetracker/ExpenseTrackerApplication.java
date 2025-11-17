package com.example.expensetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.lang.reflect.Array;
import java.util.Arrays;

@SpringBootApplication
public class ExpenseTrackerApplication {

    public static void main(String[] args) {

        AbstractApplicationContext context = new AnnotationConfigApplicationContext(ExpenseTrackerApplication.class);
        Arrays.stream(context.getBeanDefinitionNames()).forEach(System.out::println);
        SpringApplication.run(ExpenseTrackerApplication.class, args);

    }
}
