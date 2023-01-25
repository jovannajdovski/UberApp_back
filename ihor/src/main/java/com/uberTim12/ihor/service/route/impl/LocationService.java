package com.uberTim12.ihor.service.route.impl;


import com.uberTim12.ihor.dto.route.RouteStep;
import com.uberTim12.ihor.model.route.Location;

import com.uberTim12.ihor.repository.route.ILocationRepository;
import com.uberTim12.ihor.service.base.impl.JPAService;
import com.uberTim12.ihor.service.route.interfaces.ILocationService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class LocationService extends JPAService<Location> implements ILocationService {
    private final ILocationRepository locationRepository;

    @Autowired
    LocationService(ILocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    protected JpaRepository<Location, Integer> getEntityRepository() {
        return locationRepository;
    }

    @Override
    public Double calculateDistance(Location location1, Location location2) throws IOException, ParseException {
        URL url = new URL("https://routing.openstreetmap.de/routed-car/route/v1/driving/"+location1.getLongitude().toString()+","+location1.getLatitude().toString()+";"+location2.getLongitude().toString()+","+location1.getLatitude().toString()+"?geometries=geojson&overview=false&alternatives=true&steps=true");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        InputStream inputStream = con.getInputStream();
        String response = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response);
        JSONArray routes = (JSONArray) json.get("routes");
        JSONObject route=(JSONObject) routes.get(0);
        Number distance=route.getAsNumber("distance");
        return distance.doubleValue()/1000.0;
    }
    @Override
    public Double calculateEstimatedTime(Location location1, Location location2) throws IOException, ParseException{
        URL url = new URL("https://routing.openstreetmap.de/routed-car/route/v1/driving/"+location1.getLongitude().toString()+","+location1.getLatitude().toString()+";"+location2.getLongitude().toString()+","+location1.getLatitude().toString()+"?geometries=geojson&overview=false&alternatives=true&steps=true");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        InputStream inputStream = con.getInputStream();
        String response = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response);
        JSONArray routes = (JSONArray) json.get("routes");
        JSONObject route=(JSONObject) routes.get(0);
        Number durationSec=route.getAsNumber("duration");
        return durationSec.doubleValue()/60.0;
    }

    @Override
    public List<RouteStep> getSteps(Location location1, Location location2) throws IOException, ParseException {
        URL url = new URL("https://routing.openstreetmap.de/routed-car/route/v1/driving/"+location1.getLongitude().toString()+","+location1.getLatitude().toString()+";"+location2.getLongitude().toString()+","+location1.getLatitude().toString()+"?geometries=geojson&overview=false&alternatives=true&steps=true");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        InputStream inputStream = con.getInputStream();
        String response = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        List<RouteStep> stepList=new ArrayList<>();

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response);
        JSONArray routes = (JSONArray) json.get("routes");
        JSONObject route=(JSONObject) routes.get(0);
        JSONArray legs=(JSONArray) route.get("legs");
        JSONObject leg=(JSONObject) legs.get(0);
        JSONArray steps=(JSONArray) leg.get("steps");
        JSONObject step;
        JSONObject geometry;
        JSONArray coordinates;
        JSONArray location;
        Number latitude, longitude;;
        for(int i=0;i<steps.size();i++)
        {
            step=(JSONObject) steps.get(i);
            geometry=(JSONObject) step.get("geometry");
            coordinates=(JSONArray) geometry.get("coordinates");
            for(int j=0;j<coordinates.size();j++)
            {
                location=(JSONArray) coordinates.get(j);
                latitude= (Number) location.get(0);
                longitude=(Number) location.get(1);
                stepList.add(new RouteStep(latitude.doubleValue(),longitude.doubleValue()));
            }

        }

        return stepList;
    }

}
