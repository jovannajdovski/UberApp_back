package com.uberTim12.ihor.model.vehicle;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class VehicleType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated
    @Column(name = "vehicle_category", nullable = false)
    private VehicleCategory vehicleCategory;

    @Column(name = "price_per_km", nullable = false)
    private Double pricePerKM;


    public VehicleType(VehicleCategory vehicleCategory, Double pricePerKM) {
        this.vehicleCategory = vehicleCategory;
        this.pricePerKM = pricePerKM;
    }
}
