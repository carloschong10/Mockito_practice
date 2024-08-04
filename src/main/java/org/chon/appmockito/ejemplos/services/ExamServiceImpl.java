package org.chon.appmockito.ejemplos.services;

import org.chon.appmockito.ejemplos.models.Exam;
import org.chon.appmockito.ejemplos.repositories.ExamRepository;

import java.util.Optional;

public class ExamServiceImpl implements ExamService {

    private ExamRepository examRepository;

    public ExamServiceImpl(ExamRepository examRepository) {
        this.examRepository = examRepository;
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
}
