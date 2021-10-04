package com.zst.week9.q3.demo.provider;


import com.zst.week9.q3.demo.api.User;
import com.zst.week9.q3.demo.api.UserService;

public class UserServiceImpl implements UserService {

    @Override
    public User findById(int id) {
        return new User(id, "KK" + System.currentTimeMillis());
    }
}
