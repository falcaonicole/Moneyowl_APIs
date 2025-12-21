package com.finance.moneyowl.service.impl;

import com.finance.moneyowl.entity.Liability;
import com.finance.moneyowl.entity.Portfolio;
import com.finance.moneyowl.entity.User;
import com.finance.moneyowl.exceptions.ResourceNotFoundException;
import com.finance.moneyowl.generatedmodels.LiabilityDTO;
import com.finance.moneyowl.generatedmodels.PortfolioDTO;
import com.finance.moneyowl.generatedmodels.UserDTO;
import com.finance.moneyowl.generatedmodels.UserResponse;
import com.finance.moneyowl.repository.UserRepository;
import com.finance.moneyowl.service.Interface.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public UserResponse saveUser(UserDTO userDTO) {
        log.info("Start UserServiceImpl:: saveUser - {}", userDTO);
        User user = new User(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(), userDTO.getAddress(), userDTO.getMobNo());
        /* Encrypt Password then save */
        user.setPassword(userDTO.getPassword());
        User userEntity = userRepository.save(user);
        log.info("Start UserServiceImpl:: saveUser");
        return modelMapper.map(userEntity, UserResponse.class);
    }

    @Override
    public UserResponse getUser(Long id) {
        log.info("Start UserServiceImpl:: getUser - {}", id);
        User userEntity = getUserById(id);
        log.info("Start UserServiceImpl:: getUser");
        return modelMapper.map(userEntity, UserResponse.class);
    }

    @Override
    public String putUser(Long id, UserDTO userDTO) {
        log.info("Start UserServiceImpl:: putUser - {}", userDTO);
        User user = getUserById(id);
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setAddress(userDTO.getAddress());
        user.setEmail(userDTO.getEmail());
        user.setMobNo(userDTO.getMobNo());

        userRepository.save(user);
        log.info("Start UserServiceImpl:: putUser");
        return "User Info Updated Successfully";
    }

    @Override
    public String deleteUser(Long id) {
        log.info("Start UserServiceImpl:: deleteUser - {}", id);
        User user = getUserById(id);
        userRepository.delete(user);
        log.info("Start UserServiceImpl:: deleteUser");
        return "User Deleted Successfully";
    }

    @Override
    @Transactional
    public List<LiabilityDTO> getAllUserLiabilities(Long userId) {
        log.info("Start UserServiceImpl:: getAllUserLiabilities - {}", userId);
        User user = getUserById(userId);
        List<Liability> liabilities = user.getLiabilities();
        log.info("Start UserServiceImpl:: getAllUserLiabilities");
        return liabilities.stream()
                .map(liability -> modelMapper.map(liability, LiabilityDTO.class))
                .toList();
    }

    @Override
    public PortfolioDTO getUserPortfolio(Long userId) {
        log.info("Start UserServiceImpl:: getUserPortfolio - {}", userId);
        User user = getUserById(userId);
        Portfolio portfolio = user.getPortfolio();
        if (portfolio == null) {
            log.error("User's Portfolio is Empty. Please create your Portfolio first");
            throw new ResourceNotFoundException("User's Portfolio is Empty. Please create your Portfolio first");
        }
        log.info("Start UserServiceImpl:: getUserPortfolio");
        return modelMapper.map(portfolio, PortfolioDTO.class);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User with given userId does not exist");
                    return new ResourceNotFoundException("User with given userId does not exist");
                });
    }

    public Boolean existsById(Long Id){
        log.info("Start UserServiceImpl:: existsById - {}", Id);
        return userRepository.existsById(Id);
    }

    public User findByEmail(String email) {
        log.info("Start UserServiceImpl:: findByEmail - {}", email);
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User with given Email Id does not exist"));
    }

}
