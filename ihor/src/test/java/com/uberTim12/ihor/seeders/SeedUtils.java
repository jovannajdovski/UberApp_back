package com.uberTim12.ihor.seeders;

import com.uberTim12.ihor.model.ride.Favorite;
import com.uberTim12.ihor.model.ride.RideStatus;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.vehicle.VehicleCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.HashSet;

@Component
public class SeedUtils {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int insertVehicle(String model, int vehicleTypeID, String registrationPlate, int seats, int LocationID, boolean babiesAllowed, boolean petsAllowed, int driverID) {
        final String sql = "INSERT INTO VEHICLE (vehicle_model, vehicle_Type_id, registration_plate, seats, location_id, babies_allowed, pets_allowed, driver_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, model);
            ps.setString(2, String.valueOf(vehicleTypeID));
            ps.setString(3, registrationPlate);
            ps.setString(4, String.valueOf(seats));
            ps.setString(5, String.valueOf(LocationID));
            ps.setString(6, String.valueOf(babiesAllowed));
            ps.setString(7, String.valueOf(petsAllowed));
            ps.setString(8, String.valueOf(driverID));
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public int insertActiveDriver(int driverID, int locationID) {
        final String sql = "INSERT INTO ACTIVE_DRIVER(DRIVER_ID, LOCATION_ID) VALUES (?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, String.valueOf(driverID));
            ps.setString(2, String.valueOf(locationID));
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public int insertWorkHours(LocalDateTime startTime, int driverID) {
        final String sql = "INSERT INTO WORK_HOURS (START_TIME,END_TIME, DRIVER_ID) VALUES (?, ?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, String.valueOf(startTime));
            ps.setString(2, null);
            ps.setString(3, String.valueOf(driverID));
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public int insertRide(LocalDateTime startTime, LocalDateTime endTime, double totalPrice, int driverID,
                           double estimatedTime, RideStatus rideStatus, boolean babyTransport, boolean petTransport,
                           int vehicleTypeID, boolean isPanic, LocalDateTime scheduledTime) {
        final String sql = "INSERT INTO RIDE ( START_TIME , END_TIME , TOTAL_PRICE , DRIVER_ID , ESTIMATED_TIME , RIDE_STATUS , BABIES_ALLOWED , PETS_ALLOWED , VEHICLE_TYPE, IS_PANIC_ACTIVATED, SCHEDULED_TIME) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, String.valueOf(startTime));
            ps.setString(2, String.valueOf(endTime));
            ps.setString(3, String.valueOf(totalPrice));
            ps.setString(4, String.valueOf(driverID));
            ps.setString(5, String.valueOf(estimatedTime));
            ps.setString(6, String.valueOf(rideStatus.ordinal()));
            ps.setString(7, String.valueOf(babyTransport));
            ps.setString(8, String.valueOf(petTransport));
            ps.setString(9, String.valueOf(vehicleTypeID));
            ps.setString(10, String.valueOf(isPanic));
            ps.setString(11, String.valueOf(scheduledTime));

            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public void addPassengerToRide(int passengerID, int rideID) {
        final String sql = "INSERT INTO PASSENGER_RIDE ( PASSENGER_ID , RIDE_ID ) VALUES (?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, String.valueOf(passengerID));
            ps.setString(2, String.valueOf(rideID));
            return ps;
        });
    }

    public int insertPath(int startLocationID, int endLocationID, double distance) {
        final String sql = "INSERT INTO PATH (startpoint_id, endpoint_id, distance) VALUES (?, ?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, String.valueOf(startLocationID));
            ps.setString(2, String.valueOf(endLocationID));
            ps.setString(3, String.valueOf(distance));
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public void addPathToRide(int rideId, int pathId) {
        final String sql = "INSERT INTO RIDE_PATH (RIDE_ID, PATH_ID) VALUES (?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, String.valueOf(rideId));
            ps.setString(2, String.valueOf(pathId));
            return ps;
        });
    }

    public void addPathToFavorite(int pathID, int favoriteID) {
        final String sql = "INSERT INTO FAVORITE_PATH ( FAVORITE_ID , PATH_ID ) VALUES (?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, String.valueOf(favoriteID));
            ps.setString(2, String.valueOf(pathID));
            return ps;
        });
    }

    public void addPassengerToFavorite(int passengerID, int favoriteID) {
        final String sql = "INSERT INTO PASSENGER_FAVORITE ( PASSENGER_ID , FAVORITE_ID ) VALUES (?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, String.valueOf(passengerID));
            ps.setString(2, String.valueOf(favoriteID));
            return ps;
        });
    }


    public int insertFavorite(String favoriteName, boolean babyTransport, boolean petTransport, int vehicleCategory) {
        final String sql = "INSERT INTO FAVORITE ( FAVORITE_NAME , BABIES_ALLOWED , PETS_ALLOWED, VEHICLE_CATEGORY) " +
                "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, String.valueOf(favoriteName));
            ps.setString(2, String.valueOf(babyTransport));
            ps.setString(3, String.valueOf(petTransport));
            ps.setString(4, String.valueOf(vehicleCategory));

            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }
}
