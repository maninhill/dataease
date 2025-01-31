<template>
  <el-container class="geometry-container">
    <el-aside class="geonetry-aside">
      <div class="geo-title">
        <span>{{ t('online_map.geometry') }}</span>
        <span class="add-icon-span">
          <el-icon>
            <Icon name="icon_add_outlined"></Icon>
          </el-icon>
        </span>
      </div>
      <div class="geo-search">
        <el-input
          class="m16 w100"
          v-model="keyword"
          clearable
          :placeholder="t('commons.search')"
          @change="filterResource"
        >
          <template #prefix>
            <el-icon>
              <Icon name="icon_search-outline_outlined"></Icon>
            </el-icon>
          </template>
        </el-input>
      </div>
      <div class="map-tree-container">
        <el-scrollbar class="menu-tree">
          <el-tree
            menu
            ref="areaTreeRef"
            node-key="id"
            :data="treeData"
            @node-click="handleNodeClick"
            :highlight-current="true"
            :expand-on-click-node="false"
            :default-expand-all="false"
            :filter-node-method="filterResourceNode"
          >
            <template #default="{ node, data }">
              <span class="custom-tree-node" :class="{ 'is-disabled': node.disabled || data.root }">
                <span
                  :title="data.name"
                  v-html="data.colorName && keyword ? data.colorName : data.name"
                />
              </span>
            </template>
          </el-tree>
        </el-scrollbar>
      </div>
    </el-aside>
    <el-main class="geometry-main">
      <div class="geo-content-container" v-if="!selectedData">
        <EmptyBackground img-type="noneWhite" description="请在左侧选择区域" />
      </div>
      <div v-else class="geo-content-container">
        <div class="geo-content-top">
          <span>{{ selectedData.name }}</span>
        </div>
        <div class="geo-content-middle">
          <div class="geo-area">
            <div class="area-label"><span>区域代码</span></div>
            <div class="area-content">
              <span>{{ selectedData.id }}</span>
            </div>
          </div>
          <div class="geo-area">
            <div class="area-label"><span>上级区域</span></div>
            <div class="area-content">
              <span>{{ selectedData.parentName }}</span>
              <span v-if="selectedData.pid" class="area-secondary">{{
                '(' + selectedData.pid + ')'
              }}</span>
            </div>
          </div>
        </div>
        <div class="geo-content-bottom">
          <div class="area-label"><span>坐标文件</span></div>
          <el-scrollbar class="area-content-geo">
            <span>{{ selectedData.geoJson }}</span>
          </el-scrollbar>
        </div>
      </div>
    </el-main>
  </el-container>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { getWorldTree } from '@/api/map'
import EmptyBackground from '@/components/empty-background/src/EmptyBackground.vue'
import { getGeoJsonFile } from '@/views/chart/components/js/util'
import { cloneDeep } from 'lodash-es'
import { setColorName } from '@/utils/utils'
const { t } = useI18n()
const keyword = ref('')
const treeData = ref([])
interface Tree {
  label: string
  children?: Tree[]
}
const areaTreeRef = ref(null)

const selectedData = ref(null)

const handleNodeClick = async (data: Tree) => {
  selectedData.value = data
  const geoJson = cloneDeep(await getGeoJsonFile(data['id']))
  selectedData.value['geoJson'] = geoJson
  const pid = data['pid']
  if (pid) {
    const parent = areaTreeRef.value.getNode(pid)
    if (parent) {
      selectedData.value.parentName = parent.data.name
    }
  }
}
const filterResource = val => {
  areaTreeRef.value?.filter(val)
}
const filterResourceNode = (value: string, data) => {
  setColorName(data, value)
  if (!value) return true
  return data.name.toLocaleLowerCase().includes(value.toLocaleLowerCase())
}

const loadTreeData = () => {
  getWorldTree()
    .then(res => {
      const root = res.data
      treeData.value = [root]
    })
    .catch(e => {
      console.error(e)
    })
}

loadTreeData()
</script>

<style lang="less" scoped>
.geometry-container {
  height: 100%;
  .geonetry-aside {
    width: 280px !important;
    border-right: 1px solid #1f232926;
    padding: 16px;
    height: 100%;
    .geo-title {
      display: flex;
      justify-content: space-between;
      height: 24px;
      line-height: 24px;
      span:first-child {
        font-size: 16px;
        font-weight: 500;
        line-height: 24px;
      }
      .add-icon-span {
        display: none;
        color: #3370ff;
        height: 20px;
        width: 20px;
        i {
          left: 2px;
        }
        &:hover {
          background: #1f232926;
          cursor: pointer;
        }
        border-radius: 2px;
      }
      margin-bottom: 16px;
    }
    .geo-search {
      margin-bottom: 16px;
    }
    .map-tree-container {
      height: calc(100% - 96px);
      overflow-y: auto;
    }
  }
  .geometry-main {
    padding: 16px !important;
  }
}
.geo-content-container {
  width: 100%;
  height: 100%;
  .geo-content-top {
    height: 24px;
    line-height: 24px;
    margin-bottom: 16px;
    span {
      font-weight: 500;
      font-size: 16px;
      color: #1f2329;
    }
  }
  .geo-content-middle {
    display: flex;
    .geo-area {
      height: 48px;
      width: 50%;
    }
    margin-bottom: 16px;
  }
  :deep(.area-label) {
    height: 22px;
    line-height: 22px;
    span {
      font-size: 14px;
      color: #646a73;
      font-weight: 400;
    }
  }
  :deep(.area-content) {
    line-height: 22px;
    height: 22px;
    span {
      font-size: 14px;
      color: #1f2329;
      font-weight: 400;
    }
    .area-secondary {
      color: #646a73;
    }
  }
  .geo-content-bottom {
    width: 100%;
    height: calc(100% - 110px);
    .area-content-geo {
      line-height: 22px;
      overflow-x: hidden;
      overflow-y: auto;
      height: calc(100% - 30px);
      span {
        font-size: 14px;
        color: #1f2329;
        font-weight: 400;
      }
    }
  }
}
.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  box-sizing: content-box;
  padding-right: 4px;
  overflow: hidden;
}
</style>
