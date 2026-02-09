/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './index.html',
    './src/**/*.{js,jsx,ts,tsx}'
  ],
  theme: {
    extend: {
      colors: {
        brand: {
          50: '#f3faf7',
          100: '#ddf1e9',
          200: '#b8e1d0',
          300: '#86c8b2',
          400: '#5caf95',
          500: '#3f8f78',
          600: '#2f715f',
          700: '#27594c',
          800: '#22483e',
          900: '#1d3c34',
        },
        warm: {
          50: '#fff4ee',
          100: '#ffe6d8',
          200: '#f8c5ad',
          300: '#f19f7f',
          400: '#e57f5f',
          500: '#d6684b',
          600: '#b8533b',
          700: '#944131',
          800: '#76342a',
          900: '#5e2a23',
        },
        surface: '#F8F9FA',
      },
      fontFamily: {
        heading: ['Space Grotesk', 'sans-serif'],
        body: ['Manrope', 'sans-serif'],
      },
      borderRadius: {
        'xl': '1rem',
        '2xl': '1.5rem',
        '3xl': '2rem', // Added for those extra soft card corners
      },
      keyframes: {
        shake: {
          '0%, 100%': { transform: 'translateX(0)' },
          '25%': { transform: 'translateX(-4px)' },
          '75%': { transform: 'translateX(4px)' },
        },
        fadeInUp: {
          '0%': { opacity: '0', transform: 'translateY(10px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
        pageFade: {
          '0%': { opacity: '0', transform: 'translateY(6px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        }
      },
      animation: {
        shake: 'shake 0.3s ease-in-out',
        fadeInUp: 'fadeInUp 0.4s ease-out forwards',
        'page-fade': 'pageFade 0.35s ease-out forwards',
      }
    }
  },
  plugins: []
};
