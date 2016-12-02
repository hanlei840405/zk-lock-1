package com.jd.rowmapper;

import com.jd.entity.Order;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by hanlei6 on 16-11-24.
 */
public class OrderMapper implements RowMapper<Order> {
    @Override
    public Order mapRow(ResultSet resultSet, int i) throws SQLException {
        Order order = new Order();
        order.setId(resultSet.getString("id"));
        order.setQuantity(resultSet.getInt("quantity"));
        order.setSku(resultSet.getString("sku"));
        order.setSkuName(resultSet.getString("skuName"));
        order.setCreated(resultSet.getDate("created"));
        order.setModified(resultSet.getDate("modified"));
        return order;
    }
}
