import { reactive } from "vue";

export const store = reactive({
    nodes: {},
    focus: '',
    node() {
        return this.nodes[this.focus];
    },
});