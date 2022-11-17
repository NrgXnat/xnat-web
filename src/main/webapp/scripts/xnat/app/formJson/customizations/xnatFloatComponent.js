class XnatFloatComponent extends NumberComponent {
    static schema(...extend) {
        return NumberComponent.schema(
            {
                type: "xnatFloat",
            },
            ...extend
        );
    }


    static get builderInfo() {
        return {
            title: 'X-Float',
            icon: 'hashtag',
            group: 'basic',
            documentation: '/userguide/forms/form-components#number',
            weight: 32,
            schema: XnatFloatComponent.schema()
        };
    }

    setInputMask(input) {
        super.setInputMask(input);
        let numberPattern = '^[\\-\\+]?[0-9';
        numberPattern += this.decimalSeparator || '';
        numberPattern += this.delimiter || '';
        numberPattern += ']*$';
        input.setAttribute('pattern', numberPattern);
    }


    // get inputInfo() {
   //     const info = super.inputInfo;
   //     if (this.component.mask) {
   //         info.attr.type = 'password';
   //     }
   //     else {
   //         info.attr.type = 'number';
   //     }
   //     info.attr.inputmode = super.isDecimalAllowed() ? 'decimal' : 'numeric';
   //     info.changeEvent = 'input';
   //     return info;
   // }


}

Formio.Components.addComponent("xnatFloat", XnatFloatComponent);
