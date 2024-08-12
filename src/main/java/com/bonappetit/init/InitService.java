package com.bonappetit.init;

import com.bonappetit.model.entity.Category;
import com.bonappetit.model.entity.CategoryName;
import com.bonappetit.repo.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class InitService implements CommandLineRunner {
    private final CategoryRepository categoryRepository;

    public InitService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        long countCategories = this.categoryRepository.count();
        if (countCategories > 0) {
            return;
        }

        List<Category> categoriesToInsert = Arrays.stream(CategoryName.values())
                .map(Category::new).collect(toList());
        this.categoryRepository.saveAll(categoriesToInsert);


    }
}
