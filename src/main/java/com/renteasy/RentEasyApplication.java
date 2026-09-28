package com.renteasy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RentEasyApplication {

    public static void main(String[] args) {
        SpringApplication.run(RentEasyApplication.class, args);
        System.out.println("==================================================================");
        System.out.println("  RentEasy Backend & Web UI is running!");
        System.out.println("  Web UI Dashboard:    http://localhost:8080/");
        System.out.println("  Swagger API Docs:    http://localhost:8080/swagger-ui.html");
        System.out.println("  H2 Database Console: http://localhost:8080/h2-console");
        System.out.println("==================================================================");
    }
}
