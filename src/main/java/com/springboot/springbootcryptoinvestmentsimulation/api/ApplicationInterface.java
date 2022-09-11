package com.springboot.springbootcryptoinvestmentsimulation.api;

import com.springboot.springbootcryptoinvestmentsimulation.coins.*;
import com.springboot.springbootcryptoinvestmentsimulation.strategies.*;

import lombok.AllArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@AllArgsConstructor
@Component
@ComponentScan
public class ApplicationInterface {
    private List<coin> coins;
    private List<strategy> strategies;

    public ApplicationInterface() {
        this.strategies = new ArrayList<strategy>();
        this.coins = new ArrayList<coin>();
    }

    public void addStrategy(strategy strategy) {
        strategies.add(strategy);
    }

    public void addCoin(coin coin) {
        coins.add(coin);
    }

    public List<strategy> listStrategies() {
        return strategies;
    }

    public List<coin> listCoins() {
        return coins;
    }

    public strategy findStrategyByID(String id) {
        for (strategy strategy : strategies) {
            if (strategy.getId() == id)
                return strategy;
        }
        throw new NoSuchElementException("Strategy not found");
    }

    public coin findCoinByID(String id) {
        for (coin coin : coins) {
            if (coin.getId() == id)
                return coin;
        }
        throw new NoSuchElementException("Component not found");
    }

}
