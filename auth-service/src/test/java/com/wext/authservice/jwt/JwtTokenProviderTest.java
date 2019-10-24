package com.wext.authservice.jwt;

import com.wext.authservice.AuthServiceApplication;
import com.wext.authservice.config.RedisKeyPrefixs;
import com.wext.common.bean.RedisTool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuthServiceApplication.class)
public class JwtTokenProviderTest {
    @InjectMocks
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private RedisTool<Date> timeRedisTool;

    @Test
    @SuppressWarnings("deprecation")
    public void validateToken() {
        var userID = 40L;
        Mockito.when(timeRedisTool.get(RedisKeyPrefixs.lastPasswordUpdatePrefix + userID))
                .thenReturn(new Date(2019, Calendar.OCTOBER, 24, 0, 1, 0));

        var oldToken = jwtTokenProvider._createToken(
                "40", Collections.singletonList("USER"),
                new Date(2018, Calendar.OCTOBER, 24, 0, 0, 0)
        );
        var tokenBeforeChangePW = jwtTokenProvider._createToken(
                "40", Collections.singletonList("USER"),
                new Date(2019, Calendar.OCTOBER, 24, 0, 0, 0)
        );
        var tokenAfterChangePW = jwtTokenProvider._createToken(
                "40", Collections.singletonList("USER"),
                new Date(2019, Calendar.OCTOBER, 24, 0, 2, 0)
        );

        assertFalse(jwtTokenProvider.validateToken(oldToken));
        assertFalse(jwtTokenProvider.validateToken(tokenBeforeChangePW));
        assertTrue(jwtTokenProvider.validateToken(tokenAfterChangePW));
    }
}