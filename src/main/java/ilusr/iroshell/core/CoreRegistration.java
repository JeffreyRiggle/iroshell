package ilusr.iroshell.core;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import ilusr.core.ioc.ServiceManager;

public class CoreRegistration {
	
	public static void register(ServiceManager manager) throws IOException, URISyntaxException {
		Resource resource = new ClassPathResource("registrations.xml", CoreRegistration.class);
		manager.registerServicesFromResource(resource);
	}
}
