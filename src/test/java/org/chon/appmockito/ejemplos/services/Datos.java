package org.chon.appmockito.ejemplos.services;

import org.chon.appmockito.ejemplos.models.Exam;

import java.util.Arrays;
import java.util.List;

public class Datos {
    public final static List<Exam> EXAMS = Arrays.asList(
            new Exam(1L, "Matematicas"),
            new Exam(2L, "Ciencias"),
            new Exam(3L, "Comunicacion"),
            new Exam(4L, "Computacion"),
            new Exam(5L, "Fisica")
    );

    public final static List<String> QUESTIONS = Arrays.asList("¿Qué son las interfaces?", "¿Cuánto es 1+1?", "¿Cuánto es 5*2?", "¿Qué son los derechos humanos?",
            "¿Quién es el padre de la programación?", "¿Cómo se produjo el BigBang", "Cuál es la fórmula del MRU y MRUV", "¿Qué significa SOLID?");
}
