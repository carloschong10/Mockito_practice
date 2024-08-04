package org.chon.appmockito.ejemplos.services;

import org.chon.appmockito.ejemplos.models.Exam;

import java.util.Optional;

public interface ExamService {
    //    Exam findByName(String name);
    Optional<Exam> findByName(String name);
    Exam findExamWithQuestionsByName(String name);
    Exam save(Exam exam);
}
