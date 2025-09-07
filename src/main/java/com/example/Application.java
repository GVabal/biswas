package com.example;

import io.micronaut.runtime.Micronaut;

import static io.micronaut.context.env.Environment.DEVELOPMENT;

public class Application {

    public static void main(String[] args) {
        Micronaut.build(args)
                .mainClass(Application.class)
                .banner(false)
                .deduceEnvironment(false)
                .deduceCloudEnvironment(false)
                .bootstrapEnvironment(true)
                .defaultEnvironments(DEVELOPMENT)
                .start();
    }
}