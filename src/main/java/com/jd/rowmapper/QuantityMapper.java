package com.jd.rowmapper;

import com.jd.entity.Quantity;
import org.springframework.jdbc.core.RowMapper;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by hanlei6 on 16-11-24.
 */
public class QuantityMapper implements RowMapper<Quantity> {
    @Override
    public Quantity mapRow(ResultSet resultSet, int i) throws SQLException {
        Quantity quantity = new Quantity();
        quantity.setQuantity(resultSet.getInt("quantity"));
        quantity.setSku(resultSet.getString("sku"));
        return quantity;
    }
}
