package no.nav.foreldrepenger.lookup.rest.filters;

import static no.nav.foreldrepenger.lookup.Constants.NAV_AKTØR_ID;
import static no.nav.foreldrepenger.lookup.Constants.NAV_CALL_ID;
import static no.nav.foreldrepenger.lookup.Constants.NAV_CONSUMER_ID;
import static no.nav.foreldrepenger.lookup.Constants.NAV_USER_ID;
import static no.nav.foreldrepenger.lookup.util.EnvUtil.isDevOrPreprod;

import java.io.IOException;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import no.nav.foreldrepenger.lookup.CallIdGenerator;
import no.nav.foreldrepenger.lookup.util.TokenUtil;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorIdClient;

@Component
public class HeadersToMDCFilterBean extends GenericFilterBean {

    private static final Logger LOG = LoggerFactory.getLogger(HeadersToMDCFilterBean.class);

    private final CallIdGenerator generator;
    private final String applicationName;
    private final TokenUtil tokenHandler;
    private final AktorIdClient aktørIdClient;

    @Inject
    public HeadersToMDCFilterBean(CallIdGenerator generator, TokenUtil tokenHandler, AktorIdClient aktørIdClient,
            @Value("${spring.application.name}") String applicationName) {
        this.generator = generator;
        this.tokenHandler = tokenHandler;
        this.aktørIdClient = aktørIdClient;
        this.applicationName = applicationName;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        putValues(HttpServletRequest.class.cast(request));
        chain.doFilter(request, response);
    }

    private void putValues(HttpServletRequest request) {
        putValue(NAV_CONSUMER_ID, request.getHeader(NAV_CONSUMER_ID), applicationName);
        putValue(NAV_CALL_ID, request.getHeader(NAV_CALL_ID), generator.create());
        try {
            if (isDevOrPreprod(getEnvironment())) {
                putValue(NAV_USER_ID, tokenHandler.getSubject().getFnr());
            }
            putValue(NAV_AKTØR_ID, aktørIdClient.aktorIdForFnr(tokenHandler.getSubject()).getAktør());
        } catch (Exception e) {
            LOG.warn("Noe gikk feil, MDC-verdier er inkomplette", e);
        }
    }

    private static void putValue(String key, String value) {
        putValue(key, value, null);
    }

    private static void putValue(String key, String value, String defaultValue) {
        MDC.put(key, Optional.ofNullable(value).orElse(defaultValue));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [generator=" + generator + ", applicationName=" + applicationName
                + ", tokenHandler=" + tokenHandler + ", aktørIdClient=" + aktørIdClient + "]";
    }

}