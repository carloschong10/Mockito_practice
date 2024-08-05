package org.chon.appmockito.ejemplos.services;

import org.chon.appmockito.ejemplos.Datos;
import org.chon.appmockito.ejemplos.models.Exam;
import org.chon.appmockito.ejemplos.repositories.ExamRepository;
import org.chon.appmockito.ejemplos.repositories.ExamRepositoryImpl;
import org.chon.appmockito.ejemplos.repositories.QuestionRepository;
import org.chon.appmockito.ejemplos.repositories.QuestionRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExamServiceImplTest {

    /*
    @Mock
    ExamRepository repository;
    @Mock
    QuestionRepository questionRepository;
    */

    @Mock
    ExamRepositoryImpl repository;
    @Mock
    QuestionRepositoryImpl questionRepository;

    @Captor
    ArgumentCaptor<Long> captor;

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

    @Test
    void testArgumentCaptor() {
        when(repository.findAll()).thenReturn(Datos.EXAMS);

        service.findExamWithQuestionsByName("Matematicas");

        //instanciamos ArgumentCaptor:
//        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class); //lo comentamos para probar con la anotacion @Captor

        verify(questionRepository).findQuestionsByExamId(captor.capture()); //capturamos el argumento

        assertEquals(1L, captor.getValue());
//        assertEquals(2L, captor.getValue());
    }

    @Test
    void testDoThrow() {

        Exam exam = Datos.EXAM;
        exam.setQuestions(Datos.QUESTIONS); //tenemos que agregarle las preguntas porque sino el método doThrow no se estaria invocando, ya que en el método save está el sgte if( if(!exam.getQuestions().isEmpty()){} ) que valida si es que están vacias no entre y como tal nunca se ejecutaria el metodo saveQuestions()
        //y si comentamos la linea exam.setQuestions(Datos.QUESTIONS); nos mostrará el mensaje de error(Se esperaba la esxcepcion IllegalArgumentException pero no se devolvió nada) ya que no entra al metodo saveQuestions()

        //Cuando el método es void y queremos manejar o lanzar una excepcion, se usa el doThrow: que significa hacer algo(lanzamos la excepcion) cuando se invoca a un método
        doThrow(IllegalArgumentException.class).when(questionRepository).saveQuestions(anyList()); //lanzamos la excepcion o hacemos algo cuando del objeto mock questionRepository invoca al método void saveQuestions()

        assertThrows(IllegalArgumentException.class, () -> {
            service.save(exam);
        });
    }

    @Test
    void testDoAnswer() {
        when(repository.findAll()).thenReturn(Datos.EXAMS);
//        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Datos.QUESTIONS);

        //¿Que pasa si tengo algun tipo de evento de poder capturar ese parametor( anyLong() )?, lo obtenemos mediante doAnswer muy similar al metodo testSaveExam()
        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return id == 2L ? Datos.QUESTIONS : Collections.emptyList();
        }).when(questionRepository).findQuestionsByExamId(anyLong());

        Exam exam = service.findExamWithQuestionsByName("Ciencias");

        assertEquals(8, exam.getQuestions().size());
        assertEquals(2L, exam.getId());
        assertEquals("Ciencias", exam.getName());

        verify(questionRepository).findQuestionsByExamId(anyLong());
    }

    @Test
    void testDoAnswerSaveExam() {
        //BDD:
        //Given: Dado un entorno de prueba
        Exam newExam = Datos.EXAM;
        newExam.setQuestions(Datos.QUESTIONS);

        doAnswer(new Answer<Exam>() {
            Long secuence = 5L;

            @Override
            public Exam answer(InvocationOnMock invocation) throws Throwable {
                Exam exam = invocation.getArgument(0); //invocation es el examen que le estoy enviando al repository a través de save(any(Exam.class))
                exam.setId(secuence++);
                return exam;
            }
        }).when(repository).save(any(Exam.class));

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
    void testDoCallRealMethod() { //Usamos doCallRealMethod para la llamada real a un método mock, esto solo se puede hacer con las implementaciones reales
        when(repository.findAll()).thenReturn(Datos.EXAMS);
//        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Datos.QUESTIONS);
        doCallRealMethod().when(questionRepository).findQuestionsByExamId(anyLong()); //de esta forma se invoca al metodo real

        Exam exam = service.findExamWithQuestionsByName("Computacion");

        assertEquals(4L, exam.getId());
        assertEquals("Computacion", exam.getName());
    }

    @Test
    void testSpy() {
        /*los espias son un hibrido entre un objeto real y un mock, nos permite invocar sin definir ningun when(ningun simulacro), no tenemos que mockear ningun metodo de
        este spy ya que no es necesario, simplemente cuando invocamos a un metodo llamará al método real.
        El mock es 100% simulado por lo tanto todos sus metodos los tenemos que mockear con el when o doAlgo(doThrow, doAnswer, doCallRealMethod); a diferencia del
        el spy no aqui solo vamos a simular con el when o doAlgo lo que queramos simular, pero el reseto será la llamada real en los demás metodos que definamos esa simulacion,
        además el spy se crea a partir de una clase concreta y no de una clase abstracta o interfaz, porque va a llamar métodos reales; y si estamos usando una interfaz ese método real no
        está implementado, por lo tanto va a fallar la prueba.
        */

        ExamRepository examRepository = spy(ExamRepositoryImpl.class);
        QuestionRepository questionRepository1 = spy(QuestionRepositoryImpl.class);
        ExamService examService = new ExamServiceImpl(examRepository, questionRepository1);

        List<String> preguntas = Arrays.asList("¿Qué significa SOLID?");

//        when(questionRepository1.findQuestionsByExamId(anyLong())).thenReturn(Datos.QUESTIONS); //aqui hacemos una invocacion falsa al metodo findQuestionsByExamId, pero si usamos when se va a imprimir QuestionRepositoryImpl.findQuestionsByExamId, cuando no deberia imrimirse, asi que haceos uso de doReturn().when()
        doReturn(preguntas).when(questionRepository1).findQuestionsByExamId(anyLong()); //y con esto ya se imprimiria nomas ExamRepositoryImpl.findAll

        Exam exam = examService.findExamWithQuestionsByName("Computacion"); //aqui estamos llamando directamente al metodo con el service(llamada real al metodo findAll()), en los spy no usamos when

        assertEquals(4, exam.getId());
        assertEquals("Computacion", exam.getName());
        assertEquals(1, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("¿Qué significa SOLID?"));

        verify(examRepository).findAll();   //va a verificar y realmente se está llamando de forma real
        verify(questionRepository1).findQuestionsByExamId(anyLong());   //se está llamando pero el simulado (mock)
    }

    @Test
    void testOrdenDeInvocaciones() {
        when(repository.findAll()).thenReturn(Datos.EXAMS);

        service.findExamWithQuestionsByName("Ciencias");
        service.findExamWithQuestionsByName("Fisica");

        InOrder inOrder = inOrder(questionRepository);
        inOrder.verify(questionRepository).findQuestionsByExamId(2L);
        inOrder.verify(questionRepository).findQuestionsByExamId(5L);
    }

    @Test
    void testOrdenDeInvocaciones2() {
        when(repository.findAll()).thenReturn(Datos.EXAMS);

        service.findExamWithQuestionsByName("Ciencias");
        service.findExamWithQuestionsByName("Fisica");

        InOrder inOrder = inOrder(repository, questionRepository); //agregamos un mock mas que es repository

        //los colocamos en orden respectivo porque si colocamos el metodo inOrder.verify(repository).findAll(); seguido se caerá ya que en un mock no se puede llamar 2 veces si uno uno en cada mock
        inOrder.verify(repository).findAll();
        inOrder.verify(questionRepository).findQuestionsByExamId(2L);

        inOrder.verify(repository).findAll();
        inOrder.verify(questionRepository).findQuestionsByExamId(5L);
    }

    @Test
    void testNumeroDeInvocaciones() {
        when(repository.findAll()).thenReturn(Datos.EXAMS);
        service.findExamWithQuestionsByName("Computacion");

        verify(questionRepository).findQuestionsByExamId(4L); //acá por defecto el times es 1
        verify(questionRepository, times(1)).findQuestionsByExamId(4L);
        verify(questionRepository, atLeast(1)).findQuestionsByExamId(4L);
        verify(questionRepository, atLeastOnce()).findQuestionsByExamId(4L);
        verify(questionRepository, atMost(20)).findQuestionsByExamId(4L);
        verify(questionRepository, atMostOnce()).findQuestionsByExamId(4L);
    }

    @Test
    void testNumeroDeInvocaciones2() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        service.findExamWithQuestionsByName("Computacion");

        //Verificar si es que nunca se ha llamado a questionRepository
        verify(questionRepository, never()).findQuestionsByExamId(4L);
        verifyNoInteractions(questionRepository);

        //Verificar si es que se ha llamado a repository
        verify(repository).findAll(); //acá por defecto el times es 1
        verify(repository, times(1)).findAll();
        verify(repository, atLeast(1)).findAll();
        verify(repository, atLeastOnce()).findAll();
        verify(repository, atMost(10)).findAll();
        verify(repository, atMostOnce()).findAll();
    }
}