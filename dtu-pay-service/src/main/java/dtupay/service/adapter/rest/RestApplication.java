package dtupay.service.adapter.rest;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

// @author Carl
@ApplicationPath("/")
@OpenAPIDefinition(
        info = @Info(
                title = "DTU Pay - Group 17",
                version = "1.0.0")
)
public class RestApplication extends Application {

}
