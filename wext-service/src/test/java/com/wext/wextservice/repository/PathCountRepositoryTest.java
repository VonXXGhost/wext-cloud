package com.wext.wextservice.repository;

import com.wext.wextservice.domain.PathCount;
import com.wext.wextservice.domain.Wext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@DataJpaTest
public class PathCountRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PathCountRepository pathCountRepository;

    @Test
    @SuppressWarnings("deprecation")
    public void findHotChildPath() {
        entityManager.persist(
                Wext.builder().id("wext_id_1").fullPath("/")
                        .createdTime(new Date(3019, Calendar.OCTOBER, 1))
                        .userId(1L)
                        .build()
        );
        entityManager.persist(
                Wext.builder().id("wext_id_2").fullPath("/pNode1")
                        .createdTime(new Date(3019, Calendar.OCTOBER, 1))
                        .userId(1L)
                        .build()
        );
        entityManager.persist(
                Wext.builder().id("wext_id_3").fullPath("/pNode1")
                        .createdTime(new Date(3019, Calendar.OCTOBER, 1))
                        .userId(1L)
                        .build()
        );
        entityManager.persist(
                Wext.builder().id("wext_id_4").fullPath("/pNode2")
                        .createdTime(new Date(3019, Calendar.OCTOBER, 1))
                        .userId(1L)
                        .build()
        );
        entityManager.persist(
                Wext.builder().id("wext_id_5").fullPath("/pNode1/node1")
                        .createdTime(new Date(3019, Calendar.OCTOBER, 1))
                        .userId(1L)
                        .build()
        );
        entityManager.persist(
                Wext.builder().id("wext_id_6").fullPath("/pNode1/node1")
                        .createdTime(new Date(3019, Calendar.OCTOBER, 1))
                        .userId(1L)
                        .build()
        );
        entityManager.persist(
                Wext.builder().id("wext_id_7").fullPath("/pNode1/node1")
                        .createdTime(new Date(3019, Calendar.OCTOBER, 1))
                        .userId(1L)
                        .build()
        );
        entityManager.persist(
                Wext.builder().id("wext_id_8").fullPath("/pNode1/node2")
                        .createdTime(new Date(3019, Calendar.OCTOBER, 1))
                        .userId(1L)
                        .build()
        );
        Pageable pageable = PageRequest.of(0, 30);
        var res = pathCountRepository.getHotChildPath("", 10, pageable);
        assertEquals(2, res.getTotalElements());
        assertEquals(res.getContent().get(0), new PathCount("/pNode1", 2L));

        res = pathCountRepository.getHotChildPath("/pNode1", 10, pageable);
        assertEquals(new PathCount("/pNode1/node1", 3L), res.getContent().get(0));

        entityManager.persist(
                Wext.builder().id("wext_id_9").fullPath("/pNode1/node1")
                        .createdTime(new Date(2019, Calendar.OCTOBER, 1))
                        .userId(1L)
                        .build()
        );

        res = pathCountRepository.getHotChildPath("/pNode1", 10, pageable);
        assertEquals(new PathCount("/pNode1/node1", 3L), res.getContent().get(0));
    }
}
