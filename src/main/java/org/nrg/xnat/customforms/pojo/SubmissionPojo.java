/*
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2021, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * @author: Mohana Ramaratnam (mohana@radiologics.com)
 * @since: 07-03-2021
 */
package org.nrg.xnat.customforms.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.nrg.xnat.customforms.pojo.formio.RowIdentifier;

import java.util.List;

/*
 * POJO to represent a submission
 */
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionPojo {

    private String idCustomVariableFormAppliesTo = "-1" + RowIdentifier.DELIMITER + "-1";

    @JsonProperty(required = true)
    private String formTitle;

    @JsonProperty(required = true)
    private String formType;

    @JsonProperty(required = true)
    private int zIndex;

    @JsonProperty(required = true)
    private ComponentPojo xnatDatatype;

    @JsonProperty(required = true)
    private List<ComponentPojo> xnatProtocol;

    @JsonProperty(required = true)
    private List<ComponentPojo> xnatVisit;

    @JsonProperty(required = true)
    private List<ComponentPojo> xnatSubtype;

    private ComponentPojo xnatScanType;

    @JsonProperty(required = true)
    private String isThisASiteWideConfiguration;

    @JsonProperty(required = true)
    private List<ComponentPojo> xnatProject;

    public String getIdCustomVariableFormAppliesTo() {
        return idCustomVariableFormAppliesTo;
    }

    public void setIdCustomVariableFormAppliesTo(String dbPkId) {
        this.idCustomVariableFormAppliesTo = dbPkId;
    }

    public String getFormTitle() {
        return formTitle;
    }

    public void setFormTitle(String formTitle) {
        this.formTitle = formTitle;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public ComponentPojo getXnatDatatype() {
        return xnatDatatype;
    }

    public void setXnatDatatype(ComponentPojo xnatDatatype) {
        this.xnatDatatype = xnatDatatype;
    }

    public List<ComponentPojo> getXnatProtocol() {
        return xnatProtocol;
    }

    public void setXnatProtocol(List<ComponentPojo> xnatProtocol) {
        this.xnatProtocol = xnatProtocol;
    }

    public List<ComponentPojo> getXnatVisit() {
        return xnatVisit;
    }

    public void setXnatVisit(List<ComponentPojo> xnatVisit) {
        this.xnatVisit = xnatVisit;
    }

    public List<ComponentPojo> getXnatSubtype() {
        return xnatSubtype;
    }

    public void setXnatSubtype(List<ComponentPojo> xnatSubtype) {
        this.xnatSubtype = xnatSubtype;
    }

    public ComponentPojo getXnatScanType() {
        return xnatScanType;
    }

    public void setXnatScanType(ComponentPojo xnatScanType) {
        this.xnatScanType = xnatScanType;
    }

    public String getIsThisASiteWideConfiguration() {
        return isThisASiteWideConfiguration;
    }

    public void setIsThisASiteWideConfiguration(String isThisASiteWideConfiguration) {
        this.isThisASiteWideConfiguration = isThisASiteWideConfiguration;
    }

    public List<ComponentPojo> getXnatProject() {
        return xnatProject;
    }

    public void setXnatProject(List<ComponentPojo> xnatProject) {
        this.xnatProject = xnatProject;
    }

    public int getzIndex() {
        return zIndex;
    }

    public void setzIndex(int zIndex) {
        this.zIndex = zIndex;
    }
}
