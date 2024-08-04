package org.chon.appmockito.ejemplos.repositories;

import org.chon.appmockito.ejemplos.models.Exam;

import java.util.List;

public interface ExamRepository {
    List<Exam> findAll(); //crearemos un mock de este objeto sin importar su implementacion
}
