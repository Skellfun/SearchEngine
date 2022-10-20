package org.example.repository;

import org.example.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PageRepository extends JpaRepository<Page, Integer> {

    Optional<Page> findByPathAndSiteId(String path, long siteId);

    List<Page> findBySiteId(long siteId);

    Optional<Page> findByPath(String path);
}
