package org.example.filter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import org.example.service.RateLimiterService;

public class RateLimiterFilter implements ContainerRequestFilter {

    private final RateLimiterService rateLimiterService;
    private final String HEADER = "X-API-KEY";

    public RateLimiterFilter(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String clientId = requestContext.getHeaderString(HEADER);
        if(clientId == null || clientId.isEmpty()) {
            requestContext.abortWith(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Missing API Key")
                    .build());
            return;
        }
        boolean allowRequest = rateLimiterService.allowRequest(clientId);
        if(!allowRequest) {
            requestContext.abortWith(Response
                    .status(Response.Status.TOO_MANY_REQUESTS)
                    .entity("Rate limit exceeded")
                    .build());
        }

    }
}
