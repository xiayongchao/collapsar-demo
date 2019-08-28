package org.jc.test.collapsar;

import org.jc.test.collapsar.caches.UserCaches;
import org.jc.test.collapsar.modal.Order;
import org.jc.test.collapsar.modal.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

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
        System.out.println("----------------------");
        List<User> userList = new ArrayList<>();
        userList.add(new User(1L, "xxx", "123"));
        userList.add(new User(2L, "yyy", "456"));
        userList.add(new User(3L, "ccc", "789"));

        List<Order> orderList = new ArrayList<>();
        orderList.add(new Order(1L, 1L, 1.1));
        orderList.add(new Order(2L, 2L, 2.2));
        orderList.add(new Order(3L, 3L, 3.3));
        userCaches.batchDelByIdAndUserNameAndMoney(66L, orderList, userList);

//        userCaches.batchDelByIdAndUserNameAndMoney(66L, null);
    }

    @Test
    public void testBatchDelById() {
        List<Long> idList = new ArrayList<>();
        idList.add(1L);
        idList.add(2L);
        idList.add(3L);
        userCaches.batchDelById(idList);
    }

}
