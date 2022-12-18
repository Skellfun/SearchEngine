package org.example.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"path", "site_id"})})
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    @Getter
    private long id;

    @Setter
    @Getter
    @Column(nullable = false, length = 200)
    private String path;

    @Setter
    @Getter
    @Column(nullable = false)
    private int code;

    @Setter
    @Getter
    @Column(nullable = false, columnDefinition = "mediumtext")
    private String content;

    @Setter
    @Getter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Getter
    @Setter
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(name = "Indexes",
            joinColumns = {@JoinColumn(name = "page_id")},
            inverseJoinColumns = {@JoinColumn(name = "lemma_id")}
    )
    private List<Lemma> lemmas;

    public Page(String path, int code, String content, Site site) {
        this.path = path;
        this.code = code;
        this.content = content;
        this.site = site;
    }

    protected Page() {
    }
}
