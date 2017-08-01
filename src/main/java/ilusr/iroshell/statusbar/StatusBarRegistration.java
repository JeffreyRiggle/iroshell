package ilusr.iroshell.statusbar;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import ilusr.core.ioc.ServiceManager;


public class StatusBarRegistration {
	
	public static void register(ServiceManager manager) throws IOException, URISyntaxException {
		Resource resource = new ClassPathResource("statusbarregistrations.xml", StatusBarRegistration.class);
		manager.registerServicesFromResource(resource);
	}
}
