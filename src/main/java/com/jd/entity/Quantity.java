package com.jd.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by hanlei6 on 16-11-24.
 */
@Setter
@Getter
public class Quantity implements Serializable {
    private static final long serialVersionUID = 237981177344980091L;
    private String sku;
    private Integer quantity;
}
