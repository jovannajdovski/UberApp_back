package com.uberTim12.ihor.service.route.interfaces;


import com.uberTim12.ihor.model.route.Location;
import net.minidev.json.parser.ParseException;

import java.io.IOException;
import java.net.MalformedURLException;

public interface ILocationService {

    Location save(Location location);

    Double calculateDistance(Location location1, Location location2) throws IOException, ParseException;
}
