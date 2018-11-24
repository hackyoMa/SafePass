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
      <el-input type="password" v-model="regForm.checkPassword" auto-complete="off" placeholder="请再次输入密码" clearable></el-input>
    </el-form-item>
    <el-form-item prop="verifyCode">
      <el-input type="text" v-model="regForm.verifyCode" @keyup.enter.native="submitForm('regForm')" auto-complete="off" placeholder="请输入验证码" clearable>
        <template slot="append">
          <div class="verifyCodeSize"></div>
          <img :src="verifyCodeImg" class="verifyCodeImg" alt="验证码图片" @click="getVerifyCode()"/>
        </template>
      </el-input>
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
      let validateVerifyCode = (rule, value, callback) => {
        if (value === '') {
          callback(new Error('请输入验证码'));
        } else if (value.length !== 4) {
          callback(new Error('验证码错误'));
        } else {
          this.$api.get('/user/validateVerifyCode', {
            'verifyCode': value
          }, res => {
            if (res.data === true) {
              callback();
            } else {
              callback(new Error('验证码错误'));
            }
          });
        }
      };
      return {
        regForm: {
          username: '',
          nickname: '',
          password: '',
          checkPassword: '',
          verifyCode: '',
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
          ],
          verifyCode: [
            {validator: validateVerifyCode, trigger: 'blur'}
          ]
        },
        verifyCodeImg: ''
      };
    },
    mounted() {
      this.getVerifyCode();
    },
    methods: {
      getVerifyCode() {
        this.$api.get('/user/getVerifyCode', {}, res => {
          this.verifyCodeImg = 'data:image/png;base64,' + res.data;
        });
      },
      submitForm(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.$api.post('/user/register', {
              'username': this.regForm.username,
              'nickname': this.regForm.nickname,
              'password': this.regForm.password,
              "verifyCode": this.regForm.verifyCode
            }, res => {
              let data = res.data;
              if (data === 'success') {
                this.$message.success('注册成功，请登录');
                this.$router.push('login');
              } else if (data === 'verifyCodeError') {
                this.$message.error('验证码错误');
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
              this.getVerifyCode();
            });
          } else {
            return false;
          }
        });
      }
    }
  };
</script>
