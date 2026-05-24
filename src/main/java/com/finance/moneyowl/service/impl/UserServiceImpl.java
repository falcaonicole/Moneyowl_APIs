package com.finance.moneyowl.service.impl;

import com.finance.moneyowl.entity.User;
import com.finance.moneyowl.exceptions.ResourceNotFoundException;
import com.finance.moneyowl.generatedmodels.UserRequest;
import com.finance.moneyowl.generatedmodels.UserResponse;
import com.finance.moneyowl.repository.UserRepository;
import com.finance.moneyowl.service.interfaces.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.finance.moneyowl.utils.ErrorMessageConstants.LOG_TEMPLATE;
import static com.finance.moneyowl.utils.ErrorMessageConstants.USER_NOT_FOUND;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public UserResponse getUser(Long id) {
        log.info("Start UserServiceImpl:: getUser - {}", id);
        User userEntity = getUserById(id);
        log.info("Start UserServiceImpl:: getUser");
        return modelMapper.map(userEntity, UserResponse.class);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public String updateUser(Long userId, UserRequest UserRequest) {
        log.info("Start UserServiceImpl:: updateUser - {}", UserRequest);
        User user = User.builder()
                .fullName(UserRequest.getFullName())
                .address(UserRequest.getAddress())
                .email(UserRequest.getEmail())
                .mobNo(UserRequest.getMobNo())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
        log.info("End UserServiceImpl:: updateUser");
        return "User details Updated Successfully";
    }

    @Override
    public String deleteUser(Long id) {
        log.info("Start UserServiceImpl:: deleteUser - {}", id);
        User user = getUserById(id);
        userRepository.delete(user);
        log.info("Start UserServiceImpl:: deleteUser");
        return "User Deleted Successfully";
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error(LOG_TEMPLATE, USER_NOT_FOUND, id);
                    return new ResourceNotFoundException(USER_NOT_FOUND, id);
                });
    }

    // For Email based Login
    public User findByEmail(String email) {
        log.info("Start UserServiceImpl:: findByEmail - {}", email);
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND, email));
    }

    @Override
    public User findByMobNo(String mobNo) {
        return userRepository.findByMobNo(mobNo)
                .orElseThrow(() -> {
                    log.error(LOG_TEMPLATE, USER_NOT_FOUND, mobNo);
                    return new ResourceNotFoundException(USER_NOT_FOUND, mobNo);
                });
    }

}
