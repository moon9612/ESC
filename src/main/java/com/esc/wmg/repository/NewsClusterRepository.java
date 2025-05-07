package com.esc.wmg.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.esc.wmg.entity.NewsClusterEntity;
import com.esc.wmg.entity.TblNewsClusterId;

@Repository
public interface NewsClusterRepository extends JpaRepository<NewsClusterEntity, TblNewsClusterId> {
    // 
}
