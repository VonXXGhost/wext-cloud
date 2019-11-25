package com.wext.userservice.service;

import com.wext.common.domain.exception.InvalidOperationException;
import com.wext.userservice.UserServiceApplication;
import com.wext.userservice.repository.ManagerRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceApplication.class)
@Transactional
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class ManagerServiceTest {

    @Autowired
    private ManagerService managerService;

    @Autowired
    private ManagerRepository managerRepository;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void getManagerByName() {
        var src = managerService.createManager("unitTest", "123456", null);
        var manager = managerService.getManagerByName("unitTest");
        assertEquals(src, manager);
    }

    @Test
    public void getManagerById() {
        var src = managerService.createManager("unitTest", "123456", null);
        var manager = managerService.getManagerById(src.getId());
        assertEquals(src, manager);
    }

    @Test
    public void createManager() {
        var manager = managerService.createManager("unitTest", "123456", null);
        assertEquals(managerRepository.findByName("unitTest").get(), manager);
        assertEquals("Manager", manager.getRole());

        exception.expect(InvalidOperationException.class);
        managerService.createManager("unitTest", "123456", null);
    }
}
