package no.nav.foreldrepenger.oppslag.util;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

public final class EnvUtil {

    public static final String LOCAL = "local";
    public static final String DEFAULT = "default";
    public static final String DEV = "dev";
    public static final Marker CONFIDENTIAL = MarkerFactory.getMarker("CONFIDENTIAL");

    private EnvUtil() {

    }

    public static boolean isDevOrLocal(Environment env) {
        return env == null || isDev(env) || isLocal(env);
    }

    public static boolean isDev(Environment env) {
        return env.acceptsProfiles(Profiles.of(DEV));
    }

    public static boolean isLocal(Environment env) {
        return env.acceptsProfiles(Profiles.of(LOCAL));
    }

    public static boolean isProd(Environment env) {
        return !isDevOrLocal(env);
    }
}
