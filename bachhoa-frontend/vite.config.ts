// vite.config.ts
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [tailwindcss(), react()],
  server: {
    port: 5173,
    proxy: {
      // HOA: proxy sang Tomcat /ecommerce-hoa
      '/api': {
        target: 'http://localhost:8080/',
        changeOrigin: true
      }
    }
  },
  resolve: { alias: { '@': '/src' } }
})
