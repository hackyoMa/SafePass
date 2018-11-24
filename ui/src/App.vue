<template>
  <div id="app">
    <navigation></navigation>
    <router-view></router-view>
    <copyright></copyright>
    <particles></particles>
  </div>
</template>

<script>
  import navigation from './components/navigation'
  import copyright from './components/copyright'
  import particles from './components/particles'

  export default {
    name: 'app',
    components: {
      navigation,
      copyright,
      particles
    },
    created() {
      const localToken = localStorage.getItem('token');
      const sessionToken = sessionStorage.getItem('token');
      if (localToken !== null || sessionToken !== null) {
        const token = localToken === null ? sessionToken : localToken;
        this.$api.setToken(token);
      }
      console.log([
        "                   _ooOoo_",
        "                  o8888888o",
        "                  88\" . \"88",
        "                  (| -_- |)",
        "                  O\\  =  /O",
        "               ____/`---'\\____",
        "             .'  \\\\|     |//  `.",
        "            /  \\\\|||  :  |||//  \\",
        "           /  _||||| -:- |||||-  \\",
        "           |   | \\\\\\  -  /// |   |",
        "           | \\_|  ''\\---/''  |   |",
        "           \\  .-\\__  `-`  ___/-. /",
        "         ___`. .'  /--.--\\  `. . __",
        "      .\"\" '<  `.___\\_<|>_/___.'  >'\"\".",
        "     | | :  `- \\`.;`\\ _ /`;.`/ - ` : | |",
        "     \\  \\ `-.   \\_ __\\ /__ _/   .-` /  /",
        "======`-.____`-.___\\_____/___.-`____.-'======",
        "                   `=---='",
        "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^",
        "      别看了，我有佛祖加持，你破解不了的     "
      ].join('\n'));
    }
  };
</script>

<style lang="scss">
  @import "./style/style.scss";
  @import "./style/base/_color.scss";
  @import "./style/base/_utils.scss";
</style>
