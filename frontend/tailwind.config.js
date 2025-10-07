/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{vue,ts,tsx}"],
  theme: {
    extend: {
      colors: {
        brand: {
          DEFAULT: "#3b82f6",
          dark: "#1d4ed8",
          light: "#93c5fd"
        }
      }
    }
  },
  plugins: []
};
