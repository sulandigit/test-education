import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount } from '@vue/test-utils'
import VideoPlayer from '~/components/VideoPlayer.vue'

// Mock video element
const mockVideoElement = {
  play: vi.fn(),
  pause: vi.fn(),
  load: vi.fn(),
  currentTime: 0,
  duration: 100,
  volume: 1,
  muted: false,
  paused: true,
  ended: false,
  playbackRate: 1,
  addEventListener: vi.fn(),
  removeEventListener: vi.fn(),
  requestFullscreen: vi.fn(),
}

// Mock document methods
Object.defineProperty(document, 'fullscreenElement', {
  writable: true,
  value: null,
})

document.exitFullscreen = vi.fn()

const mockChapters = [
  { id: 1, title: '课程介绍', startTime: 0, duration: 300 },
  { id: 2, title: '基础概念', startTime: 300, duration: 600 },
  { id: 3, title: '实践练习', startTime: 900, duration: 800 },
]

describe('VideoPlayer', () => {
  let wrapper: any

  beforeEach(() => {
    // Mock HTMLVideoElement
    vi.spyOn(document, 'createElement').mockImplementation((tagName) => {
      if (tagName === 'video') {
        return mockVideoElement as any
      }
      return document.createElement(tagName)
    })
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  it('renders video player with basic controls', () => {
    wrapper = mount(VideoPlayer, {
      props: {
        src: 'https://example.com/video.mp4',
        chapters: mockChapters
      }
    })

    expect(wrapper.find('.video-player').exists()).toBe(true)
    expect(wrapper.find('.video-element').exists()).toBe(true)
    expect(wrapper.find('.controls').exists()).toBe(true)
    expect(wrapper.find('.play-button').exists()).toBe(true)
    expect(wrapper.find('.progress-bar').exists()).toBe(true)
    expect(wrapper.find('.volume-control').exists()).toBe(true)
  })

  it('initializes with correct default state', () => {
    wrapper = mount(VideoPlayer, {
      props: {
        src: 'https://example.com/video.mp4',
        chapters: mockChapters
      }
    })

    expect(wrapper.vm.isPlaying).toBe(false)
    expect(wrapper.vm.currentTime).toBe(0)
    expect(wrapper.vm.volume).toBe(1)
    expect(wrapper.vm.playbackRate).toBe(1)
    expect(wrapper.vm.isFullscreen).toBe(false)
  })

  it('toggles play/pause correctly', async () => {
    wrapper = mount(VideoPlayer, {
      props: {
        src: 'https://example.com/video.mp4',
        chapters: mockChapters
      }
    })

    // 模拟视频元素
    wrapper.vm.$refs.video = mockVideoElement

    // 测试播放
    await wrapper.vm.togglePlay()
    expect(mockVideoElement.play).toHaveBeenCalled()

    // 模拟播放状态
    mockVideoElement.paused = false
    wrapper.vm.isPlaying = true

    // 测试暂停
    await wrapper.vm.togglePlay()
    expect(mockVideoElement.pause).toHaveBeenCalled()
  })

  it('handles volume control correctly', async () => {
    wrapper = mount(VideoPlayer, {
      props: {
        src: 'https://example.com/video.mp4',
        chapters: mockChapters
      }
    })

    wrapper.vm.$refs.video = mockVideoElement

    // 测试音量调整
    await wrapper.vm.setVolume(0.5)
    expect(wrapper.vm.volume).toBe(0.5)
    expect(mockVideoElement.volume).toBe(0.5)

    // 测试静音切换
    await wrapper.vm.toggleMute()
    expect(wrapper.vm.isMuted).toBe(true)
    expect(mockVideoElement.muted).toBe(true)
  })

  it('handles playback rate changes', async () => {
    wrapper = mount(VideoPlayer, {
      props: {
        src: 'https://example.com/video.mp4',
        chapters: mockChapters
      }
    })

    wrapper.vm.$refs.video = mockVideoElement

    // 测试倍速调整
    await wrapper.vm.setPlaybackRate(1.5)
    expect(wrapper.vm.playbackRate).toBe(1.5)
    expect(mockVideoElement.playbackRate).toBe(1.5)

    await wrapper.vm.setPlaybackRate(2)
    expect(wrapper.vm.playbackRate).toBe(2)
    expect(mockVideoElement.playbackRate).toBe(2)
  })

  it('handles progress bar interaction', async () => {
    wrapper = mount(VideoPlayer, {
      props: {
        src: 'https://example.com/video.mp4',
        chapters: mockChapters
      }
    })

    wrapper.vm.$refs.video = mockVideoElement
    mockVideoElement.duration = 1000

    // 模拟进度条点击 (点击50%位置)
    const progressBar = wrapper.find('.progress-bar')
    const mockEvent = {
      target: { offsetWidth: 400 },
      offsetX: 200
    }

    await wrapper.vm.seek(mockEvent)
    expect(mockVideoElement.currentTime).toBe(500) // 50% of 1000s
  })

  it('displays chapter information correctly', () => {
    wrapper = mount(VideoPlayer, {
      props: {
        src: 'https://example.com/video.mp4',
        chapters: mockChapters,
        showChapters: true
      }
    })

    const chapterList = wrapper.find('.chapter-list')
    expect(chapterList.exists()).toBe(true)

    const chapterItems = wrapper.findAll('.chapter-item')
    expect(chapterItems).toHaveLength(3)
    expect(chapterItems[0].text()).toContain('课程介绍')
    expect(chapterItems[1].text()).toContain('基础概念')
    expect(chapterItems[2].text()).toContain('实践练习')
  })

  it('handles chapter navigation', async () => {
    wrapper = mount(VideoPlayer, {
      props: {
        src: 'https://example.com/video.mp4',
        chapters: mockChapters,
        showChapters: true
      }
    })

    wrapper.vm.$refs.video = mockVideoElement

    // 跳转到第二章
    await wrapper.vm.jumpToChapter(1) // index 1 = 第二章
    expect(mockVideoElement.currentTime).toBe(300)
  })

  it('handles fullscreen toggle', async () => {
    wrapper = mount(VideoPlayer, {
      props: {
        src: 'https://example.com/video.mp4',
        chapters: mockChapters
      }
    })

    const mockContainer = {
      requestFullscreen: vi.fn()
    }
    wrapper.vm.$refs.playerContainer = mockContainer

    // 测试进入全屏
    await wrapper.vm.toggleFullscreen()
    expect(mockContainer.requestFullscreen).toHaveBeenCalled()
  })

  it('handles keyboard shortcuts', async () => {
    wrapper = mount(VideoPlayer, {
      props: {
        src: 'https://example.com/video.mp4',
        chapters: mockChapters
      }
    })

    wrapper.vm.$refs.video = mockVideoElement

    // 测试空格键播放/暂停
    const spaceEvent = new KeyboardEvent('keydown', { code: 'Space' })
    await wrapper.vm.handleKeydown(spaceEvent)
    expect(mockVideoElement.play).toHaveBeenCalled()

    // 测试左箭头后退
    const leftEvent = new KeyboardEvent('keydown', { code: 'ArrowLeft' })
    mockVideoElement.currentTime = 100
    await wrapper.vm.handleKeydown(leftEvent)
    expect(mockVideoElement.currentTime).toBe(90) // 后退10秒

    // 测试右箭头前进
    const rightEvent = new KeyboardEvent('keydown', { code: 'ArrowRight' })
    mockVideoElement.currentTime = 100
    await wrapper.vm.handleKeydown(rightEvent)
    expect(mockVideoElement.currentTime).toBe(110) // 前进10秒
  })

  it('emits progress update events', async () => {
    wrapper = mount(VideoPlayer, {
      props: {
        src: 'https://example.com/video.mp4',
        chapters: mockChapters
      }
    })

    wrapper.vm.$refs.video = mockVideoElement
    mockVideoElement.currentTime = 300
    mockVideoElement.duration = 1000

    // 触发时间更新
    await wrapper.vm.handleTimeUpdate()

    const progressEvents = wrapper.emitted('progress')
    expect(progressEvents).toBeTruthy()
    expect(progressEvents![0]).toEqual([{
      currentTime: 300,
      duration: 1000,
      progress: 30 // 30%
    }])
  })

  it('handles video quality selection', async () => {
    const qualities = [
      { label: '720P', value: 720 },
      { label: '1080P', value: 1080 }
    ]

    wrapper = mount(VideoPlayer, {
      props: {
        src: 'https://example.com/video.mp4',
        chapters: mockChapters,
        qualities: qualities
      }
    })

    wrapper.vm.$refs.video = mockVideoElement

    // 测试画质切换
    await wrapper.vm.changeQuality(1080)
    expect(wrapper.vm.currentQuality).toBe(1080)

    const qualityEvents = wrapper.emitted('qualityChange')
    expect(qualityEvents).toBeTruthy()
    expect(qualityEvents![0]).toEqual([1080])
  })
})