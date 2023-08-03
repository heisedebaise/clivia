import { reactive } from "vue";

export const store = reactive({
    nodes: {},
    main: '',
    focus: '',
    node() {
        return this.nodes[this.focus];
    },
});