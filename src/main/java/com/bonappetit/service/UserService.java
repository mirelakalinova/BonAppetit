package com.bonappetit.service;

import com.bonappetit.config.UserSession;
import com.bonappetit.model.dto.LoginDataDto;
import com.bonappetit.model.dto.RecipeInfoDto;
import com.bonappetit.model.dto.UserRegDto;
import com.bonappetit.model.entity.Recipe;
import com.bonappetit.model.entity.User;
import com.bonappetit.repo.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.boot.Banner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final ModelMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserSession userSession;

    public UserService(ModelMapper mapper, PasswordEncoder passwordEncoder, UserRepository userRepository, UserSession userSession) {
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userSession = userSession;
    }

    public void register(UserRegDto userRegDto) {


        User newUserToAdd = mapper.map(userRegDto, User.class);
        newUserToAdd.setPassword(passwordEncoder.encode(userRegDto.getPassword()));

        this.userRepository.save(newUserToAdd);

    }

    public boolean existingUser(UserRegDto userRegDto) {

        return this.userRepository.findByUsernameOrEmail(userRegDto.getUsername(), userRegDto.getEmail()).isPresent();
    }

    public boolean login(LoginDataDto loginDataDto) {
        String password = loginDataDto.getPassword();

        Optional<User> userToLogin = userRepository.findByUsername(loginDataDto.getUsername());
        if (userToLogin.isPresent()) {
            boolean matchedPasswords = passwordEncoder.matches(password, userToLogin.get().getPassword());

            if (!matchedPasswords) {
                return false;
            }

            userSession.login(userToLogin.get().getId(), userToLogin.get().getUsername());

            return true;
        }
        return false;
    }

    public List<Recipe> favouriteRecipes(long userId){
      return   userRepository.findById(userId)
              .map(User::getFavouriteRecipes)
              .orElseGet(ArrayList::new);




    }
}
