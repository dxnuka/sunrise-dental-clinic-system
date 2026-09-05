package com.sunrise.dental.controller.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public interface RequestHandler {

    String handle(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
