INSERT INTO authority(name)
VALUES ('ADMIN');

INSERT INTO authority(name)
VALUES ('DRIVER');

INSERT INTO authority(name)
VALUES ('PASSENGER');


INSERT INTO ihor (name, surname, profile_picture, telephone_number, email, address, password, is_blocked, is_active, authority_id)
VALUES ('Miki', 'Mikic', 'ffdfdfedf', '+3816563122', 'miki@email.com', 'Gogoljeva 3', 'mikimilane' , false, false, 3);
INSERT INTO passenger (id) values ( 1 );

INSERT INTO ihor (name, surname, profile_picture, telephone_number, email, address, password, is_blocked,is_active, authority_id)
VALUES ('User', 'Usser', 'ffdfdfedf', '+3816563122', 'user@example.com', 'Gogoljeva 3', 'user' , false, false, 3);
INSERT INTO passenger (id) values ( 2 );

INSERT INTO ihor (name, surname, profile_picture, telephone_number, email, address, password, is_blocked,is_active, authority_id)
VALUES ('Zoki', 'Zokic', 'ffdfdfedf', '+3816563122', 'zoki@email.com', 'Gogoljeva 3', 'zokizoki' , false, false, 2);
INSERT INTO driver (id) VALUES ( 3 );

INSERT INTO ihor (name, surname, profile_picture, telephone_number, email, address, password, is_blocked,is_active, authority_id)
VALUES ('Nikola', 'Luburic', 'ffdfdfedf', '+3816563122', 'lubura@email.com', 'Pavla Papa 3', 'lubura' , false, false, 2);
INSERT INTO driver (id) VALUES ( 4 );

INSERT INTO ihor (name, surname, profile_picture, telephone_number, email, address, password, is_blocked,is_active, authority_id)
VALUES ('Ognjen', 'Ognjenovic', 'ffdfdfedf', '+3816563122', 'ogi@email.com', 'Preradoviceva 3', 'ogiogi' , false, false, 2);
INSERT INTO driver (id) VALUES ( 5 );

INSERT INTO DRIVER_DOCUMENT (NAME, PICTURE, DRIVER_ID) VALUES ('Vozačka dozvola', 'U3dhZ2dlciByb2Nrcw=', 2);

INSERT INTO WORK_HOURS (START_TIME, END_TIME, DRIVER_ID) VALUES ('2023-01-09T10:40:59.161Z', '2023-01-09T14:30:59.161Z', 3);

INSERT INTO WORK_HOURS (START_TIME, END_TIME, DRIVER_ID) VALUES ('2023-01-09T08:40:59.161Z', '2023-01-09T14:30:59.161Z', 4);

INSERT INTO WORK_HOURS (START_TIME, END_TIME, DRIVER_ID) VALUES ('2023-01-09T10:40:59.161Z', '2023-01-09T14:30:59.161Z', 5);

INSERT INTO WORK_HOURS (START_TIME, END_TIME, DRIVER_ID) VALUES ('2023-01-09T15:10:59.161Z', 3);

INSERT INTO WORK_HOURS (START_TIME, END_TIME, DRIVER_ID) VALUES ('2023-01-09T15:00:59.161Z', 4);

INSERT INTO WORK_HOURS (START_TIME, END_TIME, DRIVER_ID) VALUES ('2023-01-09T14:40:59.161Z', 5);

INSERT INTO VEHICLE_TYPE ( VEHICLE_CATEGORY , PRICE_PER_KM ) VALUES ( 1, 300.0);
INSERT INTO VEHICLE_TYPE ( VEHICLE_CATEGORY , PRICE_PER_KM ) VALUES ( 2, 400.0);

INSERT INTO LOCATION (address, latitude, longitude) VALUES ( 'Bulevar Cara Lazara 44', 34.5554, 33.445 );
INSERT INTO LOCATION (address, latitude, longitude) VALUES ( 'Bulevar Patrijaha Pavla 12', 33.4234, 21.445 );

INSERT INTO PATH (startpoint_id, endpoint_id, distance) VALUES ( 1, 2, 550.0 );

INSERT INTO RIDE ( START_TIME , END_TIME , TOTAL_PRICE , DRIVER_ID , ESTIMATED_TIME , RIDE_STATUS , BABIES_ALLOWED , PETS_ALLOWED , VEHICLE_TYPE, IS_PANIC_ACTIVATED  )
VALUES ('2023-01-09T11:40:59.161Z', '2023-01-09T12:10:59.161Z', 200.0, 3, 32.0, 5, true, true, 1, false);

INSERT INTO RIDE (START_TIME , END_TIME , TOTAL_PRICE , DRIVER_ID , ESTIMATED_TIME , RIDE_STATUS , BABIES_ALLOWED , PETS_ALLOWED , VEHICLE_TYPE, IS_PANIC_ACTIVATED  )
VALUES ('2022-12-10T09:52:06.134Z', '2022-12-10T10:52:06.134Z', 200.0, 4, 32.0, 5, true, true, 1, false);

INSERT INTO RIDE (START_TIME , END_TIME , TOTAL_PRICE , DRIVER_ID , ESTIMATED_TIME , RIDE_STATUS , BABIES_ALLOWED , PETS_ALLOWED , VEHICLE_TYPE, IS_PANIC_ACTIVATED  )
VALUES ('2022-12-12T12:52:06.134Z', '2022-12-10T13:15:06.134Z', 300.0, 3, 33.0, 5, true, true, 1, false);

INSERT INTO RIDE ( START_TIME , TOTAL_PRICE , DRIVER_ID , ESTIMATED_TIME , RIDE_STATUS , BABIES_ALLOWED , PETS_ALLOWED , VEHICLE_TYPE, IS_PANIC_ACTIVATED  )
VALUES ('2022-12-10T19:00:06.134Z',  200.0, 3, 32.0, 1, true, true, 1, false);

INSERT INTO RIDE ( START_TIME , TOTAL_PRICE , DRIVER_ID , ESTIMATED_TIME , RIDE_STATUS , BABIES_ALLOWED , PETS_ALLOWED , VEHICLE_TYPE, IS_PANIC_ACTIVATED  )
VALUES ('2022-12-10T22:52:06.134Z', 200.0, 4, 32.0, 1, true, true, 1, false);

INSERT INTO RIDE (START_TIME , TOTAL_PRICE , DRIVER_ID , ESTIMATED_TIME , RIDE_STATUS , BABIES_ALLOWED , PETS_ALLOWED , VEHICLE_TYPE, IS_PANIC_ACTIVATED  )
VALUES ('2022-12-10T23:52:06.134Z', 200.0, 5, 32.0, 1, false, false, 2, false);



INSERT INTO PASSENGER_RIDE ( PASSENGER_ID , RIDE_ID ) VALUES (1,2);

INSERT INTO PASSENGER_RIDE ( PASSENGER_ID , RIDE_ID ) VALUES (1, 1);


INSERT INTO RIDE_PATH ( RIDE_ID, PATH_ID) VALUES (1, 1 );




INSERT INTO MESSAGE (SENDER_ID, RECEIVER_ID, CONTENT, SEND_TIME, TYPE, RIDE_ID) VALUES (2,1, 'Voznja 1 moja', '2022-12-10T10:55:06.134Z', 2, 1);
INSERT INTO MESSAGE (SENDER_ID, RECEIVER_ID, CONTENT, SEND_TIME, TYPE, RIDE_ID) VALUES (2,1, 'Voznja 2 moja', '2022-12-11T10:52:06.134Z', 2, 2);
INSERT INTO MESSAGE (SENDER_ID, RECEIVER_ID, CONTENT, SEND_TIME, TYPE, RIDE_ID) VALUES (2,1, 'Voznja 1 moja2', '2022-12-12T09:52:06.134Z', 1, 1);
INSERT INTO MESSAGE (SENDER_ID, RECEIVER_ID, CONTENT, SEND_TIME, TYPE, RIDE_ID) VALUES (1,2, 'Voznja 2 tudja', '2022-12-11T10:56:06.134Z', 1, 2);
INSERT INTO MESSAGE ( SENDER_ID, RECEIVER_ID, CONTENT, SEND_TIME, TYPE, RIDE_ID) VALUES (1,2, 'Voznja 1 tudja', '2022-12-10T10:52:06.134Z', 2, 1);

INSERT INTO panic (ride_id, user_id, reason, time)
VALUES (1, 1, 'Driver is crazy', '2022-12-10T09:52:06.134Z');

INSERT INTO VEHICLE (vehicle_model, vehicle_Type_id, registration_plate, seats, location_id, babies_allowed, pets_allowed, driver_id)
values ('Seat', 1, 'SA157AJ', 5,2,TRUE,TRUE,3);

INSERT INTO VEHICLE (vehicle_model, vehicle_Type_id, registration_plate, seats, location_id, babies_allowed, pets_allowed, driver_id)
values ('Volkswagen pas', 1, 'NS157AJ', 5,2,TRUE,TRUE,4);

INSERT INTO VEHICLE (vehicle_model, vehicle_Type_id, registration_plate, seats, location_id, babies_allowed, pets_allowed, driver_id)
values ('TOYOTA', 2, 'BG157AJ', 4,2,TRUE,FALSE,5);

INSERT INTO USER_ACTIVATION (USER_ID, CREATION_DATE, EXPIRY_DATE) VALUES (1, '2022-12-10T22:40:59.161Z', '2023-12-10T22:40:59.161Z')


-- INSERT INTO location (id, address, latitude, longitude)
-- VALUES ( 10, 'mika', 3.44, 5.44);