package com.placementprep.repository;

import com.placementprep.entity.Result;
import com.placementprep.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findByUserOrderByDateDesc(User user);
    List<Result> findTop10ByUserOrderByDateDesc(User user);
    List<Result> findTop50ByOrderByScoreDesc();
}
