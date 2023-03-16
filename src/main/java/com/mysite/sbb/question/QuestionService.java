package com.mysite.sbb.question;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public List<Question> getList(){
        return this.questionRepository.findAll();
    }

    public Question getQuestion(Integer id) {
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    public void create(String subject, String content, SiteUser user) {
        Question q = new Question();
        q.setSubject(subject);
        q.setContent(content);
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(user);
        this.questionRepository.save(q);
    }

    public Page<Question> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>(); // 정렬 기준 리스트
        sorts.add(Sort.Order.desc("createDate")); // 기준 추가, 또 추가하고 싶으면 add로 또 추가하면 됨.
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Specification<Question> spec = search(kw);
        return this.questionRepository.findAll(spec, pageable);
    }

    public void modify(Question question, String subject, String content) {
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        this.questionRepository.save(question);
    }

    public void delete(Question question) {
        this.questionRepository.delete(question);
    }

    public void vote(Question question, SiteUser siteUser) {
        question.getVoter().add(siteUser);
        this.questionRepository.save(question);
    }

    // 검색어(kw)를 입력받아 쿼리의 조인문과 where문을 생성하여 리턴하는 메서드
    // 여러 테이블에서 데이터를 검색해야 할 경우에는 JPA가 제공하는 Specification 인터페이스를 사용
    private Specification<Question> search(String kw) {
        return new Specification<Question>() {
            private static final long serialVersionUid = 1L;
            // q-Root : 기준을 의미하는 Question 엔티티의 객체
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true); // 중복을 제거
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);// 조인된 속성, 조인 타입
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = q.join("author", JoinType.LEFT);
                return cb.or( // like로 검색.
                        cb.like(q.get("subject"), "%" + kw + "%"),// 제목
                        cb.like(q.get("content"), "%" + kw + "%"),// 내용
                        cb.like(u1.get("username"), "%" + kw + "%"),// 질문 작성사
                        cb.like(a.get("content"), "%" + kw + "%"),// 답변 내용
                        cb.like(u2.get("username"), "%" + kw + "%"));// 답변 작성자
            }
        };
    }
}
