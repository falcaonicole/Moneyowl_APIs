package com.finance.moneyowl.restcontroller;

import com.finance.moneyowl.generatedmodels.UserRequest;
import com.finance.moneyowl.generatedmodels.UserResponse;
import com.finance.moneyowl.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import src.finance.moneyowl.UserApi;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    ModelMapper modelMapper = new ModelMapper();
    private UserService userService;

    @Override
    public ResponseEntity<String> deleteUser(Long userId) {
        log.info("Start UserController :: deleteUser - {}", userId);
        return new ResponseEntity<>(userService.deleteUser(userId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserResponse> getUserById(Long userId) {
        log.info("Start UserController :: getUserById( - {}", userId);
        return new ResponseEntity<>(userService.getUser(userId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> updateUser(Long userId, UserRequest userRequest) {
        log.info("Start UserController :: updateUser - {}", userRequest);
        return new ResponseEntity<>(userService.updateUser(userId, userRequest), HttpStatus.OK);
    }

}
