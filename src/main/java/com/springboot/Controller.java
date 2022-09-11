package com.springboot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
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
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;

import com.springboot.springbootcryptoinvestmentsimulation.api.*;

import lombok.AllArgsConstructor;;

@RestController
@ComponentScan
public class Controller implements CommandLineRunner {
    @Autowired
    ApplicationInterface applicationInterface;

    public Controller(ApplicationInterface applicationInterface) {
        this.applicationInterface = applicationInterface;
    }

    @GetMapping("/")
    @ResponseBody
    public String allVehicles(Model model) {
        return "index";
    }

    @GetMapping("/cryptoasset/{assetId}/simulate-strategy/{strategyId}?dateFrom=YYYY-MM-DD&dateTo=YYYY-MM-DD")
    public String SimulateStrategy(@PathVariable String id1, @PathVariable String id2)
            throws NumberFormatException, IOException {
        String coin = "";
        switch (id1) {
            case "Bitcoin":
                coin = "BTCUSDT.csv";
                break;

            case "Doge":
                coin = "DOGEUSDT.csv";
                break;

            case "PAX Gold":
                coin = "PAXGUSDT.csv";
                break;

            case "OMG Network":
                coin = "OMGUSDT.csv";
                break;
        }
        BarSeries series = new BaseBarSeriesBuilder().build();
        try (BufferedReader br = new BufferedReader(new FileReader(coin))) {
            String line;
            int lineCount = 0;
            while ((line = br.readLine()) != null) {

                String[] values = line.split(",");
                series.addBar(ZonedDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(values[0])),
                        ZoneId.systemDefault()), values[1], values[2],
                        values[3], values[4], values[5]);

                lineCount = lineCount + 1;
            }
        }
        ClosePriceIndicator closeSeries = new ClosePriceIndicator(series);
        BarSeriesManager seriesManager = new BarSeriesManager(series);

        switch (id2) {
            case "RSI":
                RSIIndicator rsi = new RSIIndicator(closeSeries, 20);
                CrossedDownIndicatorRule entryRule2 = new CrossedDownIndicatorRule(rsi, 20);
                CrossedUpIndicatorRule exitRule2 = new CrossedUpIndicatorRule(rsi, 80);
                TradingRecord tradingRecord2 = seriesManager.run(new BaseStrategy(entryRule2, exitRule2));
                Num num2 = new GrossReturnCriterion().calculate(series, tradingRecord2);
                break;

            case "MACD":
                MACDIndicator macd = new MACDIndicator(closeSeries, 5, 20);
                CrossedDownIndicatorRule entryRule3 = new CrossedDownIndicatorRule(macd.getShortTermEma(),
                        macd.getLongTermEma());
                CrossedUpIndicatorRule exitRule3 = new CrossedUpIndicatorRule(macd.getShortTermEma(),
                        macd.getLongTermEma());
                TradingRecord tradingRecord3 = seriesManager.run(new BaseStrategy(entryRule3, exitRule3));
                Num num3 = new GrossReturnCriterion().calculate(series, tradingRecord3);

                break;

            case "double-EMA":
                EMAIndicator ema1 = new EMAIndicator(closeSeries, 20);
                EMAIndicator ema2 = new EMAIndicator(closeSeries, 100);
                CrossedDownIndicatorRule entryRule1 = new CrossedDownIndicatorRule(ema1, ema2);
                CrossedUpIndicatorRule exitRule1 = new CrossedUpIndicatorRule(ema2, ema2);
                TradingRecord tradingRecord1 = seriesManager.run(new BaseStrategy(entryRule1, exitRule1));
                Num num1 = new GrossReturnCriterion().calculate(series, tradingRecord1);

                break;

        }
        return "result";
    }

    @Override
    public void run(String... args) throws Exception {
        // TODO Auto-generated method stub

    }

}
