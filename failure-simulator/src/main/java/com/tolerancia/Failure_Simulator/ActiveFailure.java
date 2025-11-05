package com.tolerancia.Failure_Simulator;

import java.time.Instant;

public record ActiveFailure(String type, Instant expiresAt) { }
