package com.zst.week9.q3.demo.provider;


import com.zst.week9.q3.demo.api.Order;
import com.zst.week9.q3.demo.api.OrderService;

public class OrderServiceImpl implements OrderService {

    @Override
    public Order findOrderById(int id) {
        return new Order(id, "Cuijing" + System.currentTimeMillis(), 9.9f);
    }
}
