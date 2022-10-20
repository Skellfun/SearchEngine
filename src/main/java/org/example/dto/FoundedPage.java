package org.example.dto;

import lombok.Getter;
import lombok.Setter;

public class FoundedPage {

    @Getter
    @Setter
    private String site;

    @Setter
    @Getter
    private String siteName;

    @Setter
    @Getter
    private String uri;

    @Setter
    @Getter
    private String title;

    @Setter
    @Getter
    private String snippet;

    @Setter
    @Getter
    private float relevance;

    public FoundedPage(String site, String siteName, String uri, String title, String snippet, float relevance) {
        this.site = site;
        this.siteName = siteName;
        this.uri = uri;
        this.title = title;
        this.snippet = snippet;
        this.relevance = relevance;
    }
}