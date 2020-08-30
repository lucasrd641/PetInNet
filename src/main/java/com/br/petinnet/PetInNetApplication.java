package com.br.petinnet;

import com.br.petinnet.controller.DefaultController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.io.File;

@SpringBootApplication
public class PetInNetApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetInNetApplication.class, args);
    }
}
