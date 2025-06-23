package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MySqlShoppingCartDao implements ShoppingCartDao
{
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public MySqlShoppingCartDao(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ShoppingCart getCartByUserId(int userId)
    {
        String sql = "SELECT sc.product_id, sc.quantity, p.* " +
                "FROM shopping_cart sc " +
                "JOIN products p ON sc.product_id = p.product_id " +
                "WHERE sc.user_id = ?";

        List<ShoppingCartItem> items = jdbcTemplate.query(sql, (rowSet, rowNum) ->
        {
            Product product = new Product(
                                rowSet.getInt("product_id"),
                                rowSet.getString("name"),
                                rowSet.getBigDecimal("price"),
                                rowSet.getInt("category_id"),
                                rowSet.getString("description"),
                                rowSet.getString("color"),
                                rowSet.getInt("stock"),
                    rowSet.getBoolean("featured"),
                    rowSet.getString("image_url")
                        );

            int quantity = rowSet.getInt("quantity");

            return new ShoppingCartItem(product, quantity);
        }, userId);

        Map<Integer, ShoppingCartItem> itemMap = new HashMap<>();
        for (ShoppingCartItem item : items)
        {
            itemMap.put(item.getProduct().getProductId(), item);
        }

        return new ShoppingCart(itemMap);
    }

    @Override
    public void addToCart(int userId, int productId)
    {
        String selectSql = "SELECT quantity FROM shopping_cart WHERE user_id = ? AND product_id = ?";
        Integer currentQuantity = jdbcTemplate.query(selectSql, rs ->
        {
            if (rs.next())
                return rs.getInt("quantity");
            return null;
        }, userId, productId);

        if (currentQuantity == null)
        {
            String insertSql = "INSERT INTO shopping_cart (user_id, product_id, quantity) VALUES (?, ?, 1)";
            jdbcTemplate.update(insertSql, userId, productId);
        }
        else
        {
            String updateSql = "UPDATE shopping_cart SET quantity = quantity + 1 WHERE user_id = ? AND product_id = ?";
            jdbcTemplate.update(updateSql, userId, productId);
        }
    }

    @Override
    public void updateQuantity(int userId, int productId, int quantity)
    {
        String sql = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?";
        jdbcTemplate.update(sql, quantity, userId, productId);
    }

    @Override
    public void clearCart(int userId)
    {
        String sql = "DELETE FROM shopping_cart WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }
}
