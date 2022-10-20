package org.example.repository;

import org.example.model.Lemma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LemmaRepository extends JpaRepository<Lemma, Integer> {

    Optional<Lemma> findByLemma(String lemma);

}
