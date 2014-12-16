package org.sugis.resteasy;

import static org.junit.Assert.assertFalse;

import java.util.Collections;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.Test;

public class TestExceptions {
    static final int PORT = 12345;
    static final String ADDR = "http://127.0.0.1:" + PORT;

    @Test
    public void testExceptions() throws Exception {
        final ResteasyDeployment deploy = new ResteasyDeployment();
        deploy.setResourceClasses(Collections.singletonList(FetchResource.class.getName()));
        final TJWSEmbeddedJaxrsServer server = new TJWSEmbeddedJaxrsServer();
        server.setDeployment(deploy);
        server.setPort(PORT);
        server.start();
        try {
            final Response r = ClientBuilder.newClient().target(ADDR + "/test").request().get();
            final String strResponse = r.readEntity(String.class);
            System.out.println("=== OBSERVED RESPONSE:");
            System.out.println(r.getStatus() + ": " + strResponse);
            assertFalse(strResponse.contains("Unexpected problem"));
        } finally {
            server.stop();
        }
    }

    @Path("/")
    public static class FetchResource {
        @Path("/test")
        @GET
        public String get() {
            ClientBuilder.newClient().target(ADDR + "/404").request().get(String.class);
            throw new AssertionError("Should have failed");
        }
    }
}
