package com.finance.moneyowl.service.Interface;

import com.finance.moneyowl.entity.User;
import com.finance.moneyowl.generatedmodels.LiabilityDTO;
import com.finance.moneyowl.generatedmodels.PortfolioDTO;
import com.finance.moneyowl.generatedmodels.UserDTO;
import com.finance.moneyowl.generatedmodels.UserResponse;

import java.util.List;

public interface UserService {
    public UserResponse saveUser(UserDTO userDTO);

    public UserResponse getUser(Long userId);

    public String putUser(Long userId, UserDTO userDTO);

    public String deleteUser(Long userId);

    List<LiabilityDTO> getAllUserLiabilities(Long userId);

    PortfolioDTO getUserPortfolio(Long userId);

    User getUserById(Long userId);
}
