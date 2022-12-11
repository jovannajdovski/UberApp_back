INSERT INTO ihor (name, surname, profile_picture, telephone_number, email, address, password, is_blocked,is_active)
VALUES ('Miki', 'Mikic', 'ffdfdfedf', '+3816563122', 'miki@email.com', 'Gogoljeva 3', 'mikimilane' , false, false);
INSERT INTO passenger (id) values ( 1 );

INSERT INTO ihor (id,name, surname, profile_picture, telephone_number, email, address, password, is_blocked,is_active)
VALUES (123,'User', 'Usser', 'ffdfdfedf', '+3816563122', 'user@example.com', 'Gogoljeva 3', 'user' , false, false);
INSERT INTO passenger (id) values ( 123 );

INSERT INTO ihor (name, surname, profile_picture, telephone_number, email, address, password, is_blocked,is_active)
VALUES ('Zoki', 'Zokic', 'ffdfdfedf', '+3816563122', 'zoki@email.com', 'Gogoljeva 3', 'zokizoki' , false, false);
INSERT INTO driver (id) VALUES ( 2 );

INSERT INTO DRIVER_DOCUMENT (NAME, PICTURE, DRIVER_ID) VALUES ('Vozačka dozvola', 'U3dhZ2dlciByb2Nrcw=', 2);

INSERT INTO WORK_HOURS (START_TIME, END_TIME, DRIVER_ID) VALUES ('2022-12-10T22:40:59.161Z', '2022-12-10T22:40:59.161Z', 2);

INSERT INTO VEHICLE_TYPE ( VEHICLE_CATEGORY , PRICE_PER_KM ) VALUES ( 1, 300.0);

INSERT INTO LOCATION (address, latitude, longitude) VALUES ( 'Bulevar Cara Lazara 44', 34.5554, 33.445 );
INSERT INTO LOCATION (address, latitude, longitude) VALUES ( 'Bulevar Patrijaha Pavla 12', 33.4234, 21.445 );

INSERT INTO PATH (startpoint_id, endpoint_id, distance) VALUES ( 1, 2, 550.0 );

INSERT INTO RIDE ( START_TIME , END_TIME , TOTAL_PRICE , DRIVER_ID , ESTIMATED_TIME , RIDE_STATUS , BABIES_ALLOWED , PETS_ALLOWED , VEHICLE_TYPE, IS_PANIC_ACTIVATED  )
VALUES ('2022-12-10T09:52:06.134Z', '2023-12-10T10:52:06.134Z', 200.0, 2, 32.0, 1, true, true, 1, false);

INSERT INTO RIDE ( ID, START_TIME , END_TIME , TOTAL_PRICE , DRIVER_ID , ESTIMATED_TIME , RIDE_STATUS , BABIES_ALLOWED , PETS_ALLOWED , VEHICLE_TYPE, IS_PANIC_ACTIVATED  )
VALUES (123, '2022-12-10T09:52:06.134Z', '2022-12-10T10:52:06.134Z', 200.0, 2, 32.0, 1, true, true, 1, false);

INSERT INTO RIDE_PATH ( RIDE_ID, PATH_ID) VALUES (1, 1 );

INSERT INTO PASSENGER_RIDE ( PASSENGER_ID , RIDE_ID ) VALUES (1, 1);

INSERT INTO panic (ride_id, user_id, reason, time)
VALUES (1, 1, 'Driver is crazy', '2022-12-10T09:52:06.134Z');

INSERT INTO VEHICLE (vehicle_model, vehicle_Type_id, registration_plate, seats, location_id, babies_allowed, pets_allowed, driver_id)
values ('Seat', 1, 'SA157AJ', 5,2,TRUE,TRUE,2);

INSERT INTO USER_ACTIVATION (USER_ID, CREATION_DATE, EXPIRY_DATE) VALUES (1, '2022-12-10T22:40:59.161Z', '2023-12-10T22:40:59.161Z')


-- INSERT INTO location (id, address, latitude, longitude)
-- VALUES ( 10, 'mika', 3.44, 5.44);