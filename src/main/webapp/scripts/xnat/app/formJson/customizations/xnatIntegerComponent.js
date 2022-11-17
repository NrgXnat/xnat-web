const NumberComponent = Formio.Components.components.number;
class XnatIntegerComponent extends NumberComponent {
    static schema(...extend) {
        return NumberComponent.schema(
            {
                type: "xnatInteger",
                mask: false,
                tableView: true,
                alwaysEnabled:  false,
                delimiter: false,
                requireDecimal:  false,
                encrypted: false,
                validate: {
                    integer: true,
                    step: 1
                }
            },
            ...extend
        );
    }


    static get builderInfo() {
        return {
            title: 'X-Integer',
            icon: 'hashtag',
            group: 'basic',
            documentation: '/userguide/forms/form-components#number',
            weight: 31,
            schema: XnatIntegerComponent.schema()
        };
    }



    get inputInfo() {
        const info = super.inputInfo;
        if (this.component.mask) {
            info.attr.type = 'password';
        }
        else {
            info.attr.type = 'number';
        }
        info.attr.inputmode = super.isDecimalAllowed() ? 'decimal' : 'numeric';
        info.changeEvent = 'input';
        return info;
    }


}

Formio.Components.addComponent("xnatInteger", XnatIntegerComponent);
