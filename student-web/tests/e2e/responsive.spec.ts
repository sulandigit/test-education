import { test, expect, devices } from '@playwright/test'

test.describe('Responsive Design E2E', () => {
  // Desktop tests
  test.describe('Desktop', () => {
    test.use({ ...devices['Desktop Chrome'] })

    test('should display desktop navigation correctly', async ({ page }) => {
      await page.goto('/')

      // Desktop navigation should be visible
      await expect(page.locator('[data-testid="desktop-nav"]')).toBeVisible()
      await expect(page.locator('[data-testid="mobile-nav"]')).not.toBeVisible()

      // Search bar should be visible
      await expect(page.locator('[data-testid="search-input"]')).toBeVisible()

      // User actions should be in header
      await expect(page.locator('[data-testid="user-actions"]')).toBeVisible()
    })

    test('should display course grid properly on desktop', async ({ page }) => {
      await page.goto('/course')

      // Should display courses in grid layout
      const courseGrid = page.locator('[data-testid="course-grid"]')
      await expect(courseGrid).toBeVisible()

      // Should have multiple columns
      const courseCards = page.locator('[data-testid="course-card"]')
      const cardCount = await courseCards.count()
      
      if (cardCount >= 3) {
        // Check if cards are arranged in rows (not a single column)
        const firstCard = courseCards.first()
        const secondCard = courseCards.nth(1)
        const thirdCard = courseCards.nth(2)

        const firstCardBounds = await firstCard.boundingBox()
        const secondCardBounds = await secondCard.boundingBox()
        const thirdCardBounds = await thirdCard.boundingBox()

        // Second and third cards should be in the same row as first (similar Y position)
        expect(Math.abs((firstCardBounds?.y || 0) - (secondCardBounds?.y || 0))).toBeLessThan(50)
        expect(Math.abs((firstCardBounds?.y || 0) - (thirdCardBounds?.y || 0))).toBeLessThan(50)
      }
    })

    test('should display course detail sidebar on desktop', async ({ page }) => {
      await page.goto('/course/1')

      // Course detail should have two-column layout
      await expect(page.locator('[data-testid="course-content"]')).toBeVisible()
      await expect(page.locator('[data-testid="course-sidebar"]')).toBeVisible()

      // Sidebar should contain purchase info
      const sidebar = page.locator('[data-testid="course-sidebar"]')
      await expect(sidebar.locator('[data-testid="course-price"]')).toBeVisible()
      await expect(sidebar.locator('[data-testid="purchase-button"]')).toBeVisible()
    })
  })

  // Tablet tests
  test.describe('Tablet', () => {
    test.use({ ...devices['iPad Pro'] })

    test('should adapt navigation for tablet', async ({ page }) => {
      await page.goto('/')

      // Navigation should be visible but might be condensed
      await expect(page.locator('[data-testid="main-nav"]')).toBeVisible()

      // Search might be collapsed or in different position
      const searchInput = page.locator('[data-testid="search-input"]')
      const searchButton = page.locator('[data-testid="search-button"]')
      
      // Either search input is visible or search button exists
      const isSearchInputVisible = await searchInput.isVisible()
      const isSearchButtonVisible = await searchButton.isVisible()
      expect(isSearchInputVisible || isSearchButtonVisible).toBe(true)
    })

    test('should display course grid in tablet layout', async ({ page }) => {
      await page.goto('/course')

      // Should display courses in 2-column grid on tablet
      const courseCards = page.locator('[data-testid="course-card"]')
      const cardCount = await courseCards.count()

      if (cardCount >= 2) {
        const firstCard = courseCards.first()
        const secondCard = courseCards.nth(1)

        const firstCardBounds = await firstCard.boundingBox()
        const secondCardBounds = await secondCard.boundingBox()

        // Cards should be side by side
        expect(Math.abs((firstCardBounds?.y || 0) - (secondCardBounds?.y || 0))).toBeLessThan(50)
      }
    })

    test('should adapt course detail layout for tablet', async ({ page }) => {
      await page.goto('/course/1')

      // On tablet, sidebar might stack below content or be in tabs
      await expect(page.locator('[data-testid="course-detail"]')).toBeVisible()
      
      // Check if purchase section is accessible
      const purchaseSection = page.locator('[data-testid="purchase-section"]')
      await expect(purchaseSection).toBeVisible()
    })
  })

  // Mobile tests
  test.describe('Mobile', () => {
    test.use({ ...devices['iPhone 12'] })

    test('should display mobile navigation correctly', async ({ page }) => {
      await page.goto('/')

      // Mobile hamburger menu should be visible
      await expect(page.locator('[data-testid="mobile-menu-button"]')).toBeVisible()
      
      // Desktop navigation should be hidden
      await expect(page.locator('[data-testid="desktop-nav"]')).not.toBeVisible()

      // Open mobile menu
      await page.click('[data-testid="mobile-menu-button"]')
      await expect(page.locator('[data-testid="mobile-nav"]')).toBeVisible()

      // Check navigation items
      await expect(page.locator('[data-testid="mobile-nav"] a')).toHaveCount.greaterThan(0)
    })

    test('should display course cards in single column on mobile', async ({ page }) => {
      await page.goto('/course')

      // Should display courses in single column
      const courseCards = page.locator('[data-testid="course-card"]')
      const cardCount = await courseCards.count()

      if (cardCount >= 2) {
        const firstCard = courseCards.first()
        const secondCard = courseCards.nth(1)

        const firstCardBounds = await firstCard.boundingBox()
        const secondCardBounds = await secondCard.boundingBox()

        // Second card should be below first card (different Y position)
        expect((secondCardBounds?.y || 0) - (firstCardBounds?.y || 0)).toBeGreaterThan(100)
      }
    })

    test('should stack course detail content on mobile', async ({ page }) => {
      await page.goto('/course/1')

      // Content should be stacked vertically
      await expect(page.locator('[data-testid="course-detail"]')).toBeVisible()

      // Purchase section should be at bottom or in fixed position
      const purchaseSection = page.locator('[data-testid="purchase-section"]')
      await expect(purchaseSection).toBeVisible()
    })

    test('should handle mobile video player', async ({ page }) => {
      // Login first
      await page.goto('/auth/login')
      await page.fill('[data-testid="phone-input"]', '13800138000')
      await page.fill('[data-testid="password-input"]', 'password123')
      await page.click('[data-testid="login-button"]')

      // Navigate to learning page
      await page.goto('/learning/1/1')

      // Video player should be responsive
      await expect(page.locator('[data-testid="video-player"]')).toBeVisible()

      // Controls should be touch-friendly
      const playButton = page.locator('[data-testid="play-button"]')
      await expect(playButton).toBeVisible()

      // Test touch interaction
      await playButton.tap()
      await expect(page.locator('[data-testid="play-button"].playing')).toBeVisible()
    })

    test('should handle mobile search', async ({ page }) => {
      await page.goto('/')

      // Look for mobile search trigger
      const searchTrigger = page.locator('[data-testid="mobile-search-button"]')
      if (await searchTrigger.isVisible()) {
        await searchTrigger.click()
        
        // Search overlay should appear
        await expect(page.locator('[data-testid="mobile-search-overlay"]')).toBeVisible()
        
        // Search input should be visible
        await expect(page.locator('[data-testid="search-input"]')).toBeVisible()
      }
    })

    test('should handle mobile user menu', async ({ page }) => {
      // Login first
      await page.goto('/auth/login')
      await page.fill('[data-testid="phone-input"]', '13800138000')
      await page.fill('[data-testid="password-input"]', 'password123')
      await page.click('[data-testid="login-button"]')

      // Click user avatar/menu
      await page.click('[data-testid="user-menu-trigger"]')

      // Mobile user menu should appear
      await expect(page.locator('[data-testid="mobile-user-menu"]')).toBeVisible()

      // Should contain user options
      await expect(page.locator('text=个人中心')).toBeVisible()
      await expect(page.locator('text=我的课程')).toBeVisible()
      await expect(page.locator('text=退出登录')).toBeVisible()
    })
  })

  test.describe('Cross-device Consistency', () => {
    test('should maintain functionality across devices', async ({ page, browser }) => {
      // Test same functionality on different viewport sizes
      const viewports = [
        { width: 1920, height: 1080 }, // Desktop
        { width: 768, height: 1024 },  // Tablet
        { width: 375, height: 667 }    // Mobile
      ]

      for (const viewport of viewports) {
        await page.setViewportSize(viewport)
        await page.goto('/')

        // Basic functionality should work on all devices
        await expect(page.locator('body')).toBeVisible()
        
        // Navigation should be accessible (either desktop nav or mobile menu)
        const hasDesktopNav = await page.locator('[data-testid="desktop-nav"]').isVisible()
        const hasMobileMenu = await page.locator('[data-testid="mobile-menu-button"]').isVisible()
        expect(hasDesktopNav || hasMobileMenu).toBe(true)

        // Course browsing should work
        if (await page.locator('text=课程中心').isVisible()) {
          await page.click('text=课程中心')
          await expect(page).toHaveURL('/course')
        }
      }
    })

    test('should handle orientation changes on mobile', async ({ page }) => {
      // Start in portrait
      await page.setViewportSize({ width: 375, height: 667 })
      await page.goto('/')

      // Switch to landscape
      await page.setViewportSize({ width: 667, height: 375 })

      // Layout should adapt
      await expect(page.locator('body')).toBeVisible()
      
      // Navigation should still be accessible
      const hasMobileMenu = await page.locator('[data-testid="mobile-menu-button"]').isVisible()
      expect(hasMobileMenu).toBe(true)
    })
  })

  test.describe('Performance on Different Devices', () => {
    test('should load quickly on mobile', async ({ page }) => {
      await page.setViewportSize({ width: 375, height: 667 })
      
      const startTime = Date.now()
      await page.goto('/')
      await page.waitForLoadState('networkidle')
      const loadTime = Date.now() - startTime

      // Should load within reasonable time (adjust threshold as needed)
      expect(loadTime).toBeLessThan(5000)

      // Critical content should be visible
      await expect(page.locator('body')).toBeVisible()
    })

    test('should handle slow network on mobile', async ({ page, context }) => {
      // Simulate slow network
      await context.route('**/*', async route => {
        await new Promise(resolve => setTimeout(resolve, 100)) // Add delay
        await route.continue()
      })

      await page.setViewportSize({ width: 375, height: 667 })
      await page.goto('/')

      // Should show loading states
      // Content should eventually load
      await expect(page.locator('body')).toBeVisible({ timeout: 10000 })
    })
  })
})