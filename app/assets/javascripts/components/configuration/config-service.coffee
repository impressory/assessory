'use strict';

define ["./config"], () ->

  console.log("config defined")
  angular.module('assessory.config').constant 'AssessoryConfig', {

    oauthBase: '/oauth'

    # API base URL
    apiBase: '/api'

    assetBase: '/assets'

  }
