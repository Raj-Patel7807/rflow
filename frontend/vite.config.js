import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig(({ mode }) => {
  // Load environment variables from frontend directory
  const env = loadEnv(mode, process.cwd(), '')
  const apiTarget = env.VITE_API_URL || 'http://localhost:8080'

  return {
    plugins: [react()],
    server: {
      proxy: {
        '/api': {
          target: apiTarget,
          changeOrigin: true,
        },
        '/gateway-test': {
          target: apiTarget,
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/gateway-test/, ''),
        },
      },
    },
  }
})
