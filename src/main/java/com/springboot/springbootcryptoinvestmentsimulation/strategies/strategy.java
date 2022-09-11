package com.springboot.springbootcryptoinvestmentsimulation.strategies;

import lombok.Data;

@Data
public class strategy {
    protected String id;

    public strategy(String id) {
        this.id = id;
    }
}