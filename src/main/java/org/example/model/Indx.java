package org.example.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@EqualsAndHashCode
@Entity
@Table(name = "indexes", uniqueConstraints = {@UniqueConstraint(columnNames = {"page_id", "lemma_id"})})
public class Indx {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    @Getter
    private long id;

    @Setter
    @Getter
    @Column(name = "page_id", nullable = false)
    private long pageId;

    @Setter
    @Getter
    @Column(name = "lemma_id", nullable = false)
    private long lemmaId;

    @Setter
    @Getter
    private float rang;

    protected Indx() {
    }

    public Indx(long pageId, long lemmaId, float rang) {
        this.pageId = pageId;
        this.lemmaId = lemmaId;
        this.rang = rang;
    }
}
