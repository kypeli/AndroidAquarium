package com.kypeli.aquarium.models;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AquariumReadings {
    @SerializedName("measurements")
    public ArrayList<Reading> readings;

    public static class Reading {
        @SerializedName("epoch_timestamp")
        public long epoch;
        public String temperature;
        public Date timestamp;
    }

    public static class TemperatureDateSerializer implements JsonDeserializer<Date> {

        @Override
        public Date deserialize(JsonElement json, Type typeOfSrc, JsonDeserializationContext ctx) {
            SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy H:m:s ZZZ");
            Date d = null;
            try {
                d = formatter.parse(json.getAsString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return d;
        }
    }
}
