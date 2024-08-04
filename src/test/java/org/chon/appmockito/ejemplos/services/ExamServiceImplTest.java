package org.chon.appmockito.ejemplos.services;

import org.chon.appmockito.ejemplos.models.Exam;
import org.chon.appmockito.ejemplos.repositories.ExamRepository;
import org.chon.appmockito.ejemplos.repositories.ExamRepositoryImpl;
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
    ExamService service;

    @BeforeEach
    void setUp() {
        repository = mock(ExamRepository.class);
        service = new ExamServiceImpl(repository);
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
        List<Exam> datos = Arrays.asList(
                new Exam(1L, "Matematicas"),
                new Exam(2L, "Ciencias"),
                new Exam(3L, "Comunicacion"),
                new Exam(4L, "Computacion"),
                new Exam(5L, "Fisica")
        );

        when(repository.findAll()).thenReturn(datos); //cuando se llame al método findAll del mock ExamRepository entonces retorname mi variable datos que he creado acá

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
}