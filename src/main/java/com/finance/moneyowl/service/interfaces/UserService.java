package com.finance.moneyowl.service.interfaces;

import com.finance.moneyowl.entity.User;
import com.finance.moneyowl.generatedmodels.UserRequest;
import com.finance.moneyowl.generatedmodels.UserResponse;


public interface UserService {

    public UserResponse getUser(Long userId);

    public String updateUser(Long userId, UserRequest UserRequest);

    public String deleteUser(Long userId);

    User getUserById(Long userId);

    public User findByEmail(String email);

    public User findByMobNo(String mobNo);
}
