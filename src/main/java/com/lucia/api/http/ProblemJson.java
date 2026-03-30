package com.lucia.api.http;

import org.springframework.http.MediaType;

/**
 * RFC 7807 {@code application/problem+json} sin usar constantes deprecadas de Spring.
 */
public final class ProblemJson {

    public static final String CONTENT_TYPE = "application/problem+json";
    public static final MediaType MEDIA_TYPE = MediaType.parseMediaType(CONTENT_TYPE);

    private ProblemJson() {}
}
