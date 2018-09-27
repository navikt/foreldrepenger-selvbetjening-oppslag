package no.nav.foreldrepenger.lookup.rest.sak;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import static java.util.stream.Collectors.joining;

public class StsClient {

    private static final Logger log = LoggerFactory.getLogger(StsClient.class);

    private RestTemplate restTemplate;
    private String stsUrl;
    private String serviceUser;
    private String servicePwd;
    private String soapRequestTemplate;

    public StsClient(RestTemplate restTemplate, String stsUrl, String serviceUser, String servicePwd) {
        this.restTemplate = restTemplate;
        this.stsUrl = stsUrl;
        this.serviceUser = serviceUser;
        this.servicePwd = servicePwd;
        soapRequestTemplate = readTemplate();
    }

    public String exchangeForSamlToken(String oidcToken) {
        log.info("Exchanging OIDC token for SAML token");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        String requestBody = Base64.getEncoder().encodeToString(
            replacePlaceholders(oidcToken).getBytes());
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(stsUrl, HttpMethod.POST, requestEntity, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Error while exchanging token, STS returned " + response.getStatusCode());
        }
        return response.getBody();
    }

    private String readTemplate() {
        try {
            final Path templatePath = Paths.get(StsClient.class.getResource("/template/stsenvelope.txt").toURI());
            return Files.readAllLines(templatePath).stream().map(String::trim).collect(joining("\n"));
        } catch (Exception ex) {
            throw new RuntimeException("Error while reading SOAP request template", ex);
        }
    }

    protected String replacePlaceholders(String oidcToken) {
        return soapRequestTemplate.replace("%SOMESERVICEUSER%", serviceUser)
            .replace("%THEPASSWORD%", servicePwd)
            .replace("%OIDCTOKEN%", oidcToken);
    }
}
