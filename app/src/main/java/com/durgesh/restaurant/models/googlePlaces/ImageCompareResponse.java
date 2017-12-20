package com.durgesh.restaurant.models.googlePlaces;

import java.util.List;

/**
 * Created by Snehal Tembare on 30/8/17.
 */

public class ImageCompareResponse {


        private List<Ingredients> ingredients;

        private String id;

        private String name;

        private Double value;

        private String app_id;

        private Boolean code;

        public List<Ingredients> getIngredients ()
        {
            return ingredients;
        }

        public void setIngredients (List<Ingredients> ingredients)
        {
            this.ingredients = ingredients;
        }

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

        public Boolean getCode ()
        {
            return code;
        }

        public void setCode (Boolean code)
        {
            this.code = code;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [ingredients = "+ingredients+", id = "+id+", name = "+name+", value = "+value+", app_id = "+app_id+", code = "+code+"]";
        }

}
