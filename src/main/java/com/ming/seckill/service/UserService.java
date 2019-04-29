package com.ming.seckill.service;

import com.ming.seckill.dao.UserDAO;
import com.ming.seckill.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    UserDAO userDAO;

    public User getById(int id){
        return userDAO.getById(id);
    }

    @Transactional
    public boolean tx() {
        User user1 = new User();
        user1.setId(2);
        user1.setName("22222");
        userDAO.insert(user1);

        User user2 = new User();
        user2.setId(1);
        user2.setName("11111");
        userDAO.insert(user2);

        return true;
    }
}
