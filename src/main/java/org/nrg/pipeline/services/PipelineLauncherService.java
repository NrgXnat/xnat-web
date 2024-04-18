package org.nrg.pipeline.services;


public interface PipelineLauncherService {
     boolean launch();
     boolean launch(String cmdPrefix);
}
