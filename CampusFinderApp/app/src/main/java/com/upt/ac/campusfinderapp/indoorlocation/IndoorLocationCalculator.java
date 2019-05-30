package com.upt.ac.campusfinderapp.indoorlocation;

import android.content.Context;
import android.location.Location;

import com.tomtom.online.sdk.common.location.LatLng;
import com.upt.ac.campusfinderapp.model.WifiAccessPoint;
import com.upt.ac.campusfinderapp.utils.FileWriter;
import com.upt.ac.campusfinderapp.utils.WifiAccessPointRepository;
import java.util.List;
import io.indoorlocation.core.IndoorLocation;


class IndoorLocationCalculator {
    private static final double EARTH_RADIUS = 6378.137;
    private static final double K = (1 / ((2 * Math.PI / 360) * EARTH_RADIUS)) / 1000;

    private Context context;

    IndoorLocationCalculator(Context context){
        this.context = context;
    }

    IndoorLocation calculateIndoorLocationFromWifiAccessPointData(WifiAccessPoint[] wifiAccessPoints) {
        List<WifiAccessPoint> accessPoints = WifiAccessPointRepository.getInstance().getWifiAccessPoints();
        for(WifiAccessPoint accessPoint: accessPoints) {
            for(WifiAccessPoint wap: wifiAccessPoints){
                if(wap.getSSID().equals(accessPoint.getSSID())){
                    wap.setLatitude(accessPoint.getLatitude());
                    wap.setLongitude(accessPoint.getLongitude());
                }
            }
        }
        WifiAccessPoint wap1 = wifiAccessPoints[0];
        WifiAccessPoint wap2 = wifiAccessPoints[1];
        WifiAccessPoint wap3 = wifiAccessPoints[2];
        double distance1 = calculateDistanceFromDeviceToWifiAccessPoint(wap1.getLevel(), wap1.getFrequency());
        double distance2 = calculateDistanceFromDeviceToWifiAccessPoint(wap2.getLevel(), wap2.getFrequency());
        double distance3 = calculateDistanceFromDeviceToWifiAccessPoint(wap3.getLevel(), wap3.getFrequency());
//        Location location =
//                trilateration(wap1.getLatitude(), wap1.getLongitude(), distance1,
//                        wap2.getLatitude(), wap2.getLongitude(), distance2,
//                        wap3.getLatitude(), wap3.getLongitude(), distance3);
        Location location =
                trilateration(45.774124, 21.245351, 5,
                        45.774098, 21.245438, 5,
                        45.774022, 21.245366, 6);
        IndoorLocation indoorLocation = calculateIndoorLocation(wap1.getLatitude(), wap1.getLongitude(), distance1);
        FileWriter fileWriter = new FileWriter(context);
        //fileWriter.writeToExternalStorage(wap1, indoorLocation, distance1);
        fileWriter.writeToExternalStorage(wifiAccessPoints, indoorLocation, new double[]{distance1, distance2, distance3});
        return indoorLocation;
    }

    private double calculateDistanceFromDeviceToWifiAccessPoint(int signalLevelInDb, int freqInMHz){
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(signalLevelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }

    private IndoorLocation calculateIndoorLocation(double latitude, double longitude, double distance) {
        Double newLatitude = latitude + (distance * K);
        Double newLongitude = longitude + (distance * K) / Math.cos(newLatitude * (Math.PI / 180));
        return new IndoorLocation(WifiIndoorLocationProvider.class.getName(), newLatitude, newLongitude, 0.0, System.currentTimeMillis());
    }

    private Location trilateration(double p1x, double p1y, double d1,
                                   double p2x, double p2y, double d2,
                                   double p3x, double p3y, double d3) {

        double[] P1   = new double[2];
        double[] P2   = new double[2];
        double[] P3   = new double[2];
        double[] ex   = new double[2];
        double[] ey   = new double[2];
        double[] p3p1 = new double[2];
        double jval  = 0;
        double temp  = 0;
        double ival  = 0;
        double p3p1i = 0;
        double triptx;
        double tripty;
        double xval;
        double yval;
        double t1;
        double t2;
        double t3;
        double t;
        double exx;
        double d;
        double eyy;

        //TRANSALTE POINTS TO VECTORS
        //POINT 1
        P1[0] = p1x;
        P1[1] = p1y;
        //POINT 2
        P2[0] = p2x;
        P2[1] = p2y;
        //POINT 3
        P3[0] = p3x;
        P3[1] = p3y;

        //TRANSFORM THE METERS VALUE FOR THE MAP UNIT
        //DISTANCE BETWEEN POINT 1 AND MY LOCATION
        d1 = (d1 / 100000);
        //DISTANCE BETWEEN POINT 2 AND MY LOCATION
        d2 = (d2 / 100000);
        //DISTANCE BETWEEN POINT 3 AND MY LOCATION
        d3 = (d3 / 100000);

        for (int i = 0; i < P1.length; i++) {
            t1   = P2[i];
            t2   = P1[i];
            t    = t1 - t2;
            temp += (t*t);
        }
        d = Math.sqrt(temp);
        for (int i = 0; i < P1.length; i++) {
            t1    = P2[i];
            t2    = P1[i];
            exx   = (t1 - t2)/(Math.sqrt(temp));
            ex[i] = exx;
        }
        for (int i = 0; i < P3.length; i++) {
            t1      = P3[i];
            t2      = P1[i];
            t3      = t1 - t2;
            p3p1[i] = t3;
        }
        for (int i = 0; i < ex.length; i++) {
            t1 = ex[i];
            t2 = p3p1[i];
            ival += (t1*t2);
        }
        for (int  i = 0; i < P3.length; i++) {
            t1 = P3[i];
            t2 = P1[i];
            t3 = ex[i] * ival;
            t  = t1 - t2 -t3;
            p3p1i += (t*t);
        }
        for (int i = 0; i < P3.length; i++) {
            t1 = P3[i];
            t2 = P1[i];
            t3 = ex[i] * ival;
            eyy = (t1 - t2 - t3)/Math.sqrt(p3p1i);
            ey[i] = eyy;
        }
        for (int i = 0; i < ey.length; i++) {
            t1 = ey[i];
            t2 = p3p1[i];
            jval += (t1*t2);
        }
        xval = (Math.pow(d1, 2) - Math.pow(d2, 2) + Math.pow(d, 2))/(2*d);
        yval = ((Math.pow(d1, 2) - Math.pow(d3, 2) + Math.pow(ival, 2) + Math.pow(jval, 2))/(2*jval)) - ((ival/jval)*xval);

        t1 = p1x;
        t2 = ex[0] * xval;
        t3 = ey[0] * yval;
        triptx = t1 + t2 + t3;

        t1 = p1y;
        t2 = ex[1] * xval;
        t3 = ey[1] * yval;
        tripty = t1 + t2 + t3;

        Location location = new Location("");
        location.setLatitude(triptx);
        location.setLongitude(tripty);

        return location;
    }

    private double distance(Location x, Location y){
        return x.distanceTo(y);
    }
}
