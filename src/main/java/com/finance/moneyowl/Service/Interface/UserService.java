package com.finance.moneyowl.Service.Interface;

import com.finance.moneyowl.Entity.User;
import com.finance.moneyowl.Model.UserDTO;

public interface UserService {
    public User saveUser(UserDTO userDTO);

    public User getUser(String Id);

    public String putUser(String Id, UserDTO userDTO);

    public String deleteUser(String Id);
}
