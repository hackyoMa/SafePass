<template>
  <el-form :model="regForm" status-icon :rules="regRules" ref="regForm">
    <el-form-item prop="username">
      <el-input type="text" class="loginUsername" v-model="regForm.username" auto-complete="on" placeholder="请输入注册邮箱或手机号" clearable></el-input>
    </el-form-item>
    <el-form-item prop="nickname">
      <el-input type="text" v-model="regForm.nickname" auto-complete="on" placeholder="请输入昵称" clearable></el-input>
    </el-form-item>
    <el-form-item prop="password">
      <el-input type="password" v-model="regForm.password" auto-complete="off" placeholder="请设置密码" clearable></el-input>
    </el-form-item>
    <el-form-item prop="checkPassword">
      <el-input type="password" v-model="regForm.checkPassword" @keyup.enter.native="submitForm('regForm')" auto-complete="off" placeholder="请再次输入密码" clearable></el-input>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" class="loginBtn" @click="submitForm('regForm')">注册</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
  export default {
    data() {
      let validateUsername = (rule, value, callback) => {
        const regular = new RegExp(/^[\w-]+(\.[\w-]+)*@([\w-]+\.)+[a-zA-Z]+$/);
        const regular2 = new RegExp(/^1\d{10}$/);
        if (value === '') {
          callback(new Error('请输入注册邮箱或手机号'));
        } else if (!regular.test(value) && !regular2.test(value)) {
          callback(new Error('邮箱或手机号格式不正确'));
        } else if (value.length < 5 || value.length > 32) {
          callback(new Error('邮箱长度必须在5-32位之间'));
        } else {
          this.$api.get('/user/usernameExists', {
            'username': value
          }, res => {
            if (res.data === false) {
              callback();
            } else {
              callback(new Error('邮箱或手机号已被注册'));
            }
          });
        }
      };
      let validateNickname = (rule, value, callback) => {
        const regular = new RegExp(/^[\s]+$/);
        if (value === '' || regular.test(value)) {
          callback(new Error('请输入注册昵称'));
        } else if (value.length < 1 || value.length > 16) {
          callback(new Error('昵称长度必须在1-16位之间'));
        } else {
          callback();
        }
      };
      let validatePassword = (rule, value, callback) => {
        const regular = new RegExp(/^(?![^a-zA-Z]+$)(?!\D+$).{8,32}$/);
        if (value === '') {
          callback(new Error('请设置密码'));
        } else if (!regular.test(value)) {
          callback(new Error('密码长度必须在8-32位之间，且必须包含数字和字母'));
        } else {
          if (this.regForm.checkPassword !== '') {
            this.$refs.regForm.validateField('checkPassword');
          }
          callback();
        }
      };
      let validateCheckPassword = (rule, value, callback) => {
        const regular = new RegExp(/^(?![^a-zA-Z]+$)(?!\D+$).{8,32}$/);
        if (value === '') {
          callback(new Error('请再次输入密码'));
        } else if (!regular.test(value)) {
          callback(new Error('密码长度必须在8-32位之间，且必须包含数字和字母'));
        } else if (value !== this.regForm.password) {
          callback(new Error('两次输入密码不一致'));
        } else {
          callback();
        }
      };
      return {
        regForm: {
          username: '',
          nickname: '',
          password: '',
          checkPassword: ''
        },
        regRules: {
          username: [
            {validator: validateUsername, trigger: 'blur'}
          ],
          nickname: [
            {validator: validateNickname, trigger: 'blur'}
          ],
          password: [
            {validator: validatePassword, trigger: 'blur'}
          ],
          checkPassword: [
            {validator: validateCheckPassword, trigger: 'blur'}
          ]
        }
      };
    },
    methods: {
      submitForm(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.$api.post('/user/register', {
              'username': this.regForm.username,
              'nickname': this.regForm.nickname,
              'password': this.regForm.password
            }, res => {
              let data = res.data;
              if (data === 'success') {
                this.$message.success('注册成功，请登录');
                this.$router.push('login');
              } else if (data === 'usernameExist') {
                this.$message.warning('邮箱或手机号已被注册');
              } else {
                if (data['username'] !== undefined) {
                  this.$message.warning('邮箱或手机号出错');
                }
                if (data['nickname'] !== undefined) {
                  this.$message.warning('昵称出错');
                }
                if (data['password'] !== undefined) {
                  this.$message.warning('密码出错');
                }
              }
            });
          } else {
            return false;
          }
        });
      }
    }
  };
</script>
