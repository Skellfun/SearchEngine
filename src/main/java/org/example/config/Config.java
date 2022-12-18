package org.example.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.repository.FieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "configs")
public class Config {
    @Getter
    @Setter
    private String userAgent;
    @Getter
    @Setter
    private String webInterfacePath;
    @Getter
    @Setter
    private List<Site> sites;

    @Data
    public static class Site {
        @Getter
        @Setter
        private String url;
        @Setter
        @Getter
        private String name;
    }
}
