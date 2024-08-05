package org.chon.appmockito.ejemplos.services;

import org.chon.appmockito.ejemplos.models.Exam;
import org.chon.appmockito.ejemplos.repositories.ExamRepository;
import org.chon.appmockito.ejemplos.repositories.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExamServiceImplTest {

    @Mock
    ExamRepository repository;
    @Mock
    QuestionRepository questionRepository;

    @InjectMocks //Inyectamos los @Mocks en la implementacion
    ExamServiceImpl service;

    @BeforeEach
    void setUp() {
//        repository = mock(ExamRepository.class);
//        questionRepository = mock(QuestionRepository.class);    //aqui ponemos la interfaz pero tambien podria ser una clase que implemente a una interfaz con una implementacion cualquiera
//        service = new ExamServiceImpl(repository, questionRepository);

//        MockitoAnnotations.openMocks(this); //para que se pueda inyectar usamos MockitoAnnotations; y otra forma de inyectar es con @ExtendWith(MockitoExtension.class)
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

    @Test
    void testExamDifferentArgumentsVerify() {
        when(repository.findAll()).thenReturn(Datos.EXAMS);
        when(questionRepository.findQuestionsByExamId(4L)).thenReturn(Datos.QUESTIONS);

        Exam exam = service.findExamWithQuestionsByName("Computacion");

        assertNotNull(exam);

        verify(repository).findAll(); //verificamos que del objeto Mock "repository" se invoca el findAll(), y si es que no se llama va a fallar
        verify(questionRepository).findQuestionsByExamId(7L); //le pasamos un argumento distinto a 4L y verificamos que del objeto Mock "questionRepository" se invoca el findQuestionsByExamId(4L), y si es que no se llama va a fallar
    }

    @Test
    void testExamNotExistsVerify() {
        when(repository.findAll()).thenReturn(Datos.EXAMS);
        when(questionRepository.findQuestionsByExamId(4L)).thenReturn(Datos.QUESTIONS);

        Exam exam = service.findExamWithQuestionsByName("ComputacionXXX");

        assertNull(exam);

        verify(repository).findAll(); //verificamos que del objeto Mock "repository" se invoca el findAll(), y si es que no se llama va a fallar
        verify(questionRepository).findQuestionsByExamId(5L); //verificamos que del objeto Mock "questionRepository" se invoca el findQuestionsByExamId(4L), y si es que no se llama va a fallar
    }

    @Test
    void testSaveExam() {
        //BDD:
        //Given: Dado un entorno de prueba
        Exam newExam = Datos.EXAM;
        newExam.setQuestions(Datos.QUESTIONS);

//        when(repository.save(any(Exam.class))).thenReturn(Datos.EXAM); //cuando se ejecute el metodo save de mi repository, entonces retorname los datos del Examen
        when(repository.save(any(Exam.class))).then(new Answer<Exam>() {
            Long secuence = 5L;

            @Override
            public Exam answer(InvocationOnMock invocation) throws Throwable {
                Exam exam = invocation.getArgument(0); //invocation es el examen que le estoy enviando al repository a través de save(any(Exam.class))
                exam.setId(secuence++);
                return exam;
            }
        }); //en vez de devolver el mismo examen que estamos guardando, devolvemos un examen modificado del que estamos guardando

        //When: Cuando ejecutamos el método que queremos probar
        Exam exam = service.save(newExam);

        //Then: Entonces validamos
        assertNotNull(exam.getId());
        assertEquals(5L, exam.getId());
        assertEquals("EPT", exam.getName());

        verify(repository).save(any(Exam.class)); //verificamos que se llame al metodo save()
        verify(questionRepository).saveQuestions(anyList()); //verificamos que se llame al metodo saveQuestions()
    }

    @Test
    void testManejoException() {
        when(repository.findAll()).thenReturn(Datos.EXAMS);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenThrow(IllegalArgumentException.class);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.findExamWithQuestionsByName("Matematicas");
        });

        assertEquals(IllegalArgumentException.class, exception.getClass());
//        assertEquals(RuntimeException.class, exception.getClass());

        verify(repository).findAll();
        verify(questionRepository).findQuestionsByExamId(anyLong());
    }

    @Test
    void testManejoExceptionWithNull() {
        when(repository.findAll()).thenReturn(Datos.EXAMS_WITH_ID_NULL);
//        when(questionRepository.findQuestionsByExamId(null)).thenThrow(IllegalArgumentException.class);
        when(questionRepository.findQuestionsByExamId(isNull())).thenThrow(IllegalArgumentException.class); //isNull() es un metodo de ArgumentMatchers y seria mejor poner esto que directamente null

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.findExamWithQuestionsByName("Matematicas");
        });

        assertEquals(IllegalArgumentException.class, exception.getClass());
//        assertEquals(RuntimeException.class, exception.getClass());

        verify(repository).findAll();
//        verify(questionRepository).findQuestionsByExamId(null);
        verify(questionRepository).findQuestionsByExamId(isNull()); //isNull() es un metodo de ArgumentMatchers y seria mejor poner esto que directamente null
    }

    /*
    Argument Matches es una caracteristica de mockito que te permite saber
    si coincide el valor real que se pasa por argumento en un método (ejm. en el service)
    y lo comparamos con los definidos en el mock (ejm. en el when o verify)
    */
    @Test
    void testArgumentsMatchers() {
        when(repository.findAll()).thenReturn(Datos.EXAMS);
//        when(repository.findAll()).thenReturn(Datos.EXAMS_WITH_ID_NULL);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Datos.QUESTIONS);

        service.findExamWithQuestionsByName("Matematicas");

        verify(repository).findAll();
//        verify(questionRepository).findQuestionsByExamId(1L);
//        verify(questionRepository).findQuestionsByExamId(Mockito.argThat(argument -> argument != null && argument.equals(1L)));
        verify(questionRepository).findQuestionsByExamId(Mockito.argThat(argument -> argument != null && argument >= 1L));
    }

    @Test
    void testArgumentsMatchers2() {
//        when(repository.findAll()).thenReturn(Datos.EXAMS);
        when(repository.findAll()).thenReturn(Datos.EXAMS_NEGATIVES);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Datos.QUESTIONS);

        service.findExamWithQuestionsByName("Matematicas");

        verify(repository).findAll();
        verify(questionRepository).findQuestionsByExamId(Mockito.argThat(new MiArgsMatchers()));
    }

    @Test
    void testArgumentsMatchers3() {
//        when(repository.findAll()).thenReturn(Datos.EXAMS);
        when(repository.findAll()).thenReturn(Datos.EXAMS_NEGATIVES);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Datos.QUESTIONS);

        service.findExamWithQuestionsByName("Matematicas");

        verify(repository).findAll();
        verify(questionRepository).findQuestionsByExamId(Mockito.argThat(argument -> argument != null && argument > 0));
    }

    public static class MiArgsMatchers implements ArgumentMatcher<Long> {

        private Long argument;

        @Override
        public boolean matches(Long argument) {
            this.argument = argument;
            return argument != null && argument > 0;
        }

        @Override
        public String toString() {
            return "es para un mensaje personalizado de error" +
                    " en caso de que falle el test, " + argument +
                    " debe ser un entero positivo";
        }
    }

}