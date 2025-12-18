# Rate Limiter Service (Dropwizard + Redis)

## Overview

This project is a **Redis-backed rate limiting service** built using **Dropwizard**, **Jetty**, and **Jersey**.  
It enforces request limits using the **Token Bucket algorithm** and applies rate limiting at the **HTTP filter layer**, before requests reach resource handlers.

The service is designed as a **long-running backend component**, similar to those used in API gateways, internal platform services, and edge infrastructure.

---

## Key Features

- Token Bucket–based rate limiting
- Redis-backed state for distributed consistency
- Per-client **and per-path** rate limits
- Request interception using Jersey `ContainerRequestFilter`
- Configurable limits via YAML
- Redis connection lifecycle management using Dropwizard `Managed`
- Health checks for Redis availability
- Metrics for allowed, rejected, and error requests

---

## Rate Limiting Algorithm

The service uses the **Token Bucket algorithm**:

- Each rate-limited identity is assigned a bucket
- Buckets have a maximum token capacity
- Tokens refill at a fixed rate over time
- Each request consumes one token
- Requests are rejected when no tokens are available

Rate limiting is applied **before request processing**, ensuring minimal overhead when limits are exceeded.

---

## Rate Limit Identity

Requests are rate-limited based on a composite key consisting of:

- Client identifier (via request header)
- HTTP method
- Request path

This allows **fine-grained control**, such as applying stricter limits to sensitive endpoints (e.g. `/login`) while keeping relaxed limits elsewhere.

---

## Configuration

Rate limits are fully configurable via `config.yml`.

Example:

```yaml
rateLimit:
  default:
    maxTokens: 10
    refillRate: 10
  perPath:
    /test:
      maxTokens: 5
      refillRate: 5
    /login:
      maxTokens: 3
      refillRate: 3
 
