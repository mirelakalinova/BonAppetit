package com.bonappetit.controller;

import com.bonappetit.config.UserSession;
import com.bonappetit.model.dto.AddRecipeDto;
import com.bonappetit.model.dto.LoginDataDto;
import com.bonappetit.model.dto.UserRegDto;
import com.bonappetit.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class UserController {
    private final UserService userService;
    private final UserSession userSession;

    public UserController(UserService userService, UserSession userSession) {
        this.userService = userService;
        this.userSession = userSession;
    }


    @ModelAttribute("registerData")
    public UserRegDto registerDTO() {
        return new UserRegDto();
    }




    @ModelAttribute("loginData")
    public LoginDataDto loginDataDto() {
        return new LoginDataDto();
    }


    @GetMapping("login")
    public String login() {
        if(userSession.isLoggedIn()){
            return "redirect:/home";
        }
        return "/login";
    }

    @PostMapping("login")
    public String doLogin(@Valid LoginDataDto loginDataDto,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {

        boolean successLogin = userService.login(loginDataDto);

        if (successLogin) {

            return "redirect:/home";
        }
        redirectAttributes.addFlashAttribute("loginData", loginDataDto);
        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.loginData",
                bindingResult);

        return "redirect:/login";
    }

    @GetMapping("register")
    public String register() {
        if(userSession.isLoggedIn()){
            return "redirect:/home";
        }
        return "/register";
    }

    @PostMapping("register")
    public String doRegister(@Valid UserRegDto userRegDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors() || !userRegDto.getConfirmPassword().equals(userRegDto.getPassword())) {
            redirectAttributes.addFlashAttribute("registerData", userRegDto);
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.registerData",
                    bindingResult);
            return "redirect:/register";
        }

        if (userService.existingUser(userRegDto)) {
//            ObjectError error =
//                    new ObjectError("username","An account already exists for this username.");
//            model.addAttribute("message", "An account already exists for this username.");
//            bindingResult.rejectValue("username", "An account already exists for this username.");
            bindingResult.rejectValue("email", "An account already exists for this username.");
            redirectAttributes.addFlashAttribute("registerData", userRegDto);
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.registerData",
                    bindingResult);
            return "redirect:/register";

        }

        userService.register(userRegDto);
        LoginDataDto loginUser = new LoginDataDto();
        loginUser.setUsername(userRegDto.getUsername());
        redirectAttributes.addFlashAttribute("loginData", loginUser);
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(){
        if(!userSession.isLoggedIn()){
            return "redirect:/";
        }
        userSession.logout();
        return "redirect:/";

    }









}
