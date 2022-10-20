package org.example.dto;

import lombok.Getter;

public class NegativeResult implements Result {

    private final boolean result = false;
    @Getter
    private final String error;

    public NegativeResult(String error) {
        this.error = error;
    }

    public boolean getResult() {
        return result;
    }
}
