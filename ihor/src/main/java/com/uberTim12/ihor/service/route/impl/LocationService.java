package com.uberTim12.ihor.service.route.impl;


import com.uberTim12.ihor.model.route.Location;

import com.uberTim12.ihor.repository.route.ILocationRepository;
import com.uberTim12.ihor.service.route.interfaces.ILocationService;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class LocationService implements ILocationService {

    private final ILocationRepository locationRepository;
    @Autowired
    LocationService(ILocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public Location save(Location location) {
        return locationRepository.saveAndFlush(location);
    }

    @Override
    public Double calculateDistance(Location location1, Location location2) throws IOException, ParseException {
        URL url = new URL("https://routing.openstreetmap.de/routed-car/route/v1/driving/"+location1.getLongitude().toString()+","+location1.getLatitude().toString()+";"+location2.getLongitude().toString()+","+location1.getLatitude().toString()+"?geometries=geojson&overview=false&alternatives=true&steps=true");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        String response=con.getResponseMessage();
// routes[0]/duration
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response);
        JSONObject routes=(JSONObject) json.get("routes");
        JSONObject route=(JSONObject) routes.get(1);
        Number durationSec=route.getAsNumber("duration");
        return durationSec.doubleValue();
        /*response = requests.get(f'https://routing.openstreetmap.de/routed-car/route/v1/driving/{self.departure[1]},{self.departure[0]};{self.destination[1]},{self.destination[0]}?geometries=geojson&overview=false&alternatives=true&steps=true')
        self.routeGeoJSON = response.json()*/
        //return 0.0; //TODO iz mape izvuci
    }

}
