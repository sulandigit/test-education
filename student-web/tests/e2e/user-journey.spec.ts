import { test, expect } from '@playwright/test'

test.describe('User Authentication E2E', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/')
  })

  test('should register new user successfully', async ({ page }) => {
    // Navigate to register page
    await page.click('text=注册')
    await expect(page).toHaveURL('/auth/register')

    // Fill registration form
    await page.fill('[data-testid="phone-input"]', '13800138000')
    await page.fill('[data-testid="password-input"]', 'password123')
    await page.fill('[data-testid="confirm-password-input"]', 'password123')
    await page.fill('[data-testid="nickname-input"]', '测试用户')

    // Send SMS code
    await page.click('[data-testid="send-sms-button"]')
    await expect(page.locator('text=验证码已发送')).toBeVisible()

    // Fill SMS code
    await page.fill('[data-testid="sms-code-input"]', '123456')

    // Submit registration
    await page.click('[data-testid="register-button"]')

    // Should redirect to home page after successful registration
    await expect(page).toHaveURL('/')
    await expect(page.locator('[data-testid="user-avatar"]')).toBeVisible()
  })

  test('should login existing user successfully', async ({ page }) => {
    // Navigate to login page
    await page.click('text=登录')
    await expect(page).toHaveURL('/auth/login')

    // Fill login form
    await page.fill('[data-testid="phone-input"]', '13800138000')
    await page.fill('[data-testid="password-input"]', 'password123')

    // Submit login
    await page.click('[data-testid="login-button"]')

    // Should redirect to home page after successful login
    await expect(page).toHaveURL('/')
    await expect(page.locator('[data-testid="user-avatar"]')).toBeVisible()
  })

  test('should show validation errors for invalid input', async ({ page }) => {
    // Navigate to login page
    await page.click('text=登录')

    // Try to submit with empty fields
    await page.click('[data-testid="login-button"]')

    // Should show validation errors
    await expect(page.locator('text=请输入手机号')).toBeVisible()
    await expect(page.locator('text=请输入密码')).toBeVisible()

    // Fill invalid phone number
    await page.fill('[data-testid="phone-input"]', '123')
    await page.blur('[data-testid="phone-input"]')
    await expect(page.locator('text=请输入正确的手机号')).toBeVisible()

    // Fill short password
    await page.fill('[data-testid="password-input"]', '123')
    await page.blur('[data-testid="password-input"]')
    await expect(page.locator('text=密码长度至少8位')).toBeVisible()
  })

  test('should handle login failure gracefully', async ({ page }) => {
    // Navigate to login page
    await page.click('text=登录')

    // Fill form with wrong credentials
    await page.fill('[data-testid="phone-input"]', '13800138000')
    await page.fill('[data-testid="password-input"]', 'wrongpassword')

    // Submit login
    await page.click('[data-testid="login-button"]')

    // Should show error message
    await expect(page.locator('text=手机号或密码错误')).toBeVisible()

    // Should remain on login page
    await expect(page).toHaveURL('/auth/login')
  })

  test('should logout user successfully', async ({ page }) => {
    // First login
    await page.goto('/auth/login')
    await page.fill('[data-testid="phone-input"]', '13800138000')
    await page.fill('[data-testid="password-input"]', 'password123')
    await page.click('[data-testid="login-button"]')

    // Verify logged in
    await expect(page).toHaveURL('/')
    await expect(page.locator('[data-testid="user-avatar"]')).toBeVisible()

    // Logout
    await page.click('[data-testid="user-avatar"]')
    await page.click('text=退出登录')

    // Should redirect to home and show login button
    await expect(page).toHaveURL('/')
    await expect(page.locator('text=登录')).toBeVisible()
  })
})

test.describe('Course Discovery E2E', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/')
  })

  test('should browse featured courses on homepage', async ({ page }) => {
    // Wait for featured courses to load
    await expect(page.locator('[data-testid="featured-courses"]')).toBeVisible()

    // Should display course cards
    const courseCards = page.locator('[data-testid="course-card"]')
    await expect(courseCards).toHaveCount.greaterThan(0)

    // Each course card should have essential information
    const firstCard = courseCards.first()
    await expect(firstCard.locator('.course-title')).toBeVisible()
    await expect(firstCard.locator('.instructor')).toBeVisible()
    await expect(firstCard.locator('.price')).toBeVisible()
    await expect(firstCard.locator('.rating')).toBeVisible()
  })

  test('should search courses successfully', async ({ page }) => {
    // Click on search input
    await page.click('[data-testid="search-input"]')
    
    // Enter search query
    await page.fill('[data-testid="search-input"]', 'Vue.js')
    await page.press('[data-testid="search-input"]', 'Enter')

    // Should navigate to course list page with search results
    await expect(page).toHaveURL(/\/course\?.*q=Vue\.js/)

    // Should display search results
    await expect(page.locator('[data-testid="search-results"]')).toBeVisible()
    await expect(page.locator('[data-testid="course-card"]')).toHaveCount.greaterThan(0)

    // Search query should be displayed
    await expect(page.locator('text=搜索结果: Vue.js')).toBeVisible()
  })

  test('should filter courses by category', async ({ page }) => {
    // Navigate to course center
    await page.click('text=课程中心')
    await expect(page).toHaveURL('/course')

    // Wait for categories to load
    await expect(page.locator('[data-testid="course-categories"]')).toBeVisible()

    // Click on a category filter
    await page.click('[data-testid="category-frontend"]')

    // URL should update with category filter
    await expect(page).toHaveURL(/\/course\?.*category=frontend/)

    // Should display filtered results
    await expect(page.locator('[data-testid="course-card"]')).toHaveCount.greaterThan(0)

    // Active category filter should be highlighted
    await expect(page.locator('[data-testid="category-frontend"].active')).toBeVisible()
  })

  test('should navigate to course detail page', async ({ page }) => {
    // Go to course center
    await page.goto('/course')

    // Wait for courses to load
    await expect(page.locator('[data-testid="course-card"]')).toHaveCount.greaterThan(0)

    // Click on first course card
    const firstCourse = page.locator('[data-testid="course-card"]').first()
    await firstCourse.click()

    // Should navigate to course detail page
    await expect(page).toHaveURL(/\/course\/\d+/)

    // Should display course details
    await expect(page.locator('[data-testid="course-title"]')).toBeVisible()
    await expect(page.locator('[data-testid="course-instructor"]')).toBeVisible()
    await expect(page.locator('[data-testid="course-price"]')).toBeVisible()
    await expect(page.locator('[data-testid="course-description"]')).toBeVisible()
    await expect(page.locator('[data-testid="course-chapters"]')).toBeVisible()
  })

  test('should handle pagination in course list', async ({ page }) => {
    // Navigate to course center
    await page.goto('/course')

    // Wait for pagination to be visible (if there are enough courses)
    await page.waitForLoadState('networkidle')

    // Check if pagination exists
    const pagination = page.locator('[data-testid="pagination"]')
    if (await pagination.isVisible()) {
      // Click on page 2
      await page.click('[data-testid="page-2"]')

      // URL should update
      await expect(page).toHaveURL(/\/course\?.*page=2/)

      // Should load different courses
      await expect(page.locator('[data-testid="course-card"]')).toHaveCount.greaterThan(0)
    }
  })
})

test.describe('Learning Experience E2E', () => {
  test.beforeEach(async ({ page }) => {
    // Login first
    await page.goto('/auth/login')
    await page.fill('[data-testid="phone-input"]', '13800138000')
    await page.fill('[data-testid="password-input"]', 'password123')
    await page.click('[data-testid="login-button"]')
    await expect(page).toHaveURL('/')
  })

  test('should start learning a course', async ({ page }) => {
    // Navigate to a course detail page
    await page.goto('/course/1')

    // Click start learning button
    await page.click('[data-testid="start-learning-button"]')

    // Should navigate to learning page
    await expect(page).toHaveURL(/\/learning\/1/)

    // Should display video player
    await expect(page.locator('[data-testid="video-player"]')).toBeVisible()

    // Should display course chapters
    await expect(page.locator('[data-testid="chapter-list"]')).toBeVisible()

    // Should display learning progress
    await expect(page.locator('[data-testid="learning-progress"]')).toBeVisible()
  })

  test('should control video playback', async ({ page }) => {
    // Navigate to learning page
    await page.goto('/learning/1/1')

    // Wait for video player to load
    await expect(page.locator('[data-testid="video-player"]')).toBeVisible()

    // Play video
    await page.click('[data-testid="play-button"]')
    await expect(page.locator('[data-testid="play-button"].playing')).toBeVisible()

    // Pause video
    await page.click('[data-testid="play-button"]')
    await expect(page.locator('[data-testid="play-button"]:not(.playing)')).toBeVisible()

    // Test fullscreen
    await page.click('[data-testid="fullscreen-button"]')
    // Note: Fullscreen testing might be limited in headless mode

    // Test volume control
    await page.click('[data-testid="volume-button"]')
    await page.setInputFiles('[data-testid="volume-slider"]', [])
  })

  test('should navigate between chapters', async ({ page }) => {
    // Navigate to learning page
    await page.goto('/learning/1/1')

    // Wait for chapter list to load
    await expect(page.locator('[data-testid="chapter-list"]')).toBeVisible()

    // Click on second chapter
    await page.click('[data-testid="chapter-2"]')

    // URL should update
    await expect(page).toHaveURL('/learning/1/2')

    // Chapter should be marked as active
    await expect(page.locator('[data-testid="chapter-2"].active')).toBeVisible()

    // Video should change
    await expect(page.locator('[data-testid="video-player"]')).toBeVisible()
  })

  test('should save learning progress', async ({ page }) => {
    // Navigate to learning page
    await page.goto('/learning/1/1')

    // Wait for video to load
    await expect(page.locator('[data-testid="video-player"]')).toBeVisible()

    // Play video for a while
    await page.click('[data-testid="play-button"]')
    await page.waitForTimeout(5000) // Wait 5 seconds

    // Refresh page
    await page.reload()

    // Progress should be saved
    await expect(page.locator('[data-testid="video-player"]')).toBeVisible()
    
    // Current time should be restored (not at the beginning)
    const currentTime = await page.locator('[data-testid="current-time"]').textContent()
    expect(currentTime).not.toBe('00:00')
  })
})

test.describe('User Profile E2E', () => {
  test.beforeEach(async ({ page }) => {
    // Login first
    await page.goto('/auth/login')
    await page.fill('[data-testid="phone-input"]', '13800138000')
    await page.fill('[data-testid="password-input"]', 'password123')
    await page.click('[data-testid="login-button"]')
    await expect(page).toHaveURL('/')
  })

  test('should access user profile', async ({ page }) => {
    // Click on user avatar
    await page.click('[data-testid="user-avatar"]')
    
    // Click on profile link
    await page.click('text=个人中心')

    // Should navigate to user profile
    await expect(page).toHaveURL('/user')

    // Should display user information
    await expect(page.locator('[data-testid="user-info"]')).toBeVisible()
    await expect(page.locator('[data-testid="learning-stats"]')).toBeVisible()
    await expect(page.locator('[data-testid="recent-courses"]')).toBeVisible()
  })

  test('should display my courses', async ({ page }) => {
    // Navigate to user profile
    await page.goto('/user')

    // Click on my courses tab
    await page.click('[data-testid="my-courses-tab"]')

    // Should display enrolled courses
    await expect(page.locator('[data-testid="my-courses-list"]')).toBeVisible()

    // Should display course progress
    const courseCards = page.locator('[data-testid="my-course-card"]')
    if (await courseCards.count() > 0) {
      await expect(courseCards.first().locator('[data-testid="course-progress"]')).toBeVisible()
    }
  })

  test('should update profile information', async ({ page }) => {
    // Navigate to user profile
    await page.goto('/user')

    // Click edit profile button
    await page.click('[data-testid="edit-profile-button"]')

    // Update nickname
    await page.fill('[data-testid="nickname-input"]', '新昵称')

    // Save changes
    await page.click('[data-testid="save-profile-button"]')

    // Should show success message
    await expect(page.locator('text=个人信息更新成功')).toBeVisible()

    // Profile should be updated
    await expect(page.locator('[data-testid="user-nickname"]')).toHaveText('新昵称')
  })
})