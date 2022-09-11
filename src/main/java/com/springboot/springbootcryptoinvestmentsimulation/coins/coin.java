package com.springboot.springbootcryptoinvestmentsimulation.coins;

import lombok.Data;

@Data
public class coin {

    protected String id;

    public coin(String id) {
        this.id = id;
    }
}
