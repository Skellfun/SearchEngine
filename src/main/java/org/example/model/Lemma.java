package org.example.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Setter
@Getter
@ToString
@Table(name = "lemma", indexes = @Index(name = "l_index", columnList = "lemma"))
public class Lemma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 50, unique = true)
    private String lemma;

    @Column(nullable = false)
    private int frequency;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "lemmas")
    private List<Page> pages;

    public Lemma() {
    }

    public Lemma(String lemma, int frequency) {
        this.lemma = lemma;
        this.frequency = frequency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lemma that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 1_000 + lemma.hashCode();
    }
}
