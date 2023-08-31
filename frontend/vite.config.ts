import { build, defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'
import { VitePWA } from 'vite-plugin-pwa'

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
   // manifest: false
  }
})
