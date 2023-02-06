package com.uberTim12.ihor.seeders;

import com.uberTim12.ihor.model.ride.RideStatus;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.users.Administrator;
import com.uberTim12.ihor.model.users.Authority;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.model.vehicle.VehicleCategory;
import com.uberTim12.ihor.model.vehicle.VehicleType;
import com.uberTim12.ihor.repository.ride.IActiveDriverRepository;
import com.uberTim12.ihor.repository.ride.IFavoriteRepository;
import com.uberTim12.ihor.repository.ride.IRideRepository;
import com.uberTim12.ihor.repository.route.ILocationRepository;
import com.uberTim12.ihor.repository.route.IPathRepository;
import com.uberTim12.ihor.repository.users.*;
import com.uberTim12.ihor.repository.vehicle.IVehicleRepository;
import com.uberTim12.ihor.repository.vehicle.IVehicleTypeRepository;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@TestPropertySource("classpath:application-test.properties")
@ExtendWith({SpringExtension.class})
public class Seeder implements BeforeEachCallback, AfterEachCallback {

    private static final Lock lock = new ReentrantLock();
    private static int callCounter = 0;


    private SeedUtils seedUtils;

    private static IAuthorityRepository authorityRepository;
    private static IPassengerRepository passengerRepository;
    private static IVehicleTypeRepository vehicleTypeRepository;
    private static ILocationRepository locationRepository;
    private static IVehicleRepository vehicleRepository;
    private static IDriverRepository driverRepository;
    private static IAdministratorRepository administratorRepository;
    private static IWorkHoursRepository workHoursRepository;
    private static IRideRepository rideRepository;
    private static IActiveDriverRepository activeDriverRepository;
    private static IPathRepository pathRepository;
    private static IFavoriteRepository favoriteRepository;

    private void initializeDependencies(final ExtensionContext context) {
        authorityRepository = SpringExtension.getApplicationContext(context).getBean(IAuthorityRepository.class);
        passengerRepository = SpringExtension.getApplicationContext(context).getBean(IPassengerRepository.class);
        vehicleTypeRepository = SpringExtension.getApplicationContext(context).getBean(IVehicleTypeRepository.class);
        vehicleRepository = SpringExtension.getApplicationContext(context).getBean(IVehicleRepository.class);
        locationRepository = SpringExtension.getApplicationContext(context).getBean(ILocationRepository.class);
        driverRepository = SpringExtension.getApplicationContext(context).getBean(IDriverRepository.class);
        administratorRepository = SpringExtension.getApplicationContext(context).getBean(IAdministratorRepository.class);
        workHoursRepository = SpringExtension.getApplicationContext(context).getBean(IWorkHoursRepository.class);
        rideRepository = SpringExtension.getApplicationContext(context).getBean(IRideRepository.class);
        seedUtils = SpringExtension.getApplicationContext(context).getBean(SeedUtils.class);
        activeDriverRepository = SpringExtension.getApplicationContext(context).getBean(IActiveDriverRepository.class);
        pathRepository = SpringExtension.getApplicationContext(context).getBean(IPathRepository.class);
        favoriteRepository = SpringExtension.getApplicationContext(context).getBean(IFavoriteRepository.class);
    }

    public static int ADMIN_ID;
    public static String ADMIN_EMAIL = "admin@gmail.com";

    private void seed() {
        seedAuthority();
        seedPassengers();
        seedVehicleTypes();
        seedLocations();
        seedDrivers();
        seedVehicles();
     //   seedActiveDrivers();
        seedAdmin();
     //   seedWorkHours();
    //    seedRides();
    }

    private void dropAll() {
        administratorRepository.deleteAll();
        activeDriverRepository.deleteAll();
        rideRepository.deleteAll();
        administratorRepository.deleteAll();
        workHoursRepository.deleteAll();
        driverRepository.deleteAll();
        passengerRepository.deleteAll();
        vehicleRepository.deleteAll();
        vehicleTypeRepository.deleteAll();
        favoriteRepository.deleteAll();
        pathRepository.deleteAll();
        locationRepository.deleteAll();
        authorityRepository.deleteAll();
    }

    private Authority authorityPassenger;
    private Authority authorityDriver;
    private Authority authorityAdmin;

    private void seedAuthority() {
        authorityPassenger = new Authority("ROLE_PASSENGER");
        authorityDriver = new Authority("ROLE_DRIVER");
        authorityAdmin = new Authority("ROLE_ADMIN");
        authorityPassenger = authorityRepository.save(authorityPassenger);
        authorityDriver = authorityRepository.save(authorityDriver);
        authorityAdmin = authorityRepository.save(authorityAdmin);
    }

    public static int PASSENGER_FIRST_ID;
    public static String PASSENGER_FIRST_EMAIL = "peki@gmail.com";
    public static int PASSENGER_SECOND_ID;
    public static String PASSENGER_SECOND_EMAIL = "maki@gmail.com";
    public static int PASSENGER_THIRD_ID;
    public static String PASSENGER_THIRD_EMAIL = "preki@gmail.com";
    public static int PASSENGER_FOURTH_ID;
    public static String PASSENGER_FOURTH_EMAIL = "miki@gmail.com";

    public static String PASSWORD = "NekaSifra123";


    private void seedPassengers() {
        var firstPassenger = new Passenger("Petar", "Petrovic", null, "3816563122",
                "peki@gmail.com", "Bulevar oslobodjenja 3", "$2a$12$RLLj4K6KkYJ1kRAmXS5Ui.aSeLRRceYOO2pUhhSIx2RyL2P.zAaMW",
                authorityPassenger, new HashSet<>(), false, true, new HashSet<>(), new HashSet<>());
        var secondPassenger = new Passenger("Marko", "Nikolic", null, "3816563122",
                "maki@gmail.com", "Berislava Berica 10", "$2a$12$RLLj4K6KkYJ1kRAmXS5Ui.aSeLRRceYOO2pUhhSIx2RyL2P.zAaMW",
                authorityPassenger, new HashSet<>(), false, true, new HashSet<>(), new HashSet<>());
        var thirdPassenger = new Passenger("Predrag", "Radonjic", null, "3816563122",
                "preki@gmail.com", "Pavla Papa 5", "$2a$12$RLLj4K6KkYJ1kRAmXS5Ui.aSeLRRceYOO2pUhhSIx2RyL2P.zAaMW",
                authorityPassenger, new HashSet<>(), false, true, new HashSet<>(), new HashSet<>());
        var fourthPassenger = new Passenger("Miroslav", "Markovic", null, "3816563122",
                "miki@gmail.com", "Gogoljeva 3", "$2a$12$RLLj4K6KkYJ1kRAmXS5Ui.aSeLRRceYOO2pUhhSIx2RyL2P.zAaMW",
                authorityPassenger, new HashSet<>(), false, true, new HashSet<>(), new HashSet<>());

        PASSENGER_FIRST_ID = passengerRepository.save(firstPassenger).getId();
        PASSENGER_SECOND_ID = passengerRepository.save(secondPassenger).getId();
        PASSENGER_THIRD_ID = passengerRepository.save(thirdPassenger).getId();
        PASSENGER_FOURTH_ID = passengerRepository.save(fourthPassenger).getId();
    }

    public static int VEHICLETYPE_FIRST_ID;
    public static int VEHICLETYPE_SECOND_ID;
    public static int VEHICLETYPE_THIRD_ID;

    private void seedVehicleTypes() {
        var firstVehicleType = new VehicleType(VehicleCategory.STANDARD, 50d);
        var secondVehicleType = new VehicleType(VehicleCategory.VAN, 100d);
        var thirdVehicleType = new VehicleType(VehicleCategory.LUXURY, 200d);

        VEHICLETYPE_FIRST_ID = vehicleTypeRepository.save(firstVehicleType).getId();
        VEHICLETYPE_SECOND_ID = vehicleTypeRepository.save(secondVehicleType).getId();
        VEHICLETYPE_THIRD_ID = vehicleTypeRepository.save(thirdVehicleType).getId();
    }

    public static int LOCATION_FIRST_ID;
    public static int LOCATION_SECOND_ID;
    public static int LOCATION_THIRD_ID;
    public static int LOCATION_FOURTH_ID;

    private void seedLocations() {
        var firstLocation = new Location("Berislava berica 5", 3.4214421, 7.35345);
        var secondLocation = new Location("Save kovacevica 20", 8.232, 17.2313231);
        var thirdLocation = new Location("Rumenacka 23", 3.423423, 5.456456);
        var fourthLocation = new Location("Kisacka 14", 4.345435, 7.567567);

        LOCATION_FIRST_ID = locationRepository.save(firstLocation).getId();
        LOCATION_SECOND_ID = locationRepository.save(secondLocation).getId();
        LOCATION_THIRD_ID = locationRepository.save(thirdLocation).getId();
        LOCATION_FOURTH_ID = locationRepository.save(fourthLocation).getId();
    }

    public static int DRIVER_FIRST_ID;
    public static String DRIVER_FIRST_EMAIL = "staja@gmail.com";
    public static int DRIVER_SECOND_ID;
    public static String DRIVER_SECOND_EMAIL = "marinko@gmail.com";
    public static int DRIVER_THIRD_ID;
    public static String DRIVER_THIRD_EMAIL = "sica@gmail.com";
    public static int DRIVER_FOURTH_ID;
    public static String DRIVER_FOURTH_EMAIL = "bogdan@gmail.com";

    public void seedDrivers() {
        var firstDriver = new Driver("Zivorad", "Stajic", null, "3816563122",
                "staja@gmail.com", "Bulevar oslobodjenja 3", "$2a$12$RLLj4K6KkYJ1kRAmXS5Ui.aSeLRRceYOO2pUhhSIx2RyL2P.zAaMW",
                authorityDriver, new HashSet<>(), false, true, new HashSet<>(), new HashSet<>(), null, new HashSet<>());
        var secondDriver = new Driver("Miroslav", "Marinkovic", null, "3816563122",
                "marinko@gmail.com", "Berislava Berica 10", "$2a$12$RLLj4K6KkYJ1kRAmXS5Ui.aSeLRRceYOO2pUhhSIx2RyL2P.zAaMW",
                authorityDriver, new HashSet<>(), false, true, new HashSet<>(), new HashSet<>(), null, new HashSet<>());
        var thirdDriver = new Driver("Zoran", "Sipka", null, "3816563122",
                "sica@gmail.com", "Pavla Papa 5", "$2a$12$RLLj4K6KkYJ1kRAmXS5Ui.aSeLRRceYOO2pUhhSIx2RyL2P.zAaMW",
                authorityDriver, new HashSet<>(), false, true, new HashSet<>(), new HashSet<>(), null, new HashSet<>());
        var fourthDriver = new Driver("Djordje", "Bogdanovic", null, "3816563122",
                "bogdan@gmail.com", "Gogoljeva 3", "$2a$12$RLLj4K6KkYJ1kRAmXS5Ui.aSeLRRceYOO2pUhhSIx2RyL2P.zAaMW",
                authorityDriver, new HashSet<>(), false, true, new HashSet<>(), new HashSet<>(), null, new HashSet<>());

        DRIVER_FIRST_ID = driverRepository.save(firstDriver).getId();
        DRIVER_SECOND_ID = driverRepository.save(secondDriver).getId();
        DRIVER_THIRD_ID = driverRepository.save(thirdDriver).getId();
        DRIVER_FOURTH_ID = driverRepository.save(fourthDriver).getId();
    }

    public static int VEHICLE_FIRST_ID;
    public static int VEHICLE_SECOND_ID;
    public static int VEHICLE_THIRD_ID;
    public static int VEHICLE_FOURTH_ID;



    private void seedVehicles() {
        VEHICLE_FIRST_ID = seedUtils.insertVehicle("Skoda", VEHICLETYPE_FIRST_ID, "SO-1234-CX", 4, LOCATION_FIRST_ID, true, true, DRIVER_FIRST_ID);
        VEHICLE_SECOND_ID = seedUtils.insertVehicle("Porsche", VEHICLETYPE_SECOND_ID, "SO-3245-AU", 4, LOCATION_SECOND_ID, true, false, DRIVER_SECOND_ID);
        VEHICLETYPE_THIRD_ID = seedUtils.insertVehicle("Wolksvagen", VEHICLETYPE_THIRD_ID, "SO-3234-SF", 4, LOCATION_THIRD_ID, false, true, DRIVER_THIRD_ID);
        VEHICLE_FOURTH_ID = seedUtils.insertVehicle("Tesla", VEHICLETYPE_SECOND_ID, "SO-6435-KS", 4, LOCATION_FOURTH_ID, false, false, DRIVER_FOURTH_ID);
    }

    private void seedAdmin() {
        var admin = new Administrator("Admin", "Admin", null, "3816563122",
                "admin@gmail.com", "Gogoljeva 3", "$2a$12$RLLj4K6KkYJ1kRAmXS5Ui.aSeLRRceYOO2pUhhSIx2RyL2P.zAaMW",
                authorityAdmin, new HashSet<>(), false, true);

        ADMIN_ID = administratorRepository.save(admin).getId();
    }



    private void seedActiveDrivers() {
        seedUtils.insertActiveDriver(DRIVER_FIRST_ID, LOCATION_FIRST_ID);
    }


    public static int WORKHOURS_FIRST_ID;

    private void seedWorkHours() {
        WORKHOURS_FIRST_ID = seedUtils.insertWorkHours(LocalDateTime.now(), DRIVER_FIRST_ID);
    }


    public static int RIDE_FIRST_ID;
    private void seedRides() {
        RIDE_FIRST_ID = seedUtils.insertRide(LocalDateTime.now().minusDays(1),LocalDateTime.now().minusDays(1).plusMinutes(30),
                300.0, DRIVER_FIRST_ID, 30.0, RideStatus.FINISHED, true,
                true, VEHICLETYPE_FIRST_ID, false, LocalDateTime.now().minusDays(1));
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        lock.lock();

        if (callCounter == 0) {
            context.getRoot().getStore(GLOBAL).put("Seeder Before", this);

            initializeDependencies(context);
        }

        callCounter++;
        lock.unlock();

        seed();
    }

    @Override
    public void afterEach(ExtensionContext context) {
        lock.lock();
        callCounter--;

        if (callCounter == 0) {
            context.getRoot().getStore(GLOBAL).put("Seeder After", this);
        }

        lock.unlock();

        dropAll();
    }
}
