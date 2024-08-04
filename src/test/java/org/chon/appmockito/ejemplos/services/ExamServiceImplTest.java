package org.chon.appmockito.ejemplos.services;

import org.chon.appmockito.ejemplos.models.Exam;
import org.chon.appmockito.ejemplos.repositories.ExamRepository;
import org.chon.appmockito.ejemplos.repositories.ExamRepositoryImpl;
import org.chon.appmockito.ejemplos.repositories.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ExamServiceImplTest {

    ExamRepository repository;
    QuestionRepository questionRepository;
    ExamService service;

    @BeforeEach
    void setUp() {
        repository = mock(ExamRepository.class);
        questionRepository = mock(QuestionRepository.class);    //aqui ponemos la interfaz pero tambien podria ser una clase que implemente a una interfaz con una implementacion cualquiera
        service = new ExamServiceImpl(repository, questionRepository);
    }

    @Test
    @DisplayName("Test con el contexto de una lista con data buscando por el nombre")
    void findByName() {
//        ExamRepository repository = new ExamRepositoryImpl();
        /*
        ExamRepository repository = mock(ExamRepository.class); //creará un mock de la clase ExamRepository
        ExamService service = new ExamServiceImpl(repository);
        */

        //en este caso el contexto es una lista con data
        when(repository.findAll()).thenReturn(Datos.EXAMS); //cuando se llame al método findAll del mock ExamRepository entonces retorname mi variable datos que he creado acá

//        Exam exam = service.findByName("Matematicas");
        /*
        assertNotNull(exam);
        assertEquals(1L, exam.getId());
        assertEquals("Matematicas", exam.getName());
        */

        Optional<Exam> exam = service.findByName("Matematicas");

        assertTrue(exam.isPresent());
        assertEquals(1L, exam.orElseThrow().getId());
        assertEquals("Matematicas", exam.orElseThrow().getName());
    }

    @Test
    @DisplayName("Test con el contexto de una lista vacia")
    void findByNameEmptyList() {
        /*
        ExamRepository repository = mock(ExamRepository.class);
        ExamService service = new ExamServiceImpl(repository);
        */
        List<Exam> datos = Collections.emptyList();

        when(repository.findAll()).thenReturn(datos);

        Optional<Exam> exam = service.findByName("Matematicas");

        assertFalse(exam.isPresent());
    }

    @Test
    void testExamQuestionsByName() {
        when(repository.findAll()).thenReturn(Datos.EXAMS);
        when(questionRepository.findQuestionsByExamId(4L)).thenReturn(Datos.QUESTIONS); //cuando el resultado de la inovacion a este metodo coincida con lo que se envia en el argumento de findExamWithQuestionsByName, se conoce como un Match Arguments; si no coincide simplemente devolverá una lista vacia
//        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Datos.QUESTIONS); //anyLong() quire decir que se aplique para cualquier id que tenga el valor de Computacion

//        Exam exam = service.findExamWithQuestionsByName("Matematicas");
        Exam exam = service.findExamWithQuestionsByName("Computacion");

        assertEquals(8, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("¿Quién es el padre de la programación?"));
    }
}