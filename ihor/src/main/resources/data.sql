INSERT INTO authority(name)
VALUES ('ROLE_ADMIN');

INSERT INTO authority(name)
VALUES ('ROLE_DRIVER');

INSERT INTO authority(name)
VALUES ('ROLE_PASSENGER');

INSERT INTO ihor (name, surname, profile_picture, telephone_number, email, address, password, is_blocked,is_active, authority_id)
VALUES ('User', 'Usser', null, '+3816563122', 'passenger@gmail.com', 'Gogoljeva 3', '$2a$12$j7iYyVb8oUctjiiiHN42eOkPFXPatyKNxuqeuEKpENA6F.RFTqJhy' , false, true, 3); --NekaSifra123
INSERT INTO passenger (id) values ( 1 );

INSERT INTO ihor (name, surname, profile_picture, telephone_number, email, address, password, is_blocked,is_active, authority_id)
VALUES ('User', 'Usser', null, '+3816563122', 'driver@gmail.com', 'Gogoljeva 3', '$2a$12$j7iYyVb8oUctjiiiHN42eOkPFXPatyKNxuqeuEKpENA6F.RFTqJhy' , false, true, 2); --NekaSifra123
INSERT INTO Driver (id) values ( 2 );

INSERT INTO ihor (name, surname, profile_picture, telephone_number, email, address, password, is_blocked,is_active, authority_id)
VALUES ('User', 'Usser', null, '+3816563122', 'admin@gmail.com', 'Gogoljeva 3', '$2a$12$j7iYyVb8oUctjiiiHN42eOkPFXPatyKNxuqeuEKpENA6F.RFTqJhy' , false, true, 1); --NekaSifra123
INSERT INTO Administrator (id) values ( 3 );

INSERT INTO ihor (name, surname, profile_picture, telephone_number, email, address, password, is_blocked,is_active, authority_id)

VALUES ('User', 'Usser', null, '+3816563122', 'perapera@gmail.com', 'Gogoljeva 3', '$2a$12$j7iYyVb8oUctjiiiHN42eOkPFXPatyKNxuqeuEKpENA6F.RFTqJhy' , false, true, 3); --NekaSifra123
INSERT INTO Passenger (id) values ( 4 );

INSERT INTO ihor (name, surname, profile_picture, telephone_number, email, address, password, is_blocked,is_active, authority_id)
VALUES ('User1', 'Usser', null, '+3816563122', 'driver1@gmail.com', 'Gogoljeva 3', '$2a$12$j7iYyVb8oUctjiiiHN42eOkPFXPatyKNxuqeuEKpENA6F.RFTqJhy' , false, true, 2); --NekaSifra123
INSERT INTO Driver (id) values ( 5 );

INSERT INTO ihor (name, surname, profile_picture, telephone_number, email, address, password, is_blocked,is_active, authority_id)
VALUES ('User2', 'Usser', null, '+3816563122', 'driver2@gmail.com', 'Gogoljeva 3', '$2a$12$j7iYyVb8oUctjiiiHN42eOkPFXPatyKNxuqeuEKpENA6F.RFTqJhy' , false, true, 2); --NekaSifra123
INSERT INTO Driver (id) values ( 6 );


INSERT INTO WORK_HOURS (START_TIME,END_TIME, DRIVER_ID) VALUES ('2023-01-20T09:50:59.161','2023-01-20T17:53:59.161', 6);
INSERT INTO WORK_HOURS (START_TIME,END_TIME, DRIVER_ID) VALUES ('2023-01-21T05:50:59.161','2023-01-21T14:53:59.161', 6);
INSERT INTO WORK_HOURS (START_TIME,END_TIME, DRIVER_ID) VALUES ('2023-01-20T09:50:59.161','2023-01-20T17:53:59.161', 5);
INSERT INTO WORK_HOURS (START_TIME,END_TIME, DRIVER_ID) VALUES ('2023-01-20T09:50:59.161','2023-01-20T17:53:59.161', 2);
-- INSERT INTO WORK_HOURS (START_TIME, DRIVER_ID) VALUES ('2023-01-26T10:50:59.161', 5);


INSERT INTO VEHICLE_TYPE ( VEHICLE_CATEGORY , PRICE_PER_KM ) VALUES ( 0, 300.0);


INSERT INTO LOCATION (address, latitude, longitude) VALUES ( 'Rackog 35', 45.237360,19.884690 );
INSERT INTO LOCATION (address, latitude, longitude) VALUES ( 'Bulevar Patrijaha Pavla 2', 45.239840,19.820620 );

INSERT INTO LOCATION (address, latitude, longitude) VALUES ( 'Branislava Borote 11', 45.252080,19.807280 );
INSERT INTO LOCATION (address, latitude, longitude) VALUES ( 'Bulevar Cara Lazara 90', 45.2405129,19.8265563 );

INSERT INTO PATH (startpoint_id, endpoint_id, distance) VALUES ( 4, 2, 550.0 );

INSERT INTO RIDE ( START_TIME , END_TIME , TOTAL_PRICE , DRIVER_ID , ESTIMATED_TIME , RIDE_STATUS , BABIES_ALLOWED , PETS_ALLOWED , VEHICLE_TYPE, IS_PANIC_ACTIVATED, SCHEDULED_TIME  )
VALUES ('2023-01-10T9:40:59.161', '2023-01-10T10:10:59.161', 200.0, 2, 32.0, 4, true, true, 1, false, '2023-01-10T12:52:06.134');

INSERT INTO RIDE (START_TIME , END_TIME , TOTAL_PRICE , DRIVER_ID , ESTIMATED_TIME , RIDE_STATUS , BABIES_ALLOWED , PETS_ALLOWED , VEHICLE_TYPE, IS_PANIC_ACTIVATED, SCHEDULED_TIME  )
VALUES ('2023-01-10T12:52:06.134', '2023-01-10T13:20:06.134', 200.0, 2, 32.0, 4, true, true, 1, false, '2023-01-10T12:52:06.134');

INSERT INTO RIDE (START_TIME , END_TIME , TOTAL_PRICE , DRIVER_ID , ESTIMATED_TIME , RIDE_STATUS , BABIES_ALLOWED , PETS_ALLOWED , VEHICLE_TYPE, IS_PANIC_ACTIVATED, SCHEDULED_TIME  )
VALUES ('2023-01-10T12:52:06.134', '2023-01-10T13:15:06.134', 300.0, 2, 33.0, 4, true, true, 1, false, '2023-01-10T12:52:06.134');

INSERT INTO RIDE (START_TIME, TOTAL_PRICE , DRIVER_ID , ESTIMATED_TIME , RIDE_STATUS , BABIES_ALLOWED , PETS_ALLOWED , VEHICLE_TYPE, IS_PANIC_ACTIVATED, SCHEDULED_TIME  )
VALUES ('2023-01-26T14:40:06.134', 300.0, 2, 13.0, 0, true, true, 1, false, '2023-01-26T14:40:06.134');


INSERT INTO PASSENGER_RIDE ( PASSENGER_ID , RIDE_ID ) VALUES (1,1);
INSERT INTO PASSENGER_RIDE ( PASSENGER_ID , RIDE_ID ) VALUES (1,2);
INSERT INTO PASSENGER_RIDE ( PASSENGER_ID , RIDE_ID ) VALUES (1,3);
INSERT INTO PASSENGER_RIDE ( PASSENGER_ID , RIDE_ID ) VALUES (1,4);

INSERT INTO RIDE_PATH ( RIDE_ID, PATH_ID) VALUES (1, 1);
INSERT INTO RIDE_PATH ( RIDE_ID, PATH_ID) VALUES (2, 1);
INSERT INTO RIDE_PATH ( RIDE_ID, PATH_ID) VALUES (3, 1);
INSERT INTO RIDE_PATH ( RIDE_ID, PATH_ID) VALUES (4, 1);

-- INSERT INTO ACTIVE_DRIVER(DRIVER_ID, LOCATION_ID) VALUES (5, 3);


INSERT INTO MESSAGE (SENDER_ID, RECEIVER_ID, CONTENT, SEND_TIME, TYPE, RIDE_ID) VALUES (2,1, 'Voznja 3 vozac->passenger', '2022-12-10T10:55:06.134', 2, 3);
INSERT INTO MESSAGE (SENDER_ID, RECEIVER_ID, CONTENT, SEND_TIME, TYPE, RIDE_ID) VALUES (2,1, 'Voznja 2 vozac->passenger', '2022-12-11T10:52:06.134', 2, 2);
INSERT INTO MESSAGE (SENDER_ID, RECEIVER_ID, CONTENT, SEND_TIME, TYPE, RIDE_ID) VALUES (2,1, 'Voznja 1 vozac->passenger', '2022-12-12T09:52:06.134', 1, 1);
INSERT INTO MESSAGE (SENDER_ID, RECEIVER_ID, CONTENT, SEND_TIME, TYPE, RIDE_ID) VALUES (1,2, 'Voznja 2 passenger->vozac', '2022-12-11T10:56:06.134', 1, 2);
INSERT INTO MESSAGE ( SENDER_ID, RECEIVER_ID, CONTENT, SEND_TIME, TYPE, RIDE_ID) VALUES (1,2, 'Voznja 1 passenger->vozac', '2022-12-10T10:52:06.134', 2, 1);
INSERT INTO MESSAGE (SENDER_ID, RECEIVER_ID, CONTENT, SEND_TIME, TYPE, RIDE_ID) VALUES (1,2, 'Voznja 3 passenger->vozac', '2022-12-11T10:56:06.134', 1, 3);
INSERT INTO MESSAGE ( SENDER_ID, RECEIVER_ID, CONTENT, SEND_TIME, TYPE, RIDE_ID) VALUES (1,2, 'Voznja 1 passenger->vozac', '2022-12-10T10:52:06.134', 1, 1);
INSERT INTO MESSAGE ( SENDER_ID, RECEIVER_ID, CONTENT, SEND_TIME, TYPE, RIDE_ID) VALUES (2,4, 'Voznja 1 vozac->perapera', '2022-12-10T10:52:06.134', 1, 1);

INSERT INTO MESSAGE ( SENDER_ID, RECEIVER_ID, CONTENT, SEND_TIME, TYPE, RIDE_ID) VALUES (5,4, 'Ride is accepted', '2023-01-26 14:37:30.33892', 1, 4);
INSERT INTO MESSAGE (SENDER_ID, RECEIVER_ID, CONTENT, SEND_TIME, TYPE, RIDE_ID) VALUES (5,1, 'Ride is accepted', '2023-01-26 14:37:30.344918', 1, 4);
INSERT INTO MESSAGE ( SENDER_ID, RECEIVER_ID, CONTENT, SEND_TIME, TYPE, RIDE_ID) VALUES (5,1, 'cao', '2023-01-26 14:37:40.727059', 1, 4);
INSERT INTO MESSAGE ( SENDER_ID, RECEIVER_ID, CONTENT, SEND_TIME, TYPE, RIDE_ID) VALUES (5,4, 'cao cao', '2023-01-26 14:37:46.889548', 1, 4);

INSERT INTO MESSAGE ( SENDER_ID, RECEIVER_ID, CONTENT, SEND_TIME, TYPE, RIDE_ID) VALUES (5,null, 'driver1->live support', '2023-01-24 14:37:30.33892', 1, null);
INSERT INTO MESSAGE (SENDER_ID, RECEIVER_ID, CONTENT, SEND_TIME, TYPE, RIDE_ID) VALUES (null,1, 'live support ->passenger', '2023-01-23 14:37:30.344918', 1, null);
INSERT INTO MESSAGE ( SENDER_ID, RECEIVER_ID, CONTENT, SEND_TIME, TYPE, RIDE_ID) VALUES (null, 5, 'live support ->driver1', '2023-01-22 14:37:40.727059', 1, null);
INSERT INTO MESSAGE ( SENDER_ID, RECEIVER_ID, CONTENT, SEND_TIME, TYPE, RIDE_ID) VALUES (5,null, 'driver1->live support prva poruka', '2023-01-21 14:37:46.889548', 1, null);



INSERT INTO VEHICLE (vehicle_model, vehicle_Type_id, registration_plate, seats, location_id, babies_allowed, pets_allowed, driver_id)
values ('Seat', 1, 'SA157AJ', 5, 1, TRUE,TRUE, 2);

INSERT INTO VEHICLE (vehicle_model, vehicle_Type_id, registration_plate, seats, location_id, babies_allowed, pets_allowed, driver_id)
values ('VW', 1, 'NS157AJ', 5, 3, TRUE,TRUE, 5);

INSERT INTO VEHICLE (vehicle_model, vehicle_Type_id, registration_plate, seats, location_id, babies_allowed, pets_allowed, driver_id)
values ('Reno', 1, 'BG157AJ', 5, 4, TRUE,TRUE, 6);


INSERT INTO USER_ACTIVATION (USER_ID, TOKEN, CREATION_DATE, EXPIRY_DATE) VALUES (1, 123456, '2022-12-10T22:40:59.161', '2023-12-10T22:40:59.161');
INSERT INTO USER_ACTIVATION (USER_ID, TOKEN, CREATION_DATE, EXPIRY_DATE) VALUES (2, 654321, '2022-12-10T22:40:59.161', '2022-12-10T22:40:59.161');


INSERT INTO REVIEW (driver_comment, driver_rate, vehicle_comment, vehicle_rate, passenger_id, ride_id)
values ('Bravo', 4.0, 'BRAVOOOO', 5.0, 1, 1);

INSERT INTO DRIVER_REVIEWS(DRIVER_ID, REVIEWS_ID) VALUES (2, 1);
INSERT INTO VEHICLE_REVIEWS(VEHICLE_ID, REVIEWS_ID) VALUES (1, 1);

