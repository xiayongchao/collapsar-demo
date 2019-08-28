package org.jc.test.collapsar.modal;

/**
 * @author xiayc
 * @date 2018/10/25
 */
public class Order {
    private Long id;
    private Long userId;
    private Double money;

    public Order() {
    }

    public Order(Long id, Long userId, Double money) {
        this.id = id;
        this.userId = userId;
        this.money = money;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }
}
