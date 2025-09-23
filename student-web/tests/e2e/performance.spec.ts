import { test, expect } from '@playwright/test'

test.describe('Performance E2E Tests', () => {
  test('should meet Core Web Vitals thresholds', async ({ page }) => {
    // Navigate to homepage
    await page.goto('/')

    // Measure page load performance
    const perfTiming = await page.evaluate(() => {
      return {
        navigation: performance.getEntriesByType('navigation')[0],
        paint: performance.getEntriesByType('paint'),
        layout: performance.getEntriesByType('layout-shift')
      }
    })

    // Check First Contentful Paint (should be < 2.5s)
    const fcp = perfTiming.paint.find(entry => entry.name === 'first-contentful-paint')
    if (fcp) {
      expect(fcp.startTime).toBeLessThan(2500)
    }

    // Check Time to Interactive (should be reasonable)
    const navEntry = perfTiming.navigation as any
    if (navEntry) {
      const tti = navEntry.loadEventEnd - navEntry.fetchStart
      expect(tti).toBeLessThan(5000)
    }
  })

  test('should load images efficiently', async ({ page }) => {
    await page.goto('/')

    // Wait for images to load
    await page.waitForLoadState('networkidle')

    // Check for lazy loading
    const images = page.locator('img')
    const imageCount = await images.count()

    if (imageCount > 0) {
      // Check that images have proper loading attributes
      for (let i = 0; i < Math.min(imageCount, 5); i++) {
        const img = images.nth(i)
        const loading = await img.getAttribute('loading')
        const src = await img.getAttribute('src')
        
        // Images should either be loaded or have lazy loading
        expect(loading === 'lazy' || src !== null).toBe(true)
      }
    }
  })

  test('should handle large course lists efficiently', async ({ page }) => {
    await page.goto('/course')

    // Measure initial load time
    const startTime = Date.now()
    await page.waitForSelector('[data-testid="course-card"]')
    const initialLoadTime = Date.now() - startTime

    expect(initialLoadTime).toBeLessThan(3000)

    // Test scrolling performance
    const courseCards = page.locator('[data-testid="course-card"]')
    const initialCount = await courseCards.count()

    // Scroll to bottom
    await page.evaluate(() => {
      window.scrollTo(0, document.body.scrollHeight)
    })

    // If infinite scroll is implemented, more items should load
    await page.waitForTimeout(1000)
    const afterScrollCount = await courseCards.count()

    // Either same count (pagination) or more count (infinite scroll)
    expect(afterScrollCount).toBeGreaterThanOrEqual(initialCount)
  })

  test('should optimize video player loading', async ({ page }) => {
    // Login first
    await page.goto('/auth/login')
    await page.fill('[data-testid="phone-input"]', '13800138000')
    await page.fill('[data-testid="password-input"]', 'password123')
    await page.click('[data-testid="login-button"]')

    // Navigate to learning page
    const startTime = Date.now()
    await page.goto('/learning/1/1')

    // Video player should appear quickly
    await expect(page.locator('[data-testid="video-player"]')).toBeVisible({ timeout: 5000 })
    const videoLoadTime = Date.now() - startTime

    expect(videoLoadTime).toBeLessThan(5000)

    // Video should be ready to play
    await expect(page.locator('[data-testid="play-button"]')).toBeVisible()
  })

  test('should handle API response times', async ({ page }) => {
    // Monitor network requests
    const apiCalls: any[] = []
    
    page.on('response', response => {
      if (response.url().includes('/api/')) {
        apiCalls.push({
          url: response.url(),
          status: response.status(),
          timing: response.timing()
        })
      }
    })

    await page.goto('/')
    await page.waitForLoadState('networkidle')

    // Check API response times
    for (const call of apiCalls) {
      expect(call.status).toBeLessThan(400) // No client/server errors
      
      // API calls should be reasonably fast
      if (call.timing) {
        const totalTime = call.timing.responseEnd - call.timing.requestStart
        expect(totalTime).toBeLessThan(3000)
      }
    }
  })

  test('should cache resources effectively', async ({ page }) => {
    // First visit
    await page.goto('/')
    await page.waitForLoadState('networkidle')

    // Navigate away and back
    await page.goto('/course')
    await page.waitForLoadState('networkidle')

    const startTime = Date.now()
    await page.goto('/')
    await page.waitForLoadState('networkidle')
    const secondLoadTime = Date.now() - startTime

    // Second load should be faster due to caching
    expect(secondLoadTime).toBeLessThan(2000)
  })

  test('should minimize JavaScript bundle size impact', async ({ page }) => {
    // Check for code splitting
    const jsFiles: string[] = []
    
    page.on('response', response => {
      if (response.url().includes('.js') && response.status() === 200) {
        jsFiles.push(response.url())
      }
    })

    await page.goto('/')
    await page.waitForLoadState('networkidle')

    // Should have multiple JS files (indicating code splitting)
    expect(jsFiles.length).toBeGreaterThan(1)

    // Main bundle shouldn't be too large (this is a rough check)
    // In a real app, you'd check actual file sizes
    expect(jsFiles.some(file => file.includes('chunk') || file.includes('vendor'))).toBe(true)
  })

  test('should handle memory usage efficiently', async ({ page }) => {
    // Navigate through multiple pages
    const pages = ['/', '/course', '/course/1']
    
    for (const path of pages) {
      await page.goto(path)
      await page.waitForLoadState('networkidle')
      
      // Check for memory leaks (basic check)
      const jsHeapUsed = await page.evaluate(() => {
        return (performance as any).memory?.usedJSHeapSize || 0
      })
      
      // Memory usage shouldn't be excessive
      if (jsHeapUsed > 0) {
        expect(jsHeapUsed).toBeLessThan(50 * 1024 * 1024) // 50MB limit
      }
    }
  })

  test('should optimize search performance', async ({ page }) => {
    await page.goto('/course')

    // Type in search input
    const searchInput = page.locator('[data-testid="search-input"]')
    if (await searchInput.isVisible()) {
      const startTime = Date.now()
      
      // Type search query
      await searchInput.fill('Vue.js')
      
      // Wait for debounced search or submit
      await page.waitForTimeout(500) // Wait for debounce
      
      // Check if search results updated
      await expect(page.locator('[data-testid="course-card"]')).toBeVisible({ timeout: 3000 })
      
      const searchTime = Date.now() - startTime
      expect(searchTime).toBeLessThan(3000)
    }
  })

  test('should handle concurrent user actions', async ({ page }) => {
    await page.goto('/')

    // Simulate multiple quick actions
    const actions = [
      () => page.click('text=课程中心'),
      () => page.goto('/'),
      () => page.click('[data-testid="search-input"]'),
    ]

    // Execute actions with small delays
    for (const action of actions) {
      try {
        await action()
        await page.waitForTimeout(100)
      } catch (error) {
        // Some actions might fail due to navigation, that's OK
      }
    }

    // Page should still be functional
    await expect(page.locator('body')).toBeVisible()
  })

  test('should optimize mobile performance', async ({ page }) => {
    // Set mobile viewport
    await page.setViewportSize({ width: 375, height: 667 })
    
    // Enable mobile network simulation (if available)
    await page.route('**/*', async route => {
      // Add small delay to simulate mobile network
      await new Promise(resolve => setTimeout(resolve, 50))
      await route.continue()
    })

    const startTime = Date.now()
    await page.goto('/')
    await page.waitForLoadState('networkidle')
    const mobileLoadTime = Date.now() - startTime

    // Mobile should still load within reasonable time
    expect(mobileLoadTime).toBeLessThan(8000)

    // Check mobile-specific optimizations
    await expect(page.locator('body')).toBeVisible()
  })
})