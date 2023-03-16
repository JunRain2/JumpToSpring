package com.mysite.sbb;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerRepository;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionRepository;
import com.mysite.sbb.question.QuestionService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class SbbApplicationTests {

    @Autowired // 스프링이 객체를 대신 생성해서 주입. DI(Dependency Injection), 테스트 코드에서는 생성자 x 따라서 테스트 코드에서만
    private QuestionService questionService;

    @Test
    void testJpa() {
        for (int i = 1; i <= 300; i++) {
            System.out.println(i);
            String subject = String.format("테스트 데이터입니다:[%03d]", i);
            String content = "내용무";
            this.questionService.create(subject, content, null);
        }
    }
}
