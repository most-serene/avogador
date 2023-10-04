import { build, defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'
import { VitePWA } from 'vite-plugin-pwa'
import path from "path";
import { fileURLToPath, URL } from "url";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    react({
      jsxImportSource: '@emotion/react'
    }),
    VitePWA({
      registerType: 'autoUpdate',
      // includeAssets: ['MostSerene.svg', 'avogador.png', '512.png'],
      manifest: {
        name: 'Avogador',
        display: 'standalone',
        description: 'A system for academic untrusted code execution',
        theme_color: '#009393',
        background_color: '#ffffff',
        start_url: '/',
        icons: [ {
          src: 'icons/512.png',
          sizes: '512x512',
          type: 'image/png',
          purpose: 'any maskable'
        }],
      },
      devOptions: {
        enabled: true
      }
    })
  ],
  resolve: {
    alias:
      {
        //"@": fileURLToPath(new URL("./", import.meta.url)),
        '@assets': fileURLToPath(new URL('./src/assets', import.meta.url)),
        '@assets/*': fileURLToPath(new URL('./src/assets/*', import.meta.url)),
        '@components': fileURLToPath(new URL('./src/components', import.meta.url)),
        '@components/*': fileURLToPath(new URL('./src/components/*', import.meta.url)),
        '@authentication': fileURLToPath(new URL('./src/components/authentication', import.meta.url)),
        '@authentication/*': fileURLToPath(new URL('./src/components/authentication/*', import.meta.url)),
        '@courses': fileURLToPath(new URL('./src/components/courses', import.meta.url)),
        '@courses/*': fileURLToPath(new URL('./src/components/courses/*', import.meta.url)),
        '@error': fileURLToPath(new URL('./src/components/error', import.meta.url)),
        '@error/*': fileURLToPath(new URL('./src/components/error/*', import.meta.url)),
        '@home': fileURLToPath(new URL('./src/components/home', import.meta.url)),
        '@home/*': fileURLToPath(new URL('./src/components/home/*', import.meta.url)),
        '@profile': fileURLToPath(new URL('./src/components/profile', import.meta.url)),
        '@profile/*': fileURLToPath(new URL('./src/components/profile/*', import.meta.url)),
        '@structure': fileURLToPath(new URL('./src/components/structure', import.meta.url)),
        '@structure/*': fileURLToPath(new URL('./src/components/structure/*', import.meta.url)),
        '@theme': fileURLToPath(new URL('./src/components/theme', import.meta.url)),
        '@theme/*': fileURLToPath(new URL('./src/components/theme/*', import.meta.url)),
        '@trials': fileURLToPath(new URL('./src/components/trials', import.meta.url)),
        '@trials/*': fileURLToPath(new URL('./src/components/trials/*', import.meta.url)),
        '@hooks': fileURLToPath(new URL('./src/hooks', import.meta.url)),
        '@hooks/*': fileURLToPath(new URL('./src/hooks/*', import.meta.url)),
      },
  },
  define: {
    'import.meta.env.APP_VERSION': JSON.stringify(process.env.npm_package_version),
  },
  server: {
    port: 3000
  },
  optimizeDeps: {
    include: ["@emotion/react", "@emotion/styled"],
  },
  build: {
    rollupOptions: {
      output:{
        manualChunks(id) {
          if (id.includes('node_modules')) {
            return id.toString().split('node_modules/')[1].split('/')[0].toString();
          }
        }
      }
    }
  }
})
