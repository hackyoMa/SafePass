<template>
  <div class="center">
    <img src="/static/image/logo347x74@2x.png" class="indexTitle" alt="SafePass图标"/>
    <p class="normSubtitleColor indexSubtitle">最安全的密码管理工具</p>
    <div class="indexInputGroup">
      <el-tabs type="border-card" v-model="indexActiveName" @tab-click="indexTabClick">
        <el-tab-pane label="登录" name="login"></el-tab-pane>
        <el-tab-pane label="注册" name="reg"></el-tab-pane>
        <router-view></router-view>
      </el-tabs>
    </div>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        indexActiveName: this.$route.name
      };
    },
    watch: {
      '$route': 'toView'
    },
    methods: {
      toView() {
        this.indexActiveName = this.$route.name;
      },
      indexTabClick(tab) {
        if (tab.name === 'reg') {
          this.$router.push('reg');
        } else {
          this.$router.push('login');
        }
      }
    }
  };
</script>

<style>
  @import "../style/scss/_index.scss";
</style>
