package org.chon.appmockito.ejemplos.repositories;

import java.util.List;

public interface QuestionRepository {
    List<String> findQuestionsByExamId(Long id);
}
