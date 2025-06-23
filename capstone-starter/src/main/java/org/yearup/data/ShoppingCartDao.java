package org.yearup.data;

import org.yearup.models.ShoppingCart;

public interface ShoppingCartDao
{
    ShoppingCart getCartByUserId(int userId);

    void addToCart(int userId, int productId);

    void updateQuantity(int userId, int productId, int quantity);

    void clearCart(int userId);
}
