<template lang="html">
  <div class="">
    <form @submit.prevent class="">
      <div class="form-group">
        <b-btn @click="launchTwitterAuthentication()" variant="primary">Link Twitter Account</b-btn>
      </div>
    </form>
  </div>
</template>

<script>
import { mapActions } from 'vuex'

export default {
  methods: {
    ...mapActions(['udpateProfile']),

    launchTwitterAuthentication () {
      this.axios.get('/1/secured/accountAuth/url', {
        headers: {
          'Authorization': 'Bearer ' + localStorage.getItem('id_token')
        }
      }).then((response) => {
        var newwindow = window.open(response.data, 'Link twitter account', 'left=300,height=200,width=400')

        newwindow.onunload = function () {
          this.udpateProfile()
        }

        if (window.focus) {
          newwindow.focus()
        }
      })
    }
  }
}
</script>

<style scoped>

form button {
  width: 100%
}

</style>
