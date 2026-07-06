package com.placementprep.repository;

import com.placementprep.entity.Bookmark;
import com.placementprep.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUser(User user);
    Optional<Bookmark> findByUserIdAndQuestionId(Long userId, Long questionId);
    void deleteByUserIdAndQuestionId(Long userId, Long questionId);
}
