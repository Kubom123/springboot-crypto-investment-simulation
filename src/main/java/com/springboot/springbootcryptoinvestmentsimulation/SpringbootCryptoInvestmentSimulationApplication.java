package com.springboot.springbootcryptoinvestmentsimulation;

import java.io.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BarSeriesManager;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.analysis.criteria.pnl.GrossReturnCriterion;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringbootCryptoInvestmentSimulationApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(SpringbootCryptoInvestmentSimulationApplication.class, args);

		// Create the console object
		Console cnsl = System.console();
		boolean select = false;
		String coin = "";
		String strategy = "";
		LocalDate startDate = LocalDate.parse("2021-01-01");
		LocalDate endDate = LocalDate.parse("2021-12-01");

		if (cnsl == null) {
			System.out.println(
					"No console available");
			return;
		}

		// coin selection
		while (select == false) {
			System.out.println("Choose the coin you wish to simulate [Bitcoin, Doge, OMG Network, PAX Gold]:");

			// Read line
			coin = cnsl.readLine("Enter name: ");
			coin = coin.toLowerCase();

			switch (coin) {
				case "bitcoin":
					System.out.println("Bitcoin was selected \n");
					select = true;
					coin = "BTCUSDT.csv";
					break;

				case "doge":
					System.out.println("Doge was selected \n");
					select = true;
					coin = "DOGEUSDT.csv";
					break;

				case "omg network":
					System.out.println("OMG network was selected \n");
					select = true;
					coin = "OMGUSDT.csv";
					break;

				case "pax gold":
					System.out.println("PAX gold was selected \n");
					select = true;
					coin = "PAXGUSDT.csv";
					break;

				default:
					System.out.println("Invalid input, please repeat your choice \n");
			}
		}

		// strategy selection
		select = false;
		while (select == false) {
			System.out.println("Choose the strategy you wish to simulate [Double-EMA, RSI, MACD]:");

			strategy = cnsl.readLine("Enter name: ");
			strategy = strategy.toLowerCase();

			switch (strategy) {
				case "double-ema":
					System.out.println("Double-EMA was selected \n");
					select = true;
					break;

				case "rsi":
					System.out.println("RSI was selected \n");
					select = true;
					break;

				case "macd":
					System.out.println("MACD was selected \n");
					select = true;
					break;

				default:
					System.out.println("Invalid input, please repeat your choice \n");
			}
		}

		// time horizon selection
		// start date
		select = false;
		while (select == false) {
			System.out.println(
					"Choose the start date from which you wish to simulate [from  (2021-01-01)  to  (2021-12-01)]:");

			String inputDate = cnsl.readLine("Enter start date (yyyy-mm-dd): ");
			try {
				startDate = LocalDate.parse(inputDate);
			} catch (Exception e) {
				System.out.println("Invalid input, please repeat your choice \n");
				continue;
			}

			if (startDate.isAfter(LocalDate.parse("2020-12-31")) & startDate.isBefore(LocalDate.parse("2021-12-01"))) {
				select = true;
				System.out.println("Start date is: " + startDate + "\n");
			} else {
				System.out.println("Invalid input, please repeat your choice \n");
			}
		}

		// end date
		select = false;
		while (select == false) {
			System.out.println("Choose the end date from which you wish to simulate [from  (" + startDate
					+ ")  to  (2021-12-01)]:");

			String inputDate = cnsl.readLine("Enter end date (yyyy-mm-dd): ");
			try {
				endDate = LocalDate.parse(inputDate);
			} catch (Exception e) {
				System.out.println("Invalid input, please repeat your choice \n");
				continue;
			}

			if (endDate.isAfter(startDate) & endDate.isBefore(LocalDate.parse("2021-12-02"))) {
				select = true;
				System.out.println("End date is: " + endDate + "\n");
			} else {
				System.out.println("Invalid input, please repeat your choice \n");
			}
		}
		System.out.println("Calculating \n");

		long daysBetween1 = ChronoUnit.DAYS.between(LocalDate.parse("2021-01-01"), startDate);
		long daysBetween2 = ChronoUnit.DAYS.between(startDate, endDate);

		// formating data
		// historical data from old project have been reused

		BarSeries series = new BaseBarSeriesBuilder().build();
		try (BufferedReader br = new BufferedReader(new FileReader(coin))) {
			String line;
			int lineCount = 0;
			while ((line = br.readLine()) != null) {
				if ((lineCount > daysBetween1 * 1440) & (lineCount < (daysBetween1 +
						daysBetween2) * 1440)) {
					String[] values = line.split(",");
					series.addBar(ZonedDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(values[0])),
							ZoneId.systemDefault()), values[1], values[2],
							values[3], values[4], values[5]);
				}
				lineCount = lineCount + 1;
			}
		}
		ClosePriceIndicator closeSeries = new ClosePriceIndicator(series);
		BarSeriesManager seriesManager = new BarSeriesManager(series);

		// Simulation
		switch (strategy) {
			case "double-ema":
				EMAIndicator ema1 = new EMAIndicator(closeSeries, 20);
				EMAIndicator ema2 = new EMAIndicator(closeSeries, 100);
				CrossedDownIndicatorRule entryRule1 = new CrossedDownIndicatorRule(ema1, ema2);
				CrossedUpIndicatorRule exitRule1 = new CrossedUpIndicatorRule(ema2, ema2);
				TradingRecord tradingRecord1 = seriesManager.run(new BaseStrategy(entryRule1, exitRule1));
				System.out.println(
						"Gross return for this strategy in this configuration: "
								+ new GrossReturnCriterion().calculate(series, tradingRecord1));

				break;

			case "rsi":
				RSIIndicator rsi = new RSIIndicator(closeSeries, 20);
				CrossedDownIndicatorRule entryRule2 = new CrossedDownIndicatorRule(rsi, 20);
				CrossedUpIndicatorRule exitRule2 = new CrossedUpIndicatorRule(rsi, 80);
				TradingRecord tradingRecord2 = seriesManager.run(new BaseStrategy(entryRule2, exitRule2));
				System.out.println(
						"Gross return for this strategy in this configuration: "
								+ new GrossReturnCriterion().calculate(series, tradingRecord2));
				break;

			case "macd":
				MACDIndicator macd = new MACDIndicator(closeSeries, 5, 20);
				CrossedDownIndicatorRule entryRule3 = new CrossedDownIndicatorRule(macd.getShortTermEma(),
						macd.getLongTermEma());
				CrossedUpIndicatorRule exitRule3 = new CrossedUpIndicatorRule(macd.getShortTermEma(),
						macd.getLongTermEma());
				TradingRecord tradingRecord3 = seriesManager.run(new BaseStrategy(entryRule3, exitRule3));
				System.out.println(
						"Gross return for this strategy in this configuration: "
								+ new GrossReturnCriterion().calculate(series, tradingRecord3));
				break;

		}
	}
}
