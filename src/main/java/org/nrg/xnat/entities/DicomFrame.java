package org.nrg.xnat.entities;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntity;

import javax.persistence.*;
import java.util.Map;
import java.util.Set;

/**
 * Capture DICOM frame-level attributes.
 *
 */
@Slf4j
@Entity
@Table
@Audited
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "nrg")
public class DicomFrame extends AbstractHibernateEntity {
    private int frameNumber;
    private String imagePositionPatient;
    private String imageOrientationPatient;
    private String pixelSpacing;
    private String frameOfReferenceUid;

    @ManyToOne(fetch = FetchType.LAZY)
    private DicomInstance dicomInstance;

    public DicomFrame() {
    }

    public DicomFrame( DicomInstance dicomInstance, int frameNumber, String imagePositionPatient, String ImageOrientationPatient, String PixelSpacing, String FrameOfReferenceUid) {
        this.dicomInstance = dicomInstance;
        this.frameNumber = frameNumber;
        this.imagePositionPatient = imagePositionPatient;
        this.imageOrientationPatient = ImageOrientationPatient;
        this.pixelSpacing = PixelSpacing;
        this.frameOfReferenceUid = FrameOfReferenceUid;
    }

    public DicomInstance getDicomInstance() {
        return dicomInstance;
    }

    public void setDicomInstance(DicomInstance dicomInstance) {
        this.dicomInstance = dicomInstance;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public void setFrameNumber(int frameNumber) {
        this.frameNumber = frameNumber;
    }

    public String getImagePositionPatient() {
        return imagePositionPatient;
    }

    public void setImagePositionPatient(String imagePositionPatient) {
        this.imagePositionPatient = imagePositionPatient;
    }

    public String getImageOrientationPatient() {
        return imageOrientationPatient;
    }

    public void setImageOrientationPatient(String imageOrientationPatient) {
        this.imageOrientationPatient = imageOrientationPatient;
    }

    public String getPixelSpacing() {
        return pixelSpacing;
    }

    public void setPixelSpacing(String pixelSpacing) {
        this.pixelSpacing = pixelSpacing;
    }

    public String getFrameOfReferenceUid() {
        return frameOfReferenceUid;
    }

    public void setFrameOfReferenceUid(String frameOfReferenceUid) {
        this.frameOfReferenceUid = frameOfReferenceUid;
    }

    @Override
    public boolean equals(Object o) {
        boolean b = super.equals(o);
        if( b == true) {
            DicomFrame that = (DicomFrame) o;
            b = frameNumber == that.getFrameNumber()
                    && compareStrings( imagePositionPatient, that.getImagePositionPatient())
                    && compareStrings( imageOrientationPatient, that.getImageOrientationPatient())
                    && compareStrings( pixelSpacing, that.getPixelSpacing())
                    && compareStrings( frameOfReferenceUid, that.getFrameOfReferenceUid());
        }
        return b;
    }

    private boolean compareStrings( String first, String second) {
        // If they're both not null, we can just compare the times.
        if (ObjectUtils.allNotNull(first, second)) {
            return first.equals( second);
        }
        // If they're not both null, then they're not equal.
        return !ObjectUtils.anyNotNull(first, second);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}