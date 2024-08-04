package org.chon.appmockito.ejemplos.services;

import org.chon.appmockito.ejemplos.models.Exam;
import org.chon.appmockito.ejemplos.repositories.ExamRepository;
import org.chon.appmockito.ejemplos.repositories.QuestionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;

    public ExamServiceImpl(ExamRepository examRepository, QuestionRepository questionRepository) {
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
    }

    /*
    @Override
    public Exam findByName(String name) {
        Optional<Exam> examOptional = examRepository.findAll()
                .stream()
                .filter(e -> e.getName().contains(name))
                .findFirst();

        Exam exam = null;

        if (examOptional.isPresent()) {
            exam = examOptional.orElseThrow();
        }

        return exam;
    }
    */

    @Override
    public Optional<Exam> findByName(String name) {
        return examRepository.findAll()
                .stream()
                .filter(e -> e.getName().contains(name))
                .findFirst();
    }

    @Override
    public Exam findExamWithQuestionsByName(String name) {
        Optional<Exam> optionalExam = this.findByName(name);

        Exam exam = null;

        if (optionalExam.isPresent()) {
            exam = optionalExam.orElseThrow();
            List<String> listQuestions = questionRepository.findQuestionsByExamId(exam.getId());
            exam.setQuestions(listQuestions);
        }

        return exam;
    }
}
