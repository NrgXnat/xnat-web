package org.nrg.xnat.entities;

import lombok.extern.slf4j.Slf4j;
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
    private String ImageOrientationPatient;
    private String PixelSpacing;
    private String FrameOfReferenceUid;

    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "instance_id")
    private DicomInstance dicomInstance;

    public DicomFrame() {
    }

    public DicomFrame(int frameNumber, String imagePositionPatient, String ImageOrientationPatient, String PixelSpacing, String FrameOfReferenceUid) {
        this.frameNumber = frameNumber;
        this.imagePositionPatient = imagePositionPatient;
        this.ImageOrientationPatient = ImageOrientationPatient;
        this.PixelSpacing = PixelSpacing;
        this.FrameOfReferenceUid = FrameOfReferenceUid;
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
        return ImageOrientationPatient;
    }

    public void setImageOrientationPatient(String imageOrientationPatient) {
        ImageOrientationPatient = imageOrientationPatient;
    }

    public String getPixelSpacing() {
        return PixelSpacing;
    }

    public void setPixelSpacing(String pixelSpacing) {
        PixelSpacing = pixelSpacing;
    }

    public String getFrameOfReferenceUid() {
        return FrameOfReferenceUid;
    }

    public void setFrameOfReferenceUid(String frameOfReferenceUid) {
        FrameOfReferenceUid = frameOfReferenceUid;
    }
}