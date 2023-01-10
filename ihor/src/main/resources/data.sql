INSERT INTO authority(name)
VALUES ('ROLE_ADMIN');

INSERT INTO authority(name)
VALUES ('ROLE_DRIVER');

INSERT INTO authority(name)
VALUES ('ROLE_PASSENGER');

INSERT INTO ihor (name, surname, profile_picture, telephone_number, email, address, password, is_blocked, is_active, authority_id)
VALUES ('Miki', 'Mikic', null, '+3816563122', 'miki@email.com', 'Gogoljeva 3', '$2a$12$wwQ9iYllQax/wUT99HgAUeeHCdBk7tOT/oqo7usLtFLbMn9lpiL.S' , false, false, 3); --mikimilane
INSERT INTO passenger (id) values ( 1 );

INSERT INTO ihor (name, surname, profile_picture, telephone_number, email, address, password, is_blocked,is_active, authority_id)
VALUES ('User', 'Usser', 'ffdfdfedf', '+3816563122', 'user@example.com', 'Gogoljeva 3', '$2a$12$54NuKbL1rlXtwBClcpte8eOPgisxCI4crc0yZTjavUfy5NaJm5KWC' , false, false, 3); --user
INSERT INTO passenger (id) values ( 2 );

INSERT INTO ihor (name, surname, profile_picture, telephone_number, email, address, password, is_blocked,is_active, authority_id)
VALUES ('Zoki', 'Zokic', 'ffdfdfedf', '+3816563122', 'zoki@email.com', 'Gogoljeva 3', '$2a$12$k0VBF/xbK6qti0euHe/abeIsKILktIi3YIGjoj6.P1nFBEs99GwJS' , false, false, 2); --zokizoki
INSERT INTO driver (id) VALUES ( 3 );

INSERT INTO ihor (name, surname, profile_picture, telephone_number, email, address, password, is_blocked,is_active, authority_id)
VALUES ('Nikola', 'Luburic', 'ffdfdfedf', '+3816563122', 'lubura@email.com', 'Pavla Papa 3', '$2a$12$ckc7Wu4C74twLpbouzDR7.TOtgPUtTP5AYxrOqgbG20DpchYILUEa' , false, false, 2); --lubura
INSERT INTO driver (id) VALUES ( 4 );

INSERT INTO ihor (name, surname, profile_picture, telephone_number, email, address, password, is_blocked,is_active, authority_id)
VALUES ('Ognjen', 'Ognjenovic', 'ffdfdfedf', '+3816563122', 'ogi@email.com', 'Preradoviceva 3', '$2a$12$q7XhID2ACdawcTbvO/H6qejeiRv8u9zGfAw5xWAKCqrACBjXHV79W' , false, false, 2); --ogiogi
INSERT INTO driver (id) VALUES ( 5 );

INSERT INTO ihor (name, surname, profile_picture, telephone_number, email, address, password, is_blocked,is_active, authority_id) --ogiogi
VALUES ('Roki', 'Rokvic', 'ffdfdfedf', '+3816563122', 'roki@email.com', 'Preradoviceva 3', '$2a$12$JpTYHr7H/68YlZWuNdQ41.CJCAyIg0xPfrjKMd3EuW6ipcy2sy83m' , false, false, 1); --rokiroki
INSERT INTO administrator (id) VALUES ( 6 );

INSERT INTO DRIVER_DOCUMENT (NAME, PICTURE, DRIVER_ID) VALUES ('Vozačka dozvola', 'U3dhZ2dlciByb2Nrcw=', 3);
INSERT INTO WORK_HOURS (START_TIME, END_TIME, DRIVER_ID) VALUES ('2023-01-10T10:40:59.161', '2023-01-10T12:30:59.161', 3);
INSERT INTO WORK_HOURS (START_TIME, END_TIME, DRIVER_ID) VALUES ('2023-01-10T08:40:59.161', '2023-01-10T12:30:59.161', 4);
INSERT INTO WORK_HOURS (START_TIME, END_TIME, DRIVER_ID) VALUES ('2023-01-10T10:40:59.161', '2023-01-10T12:30:59.161', 5);
INSERT INTO WORK_HOURS (START_TIME, DRIVER_ID) VALUES ('2023-01-10T13:10:59.161', 3);
INSERT INTO WORK_HOURS (START_TIME, DRIVER_ID) VALUES ('2023-01-10T13:00:59.161', 4);
INSERT INTO WORK_HOURS (START_TIME, DRIVER_ID) VALUES ('2023-01-10T12:40:59.161', 5);

INSERT INTO VEHICLE_TYPE ( VEHICLE_CATEGORY , PRICE_PER_KM ) VALUES ( 0, 300.0);
INSERT INTO VEHICLE_TYPE ( VEHICLE_CATEGORY , PRICE_PER_KM ) VALUES ( 1, 400.0);

INSERT INTO LOCATION (address, latitude, longitude) VALUES ( 'Rackog 35', 45.237360,19.884690 );
INSERT INTO LOCATION (address, latitude, longitude) VALUES ( 'Bulevar Patrijaha Pavla 2', 45.239840,19.820620 );
INSERT INTO LOCATION (address, latitude, longitude) VALUES ( 'Branislava Borote 11', 45.252080,19.807280 );
INSERT INTO LOCATION (address, latitude, longitude) VALUES ( 'Bulevar Cara Lazara 10', 45.248211,19.850460 );

INSERT INTO PATH (startpoint_id, endpoint_id, distance) VALUES ( 1, 2, 550.0 );

INSERT INTO RIDE ( START_TIME , END_TIME , TOTAL_PRICE , DRIVER_ID , ESTIMATED_TIME , RIDE_STATUS , BABIES_ALLOWED , PETS_ALLOWED , VEHICLE_TYPE, IS_PANIC_ACTIVATED  )
VALUES ('2023-01-10T9:40:59.161', '2023-01-10T10:10:59.161', 200.0, 3, 32.0, 4, true, true, 1, false);

INSERT INTO RIDE (START_TIME , END_TIME , TOTAL_PRICE , DRIVER_ID , ESTIMATED_TIME , RIDE_STATUS , BABIES_ALLOWED , PETS_ALLOWED , VEHICLE_TYPE, IS_PANIC_ACTIVATED  )
VALUES ('2023-01-10T12:52:06.134', '2023-01-10T13:20:06.134', 200.0, 4, 32.0, 4, true, true, 1, false);

INSERT INTO RIDE (START_TIME , END_TIME , TOTAL_PRICE , DRIVER_ID , ESTIMATED_TIME , RIDE_STATUS , BABIES_ALLOWED , PETS_ALLOWED , VEHICLE_TYPE, IS_PANIC_ACTIVATED  )
VALUES ('2023-01-10T12:52:06.134', '2023-01-10T13:15:06.134', 300.0, 3, 33.0, 4, true, true, 1, false);

INSERT INTO RIDE ( START_TIME , TOTAL_PRICE , DRIVER_ID , ESTIMATED_TIME , RIDE_STATUS , BABIES_ALLOWED , PETS_ALLOWED , VEHICLE_TYPE, IS_PANIC_ACTIVATED  )
VALUES ('2023-01-10T13:30:06.134',  200.0, 3, 32.0, 1, true, true, 1, false);

INSERT INTO RIDE ( START_TIME , TOTAL_PRICE , DRIVER_ID , ESTIMATED_TIME , RIDE_STATUS , BABIES_ALLOWED , PETS_ALLOWED , VEHICLE_TYPE, IS_PANIC_ACTIVATED  )
VALUES ('2023-01-10T13:30:06.134', 200.0, 4, 12.0, 1, true, true, 1, false);

INSERT INTO RIDE (START_TIME , TOTAL_PRICE , DRIVER_ID , ESTIMATED_TIME , RIDE_STATUS , BABIES_ALLOWED , PETS_ALLOWED , VEHICLE_TYPE, IS_PANIC_ACTIVATED  )
VALUES ('2023-01-10T14:30:06.134', 200.0, 4, 32.0, 2, false, false, 2, false);



INSERT INTO PASSENGER_RIDE ( PASSENGER_ID , RIDE_ID ) VALUES (1,1);
INSERT INTO PASSENGER_RIDE ( PASSENGER_ID , RIDE_ID ) VALUES (1,2);
INSERT INTO PASSENGER_RIDE ( PASSENGER_ID , RIDE_ID ) VALUES (2,3);
INSERT INTO PASSENGER_RIDE ( PASSENGER_ID , RIDE_ID ) VALUES (2,4);
INSERT INTO PASSENGER_RIDE ( PASSENGER_ID , RIDE_ID ) VALUES (1,5);
INSERT INTO PASSENGER_RIDE ( PASSENGER_ID , RIDE_ID ) VALUES (2,6);

INSERT INTO RIDE_PATH ( RIDE_ID, PATH_ID) VALUES (1, 1);
INSERT INTO RIDE_PATH ( RIDE_ID, PATH_ID) VALUES (2, 1);
INSERT INTO RIDE_PATH ( RIDE_ID, PATH_ID) VALUES (3, 1);
INSERT INTO RIDE_PATH ( RIDE_ID, PATH_ID) VALUES (4, 1);
INSERT INTO RIDE_PATH ( RIDE_ID, PATH_ID) VALUES (5, 1);
INSERT INTO RIDE_PATH ( RIDE_ID, PATH_ID) VALUES (6, 1);

INSERT INTO ACTIVE_DRIVER(DRIVER_ID, LOCATION_ID) VALUES (3, 2);
INSERT INTO ACTIVE_DRIVER(DRIVER_ID, LOCATION_ID) VALUES (4, 3);
INSERT INTO ACTIVE_DRIVER(DRIVER_ID, LOCATION_ID) VALUES (5, 4);

INSERT INTO MESSAGE (SENDER_ID, RECEIVER_ID, CONTENT, SEND_TIME, TYPE, RIDE_ID) VALUES (3,1, 'Voznja 1 moja', '2022-12-10T10:55:06.134', 2, 1);
INSERT INTO MESSAGE (SENDER_ID, RECEIVER_ID, CONTENT, SEND_TIME, TYPE, RIDE_ID) VALUES (3,1, 'Voznja 2 moja', '2022-12-11T10:52:06.134', 2, 2);
INSERT INTO MESSAGE (SENDER_ID, RECEIVER_ID, CONTENT, SEND_TIME, TYPE, RIDE_ID) VALUES (3,1, 'Voznja 1 moja2', '2022-12-12T09:52:06.134', 1, 1);
INSERT INTO MESSAGE (SENDER_ID, RECEIVER_ID, CONTENT, SEND_TIME, TYPE, RIDE_ID) VALUES (1,3, 'Voznja 2 tudja', '2022-12-11T10:56:06.134', 1, 2);
INSERT INTO MESSAGE ( SENDER_ID, RECEIVER_ID, CONTENT, SEND_TIME, TYPE, RIDE_ID) VALUES (1,3, 'Voznja 1 tudja', '2022-12-10T10:52:06.134', 2, 1);

INSERT INTO panic (ride_id, user_id, reason, time) VALUES (1, 1, 'Driver is crazy', '2022-12-10T09:52:06.134');

INSERT INTO VEHICLE (vehicle_model, vehicle_Type_id, registration_plate, seats, location_id, babies_allowed, pets_allowed, driver_id)
values ('Seat', 1, 'SA157AJ', 5,2,TRUE,TRUE,3);

INSERT INTO VEHICLE (vehicle_model, vehicle_Type_id, registration_plate, seats, location_id, babies_allowed, pets_allowed, driver_id)
values ('Volkswagen pas', 1, 'NS157AJ', 5,2,TRUE,TRUE,4);

INSERT INTO VEHICLE (vehicle_model, vehicle_Type_id, registration_plate, seats, location_id, babies_allowed, pets_allowed, driver_id)
values ('TOYOTA', 2, 'BG157AJ', 4,2,TRUE,FALSE,5);

INSERT INTO USER_ACTIVATION (USER_ID, CREATION_DATE, EXPIRY_DATE) VALUES (1, '2022-12-10T22:40:59.161', '2023-12-10T22:40:59.161')


-- INSERT INTO location (id, address, latitude, longitude)
-- VALUES ( 10, 'mika', 3.44, 5.44);