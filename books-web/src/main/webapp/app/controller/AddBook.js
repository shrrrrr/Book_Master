'use strict';

/**j
 * Login controller.
 */
App.controller('AddBook', function($scope, $http,$state) {
    $scope.newBook = {
        author: '',
        title: '',
        genre: ''
    };
    $scope.addBook = function() {
        

        // Call the API to submit the rating
        $http.put('http://localhost:8080/books-web/api/common/manual', $scope.newBook, {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        })
        .then(function(response) {
            alert('Book Added successfully');
            window.location.reload();
            // Optionally, you can perform some action after submitting the rating
        })
        .catch(function(error) {
            console.error('Error submitting rating:', error);
        });
    };
});

