
# 🛍️ E-Commerce API – Year Up Capstone

This Spring Boot RESTful API simulates an online e-commerce backend. It supports login-based authentication, secure role-based access, product browsing, category management, and personalized shopping carts.

This capstone project modernizes the traditional CLI-based system by introducing a layered web architecture using Spring Boot, JWT authentication, and DAO-based database access with MySQL.

---

## 🌐 Overview

- Users can log in and browse products or categories
- Admins can manage product and category records
- Authenticated users can manage their shopping cart
- All routes are secured using JWT and Spring Security

---

## 📦 Features Implemented

### 🔐 **Authentication & Authorization**
- **Login via JWT Token**
- Roles: `ROLE_USER`, `ROLE_ADMIN`
- Route protection with `@PreAuthorize` annotations

**Example: Secured Controller Method**
```java
@PreAuthorize("hasRole('ROLE_ADMIN')")
@PostMapping("/products")
public Product addProduct(@RequestBody Product product) {
    return productDao.create(product);
}
```

---

### 📁 **Categories**
- `GET /categories` – Public
- `GET /categories/{id}` – Public
- `POST /categories` – Admin only
- `PUT /categories/{id}` – Admin only
- `DELETE /categories/{id}` – Admin only

---

### 📦 **Products**
- `GET /products` – Public; supports filters:
  - Category
  - Price range
  - Search by name/description
- `GET /products/{id}` – Public
- `POST /products` – Admin only
- `PUT /products/{id}` – Admin only
- `DELETE /products/{id}` – Admin only

---

### 🛒 **Shopping Cart**
- `GET /cart` – View the current user’s cart
- `POST /cart/add` – Add an item
- `DELETE /cart/remove` – Remove an item
- `DELETE /cart/clear` – Empty the cart

**Example: Adding to Cart**
```java
@PostMapping("/cart/add")
public void addToCart(@RequestBody CartItem item, Principal principal) {
    String username = principal.getName();
    User user = userDao.getByUserName(username);
    cartDao.addToCart(user.getId(), item);
}
```

---

### 🧑 **User Profiles**
- Profile records are created automatically when a user is added to the database
- Managed by `ProfileDao` and implemented via `MySqlProfileDao`

**Fix Highlight:**
- A missing implementation for `ProfileDao` was added:
```java
@Override
public Profile create(Profile profile) {
    String sql = "...";
    jdbcTemplate.update(...);
    return profile;
}
```

---

## 🧱 Architecture & Structure

| Layer         | Components                                                                 |
|---------------|---------------------------------------------------------------------------|
| **Controllers** | `AuthenticationController`, `CategoriesController`, `ProductsController`, `ShoppingCartController` |
| **DAOs**        | `ProductDao`, `CategoryDao`, `UserDao`, `ProfileDao`, `ShoppingCartDao` |
| **Models**      | `User`, `Product`, `Category`, `ShoppingCart`, `CartItem`, `Profile`     |
| **Security**    | `TokenProvider`, `JWTFilter`, Spring Security Config                    |
| **Database**    | MySQL + Spring JDBC (`BasicDataSource`)                                 |

---

## 🧪 How to Test with Postman

1. **Login**  
   `POST /login`  
   Body:
   ```json
   {
     "username": "admin",
     "password": "password123"
   }
   ```

2. **Use the Token**  
   Copy the token from the response and include in headers:
   ```
   Authorization: Bearer <your_token_here>
   ```

3. **Try Out Endpoints**:
   - Public:
     - `GET /products`
     - `GET /categories`
   - Authenticated:
     - `GET /cart`
     - `POST /cart/add`
   - Admin-only:
     - `POST /products`
     - `POST /categories`

---

## ⚙️ How to Run

1. Update database credentials in `application.properties`
2. Run `YearUpECommerceApplication.java`
3. Use Postman or any REST client to access the endpoints

---

## ✅ Completed Functionality

- [x] JWT login and authentication
- [x] Role-based route protection
- [x] Full product and category management
- [x] Shopping cart persistence by user
- [x] Profile creation using DAO
- [x] Full Postman test coverage

---



## Code Implementation Highlights

📘 Capstone Task Completion Code Highlights
Phase 1 - CategoriesController ✅

Implemented full CRUD operations for categories with security roles.

🔹 Public GET endpoint:


@GetMapping
@PreAuthorize("permitAll()")
public List<Category> getAll() {
    return categoryDao.getAllCategories();
}

🔹 ADMIN-only POST endpoint:

@PostMapping
@PreAuthorize("hasRole('ROLE_ADMIN')")
public Category add(@RequestBody Category category) {
    return categoryDao.create(category);
}

Phase 2 - Fixed Product Bugs 🔧
🔹 Fixed incorrect search/filter by updating SQL logic in MySqlProductsDao.

@Override
public List<Product> search(int categoryId, String search, BigDecimal minPrice, BigDecimal maxPrice)
{
    String sql = """
        SELECT * FROM products
        WHERE (category_id = ? OR ? = 0)
        AND name LIKE ?
        AND price >= ?
        AND price <= ?
    """;
    String searchValue = "%" + search + "%";
    return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Product.class),
        categoryId, categoryId, searchValue, minPrice, maxPrice);
}

🔹 Fixed update bug that created duplicate entries instead of updating existing product:

@Override
public void update(int productId, Product product) {
    String sql = """
        UPDATE products SET
        name = ?, description = ?, price = ?, category_id = ?,
        color = ?, stock = ?, image_url = ?, featured = ?
        WHERE product_id = ?
    """;
    jdbcTemplate.update(sql,
        product.getName(), product.getDescription(), product.getPrice(),
        product.getCategoryId(), product.getColor(), product.getStock(),
        product.getImageUrl(), product.isFeatured(), productId);
}

Optional Phase 3 - Shopping Cart 🛒
Implemented endpoints for managing shopping cart items (GET, POST, PUT, DELETE).

@PostMapping("/products/{productId}")
public void addToCart(@PathVariable int productId, Principal principal) {
    String username = principal.getName();
    User user = userDao.getByUserName(username);
    shoppingCartDao.addToCart(user.getUserId(), productId);
}


@DeleteMapping
public void clearCart(Principal principal) {
    User user = userDao.getByUserName(principal.getName());
    shoppingCartDao.clearCart(user.getUserId());
}

Optional Phase 4 - User Profile 👤
Enabled logged-in users to GET and PUT their profile information.

@GetMapping
public Profile getProfile(Principal principal) {
    User user = userDao.getByUserName(principal.getName());
    return profileDao.getByUserId(user.getUserId());
}


@PutMapping
public Profile updateProfile(@RequestBody Profile profile, Principal principal) {
    User user = userDao.getByUserName(principal.getName());
    profile.setUserId(user.getUserId());
    return profileDao.update(profile);
}

