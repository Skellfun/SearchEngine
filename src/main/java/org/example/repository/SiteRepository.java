package org.example.repository;

import org.example.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface SiteRepository extends JpaRepository<Site, Long> {
    Optional<Site> findByUrl(String url);
    void deleteByUrl(String url);
}
