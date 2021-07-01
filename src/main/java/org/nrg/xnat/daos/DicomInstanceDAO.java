package org.nrg.xnat.daos;

import org.nrg.framework.orm.hibernate.AbstractHibernateDAO;
import org.nrg.xapi.model.dicomweb.DicomImageObject;
import org.nrg.xapi.model.dicomweb.DicomObject;
import org.nrg.xnat.entities.DicomFrame;
import org.nrg.xnat.entities.DicomInstance;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.io.IOException;

@Repository
public class DicomInstanceDAO extends AbstractHibernateDAO<DicomInstance> {

    @Transactional
    public void saveDicomObject( long imagescandata_id, DicomImageObject d) throws IOException {
        DicomInstance instance = new DicomInstance();
        instance.setImagescandata_id( imagescandata_id);
        instance.setFilePath( d.getFile().getCanonicalPath());
        instance.setSopinstanceuid( d.getSOPInstanceUID());
        instance.setSopclassuid( d.getSOPClassUID());
        instance.setInstanceNumber( d.getInstanceNumber());
        instance.setRows( d.getRows());
        instance.setCols( d.getColumns());
        instance.setBits_allocated( d.getBitsAllocated());

        DicomFrame frame = new DicomFrame( instance, 1, d.getImagePositionPatient(), d.getImageOrientationPatient(), d.getPixelSpacing(), d.getFrameOfReferenceUid());

        instance.addFrame( frame);
        saveOrUpdate( instance);
    }

}
