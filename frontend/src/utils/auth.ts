// 登录状态过期时间（24小时）
const LOGIN_EXPIRE_TIME = 24 * 60 * 60 * 1000

/**
 * 检查登录状态是否过期
 */
export const isLoginExpired = (): boolean => {
  const loginTime = localStorage.getItem('loginTime')
  if (!loginTime) {
    return true
  }
  
  const loginTimestamp = parseInt(loginTime, 10)
  const now = Date.now()
  
  return now - loginTimestamp > LOGIN_EXPIRE_TIME
}

/**
 * 检查是否已登录（未过期）
 */
export const isLoggedIn = (): boolean => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')
  
  if (!token || !role) {
    return false
  }
  
  return !isLoginExpired()
}

/**
 * 获取当前登录用户信息
 */
export const getCurrentUser = () => {
  const role = localStorage.getItem('role')
  let user = null
  
  if (role === 'user') {
    const userStr = localStorage.getItem('user')
    if (userStr) {
      try {
        user = JSON.parse(userStr)
      } catch {
        user = null
      }
    }
  } else if (role === 'shop') {
    const shopStr = localStorage.getItem('shop')
    if (shopStr) {
      try {
        user = JSON.parse(shopStr)
      } catch {
        user = null
      }
    }
  } else if (role === 'rider') {
    const riderStr = localStorage.getItem('rider')
    if (riderStr) {
      try {
        user = JSON.parse(riderStr)
      } catch {
        user = null
      }
    }
  }
  
  return user
}

/**
 * 获取当前角色
 */
export const getCurrentRole = (): string | null => {
  return localStorage.getItem('role')
}

/**
 * 清除登录状态
 */
export const clearLoginState = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  localStorage.removeItem('shop')
  localStorage.removeItem('rider')
  localStorage.removeItem('role')
  localStorage.removeItem('loginTime')
}

/**
 * 检查登录状态并在过期时清除
 */
export const checkAndClearExpiredLogin = (): boolean => {
  if (isLoginExpired()) {
    clearLoginState()
    return false
  }
  return true
}