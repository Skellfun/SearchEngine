package org.example.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class SearchResult implements Result {

    private final boolean result = true;

    @Setter
    @Getter
    private int count;

    @Setter
    @Getter
    private List<FoundedPage> data = new ArrayList<>();

    @Override
    public boolean getResult() {
        return result;
    }
}
