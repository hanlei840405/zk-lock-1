package com.jd.rowmapper;

import com.jd.entity.Sku;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by hanlei6 on 16-11-24.
 */
public class SkuMapper implements RowMapper<Sku> {
    @Override
    public Sku mapRow(ResultSet resultSet, int i) throws SQLException {
        Sku sku = new Sku();
        sku.setId(resultSet.getString("id"));
        sku.setName(resultSet.getString("name"));
        sku.setQuantity(resultSet.getInt("quantity"));
        return sku;
    }
}
