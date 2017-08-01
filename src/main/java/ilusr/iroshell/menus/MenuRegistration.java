package ilusr.iroshell.menus;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import ilusr.core.ioc.ServiceManager;

public class MenuRegistration {

	public static void register(ServiceManager manager) throws IOException, URISyntaxException {
		Resource resource = new ClassPathResource("menuregistrations.xml", MenuRegistration.class);
		manager.registerServicesFromResource(resource);
	}
}
