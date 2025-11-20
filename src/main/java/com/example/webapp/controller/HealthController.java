package com.example.webapp.controller;

import com.example.webapp.service.HealthCheckService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class HealthController {

    private final HealthCheckService healthCheckService;

    public HealthController(HealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
    }

    /**
     * Checks whether the request contains query parameters.
     * Query parameters are not allowed for the /healthz endpoint.
     *
     * @param request the incoming HTTP request
     * @return true if the request contains query parameters, false otherwise
     */
    private boolean hasQueryParameters(HttpServletRequest request) {
        return !request.getParameterMap().isEmpty();
    }

    /**
     * Checks whether the request contains a request body.
     * A request body is not allowed for the /healthz endpoint.
     *
     * @param request the incoming HTTP request
     * @return true if the request contains a body, false otherwise
     * @throws IOException if an I/O error occurs while reading the request
     */
    private boolean hasRequestBody(HttpServletRequest request) throws IOException {
        if (request.getContentLength() > 0) {
            return true;
        }
        String transferEncoding = request.getHeader(HttpHeaders.TRANSFER_ENCODING);
        return "chunked".equalsIgnoreCase(transferEncoding);
    }

    /**
     * Creates a set of standard HTTP headers for all responses.
     *
     * @return a {@link HttpHeaders} object containing standard headers
     */
    private HttpHeaders createStandardHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("X-Content-Type-Options", "nosniff");
        return headers;
    }

    /**
     * Handles GET requests to the /healthz endpoint.
     * <p>
     *     This endpoint validates that the request has no query parameters and no body.
     *     If valid, it records a health check and returns:
     *     <ul>
     *         <li>200 OK: when the request is valid</li>
     *         <li>400 Bad Request: when query parameters or body are present</li>
     *         <li>503 Service Unavailable: when recording the health check fails</li>
     *     </ul>
     * </p>
     * @param request the incoming HTTP request
     * @return a {@link ResponseEntity} with an appropriate status code and headers
     * @throws IOException if an I/O error occurs during request inspection
     */
    @GetMapping("/healthz")
    public ResponseEntity<Void> health(HttpServletRequest request) throws IOException {
        HttpHeaders headers = createStandardHeaders();

        try {
            if (hasQueryParameters(request)) {
                return ResponseEntity.badRequest().headers(headers).build();
            }

            if (hasRequestBody(request)) {
                return ResponseEntity.badRequest().headers(headers).build();
            }

            healthCheckService.recordHealthCheck();
            return ResponseEntity.ok().headers(headers).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).headers(headers).build();
        }
    }

    /**
     * Handles all non-GET requests to the /healthz endpoint.
     * <p>
     *     Since only GET requests are allowed, the endpoint responds with:
     *     <ul>
     *         <li>405 Method Not Allowed: for POST, PUT, PATCH, DELETE requests</li>
     *     </ul>
     * </p>
     * @return a 405 Method Not Allowed response
     */
    @RequestMapping(value="/healthz", method = {
            RequestMethod.POST,
            RequestMethod.PUT,
            RequestMethod.PATCH,
            RequestMethod.DELETE,
            RequestMethod.HEAD,
            RequestMethod.OPTIONS,
    })
    public ResponseEntity<Void> otherRequestMethods(){
        HttpHeaders headers = createStandardHeaders();
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).headers(headers).build();
    }
}
