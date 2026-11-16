package com.fluxo.fluxoapp;

import org.springframework.boot.SpringApplication;

public class TestFluxoappApplication {

	public static void main(String[] args) {
		SpringApplication.from(FluxoappApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
