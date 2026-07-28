<script setup lang="ts">
import { onLaunch } from '@dcloudio/uni-app'
import { useUserStore } from '@/stores/user'
onLaunch(() => useUserStore().restore())
</script>
<style lang="scss">
page{background:#f6f7f5;color:#17211d;font-family:"PingFang SC","Microsoft YaHei",sans-serif}button::after{border:0}.safe-bottom{padding-bottom:env(safe-area-inset-bottom)}
</style>
