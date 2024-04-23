App.controller("Register", function ($scope, $state, User) {
  $scope.user = {};

  $scope.register = function () {
    User.register_user($scope.user).then(
      function () {
        // Registration successful, redirect to login page or another state
        alert("Registration Completed");
        $state.transitionTo("login");
      },
      function (error) {
        // Registration failed, show an error message
        alert("Registration failed: " + error.message);
      }
    );
  };
});
