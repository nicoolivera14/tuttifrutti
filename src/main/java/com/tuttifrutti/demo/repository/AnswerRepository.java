package com.tuttifrutti.demo.repository;

import com.tuttifrutti.demo.domain.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
