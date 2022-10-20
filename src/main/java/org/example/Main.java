package org.example;

import org.example.config.Config;
import org.example.model.Field;
import org.example.repository.FieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@EnableConfigurationProperties(value = Config.class)
@SpringBootApplication
public class Main implements CommandLineRunner {
    @Autowired
    FieldRepository fieldRepository;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) {
        if (fieldRepository.findAll().isEmpty()) {
            List<Field> fields = new ArrayList<>();
            Field title = new Field("title", "title", 1.0f);
            fields.add(title);
            Field body = new Field("body", "body", 0.8f);
            fields.add(body);
            fieldRepository.saveAll(fields);
        }
    }
}

