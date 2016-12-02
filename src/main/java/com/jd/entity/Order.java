package com.jd.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by hanlei6 on 16-11-24.
 */
@Setter
@Getter
public class Order implements Serializable {
    private static final long serialVersionUID = 2486710181245916441L;
    private String id;
    private String sku;
    private Integer quantity;
    private String skuName;
    private Date created;
    private Date modified;
}
