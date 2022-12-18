package org.example.repository;

import org.example.model.Indx;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface IndxRepository extends JpaRepository<Indx, Integer> {
    Indx findByPageIdAndLemmaId(long pageId, long lemmaId);
}
