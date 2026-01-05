package org.nrg.xnat.ingest.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xnat.ingest.model.pojo.FileItem;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.framework.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.ingest.utils.FileCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@XapiRestController
@Api("Ingest API Service")
public class IngestApi extends AbstractXapiRestController {

    @Autowired
    public IngestApi(final UserManagementServiceI userManagementService,
                     final RoleHolder roleHolder) {
        super(userManagementService, roleHolder);
    }

    @XapiRequestMapping(value = "/ingest", method = POST)
    @ApiOperation(value = "Ingest Data")
    public ResponseEntity<Void> ingest(final @RequestBody String ingestJson) throws NotFoundException, IOException {
        final UserI user = getSessionUser();
        //TODO add security to access and destination
        ObjectMapper mapper = new ObjectMapper();
        FileItem[] items = mapper.readValue(ingestJson, FileItem[].class);
        FileCopyUtils fileCopyUtils = new FileCopyUtils();
        fileCopyUtils.processJsonFile(items, user);
        return ResponseEntity.ok().build();
    }

}
