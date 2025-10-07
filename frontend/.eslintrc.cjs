module.exports = {
  root: true,
  env: {
    browser: true,
    es2021: true
  },
  extends: ["plugin:vue/vue3-recommended", "eslint:recommended", "prettier"],
  parserOptions: {
    ecmaVersion: "latest",
    sourceType: "module"
  },
  rules: {
    "vue/multi-word-component-names": "off"
  }
};
