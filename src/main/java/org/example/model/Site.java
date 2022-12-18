package org.example.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode
@Entity
public class Site {

    @Setter
    @Getter
    @OneToMany(mappedBy = "site", cascade = CascadeType.REMOVE)
    List<Page> pages;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    @Getter
    private long id;
    @Setter
    @Getter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
    @Setter
    @Getter
    @Column(nullable = false)
    private Date statusTime;
    @Setter
    @Getter
    @Column(columnDefinition = "mediumtext")
    private String last_error;
    @Setter
    @Getter
    @Column(nullable = false, length = 50, unique = true)
    private String url;
    @Setter
    @Getter
    @Column(nullable = false)
    private String name;

    public Site(Status status, Date statusTime, String url, String name) {
        this.status = status;
        this.statusTime = statusTime;
        this.url = url;
        this.name = name;
    }

    private Site() {
    }

    public enum Status {
        INDEXING,
        INDEXED,
        FAILED
    }
}
