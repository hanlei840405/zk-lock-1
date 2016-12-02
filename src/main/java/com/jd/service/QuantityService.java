package com.jd.service;

import com.jd.entity.Quantity;
import com.jd.rowmapper.QuantityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by hanlei6 on 16-11-24.
 */
@Service
public class QuantityService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Quantity get(String sku) {
        List<Quantity> quantities = jdbcTemplate.query("SELECT * from mall_quantity WHERE sku=?", preparedStatement -> {
            preparedStatement.setString(1, sku);
        }, new QuantityMapper());
        if (quantities.isEmpty()) {
            return null;
        }
        return quantities.get(0);
    }

    public int insert(Quantity quantity) {
        int value = jdbcTemplate.update("INSERT INTO mall_quantity VALUES (?,?)", preparedStatement -> {
            preparedStatement.setString(1, quantity.getSku());
            preparedStatement.setInt(2, quantity.getQuantity());
        });
        return value;
    }

    public int update(Quantity quantity) {
        int value = jdbcTemplate.update("UPDATE mall_quantity SET quantity=? WHERE sku=?", preparedStatement -> {
            preparedStatement.setInt(1, quantity.getQuantity());
            preparedStatement.setString(2, quantity.getSku());
        });
        return value;
    }
}
