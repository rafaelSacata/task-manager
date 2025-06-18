package br.com.rafaelaranda.task_manager.config.logger;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class BeanLogger implements CommandLineRunner {

    @Autowired
    private ApplicationContext context;

    @Override
    public void run(String... args) {
        Arrays.stream(context.getBeanDefinitionNames())
              .filter(name -> name.contains("userMapper"))
              .forEach(System.out::println);
    }


}

