<script setup>
import { ref } from 'vue';

const vertical = ref(false);

const toolbar = (action) => {
    if (action === 'direction') {
        vertical.value = !vertical.value;
    }
};

defineExpose({
    toolbar,
});
</script>

<template>
    <div :class="'workspace workspace-' + (vertical ? 'vertical' : 'horizontal')">
        隣宅の雌猫、三毛子に吾輩は恋焦がれていたが、恋が実る前に彼女は風邪をこじらせて死んでしまう。
        この失恋は吾輩にとって大きな経験となる。
        その後も珍野家で暮らしながらさまざまな人間と出会う中で、彼は人間や物事を注意深く観察し、哲学するようになる。 脚を4本もっているのに2本しか使わない贅沢さ。</div>
</template>

<style>
.workspace {
    position: absolute;
    right: 0;
    bottom: 0;
    overflow: auto;
    padding: 8px;
}

.workspace-horizontal {
    left: 80px;
    top: 42px;
}

.workspace-vertical {
    left: 0;
    top: 122px;
    writing-mode: vertical-rl;
}
</style>