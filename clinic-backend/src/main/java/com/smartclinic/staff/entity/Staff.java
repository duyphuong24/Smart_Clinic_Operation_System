package com.smartclinic.staff.entity;

import com.smartclinic.common.entity.BaseEntity;
import com.smartclinic.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "staff",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_staff_user_id", columnNames = "user_id"),
                @UniqueConstraint(name = "uq_staff_employee_code", columnNames = "employee_code")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Staff extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "employee_code", nullable = false, length = 50)
    private String employeeCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "staff_type", nullable = false, length = 50)
    private StaffType staffType;

    @Column(name = "hired_date")
    private LocalDate hiredDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private StaffStatus status = StaffStatus.ACTIVE;

    public String getFullName() {
        return user != null ? user.getFullName() : null;
    }
}