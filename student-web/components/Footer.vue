<template>
  <footer class="app-footer">
    <div class="container">
      <!-- 主要内容区域 -->
      <div class="footer-main">
        <div class="footer-content">
          <!-- 关于我们 -->
          <div class="footer-section">
            <h3 class="section-title">关于领课教育</h3>
            <p class="section-desc">
              专业的在线教育平台，致力于为学习者提供优质的学习资源和服务，
              帮助每个人实现知识提升和技能成长。
            </p>
            <div class="contact-info">
              <div class="contact-item">
                <el-icon><Phone /></el-icon>
                <span>400-888-9999</span>
              </div>
              <div class="contact-item">
                <el-icon><Message /></el-icon>
                <span>service@roncoo.com</span>
              </div>
              <div class="contact-item">
                <el-icon><Location /></el-icon>
                <span>广州市天河区珠江新城</span>
              </div>
            </div>
          </div>

          <!-- 快速链接 -->
          <div class="footer-section">
            <h3 class="section-title">快速导航</h3>
            <ul class="link-list">
              <li><NuxtLink to="/">首页</NuxtLink></li>
              <li><NuxtLink to="/courses">课程中心</NuxtLink></li>
              <li><NuxtLink to="/about">关于我们</NuxtLink></li>
              <li><NuxtLink to="/contact">联系我们</NuxtLink></li>
              <li><NuxtLink to="/help">帮助中心</NuxtLink></li>
              <li><NuxtLink to="/privacy">隐私政策</NuxtLink></li>
            </ul>
          </div>

          <!-- 热门分类 -->
          <div class="footer-section">
            <h3 class="section-title">热门分类</h3>
            <ul class="link-list">
              <li v-for="category in popularCategories" :key="category.id">
                <NuxtLink :to="`/courses?category=${category.id}`">
                  {{ category.name }}
                </NuxtLink>
              </li>
            </ul>
          </div>

          <!-- 关注我们 -->
          <div class="footer-section">
            <h3 class="section-title">关注我们</h3>
            <div class="social-links">
              <a href="#" class="social-link" title="微信公众号">
                <el-icon><ChatDotRound /></el-icon>
                <span>微信公众号</span>
              </a>
              <a href="#" class="social-link" title="微博">
                <el-icon><Share /></el-icon>
                <span>新浪微博</span>
              </a>
              <a href="#" class="social-link" title="QQ群">
                <el-icon><ChatRound /></el-icon>
                <span>QQ交流群</span>
              </a>
            </div>
            
            <!-- 二维码 -->
            <div class="qr-codes">
              <div class="qr-item">
                <img src="/images/wechat-qr.png" alt="微信公众号" @error="handleQrError">
                <p>微信公众号</p>
              </div>
              <div class="qr-item">
                <img src="/images/app-qr.png" alt="手机APP" @error="handleQrError">
                <p>手机APP</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 友情链接 -->
      <div class="footer-links" v-if="friendLinks.length > 0">
        <div class="links-title">友情链接：</div>
        <div class="links-content">
          <a
            v-for="link in friendLinks"
            :key="link.id"
            :href="link.linkUrl"
            :target="link.linkTarget || '_blank'"
            class="friend-link"
            rel="noopener noreferrer"
          >
            {{ link.linkName }}
          </a>
        </div>
      </div>

      <!-- 版权信息 -->
      <div class="footer-copyright">
        <div class="copyright-content">
          <div class="copyright-text">
            <p>© {{ currentYear }} 领课教育 版权所有</p>
            <p v-if="siteConfig.websiteIcp">
              <a 
                href="https://beian.miit.gov.cn/" 
                target="_blank" 
                rel="noopener noreferrer"
                class="icp-link"
              >
                {{ siteConfig.websiteIcp }}
              </a>
            </p>
          </div>
          <div class="powered-by">
            <span>Powered by 领课网络</span>
          </div>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import {
  Phone,
  Message,
  Location,
  ChatDotRound,
  Share,
  ChatRound
} from '@element-plus/icons-vue'

// 响应式数据
const currentYear = new Date().getFullYear()

const popularCategories = ref([
  { id: 1, name: 'Web前端开发' },
  { id: 2, name: 'Java开发' },
  { id: 3, name: 'Python编程' },
  { id: 4, name: '移动端开发' },
  { id: 5, name: '数据库技术' },
  { id: 6, name: '人工智能' }
])

const friendLinks = ref([
  // 友情链接数据将通过API获取
])

const siteConfig = ref({
  websiteName: '领课教育',
  websiteIcp: '粤ICP备xxxxxxxx号'
})

// 方法
const handleQrError = (e: Event) => {
  const target = e.target as HTMLImageElement
  target.style.display = 'none'
}

// 生命周期
onMounted(async () => {
  // 加载友情链接和站点配置
  await loadFooterData()
})

const loadFooterData = async () => {
  try {
    // 加载友情链接
    // const linksResponse = await systemApi.getFriendLinks()
    // friendLinks.value = linksResponse.data

    // 加载站点配置
    // const configResponse = await systemApi.getSiteConfig()
    // siteConfig.value = configResponse.data
  } catch (error) {
    console.error('加载底部数据失败:', error)
  }
}
</script>

<style lang="scss" scoped>
.app-footer {
  background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
  color: white;
  margin-top: auto;

  .footer-main {
    padding: $spacing-xl 0;

    .footer-content {
      display: grid;
      grid-template-columns: 2fr 1fr 1fr 1.5fr;
      gap: $spacing-xl;
    }

    .footer-section {
      .section-title {
        font-size: $font-size-lg;
        font-weight: 600;
        margin-bottom: $spacing-md;
        color: white;
        position: relative;

        &::after {
          content: '';
          position: absolute;
          bottom: -8px;
          left: 0;
          width: 30px;
          height: 2px;
          background: $primary-color;
        }
      }

      .section-desc {
        line-height: 1.8;
        color: rgba(255, 255, 255, 0.8);
        margin-bottom: $spacing-lg;
      }

      .contact-info {
        .contact-item {
          display: flex;
          align-items: center;
          gap: $spacing-sm;
          margin-bottom: $spacing-sm;
          color: rgba(255, 255, 255, 0.9);

          .el-icon {
            color: $primary-color;
          }
        }
      }

      .link-list {
        list-style: none;
        padding: 0;
        margin: 0;

        li {
          margin-bottom: $spacing-sm;

          a {
            color: rgba(255, 255, 255, 0.8);
            transition: color 0.3s ease;

            &:hover {
              color: $primary-color;
            }
          }
        }
      }

      .social-links {
        margin-bottom: $spacing-lg;

        .social-link {
          display: flex;
          align-items: center;
          gap: $spacing-sm;
          margin-bottom: $spacing-sm;
          color: rgba(255, 255, 255, 0.8);
          transition: color 0.3s ease;

          &:hover {
            color: $primary-color;
          }

          .el-icon {
            font-size: 18px;
          }
        }
      }

      .qr-codes {
        display: flex;
        gap: $spacing-md;

        .qr-item {
          text-align: center;

          img {
            width: 80px;
            height: 80px;
            border-radius: $border-radius-base;
            background: white;
            padding: 4px;
          }

          p {
            margin-top: $spacing-xs;
            font-size: $font-size-xs;
            color: rgba(255, 255, 255, 0.7);
          }
        }
      }
    }
  }

  .footer-links {
    padding: $spacing-md 0;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);

    .links-title {
      display: inline;
      color: rgba(255, 255, 255, 0.9);
      margin-right: $spacing-md;
    }

    .links-content {
      display: inline;

      .friend-link {
        color: rgba(255, 255, 255, 0.7);
        margin-right: $spacing-lg;
        transition: color 0.3s ease;

        &:hover {
          color: $primary-color;
        }
      }
    }
  }

  .footer-copyright {
    padding: $spacing-lg 0;

    .copyright-content {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .copyright-text {
        p {
          margin: 0;
          color: rgba(255, 255, 255, 0.6);
          font-size: $font-size-sm;
          line-height: 1.5;

          .icp-link {
            color: rgba(255, 255, 255, 0.6);
            
            &:hover {
              color: rgba(255, 255, 255, 0.8);
            }
          }
        }
      }

      .powered-by {
        color: rgba(255, 255, 255, 0.5);
        font-size: $font-size-sm;
      }
    }
  }
}

// 响应式设计
@media (max-width: $breakpoint-lg) {
  .app-footer {
    .footer-main {
      .footer-content {
        grid-template-columns: 1fr 1fr;
        gap: $spacing-lg;
      }
    }
  }
}

@media (max-width: $breakpoint-sm) {
  .app-footer {
    .footer-main {
      .footer-content {
        grid-template-columns: 1fr;
        gap: $spacing-lg;
      }

      .footer-section {
        .qr-codes {
          justify-content: center;
        }
      }
    }

    .footer-links {
      .links-content {
        display: block;
        margin-top: $spacing-sm;

        .friend-link {
          display: inline-block;
          margin-bottom: $spacing-sm;
        }
      }
    }

    .footer-copyright {
      .copyright-content {
        flex-direction: column;
        gap: $spacing-sm;
        text-align: center;
      }
    }
  }
}
</style>