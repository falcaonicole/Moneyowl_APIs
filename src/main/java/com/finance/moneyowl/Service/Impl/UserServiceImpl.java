package com.finance.moneyowl.Service.Impl;

import com.finance.moneyowl.Entity.User;
import com.finance.moneyowl.Model.UserDTO;
import com.finance.moneyowl.Repository.UserRepository;
import com.finance.moneyowl.Service.Interface.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.finance.moneyowl.Utils.Constants.USER_NOT_FOUND;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Override
    public User saveUser(UserDTO userDTO) {
        User user = new User(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(), userDTO.getAddress(), userDTO.getMobNo());
        /* Encrypt Password then save */
        user.setPassword(userDTO.getPassword());
        return userRepository.save(user);
    }

    @Override
    public User getUser(String id) {
        return getUserById(id);
    }

    @Override
    public String putUser(String id, UserDTO userDTO) {
        User user = getUserById(id);
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setAddress(userDTO.getAddress());
        user.setEmail(userDTO.getEmail());
        user.setMobNo(userDTO.getMobNo());

        userRepository.save(user);
        return "User Info Updated Successfully";
    }

    @Override
    public String deleteUser(String id) {
        User user = getUserById(id);
        userRepository.delete(user);
        return "User Deleted Successfully";
    }

    private User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND + id));
    }
}
