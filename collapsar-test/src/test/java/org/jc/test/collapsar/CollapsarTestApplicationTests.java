package org.jc.test.collapsar;

import org.jc.test.collapsar.caches.UserCaches;
import org.jc.test.collapsar.modal.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CollapsarTestApplicationTests {
    @Autowired
    private UserCaches userCaches;

    @Test
    public void contextLoads() {
        userCaches.setById(1L, new User());
        User user = userCaches.getById(25L);
        System.out.println(user.getId());
        System.out.println(user.getPassword());
        System.out.println(user.getUserName());
    }

}
