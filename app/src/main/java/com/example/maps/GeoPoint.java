package com.example.maps;

class GeoPoint {

    private double _x;
    private double _y;

    GeoPoint(double x, double y)
    {
        _x = x;
        _y = y;
    }

    double getX()
    {
        return _x;
    }

    double getY()
    {
        return _y;
    }
}