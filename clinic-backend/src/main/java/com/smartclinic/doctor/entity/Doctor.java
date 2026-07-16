package com.smartclinic.doctor.entity;

import com.smartclinic.common.entity.BaseEntity;
import com.smartclinic.masterdata.entity.Room;
import com.smartclinic.masterdata.entity.Specialty;
import com.smartclinic.staff.entity.Staff;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "doctors",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_doctors_staff_id", columnNames = "staff_id"),
                @UniqueConstraint(name = "uq_doctors_license_no", columnNames = "license_no")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id", nullable = false)
    private Specialty specialty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "default_room_id")
    private Room defaultRoom;

    @Column(name = "license_no", nullable = false, length = 100)
    private String licenseNo;

    @Column(name = "consultation_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal consultationFee;

    @Column(name = "bio", columnDefinition = "nvarchar(max)")
    private String bio;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;
}