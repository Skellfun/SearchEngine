package org.example.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@EqualsAndHashCode
@Entity
public class Field {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Setter
    @Getter
    private long id;

    @Setter
    @Getter
    @Column(nullable = false)
    private String name;

    @Setter
    @Getter
    @Column(nullable = false)
    private String selector;

    @Setter
    @Getter
    @Column(nullable = false)
    private float weight;

    protected Field() {
    }

    public Field(String name, String selector, float weight) {
        this.name = name;
        this.selector = selector;
        this.weight = weight;
    }
}
