package org.nrg.xapi.model.customVariables;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.nrg.xdat.entities.CustomVariable;

import java.util.List;

/**
 * Created by mike on 4/16/18.
 */
@ApiModel(description = "Contains the properties that define a custom variable on the system.")
public class VariableModel {

    @ApiModelProperty(value = "The custom variable's name.")
    public String getName() {
        return name;
    }

    public void setName(String _name) {
        this.name = _name;
    }

    @ApiModelProperty(value = "The custom variable's type.")
    public String getType() {
        return type;
    }

    public void setType(String _type) {
        this.type = _type;
    }

    @ApiModelProperty(value = "Whether the custom variable is required.")
    public String getRequired() {
        return required;
    }

    public void setRequired(String _required) {
        this.required = _required;
    }

    @ApiModelProperty(value = "The custom variable's possible values.")
    public List<String> getPossibleValues() {
        return possibleValues;
    }

    public void setPossibleValues(List<String> _possibleValues) {
        this.possibleValues = _possibleValues;
    }

    public VariableModel(String name, String type, String required, List<String> possibleValues) {
        this.name = name;
        this.type = type;
        this.required = required;
        this.possibleValues = possibleValues;
    }

    public VariableModel() {
    }

    @Override
    public String toString() {
        return "VariableModel{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", required='" + required + '\'' +
                ", possibleValues=" + possibleValues +
                '}';
    }

    private String                       name;
    private String                       type;
    private String required;
    private List<String> possibleValues;
}
