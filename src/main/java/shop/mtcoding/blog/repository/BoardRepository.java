package shop.mtcoding.blog.repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import shop.mtcoding.blog.dto.JoinDTO;
import shop.mtcoding.blog.dto.LoginDTO;
import shop.mtcoding.blog.dto.WriteDTO;
import shop.mtcoding.blog.model.Board;
import shop.mtcoding.blog.model.User;

@Repository
public class BoardRepository {

    @Autowired
    private EntityManager em;

    // select id, title from board_tb
    // resultClass 안붙이고 직접 파싱하려면 Obgject 배열(Object[])로 리턴된 값을 파싱
    // object[0] = 1
    // object[1] = 제목1
    public int count() {
        Query query = em.createNativeQuery("select count(*) from board_tb");
        // 원래는 Object 배열로 리턴을 받는다, Obgject 배열은 칼럼값의 연속이다.
        // 그룹함수를 써서, 하나의 칼럼을 조회하면 Obgject로 리턴된다.
        BigInteger count = (BigInteger) query.getSingleResult();
        return count.intValue();

    }

    @Transactional
    public void save(WriteDTO writeDTO, Integer userId) {
        System.out.println("save Repository 메서드 실행");
        Query query = em.createNativeQuery(
                "insert into board_tb(title, content, user_id, created_at) values(:title, :content, :userId, now())");
        query.setParameter("title", writeDTO.getTitle());
        query.setParameter("content", writeDTO.getContent());
        query.setParameter("userId", userId);
        query.executeUpdate();
    }

    // createQuery는 객체지향 쿼리(SPQL)라고 한다. SPQL을 쓰면 Migration안해도된다.
    // 즉, 객체지향 쿼리를 쓰면 DBMS마다 다른 문법을 신경쓰지 않고 자바에서 쓸수 있다.
    public List<Board> findAll(int page) {
        // 페이징을 해서 조회한다. 페이징쿼리는 DB에서 먼저 해보고 작성한다.
        // [limit int a, int b] << a는 시작인덱스 번호, b는 표시할 갯수
        // 예를 들어 limit 1,3 이면 DB의 1, 2, 3인덱스를 가진 튜플만 검색된다.
        // limit은 쿼리문의 가장 뒤에 들어가야 한다.
        // 페이지당 표시할 갯수는 변하면 안되기 때문에 final int로 지정한다.
        final int SIZE = 3;
        Query query = em.createNativeQuery(
                "select * from board_tb order by id desc limit :page, :size", Board.class);
        query.setParameter("page", page * SIZE);
        query.setParameter("size", SIZE);
        return query.getResultList();
    }

    public Board findById(Integer id) {
        Query query = em.createNativeQuery(
                "select * from board_tb where id =:id", Board.class);
        query.setParameter("id", id);
        Board board = (Board) query.getSingleResult();
        return board;
    }
}
