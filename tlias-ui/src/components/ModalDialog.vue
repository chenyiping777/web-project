<!-- 通用模态弹窗（支持 v-model:visible 双向绑定）
     属性：visible 是否显示 / title 标题 / large 是否加宽
     事件：update:visible（v-model）/ close 关闭回调
     插槽：默认插槽放表单内容，footer 插槽放底部按钮 -->
<script setup>
defineProps({
  visible: { type: Boolean, default: false },
  title: { type: String, default: '' },
  large: { type: Boolean, default: false }
})

// 同时支持 v-model:visible 与 @close 两种用法
const emit = defineEmits(['update:visible', 'close'])

// 关闭弹窗：同步更新绑定值并触发 close 事件
function close() {
  emit('update:visible', false)
  emit('close')
}
</script>

<template>
  <!-- v-if 控制整个遮罩层的挂载 -->
  <div v-if="visible" class="modal-mask" @click.self="close">
    <div class="modal" :class="{ large }">
      <div class="modal-header">
        <span>{{ title }}</span>
        <button class="modal-close" @click="close">✕</button>
      </div>
      <div class="modal-body">
        <slot />
      </div>
      <div class="modal-footer">
        <!-- 底部按钮区域，默认提供取消按钮 -->
        <slot name="footer">
          <button class="btn" @click="close">取消</button>
        </slot>
      </div>
    </div>
  </div>
</template>
