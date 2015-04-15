define [
    "components/course/require",
    "components/user/require",
    "components/group/require",
    "components/task/require",
    "components/question/require"
  ], () ->

    angular.module("assessory.critique", [
      "assessory.course",
      "assessory.user",
      "assessory.group",
      "assessory.task",
      "assessory.question"
    ])