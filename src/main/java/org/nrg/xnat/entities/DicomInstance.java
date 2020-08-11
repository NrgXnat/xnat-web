package org.nrg.xnat.entities;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntity;

import javax.persistence.*;
import java.util.*;

/**
 * Capture DICOM instance-level attributes.
 *
 */
@Slf4j
@Entity
@Table
@Audited
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "nrg")
public class DicomInstance extends AbstractHibernateEntity {
    private String sopinstanceuid;
    private String sopclassuid;
    private int instanceNumber;
    private String filePath;

    // fk to imagescandata.
    @Column( nullable = false)
    private long imagescandata_id;

    private int rows;
    private int cols;
    private int bits_allocated;
    private int frame_count;

    @OneToMany(mappedBy = "dicomInstance", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DicomFrame> frames = new HashSet<>();

    public DicomInstance(){}

    public DicomInstance(String sopinstanceuid, String sopclassuid, int instanceNumber, int rows, int cols, int bits_allocated, int frame_count)  {
        this.sopinstanceuid = sopinstanceuid;
        this.sopclassuid = sopclassuid;
        this.instanceNumber = instanceNumber;
        this.rows = rows;
        this.cols = cols;
        this.bits_allocated = bits_allocated;
        this.frame_count = frame_count;
    }

    public String getSopinstanceuid() {
        return sopinstanceuid;
    }

    public void setSopinstanceuid(String sopinstanceuid) {
        this.sopinstanceuid = sopinstanceuid;
    }

    public String getSopclassuid() {
        return sopclassuid;
    }

    public void setSopclassuid(String sopclassuid) {
        this.sopclassuid = sopclassuid;
    }

    public int getInstanceNumber() {
        return instanceNumber;
    }

    public void setInstanceNumber(int instanceNumber) {
        this.instanceNumber = instanceNumber;
    }

    public long getImagescandata_id() {
        return imagescandata_id;
    }

    public void setImagescandata_id(long imagescandata_id) {
        this.imagescandata_id = imagescandata_id;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public int getBits_allocated() {
        return bits_allocated;
    }

    public void setBits_allocated(int bits_allocated) {
        this.bits_allocated = bits_allocated;
    }

    public int getFrame_count() {
        return frames.size();
    }

    public void setFrame_count(int frame_count) {
        this.frame_count = frame_count;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void addFrame(DicomFrame frame) {
        frames.add( frame);
        frame.setDicomInstance( this);
    }

    public void removeFrame( DicomFrame frame) {
        frames.remove( frame);
        frame.setDicomInstance( null);
    }
}
