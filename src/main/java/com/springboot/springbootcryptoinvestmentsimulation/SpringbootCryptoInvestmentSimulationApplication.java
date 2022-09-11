package com.springboot.springbootcryptoinvestmentsimulation;

import java.io.*;

import com.springboot.springbootcryptoinvestmentsimulation.api.ApplicationInterface;
import com.springboot.springbootcryptoinvestmentsimulation.coins.*;
import com.springboot.springbootcryptoinvestmentsimulation.strategies.*;

import lombok.AllArgsConstructor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
@ComponentScan
public class SpringbootCryptoInvestmentSimulationApplication {
	private final ApplicationInterface applicationInterface;

	@Autowired
	public SpringbootCryptoInvestmentSimulationApplication(ApplicationInterface applicationInterface) {
		this.applicationInterface = applicationInterface;
	}

	public static void main(String[] args) throws IOException {
		SpringApplication.run(SpringbootCryptoInvestmentSimulationApplication.class, args);
	}

	public void run(String... args) throws Exception {
		coin Bitcoin = new coin("Bitcoin");
		coin Doge = new coin("Doge");
		coin OMGCoin = new coin("OMG Network");
		coin PAXGold = new coin("PAX Gold");

		strategy RSI = new strategy("RSI");
		strategy MACD = new strategy("MACD");
		strategy doubleEMA = new strategy("double-EMA");

		applicationInterface.addCoin(Bitcoin);
		applicationInterface.addCoin(Doge);
		applicationInterface.addCoin(OMGCoin);
		applicationInterface.addCoin(PAXGold);

		applicationInterface.addStrategy(RSI);
		applicationInterface.addStrategy(MACD);
		applicationInterface.addStrategy(doubleEMA);

	}

}
