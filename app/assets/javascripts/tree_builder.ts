/// <reference path="typings/main.d.ts" />

module TreeBuilder {
    export class TreeContainer {
        private input: HTMLElement;

        constructor(input: HTMLInputElement) {
            this.input = input;

            $(input).keypress((event: JQueryEventObject) => {
                if (event.charCode == 13) {
                    this.renderText(input.value);
                    event.preventDefault();
                }
            });
        }

        private renderText(input: string): void {
            $.getJSON('parse', {expression: input}, (data: any, textStatus: string, jqXHR: JQueryXHR) => {
                console.log(textStatus);
                console.log(data);
            })
        }

        private populate(data: any, nodes: [any], edges: [any]) {
            
        }
    }
}