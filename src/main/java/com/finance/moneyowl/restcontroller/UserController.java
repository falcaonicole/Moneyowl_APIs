package com.finance.moneyowl.restcontroller;

import com.finance.moneyowl.generatedmodels.*;
import com.finance.moneyowl.service.Interface.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import src.finance.moneyowl.UserApi;

@Slf4j
@RestController
public class UserController implements UserApi {

    @Autowired
    private UserService userService;

    ModelMapper modelMapper = new ModelMapper();

    @Override
    public ResponseEntity<UserResponse> createUser(@RequestBody UserDTO userDTO) {
        log.info("Start UserController :: createUser - {}", userDTO);
        return new ResponseEntity<>(userService.saveUser(userDTO), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<String> deleteUser(@PathVariable("userId") Long userId) {
        log.info("Start UserController :: deleteUser - {}", userId);
        return new ResponseEntity<>(userService.deleteUser(userId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserResponse> getUserById(@PathVariable("userId") Long userId) {
        log.info("Start UserController :: getUserById( - {}", userId);
        return new ResponseEntity<>(userService.getUser(userId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> updateUser(@PathVariable("userId") Long userId, @RequestBody UserDTO userDTO) {
        log.info("Start UserController :: updateUser - {}", userDTO);
        return new ResponseEntity<>(userService.putUser(userId, userDTO), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PortfolioDTO> getUserPortfolio(Long userId) {
        log.info("Start UserController :: getUserPortfolio - {}", userId);
        return new ResponseEntity<>(userService.getUserPortfolio(userId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<LiabilityDTOResponse> getAllUserLiabilities(@PathVariable("userId") Long userId) {
        log.info("Start UserController :: getAllUserLiabilities - {}", userId);
        return new ResponseEntity<>(LiabilityDTOResponse.builder().data(new LiabilityResponseDetails(userService.getAllUserLiabilities(userId))).build(), HttpStatus.OK);
    }


}
