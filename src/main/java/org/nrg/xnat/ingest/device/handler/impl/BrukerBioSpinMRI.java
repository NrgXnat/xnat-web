package org.nrg.xnat.ingest.device.handler.impl;

import lombok.extern.slf4j.Slf4j;
import org.nrg.xnat.ingest.device.handler.DeviceFile;
import org.nrg.xnat.ingest.device.handler.DeviceHandler;
import org.nrg.xnat.ingest.device.handler.ManifestEntry;
import org.nrg.xnat.ingest.device.parsers.JCAMPParameterParser;
import org.nrg.xnat.ingest.device.parsers.ParameterFile;
import org.nrg.xnat.ingest.services.components.DeviceHandlerComponent;
import org.nrg.xnat.ingest.utils.IngestUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

@Slf4j
@DeviceHandlerComponent(priority = 10, supportedDeviceTypes = {"Bruker BioSpin MRI GmbH"})
public class BrukerBioSpinMRI extends DeviceHandler {

        private DeviceFile subjectFile = null;

        public BrukerBioSpinMRI(String deviceId, String deviceType, Path deviceRootPath) {
            super(deviceId, deviceType, deviceRootPath);
        }

        @Override
        public boolean canHandleDevice(Path devicePath) {
            // Check for Bruker-specific files or folder structure
            Optional<Path> subjectFilePath = IngestUtils.findFileInFolder(deviceRootPath,"subject");
            if (subjectFilePath.isPresent()) {
                try {
                    subjectFile = new DeviceFile(subjectFilePath.get());
                    return true;
                } catch(IOException ignored) {}
            }
            return false;
        }


        @Override
        public ManifestEntry createManifestEntry() {
            String relativePath = deviceRootPath.relativize(subjectFile.getFilePath()).toString();
            ManifestEntry entry = new ManifestEntry(subjectFile.getFileName(), relativePath);

            // Add Bruker-specific attributes
            try {
                ParameterFile brukerParameterFile = JCAMPParameterParser.parseFile(subjectFile);
                for (Map.Entry<String, Object> headers : brukerParameterFile.getHeaders().entrySet()) {
                    entry.setAttribute(headers.getKey(), headers.getValue());
                }
                for (Map.Entry<String, Object> headers : brukerParameterFile.getParameters().entrySet()) {
                    entry.setAttribute(headers.getKey(), headers.getValue());
                }
                entry.setAttribute("BRUKER_COMMENTS", brukerParameterFile.getComments());
            } catch(IOException ioe) {
                log.error("Could not parse the subject file at " + subjectFile.getFilePath());
            }
            return entry;
        }

}
