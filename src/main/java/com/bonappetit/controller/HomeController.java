package com.bonappetit.controller;

import com.bonappetit.config.UserSession;
import com.bonappetit.model.dto.RecipeInfoDto;
import com.bonappetit.model.entity.CategoryName;
import com.bonappetit.model.entity.Recipe;
import com.bonappetit.model.entity.User;
import com.bonappetit.repo.CategoryRepository;
import com.bonappetit.repo.RecipeRepository;
import com.bonappetit.repo.UserRepository;
import com.bonappetit.service.RecipeService;
import com.bonappetit.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Controller
public class HomeController {
    private final UserSession userSession;
    private final CategoryRepository categoryRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final RecipeService recipeService;
    private final UserService userService;

    public HomeController(UserSession userSession, CategoryRepository categoryRepository, RecipeRepository recipeRepository, UserRepository userRepository, RecipeService recipeService, UserService userService) {
        this.userSession = userSession;
        this.categoryRepository = categoryRepository;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.recipeService = recipeService;
        this.userService = userService;
    }

    @ModelAttribute("recipeInfo")
    public RecipeInfoDto recipeInfoDto() {
        return new RecipeInfoDto();
    }

    @GetMapping("/")
    public String nonLoggedInUser() {
        if (userSession.isLoggedIn()) {
            return "redirect:/home";
        }
        return "index";
    }

    @GetMapping("/home")
    @Transactional
    public String home(Model model) {

        if (!userSession.isLoggedIn()) {
            return "redirect:/";
        }

        Map<CategoryName, List<Recipe>> allRecipe = recipeService.findAllByCategory();

        List<RecipeInfoDto> mainDishRecipeList = getRecipeByCategory(allRecipe, CategoryName.MAIN_DISH);
        addImage(mainDishRecipeList);
        model.addAttribute("mainDish", mainDishRecipeList);
        List<RecipeInfoDto> coctailsRecipeList = getRecipeByCategory(allRecipe, CategoryName.COCKTAIL);
        addImage(coctailsRecipeList);
        model.addAttribute("cocktails", coctailsRecipeList);
        List<RecipeInfoDto> dessertDishRecipeList = getRecipeByCategory(allRecipe, CategoryName.DESSERT);
        addImage(dessertDishRecipeList);
        model.addAttribute("desserts", dessertDishRecipeList);

        List<RecipeInfoDto> favRecipes = userService.favouriteRecipes(userSession.id()).stream().map(RecipeInfoDto::new).toList();
        model.addAttribute("fav", favRecipes);
        addImage(favRecipes);

        Optional<User> user = userRepository.findById(userSession.id());
        if (user.isPresent()) {

            List<RecipeInfoDto> recipeAddedByUser = recipeRepository.findAllByAddedBy(user.get());
            model.addAttribute("userRecipes", recipeAddedByUser);
            addImage(recipeAddedByUser);
        }
        return "home";
    }

    private void addImage(List<RecipeInfoDto> coctailsRecipeList) {
        coctailsRecipeList.forEach(r -> r.setImage(recipeRepository.findById(r.getId()).get().getCategory().getCategoryName().getImage()));
    }

    @PostMapping("/favourite/{recipeId}")
    public String addToFavourite(@PathVariable long recipeId) {
        if (!userSession.isLoggedIn()) {
            return "redirect:/";
        }
        long id = userSession.id();
        Optional<User> user = userRepository.findById(id);
        if (userRepository.findById(id).isEmpty()) {
            return "redirect:/";
        }

        recipeService.addToFavourite(id, recipeId);

        return "redirect:/home";
    }


    @PostMapping("/remove-favourite/{recipeId}")
    public String removeFromFavourite(@PathVariable long recipeId) {
        if (!userSession.isLoggedIn()) {
            return "redirect:/";
        }
        long id = userSession.id();
        Optional<User> user = userRepository.findById(id);
        if (userRepository.findById(id).isEmpty()) {
            return "redirect:/";
        }

        recipeService.removeFromFavourite(id, recipeId);

        return "redirect:/home";
    }

    @DeleteMapping("/delete-recipe/{recipeId}")
    @Transactional
    public String deleteRecipe(@PathVariable long recipeId) {
        if (!userSession.isLoggedIn()) {
            return "redirect:/";
        }

        // find the user that add the recipe
        Optional<Recipe> recipeToRemove = recipeRepository.findById(recipeId);
        if (recipeToRemove.isEmpty()) {
            return "redirect:/home";
        }
        User addedBy = recipeToRemove.get().getAddedBy();
        if(userSession.id()==addedBy.getId()){
          recipeService.deleteRecipe(addedBy, recipeToRemove.get());
        }
        // check if it is the userSession


        return "redirect:/home";
    }

    private static List<RecipeInfoDto> getRecipeByCategory(Map<CategoryName, List<Recipe>> allRecipe, CategoryName categoryName) {
        return allRecipe.get(categoryName)
                .stream()
                .map(RecipeInfoDto::new)
                .toList();
    }


}
