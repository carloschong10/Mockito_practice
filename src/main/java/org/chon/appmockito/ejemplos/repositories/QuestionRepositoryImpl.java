package org.chon.appmockito.ejemplos.repositories;

import org.chon.appmockito.ejemplos.Datos;

import java.util.List;

public class QuestionRepositoryImpl implements QuestionRepository{
    @Override
    public List<String> findQuestionsByExamId(Long id) {
        System.out.println("QuestionRepositoryImpl.findQuestionsByExamId");

        return Datos.QUESTIONS;
    }

    @Override
    public void saveQuestions(List<String> questions) {
        System.out.println("QuestionRepositoryImpl.saveQuestions");
    }
}
