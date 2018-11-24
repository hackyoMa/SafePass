<template>
  <div>
    <el-row type="flex" justify="center">
      <el-col :xs="24" :sm="20" :md="16" :lg="12" :xl="8">
        <el-form :inline="true" :model="searchForm" status-icon :rules="searchRules" ref="searchForm" class="searchForm">
          <el-form-item prop="value">
            <el-input type="text" v-model="searchForm.value" auto-complete="on" placeholder="搜索描述" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitForm('searchForm')">搜索</el-button>
          </el-form-item>
        </el-form>
        <div class="infoBorder">
          <el-table :data="infoTable" max-height="463">
            <el-table-column type="expand">
              <template slot-scope="props">
                <el-form label-position="left" inline>
                  <el-form-item label="标签">
                    <el-tag v-if="props.row.labels.length!==0" v-for="label in props.row.labels" :key="label" class="infoLabel">
                      <div class="clipboard pointer" :data-clipboard-text="label">{{ label }}</div>
                    </el-tag>
                    <el-tag v-if="props.row.labels.length===0" class="infoLabel">无</el-tag>
                  </el-form-item>
                  <br/>
                  <el-form-item label="操作">
                    <el-button-group>
                      <el-button size="mini" icon="el-icon-edit" plain @click="openEditInfo(props.row)"></el-button>
                      <el-button size="mini" type="danger" plain icon="el-icon-delete" @click="setDeleteInfoId(props.row.id)"></el-button>
                    </el-button-group>
                  </el-form-item>
                </el-form>
              </template>
            </el-table-column>
            <el-table-column label="描述">
              <template slot-scope="props">
                <div class="clipboard pointer" :data-clipboard-text="props.row.description">{{ props.row.description }}</div>
              </template>
            </el-table-column>
            <el-table-column label="内容">
              <template slot-scope="props">
                <div class="clipboard pointer" :data-clipboard-text="props.row.content">{{ props.row.content }}</div>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="pagination">
          <el-pagination small background @current-change="selectInfo" :current-page.sync="currentPage" :page-size="sizePage" layout="prev, pager, next" :total="totalPage"></el-pagination>
          <div class="addBtnDiv">
            <el-button plain type="primary" @click="editFormVisible = true" class="addBtn">新增</el-button>
          </div>
        </div>
      </el-col>
    </el-row>
    <el-dialog title="删除信息" :visible.sync="deleteConfirmVisible" :lock-scroll="false" width="349px">
      <span class="normSubtitleSize">删除后将无法恢复</span>
      <div slot="footer" class="dialog-footer">
        <el-button @click="deleteConfirmVisible = false">取消</el-button>
        <el-button type="danger" @click="deleteInfo">删除</el-button>
      </div>
    </el-dialog>
    <el-dialog :title="editMode ? '编辑信息':'新增信息'" :visible.sync="editFormVisible" :before-close="editFormClose" :lock-scroll="false" :close-on-click-modal="false" width="349px">
      <el-form :model="editForm" status-icon :rules="editRules" ref="editForm">
        <el-form-item prop="description" label="描述">
          <el-input type="text" v-model="editForm.description" auto-complete="on" placeholder="请输入描述" clearable></el-input>
        </el-form-item>
        <el-form-item prop="content" label="信息">
          <div @click="openGenerate">
            <el-checkbox v-model="editForm.autoGenerate">自动生成</el-checkbox>
          </div>
          <el-input type="text" v-model="editForm.content" auto-complete="on" placeholder="请输入信息" clearable></el-input>
        </el-form-item>
        <el-form-item prop="labels" label="标签">
          <el-tag :key="tag" v-for="tag in editForm.labels" closable :disable-transitions="false" @close="removeTagConfirm(tag)">
            {{ tag }}
          </el-tag>
          <div v-if="!addTagInputVisible && editForm.labels.length<10" class="addTagDiv">
            <el-button type="primary" plain class="addTag" size="small" @click="showAddTagInput">新标签</el-button>
          </div>
          <div v-if="addTagInputVisible" class="addTagDiv">
            <el-input class="addTag" v-model="addTagInputValue" ref="addTagInput" size="small" @keyup.enter.native="addTagConfirm" @blur="addTagConfirm" auto-complete="on" placeholder="标签名"></el-input>
          </div>
        </el-form-item>
      </el-form>
      <div v-if="editForm.autoGenerate">
        <span>长度</span>
        <el-slider v-model="autoGenerate.extent" :min="1" :max="64" @change="generateContent()" show-input :show-input-controls="false"></el-slider>
        <span>字母</span>
        <el-slider v-model="autoGenerate.letter" :min="0" :max="64" @change="generateContent()" show-input :show-input-controls="false"></el-slider>
        <span>数字</span>
        <el-slider v-model="autoGenerate.number" :min="0" :max="64" @change="generateContent()" show-input :show-input-controls="false"></el-slider>
        <span>符号</span>
        <el-slider v-model="autoGenerate.symbol" :min="0" :max="64" @change="generateContent()" show-input :show-input-controls="false"></el-slider>
        <div class="center addBtnDiv">
          <el-switch v-model="autoGenerate.like" active-text="所有字母" inactive-text="禁止相似" @change="generateContent()"></el-switch>
          <br/>
          <div class="addBtnDiv">
            <el-button type="primary" size="small" plain @click="generateContent()">重新生成</el-button>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="resetEditForm()">取消</el-button>
        <el-button v-if="editMode" type="primary" @click="submitForm('editForm')">修改</el-button>
        <el-button v-else @click="submitForm('addForm')" type="primary">添加</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import Clipboard from 'clipboard'

  export default {
    data() {
      let validateValue = (rule, value, callback) => {
        const regular = new RegExp(/^[\s]+$/);
        if (value === '' || regular.test(value)) {
          callback(new Error('请输入搜索内容'));
          this.selectInfo();
        } else {
          callback();
        }
      };
      let validateDescription = (rule, value, callback) => {
        const regular = new RegExp(/^[\s]+$/);
        if (value === '' || regular.test(value)) {
          callback(new Error('请输入信息描述'));
        } else {
          this.$api.get('/info/descriptionExists', {
            'id': this.editForm.id,
            description: value
          }, res => {
            let data = res.data;
            if (data) {
              callback(new Error('该描述的信息已存在'));
            } else {
              callback();
            }
          });
        }
      };
      let validateContent = (rule, value, callback) => {
        const regular = new RegExp(/^[\s]+$/);
        if (value === '' || regular.test(value)) {
          callback(new Error('请输入信息内容'));
        } else {
          callback();
        }
      };
      return {
        searchForm: {
          value: ''
        },
        searchRules: {
          value: [
            {validator: validateValue, trigger: 'blur'}
          ]
        },
        editForm: {
          id: '',
          description: '',
          content: '',
          autoGenerate: false,
          labels: []
        },
        editRules: {
          description: [
            {validator: validateDescription, trigger: 'blur'}
          ],
          content: [
            {validator: validateContent, trigger: 'blur'}
          ]
        },
        autoGenerate: {
          extent: 16,
          letter: 8,
          number: 4,
          symbol: 4,
          like: false,
          letterList: 'acdefhjkmnprstwxyzBCEFGHJKLMNPQRTWXY',
          likeLetterList: 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ',
          numberList: '0123456789',
          symbolList: '~`!@#$%^&*()_+-={}[]:\"\';<>?,./|\\'
        },
        editFormVisible: false,
        addTagInputVisible: false,
        addTagInputValue: '',
        deleteConfirmVisible: false,
        deleteInfoId: null,
        currentPage: 1,
        totalPage: 100,
        sizePage: 10,
        infoTable: [],
        editMode: false
      };
    },
    created() {
      this.selectInfo();
      const clipboard = new Clipboard('.clipboard');
      clipboard.on('success', () => {
        this.$message.success('已复制到剪切板');
      });
      clipboard.on('error', () => {
        this.$message.error('复制失败，请手动复制');
      });
    },
    methods: {
      addInfo() {
        this.$api.post('/info/insert', {
          'description': this.editForm.description,
          'content': this.editForm.content,
          'labels': this.editForm.labels
        }, res => {
          let data = res.data;
          if (data === 'success') {
            this.$message.success('新增成功');
            this.selectInfo();
            this.resetEditForm();
          } else if (data === 'existed') {
            this.$message.warning('该描述的信息已存在');
          } else if (data === 'labelError') {
            this.$message.warning('信息标签格式错误');
          } else {
            if (data['description'] !== undefined) {
              this.$message.warning('信息描述不能为空');
            }
            if (data['content'] !== undefined) {
              this.$message.warning('信息内容不能为空');
            }
            if (data['labels'] !== undefined) {
              this.$message.warning('信息标签最多10个');
            }
          }
        });
      },
      openEditInfo(row) {
        this.editMode = true;
        this.editForm.id = row['id'];
        this.editForm.description = row['description'];
        this.editForm.content = row['content'];
        this.editForm.labels = row['labels'];
        this.editFormVisible = true;
      },
      editInfo() {
        this.$api.post('/info/update', {
          'id': this.editForm.id,
          'description': this.editForm.description,
          'content': this.editForm.content,
          'labels': this.editForm.labels
        }, res => {
          let data = res.data;
          if (data === 'success') {
            this.$message.success('修改成功');
            this.selectInfo();
            this.resetEditForm();
          } else if (data === 'existed') {
            this.$message.warning('该描述的信息已存在');
          } else if (data === 'labelError') {
            this.$message.warning('信息标签格式错误');
          } else {
            if (data['description'] !== undefined) {
              this.$message.warning('信息描述不能为空');
            }
            if (data['content'] !== undefined) {
              this.$message.warning('信息内容不能为空');
            }
            if (data['labels'] !== undefined) {
              this.$message.warning('信息标签最多10个');
            }
          }
        });
      },
      setDeleteInfoId(id) {
        this.deleteInfoId = id;
        this.deleteConfirmVisible = true;
      },
      deleteInfo() {
        this.$api.post('/info/delete', {
          'id': this.deleteInfoId
        }, res => {
          let data = res.data;
          if (data === 'success') {
            this.$message.success('删除成功');
            this.selectInfo();
            this.deleteConfirmVisible = false;
          } else {
            this.$message.error('发生错误');
          }
        });
      },
      selectInfo() {
        this.$api.get('/info/getForPage', {
          'page': this.currentPage
        }, res => {
          let data = res.data;
          this.totalPage = data['count'];
          this.infoTable = data['info'];
        });
      },
      searchInfo() {
        this.$api.get('/info/search', {
          'search': this.searchForm.value
        }, res => {
          let data = res.data;
          if (data === null) {
            this.$message.warning('搜索失败');
          } else {
            this.infoTable = data;
          }
        });
      },
      showAddTagInput() {
        this.addTagInputVisible = true;
        this.$nextTick(() => {
          this.$refs['addTagInput'].$refs.input.focus();
        });
      },
      addTagConfirm() {
        let addTagInputValue = this.addTagInputValue;
        const regular = new RegExp(/^[\s]+$/);
        if (addTagInputValue !== '' && !regular.test(addTagInputValue)) {
          if (addTagInputValue.length <= 10) {
            let labels = this.editForm.labels;
            let exist = false;
            for (let i = 0; i < labels.length; i++) {
              if (addTagInputValue === labels[i]) {
                exist = true;
                break;
              }
            }
            if (!exist) {
              this.editForm.labels.push(addTagInputValue);
            } else {
              this.$message.warning('该标签已存在');
            }
          } else {
            this.$message.warning('标签长度必须小于10位');
          }
        } else {
          this.$message.warning('标签不能为空');
        }
        this.addTagInputVisible = false;
        this.addTagInputValue = '';
      },
      removeTagConfirm(tag) {
        this.editForm.labels.splice(this.editForm.labels.indexOf(tag), 1);
      },
      openGenerate() {
        if (!this.editForm.autoGenerate) {
          this.generateContent();
        }
      },
      generateContent() {
        const extent = this.autoGenerate.extent;
        const number = this.autoGenerate.number;
        const symbol = this.autoGenerate.symbol;
        const symbolList = this.autoGenerate.symbolList;
        const numberList = this.autoGenerate.numberList;
        const letterList = this.autoGenerate.letterList;
        const likeLetterList = this.autoGenerate.likeLetterList;
        let content = [];
        if (symbol >= extent) {
          for (let i = 0; i < extent; i++) {
            content.push(symbolList.charAt(Math.random() * symbolList.length));
          }
        } else {
          const unLetterCount = number + symbol;
          if (unLetterCount >= extent) {
            for (let i = 0; i < symbol; i++) {
              content.push(symbolList.charAt(Math.random() * symbolList.length));
            }
            for (let i = 0; i < extent - symbol; i++) {
              content.push(numberList.charAt(Math.random() * numberList.length));
            }
          } else {
            for (let i = 0; i < symbol; i++) {
              content.push(symbolList.charAt(Math.random() * symbolList.length));
            }
            for (let i = 0; i < number; i++) {
              content.push(numberList.charAt(Math.random() * numberList.length));
            }
            if (this.autoGenerate.like) {
              for (let i = 0; i < extent - symbol - number; i++) {
                content.push(likeLetterList.charAt(Math.random() * likeLetterList.length));
              }
            } else {
              for (let i = 0; i < extent - symbol - number; i++) {
                content.push(letterList.charAt(Math.random() * letterList.length));
              }
            }
          }
        }
        content.sort(function () {
          return 0.5 - Math.random();
        });
        this.editForm.content = content.join('');
      },
      submitForm(formName) {
        if (formName === 'addForm') {
          this.$refs['editForm'].validate((valid) => {
            if (valid) {
              this.addInfo();
            }
          });
        }
        if (formName === 'editForm') {
          this.$refs['editForm'].validate((valid) => {
            if (valid) {
              this.editInfo();
            }
          });
        }
        if (formName === 'searchForm') {
          this.$refs['searchForm'].validate((valid) => {
            if (valid) {
              this.searchInfo();
            }
          });
        }
      },
      editFormClose(done) {
        done();
        this.resetEditForm();
      },
      resetEditForm() {
        this.editFormVisible = false;
        this.editMode = false;
        this.$refs['editForm'].resetFields();
        this.editForm.id = '';
        this.editForm.description = '';
        this.editForm.content = '';
        this.editForm.labels = [];
        this.editForm.autoGenerate = false;
      }
    }
  };
</script>

<style>
  @import "../style/scss/_content.scss";
</style>
