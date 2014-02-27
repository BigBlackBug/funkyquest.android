package com.newresources.funkyquest.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class FQObjectMapper extends ObjectMapper {

    public static final String MODULE_NAME = "FQObjectMapper";

    public FQObjectMapper() {
        super();
        SimpleModule testModule = new SimpleModule(MODULE_NAME);
	    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	    configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
	    setSerializationInclusion(JsonInclude.Include.NON_NULL);
        registerModule(testModule);
    }
}