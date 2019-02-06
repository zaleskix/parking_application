package com.zaleskix.parking.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.regex.Pattern;

public abstract class AbstractRestControllerTest {

    public static String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}