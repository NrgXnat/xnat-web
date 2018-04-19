package org.nrg.xapi.model.customVariables;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by mike on 4/16/18.
 */
@ApiModel(description = "Contains the properties that define a custom variable set on the system.")
public class VariableSetModel {

    @ApiModelProperty(value = "The custom variable set's name.")
    public String getName() {
        return name;
    }
    public void setName(String _name) {
        this.name = _name;
    }
    @ApiModelProperty(value = "The custom variable set's datatype.")
    public String getDatatype() {
        return datatype;
    }
    public void setDatatype(String _datatype) {
        this.datatype = _datatype;
    }
    @ApiModelProperty(value = "The custom variable set's description.")
    public String getDescription() {
        return description;
    }
    public void setDescription(String _description) {
        this.description = _description;
    }
    @ApiModelProperty(value = "Whether the custom variable set is project specific.")
    public boolean isProjectSpecific() {
        return projectSpecific;
    }
    public void setProjectSpecific(boolean _projectSpecific) {
        this.projectSpecific = _projectSpecific;
    }
    @ApiModelProperty(value = "The custom variable set's parent set.")
    public List<VariableModel> getVariables() {
        return variables;
    }
    public void setVariables(List<VariableModel> _variables) {
        this.variables = _variables;
    }
    @ApiModelProperty(value = "The custom variable set's owning project.")
    public String getOwningProjectId() {
        return owningProjectId;
    }
    public void setOwningProjectId(String _owningProjectId) {
        this.owningProjectId = _owningProjectId;
    }

    public VariableSetModel(String name, String datatype, String description, boolean projectSpecific, List<VariableModel> variables, String owningProjectId) {
        this.name = name;
        this.datatype = datatype;
        this.description = description;
        this.projectSpecific = projectSpecific;
        this.variables = variables;
        this.owningProjectId = owningProjectId;
    }

    public VariableSetModel() {
    }

    @Override
    public String toString() {
        return "VariableSetModel{" +
                "name='" + name + '\'' +
                ", datatype='" + datatype + '\'' +
                ", description='" + description + '\'' +
                ", projectSpecific=" + projectSpecific +
                ", variables=" + variables +
                ", owningProjectId='" + owningProjectId + '\'' +
                '}';
    }

    private String                       name;
    private String                       datatype;
    private String                       description;
    private boolean                      projectSpecific;
    private List<VariableModel> variables;
    private String                       owningProjectId;
}
