# Rate Limiter Service (Dropwizard + Redis)

## Overview

This project is a Redis-backed rate limiting service built using Dropwizard, Jetty, and Jersey.
It enforces request limits using the Token Bucket algorithm and applies rate limiting at the HTTP filter layer, before requests reach resource handlers.

The service is designed as a long-running backend component, similar to what is used in API gateways and internal platform services.

## Key Features
  •	Token Bucket–based rate limiting </br>
	•	Redis-backed state for distributed consistency </br>
	•	Request interception using Jersey ContainerRequestFilter </br>
	•	Dropwizard application lifecycle and configuration </br>
	•	Externalized configuration via YAML </br>
	•	Packaged as a runnable shaded JAR </br>

## Rate Limiting Algorithm
  The service uses the Token Bucket algorithm: </br>
  	•	Each client is assigned a bucket with a maximum number of tokens </br>
  	•	Tokens refill at a fixed rate over time </br>
  	•	Each request consumes one token </br>
  	•	Requests are rejected when no tokens are available </br>
 
