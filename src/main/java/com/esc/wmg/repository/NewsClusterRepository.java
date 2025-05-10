package com.esc.wmg.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.esc.wmg.entity.NewsClusterEntity;
import com.esc.wmg.entity.TblNewsClusterId;

@Repository
public interface NewsClusterRepository extends JpaRepository<NewsClusterEntity, TblNewsClusterId> {
    List<NewsClusterEntity> findAllByOrderBySimilarityDesc();

    // Add method to find news clusters by date and order by similarity descending
    // List<NewsClusterEntity> findByDateOrderBySimilarityDesc(LocalDate date);
    List<NewsClusterEntity> findByDateBetweenOrderBySimilarityDesc(LocalDateTime start, LocalDateTime end);
}
