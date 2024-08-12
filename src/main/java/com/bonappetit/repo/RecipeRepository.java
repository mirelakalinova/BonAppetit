package com.bonappetit.repo;

import com.bonappetit.model.dto.RecipeInfoDto;
import com.bonappetit.model.entity.Category;
import com.bonappetit.model.entity.Recipe;
import com.bonappetit.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findAllByCategory(Category c);

    List<RecipeInfoDto> findAllByAddedBy(User addedBy);

}
