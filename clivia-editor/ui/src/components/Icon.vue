<script setup>
import { ref } from 'vue';
const props = defineProps({
    name: String
});

const icons = ref({
    backlog: new URL('../assets/backlog.svg', import.meta.url).href,
    bold: new URL('../assets/bold.svg', import.meta.url).href,
    direction: new URL('../assets/direction.svg', import.meta.url).href,
    divider: new URL('../assets/divider.svg', import.meta.url).href,
    header: new URL('../assets/header.svg', import.meta.url).href,
    image: new URL('../assets/image.svg', import.meta.url).href,
    italic: new URL('../assets/italic.svg', import.meta.url).href,
    linethrough: new URL('../assets/linethrough.svg', import.meta.url).href,
    link: new URL('../assets/link.svg', import.meta.url).href,
    quote: new URL('../assets/quote.svg', import.meta.url).href,
    redo: new URL('../assets/redo.svg', import.meta.url).href,
    underline: new URL('../assets/underline.svg', import.meta.url).href,
    undo: new URL('../assets/undo.svg', import.meta.url).href,
});
</script>

<template>
    <div :class="'icon icon-' + name">
        <img :src="icons[name]" alt="" />
    </div>
</template>

<style>
.icon {
    padding: 4px;
}

.icon img {
    display: block;
    width: 16px;
    height: 16px;
}
</style>