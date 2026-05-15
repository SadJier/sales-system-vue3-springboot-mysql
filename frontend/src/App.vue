<script setup>

</script>

<template>
  <router-view :key="$route.fullPath + Math.random()"></router-view>
</template>
<style> 
</style>