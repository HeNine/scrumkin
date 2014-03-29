package com.scrumkin.rs;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * REST configuration.
 */
@ApplicationPath("/")
public class RESTApplication extends Application {

    private final Set<Class<?>> classes;

    public RESTApplication() {
        HashSet<Class<?>> c = new HashSet<>();

        classes = Collections.unmodifiableSet(c);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }

}
