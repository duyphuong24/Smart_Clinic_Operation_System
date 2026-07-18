package com.smartclinic.servicecatalog.dto;

import com.smartclinic.servicecatalog.entity.ServiceType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceCatalogRequest {

    @NotBlank
    @Size(max = 50)
    private String serviceCode;

    @NotBlank
    @Size(max = 150)
    private String name;

    @NotNull
    private ServiceType type;

    @NotNull
    @DecimalMin(value = "0.00")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal price;

    private boolean active = true;
}