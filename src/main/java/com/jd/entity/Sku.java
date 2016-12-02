package com.jd.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by hanlei6 on 16-11-24.
 */
@Setter
@Getter
public class Sku implements Serializable {

    private static final long serialVersionUID = -1766199649277415835L;
    private String id;
    private String name;
    private Integer quantity;
}
