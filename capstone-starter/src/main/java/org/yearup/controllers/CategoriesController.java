package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize; // For role-based access
import org.springframework.web.bind.annotation.*;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.List;

// ✅ This class is now a REST controller
@RestController

// ✅ Base URL for this controller: http://localhost:8080/categories
@RequestMapping("/categories")

// ✅ Allow cross-origin requests from frontend (like http://127.0.0.1:5500)
@CrossOrigin
public class CategoriesController
{
    private final CategoryDao categoryDao;
    private final ProductDao productDao;

    // ✅ Automatically inject DAO dependencies via constructor
    @Autowired
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao)
    {
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }

    // ✅ GET /categories — anyone can view all categories
    @GetMapping
    public List<Category> getAll()
    {
        return categoryDao.getAllCategories(); // Retrieves all categories from DB
    }

    // ✅ GET /categories/{id} — fetch a single category by ID
    @GetMapping("{id}")
    public Category getById(@PathVariable int id)
    {
        return categoryDao.getById(id); // Get specific category by ID
    }

    // ✅ GET /categories/{id}/products — fetch all products in this category
    @GetMapping("{categoryId}/products")
    public List<Product> getProductsById(@PathVariable int categoryId)
    {
        return productDao.getByCategoryId(categoryId); // Filtered by categoryId
    }

    // ✅ POST /categories — create a new category (admin only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Requires ADMIN role
    public Category addCategory(@RequestBody Category category)
    {
        categoryDao.create(category); // Save new category
        return category;
    }

    // ✅ PUT /categories/{id} — update category (admin only)
    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')") // Requires ADMIN role
    public void updateCategory(@PathVariable int id, @RequestBody Category category)
    {
        categoryDao.update(id, category); // Update category by ID
    }

    // ✅ DELETE /categories/{id} — delete category (admin only)
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')") // Requires ADMIN role
    public void deleteCategory(@PathVariable int id)
    {
        categoryDao.delete(id); // Delete category by ID
    }
}
