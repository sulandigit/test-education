<template>
  <div class="video-player" ref="playerContainer">
    <!-- 视频播放器容器 -->
    <div class="player-wrapper" :class="{ 'is-fullscreen': isFullscreen }">
      <!-- 视频元素 -->
      <video
        ref="videoElement"
        class="video-element"
        :poster="videoPoster"
        preload="metadata"
        @loadedmetadata="onLoadedMetadata"
        @timeupdate="onTimeUpdate"
        @ended="onVideoEnded"
        @play="onPlay"
        @pause="onPause"
        @progress="onProgress"
        @waiting="onWaiting"
        @canplay="onCanPlay"
        @error="onError"
      >
        <source v-if="videoUrl" :src="videoUrl" type="video/mp4">
        您的浏览器不支持视频播放。
      </video>

      <!-- 加载中状态 -->
      <div v-if="isLoading" class="loading-overlay">
        <el-icon class="loading-icon"><Loading /></el-icon>
        <span>视频加载中...</span>
      </div>

      <!-- 错误状态 -->
      <div v-if="hasError" class="error-overlay">
        <el-icon class="error-icon"><VideoCamera /></el-icon>
        <p>视频加载失败</p>
        <el-button @click="retryLoad">重试</el-button>
      </div>

      <!-- 播放器控制栏 -->
      <div 
        class="player-controls" 
        :class="{ 'is-visible': showControls }"
        @mouseenter="showControlsTemp"
        @mouseleave="hideControlsTemp"
      >
        <!-- 进度条 -->
        <div class="progress-bar-container">
          <div class="progress-bar" @click="seekTo">
            <div class="progress-track"></div>
            <div 
              class="progress-played" 
              :style="{ width: playedPercent + '%' }"
            ></div>
            <div 
              class="progress-buffered"
              :style="{ width: bufferedPercent + '%' }"
            ></div>
            <div 
              class="progress-thumb" 
              :style="{ left: playedPercent + '%' }"
            ></div>
          </div>
          <div class="time-tooltip" v-show="showTimeTooltip" :style="timeTooltipStyle">
            {{ tooltipTime }}
          </div>
        </div>

        <!-- 控制按钮区域 -->
        <div class="controls-bar">
          <div class="controls-left">
            <!-- 播放/暂停按钮 -->
            <el-button 
              class="control-btn play-btn"
              :icon="isPlaying ? VideoPause : VideoPlay"
              @click="togglePlay"
              circle
            />

            <!-- 时间显示 -->
            <div class="time-display">
              <span class="current-time">{{ formatTime(currentTime) }}</span>
              <span class="time-separator">/</span>
              <span class="duration">{{ formatTime(duration) }}</span>
            </div>
          </div>

          <div class="controls-center">
            <!-- 章节导航 -->
            <div class="chapter-nav" v-if="chapters.length > 1">
              <el-button 
                class="control-btn"
                :icon="DArrowLeft"
                @click="previousChapter"
                :disabled="currentChapterIndex === 0"
                circle
              />
              <span class="chapter-info">
                {{ currentChapterIndex + 1 }}/{{ chapters.length }}
              </span>
              <el-button 
                class="control-btn"
                :icon="DArrowRight"
                @click="nextChapter"
                :disabled="currentChapterIndex === chapters.length - 1"
                circle
              />
            </div>
          </div>

          <div class="controls-right">
            <!-- 倍速控制 -->
            <el-dropdown @command="changePlaybackRate" trigger="hover">
              <el-button class="control-btn speed-btn">
                {{ playbackRate }}x
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item 
                    v-for="speed in speedOptions" 
                    :key="speed"
                    :command="speed"
                    :class="{ 'is-active': playbackRate === speed }"
                  >
                    {{ speed }}x
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>

            <!-- 音量控制 -->
            <div class="volume-control">
              <el-button 
                class="control-btn volume-btn"
                :icon="volumeIcon"
                @click="toggleMute"
                circle
              />
              <div class="volume-slider" v-show="showVolumeSlider">
                <el-slider
                  v-model="volume"
                  :min="0"
                  :max="100"
                  :show-tooltip="false"
                  @input="onVolumeChange"
                  vertical
                  height="80px"
                />
              </div>
            </div>

            <!-- 画质选择 -->
            <el-dropdown v-if="qualityOptions.length > 1" @command="changeQuality" trigger="hover">
              <el-button class="control-btn quality-btn">
                {{ currentQuality }}
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item 
                    v-for="quality in qualityOptions" 
                    :key="quality.value"
                    :command="quality.value"
                    :class="{ 'is-active': currentQuality === quality.label }"
                  >
                    {{ quality.label }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>

            <!-- 全屏按钮 -->
            <el-button 
              class="control-btn fullscreen-btn"
              :icon="isFullscreen ? Fold : Expand"
              @click="toggleFullscreen"
              circle
            />
          </div>
        </div>
      </div>

      <!-- 点击播放覆盖层 -->
      <div 
        class="play-overlay" 
        v-show="!isPlaying && !showControls"
        @click="togglePlay"
      >
        <el-icon class="play-icon"><VideoPlay /></el-icon>
      </div>
    </div>

    <!-- 快捷键提示 -->
    <div class="shortcuts-hint" v-show="showShortcuts">
      <div class="hint-item">空格: 播放/暂停</div>
      <div class="hint-item">→/←: 快进/快退10秒</div>
      <div class="hint-item">↑/↓: 调节音量</div>
      <div class="hint-item">F: 全屏</div>
      <div class="hint-item">M: 静音</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  Loading,
  VideoCamera,
  VideoPlay,
  VideoPause,
  DArrowLeft,
  DArrowRight,
  Mute,
  Fold,
  Expand
} from '@element-plus/icons-vue'

// Props
interface Props {
  videoUrl?: string
  videoPoster?: string
  chapters?: Array<{
    id: number
    title: string
    startTime: number
    duration: number
  }>
  autoplay?: boolean
  loop?: boolean
  muted?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  chapters: () => [],
  autoplay: false,
  loop: false,
  muted: false
})

// Emits
const emit = defineEmits<{
  play: []
  pause: []
  ended: []
  timeupdate: [currentTime: number]
  progress: [percent: number]
  chapterChange: [chapterIndex: number]
  qualityChange: [quality: string]
  speedChange: [speed: number]
  fullscreenChange: [isFullscreen: boolean]
}>()

// 响应式数据
const playerContainer = ref<HTMLElement>()
const videoElement = ref<HTMLVideoElement>()

const isPlaying = ref(false)
const isLoading = ref(false)
const hasError = ref(false)
const isFullscreen = ref(false)
const showControls = ref(false)
const showTimeTooltip = ref(false)
const showVolumeSlider = ref(false)
const showShortcuts = ref(false)

const currentTime = ref(0)
const duration = ref(0)
const volume = ref(80)
const playbackRate = ref(1)
const currentChapterIndex = ref(0)
const currentQuality = ref('高清')

const controlsTimer = ref<NodeJS.Timeout>()
const timeTooltipStyle = ref({})
const tooltipTime = ref('')

// 配置选项
const speedOptions = [0.5, 0.75, 1, 1.25, 1.5, 2]
const qualityOptions = ref([
  { label: '高清', value: 'hd' },
  { label: '标清', value: 'sd' },
  { label: '流畅', value: 'ld' }
])

// 计算属性
const playedPercent = computed(() => {
  return duration.value > 0 ? (currentTime.value / duration.value) * 100 : 0
})

const bufferedPercent = computed(() => {
  const video = videoElement.value
  if (!video || !video.buffered.length) return 0
  const bufferedEnd = video.buffered.end(video.buffered.length - 1)
  return duration.value > 0 ? (bufferedEnd / duration.value) * 100 : 0
})

const volumeIcon = computed(() => {
  if (volume.value === 0) return Mute
  return 'volume-up' // 需要导入相应图标
})

// 方法
const togglePlay = () => {
  const video = videoElement.value
  if (!video) return

  if (isPlaying.value) {
    video.pause()
  } else {
    video.play()
  }
}

const seekTo = (event: MouseEvent) => {
  const video = videoElement.value
  const progressBar = event.currentTarget as HTMLElement
  if (!video || !progressBar) return

  const rect = progressBar.getBoundingClientRect()
  const percent = (event.clientX - rect.left) / rect.width
  const seekTime = percent * duration.value

  video.currentTime = seekTime
}

const changePlaybackRate = (rate: number) => {
  const video = videoElement.value
  if (!video) return

  playbackRate.value = rate
  video.playbackRate = rate
  emit('speedChange', rate)
}

const toggleMute = () => {
  const video = videoElement.value
  if (!video) return

  if (volume.value > 0) {
    video.volume = 0
    volume.value = 0
  } else {
    video.volume = 0.8
    volume.value = 80
  }
}

const onVolumeChange = (value: number) => {
  const video = videoElement.value
  if (!video) return

  video.volume = value / 100
}

const toggleFullscreen = () => {
  const container = playerContainer.value
  if (!container) return

  if (!isFullscreen.value) {
    if (container.requestFullscreen) {
      container.requestFullscreen()
    }
  } else {
    if (document.exitFullscreen) {
      document.exitFullscreen()
    }
  }
}

const previousChapter = () => {
  if (currentChapterIndex.value > 0) {
    currentChapterIndex.value--
    loadChapter()
  }
}

const nextChapter = () => {
  if (currentChapterIndex.value < props.chapters.length - 1) {
    currentChapterIndex.value++
    loadChapter()
  }
}

const loadChapter = () => {
  const chapter = props.chapters[currentChapterIndex.value]
  if (chapter) {
    // 加载章节视频
    emit('chapterChange', currentChapterIndex.value)
  }
}

const changeQuality = (quality: string) => {
  const option = qualityOptions.value.find(opt => opt.value === quality)
  if (option) {
    currentQuality.value = option.label
    emit('qualityChange', quality)
  }
}

const showControlsTemp = () => {
  showControls.value = true
  clearTimeout(controlsTimer.value)
}

const hideControlsTemp = () => {
  controlsTimer.value = setTimeout(() => {
    if (!isPlaying.value) return
    showControls.value = false
  }, 3000)
}

const retryLoad = () => {
  hasError.value = false
  isLoading.value = true
  videoElement.value?.load()
}

const formatTime = (seconds: number): string => {
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = Math.floor(seconds % 60)

  if (hours > 0) {
    return `${hours}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
  }
  return `${minutes}:${secs.toString().padStart(2, '0')}`
}

// 事件处理
const onLoadedMetadata = () => {
  const video = videoElement.value
  if (!video) return

  duration.value = video.duration
  isLoading.value = false
}

const onTimeUpdate = () => {
  const video = videoElement.value
  if (!video) return

  currentTime.value = video.currentTime
  emit('timeupdate', currentTime.value)
  emit('progress', playedPercent.value)
}

const onPlay = () => {
  isPlaying.value = true
  emit('play')
  hideControlsTemp()
}

const onPause = () => {
  isPlaying.value = false
  emit('pause')
  showControls.value = true
}

const onVideoEnded = () => {
  isPlaying.value = false
  emit('ended')
  
  // 自动播放下一章节
  if (currentChapterIndex.value < props.chapters.length - 1) {
    nextChapter()
  }
}

const onProgress = () => {
  // 缓冲进度更新
}

const onWaiting = () => {
  isLoading.value = true
}

const onCanPlay = () => {
  isLoading.value = false
  hasError.value = false
}

const onError = () => {
  isLoading.value = false
  hasError.value = true
}

// 键盘快捷键
const handleKeydown = (event: KeyboardEvent) => {
  const video = videoElement.value
  if (!video) return

  switch (event.code) {
    case 'Space':
      event.preventDefault()
      togglePlay()
      break
    case 'ArrowLeft':
      event.preventDefault()
      video.currentTime = Math.max(0, video.currentTime - 10)
      break
    case 'ArrowRight':
      event.preventDefault()
      video.currentTime = Math.min(duration.value, video.currentTime + 10)
      break
    case 'ArrowUp':
      event.preventDefault()
      volume.value = Math.min(100, volume.value + 5)
      onVolumeChange(volume.value)
      break
    case 'ArrowDown':
      event.preventDefault()
      volume.value = Math.max(0, volume.value - 5)
      onVolumeChange(volume.value)
      break
    case 'KeyF':
      event.preventDefault()
      toggleFullscreen()
      break
    case 'KeyM':
      event.preventDefault()
      toggleMute()
      break
  }
}

// 全屏状态监听
const handleFullscreenChange = () => {
  isFullscreen.value = !!document.fullscreenElement
  emit('fullscreenChange', isFullscreen.value)
}

// 生命周期
onMounted(() => {
  // 添加事件监听
  document.addEventListener('keydown', handleKeydown)
  document.addEventListener('fullscreenchange', handleFullscreenChange)

  // 设置初始音量
  const video = videoElement.value
  if (video) {
    video.volume = volume.value / 100
    video.muted = props.muted
  }
})

onUnmounted(() => {
  // 清理事件监听
  document.removeEventListener('keydown', handleKeydown)
  document.removeEventListener('fullscreenchange', handleFullscreenChange)
  clearTimeout(controlsTimer.value)
})

// 监听props变化
watch(() => props.videoUrl, (newUrl) => {
  if (newUrl && videoElement.value) {
    hasError.value = false
    isLoading.value = true
    videoElement.value.load()
  }
})
</script>

<style lang="scss" scoped>
.video-player {
  position: relative;
  width: 100%;
  background: #000;
  border-radius: $border-radius-base;
  overflow: hidden;

  .player-wrapper {
    position: relative;
    width: 100%;
    padding-bottom: 56.25%; // 16:9 aspect ratio
    height: 0;

    &.is-fullscreen {
      position: fixed;
      top: 0;
      left: 0;
      width: 100vw;
      height: 100vh;
      padding-bottom: 0;
      z-index: 9999;
      border-radius: 0;
    }

    .video-element {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      object-fit: contain;
    }

    .loading-overlay,
    .error-overlay {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      background: rgba(0, 0, 0, 0.8);
      color: white;
      gap: $spacing-md;

      .loading-icon {
        font-size: 2rem;
        animation: spin 1s linear infinite;
      }

      .error-icon {
        font-size: 3rem;
        color: $text-placeholder;
      }
    }

    .player-controls {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
      color: white;
      opacity: 0;
      transition: opacity 0.3s ease;
      padding: $spacing-lg $spacing-md $spacing-md;

      &.is-visible {
        opacity: 1;
      }

      .progress-bar-container {
        position: relative;
        margin-bottom: $spacing-md;

        .progress-bar {
          height: 4px;
          background: rgba(255, 255, 255, 0.3);
          border-radius: 2px;
          cursor: pointer;
          position: relative;

          .progress-track,
          .progress-played,
          .progress-buffered {
            position: absolute;
            top: 0;
            left: 0;
            height: 100%;
            border-radius: inherit;
          }

          .progress-played {
            background: $primary-color;
            z-index: 2;
          }

          .progress-buffered {
            background: rgba(255, 255, 255, 0.5);
            z-index: 1;
          }

          .progress-thumb {
            position: absolute;
            top: -4px;
            width: 12px;
            height: 12px;
            background: $primary-color;
            border-radius: 50%;
            transform: translateX(-50%);
            opacity: 0;
            transition: opacity 0.3s ease;
            z-index: 3;
          }

          &:hover .progress-thumb {
            opacity: 1;
          }
        }

        .time-tooltip {
          position: absolute;
          bottom: 20px;
          background: rgba(0, 0, 0, 0.8);
          color: white;
          padding: 4px 8px;
          border-radius: 4px;
          font-size: $font-size-xs;
          transform: translateX(-50%);
          white-space: nowrap;
        }
      }

      .controls-bar {
        display: flex;
        align-items: center;
        justify-content: space-between;

        .controls-left,
        .controls-center,
        .controls-right {
          display: flex;
          align-items: center;
          gap: $spacing-sm;
        }

        .control-btn {
          background: transparent;
          border: none;
          color: white;
          padding: $spacing-xs;

          &:hover {
            background: rgba(255, 255, 255, 0.2);
          }

          &:disabled {
            opacity: 0.5;
            cursor: not-allowed;
          }
        }

        .time-display {
          font-size: $font-size-sm;
          color: rgba(255, 255, 255, 0.9);

          .time-separator {
            margin: 0 4px;
          }
        }

        .chapter-info {
          font-size: $font-size-sm;
          color: rgba(255, 255, 255, 0.9);
          margin: 0 $spacing-sm;
        }

        .volume-control {
          position: relative;

          .volume-slider {
            position: absolute;
            bottom: 100%;
            left: 50%;
            transform: translateX(-50%);
            background: rgba(0, 0, 0, 0.8);
            padding: $spacing-sm;
            border-radius: $border-radius-base;
            margin-bottom: $spacing-sm;
          }
        }
      }
    }

    .play-overlay {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;

      .play-icon {
        font-size: 4rem;
        color: white;
        background: rgba(0, 0, 0, 0.6);
        border-radius: 50%;
        padding: $spacing-lg;
        transition: transform 0.3s ease;

        &:hover {
          transform: scale(1.1);
        }
      }
    }
  }

  .shortcuts-hint {
    position: absolute;
    top: $spacing-md;
    right: $spacing-md;
    background: rgba(0, 0, 0, 0.8);
    color: white;
    padding: $spacing-sm;
    border-radius: $border-radius-base;
    font-size: $font-size-xs;

    .hint-item {
      margin-bottom: 4px;

      &:last-child {
        margin-bottom: 0;
      }
    }
  }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

// 移动端适配
@media (max-width: $breakpoint-sm) {
  .video-player {
    .player-controls {
      .controls-bar {
        flex-wrap: wrap;
        gap: $spacing-xs;

        .controls-center {
          order: 3;
          width: 100%;
          justify-content: center;
          margin-top: $spacing-sm;
        }
      }
    }
  }
}
</style>