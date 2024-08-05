package org.chon.appmockito.ejemplos.repositories;

import org.chon.appmockito.ejemplos.Datos;
import org.chon.appmockito.ejemplos.models.Exam;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExamRepositoryImpl implements ExamRepository {
    @Override
    public List<Exam> findAll() {
        try {
            System.out.println("ExamRepositoryImpl.findAll");

            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return Datos.EXAMS;
    }

    @Override
    public Exam save(Exam exam) {
        System.out.println("ExamRepositoryImpl.save");
        return Datos.EXAM;
    }
}
