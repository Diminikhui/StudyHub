package com.fintrack.config;

import com.fintrack.entity.Category;
import com.fintrack.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        List<String> defaults = List.of("Еда", "Транспорт", "Зарплата");

        for (String name : defaults) {
            categoryRepository.findByName(name)
                    .orElseGet(() -> categoryRepository.save(
                            Category.builder().name(name).build()
                    ));
        }
    }
}