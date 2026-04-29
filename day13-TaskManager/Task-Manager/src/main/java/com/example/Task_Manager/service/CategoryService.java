package com.example.Task_Manager.service;


import com.example.Task_Manager.dto.CategoryRequestDTO;
import com.example.Task_Manager.dto.CategoryResponseDTO;
import com.example.Task_Manager.exception.BadRequestException;
import com.example.Task_Manager.exception.ResourceNotFoundException;
import com.example.Task_Manager.model.Category;
import com.example.Task_Manager.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryService(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public CategoryResponseDTO getCategoryById(Long id) {
        return toResponseDTO(findCategoryEntity(id));
    }

    public CategoryResponseDTO createCategory(CategoryRequestDTO request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new BadRequestException("Category name already exists: " + request.getName());
        }
        Category category = modelMapper.map(request, Category.class);
        return toResponseDTO(categoryRepository.save(category));
    }

    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO request) {
        Category existing = findCategoryEntity(id);
        modelMapper.map(request, existing);
        return toResponseDTO(categoryRepository.save(existing));
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category", "id", id);
        }
        categoryRepository.deleteById(id);
    }

    public Category findCategoryEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }

    private CategoryResponseDTO toResponseDTO(Category category) {
        CategoryResponseDTO dto = modelMapper.map(category, CategoryResponseDTO.class);
        dto.setTaskCount(category.getTasks().size());
        return dto;
    }
}