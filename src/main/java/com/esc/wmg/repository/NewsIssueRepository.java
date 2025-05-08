package com.esc.wmg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.esc.wmg.entity.NewsIssueEntity;

@Repository
public interface NewsIssueRepository extends JpaRepository<NewsIssueEntity, Long> {

    // NewsRepository.java (최근 10개 데이터 조회)
    // @Query("SELECT n FROM NewsIssueEntity n ORDER BY n.date DESC, n.rnk ASC")
    List<NewsIssueEntity> findTop10ByOrderByDateDescRnkAsc();

}
