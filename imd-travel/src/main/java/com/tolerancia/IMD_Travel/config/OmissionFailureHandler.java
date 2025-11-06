package com.tolerancia.IMD_Travel.config;

import com.tolerancia.Failure_Simulator.Exceptions.OmissionFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class OmissionFailureHandler {

    @ExceptionHandler(OmissionFailureException.class)
    public void handleOmission() {
        //nao faz nada
    }
}