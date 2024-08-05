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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamServiceImplSpyTest {

    @Spy
    ExamRepositoryImpl repository; //con los Spy siempre tiene que llamarse a la invocacion real del metodo es decir su implementacion y no a la interfaz
    @Spy
    QuestionRepositoryImpl questionRepository; //con los Spy siempre tiene que llamarse a la invocacion real del metodo es decir su implementacion y no a la interfaz

    @Captor
    ArgumentCaptor<Long> captor;

    @InjectMocks //Inyectamos los @Spy en la implementacion
    ExamServiceImpl service;

    @Test
    void testSpy() {
        /*los espias son un hibrido entre un objeto real y un mock, nos permite invocar sin definir ningun when(ningun simulacro), no tenemos que mockear ningun metodo de
        este spy ya que no es necesario, simplemente cuando invocamos a un metodo llamará al método real.
        El mock es 100% simulado por lo tanto todos sus metodos los tenemos que mockear con el when o doAlgo(doThrow, doAnswer, doCallRealMethod); a diferencia del
        el spy no aqui solo vamos a simular con el when o doAlgo lo que queramos simular, pero el reseto será la llamada real en los demás metodos que definamos esa simulacion,
        además el spy se crea a partir de una clase concreta y no de una clase abstracta o interfaz, porque va a llamar métodos reales; y si estamos usando una interfaz ese método real no
        está implementado, por lo tanto va a fallar la prueba.
        */

        List<String> preguntas = Arrays.asList("¿Qué significa SOLID?");

//        when(questionRepository1.findQuestionsByExamId(anyLong())).thenReturn(Datos.QUESTIONS); //aqui hacemos una invocacion falsa al metodo findQuestionsByExamId, pero si usamos when se va a imprimir QuestionRepositoryImpl.findQuestionsByExamId, cuando no deberia imrimirse, asi que haceos uso de doReturn().when()
        doReturn(preguntas).when(questionRepository).findQuestionsByExamId(anyLong()); //y con esto ya se imprimiria nomas ExamRepositoryImpl.findAll

        Exam exam = service.findExamWithQuestionsByName("Computacion"); //aqui estamos llamando directamente al metodo con el service(llamada real al metodo findAll()), en los spy no usamos when

        assertEquals(4, exam.getId());
        assertEquals("Computacion", exam.getName());
        assertEquals(1, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("¿Qué significa SOLID?"));

        verify(repository).findAll();   //va a verificar y realmente se está llamando de forma real
        verify(questionRepository).findQuestionsByExamId(anyLong());   //se está llamando pero el simulado (mock)
    }
}