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
                trilateration(45.774510, 21.245588, 2,
                        45.774344, 21.245645, 2,
                        45.774304, 21.245579, 2);
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

    private Location trilateration(double p1x, double p1y, double r1, double p2x, double p2y, double r2, double p3x, double p3y, double r3){
        double earthR = 6378.137;

        double P1x = (earthR*(Math.cos(Math.toRadians(p1x))*Math.cos(Math.toRadians(p1y))));
        double P1y = (earthR*(Math.cos(Math.toRadians(p1x))*Math.sin(Math.toRadians(p1y))));
        double P1z = (earthR*(Math.sin(Math.toRadians(p1x))));

        double P2x = (earthR*(Math.cos(Math.toRadians(p2x))*Math.cos(Math.toRadians(p2y))));
        double P2y = (earthR*(Math.cos(Math.toRadians(p2x))*Math.sin(Math.toRadians(p2y))));
        double P2z = (earthR*(Math.sin(Math.toRadians(p2x))));

        double P3x = (earthR*(Math.cos(Math.toRadians(p3x))*Math.cos(Math.toRadians(p3y))));
        double P3y = (earthR*(Math.cos(Math.toRadians(p3x))*Math.sin(Math.toRadians(p3y))));
        double P3z = (earthR*(Math.sin(Math.toRadians(p3x))));

        double exx = ((P2x-P1x)/Math.sqrt(Math.pow(P2z-P1z, 2)+Math.pow((P2x-P1x),2)+Math.pow((P2y-P1y),2)));
        double exy = ((P2y-P1y)/Math.sqrt(Math.pow(P2z-P1z, 2)+Math.pow((P2x-P1x),2)+Math.pow((P2y-P1y),2)));
        double exz = ((P2z-P1z)/Math.sqrt(Math.pow(P2z-P1z, 2)+Math.pow((P2x-P1x),2)+Math.pow((P2y-P1y),2)));
        double EX = Math.sqrt(Math.pow(exx, 2)+Math.pow(exy, 2)+Math.pow(exz,2));

        double i = Math.sqrt(Math.pow((P3x-P1x)*EX, 2)+Math.pow((P3y-P1y)*EX, 2)+Math.pow((P3z-P1z)*EX, 2));

        double eyx = ((P3x-P1x-(i*exx))/Math.sqrt((Math.pow(P3z-P1z-(i*exz),2))+(Math.pow(P3x-P1x-(i*exx),2))+(Math.pow(P3y-P1y-(i*exy),2))));
        double eyy = ((P3y-P1y-(i*exy))/Math.sqrt((Math.pow(P3z-P1z-(i*exz),2))+(Math.pow(P3x-P1x-(i*exx),2))+(Math.pow(P3y-P1y-(i*exy),2))));
        double eyz = ((P3z-P1z-(i*exz))/Math.sqrt((Math.pow(P3z-P1z-(i*exz),2))+(Math.pow(P3x-P1x-(i*exx),2))+(Math.pow(P3y-P1y-(i*exy),2))));
        double EY = Math.sqrt(Math.pow(eyx, 2)+Math.pow(eyy, 2)+Math.pow(eyz, 2));

        double ezx = (exy*eyz)-(exz*exy);
        double ezy = (exz*eyx)-(exx*eyz);

        double d = Math.sqrt((Math.pow(P2x-P1x,2))+(Math.pow(P2y-P1y,2))+Math.pow(P2z-P1z, 2));

        double j = Math.sqrt(Math.pow((P3x-P1x)*EY, 2)+Math.pow((P3y-P1y)*EY, 2)+Math.pow((P3z-P1z)*EY, 2));
        double x = ((Math.pow(r1, 2)-Math.pow(r2, 2)+Math.pow(d, 2))/(2*d));
        double y = (Math.pow(r1, 2)-Math.pow(r3, 2)+Math.pow(i, 2)+Math.pow(j, 2))/(2*j)- (i*x/j);

        double z1 = (Math.pow(r1,2) - Math.pow(x,2) - Math.pow(y,2));
        double z2;
        if (z1<0){
            return null;
        }
        else{
            z1 = Math.sqrt(z1);
            z2 = z1*-1;
        }

        double result1x = P1x + exx + eyy + ezx*z1;
        double result1y = P1y + exx + eyy + ezy*z1;
        double result2x = P1x + exx + eyy + ezx*z2;
        double result2y = P1y + exx + eyy + ezy*z2;

        Location result1 = new Location("");
        result1.setLatitude(result1x);
        result1.setLongitude(result1y);
        Location result2 = new Location("");
        result2.setLatitude(result2x);
        result2.setLongitude(result2y);
        Location P3 = new Location("");
        P3.setLatitude(P3x);
        P3.setLongitude(P3y);

        if(Math.abs(distance(result1, P3) - r3) < Math.abs(distance(result2, P3) - r3))
            return result1;
        else return result2;
    }

    private double distance(Location x, Location y){
        return x.distanceTo(y);
    }
}
