package com.durgesh.restaurant.models.googlePlaces;

/**
 * Created by Snehal Tembare on 30/8/17.
 */

public class Ingredients {

    private String id;

    private String name;

    private Double value;

    private String app_id;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public Double getValue ()
    {
        return value;
    }

    public void setValue (Double value)
    {
        this.value = value;
    }

    public String getApp_id ()
    {
        return app_id;
    }

    public void setApp_id (String app_id)
    {
        this.app_id = app_id;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", name = "+name+", value = "+value+", app_id = "+app_id+"]";
    }
}
