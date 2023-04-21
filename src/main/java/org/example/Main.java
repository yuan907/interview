package org.example;

import groovy.swing.factory.BeanFactory;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: yuanchao
 * @DATE: ${DATE} ${TIME}
 */
@SpringBootApplication
@EnableProcessApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

    }

}