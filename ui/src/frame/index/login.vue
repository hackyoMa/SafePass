<template>
  <el-form :model="loginForm" status-icon :rules="loginRules" ref="loginForm">
    <el-form-item prop="username">
      <el-input type="text" class="loginUsername" v-model="loginForm.username" auto-complete="on" placeholder="请输入邮箱或手机号" clearable></el-input>
    </el-form-item>
    <el-form-item prop="password">
      <el-input type="password" v-model="loginForm.password" @keyup.enter.native="submitForm('loginForm')" auto-complete="off" placeholder="请输入密码" clearable></el-input>
    </el-form-item>
    <el-form-item prop="remember">
      <el-checkbox v-model="loginForm.remember">保持登录状态</el-checkbox>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" class="loginBtn" @click="submitForm('loginForm')">登录</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
  export default {
    data() {
      let validateEmail = (rule, value, callback) => {
        const regular = new RegExp(/^[\w-]+(\.[\w-]+)*@([\w-]+\.)+[a-zA-Z]+$/);
        const regular2 = new RegExp(/^1\d{10}$/);
        if (value === '') {
          callback(new Error('请输入邮箱或手机号'));
        } else if (!regular.test(value) && !regular2.test(value)) {
          callback(new Error('邮箱或手机号格式不正确'));
        } else if (value.length < 5 || value.length > 32) {
          callback(new Error('邮箱或手机号格式不正确'));
        } else {
          callback();
        }
      };
      let validatePassword = (rule, value, callback) => {
        const regular = new RegExp(/^(?![^a-zA-Z]+$)(?!\D+$).{8,32}$/);
        if (value === '') {
          callback(new Error('请输入密码'));
        } else if (!regular.test(value)) {
          callback(new Error('密码错误'));
        } else {
          callback();
        }
      };
      return {
        loginForm: {
          username: '',
          password: '',
          remember: false
        },
        loginRules: {
          username: [
            {validator: validateEmail, trigger: 'blur'}
          ],
          password: [
            {validator: validatePassword, trigger: 'blur'}
          ]
        }
      };
    },
    methods: {
      submitForm(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.$api.post('/user/login', {
              "username": this.loginForm.username,
              "password": this.loginForm.password
            }, res => {
              const data = res.data;
              this.$message.success('登录成功');
              this.$api.removeToken();
              if (this.loginForm.remember) {
                localStorage.setItem('token', data);
              } else {
                sessionStorage.setItem('token', data);
              }
              this.$api.setToken(data);
              const redirect = this.$route.query.redirect;
              if (redirect !== undefined) {
                this.$router.push(redirect);
              } else {
                this.$router.push('content');
              }
            }, err => {
              if (err === 'loginFailed') {
                this.$message.warning('用户名或密码错误');
              }
            });
          } else {
            return false;
          }
        })
        ;
      }
    }
  };
</script>
