package com.esc.wmg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.esc.wmg.entity.IssueStatisticsEntity;

@Repository
public interface IssueStatisticsRepository extends JpaRepository<IssueStatisticsEntity, String> {

}
