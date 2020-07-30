package org.nrg.xnat.export.event.listener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.nrg.action.ServerException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.bean.CatCatalogBean;
import org.nrg.xdat.model.CatEntryI;
import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xdat.om.base.auto.AutoXnatProjectdata;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.export.event.ExportFileTransportEvent;
import org.nrg.xnat.export.utils.ExportConstants;
import org.nrg.xnat.export.utils.ExportUtils;
import org.nrg.xnat.services.archive.CatalogService;
import org.nrg.xnat.utils.CatalogUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mohana Ramaratnam
 *
 */

@Component
@Slf4j
public class ExportFileTransportEventListener  {

	//@Async
	@EventListener
    public void handleFileTransport(ExportFileTransportEvent event) {
		//Open a file and write the message
		final XnatProjectdata project=AutoXnatProjectdata.getXnatProjectdatasById(event.getProjectId(), event.getUser(), true);
		final String trackingId = event.getTrackingId();   
		writeToFile(project, trackingId, event.getMessage(), event.isComplete(), event.getUser());
		log.info(event.getMessage());
	}
	
	
	private synchronized void writeToFile(final XnatProjectdata project, final String trackingId, String message, boolean eventIsComplete, UserI user) {
		XnatResourcecatalog res=null;
		List<String> names=Lists.newArrayList();
		String trackingFileName = ExportUtils.exportEventIdToFilename(trackingId)+".log";
		names.add(trackingFileName);
		
		for(XnatAbstractresourceI r: project.getResources_resource()){
			if(r instanceof XnatResourcecatalog && ExportConstants.EXPORT_LOGS.equals(r.getLabel())){
				res=(XnatResourcecatalog)r;
			}
		}
		
		
		File matchedFile=null;
		if(res!=null){
			try {
				final CatalogUtils.CatalogData catalogData = CatalogUtils.CatalogData.getOrCreate(project.getRootArchivePath(), res, project.getId());
				final CatCatalogBean cat = catalogData.catBean;
				final File catalog_xml = catalogData.catFile;

				CatEntryI entry=null;
				for(String name:names){
					entry=CatalogUtils.getEntryByURI(cat, name);
					if(entry!=null)break;
				}

				if(entry!=null){
					matchedFile=CatalogUtils.getFile(entry, catalog_xml.getParent(), project.getId());
				}else {
						matchedFile = new File(catalog_xml.getParent() + "/" + trackingFileName);
				}
			} catch (ServerException e) {
				log.error("Unable to read or create catalog for resource {}", 
						res.getXnatAbstractresourceId(), e);
			}
		}
		
		try {
            FileWriter fw = new FileWriter(matchedFile, true);
            DateFormat simple = new SimpleDateFormat("YYYY-MMM-dd HH:mm:ss:SSS Z");
            Date d = new Date(System.currentTimeMillis());
            fw.write(simple.format(d) + " : " + message + System.lineSeparator());
            fw.close();
        }catch(IOException e) {
        }
		if (eventIsComplete) {
			final String proj_URI = "/archive/projects/" + project.getId();
			try {
				final CatalogService _catService = XDAT.getContextService().getBean(CatalogService.class);
				_catService.refreshResourceCatalog(user,proj_URI, CatalogService.Operation.ALL);
			}catch(Exception e) {
				log.warn("WARNING:  Could not update file counts for " + project.getId());
			}

		}

	}

	
}
