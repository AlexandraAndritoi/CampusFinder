package com.upt.ac.campusfinderapp.indoorlocation;

import android.content.Context;
import android.location.Location;

import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;
import com.upt.ac.campusfinderapp.model.WifiAccessPoint;
import com.upt.ac.campusfinderapp.utils.FileWriter;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

import io.indoorlocation.core.IndoorLocation;


class IndoorLocationCalculator {
    private static final double EARTH_RADIUS = 6378.137;
    private static final double K = (1 / ((2 * Math.PI / 360) * EARTH_RADIUS)) / 1000;

    private Context context;

    IndoorLocationCalculator(Context context){
        this.context = context;
    }

    IndoorLocation calculateIndoorLocationFromWifiAccessPointData(WifiAccessPoint[] wifiAccessPoints) {
        if(IsOneWifiAccessPointNull(wifiAccessPoints)){
            return null;
        }
        double distance1 = calculateDistanceFromDeviceToWifiAccessPoint(wifiAccessPoints[0].getLevel(), wifiAccessPoints[0].getFrequency());
        double distance2 = calculateDistanceFromDeviceToWifiAccessPoint(wifiAccessPoints[1].getLevel(), wifiAccessPoints[1].getFrequency());
        double distance3 = calculateDistanceFromDeviceToWifiAccessPoint(wifiAccessPoints[2].getLevel(), wifiAccessPoints[2].getFrequency());
        Location location =
                trilateration(wifiAccessPoints[0].getLatitude(), wifiAccessPoints[0].getLongitude(), distance1,
                              wifiAccessPoints[1].getLatitude(), wifiAccessPoints[1].getLongitude(), distance2,
                              wifiAccessPoints[2].getLatitude(), wifiAccessPoints[2].getLongitude(), distance3);
//        Location location =
//                trilateration(45.774124, 21.245351, 5,
//                        45.774098, 21.245438, 5,
//                        45.774022, 21.245366, 6);
        IndoorLocation indoorLocation = new IndoorLocation(location, wifiAccessPoints[0].getFloor());
        FileWriter fileWriter = new FileWriter(context);
        fileWriter.writeToExternalStorage(wifiAccessPoints, indoorLocation, new double[]{distance1, distance2, distance3});
        return indoorLocation;
    }

    private double calculateDistanceFromDeviceToWifiAccessPoint(int signalLevelInDb, int freqInMHz){
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(signalLevelInDb)) / 20.0;
        return Math.pow(10.0, exp);     //meters
    }

    private Location trilateration(double p1x, double p1y, double d1,
                                   double p2x, double p2y, double d2,
                                   double p3x, double p3y, double d3) {

        double earthR = 6378137;    //meters

        double P1x = (earthR*(Math.cos(Math.toRadians(p1x))*Math.cos(Math.toRadians(p1y))));
        double P1y = (earthR*(Math.cos(Math.toRadians(p1x))*Math.sin(Math.toRadians(p1y))));
        double P1z = (earthR*(Math.sin(Math.toRadians(p1x))));

        double P2x = (earthR*(Math.cos(Math.toRadians(p2x))*Math.cos(Math.toRadians(p2y))));
        double P2y = (earthR*(Math.cos(Math.toRadians(p2x))*Math.sin(Math.toRadians(p2y))));
        double P2z = (earthR*(Math.sin(Math.toRadians(p2x))));

        double P3x = (earthR*(Math.cos(Math.toRadians(p3x))*Math.cos(Math.toRadians(p3y))));
        double P3y = (earthR*(Math.cos(Math.toRadians(p3x))*Math.sin(Math.toRadians(p3y))));
        double P3z = (earthR*(Math.sin(Math.toRadians(p3x))));

        double[][] positions = new double[][] {{P1x, P1y, P1z}, {P2x, P2y, P2z}, {P3x, P3y, P3z}};
        double[] distances = new double[] {d1, d2, d3};

        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
        LeastSquaresOptimizer.Optimum optimum = solver.solve();

        double[] centroid = optimum.getPoint().toArray();
        double x = centroid[0];
        double y = centroid[1];
        double z = centroid[2];

        double latitude =  Math.toDegrees(Math.asin(z/earthR));
        double longitude = Math.toDegrees(Math.atan2(y, x));

        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        return location;
    }

    private boolean IsOneWifiAccessPointNull(WifiAccessPoint[] wifiAccessPoints){
        for(WifiAccessPoint wifiAccessPoint: wifiAccessPoints){
            if(wifiAccessPoint == null){
                return true;
            }
        }
        return false;
    }
}
