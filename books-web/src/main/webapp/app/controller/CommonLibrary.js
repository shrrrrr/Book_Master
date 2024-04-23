'use strict';

/**
 * Tag controller.
 */
App.controller('CommonLibrary', function($scope, $http, $state) {
    // Define the register function
    //alert("inside the CommonLibrary Controller");

    $http({
        method: 'GET',
        url: 'http://localhost:8080/books-web/api/common/isRegistered'
    }).then(function successCallback(response) {
        // Handle success
        console.log(response);
        if(response?.data?.val=="true"){
            $state.transitionTo('library');
        }
        // Redirect to a different state upon success
        //$state.transitionTo('login'); // Change 'main' to the desired state name
    }, function errorCallback(response) {
        // Handle error
        console.error('Error registering:', response.statusText);
    });
    
    $scope.register = function() {
        $http({
            method: 'POST',
            url: 'http://localhost:8080/books-web/api/common/register'
        }).then(function successCallback(response) {
            // Handle success
            console.log('Registration successful');
            // Redirect to a different state upon success
            $state.transitionTo('library'); // Change 'main' to the desired state name
        }, function errorCallback(response) {
            // Handle error
            console.error('Error registering:', response.statusText);
        });
    };
})



