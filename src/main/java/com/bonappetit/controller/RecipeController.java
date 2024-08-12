package com.bonappetit.controller;

import com.bonappetit.config.UserSession;
import com.bonappetit.model.dto.AddRecipeDto;

import com.bonappetit.model.entity.CategoryName;

import com.bonappetit.service.RecipeService;
import org.springframework.stereotype.Controller;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;


@Controller
public class RecipeController {

    private final UserSession userSession;
    private final RecipeService recipeService;

    public RecipeController(UserSession userSession, RecipeService recipeService) {
        this.userSession = userSession;
        this.recipeService = recipeService;
    }

    @ModelAttribute("addRecipeData")
    public AddRecipeDto addRecipeData() {
        return new AddRecipeDto();
    }

    @ModelAttribute("categoryNameAll")
    public CategoryName[] allCategoryNames() {
        return CategoryName.values();
    }

    @GetMapping("/recipe-add")
    public String addRecipe(
//            @RequestParam(name = "categoryName",
//            defaultValue = "MAIN_DISH")
//                            CategoryName categoryName, Model model
    ) {

//        model.addAttribute("allCategoryNames",
//                CategoryName.values());
        if (userSession.isLoggedIn()) {

            return "/recipe-add";
        }

        return "/index";
    }

    @PostMapping("/recipe-add")
    public String addRecipe(@Valid AddRecipeDto addRecipeDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("addRecipeData", addRecipeDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.addRecipeData", bindingResult);
            return "redirect:/recipe-add";
        }


        if (!recipeService.saveRecipe(addRecipeDto)) {
            return "redirect:/recipe-add";
        }
        return "redirect:/home";
    }
}
