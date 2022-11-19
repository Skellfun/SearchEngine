package org.example.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@EqualsAndHashCode
@Entity
@Setter
@Getter
@Table(name = "indexes", uniqueConstraints = {@UniqueConstraint(columnNames = {"page_id", "lemma_id"})})
public class Indx {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "page_id", nullable = false)
    private long pageId;

    @Column(name = "lemma_id", nullable = false)
    private long lemmaId;

    private float rang;

    public Indx() {
    }

    public Indx(long pageId, long lemmaId, float rang) {
        this.pageId = pageId;
        this.lemmaId = lemmaId;
        this.rang = rang;
    }
}
