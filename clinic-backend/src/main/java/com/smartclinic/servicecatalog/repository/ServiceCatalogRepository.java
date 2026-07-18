package com.smartclinic.servicecatalog.repository;

import com.smartclinic.servicecatalog.entity.ServiceCatalog;
import com.smartclinic.servicecatalog.entity.ServiceType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceCatalogRepository extends JpaRepository<ServiceCatalog, Long> {

    boolean existsByServiceCodeIgnoreCase(String serviceCode);

    Optional<ServiceCatalog> findByServiceCodeIgnoreCase(String serviceCode);

    List<ServiceCatalog> findByActiveTrue();

    List<ServiceCatalog> findByType(ServiceType type);

    List<ServiceCatalog> findByTypeAndActiveTrue(ServiceType type);
}
