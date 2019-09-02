package org.jc.test.collapsar;

import org.jc.framework.collapsar.support.parser.Optional;
import org.jc.test.collapsar.caches.AskCaches;
import org.jc.test.collapsar.caches.OrderCaches;
import org.jc.test.collapsar.caches.UserCaches;
import org.jc.test.collapsar.caches.CommonCaches;
import org.jc.test.collapsar.modal.Order;
import org.jc.test.collapsar.modal.User;
import org.jc.test.collapsar.util.Gsons;
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
    private OrderCaches orderCaches;
    @Autowired
    private UserCaches userCaches;
    @Autowired
    private CommonCaches commonCaches;
    @Autowired
    private AskCaches askCaches;

    @Test
    public void testAskDelById() {
        askCaches.delById(234L);
    }

    @Test
    public void testOrderDelById() {
//        orderCaches.delById(234L);
        orderCaches.getById(145L);
//        orderCaches.getByUserId(42L);
    }

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
    public void batchDelById() {
        List<Long> idList = new ArrayList<>();
        idList.add(1L);
        idList.add(2L);
        idList.add(3L);
        userCaches.batchDelById(idList);
    }

    @Test
    public void batchGetById() {
        List<Long> idList = new ArrayList<>();
        idList.add(1L);
        idList.add(2L);
        idList.add(3L);
        Optional<User> userOptional = userCaches.batchGetById(idList);
        System.out.println(Gsons.getJson(userOptional));
    }

    @Test
    public void batchSetById() {
        List<User> userList = new ArrayList<>();
        userList.add(new User(1L, "xxx", "123"));
        userList.add(new User(2L, "yyy", "456"));
        userList.add(new User(3L, "ccc", "789"));
        userCaches.batchSetById(userList);
    }

    @Test
    public void setById() {
//        userCaches.setById(new User(3L, "ccc", "789"));
    }

    @Test
    public void batchSetUserById() {
        List<User> userList = new ArrayList<>();
        userList.add(new User(1L, "xxx", "123"));
        userList.add(new User(2L, "yyy", "456"));
        userList.add(new User(3L, "ccc", "789"));
        commonCaches.batchSetUserById(userList);
    }

    @Test
    public void batchGetUserById() {
        List<Long> idList = new ArrayList<>();
        idList.add(1L);
        idList.add(2L);
        idList.add(3L);
        System.out.println(Gsons.getJson(commonCaches.batchGetUserById(idList)));
    }

    @Test
    public void batchDelUserById() {
        List<Long> idList = new ArrayList<>();
        idList.add(1L);
        idList.add(2L);
        idList.add(3L);
        commonCaches.batchDelUserById(idList);
    }


    @Test
    public void delUserById() {
        commonCaches.delUserById(2L);
    }

    @Test
    public void setUserById() {
        commonCaches.setXycById(1L, new User());
    }

    @Test
    public void getUserById() {
        commonCaches.getUserById(new User());
    }
}
