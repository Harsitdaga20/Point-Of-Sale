package com.increff.pos.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.increff.pos.service.ApiException;

import java.util.List;

public class ErrorUtil {
    public static <T> void errors(List<T> errors) throws JsonProcessingException, ApiException {
        ObjectMapper mapper=new ObjectMapper();
        String error= mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errors);
        throw new ApiException(error);
    }
}
