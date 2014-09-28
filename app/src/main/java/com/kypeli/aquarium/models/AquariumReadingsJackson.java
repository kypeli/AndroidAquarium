package com.kypeli.aquarium.models;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AquariumReadingsJackson {
    @JsonProperty("measurements")
    public ArrayList<Reading> readings;

    public static class Reading {
        @JsonProperty("epoch_timestamp")
        public long epoch;
        public Float temperature;
        @JsonDeserialize(using = TemperatureJsonDateSerializer.class)
        public Date timestamp;
    }

    public static class TemperatureJsonDateSerializer extends JsonDeserializer<Date> {
        @Override
        public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy H:m:s ZZZ");
            Date d = null;
            try {
                d = formatter.parse(jp.getText());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return d;
        }
    }
 }
