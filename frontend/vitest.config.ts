import { defineConfig } from 'vitest/config'

export default defineConfig({
  test: {
    reporters: ['junit', 'default'],
    outputFile: 'reports/report.xml'
  },
})