package no.nav.foreldrepenger.config;

import static no.nav.foreldrepenger.lookup.util.EnvUtil.DEFAULT;
import static no.nav.foreldrepenger.lookup.util.EnvUtil.DEV;
import static no.nav.foreldrepenger.lookup.util.EnvUtil.LOCAL;

public class ClusterAwareSpringProfileResolver {

    private static final String NAIS_CLUSTER_NAME = "NAIS_CLUSTER_NAME";

    public String getProfile() {
        return clusterFra(System.getenv(NAIS_CLUSTER_NAME));
    }

    private static String clusterFra(String cluster) {
        if (cluster == null) {
            return LOCAL;
        }
        if (cluster.contains(DEV)) {
            return DEV;
        }
        return DEFAULT;
    }
}
