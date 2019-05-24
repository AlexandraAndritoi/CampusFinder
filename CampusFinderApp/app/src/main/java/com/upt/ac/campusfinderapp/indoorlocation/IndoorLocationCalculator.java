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
                trilateration(45.7742240, 21.245395, 2,
                        45.774187, 21.245362, 2,
                        45.774167, 21.245424, 2,
                        45.774215, 21.245444, 2);
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
                                   double p3x, double p3y, double d3,
                                   double p4x, double p4y, double d4) {
        double earthR = 6378.137;

        double P1x = (earthR * (Math.cos(Math.toRadians(p1x)) * Math.cos(Math.toRadians(p1y))));
        double P1y = (earthR * (Math.cos(Math.toRadians(p1x)) * Math.sin(Math.toRadians(p1y))));
        double P1z = (earthR * (Math.sin(Math.toRadians(p1x))));

        double P2x = (earthR * (Math.cos(Math.toRadians(p2x)) * Math.cos(Math.toRadians(p2y))));
        double P2y = (earthR * (Math.cos(Math.toRadians(p2x)) * Math.sin(Math.toRadians(p2y))));
        double P2z = (earthR * (Math.sin(Math.toRadians(p2x))));

        double P3x = (earthR * (Math.cos(Math.toRadians(p3x)) * Math.cos(Math.toRadians(p3y))));
        double P3y = (earthR * (Math.cos(Math.toRadians(p3x)) * Math.sin(Math.toRadians(p3y))));
        double P3z = (earthR * (Math.sin(Math.toRadians(p3x))));

        double P4x = (earthR * (Math.cos(Math.toRadians(p4x)) * Math.cos(Math.toRadians(p4y))));
        double P4y = (earthR * (Math.cos(Math.toRadians(p4x)) * Math.sin(Math.toRadians(p4y))));
        double P4z = (earthR * (Math.sin(Math.toRadians(p4x))));

        double A = P2x-P1x;
        double B = P2y-P1y;
        double C = P1z-P2z;
        double D = (Math.pow(d2,2)-Math.pow(d1,2)+Math.pow(P1x,2)-Math.pow(P2x,2)+Math.pow(P1y,2)-Math.pow(P2y,2)+Math.pow(P1z,2)-Math.pow(P2z,2))/2;
        double E = P2x-P3x;
        double F = P2y-P3y;
        double G = P2z-P3z;
        double H = (Math.pow(d3,2)-Math.pow(d2,2)+Math.pow(P2x,2)-Math.pow(P3x,2)+Math.pow(P2y,2)-Math.pow(P3y,2)+Math.pow(P2z,2)-Math.pow(P3z,2))/2;
        double I = P3x-P4x;
        double J = P3y-P4y;
        double K = P3z-P4z;
        double L = (Math.pow(d4,2)-Math.pow(d3,2)+Math.pow(P3x,2)-Math.pow(P4x,2)+Math.pow(P3y,2)-Math.pow(P4y,2)+Math.pow(P3z,2)-Math.pow(P4z,2))/2;

        double Z = ((A*L-I*D)*(A*F-E*B) - (A*H-E*D)*(A*J-I*B)) / ((C*E-A*G)*(A*J-I*B) + (A*K-I*C)*(A*F-E*B));
        double Y = (A*H - E*D + Z*(C*E-A*G)) / (A*F-E*B);
        double X = (D - B*Y - C*Z) / A;

        double latitude =  Math.asin(Z/earthR);
        double longitude = Math.atan2(Y, X);

        Location location = new Location("WifiIndoorLocationProvider");
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        return location;
    }

    private double distance(Location x, Location y){
        return x.distanceTo(y);
    }
}
