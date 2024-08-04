package org.chon.appmockito.ejemplos.repositories;

import org.chon.appmockito.ejemplos.models.Exam;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ExamRepositoryImpl implements ExamRepository {
    @Override
    public List<Exam> findAll() {
        System.out.println("ExamRepositoryImpl nunca será invocado porque el mock llama desde el service a ExamRepository");

        return Collections.emptyList(); //son muchos contextos que se pueden dar como que esta lista sea null o que tenga datos, etc y para eso usamos mockito
        /*return Arrays.asList(
                new Exam(1L, "Matematicas"),
                new Exam(2L, "Ciencias"),
                new Exam(3L, "Comunicacion"),
                new Exam(4L, "Computacion"),
                new Exam(5L, "Fisica")
        );*/
    }
}
