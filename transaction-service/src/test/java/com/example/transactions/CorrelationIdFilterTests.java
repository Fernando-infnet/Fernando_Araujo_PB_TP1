package com.example.transactions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.example.transactions.config.CorrelationIdFilter;

class CorrelationIdFilterTests {
    private final CorrelationIdFilter filter = new CorrelationIdFilter();

    @Test
    void preservesClientCorrelationIdInResponse() throws Exception {
        var request = new MockHttpServletRequest();
        request.addHeader(CorrelationIdFilter.HEADER, "demo-correlation-id");
        var response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getHeader(CorrelationIdFilter.HEADER)).isEqualTo("demo-correlation-id");
        assertThat(MDC.get("correlationId")).isNull();
    }

    @Test
    void createsCorrelationIdWhenHeaderIsMissing() throws Exception {
        var response = new MockHttpServletResponse();

        filter.doFilter(new MockHttpServletRequest(), response, new MockFilterChain());

        assertThat(response.getHeader(CorrelationIdFilter.HEADER)).isNotBlank();
    }
}
