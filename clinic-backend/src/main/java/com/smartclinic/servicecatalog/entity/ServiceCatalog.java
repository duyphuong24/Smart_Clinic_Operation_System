package com.smartclinic.servicecatalog.entity;

import com.smartclinic.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "service_catalog")
@Getter
@Setter
public class ServiceCatalog extends BaseEntity {

    @Column(name = "service_code", nullable = false, unique = true, length = 50)
    private String serviceCode;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "type", nullable = false, length = 50)
    private String type; // CONSULTATION, LAB_TEST, PROCEDURE, MEDICINE

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
