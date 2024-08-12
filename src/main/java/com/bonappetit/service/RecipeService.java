package com.bonappetit.service;

import com.bonappetit.config.UserSession;
import com.bonappetit.model.dto.AddRecipeDto;
import com.bonappetit.model.dto.RecipeInfoDto;
import com.bonappetit.model.entity.Category;
import com.bonappetit.model.entity.CategoryName;
import com.bonappetit.model.entity.Recipe;
import com.bonappetit.model.entity.User;
import com.bonappetit.repo.CategoryRepository;
import com.bonappetit.repo.RecipeRepository;
import com.bonappetit.repo.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.beans.Transient;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RecipeService {
    private final ModelMapper mapper;
    private final RecipeRepository recipeRepository;
    private final UserSession userSession;
    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;


    public RecipeService(ModelMapper mapper, RecipeRepository recipeRepository, UserSession userSession, UserRepository userRepository,
                         CategoryRepository categoryRepository) {
        this.mapper = mapper;
        this.recipeRepository = recipeRepository;
        this.userSession = userSession;
        this.userRepository = userRepository;

        this.categoryRepository = categoryRepository;

    }

    public boolean saveRecipe(AddRecipeDto addRecipeDto) {
        Recipe newRecipe = mapper.map(addRecipeDto, Recipe.class);

        Optional<Category> category =
                categoryRepository.findByCategoryName(addRecipeDto.getCategory());

        if (category.isEmpty()) {
            return false;
        }
        Category categoryToAdd = mapper.map(category, Category.class);
        newRecipe.setCategory(categoryToAdd);
        Optional<User> user = userRepository.findById(userSession.id());
        if (user.isEmpty()) {
            return false;
        }
        User addedBy = mapper.map(user, User.class);
        newRecipe.setAddedBy(addedBy);
        recipeRepository.save(newRecipe);

        return true;
    }

    public Map<CategoryName, List<Recipe>> findAllByCategory() {
        Map<CategoryName, List<Recipe>> allRecipe = new HashMap<>();

        List<Category> allCategories = categoryRepository.findAll();

        allCategories.forEach(c
                -> {
            List<Recipe> recipeToAdd = recipeRepository.findAllByCategory(c);
            allRecipe.put(c.getCategoryName(), recipeToAdd);
        });

        return allRecipe;
    }

    @Transactional
    public void addToFavourite(long userId, long recipeId) {

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return;
        }

        Optional<Recipe> recipeAlreadyAdded =
                user.get().getFavouriteRecipes().stream().filter(r -> r.getId() == recipeId).findFirst();


        if (recipeAlreadyAdded.isEmpty()) {

            user.get().getFavouriteRecipes().add(recipeRepository.getById(recipeId));
            userRepository.save(user.get());
        }


    }

    @Transactional
    public void removeFromFavourite(long userId, long recipeId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return;
        }

        Optional<Recipe> recipeAlreadyAdded =
                user.get().getFavouriteRecipes().stream().filter(r -> r.getId() == recipeId).findFirst();


        if (recipeAlreadyAdded.isPresent()) {

            user.get().getFavouriteRecipes().remove(recipeRepository.getById(recipeId));
            userRepository.save(user.get());
        }
    }

    public void deleteRecipe(User addedBy, Recipe recipeToRemove) {
        // remove from category


        Category category = recipeToRemove.getCategory();
        category.getRecipes().remove(recipeToRemove);
        categoryRepository.save(category);
        //remove from user Favourites
        List<User> allUsers = userRepository.findAll();
//

        for (User u : allUsers) {
            for(Recipe r: u.getFavouriteRecipes()){
                if(r.getId()==recipeToRemove.getId()){

                    u.getFavouriteRecipes().remove((recipeToRemove));
                    userRepository.save(u);
                    break;
                }
            }
        }


//        addedBy.getFavouriteRecipes().remove(recipeToRemove);
        addedBy.getAddedRecipes().remove(recipeToRemove);
        userRepository.save(addedBy);
        // remove from Recipe Repository

        recipeRepository.delete(recipeToRemove);


    }
}
