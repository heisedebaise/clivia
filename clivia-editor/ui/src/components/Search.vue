<script setup>
import { ref, onMounted } from 'vue';
import { message } from './locale';
import { listen } from './event';
import Icon from './Icon.vue';

const data = ref({
    show: false,
    search: '',
    replace: '',
    result: '',
    up: false,
    down: false,
    one: false,
    all: false,
});

const show = () => {
    data.value.show = true;
};

onMounted(() => {
    listen('search', show);
});
</script>

<template>
    <div v-if="data.show" class="search">
        <div class="line">
            <input :placeholder="message('search.placeholder')" v-model="data.search" />
            <div class="result">{{ data.result }}</div>
            <div :class="'button ' + (data.up ? 'enable' : '')">
                <Icon name="arrow-up" :enable="data.up" />
            </div>
            <div :class="'button ' + (data.down ? 'enable' : '')">
                <Icon name="arrow-down" :enable="data.down" />
            </div>
            <div class="button enable" @click="data.show = false">
                <Icon name="close" :enable="true" />
            </div>
        </div>
        <div class="line">
            <input :placeholder="message('search.replace.placeholder')" v-model="data.replace" />
            <div :class="'button ' + (data.one ? 'enable' : '')">
                <Icon name="up" :enable="data.one" />
            </div>
            <div :class="'button ' + (data.all ? 'enable' : '')">
                <Icon name="down" :enable="data.all" />
            </div>
        </div>
    </div>
</template>

<style scoped>
.search {
    position: fixed;
    top: 8px;
    right: 8px;
    background-color: var(--background);
    border: 1px solid var(--border);
    border-radius: 4px;
    overflow: hidden;
    box-shadow: var(--shadow);
}

.line {
    display: flex;
    align-items: center;
}

.button {
    padding: 2px;
}

.enable {
    cursor: pointer;
}

.enable:hover {
    background-color: var(--hover-bg);
}
</style>