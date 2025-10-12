// vite.config.ts
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [tailwindcss(), react()],
  server: {
    port: 5173,
    proxy: {
      // proxy mọi request bắt đầu bằng /api sang Tomcat
      '/api': {
        target: 'http://localhost:8080/bachhoa-backend',
        changeOrigin: true,
      },
    },
  },
  resolve: { alias: { '@': '/src' } },
})
